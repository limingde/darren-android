package com.dd.whateat.bean;

import java.io.Serializable;

import org.json.JSONArray;

public class JSONArrayBaseBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "BaseBean [msg=" + msg + ", ret=" + String.valueOf(ret) + ",data=" + String.valueOf(data) + "]";
	}
	
	private String msg;
	private JSONArray data;
	private int ret;
	
	public String getMsg() {
		return msg;
	}
	
	public JSONArray getData() {
		return data;
	}
	
	public int getRet() {
		return ret;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void setData(JSONArray data) {
		this.data = data;
	}
	
	public void setRet(int ret) {
		this.ret = ret;
	}
}
