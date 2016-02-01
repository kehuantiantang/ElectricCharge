package yj.com.fileexplorer.state;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Design;

import java.io.File;

import yj.com.fileexplorer.R;

/**
 * 可以进行长按选择的Fragment
 */
public class SingleSelectFragment extends FileExplorerFragment implements AdapterView.OnItemLongClickListener {
    private String TAG = getClass().getSimpleName();
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
        Toast.makeText(getActivity(), "请长按选择指定文件夹", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        super.listView.setOnItemLongClickListener(this);
        return view;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final File file = super.fileItems.get(position).getFile();
        if (file.isDirectory()) {
            PostOffice.newMail(getActivity())
                    .setTitle("警告")
                    .setThemeColor(R.color.dialogColor)
                    .setButtonTextColor(Dialog.BUTTON_POSITIVE, android.R.color.holo_red_light)
                    .setDesign(Design.MATERIAL_LIGHT).setMessage("选择该文件夹存储数据？")
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
        }
        return true;
    }
}
