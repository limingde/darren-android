package com.dd.datastatistics.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.text.TextUtils;

import com.dd.datastatistics.biz.DataStatistics;
/**
 * 涓�簺閰嶇疆鏁版嵁锛屼繚瀛樺湪鏂囦欢涓�
 * @author Administrator
 *
 */
public class DataStaFConfigUtil {
	static final String TAG = "MConfigUtil";
	
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
			File dir =DataStatistics.CONTEXT.getFilesDir();
			File f = new File(dir, key);
			@SuppressWarnings("resource")
			FileInputStream in = new FileInputStream(f);
			byte[] buf = new byte[(int) f.length()];
			in.read(buf);
			cf = new String(buf);
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
			File dir = DataStatistics.CONTEXT.getFilesDir();
			File f = new File(dir, key);
			FileOutputStream out = new FileOutputStream(f);
			out.write(val.toString().getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}

		DataStaMeilaLog.d(TAG, "save: " + val);
	}

	synchronized public static void clear(final String key) {
		DataStaMeilaLog.d(TAG, "clear key: " + key);
		try {
			File dir = DataStatistics.CONTEXT.getFilesDir();
			File f = new File(dir, key);
			if (f.isFile() && f.exists()) {
				f.delete();
			}
		} catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}
	}
}
