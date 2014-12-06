package com.dd.datastatistics.bean;

import org.json.JSONObject;

public class JSONObjectBaseBean {
	@Override
	public String toString() {
		return "BaseBean [msg=" + msg + ", ret=" + String.valueOf(ret) + ",data=" + String.valueOf(data) + "]";
	}
	
	private String msg;
	private JSONObject data;
	private int ret;
	
	public String getMsg() {
		return msg;
	}
	
	public JSONObject getData() {
		return data;
	}
	
	public int getRet() {
		return ret;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void setData(JSONObject data) {
		this.data = data;
	}
	
	public void setRet(int ret) {
		this.ret = ret;
	}
}
