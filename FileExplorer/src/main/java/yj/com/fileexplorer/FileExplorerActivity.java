package yj.com.fileexplorer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 文件浏览管理的Activity
 */
public class FileExplorerActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FileExplorerFragment fileExplorerFragment;
    private ActionBar actionBar;
    //选择的文件夹的地址
    private String selectedDirectoryPath;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_explorer);

        this.actionBar = super.getSupportActionBar();
        this.actionBar.setTitle("文件浏览器");
        this.actionBar.setDisplayHomeAsUpEnabled(true);

        this.fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
        this.fileExplorerFragment = new FileExplorerFragment();
        this.fileExplorerFragment.setExplorerCallBack(new FileExplorerFragment.ExplorerCallBack() {
            @Override
            public void updateTitle(String title) {
                if("".equals(title)){
                    actionBar.setTitle("文件浏览器");
                }else {
                    actionBar.setTitle(title);
                }
            }

            @Override
            public void selectedFile(String path) {
                //TODO 传递出去消息
            }
        });
        fragmentTransaction.add(R.id.fragment_container, this.fileExplorerFragment, "RootFragment");
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_explorer, menu);
        return true;
    }

    /**
     * ActionBar的下拉菜单和Home的事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //点击了actionBar的返回键
            case android.R.id.home:
                super.onDestroy();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onDestroy() {
        this.fileExplorerFragment.onFragmentDestroy();
        this.finish();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {

       //在Fragment中按了返回按钮
        if (this.fileExplorerFragment.onBackPressed()) {
            //已经是最后一页了，返回
            super.onBackPressed();
        }
    }
}
