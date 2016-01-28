package com.yj.eleccharge;

import android.app.Application;

import com.kehuantiantang.logcollection.LogCollector;
import com.yj.eleccharge.tools.DbTools;

/**
 * Created by kehuantiantang on 2015/11/26.
 */
public class BaseApplication extends Application {
    //post method , upload logfile url,replace your site . support http or https

    @Override
    public void onCreate() {
        super.onCreate();

        //set debug mode , you can see debug log , and also you can get logfile in sdcard;
        LogCollector.setDebugMode(AppConfig.DEBUG_MODEL);

        //设置Log目录文件夹
        LogCollector.setCacheDir(AppConfig.LOG_PATH);

        //params can be null
        LogCollector.init(getApplicationContext(), AppConfig.UPLOAD_URL, null);

        //is wifi available update ?
        LogCollector.upload(AppConfig.IS_WIFI_UPDATE_LOG);

        //创建所有的表
        DbTools.initAllTable(this);
    }
}
