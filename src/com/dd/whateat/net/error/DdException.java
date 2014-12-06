package com.dd.whateat.net.error;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

public class DdException extends Exception {

	List<ErrorMsg> errMsgList = new ArrayList<ErrorMsg>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 6152500776465875785L;
	private String mExtra;

	public DdException(Exception e) {
		super(e);
	}

	public DdException(String message) {
		super(message);
	}

	public DdException(String message, String extra) {
		super(message);
		mExtra = extra;
	}

	public String getExtra() {
		return mExtra;
	}
	
	public void put(String key, String msgVal){
		if(TextUtils.isEmpty(key) || TextUtils.isEmpty(msgVal)){
			return;
		}
		
		ErrorMsg msg = new ErrorMsg(key, msgVal); 
		errMsgList.add(msg);
	}
	public List<ErrorMsg> getErrorMsgList(){
		return errMsgList;
	}

}
