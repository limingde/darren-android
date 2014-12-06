package com.dd.datastatistics.constant;



public class DataStaMeilaConfig {


	//浣跨敤缁熻宸ュ叿鏄垵濮嬪寲鐨勪竴浜涘父浜�
	public static String appVersionName = null;//鐗堟湰
	public static String appVersionCode = null;//鐗堟湰鍙�
	public static String packageName = null;//鍖呭悕
	//鏁版嵁搴撳悕涓庣増鏈彿
	public static  String DB_NAME = "meilaDataStatistic";
	public static  int DB_VERSION = 1;
	public static long CHECK_TIME = 30 * 60 ; //榛樿鏄竴灏忔椂妫�祴涓�鐪嬫槸鍚﹂渶瑕佷笂鎶�
	public static long UPLOAD_TIME = 3 * 60 * 60; //涓婃姤闂撮殧鏃堕棿  榛樿涓夊皬鏃朵笂鎶�涓�
	public static long OVER_DUE_TIME = 15 * 24 * 60 * 60 ;//榛樿鏄�5澶╃殑绠楄繃鏈� 灏变笉鍦ㄤ笂浼�
	public static int UPLOAD_COUNT_EVERY_TIME = 500;    //姣忔涓婃姤鏉℃暟
	
	//WiFi 鐜涓嬩笂鎶ュ弬鏁�
//	public static long WIFI_CHECK_TIME = 30 * 60 ; //WIFI 鐜
//	public static long WIFI_UPLOAD_TIME = 3 * 30 * 60; //WIFI 鐜
	public static int WIFI_UPLOAD_COUNT_EVERY_TIME = 1000;   //WIFI 鐜
	
	//鍏朵粬鐜涓嬩笂鎶ュ弬鏁�
//	public static long NO_WIFI_CHECK_TIME = 60 * 60 ; //3G 鐜
//	public static long NO_WIFI_UPLOAD_TIME = 3 * 60 * 60; //3G鐜
	public static int NO_WIFI_UPLOAD_COUNT_EVERY_TIME = 500; // 3G鐜
	
	public static String JSON_URL = "http://api.meilapp.com";//涓婃姤鐨勬湇鍔″櫒鍦板潃锛岄粯璁ょ殑鏄疉PP 绾夸笂鍦板潃
//	public static String JSON_URL = "http://dev.meilapp.com:10000";//涓婃姤鐨勬湇鍔″櫒鍦板潃锛岄粯璁ょ殑鏄疉PP 绾夸笅鍦板潃

	public static String CLIENT_ID;
	
	public static boolean isDebug = false;//閰嶇疆鏂囦欢鎵撳寘鍓嶉渶瑕佹敼


	public static long timeDelta = 0;
	public static String client_Id;

	public static final String VERSION_NAME = "meila_version";
	public static final String CLIENT_ID_NAME = "meila_client_id";

	public  static final String KEY_CHECK_TIME = "check time";
	public  static final String KEY_UPLOAD_TIME = "upload time";
	public final static Boolean MEILA_DEBUG_LOG = false;


	public final static String ACTION_UPLOAD_DATA_OK = "mainactivity.ACTION_UPLOAD_DATA_OK";

	public final static String INTENT_EXTRA_UPLOAD_DATA = "upload_data";
	public static final String KEY_HTTP_HEADER_MUD = "data_statistic_header.mud";
	
	
	public static final int k_BUFFER_SIZE = 1024 * 4;
	
	
	public static final boolean DEBUG = false;
	

	/**
	 * 鏍″鏈嶅姟鍣ㄦ椂闂翠笌鏈湴鏃堕棿宸窛
	 */
	public static void checkServerTime(long ServerTime){
		timeDelta = ServerTime;
	}
	public static long currentTimeSec(){
		return System.currentTimeMillis()/1000 + timeDelta;
	}
	//鑾峰彇鐗堟湰鍙�
	public static String getApplicationVersionCode() {
		return appVersionCode;
	}

	//鑾峰彇meila_client_id
	public static String getUniqueId() {
		return CLIENT_ID;
	}

}
