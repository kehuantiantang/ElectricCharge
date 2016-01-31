package com.yj.eleccharge.fragment;


import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.Dialog;
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
import com.yj.eleccharge.R;
import com.yj.eleccharge.activity.AddGroupActivity;
import com.yj.eleccharge.activity.UsersListActivity;
import com.yj.eleccharge.entity.Charge;
import com.yj.eleccharge.entity.Group;
import com.yj.eleccharge.entity.User;
import com.yj.eleccharge.tools.DbTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 游捷 on 2015/8/19.
 * 滑动ListView的fragment创建
 */
public class GroupItemFragment extends Fragment {

    private GroupAdapter groupAdapter;

    private AnimationAdapter animAdapter;

    private List<Group> groupListData;

    private SwipeMenuCreator creator;

    private DbUtils db;

    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //这里设置了才会出现自定义的menu和ActionBar
        setHasOptionsMenu(true);

        refreshData(null);

        //通过数据创建adapter
        groupAdapter = new GroupAdapter(getActivity().getApplicationContext(), groupListData);

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
                //TODO 调到群组内部信息去
                Intent intent = new Intent(getActivity(), UsersListActivity.class);
                intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, groupListData.get(position).getId());
                startActivityForResult(intent, 0);
            }
        });


        //ListView 的animation
        animAdapter = new SwingLeftInAnimationAdapter(groupAdapter);

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
                        // info
                        jump2Group(position, false);
                        break;
                    case 1:
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
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private boolean delete(final int position) {
        Delivery dialog = PostOffice.newMail(getActivity())
                .setThemeColor(R.color.colorPrimaryDark)
                .setTitle("警告")
                .setMessage("删除群组? 相关的用户和电费记录也会删除 !!!!!")
                .setButtonTextColor(Dialog.BUTTON_POSITIVE, android.R.color.holo_red_light)
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setDesign(Design.MATERIAL_LIGHT)
                .setButton(Dialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Group group = groupListData.get(position);
                        try {
                            //删除群组及与群组人员的一系列电费人员信息
                            //TODO 这里考虑要用UI线程同步的方法
                            db.delete(group);
                            db.delete(group.getPriceForeign().getFirstFromDb());
                            List<User> deleteUsers = group.getUserForeign().getAllFromDb();
                            db.deleteAll(deleteUsers);
                            for (User user : deleteUsers) {
                                List<Charge> deleteCharges = user.getChargeForeign().getAllFromDb();
                                db.deleteAll(deleteCharges);
                            }

                            dialog.dismiss();

                            new SnackBar(getActivity(),
                                    "已删除群组  \"" + group.getName() + "\"",
                                    "确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();

                            //刷新信息
//                            refreshData();
                            groupListData.remove(position);
                            groupAdapter.notifyDataSetChanged();
                        } catch (DbException e) {
                            e.printStackTrace();
                            //TODO 失败信息保存
                            new SnackBar(getActivity(),
                                    "群组删除失败",
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

    /**
     * 跳转得到GroupActivity
     *
     * @param position
     */
    private void jump2Group(int position, boolean isEdit) {
        Intent intent = new Intent(getActivity(), AddGroupActivity.class);
        intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, groupListData.get(position));
        intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG, isEdit);
        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("请输入您要查找的群组名");
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
//        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshData(query);
                animAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
                    refreshData(newText);
                    animAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * 更新数据源
     *
     * @param keyword
     */
    private void refreshData(String keyword) {
        if (this.db == null) {
            this.db = DbTools.getInstance(getActivity());
        }
        List<Group> newList = null;

        //数据库获取数据
        try {
            if (keyword == null || keyword.equals("")) {
                newList = db.findAll(Selector.from(Group.class).orderBy("name"));

            } else {
                newList = db.findAll(Selector.from(Group.class).where("name", "like", "%" + keyword + "%"));
            }

            if (newList == null)
                newList = new ArrayList<>();

            for (Group group : newList) {
                if (group != null)
                    group.price = group.getPriceForeign().getFirstFromDb().getPrice();
            }


        } catch (DbException e1) {
            e1.printStackTrace();
        }

        if (groupListData == null) {
            groupListData = newList;
        } else {
            groupListData.clear();
            groupListData.addAll(newList);
        }
    }


    static class ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView usersTextView;
        LinearLayout linearLayout;
        TextView remarkTextView;
    }

    /**
     * Created by 游捷 on 2015/8/23.
     */
    private class GroupAdapter extends BaseAdapter {
        private List<Group> groupListData;

        private LayoutInflater layoutInflater;

        public GroupAdapter(Context context, List<Group> groupListData) {
            this.groupListData = groupListData;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return groupListData.size();
        }

        @Override
        public Group getItem(int position) {
            return groupListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            Group group = groupListData.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_group, null);
                viewHolder = new ViewHolder();
                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.group_item_name);
                viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.group_item_price);
                viewHolder.remarkTextView = (TextView) convertView.findViewById(R.id.group_item_remark);
                viewHolder.usersTextView = (TextView) convertView.findViewById(R.id.group_item_totalUsers);
                viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.group_item_remarkGroup);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.nameTextView.setText(group.getName());

            viewHolder.priceTextView.setText(String.valueOf(group.price));

            viewHolder.usersTextView.setText(String.valueOf(group.getTotalNum()));

            String remark = group.getRemark();
            if (remark == null || remark.equals("")) {
                viewHolder.linearLayout.setVisibility(LinearLayout.GONE);
            } else {
                viewHolder.remarkTextView.setText(group.getRemark());
            }
            return convertView;
        }
    }
}
