package com.dd.whateat.utils;

public class StringUtils {
	final static String TAG = "StringUtils";
	
	public static String truncateZero(String s){
		try{
			double d = Double.parseDouble(s);
			if(d*10%10 == 0){
				return ""+((int)d);
			}
		}catch (Exception e) {
			DdLog.e(TAG, e);
		}
		return s;
	}
}
