package com.yj.eleccharge.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yj.eleccharge.R;
import com.yj.eleccharge.activity.TempFragment;
import com.yj.eleccharge.util.DrawerListAdapter;
import com.yj.eleccharge.util.DrawerListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerListView;

    /**
     * 承载当前fragment的容器为父亲的activity
     */
    private View mFragmentContainerView;

    /**
     * 当前选择的是第几个页面
     */
    private int mCurrentSelectedPosition = 0;

    /**
     * 是否是正在使用fragment，即是否是从之前的activity暂停的
     */
    private boolean mFromSavedInstanceState;

    /**
     * 用户是否已经知道有drawer这个工具
     */
    private boolean mUserLearnedDrawer;

    /**
     * 用来存放adapter中list的数据
     */
    private List<DrawerListItem> mListData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //从SharedPreference中读取，看用户是否是第一次使用这个软件
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        //这个当这个Fragment被重新调用的时候，从savedInstanceState中取出保存的当前选的是第几个的对象
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true; //在设置当item里面
            selectItem(mCurrentSelectedPosition);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.

        // 设置该fragment拥有自己的actionbar action item
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //找到Drawer的layout
        LinearLayout mDrawerLinearLayout = (LinearLayout) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        //找到下面的ListView
        mDrawerListView = (ListView) mDrawerLinearLayout.findViewById(R.id.drawer_listView);

        //设置点击list事件监听
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        //通过Resource获得Strings.xml文件的drawer字段和相应的class字段
        String titlesAndClasses[] = getResources().getStringArray(R.array.drawer_item_title);

//        Drawer icon
        int icons[] = {R.drawable.ic_drawer_group, R.drawable.ic_drawer_question, R.drawable.ic_drawer_account, R.drawable.ic_drawer_time, R.drawable.ic_drawer_setting};

        mListData = new ArrayList<>();

        for (int i = 0; i < titlesAndClasses.length; i++) {
            //通过“|”符号区分开来的字符,因为string-arrays里面写了title|classString
            String[] arrays = titlesAndClasses[i].split("\\|");
            DrawerListItem drawerListItem = new DrawerListItem(getResources().getDrawable(icons[i]), arrays[0], arrays[1]);
            mListData.add(drawerListItem);
        }

        DrawerListAdapter drawerListAdapter = new DrawerListAdapter(this.getActivity(), mListData);
        mDrawerListView.setAdapter(drawerListAdapter);

        //选择一开始打开应用时候的页面
        selectItem(mCurrentSelectedPosition);
        //设置当前打开的选项
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);


        //设置用户登录这个选项的操作
        View drawerHeader = mDrawerLinearLayout.findViewById(R.id.drawer_header);
        drawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TempFragment.class);
                getActivity().startActivity(intent);
            }
        });

        return mDrawerLinearLayout;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the menu_search content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 隐藏Action bar上的app icon
        //actionBar.setDisplayShowHomeEnabled(false);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        //用户不知道有Drawer框就打开Drawer给他显示一下，让他知道有这个功能
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * 选择相应的item，之后通过回调函数显示
     * @see com.yj.eleccharge.fragment.NavigationDrawerFragment.NavigationDrawerCallbacks#onNavigationDrawerItemSelected(DrawerListItem)
     * @param position
     */
    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(mListData.get(position));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
            Log.e("drawer", "open");
            //TODO 下级fragment出现setOptionMenu会出现遮挡的情况
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * 回调接口，所有的fragment，必须实现这个接口
     */
    public interface NavigationDrawerCallbacks {
        /**
         * 选择对应的item项目，然后打开drawer
         */
        void onNavigationDrawerItemSelected(DrawerListItem drawerListItem);
    }
}
