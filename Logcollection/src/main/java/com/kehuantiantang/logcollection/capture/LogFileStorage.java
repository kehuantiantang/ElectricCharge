package com.kehuantiantang.logcollection.capture;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.kehuantiantang.logcollection.utils.Constants;
import com.kehuantiantang.logcollection.utils.LogCollectorUtility;
import com.kehuantiantang.logcollection.utils.LogHelper;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 日志文件存储类，该类操作log日志到磁盘文件中
 */
public class LogFileStorage {

	private static final String TAG = LogFileStorage.class.getName();

	public static final String LOG_SUFFIX = ".log";

	private static final String CHARSET = "UTF-8";

	private static LogFileStorage sInstance;

	private Context mContext;

	private LogFileStorage(Context ctx) {
		mContext = ctx.getApplicationContext();
	}

	public static synchronized LogFileStorage getInstance(Context ctx) {
		if (ctx == null) {
			LogHelper.e(TAG, "Context is null");
			return null;
		}
		if (sInstance == null) {
			sInstance = new LogFileStorage(ctx);
		}
		return sInstance;
	}

	/**
	 * 获得log文件
	 * @return
	 */
	public File getUploadLogFile(){
		File dir = mContext.getFilesDir();
		File logFile = new File(dir, LogCollectorUtility.getMid(mContext)
				+ LOG_SUFFIX);
		if(logFile.exists()){
			return logFile;
		}else{
			return null;
		}
	}

	/**
	 * 删除内置存储器中的log文件
	 * @return
	 */
	public boolean deleteInternalUploadLogFile(){
		File dir = mContext.getFilesDir();
		File logFile = new File(dir, LogCollectorUtility.getMid(mContext)
				+ LOG_SUFFIX);
		if(logFile == null){
			return false;
		}
		return logFile.delete();
	}

	/**
	 * 删除内置存储器中的log文件
	 * @return
	 */
	public boolean deleteExternalUploadLogFile(){
		File dir = getExternalLogDir();
		File logFile = new File(dir, LogCollectorUtility.getMid(mContext)
				+ LOG_SUFFIX);
		if(logFile == null){
			return false;
		}
		return logFile.delete();
	}

	/**
	 * 将日志写在内部存储器中
	 * @param logString 日志文件要记录的数据
	 * @return
	 */
	public boolean saveLogFile2Internal(String logString) {
		try {
			File dir = mContext.getFilesDir();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File logFile = new File(dir, LogCollectorUtility.getMid(mContext)
					+ LOG_SUFFIX);
			FileOutputStream fos = new FileOutputStream(logFile , true);
			fos.write(logString.getBytes(CHARSET));
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			LogHelper.e(TAG, "SaveLogFile2Internal failed!");
			return false;
		}
		return true;
	}

	/**
	 * 将日志写在SD卡里面
	 * @param logString
	 * @param isAppend
	 * @return
	 */
	public boolean saveLogFile2SDCard(String logString, boolean isAppend) {
		if (!this.isSDCardExist()) {
			LogHelper.e(TAG, "Sdcard not exist");
			return false;
		}
		try {
			File logDir = getExternalLogDir();
			if (!logDir.exists()) {
				logDir.mkdirs();
			}
			
			File logFile = new File(logDir, LogCollectorUtility.getMid(mContext)
					+ LOG_SUFFIX);

			/*if (!isAppend) {
				if (logFile.exists() && !logFile.isFile())
					logFile.delete();
			}*/

			LogHelper.d(TAG, logFile.getPath());
			
			FileOutputStream fos = new FileOutputStream(logFile , isAppend);
			fos.write(logString.getBytes(CHARSET));
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "SaveLogFile2SDCard failed!");
			return false;
		}
		return true;
	}

	/**
	 * 获得SDCard的路径中的文件夹File
	 * @return
	 */
	private File getExternalLogDir() {
		File logDir = this.getExternalDir(mContext, "Log");
		LogHelper.d(TAG, logDir.getPath());
		return logDir;
	}

	/**
	 * 获取程序外部(sd)的目录，默认存在"/Android/data/packageName"里面
	 *
	 * 如果不想存在默认文件夹中 @see com.kehuantiantang.logcollection.LogCollector#setCacheDir(String)
	 * @param dirName cacheDir下的子文件夹
	 * @param context
	 * @return
	 */
	public File getExternalDir(Context context , String dirName) {
		 String cacheDir = Constants.CACHE_DIR + context.getPackageName()
				+ "/";
		return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ cacheDir + dirName + "/");
	}

	/**
	 * SDCard 存在？
	 * @return exist--> true , otherwise false
	 */
	public boolean isSDCardExist() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}


}
