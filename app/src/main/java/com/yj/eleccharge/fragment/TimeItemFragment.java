package com.yj.eleccharge.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ButtonFloat;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.R;
import com.yj.eleccharge.activity.TimeDetailActivity;
import com.yj.eleccharge.entity.Charge;
import com.yj.eleccharge.util.TimeTitle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by 游捷 on 2015/8/19.
 * 滑动ListView的fragment创建
 */
public class TimeItemFragment extends Fragment {

//    public static final String CONSTANT_SWIPEFRAGMENT_ARGUMENT = "TimeItemFragment";

    private TitleAdapter timeAdapter;

    private List<TimeTitle> titleListData;

    private DbUtils db;

    public TimeItemFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //这里设置了才会出现自定义的menu和ActionBar
        setHasOptionsMenu(true);

        refreshData(null);

        //从设置bundle中获取数据，如果没有则默认加载
        //titleListData = (ArrayList) getArguments().get(CONSTANT_SWIPEFRAGMENT_ARGUMENT);

        //通过数据创建adapter
        timeAdapter = new TitleAdapter(getActivity().getApplicationContext(), titleListData);
    }


    private void refreshData(String keyword) {
        if (db == null) {
            db = DbTools.getInstance(getActivity());
        }
        List<Charge> charges;
        try {
            if (keyword == null || "".equals(keyword)) {
                charges = db.findAll(Selector.from(Charge.class)
                        .orderBy("time"));
            } else {
                charges = db.findAll(Selector.from(Charge.class).where("time", "=", keyword).orderBy("time"));
            }
            if (charges == null) {
                charges = new ArrayList<>();
            }
            //Get all charge data and group by time from charge data
            Map<String, TimeTitle> map = new Hashtable<>();
            for (Charge charge : charges) {
                TimeTitle valueTimeTitle;
                if (map.containsKey(charge.getTime())) {
                    valueTimeTitle = map.get(charge.getTime());
                    String margin = charge.getMargin();
                    String totalPrice = charge.getAggregate();
                    int num = valueTimeTitle.getUserNum();

                    valueTimeTitle.setTotalCount(valueTimeTitle.getTotalCount() + Double.valueOf(margin));
                    valueTimeTitle.setTotalValues(valueTimeTitle.getTotalValues() + Double.valueOf(totalPrice));
                    valueTimeTitle.setUserNum(num + 1);
                } else {
                    valueTimeTitle = new TimeTitle(charge.getTime(), 1, charge.getMargin(), charge.getAggregate());
                    map.put(charge.getTime(), valueTimeTitle);
                }
            }

            //map convert to list
            List<TimeTitle> newList = new ArrayList<>();
            for (String key : map.keySet()) {
                newList.add(map.get(key));
            }
            if (titleListData == null) {
                titleListData = newList;
            } else {
                titleListData.clear();
                titleListData.addAll(newList);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time, null);

        //************************SwipeMenuList*************************************
        SwipeMenuListView swipeMenuListView = (SwipeMenuListView) rootView.findViewById(R.id.time_swipListView);

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //*******************直接Activitgy跳转***************************************************************
                Intent intent = new Intent(getActivity(), TimeDetailActivity.class);
                intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, titleListData.get(position).getTime());
                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                //***************************************************************************************************
            }
        });


        //ListView 的animation
        AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(timeAdapter);

        animAdapter.setAbsListView(swipeMenuListView);

        //设置第一次动画的时间,注意顺序一定要先设置绑定的listView
        assert animAdapter.getViewAnimator() != null;
        animAdapter.getViewAnimator().setInitialDelayMillis(200);


        //为list设置动画的adapter
        swipeMenuListView.setAdapter(animAdapter);


        final Calendar calendar = Calendar.getInstance();

        //Float Button
        ButtonFloat addButton = (ButtonFloat) rootView.findViewById(R.id.time_timer);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //使用了DateTimePicker的类库的时间选择器
                DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                        //TODO to use asy way
                        refreshData(String.format("%04d", year) + "-" + String.format("%02d", (monthOfYear + 1)));
                        timeAdapter.notifyDataSetChanged();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        .show(getActivity().getFragmentManager(), "datePicker");
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.global, menu);
    }

    static class ViewHolder {
        TextView timeTexView;
        TextView usersTexView;
        TextView totalCountTexView;
        TextView totalValueTexView;
    }

    private class TitleAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;

        private List<TimeTitle> mDataList;

        public TitleAdapter(Context context, List<TimeTitle> mDataList) {
            this.layoutInflater = LayoutInflater.from(context);
            this.mDataList = mDataList;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public TimeTitle getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TimeTitle timeTitle = this.mDataList.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_time_title, null);

                viewHolder = new ViewHolder();
                viewHolder.timeTexView = (TextView) convertView.findViewById(R.id.time_item_title_time);
                viewHolder.usersTexView = (TextView) convertView.findViewById(R.id.time_item_title_users);
                viewHolder.totalCountTexView = (TextView) convertView.findViewById(R.id.time_item_title_totalCount);
                viewHolder.totalValueTexView = (TextView) convertView.findViewById(R.id.time_item_title_totalValue);

                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.timeTexView.setText(timeTitle.getTime());
            viewHolder.usersTexView.setText(timeTitle.getUserNum() + "");
            viewHolder.totalCountTexView.setText(timeTitle.getTotalCount() + "");
            viewHolder.totalValueTexView.setText(timeTitle.getTotalValues() + "");

            return convertView;
        }
    }

}
