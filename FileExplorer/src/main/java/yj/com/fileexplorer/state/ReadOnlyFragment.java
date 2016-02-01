package yj.com.fileexplorer.state;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.io.File;

/**
 * 只能文件的状态
 */
public class ReadOnlyFragment extends FileExplorerFragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected boolean listFiles(File dir) {
        boolean flag = super.listFiles(dir);
        return flag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //没有选取模式
       listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

}
