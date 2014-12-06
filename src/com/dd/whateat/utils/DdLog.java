package com.dd.whateat.utils;

import java.util.List;

import android.util.Log;

import com.dd.whateat.comfig.DdConfig;
import com.dd.whateat.net.error.DdException;
import com.dd.whateat.net.error.ErrorMsg;

public class DdLog {
	public static void i(String tag, String msg) {
		try {
			if (DdConfig.DD_DEBUG_LOG) {
				Log.i(tag, msg);
			}
		} catch (Exception e) {
		}
	}
	
	public static void d(String tag, String msg) {
		try {
			if (DdConfig.DD_DEBUG_LOG) {
				Log.d(tag, msg);
			}
		} catch (Exception e) {
		}
	}

	public static void w(String tag, String msg) {
		try {
			if (DdConfig.DD_DEBUG_LOG) {
				Log.w(tag, msg);
			}
		} catch (Exception e) {
		}
	}

	public static void e(String tag, String msg) {
		try {
			if (DdConfig.DD_DEBUG_LOG) {
				Log.e(tag, msg);
			}
		} catch (Exception e) {
		}
	}

	public static void e(String tag, Throwable throwable, String additionalMsg) {
		try {
			if (DdConfig.DD_DEBUG_LOG) {
				e(tag, throwable, additionalMsg, false);
			}
		} catch (Exception e) {
		}
	}
	
	public static void e(String tag, Throwable throwable) {
		try {
			if (DdConfig.DD_DEBUG_LOG) {
				e(tag, throwable, false);
			}
		} catch (Exception e) {
		}
	}
	
	public static void e(String tag, Throwable throwable, boolean upload) {
		try {
			if (DdConfig.DD_DEBUG_LOG) {
				e(tag, throwable, null, false);
			}
		} catch (Exception e) {
		}
	}
	
	public static void e(String tag, Throwable throwable, String additionalMsg, boolean upload) {
		try {
			if (DdConfig.DD_DEBUG_LOG && additionalMsg != null) {
				Log.e(tag, additionalMsg);
			}
			
			String errMsg = "";
			if(throwable instanceof DdException){
				DdException me = (DdException) throwable;
				List<ErrorMsg> emList = me.getErrorMsgList();
				if(emList != null && emList.size()>0){
					for(int i=0; i<emList.size(); ++i){
						ErrorMsg em = emList.get(i);
						errMsg += (em.key+": "+em.msgVal+"\r\n");
					}
				}
			}
			
			
			String errString = errMsg;

			if (DdConfig.DD_DEBUG_LOG && errString != null) {
				Log.e(tag, errString);
			}
		} catch (Exception e) {
		}
	}
}
