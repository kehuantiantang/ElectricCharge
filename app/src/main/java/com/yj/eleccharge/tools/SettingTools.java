package com.yj.eleccharge.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.yj.eleccharge.AppConfig;
import com.yj.eleccharge.entity.Group;

import java.util.List;

/**
 * 通过SharedPreferences进行设置进行修改和查找的工具类
 */
public class SettingTools {

    /**
     * 恢复默认设置
     */
    public static boolean resetDefault(Context context) {
        //用来删除每一个group的生成xls的设置
        DbUtils db = DbTools.getInstance(context);

        //从数据库中读取每一个group的sharePreference的key，之后一个一个恢复默认设置
        try {
            List<Group> groups = db.findAll(Group.class);
            if (groups != null) {
                for (Group group : groups) {
                    String settingString = group.getGroupSetting();
                    //将SharedPreferences的数据清除
                    if ("".equals(settingString)) {
                        SharedPreferences settingGroupSharedPreferences = context.getSharedPreferences(settingString, 0);
                        //直接将这个设置清除
                        settingGroupSharedPreferences.edit().clear().apply();
                        group.setGroupSetting("");
                        //group中的Setting清除
                        db.update(group);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }

        //系统设置清除
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConfig.SETTING_SHAREDPREFERENCE, 0);
        sharedPreferences.edit().clear().apply();
        return true;

        //TODO 注意Activity的刷新
    }

    /**
     * 这个serial名字的sharedPreferences是否存在
     * @param context
     * @param serial
     * @return
     */
    public static boolean isExist(Context context , String serial){
        if("".equals(serial)){
            return false;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(serial, 0);
        return Boolean.valueOf(sharedPreferences.getString("isExist", "false"));
    }
    /**
     * 从string.xml中读取值，将他放入以“name”为名字的sharedPreferences
     * @param context
     * @param name
     * @param stringsId
     * @return 返回一个生成的SharedPreferences
     */
    public static SharedPreferences getSharedPreferencesFromStrings(Context context, String name , int stringsId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String[] strings = context.getResources().getStringArray(stringsId);
        for (String string : strings) {
            String[] arrays = string.split("\\|");
            editor.putString(arrays[0], arrays[1]);
        }
        editor.apply();
        return sharedPreferences;
    }

}
