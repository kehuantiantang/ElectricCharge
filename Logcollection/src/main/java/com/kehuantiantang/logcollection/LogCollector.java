package com.kehuantiantang.logcollection;

import android.content.Context;
import android.util.Log;

import com.kehuantiantang.logcollection.capture.CrashHandler;
import com.kehuantiantang.logcollection.upload.HttpParameters;
import com.kehuantiantang.logcollection.upload.UploadLogManager;
import com.kehuantiantang.logcollection.utils.Constants;
import com.kehuantiantang.logcollection.utils.LogCollectorUtility;
import com.kehuantiantang.logcollection.utils.LogHelper;


/**
 * log收集器，对log进行设定
 */
public class LogCollector {

private static final String TAG = LogCollector.class.getName();

	/**
	 * 上传文件URL路径
	 */
	private static String Upload_Url;
	
	private static Context mContext;

	/**
	 * 是否初始化过
	 */
	private static boolean isInit = false;

	/**
	 * HttpParameter配置
	 */
	private static HttpParameters mParams;

	/**
	 * 初始化
	 * @param context
	 * @param upload_url upload url site
	 * @param params HttpParameter的参数
	 */
	public static void init(Context context , String upload_url , HttpParameters params){
		
		if(context == null){
			return;
		}
		
		if(isInit){
			return;
		}
		
		Upload_Url = upload_url;
		mContext = context;
		mParams = params;
		
		CrashHandler crashHandler = CrashHandler.getInstance(context);
		crashHandler.init();
		
		isInit = true;
	}

	/**
	 * 设定是否将文件上传,可以在程序每次启动的时候上传
	 * @param isWifiOnly
	 */
	public static void upload(boolean isWifiOnly){
		if(mContext == null || Upload_Url == null){
			Log.d(TAG, "Please check if init() or not");
			return;
		}
		if(!LogCollectorUtility.isNetworkConnected(mContext)){
			return;
		}
		
		boolean isWifiMode = LogCollectorUtility.isWifiConnected(mContext);
		
		if(isWifiOnly && !isWifiMode){
			return;
		}
		//生成上传实例
		UploadLogManager.getInstance(mContext).uploadLogFile(Upload_Url, mParams);
	}

	/**
	 * 是否是调试模式
	 * @param isDebug
	 */
	public static void setDebugMode(boolean isDebug){
		Constants.DEBUG = isDebug;
		LogHelper.enableDefaultLog = isDebug;
	}

	/**
	 * 设置SD卡的缓存位置，如果不想要默认的路径，则需要注意加“/”
	 * @param dir
	 */
	public static void setCacheDir(String dir){
		Constants.CACHE_DIR = dir;
	}
}
