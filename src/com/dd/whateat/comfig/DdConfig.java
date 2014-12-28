package com.dd.whateat.comfig;

import android.text.TextUtils;



public final class DdConfig {

	//是否要显示第三方logo
	public static final boolean SHOW_OTHER_PLATFORM_LOGO = false;

	// 是否是调试状，发布时设置为false
	public static final boolean DEBUG = false;

	//是否允许http缓存
	public static final boolean ENABLE_HTTP_CACHE = DEBUG?false:true;
	// 是否将网络请求保存到本地文件
	public static final boolean SAVE_HTTP_CACHE = false;

	// 是否调试显示log,发布时设置为false
	public static volatile boolean DD_DEBUG_LOG = DEBUG?true:false;

	//腾讯分析统计上报
	public static final boolean MTA_TONGJI_LOG = DEBUG?false:true;

	// 用于调试一些异常的问题
	public static final boolean DEBUG_TEST = DEBUG?true:false;

	//数据库名与版本号
	public static final String DB_NAME = "dd_whateat";
	public static final int DB_VERSION = 4;


	//http请求的地地址
	public static final String JSON_URL = "http://openapi.aibang.com/";// 线上地址
	public final static String APK_KEY="59dfaf9ca55052fc6bce01cf9feb9ef2";

	public static final String JSON_URL_ORIGINAL = JSON_URL;//当开发修改了请求host之后，需要用这个复原，初始与JSON_URL相同

	public static final String URL_BANGDAN = "/html/top_list_hot";
	public static final String URL_HELP = "/html/help";
	public static final String URL_HELP_LEVEL = "/score/history/";
	//	public static final String URL_HELP_COIN = "/coin_mall/entry/";	
	public static final String URL_COIN_SHOP = "/coin_mall/goods/";
	//	public static final String URL_COIN_SHOP = "http://dev.meilapp.com:10001/coin_mall/goods/";
	public static final String URL_MEILA_AUTH = "/html/meila_auth";
	public static final String URL_CUBE = "/html/cube";
	public static final String URL_DISCLAIMER = "/html/help_policies";
	public static final String URL_MASS_CREATE_HELP = "/circle/create/intro/";
	//	public static final String URL_MASS_CREATE_HELP = "http://192.168.2.206:8001/circle/create/intro/";
	public static final String URL_HOMEPAGE = "http://www.meilapp.com";
	public static final String URL_CHECKIN_AGAIN = "/checkin_again/";
}
