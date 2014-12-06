package com.dd.whateat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
	private static final String TAG = "Md5Util";
	private static final int OffsetBig = 256;
	private static final int OffsetSmall = 16;
	private static final int BeginIndex = 8;
	private static final int EndIndex = 24;

	/**
	 * 将字符串装换为MD5
	 * 
	 * @param str
	 *            要转换的字符串
	 * @return str的md5字符串
	 */
	public static String strToMd5(String str) {
		String md5Str = null;
		if (str != null && str.length() != 0) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(str.getBytes());
				byte[] b = md.digest();

				int i;
				StringBuffer buf = new StringBuffer("");
				for (int offset = 0; offset < b.length; offset++) {
					i = b[offset];
					if (i < 0) {
						i += OffsetBig;
					}
					if (i < OffsetSmall) {
						buf.append("0");
					}
					buf.append(Integer.toHexString(i));
				}
				// 32位
				// md5Str = buf.toString();
				// 16位
				md5Str = buf.toString().substring(BeginIndex, EndIndex);
			} catch (NoSuchAlgorithmException e) {
				DdLog.e(TAG, e);
			}
		}
		return md5Str;
	}
	
	/**
	 * 将字符串装换为MD5
	 * 
	 * @param str
	 *            要转换的字符串
	 * @return str的md5字符串
	 */
	public static String strToMd5For32bit(String str) {
		String md5Str = null;
		if (str != null && str.length() != 0) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(str.getBytes());
				byte[] b = md.digest();

				int i;
				StringBuffer buf = new StringBuffer("");
				for (int offset = 0; offset < b.length; offset++) {
					i = b[offset];
					if (i < 0) {
						i += OffsetBig;
					}
					if (i < OffsetSmall) {
						buf.append("0");
					}
					buf.append(Integer.toHexString(i));
				}
				
				// 32位
				 md5Str = buf.toString();
				// 16位
//				md5Str = buf.toString().substring(BeginIndex, EndIndex);
			} catch (NoSuchAlgorithmException e) {
				DdLog.e(TAG, e);
			}
		}
		return md5Str;
	}
	
	public static byte[] Md5(byte[] bytes) {
		if (bytes != null && bytes.length != 0) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(bytes);
				return md.digest();

				
			} catch (NoSuchAlgorithmException e) {
				System.err.println("NoSuchAlgorithmException");
			}
		}
		return null;
	}
}
