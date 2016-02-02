package yj.com.fileexplorer.state;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import yj.com.fileexplorer.FileTools;
import yj.com.fileexplorer.R;

/**
 * 可以选择多个文件进行分享
 */
public class MultiSelectFragment extends NormalFragment {
    private String TAG = getClass().getSimpleName();


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//        Log.e(TAG, "oncreate");
//        Log.e(TAG, operateType.getChineseValue());
        // actionmode的菜单处理
        this.actionMode = mode;

        // actionmode的菜单处理
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_mul_select_only, menu);
        this.actionMode = mode;
        mode.setTitle("已选择");

        //TODO 改变状态,完成其他多选操作
        showFooter(true , FileTools.OperateType.SHARE);
        this.operateType = FileTools.OperateType.SHARE;

        return true;
    }


    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.menu_mulSelectState) {
            int selectSize = listView.getCheckedItemCount();
            if((filenameFilter != null && fileTools.list(currentDir.getAbsolutePath(), filenameFilter) == selectSize )|| selectSize == fileAdapter.getCount()){
                unSelectedAll();
            } else {
                selectedAll();
            }
            fileAdapter.notifyDataSetChanged();
        }
        return true;
    }

}
