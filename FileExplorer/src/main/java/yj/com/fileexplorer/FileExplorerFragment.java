package yj.com.fileexplorer;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.ProgressStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Sober on 2016/1/27.
 */
public class FileExplorerFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, SwipeMenuListView.OnMenuItemClickListener {

    private String TAG = getClass().getSimpleName();

    private ExplorerCallBack explorerCallBack;
    private File currentDir = null;
    private boolean receiverRegistered = false;
    private List<FileItem> fileItems;
    private FileTools fileTools;
    private FileAdapter fileAdapter;
    private LinearLayout emptyView;
    private SwipeMenuListView swipeMenuListView;

    private MyHandler myHandler = new MyHandler();

    /**
     * 浏览的历史信息
     */
    private Stack<HistoryEntity> historyEntities = null;

    /**
     * 点击Item的时候的触发事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 0 || position > this.fileItems.size()) {
            return;
        }
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
                swipeMenuListView.setSelection(historyEntity.scrollItem);

            }
        } else if (!file.canRead()) {
            showAlertDialog(null , "对不起，您无法访问该文件");
        } else if (file.isDirectory()) {
            //历史记录,永远是上一层
            HistoryEntity historyEntity = new HistoryEntity();
            historyEntity.scrollItem = this.swipeMenuListView.getFirstVisiblePosition();
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
            this.swipeMenuListView.setSelection(0);
        } else {
            fileTools.openFile(file);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 0 || position > this.fileItems.size()) {
            return false;
        }
        final File file = this.fileItems.get(position).getFile();
        //是文件夹
        if (file.isDirectory()) {
            PostOffice.newMail(getActivity())
                    .setTitle("警告")
                    .setThemeColor(R.color.dialogColor)
                    .setButtonTextColor(Dialog.BUTTON_POSITIVE, android.R.color.holo_red_light)
                    .setDesign(Design.MATERIAL_LIGHT).setMessage("是否选择该文件夹存储数据？")
                    .setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setButton(Dialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            explorerCallBack.selectedFile(file.getAbsolutePath());
                            dialog.dismiss();
                        }
                    }).show(getFragmentManager());
        }else{
            //不是文件夹，长按显示详细信息
            try {
                showAlertDialog(file.getName(), fileTools.getFileDetailInfo(file.getAbsolutePath()));
            } catch (Exception e) {
                showAlertDialog("警告" , e.getMessage());
            }
        }
        return true;
    }

    /**
     * 滑动的，点击了其中的一个按钮的触发事件
     *
     * @param position
     * @param menu
     * @param index
     * @return
     */
    @Override
    public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
        if (position <= 0 || position > this.fileItems.size()) {
            return false;
        }
        final File file = this.fileItems.get(position).getFile();
        switch (index) {
            //delete
            case 0:
                final Delivery dialog = showProgressDialog("删除中.....");

                this.myHandler.setHandlerCallBack(new HandlerCallBack() {
                    @Override
                    public void executeMessage(Message message) {
                        dialog.dismiss();
                        if(!(boolean)message.obj){
                            showAlertDialog("警告", "删除 "+file.getName() + " 失败");
                        }else{
                            fileItems.remove(position);
                            fileAdapter.notifyDataSetChanged();
                        }
                    }
                });

                this.myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.obj = fileTools.delFile(file.getAbsolutePath());
                        myHandler.sendMessage(message);
                    }
                });
                break;
            //copy
            case 1:
                //TODO 一个选择复制路径
                showProgressDialog("正在复制文件");
//                fileTools.copyFile()
                break;
            //share
            case 2:
                fileTools.shareFile(file);
                break;
            default:

        }
        return false;
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
                swipeMenuListView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }
    };


    /**
     * 在root目录中显示内存卡和内置内存卡有多少空间，还有多少空间剩余
     *
     * @param path  lujin
     * @return lujin
     */
    private String getRootVolume(String path) {
        StatFs stat = new StatFs(path);
        long total = (long) stat.getBlockCount() * (long) stat.getBlockSize();
        long free = (long) stat.getAvailableBlocks()
                * (long) stat.getBlockSize();
        if (total == 0) {
            return "";
        }
        return "Free " + fileTools.formatFileSize(free) + " of " + fileTools.formatFileSize(total);
    }

    /**
     * 根目录的情况
     */
    private void listRoots() {
        currentDir = null;
        fileItems.clear();
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
                fileItem.setOther(getRootVolume(path));
                fileItem.setFile(new File(path));
                this.fileItems.add(fileItem);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            index++;
        }
        this.swipeMenuListView.setSwipeEnabled(false);
        fileAdapter.notifyDataSetChanged();
    }

    /**
     * 因为某些情况造成SDCard不可读的页面提示信息
     *
     * @param message
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
     * @param dir
     * @return true 代表成功展示子文件夹
     */
    private boolean listFiles(File dir) {
        this.swipeMenuListView.setSwipeEnabled(true);
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
        setEmptyView("NoFiles");
        try {
            this.fileItems.clear();
            List<FileItem> readFileItems = fileTools.getChildrenFilesInfo(dir.getAbsolutePath());
            Collections.sort(readFileItems, fileTools.increaseNameSort());
            this.fileItems.addAll(readFileItems);
        } catch (Exception e) {
            e.printStackTrace();
            showAlertDialog("警告", e.getMessage());
            return false;
        }

        //添加上一层目录
        FileItem item = new FileItem();
        item.setName("...");
        item.setImageId(R.drawable.ic_fso_type_folder);
        //file是空，注意看这里的OnClickListener
        item.setFile(null);
        this.fileItems.add(0, item);

        this.fileAdapter.notifyDataSetChanged();
        return true;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fileTools = new FileTools(getActivity());
        this.historyEntities = new Stack<>();
        this.fileItems = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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


        View root = inflater.inflate(R.layout.file_explorer_list, container, false);
        this.emptyView = (LinearLayout) root.findViewById(R.id.sd_not_available_page);


        this.swipeMenuListView = (SwipeMenuListView) root.findViewById(R.id.file_listView);
        //设置空的View
        this.swipeMenuListView.setEmptyView(this.emptyView);
        this.swipeMenuListView.setOnItemClickListener(this);
        this.swipeMenuListView.setOnItemLongClickListener(this);

        this.fileAdapter = new FileAdapter(getActivity(), this.fileItems);
        this.createAnimationAdapter(this.swipeMenuListView, fileAdapter);

        SwipeMenuCreator creator = this.createSwipeMenuCreator(getActivity(), new int[][]{{R.drawable.ic_delete_dark, Color.rgb(0xF9, 0x3F, 0x25)},
                {R.drawable.ic_copy_dark, Color.rgb(0xEA, 0xff, 0x56)},
                {R.drawable.ic_share_dark, Color.rgb(0xC9, 0xC9, 0xCE)}});


        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setOnMenuItemClickListener(this);
        swipeMenuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

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
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 设置回调事件
     *
     * @param explorerCallBack
     */
    public void setExplorerCallBack(ExplorerCallBack explorerCallBack) {
        this.explorerCallBack = explorerCallBack;
    }


    /**
     * 显示提示框
     *
     * @param message
     */
    public Delivery showAlertDialog(String title, String message) {
        Delivery dialog;
        if("".equals(title)){
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
        }else {
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
     * @param message
     */
    public Delivery showProgressDialog(String message){
        Delivery dialog = PostOffice.newMail(getActivity())
                .setDesign(Design.MATERIAL_LIGHT)
                .setThemeColor(R.color.dialogColor)
                .setStyle(new ProgressStyle.Builder(getActivity())
                        .setProgressStyle(ProgressStyle.NORMAL)
                        .setProgressMessage(message)
                        .build())
                .build();
        dialog.show(getFragmentManager());
        return dialog;
    }

    /**
     * 创建动作ListView适配器
     *
     * @param swipeMenuListView 需要添加进动画的ListView
     * @param adapter           需要的数据适配器
     * @return
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
     * 将dp单位转化为px的单位
     *
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * 创建滑动的按钮
     *
     * @param iconAndColors 滑动按钮需要的图标和颜色
     * @param context
     * @return
     */
    public SwipeMenuCreator createSwipeMenuCreator(final Context context, final int[][] iconAndColors) {
        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                for (int i = 0; i < iconAndColors.length; i++) {
                    if (iconAndColors[i].length != 2) {
                        throw new IllegalArgumentException("The Icon and Color must set !");
                    }
                    // create "info" item
                    SwipeMenuItem item = new SwipeMenuItem(context);
                    // set item title
                    item.setIcon(iconAndColors[i][0]);
                    // set item width
                    item.setWidth(dp2px(56));

                    // set item background
                    item.setBackground(new ColorDrawable(iconAndColors[i][1]));
                    // add to menu
                    menu.addMenuItem(item);
                }
            }
        };
        return swipeMenuCreator;
    }

    /**
     * 函数回调接口
     */
    interface ExplorerCallBack {
        /**
         * 更新标题栏
         *
         * @param title
         */
        public void updateTitle(String title);

        /**
         * 选择的文件的路径
         *
         * @param path
         */
        public void selectedFile(String path);
    }

    /**
     * 点击了返回键，如果不是根目录就不退出
     *
     * @return
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
            this.swipeMenuListView.setSelection(historyEntity.scrollItem);
            return false;
        } else {
            //最后一页在Activity中，退出
            return true;
        }
    }


    class MyHandler extends Handler{
        private HandlerCallBack handlerCallBack;

        public MyHandler(){
            super();
        }

        public MyHandler(HandlerCallBack handlerCallBack){
            this.handlerCallBack = handlerCallBack;
        }

        @Override
        public void handleMessage(Message msg) {
            handlerCallBack.executeMessage(msg);
        }

        public void setHandlerCallBack(HandlerCallBack handlerCallBack){
            this.handlerCallBack = handlerCallBack;
        }

    }

    private interface HandlerCallBack{
        public void executeMessage(Message message);
    }
}
