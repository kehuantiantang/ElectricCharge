package com.yj.eleccharge;

import android.os.Environment;

import java.io.File;

/**
 * Created by 游捷 on 2015/8/20.
 */
public class AppConfig {

    public final static String CONSTANT_CLAER_ALL = "删除全部";

    public final static String CONSTANT_ACTIVITY_DELIVER_INFO = "INFO";

    public final static String CONSTANT_ACTIVITY_DELIVER_FLAG = "FLAG";

    public final static String CONSTANT_ACTIVITY_DELIVER_SETTING = "DELIVER_SETTING";

    public final static String CONSTANT_FRAGMENT_TAG = "tagFragment";

    /**
     * 关于设置的一些常量
     */
    public final static double SETTING_SETTING_FULL_QUOTA = 9999.0;



    /**
     * 系统的设置的SharePreference
     */
    public final static String SETTING_SHAREDPREFERENCE = "setting";

    public final static int ACTIVITY_RESULT_OK = -1;


    //	ExportExcel的常量
    /**
     * 设置表格默认列宽度为12个字节
     */
    public final static int DEFALUT_COLUMN_WIDTH = 12;
    /**
     * 设置表格行高为20个字节
     */
    public final static float DEFALUT_COLUMN_HEIGHT = 19.5f;

    /**
     * 大标题开始在第几行
     */
    public final static int START_TITLE_ROW = 1;

    /**
     * 开始内容是在第几行
     */
    public final static int START_CONTENT_ROW = 4;
    /**
     * 开始的时间是第几行
     */
    public static final int START_TIME_ROW = 2;

    public final static String DIR_XLS = "xls";

    /**
     * 内容文字大小
     */
    public final static short CUSTOM_CONTENT_TEXT_SIZE = (short)14;

    /**
     * 标题大小
     */
    public final static short CUSTOM_TITLE_TEXT_SIZE = (short)18;


    /**
     * ExpportInfo的常量
     */
    public final static String[] CUSTOM_TITLE = { "户号", "姓名", "上月电", "本月电",
            "计费量(度)", "单价(元/度)", "金额(元)" };

    public static final String CUSTOM_GROUP = "赣州八五二台生活用电计费表";

    public final static String DIR_BASE = Environment
            .getExternalStorageDirectory() + File.separator + "ElectricCharge" + File.separator;


    public final static int FLAG_SUCCESS = 0;
    public final static int FLAG_FAILED = 1;


    /**
     * CrashHandler的Log文件保存路径
     */
    public static final String LOG_PATH = "/";

    /**
     * log 日志上传的URL地址
     */
    public static final String UPLOAD_URL = "http://117.170.70.228:8080/AndroidService/switch/logfile";

    /**
     * Debug模式？
     */
    public static final boolean DEBUG_MODEL = false;

    /**
     * wifi 上传日志？
     */
    public static final boolean IS_WIFI_UPDATE_LOG = true;

}
