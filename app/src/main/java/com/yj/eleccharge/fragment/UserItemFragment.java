package com.yj.eleccharge.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.R;
import com.yj.eleccharge.activity.AddUserActivity;
import com.yj.eleccharge.entity.User;
import com.yj.eleccharge.util.UserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by 游捷 on 2015/8/19.
 * 滑动ListView的fragment创建
 */
public class UserItemFragment extends Fragment {

//    public static final String CONSTANT_SWIPEFRAGMENT_ARGUMENT = "UserItemFragment";

    private UserAdapter userAdapter;

    private AnimationAdapter animAdapter;

    private List<User> userListData;

    private SwipeMenuCreator creator;

    private DbUtils db;

    public UserItemFragment() {
        super();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //这里设置了才会出现自定义的menu和ActionBar

        setHasOptionsMenu(true);
        refreshData(null);

        //从设置bundle中获取数据，如果没有则默认加载
        //swipeListData = (ArrayList) getArguments().get(CONSTANT_SWIPEFRAGMENT_ARGUMENT);

        //通过数据创建adapter
        userAdapter = new UserAdapter(getActivity().getApplicationContext(), userListData);

        // step 1. 创建滑动出的按钮
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "info" item
                SwipeMenuItem infoItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                infoItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                infoItem.setWidth(dp2px(56));
                // set item title
                infoItem.setIcon(R.drawable.ic_info);
                // add to menu
                menu.addMenuItem(infoItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());

                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(56));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_white);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

    }

    /**
     * refresh data from SQLLite
     *
     * @param keyword
     */
    private void refreshData(String keyword) {
        if (db == null) {
            db = DbTools.getInstance(getActivity());
        }
        List<User> newList;
        try {
            if (keyword == null || keyword.equals("")) {
                newList = db.findAll(User.class);
            } else {
                //if all is numeric
                Pattern pattern = Pattern.compile("[0-9]*");
                if (pattern.matcher(keyword).matches()) {
                    //select by user code
                    newList = db.findAll(Selector.from(User.class).where("code", "like", "%" + keyword + "%"));
                } else {
                    //select by group name
                    newList = db.findAll(Selector.from(User.class).where("name", "like", "%" + keyword + "%"));
                }
            }
            if (newList == null) {
                newList = new ArrayList<>();
            }
            if (userListData == null) {
                userListData = newList;
            } else {
                userListData.clear();
                userListData.addAll(newList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
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

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.swipelist, null);

        //************************SwipeMenuList*************************************
        SwipeMenuListView swipeMenuListView = (SwipeMenuListView) rootView.findViewById(R.id.swipeListView);

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO 这里显示该用户所有的电费记录
                jump2User(position, false);
            }
        });


        //这里可以找到下面的组件，为其添加特殊的信息


        //ListView 的animation
        animAdapter = new SwingLeftInAnimationAdapter(userAdapter);

        animAdapter.setAbsListView(swipeMenuListView);

        //设置第一次动画的时间,注意顺序一定要先设置绑定的listView
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(200);


        //为list设置动画的adapter
        swipeMenuListView.setAdapter(animAdapter);


        //设置Menu的按钮
        swipeMenuListView.setMenuCreator(creator);

        //设置点击按钮的监听事件
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // edit
                        jump2User(position, false);
                        break;
                    case 1:
                        // delete
                        delete(position);
                        break;
                }
                return false;
            }
        });

        //设置滑动出现的方向
        swipeMenuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        //*********************************************************************


        //Float Button
        ButtonFloat addButton = (ButtonFloat) rootView.findViewById(R.id.buttonFloat);
        addButton.setDrawableIcon(getResources().getDrawable(R.drawable.ic_new));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddUserActivity.class);
                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.push_up_in,
//                        R.anim.push_up_out);
//                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            }
        });


        return rootView;
    }

    private void jump2User(int position, boolean isEdit){
        Intent intent = new Intent(getActivity(), AddUserActivity.class);
        intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, userListData.get(position));
        intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG, isEdit);
        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private boolean delete(final int position) {
        Delivery dialog = PostOffice.newMail(getActivity())
                .setThemeColor(R.color.colorPrimaryDark)
                .setTitle("警告")
                .setMessage("删除用户? 相关的用户电费记录也会删除 !!!!!")
                .setButtonTextColor(Dialog.BUTTON_POSITIVE, android.R.color.holo_red_light)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setDesign(Design.MATERIAL_LIGHT)
                .setButton(Dialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final User user = userListData.get(position);
                        try {
                            //删除用户和相关的一系列电费人员信息

                            //TODO 这里考虑要用UI线程同步的方法
                            //delete user
                            db.delete(user);
                            //delete relational charge data
                            db.deleteAll(user.getChargeForeign().getAllFromDb());

                            dialog.dismiss();

                            new SnackBar(getActivity(),
                                    "已删除用户  \"" + user.getCode() + " " + user.getName() + "\"",
                                    "确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();

                            //刷新信息
//                            refreshData();
                            userListData.remove(position);
                            userAdapter.notifyDataSetChanged();
                        } catch (DbException e) {
                            e.printStackTrace();
                            //TODO 失败信息保存
                            new SnackBar(getActivity(),
                                    "用户删除失败",
                                    "确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                        }

                    }
                })
                .setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).build();

        if (dialog != null) {
            dialog.show(getFragmentManager());
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("查询的用户名或代码");
        /**
         * 默认情况下, search widget是"iconified“的，只是用一个图标 来表示它(一个放大镜),
         * 当用户按下它的时候才显示search box . 你可以调用setIconifiedByDefault(false)让search
         * box默认都被显示。 你也可以调用setIconified()让它以iconified“的形式显示。
         */
        searchView.setIconifiedByDefault(true);
        /**
         * 默认情况下是没提交搜索的按钮，所以用户必须在键盘上按下"enter"键来提交搜索.你可以同过setSubmitButtonEnabled(
         * true)来添加一个提交按钮（"submit" button)
         * 设置true后，右边会出现一个箭头按钮。如果用户没有输入，就不会触发提交（submit）事件
         */
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshData(query);
                userAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
                    refreshData(newText);
                    userAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
