package com.yj.eleccharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonRectangle;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.ListStyle;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.R;
import com.yj.eleccharge.entity.Group;
import com.yj.eleccharge.entity.User;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_add_user)
public class AddUserActivity extends AppCompatActivity {

    @ViewInject(R.id.addUser_name)
    private MaterialEditText nameEditText;
    @ViewInject(R.id.addUser_code)
    private MaterialEditText codeEditText;
    @ViewInject(R.id.addUser_birthday)
    private MaterialEditText birthdayEditText;
    @ViewInject(R.id.addUser_eMail)
    private MaterialEditText eMailditText;
    @ViewInject(R.id.addUser_location)
    private MaterialEditText locationEditText;
    @ViewInject(R.id.addUser_phone)
    private MaterialEditText phoneEditText;
    @ViewInject(R.id.addUser_remark)
    private MaterialEditText remarkEditText;

    @ViewInject(R.id.addUser_group)
    private MaterialEditText groupEditText;

    @ViewInject(R.id.confirm_buttonRectangle)
    private ButtonRectangle confirmButton;
    @ViewInject(R.id.cancel_buttonRectangle)
    private ButtonRectangle cancelButton;

    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        isEdit = getIntent().getBooleanExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG, false);
        User deliverUser = getIntent().getParcelableExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO);
        if(deliverUser != null){
            initView(deliverUser, isEdit);
        }

        //设置ActionBar的返回按钮
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        groupEditText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 如果这里没有群组信息，使用Dialog添加群组
                DbUtils db = DbTools.getInstance(AddUserActivity.this);


                try {
                    List<Group> groups = db.findAll(Group.class);
                    if (groups == null) {
                        groups = new ArrayList<>();
                    }
                    groups.add(new Group("添加新群组"));


                    ArrayAdapter<Group> adapter = new ArrayAdapter<>(AddUserActivity.this, com.r0adkll.postoffice.R.layout.simple_listitem_mtrl_light, groups);

                    final Delivery dialog = PostOffice.newMail(AddUserActivity.this)
                            .setTitle("群组选择")
                            .setDesign(Design.MATERIAL_LIGHT)
                            .setCanceledOnTouchOutside(true)
                            .setStyle(new ListStyle.Builder(AddUserActivity.this)
                                    .setDrawSelectorOnTop(true)
                                    .setDividerHeight(1)
                                    .setOnItemAcceptedListener(new ListStyle.OnItemAcceptedListener<Group>() {
                                        @Override
                                        public void onItemAccepted(Group item, int position) {
                                            if (item.getName().equals("添加新群组")) {
                                                //添加群组后，将信息通过Bundle回传
                                                Intent intent = new Intent(AddUserActivity.this, AddGroupActivity.class);
                                                startActivityForResult(intent, 0);
                                            } else {
                                                groupEditText.setText(item.getName());
                                            }
                                        }
                                    })
                                    .build(adapter)).build();

                    if (dialog != null)
                        dialog.show(getFragmentManager(), "添加群组");

                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        });

        confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int nameLength = nameEditText.getText().length();
                int codeLength = codeEditText.getText().length();
                int groupLength = groupEditText.getText().length();

                boolean flag = true;

                if (codeLength == 0) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(codeEditText);
                    flag = false;
                }

                if (nameLength == 0 || nameLength > nameEditText.getMaxCharacters()) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(nameEditText);
                    flag = false;
                }

                if (groupLength == 0 || groupLength > groupEditText.getMaxCharacters()) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(groupEditText);
                    flag = false;
                }

                if (flag == true) {
                    if (AddUserActivity.this.dataExecute()) {
                        Toast.makeText(AddUserActivity.this, "增改完成", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddUserActivity.this, "增改失败", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //类似点击返回按钮
//                onBackPressed();
                finish();
            }
        });
    }

    private void edit(){
        nameEditText.setEnabled(true);
        codeEditText.setEnabled(true);
        birthdayEditText.setEnabled(true);

        groupEditText.setClickable(true);

        phoneEditText.setEnabled(true);
        locationEditText.setEnabled(true);
        eMailditText.setEnabled(true);
        remarkEditText.setEnabled(true);

        nameEditText.setFocusable(true);
        nameEditText.setFocusableInTouchMode(true);
        nameEditText.requestFocus();
    }

    private void initView(User deliverUser, boolean isEdit){
        nameEditText.setText(deliverUser.getName());
        nameEditText.setEnabled(isEdit);

        codeEditText.setText(deliverUser.getCode());
        codeEditText.setEnabled(isEdit);

        birthdayEditText.setText(deliverUser.getBirthday());
        birthdayEditText.setEnabled(isEdit);

        groupEditText.setText(deliverUser.groupForeign.getName());
        groupEditText.setEnabled(isEdit);
        groupEditText.setClickable(isEdit);

        phoneEditText.setText(deliverUser.getPhone());
        phoneEditText.setEnabled(isEdit);

        locationEditText.setText(deliverUser.getLocation());
        locationEditText.setEnabled(isEdit);

        eMailditText.setText(deliverUser.getEmail());
        eMailditText.setEnabled(isEdit);

        remarkEditText.setText(deliverUser.getRemark());
        remarkEditText.setEnabled(isEdit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //resultCode为回传的标记，我在B中回传的是RESULT_OK
        switch (resultCode) {
            case AppConfig.ACTIVITY_RESULT_OK:
                String name = data.getExtras().getString("name");//str即为回传的值
                this.groupEditText.setText(name);
                break;
            default:
                break;
        }
    }

    /**
     * 数据存储操作
     */
    private boolean dataExecute() {
        DbUtils db = DbTools.getInstance(this);

        User user = new User();
        user.setCode(codeEditText.getText().toString());
        user.setName(nameEditText.getText().toString());
        user.setRemark(remarkEditText.getText().toString());
        user.setEmail(eMailditText.getText().toString());
        user.setPhone(phoneEditText.getText().toString());
        user.setLocation(locationEditText.getText().toString());
        user.setBirthday(birthdayEditText.getText().toString());

        try {
            //查找选择的群组外键
            Group group = db.findFirst(Selector.from(Group.class).where("name", "=", groupEditText.getText().toString()));
            //设置外键
            user.groupForeign = group;

            db.saveBindingId(user);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
        MenuItem item = menu.findItem(R.id.action_edit);
        if (isEdit == true) {
            item.setVisible(false);
        }
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
            case R.id.action_edit:
                isEdit = true;
                edit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
