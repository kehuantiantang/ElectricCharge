package com.yj.eleccharge.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
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
import com.r0adkll.postoffice.styles.ProgressStyle;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.R;
import com.yj.eleccharge.entity.Charge;
import com.yj.eleccharge.entity.Group;
import com.yj.eleccharge.entity.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TimeDetailActivity extends AppCompatActivity {


    private ContentListAdapter contentListAdapter;

    private AnimationAdapter animAdapter;

    private List<Charge> contentListData;

    private SwipeMenuCreator creator;

    private String title;

    private DbUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_detail);

        Intent intent = getIntent();
//       传递过来的标题
       title = intent.getStringExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO);

        //Action Home Back
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle(title);

        this.contentListData = new ArrayList<>();
        this.contentListAdapter = new ContentListAdapter(this, contentListData);
        DetailAsyncTask asyncTask = new DetailAsyncTask(contentListData, contentListAdapter, title, null);
        asyncTask.execute();

        //通过数据创建adapter
//       contentListAdapter = new ContentListAdapter(this, contentListData);


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
            }
        };

        //************************SwipeMenuList*************************************
        SwipeMenuListView swipeMenuListView = (SwipeMenuListView) findViewById(R.id.time_content_swipeListView);

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buildDialog(position);
            }
        });

        //ListView 的animation
        animAdapter = new SwingLeftInAnimationAdapter(contentListAdapter);

        animAdapter.setAbsListView(swipeMenuListView);

        //设置第一次动画的时间,注意顺序一定要先设置绑定的listView
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(200);


        //为list设置动画的adapter
        swipeMenuListView.setAdapter(animAdapter);


        //设置Menu的按钮
        swipeMenuListView.setMenuCreator(creator);

        //设置滑动点击按钮的监听事件
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Toast.makeText(getApplicationContext(), "info", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        initButton();

    }

    private void initButton() {
        ButtonFloat groupButton = (ButtonFloat)findViewById(R.id.time_content_buttonFloat);
        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Group> groups = null;
                try {
                    groups = DbTools.getInstance(getApplicationContext()).findAll(Group.class);
                } catch (DbException e) {
                    e.printStackTrace();
                } finally {
                    if (groups == null) {
                        groups = new ArrayList<>();
                    }
                }

                ArrayAdapter<Group> adapter = new ArrayAdapter<>(getApplicationContext(), com.r0adkll.postoffice.R.layout.simple_listitem_mtrl_light, groups);
                final Delivery dialog = PostOffice.newMail(TimeDetailActivity.this)
                        .setTitle("群组选择")
                        .setDesign(Design.MATERIAL_LIGHT)
                        .setCanceledOnTouchOutside(true)
                        .setStyle(new ListStyle.Builder(TimeDetailActivity.this)
                                .setDrawSelectorOnTop(true)
                                .setDividerHeight(1)
                                .setOnItemAcceptedListener(new ListStyle.OnItemAcceptedListener<Group>() {
                                    @Override
                                    public void onItemAccepted(Group item, int position) {
                                        DetailAsyncTask asyncTask = new DetailAsyncTask(contentListData, contentListAdapter, title, item.getId()+"");
                                        asyncTask.execute();
                                    }

                                }).build(adapter)).build();


                if (dialog != null)
                    dialog.show(getFragmentManager(), "添加群组");
            }
        });
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
     * 创建一个自定义的Dialog
     *
     * @param position
     */
    private void buildDialog(final int position) {

        //创建dialog
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);

        dialog.setContentView(R.layout.dialog_time_detail);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        if (db == null) {
            db = DbTools.getInstance(this);
        }
        Charge charge = contentListData.get(position);

        //寻找内部布局对他进行设置
        TextView title = (TextView) dialog.findViewById(R.id.time_detail_title);
        title.setText(contentListData.get(position).userForeign.getCode());
        final TextView subTitle = (TextView) dialog.findViewById(R.id.time_detail_subTitle);
        subTitle.setText(contentListData.get(position).userForeign.getName());

        final String orignNextElec = "0.0";

        final MaterialEditText priceEditText = (MaterialEditText) dialog.findViewById(R.id.time_detail_price);
        //select price from group
        try {
            priceEditText.setText(charge.userForeign.groupForeign.getPriceForeign().getFirstFromDb().getPrice() + "");
        } catch (DbException e) {
            e.printStackTrace();
        }


        final MaterialEditText lastElecEditText = (MaterialEditText) dialog.findViewById(R.id.time_detail_lastElec);
        lastElecEditText.setText(charge.getLastElec());
        final MaterialEditText nowElecEditText = (MaterialEditText) dialog.findViewById(R.id.time_detail_nextElec);
        nowElecEditText.setText(charge.getNowElec());
        final MaterialEditText totalCountEditText = (MaterialEditText) dialog.findViewById(R.id.time_detail_totalCount);
        totalCountEditText.setText(charge.getMargin() + "");
        final MaterialEditText totalValuesEditText = (MaterialEditText) dialog.findViewById(R.id.time_detail_totalValues);
        totalValuesEditText.setText(charge.getAggregate() + "");

        final RelativeLayout layout = (RelativeLayout) dialog.findViewById(R.id.time_detail_layout);

        ButtonFlat confirm = (ButtonFlat) dialog.findViewById(R.id.time_detail_confirm);
        ButtonFlat cancel = (ButtonFlat) dialog.findViewById(R.id.time_detail_cancel);
        ButtonFlat modify = (ButtonFlat) dialog.findViewById(R.id.time_detail_modify);

        // 触摸屏幕取消EditText的焦点光标
        layout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // rel.setFocusable(true);
                // 如果xml文件里面没设置，就需要在这里设置
                // rel.setFocusableInTouchMode(true);
                layout.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
                return false;
            }
        });

        nowElecEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String nextElecString = nowElecEditText.getText().toString();

                if (hasFocus == false && nextElecString.length() != 0 && !nowElecEditText.equals(orignNextElec)) {
                    double nextElec = Double.valueOf(nextElecString);

                    double lastElec = Double.valueOf(lastElecEditText.getText().toString());

                    //TODO level price
                    double price = Double.valueOf(priceEditText.getText().toString());

                    double subtract;

                    if (nextElec < lastElec) {
                        subtract = AppConfig.SETTING_SETTING_FULL_QUOTA - lastElec + nextElec;
                    } else if (nextElec > lastElec) {
                        subtract = nextElec - lastElec;
                    } else {
                        subtract = 0.0;
                    }
                    totalCountEditText.setText(String.valueOf(subtract));
                    //TODO level electricity
                    totalValuesEditText.setText(String.valueOf(subtract * price));
                }
            }
        });


        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowElecEditText.setTextColor(getResources().getColor(R.color.colorAccent));
                nowElecEditText.setEnabled(true);
                nowElecEditText.setFocusable(true);
                nowElecEditText.setFocusableInTouchMode(true);
                nowElecEditText.requestFocus();
                nowElecEditText.findFocus();

                // 显示键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(nowElecEditText, InputMethodManager.SHOW_FORCED);
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cal the price and total price
                String elec = nowElecEditText.getText().toString();
                if (elec.length() != 0 && !elec.equals("")) {
                    Charge charge = contentListData.get(position);
                    charge.setNowElec(elec);

                    double lastElec = Double.valueOf(lastElecEditText.getText().toString());
                    double nextElec = Double.valueOf(elec);

                    //TODO level price
                    double price = Double.valueOf(priceEditText.getText().toString());

                    double subtract;

                    if (nextElec < lastElec) {
                        subtract = AppConfig.SETTING_SETTING_FULL_QUOTA - lastElec + nextElec;
                    } else if (nextElec > lastElec) {
                        subtract = nextElec - lastElec;
                    } else {
                        subtract = 0.0;
                    }
                    charge.setAggregate(String.valueOf(subtract * price));

                    contentListAdapter.notifyDataSetChanged();

                    // save charge data
                    try {
                        db.saveBindingId(charge);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_xls:
                Intent intent = new Intent(this, GenerateXlsActivity.class);
                //set select time and group
                intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, (Serializable)contentListData);
                intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG, "群组 " + title);
                startActivityForResult(intent, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * content的内容
     */
    private class ContentListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        //TODO 数据
        private List<Charge> mDataList;

        public ContentListAdapter(Context context, List<Charge> mDataList) {
            this.layoutInflater = LayoutInflater.from(context);
            this.mDataList = mDataList;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Charge getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Charge timeContent = mDataList.get(position);

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_time_content, null);
            }
            TextView codeTextView = (TextView) convertView.findViewById(R.id.time_item_content_code);
            codeTextView.setText(timeContent.userForeign.getCode());

            TextView nameTextView = (TextView) convertView.findViewById(R.id.time_item_content_name);
            nameTextView.setText(timeContent.userForeign.getName());

            TextView elecTextView = (TextView) convertView.findViewById(R.id.time_item_content_elec);
            elecTextView.setText(timeContent.getMargin() + "");

            TextView valuesTextView = (TextView) convertView.findViewById(R.id.time_item_content_values);
            valuesTextView.setText(timeContent.getAggregate() + "");

            TextView groupTextView = (TextView) convertView.findViewById(R.id.time_item_content_group);
            groupTextView.setText(timeContent.userForeign.groupForeign.getName());
            return convertView;
        }
    }

    /**
     * User AsyncTask to load UI data
     */
    private class DetailAsyncTask extends AsyncTask<Object, Object, List<Charge>> {

        private ContentListAdapter contentListAdapter;
        private String time;
        private String group;
        private List<Charge> refreshData;
        //progressBar UI
        private Delivery delivery;

        public DetailAsyncTask(List<Charge> contentData, ContentListAdapter contentListAdapter, String time, String group) {
            this.contentListAdapter = contentListAdapter;
            this.time = time;
            this.group = group;
            this.refreshData = contentData;
        }

        @Override
        protected void onPreExecute() {
            delivery = PostOffice.newMail(TimeDetailActivity.this)
                    .setThemeColor(getResources().getColor(R.color.colorPrimaryDark))
                    .setDesign(Design.MATERIAL_LIGHT)
                    .setStyle(new ProgressStyle.Builder(TimeDetailActivity.this)
                            .setProgressStyle(ProgressStyle.NORMAL)
                            .setProgressMessage("正在读取数据.....")
                            .build())
                    .setCancelable(true)
                    .build();
            if (delivery != null) {
                delivery.show(TimeDetailActivity.this.getFragmentManager());
            }
        }

        @Override
        protected void onPostExecute(List<Charge> charges) {
            //Hide the progressBar and refresh data
            delivery.dismiss();
            if (contentListAdapter == null) {
                refreshData = charges;
                contentListAdapter = new ContentListAdapter(TimeDetailActivity.this, charges);
            }
            contentListAdapter.notifyDataSetChanged();
        }


        @Override
        protected List<Charge> doInBackground(Object... params) {

            //Get all time detail data from DB
            if (db == null) {
                db = DbTools.getInstance(TimeDetailActivity.this);
            }
            //Get charge data
            List<Charge> newCharges = null;
            try {
                if (time == null || "".equals(time)) {
                    throw new IllegalArgumentException("The keywords is illegal argument !");
                } else {
                    if(group == null) {
                        newCharges = db.findAll(Selector.from(Charge.class).where("time", "=", time));
                    }else{
                        String sql = "select * from charge where time = '" + time + "' and userId in (select id from user where groupId = '"+  group + "')";
                        List<DbModel> lists = db.findDbModelAll(new SqlInfo(sql));

                        if(lists != null){
                            newCharges = new ArrayList<>();
                            for(DbModel dbModel : lists){
                                Charge charge = new Charge();
                                User user = db.findById(User.class, dbModel.getString("userId"));
                                charge.userForeign = user;
                                charge.setAggregate(dbModel.getString("total"));
                                charge.setTime(title);
                                charge.setId(dbModel.getInt("id"));
                                charge.setNowElec(dbModel.getString("nextElec"));
                                charge.setLastElec(dbModel.getString("lastElec"));
                                charge.setRemark(dbModel.getString("remark"));
                                charge.setMargin(dbModel.getString("value"));
                                newCharges.add(charge);
                            }
                        }
                    }
                    if (newCharges == null) {
                        newCharges = new ArrayList<>();
                    }
                }

                if (refreshData == null) {
                    refreshData = newCharges;
                } else {

                    refreshData.clear();
                    refreshData.addAll(newCharges);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            return refreshData;
        }
    }

}
