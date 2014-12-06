package com.dd.datastatistics.bean;

import com.dd.datastatistics.orm.DataStaBaseModel;

public class BtnClickEventData extends DataStaBaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String TAG = "BtnClickEventData";

	
	public final static String COL_UPDATE_TIME = "updateTime";
	public long updateTime;
	public String data = "";
}
