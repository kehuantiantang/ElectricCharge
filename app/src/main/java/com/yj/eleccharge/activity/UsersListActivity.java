package com.yj.eleccharge.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.ListStyle;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.R;
import com.yj.eleccharge.entity.User;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.util.UserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UsersListActivity extends AppCompatActivity {

    private UserAdapter userAdapter;

    private AnimationAdapter animAdapter;

    private List<User> userListData;

    private SwipeMenuCreator creator;

    private DbUtils db;

    private Integer groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);


        //Action Home Back
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        groupId = getIntent().getIntExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, -1);
        refreshData(null);

        //从设置bundle中获取数据，如果没有则默认加载
        //swipeListData = (ArrayList) getArguments().get(CONSTANT_SWIPEFRAGMENT_ARGUMENT);

        //通过数据创建adapter
        userAdapter = new UserAdapter(getApplicationContext(), userListData);


        // step 1. 创建滑动出的按钮
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "info" item
                SwipeMenuItem infoItem = new SwipeMenuItem(
                        getApplicationContext());
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
                        getApplicationContext());

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

        initUI();
        initButton();
    }

    private void initUI() {
        //************************SwipeMenuList*************************************
        SwipeMenuListView swipeMenuListView = (SwipeMenuListView) findViewById(R.id.userList_swipeListView);

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
    }


    /**
     * float button settting
     */
    private void initButton() {
        ButtonFloat xlsButton = (ButtonFloat) findViewById(R.id.userList_xls);
        xlsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sql = "select distinct time from charge where userId in (select id from user where groupId = '" + groupId + "')";
                if (db == null) {
                    db = DbTools.getInstance(getApplicationContext());
                }
                List<DbModel> times = null;
                try {
                    times = db.findDbModelAll(new SqlInfo(sql));
                } catch (DbException e) {
                    e.printStackTrace();
                }

                List<String> timeStrings = new ArrayList<>();
                if (times != null) {
                    for (DbModel dbModel : times) {
                        String time = dbModel.getString("time");
                        timeStrings.add(time);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), com.r0adkll.postoffice.R.layout.simple_listitem_mtrl_light, timeStrings);
                final Delivery dialog = PostOffice.newMail(getApplicationContext())
                        .setTitle("选择导出xls时间")
                        .setDesign(Design.MATERIAL_LIGHT)
                        .setCanceledOnTouchOutside(true)
                        .setStyle(new ListStyle.Builder(getApplicationContext())
                                .setDrawSelectorOnTop(true)
                                .setDividerHeight(1)
                                .setOnItemAcceptedListener(new ListStyle.OnItemAcceptedListener<String>() {
                                    @Override
                                    public void onItemAccepted(String item, int position) {

                                        //TODO 导出Html
                                    }
                                })
                                .build(adapter)).build();


                if (dialog != null)
                    dialog.show(getFragmentManager(), "添加群组");
            }
        });
    }

    /**
     * refresh data from SQLLite
     *
     * @param keyword search by keword
     */
    private void refreshData(String keyword) {
        if (db == null) {
            db = DbTools.getInstance(getApplicationContext());
        }
        List<User> newList;
        try {
            if (keyword != null && !keyword.equals("")) {
                //if all is numeric
                Pattern pattern = Pattern.compile("[0-9]*");
                if (pattern.matcher(keyword).matches()) {
                    //select by user code
                    newList = db.findAll(Selector.from(User.class).where("code", "like", "%" + keyword + "%").and("groupId", "=", groupId));
                } else {
                    //select by group name
                    newList = db.findAll(Selector.from(User.class).where("name", "like", "%" + keyword + "%").and("groupId", "=", groupId));
                }
            } else {
                newList = db.findAll(Selector.from(User.class).where("groupId", "=", groupId));
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


    private void jump2User(int position, boolean isEdit) {
        Intent intent = new Intent(this, AddUserActivity.class);
        intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, userListData.get(position));
        intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG, isEdit);
        startActivity(intent);
    }

    private boolean delete(final int position) {
        Delivery dialog = PostOffice.newMail(getApplicationContext())
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

                            new SnackBar(UsersListActivity.this,
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
                            new SnackBar(UsersListActivity.this,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
