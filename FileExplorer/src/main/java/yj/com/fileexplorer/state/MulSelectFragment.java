package yj.com.fileexplorer.state;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FilenameFilter;

import yj.com.fileexplorer.R;

/**
 * Created by Sober on 2016/2/1.
 */
public class MulSelectFragment extends ReadOnlyFragment implements ListView.MultiChoiceModeListener{
    private FilenameFilter filenameFilter;
    private boolean isActionModeStarted = false;


    public void setFilenameFilter(FilenameFilter filenameFilter) {
        this.filenameFilter = filenameFilter;
    }

    private final static int MENU_NEW_FOLDER = 0xdddfdf;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(), "请长按选择文件夹", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem newFolderMenuItem = menu.add(Menu.NONE, MENU_NEW_FOLDER, Menu.NONE, "新建文件夹");
        newFolderMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        newFolderMenuItem.setIcon(R.drawable.ic_fso_type_folder);
        newFolderMenuItem.setVisible(false);
        newFolderMenuItem.setEnabled(false);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setOnItemClickListener(this);
        //多选回调事件
        listView.setMultiChoiceModeListener(this);
        return root;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
