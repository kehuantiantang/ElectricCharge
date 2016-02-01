package yj.com.fileexplorer.state;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.io.File;

import yj.com.fileexplorer.R;

/**
 * 只能文件的状态
 */
public class ReadOnlyFragment extends FileExplorerFragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        showOrHideMenus(menu, false, R.id.menu_new_folder, R.id.menu_mulSelected);
    }

    @Override
    protected boolean listFiles(File dir) {
        boolean flag = super.listFiles(dir);
        showOrHideMenus(super.myMenu , false, R.id.menu_new_folder, R.id.menu_mulSelected);
        return flag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //没有选取模式
        super.listViewChoiceMode = ListView.CHOICE_MODE_NONE;
    }

}
