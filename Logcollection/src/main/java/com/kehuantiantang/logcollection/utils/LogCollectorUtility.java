package com.kehuantiantang.logcollection.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 崩溃日志收集,获取系统各种信息
 */
public class LogCollectorUtility {
	//在该类中显示的tag
	private static final String TAG = LogCollectorUtility.class.getName();

	/**
	 * 判断当前是否有网络
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }

	/**
	 * Wifi？
	 * @param context
	 * @return
	 */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable() && mWiFiNetworkInfo.isConnected();
            }
        }
        return false;
    }


	/**
	 * 获得权限，当前是否有读取信息的用户权限
	 * @param context
	 * @return
	 */
	public static boolean hasPermission(Context context) {
		if (context != null) {
			boolean b1 = context
					.checkCallingOrSelfPermission("android.permission.INTERNET") == 0;// 
			boolean b2 = context.checkCallingOrSelfPermission("android.permission.READ_PHONE_STATE") == 0;
			boolean b3 = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
			boolean b4 = context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0;
			boolean b5 = context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") == 0;
			
			if(!b1 || !b2 || !b3 || !b4 || !b5){
				Log.d(TAG, "没有添加权限");
				Toast.makeText(context.getApplicationContext(), "没有添加权限", Toast.LENGTH_SHORT).show();
			}
			return b1 && b2 && b3 && b4 && b5;
		}

		return false;
	}

	/**
	 * 获得现在的系统时间,并且以yyyy-MM-dd HH:mm:ss的格式返回
	 * @see SimpleDateFormat#SimpleDateFormat(String)
	 * @return
	 */
	public static String getCurrentTime(){
		long currentTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(currentTime);
		return time;
	}

	/**
	 * 包的版本名
	 * @param
	 * @return
	 */
	public static String getVerName(Context context){
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
			e.printStackTrace();
			return "error";
		}
		if(pi == null){
			return "error";
		}
		String versionName = pi.versionName;
		if(versionName == null){
			return "not set";
		}
		return versionName;
	}

	/**
	 * 返回包的代码版本
	 * @param context
	 * @return
	 */
	public static String getVerCode(Context context){
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
			e.printStackTrace();
			return "error";
		}
		if(pi == null){
			return "error";
		}
		int versionCode = pi.versionCode;

		return String.valueOf(versionCode);
	}

	/**
	 * 返回包的包名
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context){
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
			e.printStackTrace();
			return "error";
		}
		if(pi == null){
			return "error";
		}
		String packageName = pi.packageName;
		if(packageName == null){
			return "not set";
		}
		return packageName;
	}

	/**
	 * IMEI+androidID+IMSI
	 * @see LogCollectorUtility#getDeviceSerialForMid2() IMSI
	 * @see TelephonyManager#getDeviceId() IMEI
	 * @param context
	 * @return
	 */
	public static String getMid(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
        String AndroidID = android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
        String serialNo = getDeviceSerialForMid2();
        String m2 = getMD5Str("" + imei + AndroidID + serialNo);
        return m2;
    }

	/**
	 * 唯一设备号 imsi
	 * @return
	 */
	private static String getDeviceSerialForMid2() {
        String serial = "";
        try {
				Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }


	/**
	 * 根据str获得MD5值
	 * @param str
	 * @return
	 */
	public static String getMD5Str(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }
}
