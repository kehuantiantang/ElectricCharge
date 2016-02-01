package yj.com.fileexplorer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import yj.com.fileexplorer.state.FileExplorerFragment;
import yj.com.fileexplorer.state.NormalFragment;

/**
 * 文件浏览管理的Activity
 */
public class FileExplorerActivity extends AppCompatActivity {
    private FileExplorerFragment fileExplorerFragment;

    private FragmentManager fragmentManager;
    private ActionBar actionBar;


    //显示图标
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    //选择的文件夹的地址
    private String selectedDirectoryPath;

    private String TAG = getClass().getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_explorer);

        this.actionBar = super.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        this.actionBar.setTitle("文件浏览器");

        this.fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        this.fileExplorerFragment = new NormalFragment();
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
        fragmentTransaction.add(R.id.fragment_container, this.fileExplorerFragment);
        fragmentTransaction.commit();

        this.setOverflowMenu();
    }




    /**
     * 通过反射强制让Menu显示在ActionBar上面,在OnCreate中调用
     * http://www.cnblogs.com/xiaofeixiang/p/4034067.html
     */
    private void setOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                super.onBackPressed();
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        this.fileExplorerFragment.onFragmentDestroy();
        super.onDestroy();
        this.finish();
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
