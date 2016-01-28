package com.yj.eleccharge.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonFlat;
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
import com.r0adkll.postoffice.styles.ListStyle;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.R;
import com.yj.eleccharge.activity.AddGroupActivity;
import com.yj.eleccharge.activity.GenerateXlsActivity;
import com.yj.eleccharge.entity.Charge;
import com.yj.eleccharge.entity.Group;
import com.yj.eleccharge.entity.Price;
import com.yj.eleccharge.entity.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 游捷 on 2015/8/19.
 * 滑动ListView的fragment创建
 */
public class ChargeItemFragment extends Fragment {

//    public static final String CONSTANT_SWIPEFRAGMENT_ARGUMENT = "ChargeItemFragment";

    private ChargeAdapter chargeAdapter;

    private List<Charge> chargeListData;

    private DbUtils db;

    /**
     * time of charge
     */
    private String selectTime;

    /**
     * group of select
     */
    private String selectGroup;

    /**
     * Group name
     */
    private String groupName;

    /**
     * the group order price
     */
    private Price price;

    /**
     * cal fill in item numbers
     */
    private int recordNum = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //这里设置了才会出现自定义的menu和ActionBar
        setHasOptionsMenu(true);

        refreshData(null);

        //通过数据创建adapter
        chargeAdapter = new ChargeAdapter(getActivity().getApplicationContext(), chargeListData);
    }

    /**
     * @param keyword group keyword
     */
    private void refreshData(String keyword) {
        recordNum = 0;
        selectGroup = keyword;

        if (db == null) {
            db = DbTools.getInstance(getActivity());
        }

        try {
            //first find select group
            Group group;
            if (keyword == null || keyword.equals("")) {
                group = db.findFirst(Group.class);
                groupName = group.getName();
                //Get price
                price = group.getPriceForeign().getFirstFromDb();
            } else {
                //Select from group button's name
                group = db.findFirst(Selector.from(Group.class).where("name", "=", keyword));
                price = group.getPriceForeign().getFirstFromDb();
            }
            List<User> users = group.getUserForeign().getAllFromDb();
            if (users == null) {
                users = new ArrayList<>();
            }

            List<Charge> newList = new ArrayList<>();
            for (User user : users) {
                Charge charge = new Charge();
                charge.name = user.getName();
                charge.code = user.getCode();
                charge.userForeign = user;
                charge.isComplete = false;

                selectTime = Charge.getNowFormatDate();
                charge.setTime(selectTime);

                newList.add(charge);
            }

            if (chargeListData == null) {
                chargeListData = newList;
            } else {
                chargeListData.clear();
                chargeListData.addAll(newList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_charge, null);

        initSwipeMenuList(rootView);

        initButton(rootView);

        return rootView;
    }

    private void initButton(View rootView) {
        ButtonFloat groupButton = (ButtonFloat) rootView.findViewById(R.id.charge_group_buttonFloat);
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Group> groups = null;
                try {
                    groups = DbTools.getInstance(getActivity()).findAll(Group.class);
                } catch (DbException e) {
                    e.printStackTrace();
                } finally {
                    if (groups == null) {
                        groups = new ArrayList<>();
                    }
                }

                ArrayAdapter<Group> adapter = new ArrayAdapter<>(getActivity(), com.r0adkll.postoffice.R.layout.simple_listitem_mtrl_light, groups);
                final Delivery dialog = PostOffice.newMail(getActivity())
                        .setTitle("群组选择")
                        .setDesign(Design.MATERIAL_LIGHT)
                        .setCanceledOnTouchOutside(true)
                        .setStyle(new ListStyle.Builder(getActivity())
                                .setDrawSelectorOnTop(true)
                                .setDividerHeight(1)
                                .setOnItemAcceptedListener(new ListStyle.OnItemAcceptedListener<Group>() {
                                    @Override
                                    public void onItemAccepted(Group item, int position) {
                                        if (item.getName().equals("添加新群组")) {
                                            //添加群组后，将信息通过Bundle回传
                                            Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                                            startActivityForResult(intent, 0);
                                        } else {
                                            groupName = item.getName();
                                            refreshData(item.getName());
                                            chargeAdapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .build(adapter)).build();


                if (dialog != null)
                    dialog.show(getFragmentManager(), "添加群组");
            }
        });
    }

    /**
     * initSwipmenuList
     *
     * @param rootView
     */
    private void initSwipeMenuList(View rootView) {
        SwipeMenuListView swipeMenuListView = (SwipeMenuListView) rootView.findViewById(R.id.charge_swipeListView);

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buildChargeDialog(position);
            }
        });

        AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(chargeAdapter);

        animAdapter.setAbsListView(swipeMenuListView);

        //设置第一次动画的时间,注意顺序一定要先设置绑定的listView
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(200);

        //为list设置动画的adapter
        swipeMenuListView.setAdapter(animAdapter);


        //设置滑动出现的方向
        swipeMenuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    /**
     * 创建一个自定义的Dialog
     *
     * @param position
     */
    private void buildChargeDialog(int position) {
        final Charge charge = chargeListData.get(position);

        //create dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_add_charge);
        dialog.show();

        //init title
        TextView title = (TextView) dialog.findViewById(R.id.addCharge_title);
        title.setText(charge.code);
        TextView subTitle = (TextView) dialog.findViewById(R.id.addCharge_subTitle);
        subTitle.setText(charge.name);

        //init lastElectEditText
        TextView lastElectEditText = (TextView) dialog.findViewById(R.id.addCharge_lastElec);
        if (charge.getLastElec() == null || "0.0".equals(charge.getLastElec())) {
            Charge lastCharge = null;
            if (db == null) {
                db = DbTools.getInstance(getActivity());
            }
            //search last month electric
            try {
                lastCharge = db.findFirst(Selector.from(Charge.class).where("userId", "=", charge.userForeign.getId()).and("time", "=", Charge.getLastFormatDate()));
            } catch (DbException e) {
                e.printStackTrace();
            }
            String tpCharge = (lastCharge == null ? "0.0" : lastCharge.getNowElec());
            charge.setLastElec(tpCharge);
            lastElectEditText.setText(tpCharge);
        } else {
            //have value set editText
            lastElectEditText.setText(charge.getLastElec());
        }

        //init nextElectEdit and get focus
        TextView nextElectEditText = (TextView) dialog.findViewById(R.id.addCharge_nextElec);
        if (charge.getNowElec() != null) {
            nextElectEditText.setText(charge.getNowElec());
        }
        //获取焦点
        nextElectEditText.setFocusable(true);
        nextElectEditText.setFocusableInTouchMode(true);
        nextElectEditText.requestFocus();
        nextElectEditText.findFocus();


        // show input keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(nextElectEditText, InputMethodManager.SHOW_FORCED);


        //Get dialg EditText
        final MaterialEditText ev1 = (MaterialEditText) dialog.findViewById(R.id.addCharge_lastElec);
        final MaterialEditText ev2 = (MaterialEditText) dialog.findViewById(R.id.addCharge_nextElec);
        final MaterialEditText ev3 = (MaterialEditText) dialog.findViewById(R.id.addCharge_remark);

        //dialog confirm button
        ButtonFlat confirm = (ButtonFlat) dialog.findViewById(R.id.addCharge_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                int ev1Length = ev1.getText().length();
                int ev2Length = ev2.getText().length();
                boolean flag = true;

                if (ev1Length == 0) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(ev1);
                    flag = false;
                }

                if (ev2Length == 0) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(ev2);
                    flag = false;
                }

                if (flag == true) {
                    //TODO 这里可以进行数据存储操作
                    final Double lastElec = Double.valueOf(ev1.getText().toString());
                    final Double nextElec = Double.valueOf(ev2.getText().toString());

                    //出现上月大于下月的情况,注意差值
                    if (nextElec >= lastElec) {
                        charge.setLastElec(lastElec + "");
                        charge.setMargin(String.valueOf(nextElec - lastElec));
                        charge.setNowElec(nextElec + "");
                        charge.setAggregate(getTotalValue(Double.valueOf(charge.getMargin())));
                        charge.setRemark(ev3.getText().toString());

                        chargeAdapter.notifyDataSetChanged();

                        //统计完成了的项目数
                        if (charge.isComplete == false) {
                            charge.isComplete = true;
                            recordNum++;
                        }
                        dialog.dismiss();
                    } else {
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(ev2);

                        //上月大于下月电费
                        new SnackBar(getActivity(),
                                "上月电费大于本月电费，确定录入？",
                                "是", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                charge.setLastElec(lastElec + "");
                                charge.setNowElec(nextElec + "");
                                charge.setMargin(String.valueOf(AppConfig.SETTING_SETTING_FULL_QUOTA - lastElec + nextElec));
                                charge.setRemark(ev3.getText().toString());
                                charge.setAggregate(getTotalValue(Double.valueOf(charge.getMargin())));
                                chargeAdapter.notifyDataSetChanged();

                                //统计完成了的项目数
                                if (charge.isComplete == false) {
                                    charge.isComplete = true;
                                    recordNum++;
                                }

                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
            }
        });

        //cancel dialog
        ButtonFlat cancel = (ButtonFlat) dialog.findViewById(R.id.addCharge_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * the method to get total value(elec * price) which can expand to level electricity
     *
     * @return
     */
    private String getTotalValue(double elec) {
        return elec * price.getPrice() + "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_confirm:
                //success jump to generateXlsActivity
                if (dataExecute()) {
                    //TODO 一个寻求用户是否生成xls的弹窗,这里检测一下是否有设置的默认选项，有就直接生成数据
                    Intent intent = new Intent(getActivity(), GenerateXlsActivity.class);

                    //set select time and group
                    intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, (Serializable) chargeListData);
                    intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG, groupName);
                    getActivity().startActivityForResult(intent, 0);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean dataExecute() {
        if (recordNum == chargeListData.size()) {
            //TODO thread task
            if (db == null) {
                db = DbTools.getInstance(getActivity());
            }
            //save all the charge data
            if (chargeListData != null) {
                try {
                    db.saveBindingIdAll(chargeListData);
                    return true;
                } catch (DbException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                throw new IllegalArgumentException("chargeListData is null");
            }
        } else {
            Delivery dialog = PostOffice.newMail(getActivity()).setMessage("对不起，您的数据未填写完整!").setDesign(Design.MATERIAL_LIGHT).build();
            if (dialog != null) {
                dialog.show(getFragmentManager());
            }
            return false;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_add_charge, menu);
//       TODO 添加一个确认按钮，将数据保存，生成xls的确认
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView codeTextView;
        TextView lastElecTextView;
        TextView nowTextView;
    }

    private class ChargeAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Charge> chargeListData;

        public ChargeAdapter(Context context, List<Charge> chargeListData) {
            this.layoutInflater = LayoutInflater.from(context);
            this.chargeListData = chargeListData;
        }

        @Override
        public int getCount() {
            return chargeListData.size();
        }

        @Override
        public Charge getItem(int position) {
            return chargeListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            Charge charge = chargeListData.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_charge, null);
                viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.charge_item_name);
                viewHolder.codeTextView = (TextView) convertView.findViewById(R.id.charge_item_code);
                viewHolder.lastElecTextView = (TextView) convertView.findViewById(R.id.charge_item_lastElec);
                viewHolder.nowTextView = (TextView) convertView.findViewById(R.id.charge_item_nowElec);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.nameTextView.setText(charge.name);

            viewHolder.codeTextView.setText(charge.code);

            viewHolder.lastElecTextView.setText(String.valueOf(charge.getLastElec() == null ? "" : charge.getLastElec()));

            viewHolder.nowTextView.setText(String.valueOf(charge.getNowElec() == null ? "" : charge.getNowElec()));

            return convertView;
        }
    }
}
