/**
 * 缃戠粶杈呭姪绫�
 */
package com.dd.datastatistics.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.dd.datastatistics.biz.DataStatistics;

/**
 * @author tom
 * 
 */
public class DataStaNetUtil {

	/**
	 * 瀹㈡埛绔疘P鍦板潃锛屽綋缃戠粶鍒囨崲鏃讹紝闇�閲嶆柊璋冪敤;濡傛灉鑾峰彇涓嶅埌鏃朵负绌哄瓧绗︿覆
	 */
	public static String HOST_IP = "";
	private static final String TAG = "NetUtil";

	static {
		refreshHostIP();
	}

	/**
	 * 妫�煡缃戠粶鏄惁鍙敤
	 * 
	 * @return 濡傛灉鍙敤锛岃繑鍥瀟rue锛屼笉鍙敤鍒欒繑鍥瀎alse
	 */
	public static boolean isNetworkAvailable() {
		Context con = DataStatistics.CONTEXT;
		ConnectivityManager cm = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}

		NetworkInfo[] info = cm.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 妫�煡缃戠粶鏄惁鍙敤
	 * 
	 * @return 濡傛灉鍙敤锛岃繑鍥瀟rue锛屼笉鍙敤鍒欒繑鍥瀎alse
	 */
	public static boolean isGPSAvailable() {
		boolean result;
		LocationManager locationManager = (LocationManager) DataStatistics.CONTEXT
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			result = true;
		} else {
			result = false;
		}
		DataStaMeilaLog.d(TAG, "result:" + result);

		return result;
	}

	/**
	 * @return 妫�煡wifi缃戠粶鏄惁鍙敤
	 */
	public static boolean isWifiAvailable() {
		WifiManager wm = (WifiManager) DataStatistics.CONTEXT
				.getSystemService(Context.WIFI_SERVICE);
		if (wm != null && wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			return true;
		}
		return false;
	}

	/**
	 * 閲嶆柊鑾峰彇瀹㈡埛绔疘P鍦板潃锛屽綋缃戠粶鍒囨崲鏃讹紝闇�璋冪敤璇ユ柟娉�
	 */
	public static void refreshHostIP() {
		HOST_IP = getHostIP();
	}

	/**
	 * 鑾峰彇IP鍦板潃
	 * 
	 * @return 杩斿洖瀹㈡埛绔疘P鍦板潃锛岃幏鍙栧湴鍧�笉鎴愬姛鏃讹紝杩斿洖绌哄瓧绗︿覆
	 */
	private static String getHostIP() {
		DataStaMeilaLog.d(TAG, "寮�鑾峰彇瀹㈡埛绔疘P鍦板潃");
		String hostIP = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			if (en != null) {
				while (en.hasMoreElements()) {
					NetworkInterface intf = en.nextElement();
					if (intf != null) {
						Enumeration<InetAddress> ipAddr = intf
								.getInetAddresses();
						while (ipAddr.hasMoreElements()) {
							InetAddress inetAddress = ipAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()) {
								hostIP = inetAddress.getHostAddress();
								break;
							}
						}
					}

					if (hostIP != null && !"".equals(hostIP)) {
						break;
					}
				}
			}
		} catch (Exception e) {
			DataStaMeilaLog.w(TAG, "涓鸿幏鍙栧埌璁块棶鐨処P鍦板潃");
		}

		DataStaMeilaLog.d(TAG, "鑾峰彇鐨勫鎴风IP鍦板潃:" + hostIP);
		return hostIP;
	}
	
	public static String getMac(Context con){
		//鍦╳ifi鏈紑鍚姸鎬佷笅锛屼粛鐒跺彲浠ヨ幏鍙朚AC鍦板潃锛屼絾鏄疘P鍦板潃蹇呴』鍦ㄥ凡杩炴帴鐘舵�涓嬪惁鍒欎负0
		String macAddress = "", ip = null;
		try{
			WifiManager wifiMgr = (WifiManager)con.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
			if (null != info) {
			    macAddress = info.getMacAddress();
			    ip = int2ip(info.getIpAddress());
			}
		}catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}
		DataStaMeilaLog.d(TAG, "mac:" + macAddress + ",ip:" + ip);
		return ""+macAddress;
	}
	public static long ip2int(String ip) {
	    String[] items = ip.split("\\.");
	    return Long.valueOf(items[0]) << 24
	            | Long.valueOf(items[1]) << 16
	            | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
	}
	
	public static String int2ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
	}
	
	@SuppressWarnings("deprecation")
	public static String urlEncode(String s){
		String charset = "UTF-8";
		try {
			String rs = URLEncoder.encode(s, charset);
			return rs;
		} catch (UnsupportedEncodingException e) {
			DataStaMeilaLog.e(TAG, e, true);
		}
		//涓嶆敮鎸佹寚瀹氱殑绫诲瀷锛屽氨鐢ㄧ郴缁熼粯璁ゆ敮鎸佺殑绫诲瀷锛堝悇涓郴缁熶細鏈変笉涓�牱锛�
		String rs = URLEncoder.encode(s);
		return rs;
	}
	
	@SuppressWarnings("deprecation")
	public static String urlDecode(String s){
		String charset = "UTF-8";
		try {
			String rs = URLDecoder.decode(s, charset);
			return rs;
		} catch (UnsupportedEncodingException e) {
			DataStaMeilaLog.e(TAG, e, true);
		}
		//涓嶆敮鎸佹寚瀹氱殑绫诲瀷锛屽氨鐢ㄧ郴缁熼粯璁ゆ敮鎸佺殑绫诲瀷锛堝悇涓郴缁熶細鏈変笉涓�牱锛�
		String rs = URLDecoder.decode(s);
		return rs;
	}
}
