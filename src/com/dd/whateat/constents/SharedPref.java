package com.dd.whateat.constents;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
	// app相关的
	private static SharedPreferences mAppPref = null;
	
	// 用户相关的
	private static SharedPreferences mUserPref = null;
	
	private final static String USER_PREF_KEY = "userPrefKey";
	
	public static SharedPreferences appPref(Context context) {
		if (mAppPref == null) {
			mAppPref = context.getSharedPreferences(DdConstants.APPLICATION_NAME, Activity.MODE_PRIVATE);
		}
		
		return mAppPref;
	}
	
	public static SharedPreferences UserPref(Context context) {
		if (mUserPref == null) {
			mUserPref = context.getSharedPreferences(SharedPref.userPrefKey(), Activity.MODE_PRIVATE);
		}
		
		return mUserPref;
	}
	
	public static String userPrefKey() {
		String key = USER_PREF_KEY;
		return key;
	}
}
