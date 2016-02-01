package yj.com.fileexplorer.state;

import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.File;

import yj.com.fileexplorer.FileTools;
import yj.com.fileexplorer.R;

/**
 * 能够进行完全文件操作的类，有所有功能
 */
public class NormalFragment extends FileExplorerFragment{
    private String TAG = getClass().getSimpleName();
    private FileTools.OperateType operateType;

    /**
     * 点击长按以后的多选事件
     * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1105/1906.html
     */
    private class MultipleChoiceModeCallBack implements ListView.MultiChoiceModeListener {
        private ActionMode actionMode;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Log.e(TAG, "oncreate");
            Log.e(TAG, operateType.getChineseValue());

            //不允许在root文件夹下面或者是有执行状态的时候启用
            if(currentDir == null || operateType != FileTools.OperateType.EMPTY){
                listView.clearChoices();
                mode.finish();
                this.actionMode = null;
                return false;
            }else{
                // actionmode的菜单处理
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.multi_select_menu, menu);
                this.actionMode = mode;
                mode.setTitle("已选择");
                return true;
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            Log.e(TAG, "prepare");
            if (this.actionMode == null) {
                this.actionMode = mode;
            }

            //更新菜单的状态
            MenuItem mItem = menu.findItem(R.id.menu_mulSelectState);
            if (listView.getCheckedItemCount() == fileAdapter.getCount()) {
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
            Log.e(TAG, "action click");

            int id = item.getItemId();

            if (item.getItemId() == R.id.menu_mulSelectState) {
                if (listView.getCheckedItemCount() == fileAdapter.getCount()) {
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
            Log.e(TAG, "destory action");
            listView.clearChoices();
            this.actionMode = null;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position, long id, boolean checked) {
            Log.e(TAG, "item check");

            if (checked) {
                operateFiles.add(fileItems.get(position).getFile());
            } else {
                operateFiles.remove(fileItems.get(position).getFile());
            }
            updateSelectedState();
            mode.invalidate();
            fileAdapter.notifyDataSetChanged();
        }

        private void updateSelectedState() {
            actionMode.setSubtitle(listView.getCheckedItemCount() + "");
        }

        private void selectedAll() {
            for (int i = 0; i < fileAdapter.getCount(); i++) {
                listView.setItemChecked(i, true);
                operateFiles.add(fileAdapter.getItem(i).getFile());
            }
            updateSelectedState();
        }

        private void unSelectedAll() {
            listView.clearChoices();
            listView.setItemChecked(0, false);
            updateSelectedState();
            operateFiles.clear();
        }

        public ActionMode getActionMode(){
            return this.actionMode;
        }

        private void destoryActionMode(ActionMode mode){
            mode.finish();
            this.actionMode = null;
        }

    }
}
