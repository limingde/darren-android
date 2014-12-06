package com.dd.datastatistics.biz;

import org.apache.http.HttpHost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.dd.datastatistics.constant.DataStaMeilaConfig;
import com.dd.datastatistics.dbutil.DataStaDBUtil;
import com.dd.datastatistics.orm.DataStaDataManager;
import com.dd.datastatistics.util.DataStaSPConfigUtil;

/**
 * 浜嬩欢缁熻
 * 
 * @author Darren
 *
 */
public class DataStatistics {
	public static Context CONTEXT;

	private static String apnType = "";

	private static UploaDataTask mUploaDataTask;

	private static boolean isLoop = true;
	/**
	 * 浣胯缃叏灞�娇鐢ㄧ殑鐗堟湰鍏宠仈鍙傛暟
	 * @param hostUrl 涓婃姤鐨勬湇鍔″櫒鍦板潃
	 * @param client_id    鐢ㄦ埛璁惧鍙�
	 * @param appVersionName  鐗堟湰鍚�    濡傛灉涓虹┖锛屽垯鏄粯璁ゅ�
	 * @param appVersionCode  鐗堟湰鍙�   濡傛灉涓虹┖锛屽垯鏄粯璁ゅ�
	 * @param packageName     APP鍖呭悕       濡傛灉涓虹┖锛屽垯鏄粯璁ゅ�
	 * @param dbName          鏁版嵁搴撳悕绉�    濡傛灉涓虹┖锛屽垯鏄粯璁ゅ�
	 * @param dbVersion       鏁版嵁搴撶増鏈彿    濡傛灉涓虹┖锛屽垯鏄粯璁ゅ�
	 * 
	 */
	public static void setVersionConstent(Context context,String hostUrl,String client_id,String appVersionName,String appVersionCode,String packageName){	
		CONTEXT = context;
		DataStaMeilaConfig.CLIENT_ID = client_id;
		
		if( !TextUtils.isEmpty(hostUrl)){
			DataStaMeilaConfig.JSON_URL = hostUrl;
		}
		if(!TextUtils.isEmpty(appVersionName)){
			DataStaMeilaConfig.appVersionName = appVersionName;	
		}
		if(!TextUtils.isEmpty(appVersionCode)){
			DataStaMeilaConfig.appVersionCode = appVersionCode;		
		}
		if(!TextUtils.isEmpty(packageName)){
			DataStaMeilaConfig.packageName = packageName;		
		}
	}

	/**
	 * 鍚姩缁熻浠诲姟
	 * 
	 */
	public static void dataStatisticStart(){
		//鍒濆鍖栧弬鏁�
		init();
		//寮�惎瀹氭椂鍣ㄥ畾鏃舵娴嬩笂鎶�
		checkServiceStart();
	}

	/**
	 * 鍏抽棴缁熻涓氬姟鎺ュ彛
	 * 
	 */
	public static void dataStatisticEnd(){		
		//鍏抽棴璇诲啓鏁版嵁搴撴搷浣�
		SaveDataTask.closeSaveToDbTask();
		if(CONTEXT != null){
			CONTEXT.unregisterReceiver(mReceiver);
		}
		//鍏抽棴涓婃姤绾跨▼
		isLoop = false;
		if(mUploaDataTask != null){
			mUploaDataTask.closeUploaDataThread();
			mUploaDataTask = null;
		}		
		//鍏抽棴鏁版嵁搴�
		DataStaDataManager dbMgr = DataStaDBUtil.getDataManager();
		dbMgr.lastClose();
	}
	/**
	 * 璁剧疆涓婃姤鏈哄埗鐨勫弬鏁�      榛樿璁剧疆鐨勬槸3G 缃戠粶鐜涓嬬殑 涓婁紶鍙傛暟   WiFi鐜涓�鐩稿簲鐨勭缉鐭椂闂�澧炲姞涓婁紶鏁伴噺
	 * 

	 * 
	 * checkTime  瀹氭椂鍣ㄥ畾鏃堕棿妫�祴鐨�闂撮殧鏃堕棿   鍗曚綅涓虹
	 * 
	 * uploadTime 涓婃姤鏁版嵁鐨�闂撮殧鏃堕棿  鍗曚綅涓虹
	 * 
	 * overDueTime 鏁版嵁澶辨晥鏃堕棿   鍗曚綅涓虹
	 * 
	 */
	public static void setParameters(long checkTime,long uploadTime,long overDueTime,int dataCount){
		if(checkTime > 0){
			DataStaMeilaConfig.CHECK_TIME = checkTime;
			//			DataStaMeilaConfig.WIFI_CHECK_TIME = checkTime /2;
			//			DataStaMeilaConfig.NO_WIFI_CHECK_TIME = checkTime;
		}
		if(uploadTime > 0){
			DataStaMeilaConfig.UPLOAD_TIME = uploadTime;
			//			DataStaMeilaConfig.WIFI_UPLOAD_TIME = uploadTime /2;
			//			DataStaMeilaConfig.NO_WIFI_UPLOAD_TIME = uploadTime;
		}
		if(overDueTime > 0){
			//澶栭儴璋冪敤鏃惰缃殑鍗曚綅閮芥槸绉�   浣嗘槸 璇ュ�闇�鐢ㄥ埌姣鍊硷紝鍥犱负淇濆瓨鍦ㄦ暟鎹簱涓殑鏄绉掑�
			DataStaMeilaConfig.OVER_DUE_TIME = overDueTime;			
		}
		if(dataCount > 0){
			DataStaMeilaConfig.UPLOAD_COUNT_EVERY_TIME = 	dataCount;
			DataStaMeilaConfig.WIFI_UPLOAD_COUNT_EVERY_TIME = dataCount * 2;
			DataStaMeilaConfig.NO_WIFI_UPLOAD_COUNT_EVERY_TIME = dataCount;
		}
	}

	/**
	 * 鏍″鏈嶅姟鍣ㄦ椂闂翠笌鏈湴鏃堕棿宸窛  
	 *     濡傛灉涓嶈缃粯璁や负娌℃湁宸�
	 *     鏈�ソ鏄瘡娆℃嬁鍒版湇鍔″櫒鏃堕棿涔嬪悗閮借缃竴娆�
	 */
	public static void checkServerTime(long ServerTime){
		DataStaMeilaConfig.checkServerTime(ServerTime);

	}
	private static void init(){
		//鍒濆鍖栨暟鎹簱
		// 鍒濆鍖栨暟鎹簱杩炴帴
		DataStaDataManager dbMgr = DataStaDBUtil.getDataManager();
		dbMgr.firstOpen();
		SaveDataTask.IsOpenStat = true;
		if(CONTEXT != null){
			CONTEXT.registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
		//濡傛灉鏄涓�浣跨敤閲岄潰杩樻病鏈変繚瀛樹笂娆¤褰曠殑鏁版嵁 鍒欏厛淇濆瓨涓婃涓婁紶鐨勬椂闂翠负鐜板湪
		long l = DataStaSPConfigUtil.loadLong(DataStaMeilaConfig.KEY_UPLOAD_TIME, -1);
		if(l== -1){
			DataStaSPConfigUtil.save(DataStaMeilaConfig.KEY_UPLOAD_TIME, String.valueOf(DataStaMeilaConfig.currentTimeSec()));
		}
		isLoop = true;
		//寮�惎瀹氭椂鍣�
		Log.i("init","init:" + mUploaDataTask );
		if(mUploaDataTask == null){
			mUploaDataTask = UploaDataTask.getInstance();
		}
	}


	/**
	 * 瀹氭椂鍣ㄨ疆璇㈡娴嬫槸鍚﹂渶瑕佷笂鎶�濡傛灉闇�涓婃姤灏卞惎鍔ㄤ笂鎶ユ暟鎹殑绾跨▼ 杩涜鏁版嵁涓婃姤
	 * 
	 */
	final static Handler h = new Handler();
	private static void checkServiceStart(){	
		Log.i("checkServiceStart","CHECK_TIME:" + DataStaMeilaConfig.CHECK_TIME * 1000 );
		h.post(runnable);
	}
	private static Runnable runnable = new Runnable () {			
		@Override
		public void run() {
			try {			
			if(DataStaSPConfigUtil.isUpload() ||isWiFi()){
				if(mUploaDataTask != null){
					Log.i("Runnable","runnable:" + mUploaDataTask );
					mUploaDataTask.doUploadData();
					Log.i("Runnable","runnable:" + mUploaDataTask );
				} else {
					Log.i("Runnable","runnable:" + mUploaDataTask );
					mUploaDataTask = UploaDataTask.getInstance();
					mUploaDataTask.doUploadData();
					Log.i("Runnable","runnable:" + mUploaDataTask );
				}
			}
			if(isLoop){
				h.postDelayed(this,DataStaMeilaConfig.CHECK_TIME * 1000);
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};



	public final static HttpHost getHttpProxy() {
		HttpHost httpHost = null;
		try {
			ConnectivityManager cm = (ConnectivityManager) getContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isAvailable()) {
				String typeName = netInfo.getTypeName();
				String extra = netInfo.getExtraInfo();
				if (typeName.equalsIgnoreCase("MOBILE")) {
					if (extra == null) {
						apnType = "unknown";
					} else {
						apnType = extra;
					}
					if (extra != null) {
						if (extra.toLowerCase().startsWith("cmwap")
								|| extra.toLowerCase().startsWith("uniwap")
								|| extra.toLowerCase().startsWith("3gwap")) {
							httpHost = new HttpHost("10.0.0.172", 80);
						} else if (extra.startsWith("#777")) {
							Uri apn_uri = Uri
									.parse("content://telephony/carriers/preferapn");
							Cursor c = getContext().getContentResolver().query(
									apn_uri, null, null, null, null);
							if (c.getCount() > 0) {
								c.moveToFirst();
								String ctapn = c.getString(c
										.getColumnIndex("user"));
								if (ctapn != null && !ctapn.equals("")) {
									if (ctapn.startsWith("ctwap")) {
										httpHost = new HttpHost("10.0.0.200",
												80);
										apnType = "ctwap";
									} else if (ctapn.toLowerCase().startsWith(
											"wap")) {
										httpHost = new HttpHost("10.0.0.200",
												80);
										apnType = "1x_wap";
									} else if (ctapn.startsWith("ctnet")) {
										apnType = "ctnet";
									} else if (ctapn.toLowerCase().startsWith(
											"card")) {
										apnType = "1x_net";
									}
								}
							}
						}
					}
				} else if (typeName.equalsIgnoreCase("WIFI")
						|| typeName.equalsIgnoreCase("WI FI")) {
					apnType = "wifi";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			httpHost = null;
			apnType = "unknown";
			Log.d("httpHost", "缃戠粶鐘舵�宸茬粡鏀瑰彉" + apnType);
		}
		return httpHost;
	}
	public static Context getContext() {
		if (CONTEXT != null)
			return CONTEXT;
		else
			return null;
	}


	/**
	 * 妫�祴鍒扮綉璺姸鎬�鍙戠敓鍙樺寲鏃�   妫�祴鏃堕棿鍜�涓婃姤鏃堕棿 涓婃姤鏉℃暟鐩稿簲鏀瑰彉
	 * 
	 * WiFi 鐜锛�涓婃姤鏃堕棿 鍜�妫�祴鏃堕棿缂╃煭涓��   涓婃姤鏉℃暟澧炲姞涓��
	 * 
	 * 鍏朵粬鐜 锛氭娴嬫椂闂村拰涓婃姤鏃堕棿鍔犻暱涓�� 涓婃姤鏉℃暟鍑忓皯涓�崐
	 * 
	 */
	public static String netType = "";
	private static BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.d("mark", "缃戠粶鐘舵�宸茬粡鏀瑰彉");

				ConnectivityManager cm = (ConnectivityManager) getContext()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isAvailable()) {
					String typeName = netInfo.getTypeName();
					if (typeName.equalsIgnoreCase("MOBILE")) {

					} else  if (typeName.equalsIgnoreCase("WIFI")
							|| typeName.equalsIgnoreCase("WI FI")){
						netType = "wifi";
					}
				} else{

				}
				Log.d("BroadcastReceiver mReceiver:", "缃戠粶鐘舵�宸茬粡鏀瑰彉 apnType:" + netType);
				if("wifi".equals(netType)){
					//					DataStaMeilaConfig.CHECK_TIME = DataStaMeilaConfig.WIFI_CHECK_TIME;
					//					DataStaMeilaConfig.UPLOAD_TIME = DataStaMeilaConfig.WIFI_UPLOAD_TIME;
					DataStaMeilaConfig.UPLOAD_COUNT_EVERY_TIME = DataStaMeilaConfig.WIFI_UPLOAD_COUNT_EVERY_TIME;
				} else {
					//					DataStaMeilaConfig.CHECK_TIME = DataStaMeilaConfig.NO_WIFI_CHECK_TIME;
					//					DataStaMeilaConfig.UPLOAD_TIME = DataStaMeilaConfig.NO_WIFI_UPLOAD_TIME;
					DataStaMeilaConfig.UPLOAD_COUNT_EVERY_TIME = DataStaMeilaConfig.NO_WIFI_UPLOAD_COUNT_EVERY_TIME;
				}
			}
		}

	};

	/**
	 * 鍒ゆ柇褰撳墠鐜鏄笉鏄疻iFi
	 * @return
	 */
	public static boolean isWiFi(){
		if(netType != null && "wifi".equals(netType)){
			return true;
		} else {
			return false;
		}
	}
}
