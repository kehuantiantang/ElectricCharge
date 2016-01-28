package com.kehuantiantang.logcollection.upload;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 保存多个需要上传文件的文件名，使用key value的形式保存
 *
 * 关于上传InputStream流的消息，见下
 * @see http://blog.csdn.net/wangpeng047/article/details/38303865
 */
public class HttpParameters {
	/**
	 * 一般为你设定的表示字符串
	 */
	private ArrayList<String> mKeys = new ArrayList<String>();
	/**
	 * 一般为文件名
	 */
	private ArrayList<String> mValues = new ArrayList<String>();

	public void add(String key, String value) {
		if ((!TextUtils.isEmpty(key)) && (!TextUtils.isEmpty(value))) {
			this.mKeys.add(key);
			this.mValues.add(value);
		}
	}

	public void add(String key, int value) {
		this.mKeys.add(key);
		this.mValues.add(String.valueOf(value));
	}

	public void add(String key, long value) {
		this.mKeys.add(key);
		this.mValues.add(String.valueOf(value));
	}

	public void remove(String key) {
		int firstIndex = this.mKeys.indexOf(key);
		if (firstIndex >= 0) {
			this.mKeys.remove(firstIndex);
			this.mValues.remove(firstIndex);
		}
	}

	public void remove(int i) {
		if (i < this.mKeys.size()) {
			this.mKeys.remove(i);
			this.mValues.remove(i);
		}
	}

	private int getLocation(String key) {
		if (this.mKeys.contains(key)) {
			return this.mKeys.indexOf(key);
		}
		return -1;
	}

	public String getKey(int location) {
		if ((location >= 0) && (location < this.mKeys.size())) {
			return (String) this.mKeys.get(location);
		}
		return "";
	}

	public String getValue(String key) {
		int index = getLocation(key);
		if ((index >= 0) && (index < this.mKeys.size())) {
			return (String) this.mValues.get(index);
		}
		return null;
	}

	public String getValue(int location) {
		if ((location >= 0) && (location < this.mKeys.size())) {
			String rlt = this.mValues.get(location);
			return rlt;
		}
		return null;
	}

	public int size() {
		return this.mKeys.size();
	}

	public void addAll(HttpParameters parameters) {
		for (int i = 0; i < parameters.size(); i++)
			add(parameters.getKey(i), parameters.getValue(i));
	}

	public void clear() {
		this.mKeys.clear();
		this.mValues.clear();
	}
}
