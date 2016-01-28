package com.yj.eleccharge.tools;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * 通过package名，查找package下面的指定类的类名
 */
public class Scanner {

    /**
     * @param context
     * @param packageName
     * @param excludeString 结尾字符串
     * @param endTag  map中key是否需要结尾字符串
     * @return key value 类名键值对
     */
    public static Map<String, String> scan(Context context, String packageName, @NonNull String excludeString, @NonNull String endTag) {
        //生成类名对应类名的映射
        Map<String, String> classes = new HashMap<>();
        try {
            //apk的dex文件，获得源码来反射
            DexFile dex = new DexFile(context.getPackageCodePath());
            //枚举类，来遍历所有源代码
            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String className = entries.nextElement();
                //Add whole classname --> package + classname
                //classList.add(className);
                //指定package包里面的文件
                if (className.contains(packageName)) {
                    //排除包含有excludeString的类
                    if (!className.matches(excludeString)) {
                        //需不需要留结尾的tag
                        if (!"".equals(endTag)) {
                            classes.put(className.substring(className.lastIndexOf(".") + 1, className.length() - endTag.length()), className);
                        } else {
                            classes.put(className.substring(className.lastIndexOf(".") + 1, className.length()), className);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static Map<String, String> scan(Context context, String packageName, @NonNull String excludeString) {
        return scan(context, packageName, excludeString, "");
    }


    /**
     * .*\$.*防止内部类和空文件
     * @param context
     * @param packageName
     * @return
     */
    public static Map<String, String> scan(Context context, String packageName) {
        return scan(context, packageName, ".*\\$.*", "");
    }
}