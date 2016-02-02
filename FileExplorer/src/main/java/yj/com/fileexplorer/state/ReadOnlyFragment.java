package yj.com.fileexplorer.state;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.ProgressStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import yj.com.fileexplorer.FileAdapter;
import yj.com.fileexplorer.FileItem;
import yj.com.fileexplorer.FileTools;
import yj.com.fileexplorer.R;

/**
 * Created by Sober on 2016/1/27.
 */
public class ReadOnlyFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String TAG = getClass().getSimpleName();

    protected ExplorerCallBack explorerCallBack;
    protected File currentDir = null;
    private boolean receiverRegistered = false;
    protected List<FileItem> fileItems;
    protected FileTools fileTools;
    protected FileAdapter fileAdapter;
    private LinearLayout emptyView;
    protected ListView listView;

    //多个项目选择
    private boolean isActionModeStarted = false;

    /**
     * 保存下来，按照需要隐藏一些menu
     */
    protected Menu myMenu;

    /**
     * 文件比较的一个记录
     */
    private CurrentOrder currentOrder;

    /**
     * 浏览的历史信息
     */
    private Stack<HistoryEntity> historyEntities;


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "on item click " + position);

        if (position < 0 || position > this.fileItems.size()) {
            return;
        }
        if (isActionModeStarted) {
            listView.setItemChecked(position, !listView.isItemChecked(position));
        } else {

            FileItem fileItem = this.fileItems.get(position);
            File file = fileItem.getFile();

            //上一层
            if (file == null) {
                HistoryEntity historyEntity = null;
                if (historyEntities.size() > 0) {
                    historyEntity = this.historyEntities.pop();
                }
                //到根了
                if (historyEntity.dir == null) {
                    this.explorerCallBack.updateTitle("");
                    listRoots();
                } else {
                    this.explorerCallBack.updateTitle(historyEntity.title);
                    listFiles(historyEntity.dir);
                    currentDir = historyEntity.dir;
                    listView.setSelection(historyEntity.scrollItem);

                }
            } else if (!file.canRead()) {
                showAlertDialog(null, "对不起，您无法访问该文件");
            } else if (file.isDirectory()) {
                //历史记录,永远是上一层
                HistoryEntity historyEntity = new HistoryEntity();
                historyEntity.scrollItem = this.listView.getFirstVisiblePosition();
                historyEntity.dir = currentDir;
                historyEntity.title = (currentDir == null ? "" : currentDir.getName());
                this.explorerCallBack.updateTitle(file.getName());
                //没有成功展示子文件夹，则退出
                if (!listFiles(file)) {
                    return;
                }
                this.historyEntities.push(historyEntity);
                currentDir = file;
                //滚到第一个位置
                this.listView.setSelection(0);
            } else {
                fileTools.openFile(file);
            }
        }
    }

    protected void setActionModeStarted(boolean isStarted) {
        this.isActionModeStarted = isStarted;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_file_explorer, menu);
        this.myMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * 显示，隐藏menu
     *
     * @param menuId  需要使用的MenuId
     * @param visible 可见？
     */
    protected void showOrHideMenus(Menu menu, boolean visible, int... menuId) {
        if (menu != null) {
            for (int id : menuId) {
                menu.findItem(id).setVisible(visible);
                menu.findItem(id).setEnabled(visible);
            }
        }
    }


    /**
     * 存储上一个历史的类
     */
    private class HistoryEntity {
        int scrollItem;
        //上一层目录
        File dir;
        //本层标题
        String title;
    }

    /**
     * 广播监听器，用来检测SDCard的活动，做出相应的操作
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        if (currentDir == null) {
                            listRoots();
                        } else {
                            listFiles(currentDir);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }
    };


    /**
     * 根目录的情况
     */
    protected void listRoots() {
        currentDir = null;
        fileItems.clear();
        //隐藏排序功能
        showOrHideMenus(this.myMenu, false, R.id.menu_sort);
        Set<String> paths = fileTools.getExternalStoragePaths();
        int index = 0;
        for (String path : paths) {
            //第一个，就是内置存储器
            FileItem fileItem = new FileItem();
            try {
                if (index == 0) {
                    fileItem.setName("ExternalStorage");
                    fileItem.setImageId(R.drawable.ic_storage_black);
                } else {
                    fileItem.setName("SDCard");
                    fileItem.setImageId(R.drawable.ic_sd_storage_black);

                }
                fileItem.setOther(fileTools.getRootVolume(path));
                fileItem.setFile(new File(path));
                this.fileItems.add(fileItem);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            index++;
        }
        fileAdapter.notifyDataSetChanged();

    }

    /**
     * 因为某些情况造成SDCard不可读的页面提示信息
     *
     * @param message 没有Item的时候显示的信息
     */
    private void setEmptyView(String message) {
        if (this.emptyView != null) {
            TextView textView = (TextView) this.emptyView.findViewById(R.id.file_emptyView_text);
            textView.setText(message);
        }
    }

    /**
     * 读取一个文件夹下面的文件
     *
     * @param dir 当前文件夹
     * @return true 代表成功展示子文件夹
     */
    protected boolean listFiles(File dir) {

        if (dir == null) {
            return false;
        }
        if (!dir.canRead()) {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)
                    && !Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED_READ_ONLY)) {
                currentDir = dir;
                this.fileItems.clear();
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_SHARED.equals(state)) {
                    setEmptyView("Usb Active");
                    this.historyEntities.clear();
                } else {
                    setEmptyView("Not Mounted");
                    this.historyEntities.clear();
                }
                fileAdapter.notifyDataSetChanged();

                return true;
            }
            showAlertDialog(null, "抱歉，无法读取该文件夹");
            return false;
        }
        setEmptyView("没有文件");
        try {
            this.fileItems.clear();
            List<FileItem> readFileItems = fileTools.getChildrenFilesInfo(dir.getAbsolutePath());
            Collections.sort(readFileItems, this.currentOrder.comparator);
            this.fileItems.addAll(readFileItems);
        } catch (Exception e) {
            e.printStackTrace();
            showAlertDialog("警告", e.getMessage());
            return false;
        }

        this.fileAdapter.notifyDataSetChanged();

        //显示隐藏的菜单
        showOrHideMenus(this.myMenu, true, R.id.menu_sort);
        return true;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        this.fileTools = new FileTools(getActivity());
        this.historyEntities = new Stack<>();
        this.fileItems = new ArrayList<>();

        this.currentOrder = new CurrentOrder();
    }


    /**
     * 注册系统的关于SDCard的广播监听
     */
    private void registerReceiver() {
        if (!receiverRegistered) {
            receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
            filter.addAction(Intent.ACTION_MEDIA_CHECKING);
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_NOFS);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter.addAction(Intent.ACTION_MEDIA_SHARED);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            /*
             * http://blog.csdn.net/silenceburn/article/details/6083375
			 * 隐式事件监听接收器，为了监听系统发出的SDCard发生了修改，添加的过滤器
			 */
            filter.addDataScheme("file");
            //注册在这个的Receiver中监听
            getActivity().registerReceiver(receiver, filter);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        registerReceiver();


        View root = inflater.inflate(R.layout.file_explorer_list, container, false);
        this.emptyView = (LinearLayout) root.findViewById(R.id.sd_not_available_page);


        this.listView = (ListView) root.findViewById(R.id.file_listView);

        //设置空的View
        this.listView.setEmptyView(this.emptyView);
        this.listView.setOnItemClickListener(this);


        this.fileAdapter = new FileAdapter(getActivity(), this.fileItems, this.listView);

        this.createAnimationAdapter(this.listView, fileAdapter);

        listRoots();
        return root;
    }


    /**
     * 摧毁掉注册的监听器
     */
    public void onFragmentDestroy() {
        try {
            if (receiverRegistered) {
                getActivity().unregisterReceiver(receiver);
                receiverRegistered = false;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        super.onDestroy();
    }


    /**
     * 当前的顺序
     * 初始排序为按照名字的升序排序
     */
    private class CurrentOrder {
        //之前点击了的order
        int orderId = R.id.menu_name;
        boolean isIncreased = true;
        Comparator<FileItem> comparator = FileTools.sortByName(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_name) {
            boolean tmp = false;
            if (currentOrder.orderId == id) {
                tmp = currentOrder.isIncreased;
                currentOrder.isIncreased = !tmp;
            }
            currentOrder.orderId = id;
            sortItems(FileTools.sortByName(!tmp));

            return true;
        } else if (id == R.id.menu_size) {
            boolean tmp = false;
            if (currentOrder.orderId == id) {
                tmp = currentOrder.isIncreased;
                currentOrder.isIncreased = !tmp;
            }
            currentOrder.orderId = id;
            sortItems(FileTools.sortBySize(!tmp));

            return true;
        } else if (id == R.id.menu_time) {
            boolean tmp = false;
            if (currentOrder.orderId == id) {
                tmp = currentOrder.isIncreased;
                currentOrder.isIncreased = !tmp;
            }
            currentOrder.orderId = id;
            sortItems(FileTools.sortByTime(!tmp));

            return true;
        } else if (id == R.id.menu_refresh) {
            if (currentDir == null) {
                listRoots();
            } else {
                listFiles(currentDir);
            }
            return true;
        } else if (id == R.id.menu_exit || id == android.R.id.home) {
            this.onFragmentDestroy();
            getActivity().finish();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对显示的File文件夹按照比较器要求排序
     *
     * @param comparator 比较器
     */
    private void sortItems(Comparator<FileItem> comparator) {
        //根目录不进行排序
        if (currentDir != null) {
            this.currentOrder.comparator = comparator;
            Collections.sort(this.fileItems, comparator);
            this.fileAdapter.notifyDataSetChanged();

            //因为顺序发生了改变，原来存储的历史位置无效了
            for (HistoryEntity historyEntity : this.historyEntities) {
                historyEntity.scrollItem = 0;
            }
        }
    }

    /**
     * 设置回调事件
     *
     * @param explorerCallBack 设置Handler的回调事件
     */
    public void setExplorerCallBack(ExplorerCallBack explorerCallBack) {
        this.explorerCallBack = explorerCallBack;
    }


    /**
     * 显示提示框
     *
     * @param title   标题
     * @param message 信息
     */
    public  Delivery showAlertDialog(String title, String message) {
        Delivery dialog;
        if ("".equals(title)) {
            dialog = PostOffice.newMail(getActivity())
                    .setCanceledOnTouchOutside(true)
                    .setCancelable(true)
                    .setThemeColor(R.color.dialogColor)
                    .setDesign(Design.MATERIAL_LIGHT).setMessage(message)
                    .setButton(Dialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show(getFragmentManager());
        } else {
            dialog = PostOffice.newMail(getActivity())
                    .setTitle(title)
                    .setCanceledOnTouchOutside(true)
                    .setCancelable(true)
                    .setThemeColor(R.color.dialogColor)
                    .setDesign(Design.MATERIAL_LIGHT).setMessage(message)
                    .setButton(Dialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show(getFragmentManager());
        }
        return dialog;
    }


    /**
     * 显示进度对话框
     *
     * @param message 通知
     */
    public Delivery showProgressDialog(String message) {
        Delivery dialog = PostOffice.newMail(getActivity())
                .setDesign(Design.MATERIAL_LIGHT)
                .setThemeColor(R.color.dialogColor)
                .setCanceledOnTouchOutside(true)
                .setStyle(new ProgressStyle.Builder(getActivity())
                        .setProgressStyle(ProgressStyle.NORMAL)
                        .setProgressMessage(message)
                        .build())
                .build();
        dialog.show(getFragmentManager());
        return dialog;
    }

    /**
     * 创建拥有动画效果的ListView适配器
     *
     * @param swipeMenuListView 需要添加进动画的ListView
     * @param adapter           需要的数据适配器
     * @return 返回一个配置好动画的Adapter
     */
    public AnimationAdapter createAnimationAdapter(AbsListView swipeMenuListView, BaseAdapter adapter) {
        //ListView 的animation
        AnimationAdapter animationAdapter = new SwingLeftInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(swipeMenuListView);

        //设置第一次动画的时间,注意顺序一定要先设置绑定的listView
        assert animationAdapter.getViewAnimator() != null;
        animationAdapter.getViewAnimator().setInitialDelayMillis(200);

        //设置Adapter
        swipeMenuListView.setAdapter(animationAdapter);
        return animationAdapter;
    }


    /**
     * 函数回调接口
     */
    public interface ExplorerCallBack {
        /**
         * 更新标题栏
         *
         * @param title 标题
         */
        void updateTitle(String title);

        /**
         * 选择的文件的路径
         *
         * @param files 选择的文件
         */
        void selectedFiles(Collection<File> files);
    }

    /**
     * 点击了返回键，如果不是根目录就不退出
     *
     * @return true 调用Activity中的系统的onBackPressed操作
     */
    public boolean onBackPressed() {
        HistoryEntity historyEntity = null;
        if (this.historyEntities.size() > 0) {
            historyEntity = this.historyEntities.pop();
        }
        if (historyEntity != null) {
            this.explorerCallBack.updateTitle(historyEntity.title);
            if (historyEntity.dir != null) {
                listFiles(historyEntity.dir);
            } else {
                listRoots();
            }
            this.listView.setSelection(historyEntity.scrollItem);
            return false;
        } else {
            //最后一页在Activity中，退出
            return true;
        }
    }

}
