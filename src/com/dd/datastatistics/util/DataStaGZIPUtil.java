package com.dd.datastatistics.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP鍘嬬缉瑙ｅ帇绫�
 */
public class DataStaGZIPUtil {

	private static String encode = "utf-8";// "ISO-8859-1"

	public String getEncode() {
		return encode;
	}

	/*
	 * 璁剧疆 缂栫爜锛岄粯璁ょ紪鐮侊細UTF-8
	 */
	public void setEncode(String encode) {
		DataStaGZIPUtil.encode = encode;
	}

	/**
	 * 鍒ゆ柇瀛楄妭鏁扮粍鏄惁鏄痝zip鏍煎紡鐨�
	 * @param buf
	 * @return
	 */
	public static boolean isGzipFormat(byte[] buf){
		if(buf == null || buf.length<=2){
			return false;
		}
		int flag = (buf[0] & 0xff) | ((buf[1] & 0xff) << 8);
		return flag == GZIPInputStream.GZIP_MAGIC;
	}
	
	/*
	 * 瀛楃涓插帇缂╀负瀛楄妭鏁扮粍
	 */
	public static byte[] compressToByte(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes(encode));
			gzip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/*
	 * 瀛楃涓插帇缂╀负瀛楄妭鏁扮粍
	 */
	public static byte[] compressToByte(String str, String encoding) {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes(encoding));
			gzip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/*
	 * 瀛楄妭鏁扮粍瑙ｅ帇缂╁悗杩斿洖瀛楃涓�
	 */
	public static String uncompressToString(byte[] b) {
		if (b == null || b.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(b);

		try {
			GZIPInputStream gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	/*
	 * 瀛楄妭鏁扮粍瑙ｅ帇缂╁悗杩斿洖瀛楃涓�
	 */
	public static String uncompressToString(byte[] b, String encoding) {
		if (b == null || b.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(b);

		try {
			GZIPInputStream gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			return out.toString(encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
