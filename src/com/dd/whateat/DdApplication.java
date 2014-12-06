package com.dd.whateat;

import org.apache.http.HttpHost;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dd.whateat.utils.DdLog;
public class DdApplication extends Application {
	private static final String TAG = "DdApplication";
	public static DdApplication CONTEXT;
	public RequestQueue requestQueue;
	private static String apnType = "";
	
	public static int mScreenWidth = 0;
	public static int mScreenHeight = 0;
	public static Context getContext() {
		if (CONTEXT != null)
			return CONTEXT.getApplicationContext();
		else
			return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		CONTEXT = this;
		DisplayMetrics mDisplayMetrics = getApplicationContext().getResources().getDisplayMetrics();
		DdApplication.mScreenWidth = mDisplayMetrics.widthPixels;
		DdApplication.mScreenHeight = mDisplayMetrics.heightPixels;
		requestQueue = Volley.newRequestQueue(DdApplication.CONTEXT);
	}
	

	public final static HttpHost getHttpProxy() {
		HttpHost httpHost = null;
		try {
			ConnectivityManager cm = (ConnectivityManager) getContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isAvailable()) {
				String typeName = netInfo.getTypeName();
				String extra = netInfo.getExtraInfo();
				if (typeName != null && typeName.equalsIgnoreCase("MOBILE")) {
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
				} else if (typeName != null && (typeName.equalsIgnoreCase("WIFI")
						|| typeName.equalsIgnoreCase("WI FI"))) {
					apnType = "wifi";
				}
			}
		} catch (Exception e) {
			DdLog.e(TAG, e);
			httpHost = null;
			apnType = "unknown";
		}
		return httpHost;
	}
}
