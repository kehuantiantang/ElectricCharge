package com.yj.eleccharge.fragment;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ButtonFloat;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.yj.eleccharge.R;

import java.util.List;

/**
 * Created by 游捷 on 2015/8/19.
 * 滑动ListView的fragment创建
 */
public abstract class SwipeItemFragment extends Fragment {

    protected BaseAdapter swipeListAdapter;

    protected List swipeListData;

    private SwipeMenuCreator creator;

    protected ButtonFloat buttonFloat;

    private int[] swipeIcons;
    private int[] swipeColorDrawables;

    //回调函数，用来对每个函数中添加一些需要的额外的功能
    private IFunction iFunction;

    private int buttonFloatId;

    //滑动按钮
    protected SwipeMenuListView swipeMenuListView;

    /**
     * 初始化数据列表List
     *
     * @see SwipeItemFragment#swipeListData
     */
    protected abstract void initAdapterAndListData();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iFunction.onCreateExecuteBefore();

        //这里设置了才会出现自定义的menu和ActionBar
        setHasOptionsMenu(true);

        //初始化ListData
        initAdapterAndListData();

        // step 1. 创建滑动出的按钮
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                for (int i = 0; i < swipeIcons.length; i++) {
                    SwipeMenuItem item = new SwipeMenuItem(
                            getActivity().getApplicationContext());
                    // set item background
                    item.setBackground(new ColorDrawable(swipeColorDrawables[i]));
                    // set item width
                    item.setWidth(dp2px(56));
                    // set item title
                    item.setIcon(swipeIcons[i]);
                    // add to menu
                    menu.addMenuItem(item);
                }
            }
        };
        iFunction.onCreateExecuteAfter();
    }

    protected interface IFunction {
        /**
         * 在onCreate方法执行最前面调用
         */
        public void onCreateExecuteBefore();

        /**
         * 在执行了onCreate方法最后面调用
         */
        public void onCreateExecuteAfter();

        /**
         * 在执行了onCreateView方法最前调用
         */
        public void onCreateViewExecuteBefore();

        /**
         * 在执行了onCreateView方法的最后调用
         */
        public void onCreateViewExecuteAfter();

        /**
         * 在执行了onCreateOptionsMenu方法的最前面调用
         */
        public void onCreateOptionsMenuBefore();

        /**
         * 在执行了onCreateOptionsMenu的最后面调用
         */
        public void onCreateOptionsMenuAfter();
    }

    /**
     * 设置Swipe栏需要的图片和颜色参数
     *
     * @param swipeIcons
     * @param swipeColorDrawables
     */
    protected void setSwipeIconsColors(int[] swipeIcons, int[] swipeColorDrawables) {
        this.swipeIcons = swipeIcons;
        this.swipeColorDrawables = swipeColorDrawables;
    }

    /**
     * 将dp单位转化为px的单位
     *
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * 设置ButtonFloat的ID
     *
     * @param id
     */
    protected void setButtonFloatId(int id) {
        this.buttonFloatId = id;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.iFunction.onCreateViewExecuteBefore();
        View rootView = inflater.inflate(R.layout.swipelist, null);

        //************************SwipeMenuList*************************************
        swipeMenuListView = (SwipeMenuListView) rootView.findViewById(R.id.swipeListView);


        //ListView 的animation
        AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(swipeListAdapter);

        animAdapter.setAbsListView(swipeMenuListView);

        //设置第一次动画的时间,注意顺序一定要先设置绑定的listView
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(200);


        //为list设置动画的adapter
        swipeMenuListView.setAdapter(animAdapter);


        //设置Menu的按钮
        swipeMenuListView.setMenuCreator(creator);


        //设置滑动出现的方向
        swipeMenuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        //*********************************************************************


        //Float Button
        this.buttonFloat = (ButtonFloat) rootView.findViewById(R.id.buttonFloat);
        buttonFloat.setDrawableIcon(getActivity().getResources().getDrawable(this.buttonFloatId));

        this.iFunction.onCreateViewExecuteAfter();
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Toast.makeText(getActivity(), "search", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.iFunction.onCreateOptionsMenuBefore();
        getActivity().getMenuInflater().inflate(R.menu.global, menu);

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setQueryHint("提示");
//        /**
//         * 默认情况下, search widget是"iconified“的，只是用一个图标 来表示它(一个放大镜),
//         * 当用户按下它的时候才显示search box . 你可以调用setIconifiedByDefault(false)让search
//         * box默认都被显示。 你也可以调用setIconified()让它以iconified“的形式显示。
//         */
//        searchView.setIconifiedByDefault(true);
//        /**
//         * 默认情况下是没提交搜索的按钮，所以用户必须在键盘上按下"enter"键来提交搜索.你可以同过setSubmitButtonEnabled(
//         * true)来添加一个提交按钮（"submit" button)
//         * 设置true后，右边会出现一个箭头按钮。如果用户没有输入，就不会触发提交（submit）事件
//         */
//        searchView.setSubmitButtonEnabled(true);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                swipeListData.remove(0);
//                swipeListData.remove(1);
//                swipeListAdapter.notifyDataSetChanged();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return true;
//            }
//        });

        this.iFunction.onCreateOptionsMenuAfter();
    }

    public class SwipeListItem {
        private Drawable icon;
        private String title;

        public SwipeListItem() {
            super();
        }

        public SwipeListItem(Drawable icon, String title) {
            this.icon = icon;
            this.title = title;
        }

        public Drawable getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    /**
     * Created by 游捷 on 2015/8/19.
     */
    public class SwipeListAdapter extends BaseAdapter {

        private List<SwipeListItem> swipeListData;

        private LayoutInflater layoutInflater;


        public SwipeListAdapter(Context context, List<SwipeListItem> swipeListData) {
            this.swipeListData = swipeListData;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return swipeListData.size();
        }

        @Override
        public SwipeListItem getItem(int position) {
            return swipeListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SwipeListItem item = getItem(position);
            if (convertView == null) {
//            convertView = layoutInflater.inflate(R.layout.base_swipe_list_item, null);
            }

//        TextView itemTitle = (TextView) convertView.findViewById(R.id.swipeList_item_title);
//        ImageView itemIcon = (ImageView) convertView.findViewById(R.id.swipeList_item_icon);
//        itemTitle.setText(item.getTitle());
//        itemIcon.setBackground(item.getIcon());
            return convertView;
        }
    }

}
