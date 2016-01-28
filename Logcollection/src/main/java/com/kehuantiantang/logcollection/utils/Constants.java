package com.kehuantiantang.logcollection.utils;

/**
 * 设置修改的常量
 */
public class Constants {
	/**
	 * 是否是Debug模式,如果是debug模式，会将文件写到sd卡,通过CACHE_DIR设置存储卡路径
	 * @see Constants#CACHE_DIR
	 * @see com.kehuantiantang.logcollection.LogCollector#setDebugMode(boolean)
	 */
	public static boolean DEBUG = false;

	/**
	 * 外部存储器默认存储位置
	 * @see com.kehuantiantang.logcollection.LogCollector#setCacheDir(String)
	 */
	public static String CACHE_DIR = "/Android/data/";

}
