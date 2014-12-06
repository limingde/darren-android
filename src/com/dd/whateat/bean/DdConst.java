package com.dd.whateat.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dd.whateat.DdApplication;
import com.dd.whateat.utils.DdLog;
import com.dd.whateat.utils.DdResource;
import com.dd.whateat.utils.HttpUtils;

public class DdConst implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String TAG = "Const";
	private static DdConst mConst;
	
//	private String[] musicMotto;
//	private String SPA_MUSIC_URL;
	public String ImgHttpPrefix;//图片服务器地址
	public String ShareHttpPrefix;//分享服务器地址
//	private String shareHttpPrefixTopic;
//	private String shareHttpPrefixArticle;
//	private String shareHttpPrefixProduct;

//	private int meilaSearchTypeTopic;
//	private int meilaSearchTypeProduct;
//
//	private String meilaModuleTopic;
//	private String meilaModuleArticle;
//	private String meilaModuleProduct;
//	private String meilaModuleTalent;

	public long ServerTime;//服务器时间
	
	public String HttpHost;//http请求的地址
	
	public String PNServerIP;//推送地址服务器ip
	public int PNServerPort;//推送地址服务器port
	
	public int CheckVersionInterval;//
	
	public long LatestVbookTime;
	public boolean IsUploadStat = false;
	
	public long VTalkRefreshInterval;//话题自动刷新时间，app隔一小时后再打开就自动刷新
	public List<String> StartupBanner;

	
	public void log(String tag){
		try{
			DdLog.d(tag, "--const log:");
			DdLog.d(tag, "--ImgHttpPrefix: "+ImgHttpPrefix);
			DdLog.d(tag, "--ShareHttpPrefix: "+ShareHttpPrefix);
			DdLog.d(tag, "--ServerTime: "+ServerTime);
			DdLog.d(tag, "--HttpHost: "+HttpHost);
			DdLog.d(tag, "--PNServerIP: "+PNServerIP);
			DdLog.d(tag, "--PNServerPort: "+PNServerPort);
		}catch (Exception e) {
		}
	}
	
	public synchronized static DdConst getConst(){
		if(mConst == null){
			mConst = loadConst();
//			if(mConst == null){
//				mConst = new MeilaConst();
//				mConst.HttpHost = MeilaConfig.JSON_URL;
//			}
		}
		return mConst;
	}
	public synchronized static void set(DdConst con){
		if(con != null){
			mConst = con;
			SharedPreferences sp = DdApplication.CONTEXT.getSharedPreferences(
					DdResource.SAVE_DATA_CONST,
					Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putString("constJSON", JSON.toJSONString(con));
			Calendar cal = Calendar.getInstance();
			edit.putLong("constTime", cal.getTimeInMillis());
			edit.commit();
			
		}else{
			DdLog.e(TAG, "cannot set null const");
		}
	}
	
	private static DdConst loadConst() {
		DdLog.d(TAG, "loadConst");
		SharedPreferences sharedPreferences = DdApplication.CONTEXT
				.getSharedPreferences(DdResource.SAVE_DATA_CONST,
						Context.MODE_PRIVATE);
		String strJSON = sharedPreferences.getString("constJSON", "");
		
		if (strJSON == null || strJSON.equals("")) {
			DdLog.e(TAG, "loadConst, pass wrong parameter, " + strJSON);
			return null;
		}
		
		DdConst con = JSON.parseObject(strJSON, DdConst.class);
		if(con != null && !TextUtils.isEmpty(con.PNServerIP)){
			mConst = con;
		}
		return mConst;
	}
	
	
	public void saveCheckVersionTime(Context con){
		SharedPreferences sp = con.getSharedPreferences(DdResource.SAVE_CHECKVERSION_TIME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		long time = currentTimeSec()+getConst().CheckVersionInterval;
		editor.putLong(DdResource.KEY_CHECKVERSION_TIME, time);
		editor.commit();
	}
	
	public long getCheckVersionTime(Context con){
		SharedPreferences sp = con.getSharedPreferences(DdResource.SAVE_CHECKVERSION_TIME, Context.MODE_PRIVATE);
//		long time = DdResource.timeDelta+ CalendarUtils.getTimestampInSecond()+Const.getConst().CheckVersionInterval;
		long time = sp.getLong(DdResource.KEY_CHECKVERSION_TIME, 0);
		return time;
	}
	public void clearCheckVersionTime(Context con){
		SharedPreferences sp = con.getSharedPreferences(DdResource.SAVE_CHECKVERSION_TIME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
	
	
	public static long timeDelta = 0;
	/**
	 * 校对服务器时间与本地时间差距
	 */
	public void checkServerTime(){
		timeDelta = ServerTime - System.currentTimeMillis()/1000;
	}
	public static long currentTimeSec(){
		return System.currentTimeMillis()/1000 + timeDelta;
	}
	
	public static String parseImgUrlPrefix(String url){
		if(TextUtils.isEmpty(url)){
			return url;
		}
		
		String parsedUrl;
		if(!url.toLowerCase().startsWith("http:") && !url.toLowerCase().startsWith("https:") ){
			if(DdConst.getConst() == null || DdConst.getConst().ImgHttpPrefix == null){
				parsedUrl = url;
			}else{
				parsedUrl = HttpUtils.concatUrl(DdConst.getConst().ImgHttpPrefix, url);
			}
		}else{
			parsedUrl = url;
		}
		return parsedUrl;
	}
	
	
	/**
	 * 统计上报控制看开关
	 * 
	 * @return
	 */
	public static boolean isUploadStat(){
		if(mConst == null){
			mConst = loadConst();			
		}
		if(mConst != null){
			return mConst.IsUploadStat;
		} else {
			return false;
		}
	}
}
