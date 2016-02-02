package yj.com.fileexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.ViewConfiguration;
import android.view.Window;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import yj.com.fileexplorer.state.MultiSelectFragment;
import yj.com.fileexplorer.state.NormalFragment;
import yj.com.fileexplorer.state.ReadOnlyFragment;
import yj.com.fileexplorer.state.SingleSelectFragment;

/**
 * 文件浏览管理的Activity
 */
public class FileExplorerActivity extends AppCompatActivity{
    private ReadOnlyFragment fileExplorerFragment;

    private FragmentManager fragmentManager;
    private ActionBar actionBar;

    /**
     * 回传的数据
     */
    public static final String DELIVERY_STRING = "delivery";
    /**
     * 想要对文件进行过滤，需要传递一个接口
     * @see IFileFilter
     */
    public static final String DELIVERY_INTERFACE = "interface";
    public static final int DELIVERY_RESULT_CODE_OK = 0xffff12;
    public static final int DELIVERY_RESULT_CODE_FAIL = 0xffff13;

    /**
     * 进行文件操作的类型
     *
     * @see ExplorerState
     */
    public static final String EXPLORER_STATE = "EXPLORER_STATE";

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
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        this.fileExplorerFragment = initFragment();

        this.fileExplorerFragment.setExplorerCallBack(new ReadOnlyFragment.ExplorerCallBack() {
            @Override
            public void updateTitle(String title) {
                if ("".equals(title)) {
                    actionBar.setTitle("文件浏览器");
                } else {
                    actionBar.setTitle(title);
                }
            }

            @Override
            public void selectedFiles(Collection<File> files) {
                //TODO 传递出去消息
                Intent intent = new Intent();
                if (files.size() > 0) {
                    ArrayList<String> deliveryData = new ArrayList();
                    for (File file : files) {
                        deliveryData.add(file.getAbsolutePath());
                    }
                    intent.putStringArrayListExtra(DELIVERY_STRING, deliveryData);
                    FileExplorerActivity.this.setResult(DELIVERY_RESULT_CODE_OK, intent);
                } else {
                    FileExplorerActivity.this.setResult(DELIVERY_RESULT_CODE_FAIL, intent);
                }
                fileExplorerFragment.onFragmentDestroy();
                finish();
            }
        });
        fragmentTransaction.add(R.id.fragment_container, this.fileExplorerFragment);
        fragmentTransaction.commit();

        this.setOverflowMenu();
    }

    public ReadOnlyFragment initFragment() {
        ExplorerState state = (ExplorerState) getIntent().getSerializableExtra(EXPLORER_STATE);
        IFileFilter fileFilter = (IFileFilter)getIntent().getSerializableExtra(DELIVERY_INTERFACE);
        if(state != null) {
            switch (state) {
                case SINGLE_SELECT:
                    return new SingleSelectFragment();
                case NORMAL:
                    NormalFragment normalFragment = new NormalFragment();
                    if (fileFilter != null) {
                        normalFragment.setIFileFilter(fileFilter);
                    }
                    return normalFragment;
                case MUL_SELECT:
                    MultiSelectFragment multiSelectFragment = new MultiSelectFragment();
                    if (fileFilter != null) {
                        multiSelectFragment.setIFileFilter(fileFilter);
                    }
                    return multiSelectFragment;
                case READ_ONLY:
                default:
                    return new ReadOnlyFragment();
            }
        }else{
            return new ReadOnlyFragment();
        }
    }

    /**
     * 通过反射强制让Menu显示在ActionBar上面,在OnCreate中调用
     * http://www.cnblogs.com/xiaofeixiang/p/4034067.html
     */
    private void setOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
