package com.dd.whateat.bean;

import com.dd.whateat.db.BaseModel;

/**
 * 缓存image
 * @author Administrator
 *
 */
public class ImgCacheItemModel extends BaseModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String TAG = "ImgCacheItemModel";
	final static long OVER_DUE = 15*24*3600;//15天过期

	public final static String COL_MD5 = "md5";
	public final static String COL_UPDATETIME = "upadtetime";

	public String md5 = "";//对url做16位md5，取1，2位作为一级目录，3，4位作为二级目录
	public String dir1= "";//存放目录
	public String dir2= "";
	public String dir3= "";
	public String filename = "";
	public long upadtetime;
}
