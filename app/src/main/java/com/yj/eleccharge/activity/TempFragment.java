package com.yj.eleccharge.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kehuantiantang.logcollection.capture.LogFileStorage;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.R;
import com.yj.eleccharge.entity.Group;
import com.yj.eleccharge.tools.DbTools;
import com.yj.eleccharge.tools.SettingTools;
import com.yj.eleccharge.tools.xls.ExportXls;
import com.yj.eleccharge.tools.xls.XlsData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import yj.com.fileexplorer.FileExplorerActivity;

/**
 * Created by 游捷 on 2015/8/19.
 * 滑动ListView的fragment创建
 */
public class TempFragment extends AppCompatActivity implements View.OnClickListener {
    private String TAG = getClass().getSimpleName();

    @ViewInject(R.id.temp_button_clearData)
    private Button clearDataButton;
    @ViewInject(R.id.temp_button_clearTable)
    private Button clearTableButton;
    @ViewInject(R.id.temp_button_addInfo)
    private Button addInfoButton;
    @ViewInject(R.id.temp_button_jump)
    private Button jumpButton;
    @ViewInject(R.id.temp_button_test)
    private Button testButton;

    public TempFragment() {
        super();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.templayout);
        ViewUtils.inject(this);
    }

    @OnClick({R.id.temp_button_clearData, R.id.temp_button_clearTable, R.id.temp_button_addInfo, R.id.temp_button_jump, R.id.temp_button_test})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.temp_button_clearData:
                DbTools.clearData(this);
                break;
            case R.id.temp_button_clearTable:
                DbTools.clearTable(this);
                break;
            case R.id.temp_button_addInfo:
                DbTools.addData(this);
                break;
            case R.id.temp_button_jump:
                jumpActivity();
                break;
            case R.id.temp_button_test:
                test();
                break;
            default:
                Toast.makeText(this, "Error ! No button id !", Toast.LENGTH_SHORT).show();
        }
    }

    private void jumpActivity() {
        Log.e(TAG, "jump");
        Intent intent = new Intent(this, FileExplorerActivity.class);

        intent.putExtra(AppConfig.CONSTANT_ACTIVITY_DELIVER_INFO, "2015-07");
        this.startActivityForResult(intent, 0);
    }

    private void test() {

        DbUtils db = DbTools.getInstance(this);
        ExportXls exportXls = null;
        try {
            Group group = db.findFirst(Selector.from(Group.class).where("name", "=", "name group 0"));
            List<XlsData.XlsLine> list = DbTools.generateData(this, "2016-10", group.getId());
            SharedPreferences sharedPreferences;
            String settingName = group.getGroupSetting();
            if (!SettingTools.isExist(this, settingName)) {
                String name = UUID.randomUUID() + "";
                sharedPreferences = SettingTools.getSharedPreferencesFromStrings(this, name, R.array.setting_xls_default);
                group.setGroupSetting(name);
                db.update(group);
            } else {
                sharedPreferences = this.getSharedPreferences(settingName, 0);
            }
            Map<String, ?> settings = sharedPreferences.getAll();
            exportXls = new ExportXls(list, settings);
//            exportXls.setTitle("江西理工大学信息工程学院");
        } catch (DbException e) {
            e.printStackTrace();
        }

        LogFileStorage logFileStorage = LogFileStorage.getInstance(this);
        File file = logFileStorage.getExternalDir(this, "eleccharge");
        if (!file.exists()) {
            file.mkdirs();
        }

        String saveFileString = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".xls";
        try {
            OutputStream outputStream = new FileOutputStream(saveFileString);
            exportXls.exportExcel(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
