package com.yj.eleccharge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gc.materialdesign.views.ButtonRectangle;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.R;
import com.yj.eleccharge.entity.Group;
import com.yj.eleccharge.entity.Price;

/**
 * 添加群组信息
 */
@ContentView(R.layout.activity_add_group)
public class AddGroupActivity extends AppCompatActivity {

    //EditText
    @ViewInject(R.id.addGroup_name)
    private MaterialEditText nameEditText;
    @ViewInject(R.id.addGroup_price)
    private MaterialEditText priceEditText;
    @ViewInject(R.id.addGroup_totalNum)
    private MaterialEditText totalNumEditText;
    @ViewInject(R.id.addGroup_descrip)
    private MaterialEditText descripEditText;
    @ViewInject(R.id.addGroup_remark)
    private MaterialEditText remarkEditText;

    //Button
    @ViewInject(R.id.cancel_buttonRectangle)
    private ButtonRectangle cancelButton;
    //
    @ViewInject(R.id.confirm_buttonRectangle)
    private ButtonRectangle comfirmButton;

    /**
     * 是否是编辑页面
     */
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        isEdit = getIntent().getBooleanExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG, false);
        Group deliverGroup = getIntent().getParcelableExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO);
        if(deliverGroup != null){
            initView(deliverGroup, isEdit);
        }

        //Action Home Back
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        comfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nameLength = nameEditText.getText().length();
                int priceLength = priceEditText.getText().length();

                boolean flag = true;

                if (nameLength == 0 || nameLength > nameEditText.getMaxCharacters()) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(nameEditText);
                    flag = false;
                }


                if (priceLength == 0) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(priceEditText);
                    flag = false;
                }

                if (flag == true) {
                    if (AddGroupActivity.this.dataExecute()) {
                        Toast.makeText(AddGroupActivity.this, "增改完成", Toast.LENGTH_SHORT).show();

                        //将群组的名字信息回传
                        Intent intent = new Intent();
                        intent.putExtra("name", nameEditText.getText().toString());
                        setResult(AppConfig.ACTIVITY_RESULT_OK, intent);
                        finish();
                    } else {
                        //TODO 数据记录
                        Toast.makeText(AddGroupActivity.this, "增改失败请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
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
        priceEditText.setEnabled(true);
        totalNumEditText.setEnabled(true);
        descripEditText.setEnabled(true);
        remarkEditText.setEnabled(true);

        nameEditText.setFocusable(true);
        nameEditText.setFocusableInTouchMode(true);
        nameEditText.requestFocus();
    }

    /**
     * 初始化view
     *
     * @param deliverGroup
     */
    private void initView(Group deliverGroup, boolean isEdit) {
        nameEditText.setText(deliverGroup.getName());
        nameEditText.setEnabled(isEdit);

        priceEditText.setText(deliverGroup.price + "");
        priceEditText.setEnabled(isEdit);

        totalNumEditText.setText(deliverGroup.getTotalNum() + "");
        totalNumEditText.setEnabled(isEdit);

        descripEditText.setText(deliverGroup.getDecrip());
        descripEditText.setEnabled(isEdit);

        remarkEditText.setText(deliverGroup.getRemark());
        remarkEditText.setEnabled(isEdit);

    }

    /**
     * 对EditText中输入的数据进行处理
     */
    private boolean dataExecute() {
        //TODO 进行数据处理
        DbUtils db = DbTools.getInstance(getApplication());
        Group group = new Group();
        group.setName(this.nameEditText.getText().toString());
        group.setTotalNum(Integer.valueOf(this.totalNumEditText.getText().toString()));
        group.setRemark(this.remarkEditText.getText().toString());
        group.setDecrip(this.descripEditText.getText().toString());

        Price price = new Price(Double.valueOf(this.priceEditText.getText().toString()));
        price.groupForeign = group;

        try {
            db.saveBindingId(group);
            db.saveBindingId(price);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_group, menu);
        MenuItem editMenu = menu.findItem(R.id.action_edit);
        if (isEdit == true) {
            editMenu.setVisible(false);
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
                edit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}