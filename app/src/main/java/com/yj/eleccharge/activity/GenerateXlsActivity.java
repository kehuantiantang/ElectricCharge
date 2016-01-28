package com.yj.eleccharge.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Design;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yj.eleccharge.R;
import com.yj.eleccharge.entity.Charge;
import com.yj.eleccharge.ui.MaterialCheckBox;

import java.util.List;

@ContentView(R.layout.activity_generate_xls)
public class GenerateXlsActivity extends AppCompatActivity {

    //TODO 需要生成一个Setting的map数据，从map中取得设置

    //EditText
    @ViewInject(R.id.xls_title_editText)
    private MaterialEditText titleEditText;
    @ViewInject(R.id.xls_date_editText)
    private MaterialEditText dateEditText;
    @ViewInject(R.id.xls_remark_editText)
    private MaterialEditText remarkEditText;
    @ViewInject(R.id.xls_signature_editText)
    private MaterialEditText signEditText;

    //MaterialCheckBox
    @ViewInject(R.id.xls_code_checkBox)
    private MaterialCheckBox codeCheckBox;
    @ViewInject(R.id.xls_name_checkBox)
    private MaterialCheckBox nameCheckBox;
    @ViewInject(R.id.xls_lastElec_checkBox)
    private MaterialCheckBox lastElecCheckBox;
    @ViewInject(R.id.xls_nowElec_checkBox)
    private MaterialCheckBox nowElecCheckBox;
    @ViewInject(R.id.xls_margin_checkBox)
    private MaterialCheckBox marginCheckBox;
    @ViewInject(R.id.xls_price_checkBox)
    private MaterialCheckBox priceCheckBox;
    @ViewInject(R.id.xls_eMail_checkBox)
    private MaterialCheckBox eMailCheckBox;
    @ViewInject(R.id.xls_location_checkBox)
    private MaterialCheckBox locationCheckBox;
    @ViewInject(R.id.xls_phone_checkBox)
    private MaterialCheckBox phoneCheckBox;

    @ViewInject(R.id.xls_aggregate_checkBox)
    private MaterialCheckBox aggregateCheckBox;


    private MaterialCheckBox[] materialCheckBoxes;
    private MaterialEditText[] materialEditTexts;


    @ViewInject(R.id.confirm_buttonRectangle)
    private ButtonRectangle confirm;
    @ViewInject(R.id.cancel_buttonRectangle)
    private ButtonRectangle cancel;

    /**
     * generate xls's time
     */
    private String time;
    /**
     * generate xls's group
     */
    private String group = "";

    /**
     * whether this group setting is exist
     */
    private boolean isSettingExist;
    /**
     * Read setting
     */
    private SharedPreferences sp;

    private List<Charge> chargeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先获得
        sp = getSharedPreferences(group + "Setting", 0);

        ViewUtils.inject(this);

//        //receiver the parameter of ChargeItemFragment
//        chargeList = (List<Charge>)getIntent().getSerializableExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO);
//
//        //charge list not exist !
//        if(chargeList == null){
//            String[] values = getIntent().getStringArrayExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG);
//            this.group = values[0];
//            this.time = values[1];
//        }else{
//            this.group = getIntent().getStringExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_FLAG);
//        }


        //Action Home Back
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        /**
         * confirm click listener
         */
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSettingExist == false) {
                    PostOffice.newMail(getApplicationContext()).setTitle("警告").setDesign(Design.MATERIAL_LIGHT).setMessage("是否将当前选项设置为该群组默认选项").setButton(Dialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            savePreference();
                            dialog.dismiss();

                            //TODO 生成xls文件

                        }
                    }).setButton(Dialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            //TODO 生成xls文件
                        }
                    }).show(getFragmentManager());

                } else {
                    //TODO 生成xls文件
                    Toast.makeText(GenerateXlsActivity.this, String.valueOf(codeCheckBox.isChecked()), Toast.LENGTH_SHORT).show();
                }
            }
        });
        /**
         * confirm click listener
         */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * init checkBox and editText from preference and parameter
     */
    private void initView() {
        //save preference
        isSettingExist = sp.getBoolean("isSave", false);

        if (isSettingExist == true) {
            //EditText
            titleEditText.setText(sp.getString("title", group));
            dateEditText.setText(time);
            signEditText.setText(sp.getString("sign", ""));

            //CheckBox
            codeCheckBox.setChecked(sp.getBoolean("code", true));
            nameCheckBox.setChecked(sp.getBoolean("name", true));
            lastElecCheckBox.setChecked(sp.getBoolean("lastElec", true));
            nowElecCheckBox.setChecked(sp.getBoolean("nextElec", true));
            marginCheckBox.setChecked(sp.getBoolean("elec", true));
            priceCheckBox.setChecked(sp.getBoolean("price", true));
            aggregateCheckBox.setChecked(sp.getBoolean("value", true));
            phoneCheckBox.setChecked(sp.getBoolean("phone", false));
            locationCheckBox.setChecked(sp.getBoolean("location", false));
            eMailCheckBox.setChecked(sp.getBoolean("eMail", true));
        } else {
            titleEditText.setText(group);
            dateEditText.setText(time);
        }
    }

    /**
     * save preference from checkBox and editText
     */
    private void savePreference() {
        Toast.makeText(GenerateXlsActivity.this, "setting is not exist", Toast.LENGTH_SHORT).show();

        //Save in preferences
        //EditText
        SharedPreferences.Editor editor = sp.edit();
        //EditText
        editor.putString("title", group);
        editor.putString("sign", signEditText.getText().toString());

        //CheckBox
        editor.putBoolean("code", codeCheckBox.isChecked());
        editor.putBoolean("name", nameCheckBox.isChecked());
        editor.putBoolean("lastElec", lastElecCheckBox.isChecked());
        editor.putBoolean("nextElec", nowElecCheckBox.isChecked());
        editor.putBoolean("elec", marginCheckBox.isChecked());
        editor.putBoolean("price", priceCheckBox.isChecked());
        editor.putBoolean("value", aggregateCheckBox.isChecked());
        editor.putBoolean("phone", phoneCheckBox.isChecked());
        editor.putBoolean("location", locationCheckBox.isChecked());
        editor.putBoolean("eMail", eMailCheckBox.isChecked());

        //property is exist
        editor.putBoolean("isSave", true);

        editor.apply();

        //save generate group setting name to database
//        DbUtils db = DbTools.getInstance(this);
//        try {
//            db.saveBindingId(new Setting(group + "Setting"));
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generate_xls, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
