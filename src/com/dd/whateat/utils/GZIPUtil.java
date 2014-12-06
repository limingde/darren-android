package com.dd.whateat.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP压缩解压类
 */
public class GZIPUtil {

	private static String encode = "utf-8";// "ISO-8859-1"

	public String getEncode() {
		return encode;
	}

	/*
	 * 设置 编码，默认编码：UTF-8
	 */
	public void setEncode(String encode) {
		GZIPUtil.encode = encode;
	}

	/**
	 * 判断字节数组是否是gzip格式的
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
	 * 字符串压缩为字节数组
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
	 * 字符串压缩为字节数组
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
	 * 字节数组解压缩后返回字符串
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
	 * 字节数组解压缩后返回字符串
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
