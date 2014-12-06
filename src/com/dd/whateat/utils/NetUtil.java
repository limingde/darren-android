/**
 * 网络辅助类
 */
package com.dd.whateat.utils;

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

import com.dd.whateat.DdApplication;

/**
 * @author tom
 * 
 */
public class NetUtil {

	/**
	 * 客户端IP地址，当网络切换时，需要重新调用;如果获取不到时为空字符串
	 */
	public static String HOST_IP = "";
	private static final String TAG = "NetUtil";

	static {
		refreshHostIP();
	}

	/**
	 * 检查网络是否可用
	 * 
	 * @return 如果可用，返回true，不可用则返回false
	 */
	public static boolean isNetworkAvailable() {
		Context con = DdApplication.CONTEXT;
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
	 * 检查网络是否可用
	 * 
	 * @return 如果可用，返回true，不可用则返回false
	 */
	public static boolean isGPSAvailable() {
		boolean result;
		LocationManager locationManager = (LocationManager) DdApplication.CONTEXT
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			result = true;
		} else {
			result = false;
		}
		DdLog.d(TAG, "result:" + result);

		return result;
	}

	/**
	 * @return 检查wifi网络是否可用
	 */
	public static boolean isWifiAvailable() {
		WifiManager wm = (WifiManager) DdApplication.CONTEXT
				.getSystemService(Context.WIFI_SERVICE);
		if (wm != null && wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			return true;
		}
		return false;
	}

	/**
	 * 重新获取客户端IP地址，当网络切换时，需要调用该方法
	 */
	public static void refreshHostIP() {
		HOST_IP = getHostIP();
	}

	/**
	 * 获取IP地址
	 * 
	 * @return 返回客户端IP地址，获取地址不成功时，返回空字符串
	 */
	private static String getHostIP() {
		DdLog.d(TAG, "开始获取客户端IP地址");
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
			DdLog.w(TAG, "为获取到访问的IP地址");
		}

		DdLog.d(TAG, "获取的客户端IP地址:" + hostIP);
		return hostIP;
	}
	
	public static String getMac(Context con){
		//在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
		String macAddress = "", ip = null;
		try{
			WifiManager wifiMgr = (WifiManager)con.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
			if (null != info) {
			    macAddress = info.getMacAddress();
			    ip = int2ip(info.getIpAddress());
			}
		}catch (Exception e) {
			DdLog.e(TAG, e);
		}
		DdLog.d(TAG, "mac:" + macAddress + ",ip:" + ip);
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
	
	public static String urlEncode(String s){
		String charset = "UTF-8";
		try {
			String rs = URLEncoder.encode(s, charset);
			return rs;
		} catch (UnsupportedEncodingException e) {
			DdLog.e(TAG, e, true);
		}
		//不支持指定的类型，就用系统默认支持的类型（各个系统会有不一样）
		String rs = URLEncoder.encode(s);
		return rs;
	}
	
	public static String urlDecode(String s){
		String charset = "UTF-8";
		try {
			String rs = URLDecoder.decode(s, charset);
			return rs;
		} catch (UnsupportedEncodingException e) {
			DdLog.e(TAG, e, true);
		}
		//不支持指定的类型，就用系统默认支持的类型（各个系统会有不一样）
		String rs = URLDecoder.decode(s);
		return rs;
	}
}
