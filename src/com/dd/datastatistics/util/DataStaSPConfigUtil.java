package com.dd.datastatistics.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.dd.datastatistics.biz.DataStatistics;
import com.dd.datastatistics.constant.DataStaMeilaConfig;

/**
 * 程序配置数据，保存在sp中，清除缓存时不清除
 * @author Administrator
 *
 */
public class DataStaSPConfigUtil {
	static final String TAG = "SPConfigUtil";
	static final String SP_NAME = "SPConfigUtil";

	synchronized public static long loadLong(final String key, long defVal) {
		Long cf = defVal;
		String cfStr = load(key);
		try {
			cf = Long.parseLong(cfStr);
		} catch (Exception e) {

		}
		return cf;
	}
	synchronized public static int loadInt(final String key, int defVal) {
		Integer cf = defVal;
		String cfStr = load(key);
		try {
			cf = Integer.parseInt(cfStr);
		} catch (Exception e) {

		}
		return cf;
	}

	synchronized public static boolean loadBoolean(final String key,
			boolean defVal) {
		Boolean cf = defVal;
		String cfStr = load(key);
		try {
			if(!TextUtils.isEmpty(cfStr)){
				cf = Boolean.parseBoolean(cfStr);
			}
		} catch (Exception e) {

		}
		return cf;
	}

	synchronized public static String load(final String key) {
		String cf = null;
		if (DataStatistics.CONTEXT == null) {
			return cf;
		}
		try {
			SharedPreferences sp = DataStatistics.CONTEXT.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
			cf = sp.getString(key, null);
		} catch (Exception e) {
			// MeilaLog.e(TAG, e);
		}
		DataStaMeilaLog.d(TAG, "load key: " + key + ", val: " + cf);
		return cf;
	}

	synchronized public static void save(final String key, final String val) {
		DataStaMeilaLog.d(TAG, "save key: " + key + ", val: " + val);
		if (DataStatistics.CONTEXT == null || val == null) {
			return;
		}
		try {
			SharedPreferences sp = DataStatistics.CONTEXT.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putString(key, val);
			edit.commit();
		} catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}

		DataStaMeilaLog.d(TAG, "save: " + val);
	}

	synchronized public static void clear(final String key) {
		DataStaMeilaLog.d(TAG, "clear key: " + key);
		try {
			SharedPreferences sp = DataStatistics.CONTEXT.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.remove(key);
			edit.commit();
		} catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}
	}
	
	synchronized public static void clearAll() {
		DataStaMeilaLog.d(TAG, "clearAll");
		try {
			SharedPreferences sp = DataStatistics.CONTEXT.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.clear();
			edit.commit();
		} catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}
	}
	
	public static boolean isUpload(){
		long time = loadLong(DataStaMeilaConfig.KEY_UPLOAD_TIME, 0);		
			return (DataStaMeilaConfig.currentTimeSec() - time) > DataStaMeilaConfig.UPLOAD_TIME;
	}
}
