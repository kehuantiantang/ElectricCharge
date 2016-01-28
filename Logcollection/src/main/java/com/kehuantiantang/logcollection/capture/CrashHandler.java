package com.kehuantiantang.logcollection.capture;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.kehuantiantang.logcollection.utils.Constants;
import com.kehuantiantang.logcollection.utils.LogCollectorUtility;
import com.kehuantiantang.logcollection.utils.LogHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URLEncoder;

/**
 * 该用来承接错误崩溃的错误信息，将其转换成StringBuffer
 */
public class CrashHandler implements UncaughtExceptionHandler {
	/**
	 * app package name
	 */
	private static final String TAG = CrashHandler.class.getName();

	/**
	 * 编码格式
	 */
	private static final String CHARSET = "UTF-8";

	private static CrashHandler sInstance;

	private Context mContext;

	private UncaughtExceptionHandler mDefaultCrashHandler;

	/**
	 * app 版本名
	 */
	String appVerName;

	/**
	 * app 版本号
	 */
	String appVerCode;

	/**
	 * 操作系统版本
	 */
	String OsVer;

	/**
	 * 供应商
	 */
	String vendor;

	/**
	 * 机型
	 */
	String model;

	/**
	 * imei  + android_id + imsi
	 */
	String mid;

	String packageName;

	private CrashHandler(Context context) {
		//单例返回context
		mContext = context.getApplicationContext();
		appVerName = "AppVerName: \t" + LogCollectorUtility.getVerName(mContext);
		appVerCode = "AppVerCode: \t" + LogCollectorUtility.getVerCode(mContext);
		//release vision
		OsVer = "OsVer: \t" + Build.VERSION.RELEASE;
		//制造厂商
		vendor = "Vendor: \t" + Build.MANUFACTURER;
		//最后产品版本
		model = "Model: \t" + Build.MODEL;
		mid = "Mid: \t" + LogCollectorUtility.getMid(mContext);
		packageName = "PackageName: \t" + LogCollectorUtility.getPackageName(mContext);
	}

	/**
	 * 单例获得实例化对象
	 * @param context
	 * @return
	 */
	public static synchronized CrashHandler getInstance(Context context) {
		if (context == null) {
			LogHelper.e(TAG, "Context is null");
			return null;
		}
		if (sInstance == null) {
			sInstance = new CrashHandler(context);
		}
		return sInstance;
	}

	/**
	 * 初始化，先判断是否有权限
	 */
	public void init() {
		if (mContext == null) {
			return;
		}

		boolean b = LogCollectorUtility.hasPermission(mContext);
		if (!b) {
			return;
		}
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 方法复写，将错误信息承接
	 * @param thread
	 * @param ex
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		//
		handleException(ex);

		//错误提示log里面
		ex.printStackTrace();

		if (mDefaultCrashHandler != null) {
			mDefaultCrashHandler.uncaughtException(thread, ex);
		} else {
			//终止
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}


	/**
	 * 将错误承接下来，保存在sd卡中
	 * @param ex
	 */
	private void handleException(Throwable ex) {
		String s = formatCrashInfo(ex);

		//保存转码的格式
		// String bes = fomatCrashInfoEncode(ex);

		// LogHelper.d(TAG, bes);
		//LogFileStorage.getInstance(mContext).saveLogFile2Internal(bes);
		LogHelper.d(TAG, s);

		//写在内部存储卡里面
		LogFileStorage.getInstance(mContext).saveLogFile2Internal(s);

		//默认false，不输出错误信息到sd卡中的log里面
		if(Constants.DEBUG){
			//写到SD卡里面
			LogFileStorage.getInstance(mContext).saveLogFile2SDCard(s, true);

		}
	}

	/**
	 * 格式化字符串，按照一定格式，将其变成String
	 * @param ex
	 * @return
	 */
	private String formatCrashInfo(Throwable ex) {

		/*
		 * String lineSeparator = System.getProperty("line.separator");
		 * if(TextUtils.isEmpty(lineSeparator)){ lineSeparator = "\n"; }
		 */
		String lineSeparator = "\r\n";

		StringBuilder sb = new StringBuilder();
		String logTime = "LogTime: \t" + LogCollectorUtility.getCurrentTime();

		String exception = "Exception: \t" + ex.toString();

		//writer
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);
		
		String dump = info.toString();

		//writer的md5
		String crashMD5 = "CrashMD5: \t"
				+ LogCollectorUtility.getMD5Str(dump);

		String crashDump = "CrashDump: \t\r\n" + "```\r\n{" + dump + "}\r\n```";
		printWriter.close();
		

		sb.append("------------------------------------").append(lineSeparator);
		sb.append(logTime).append(lineSeparator);
		sb.append(appVerName).append(lineSeparator);
		sb.append(appVerCode).append(lineSeparator);
		sb.append(OsVer).append(lineSeparator);
		sb.append(vendor).append(lineSeparator);
		sb.append(packageName).append(lineSeparator);
		sb.append(model).append(lineSeparator);
		sb.append(mid).append(lineSeparator);
		sb.append(exception).append(lineSeparator);
		sb.append(crashMD5).append(lineSeparator);
		sb.append(crashDump).append(lineSeparator);
		sb.append("------------------------------------").append(lineSeparator).append(lineSeparator)
				.append(lineSeparator);

		return sb.toString();

	}

	/**
	 * 将错误按照UTF-8编码写成String
	 * @param ex
	 * @return
	 */
	private String formatCrashInfoEncode(Throwable ex) {

		/*
		 * String lineSeparator = System.getProperty("line.separator");
		 * if(TextUtils.isEmpty(lineSeparator)){ lineSeparator = "\n"; }
		 */
		String lineSeparator = "\r\n";

		StringBuilder sb = new StringBuilder();
		String logTime = "LogTime: \t" + LogCollectorUtility.getCurrentTime();

		String exception = "Exception: \t" + ex.toString();

		//writer
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		String dump = info.toString();

		//writer的md5
		String crashMD5 = "CrashMD5: \t"
				+ LogCollectorUtility.getMD5Str(dump);



		try {
			dump = URLEncoder.encode(dump, CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}


		String crashDump = "CrashDump: \t\r\n" + "```\r\n{" + dump + "}\r\n```";
		printWriter.close();

		sb.append("------------------------------------").append(lineSeparator);
		sb.append(logTime).append(lineSeparator);
		sb.append(appVerName).append(lineSeparator);
		sb.append(appVerCode).append(lineSeparator);
		sb.append(OsVer).append(lineSeparator);
		sb.append(vendor).append(lineSeparator);
		sb.append(packageName).append(lineSeparator);
		sb.append(model).append(lineSeparator);
		sb.append(mid).append(lineSeparator);
		sb.append(exception).append(lineSeparator);
		sb.append(crashMD5).append(lineSeparator);
		sb.append(crashDump).append(lineSeparator);
		sb.append("------------------------------------").append(lineSeparator).append(lineSeparator)
				.append(lineSeparator);

		String bes = Base64.encodeToString(sb.toString().getBytes(),
				Base64.NO_WRAP);
		return bes;

	}

}
