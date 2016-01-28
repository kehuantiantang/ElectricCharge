package com.yj.eleccharge.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yj.eleccharge.R;
import com.yj.eleccharge.entity.User;

import java.util.List;

/**
 * Created by 游捷 on 2015/8/23.
 * UserAdapter
 */
public class UserAdapter extends BaseAdapter {

    private List<User> userListData;

    private LayoutInflater layoutInflater;

    public UserAdapter(Context context, List<User> userListData) {
        this.userListData = userListData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userListData.size();
    }

    @Override
    public User getItem(int position) {
        return userListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = userListData.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_user, null);
        }

        TextView codeTextView = (TextView) convertView.findViewById(R.id.user_item_code);
        codeTextView.setText(user.getCode());

        TextView nameTextView = (TextView) convertView.findViewById(R.id.user_item_name);
        nameTextView.setText(user.getName());

        TextView groupTextView = (TextView) convertView.findViewById(R.id.user_item_group);
        groupTextView.setText(user.groupForeign.getName());

        if (user.getPhone() == null || user.getPhone().equals("")) {
            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.user_item_phoneGroup);
            linearLayout.setVisibility(LinearLayout.GONE);
        } else {
            TextView phoneTextView = (TextView) convertView.findViewById(R.id.user_item_phone);
            phoneTextView.setText(user.getPhone());
        }

        return convertView;
    }
}