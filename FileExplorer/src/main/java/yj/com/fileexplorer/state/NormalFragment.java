package yj.com.fileexplorer.state;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonRectangle;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import yj.com.fileexplorer.FileItem;
import yj.com.fileexplorer.FileTools;
import yj.com.fileexplorer.IFileFilter;
import yj.com.fileexplorer.R;
import yj.com.fileexplorer.ui.QuickReturnListViewOnScrollListener;
import yj.com.fileexplorer.ui.QuickReturnViewType;

/**
 * 能够进行完全文件操作的类，有所有功能
 */
public  class NormalFragment extends ReadOnlyFragment implements View.OnClickListener, ListView.MultiChoiceModeListener {
    private String TAG = getClass().getSimpleName();

    protected FileTools.OperateType operateType;
    private FooterViewHolder footerViewHolder;
    protected Set<File> operateFiles;
    private MyHandler myHandler = new MyHandler();


    /**
     * 存储需要经常使用的View
     */
    static class FooterViewHolder {
        View footer;
        Map<Integer, Button> button = new HashMap<>();
        QuickReturnListViewOnScrollListener scrollListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.operateFiles = new HashSet<>();
        this.operateType = FileTools.OperateType.EMPTY;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        this.footerViewHolder = initFooterViewHolder(root);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setOnItemClickListener(this);
        //多选回调事件
        listView.setMultiChoiceModeListener(this);
        return root;
    }

    @Override
    protected boolean listFiles(File dir) {
        boolean flag = super.listFiles(dir);
        this.footerViewHolder.button.get(R.id.confirm_buttonRectangle).setEnabled(true);
        showOrHideMenus(myMenu, true, MENU_NEW_FOLDER, MENU_MUL_SELECT);
        return flag;
    }


    @Override
    protected void listRoots() {
        super.listRoots();
        if (this.operateType != FileTools.OperateType.EMPTY) {
            this.footerViewHolder.button.get(R.id.confirm_buttonRectangle).setEnabled(false);
        }
        showOrHideMenus(myMenu, false, MENU_NEW_FOLDER, MENU_MUL_SELECT);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        //ActionMode不需要了
        actionMode.finish();

        if (id == R.id.cancel_buttonRectangle) {
//            Log.e(TAG, "cancel_buttonRectangle");

            //隐藏footer
            showFooter(false, null);
            this.operateType = FileTools.OperateType.EMPTY;
            operateFiles.clear();

        } else if (id == R.id.confirm_buttonRectangle) {
//            Log.e(TAG, "confirm_buttonRectangle");
            showFooter(false, null);

            final String targetPath = currentDir.getAbsolutePath();

            backgroundOperate(targetPath, this.operateType);


        } else {
            Log.e(TAG, "onClick");
        }
    }

    /**
     * 显示粘贴，复制这些的footer
     *
     * @param visible     可见？
     * @param operateType 操作
     */
    protected void showFooter(boolean visible, FileTools.OperateType operateType) {
        if (footerViewHolder != null) {
            footerViewHolder.scrollListener.setScrollListenerEnabled(visible);
            this.listView.setOnScrollListener(footerViewHolder.scrollListener);
            footerViewHolder.footer.setVisibility(visible ? View.VISIBLE : View.GONE);
            if (operateType != null) {
                footerViewHolder.button.get(R.id.confirm_buttonRectangle).setText(operateType.getChineseValue());
            }
        } else {
            Log.e(TAG, "Key Error");
        }
    }


    /**
     * 初始化系统需要的ViewHolder
     *
     * @param root
     */
    private FooterViewHolder initFooterViewHolder(View root) {
        //获得footer的高度
        int footerHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.footer_height);

        FooterViewHolder confirmFooterViewHolder = new FooterViewHolder();

        //其中的按钮
        int[] confirmButtonIds = {R.id.confirm_buttonRectangle, R.id.cancel_buttonRectangle};
        for (int id : confirmButtonIds) {
            ButtonRectangle button = (ButtonRectangle) root.findViewById(id);
            button.setOnClickListener(this);
            confirmFooterViewHolder.button.put(id, button);
        }
        View confirmFooter = root.findViewById(R.id.confirm_bar);
        confirmFooterViewHolder.footer = confirmFooter;

        //设置onScrollListener监听事件
        QuickReturnListViewOnScrollListener confirmListener = new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .footer(confirmFooter)
                .minFooterTranslation(footerHeight)
                .build();
        confirmFooterViewHolder.scrollListener = confirmListener;

        return confirmFooterViewHolder;
    }


    private static final int MENU_MUL_SELECT = 0xfafddd;
    private static final int MENU_NEW_FOLDER = 0xfafddc;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem mulSelectMenuItem = menu.add(Menu.NONE, MENU_MUL_SELECT, Menu.NONE, "多选");
        mulSelectMenuItem.setIcon(R.drawable.ic_fso_type_app);
        mulSelectMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        mulSelectMenuItem.setVisible(false);
        mulSelectMenuItem.setEnabled(false);

        MenuItem newFolderMenuItem = menu.add(Menu.NONE, MENU_NEW_FOLDER, Menu.NONE, "新建文件夹");
        newFolderMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        newFolderMenuItem.setIcon(R.drawable.ic_fso_type_folder);
        newFolderMenuItem.setVisible(false);
        newFolderMenuItem.setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean flag = super.onOptionsItemSelected(item);
        if (!flag) {
            switch (item.getItemId()) {
                case MENU_MUL_SELECT:
                    setActionModeStarted(true);
                    listView.startActionMode(this);

                    return true;
                case MENU_NEW_FOLDER:
                    //根文件夹无效
                    if (currentDir != null) {
                        PostOffice.newMail(getActivity())
                                .setTitle("新建文件夹")
                                .setThemeColor(R.color.dialogColor)
                                .setDesign(Design.MATERIAL_LIGHT)
                                .showKeyboardOnDisplay(true)
                                .setButton(Dialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setStyle(new EditTextStyle.Builder(getActivity())
                                        .setHint("输入文件夹名称")
                                        .setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL)
                                        .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                            @Override
                                            public void onAccepted(final String text) {
                                                backgroundOperate(text, FileTools.OperateType.NEW_FOLDER);
                                            }
                                        }).build()).show(getFragmentManager());
                    }
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * 封装需要重复写的Handler的一些操作
     *
     * @param targetPath   目标地址，如果是两个参数要写这个， 否则为null
     * @param operateType1 操作的类型
     */
    protected void backgroundOperate(final String targetPath, final FileTools.OperateType operateType1) {
        final Delivery dialog = showProgressDialog("正在" + operateType1.getChineseValue() + "......");

        myHandler.setHandlerCallBack(new MyHandler.HandlerCallBack() {
            @Override
            public void executeMessage(Message message) {
                String log = message.obj.toString();
                if (!"".equals(log)) {
                    showAlertDialog("失败", log);
                }
                dialog.dismiss();
                //操作完成，去掉所有的操作文件
                operateFiles.clear();
                operateType = FileTools.OperateType.EMPTY;

                //重新刷新当前页
                listFiles(currentDir);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer log = new StringBuffer();
                try {
                    boolean isOneParameter = true;
                    Method operate;
                    //一个参数的
                    if (targetPath == null) {
                        operate = FileTools.class.getMethod(operateType1.getValue(), String.class);
                    } else {
                        //两个参数的
                        operate = FileTools.class.getMethod(operateType1.getValue(), String.class, String.class);
                        isOneParameter = false;
                    }

                    for (File file : operateFiles) {
//                        Log.e(TAG, file.getName());
                        if (!isOneParameter) {
                            //两个参数的
                            if (!(boolean) operate.invoke(fileTools, file.getAbsolutePath(), targetPath)) {
                                log.append(file.getName() + "\n");
                            }
                        } else {
                            if (!(boolean) operate.invoke(fileTools, file.getAbsolutePath())) {
                                log.append(file.getName() + "\n");
                            }
                        }
                    }
                    Message message = new Message();
                    message.obj = log;
                    myHandler.sendMessage(message);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static class MyHandler extends Handler {
        private HandlerCallBack handlerCallBack;

        public MyHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            handlerCallBack.executeMessage(msg);
        }

        public void setHandlerCallBack(HandlerCallBack handlerCallBack) {
            this.handlerCallBack = handlerCallBack;
        }

        interface HandlerCallBack {
            void executeMessage(Message message);
        }
    }



    protected ActionMode actionMode;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//        Log.e(TAG, "oncreate");
//        Log.e(TAG, operateType.getChineseValue());

        //不允许在root文件夹下面或者是有执行状态的时候启用
        if (currentDir == null || operateType != FileTools.OperateType.EMPTY) {
            Log.e(TAG, "exit actionMode");

            listView.clearChoices();
            mode.finish();
            setActionModeStarted(false);
            return false;
        } else {

            // actionmode的菜单处理
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_mul_select_normal, menu);
            this.actionMode = mode;
            mode.setTitle("已选择");
            return true;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//        Log.e(TAG, "prepare");
        if (this.actionMode == null) {
            this.actionMode = mode;
        }

        //更新菜单的状态
        MenuItem mItem = menu.findItem(R.id.menu_mulSelectState);
        int selectSize = listView.getCheckedItemCount();
        if(fileTools.list(currentDir.getAbsolutePath(), filenameFilter) == selectSize || selectSize == fileAdapter.getCount()){
            mItem.setTitle("全不选");
        } else {
            mItem.setTitle("全选");
        }

        if (listView.getCheckedItemCount() == 1) {
            //显示详细信息
            showOrHideMenus(menu, true, R.id.menu_detail);
        } else {
            showOrHideMenus(menu, false, R.id.menu_detail);
        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//        Log.e(TAG, "action click");

        int id = item.getItemId();

        if (item.getItemId() == R.id.menu_mulSelectState) {
            int selectSize = listView.getCheckedItemCount();
            if(fileTools.list(currentDir.getAbsolutePath(), filenameFilter) == selectSize || selectSize == fileAdapter.getCount()){
                unSelectedAll();
            } else {
                selectedAll();
            }
            fileAdapter.notifyDataSetChanged();
        } else if (id == R.id.menu_delete) {
            backgroundOperate(null, FileTools.OperateType.DELETE);
            mode.finish();
        } else if (id == R.id.menu_copy) {
            //显示footer
            showFooter(true, FileTools.OperateType.COPY);
            operateType = FileTools.OperateType.COPY;

            mode.finish();
        } else if (id == R.id.menu_cut) {
            //显示footer
            showFooter(true, FileTools.OperateType.CUT);
            operateType = FileTools.OperateType.CUT;

            mode.finish();
        } else if (id == R.id.menu_share) {
            operateType = FileTools.OperateType.SHARE;
            Log.e(TAG, "share");

            mode.finish();
        } else if (id == R.id.menu_detail) {
            //不是文件夹，显示详细信息
            try {
                File file = operateFiles.iterator().next();
                showAlertDialog(file.getName(), fileTools.getFileDetailInfo(file.getAbsolutePath()));
            } catch (Exception e) {
                showAlertDialog("警告", e.getMessage());
            }

            mode.finish();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
//        Log.e(TAG, "destory action");
        listView.clearChoices();
        //隐藏footer状态
        showFooter(false, null);
        setActionModeStarted(false);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode,
                                          int position, long id, boolean checked) {
//        Log.e(TAG, "item check");

        if (checked) {
            File file = fileItems.get(position).getFile();
            if((filenameFilter != null && filenameFilter.accept(file.getParentFile(), file.getName())) || filenameFilter == null ) {
                operateFiles.add(fileItems.get(position).getFile());
            }else{
                listView.setItemChecked(position, false);
            }
        } else {
            operateFiles.remove(fileItems.get(position).getFile());
        }
        updateSelectedState();
        mode.invalidate();
        fileAdapter.notifyDataSetChanged();
    }

    protected void updateSelectedState() {
        actionMode.setSubtitle(listView.getCheckedItemCount() + "");
    }

    protected void selectedAll() {
        int index = 0;
        for (FileItem fileItem : fileItems) {
            File file = fileItem.getFile();
            if (filenameFilter == null || (filenameFilter != null && filenameFilter.accept(file.getParentFile(), file.getName()))) {
                listView.setItemChecked(index, true);
                operateFiles.add(file);
            }
            index++;
        }
        updateSelectedState();
    }

    protected void unSelectedAll() {
        listView.clearChoices();
        listView.setItemChecked(0, false);
        updateSelectedState();
        operateFiles.clear();
    }

    //文件名字过滤器，注意第一个参数dir是
    protected IFileFilter filenameFilter;

    /**
     * 设置文件过滤器，注意第一个参数dir是该文件所在的文件夹的File，第二个是该文件的文件名
     * @param filenameFilter 过滤器
     *
     */
    public void setIFileFilter(IFileFilter filenameFilter) {
        this.filenameFilter = filenameFilter;
    }
}
