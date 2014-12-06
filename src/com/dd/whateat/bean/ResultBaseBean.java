package com.dd.whateat.bean;
public class ResultBaseBean implements DataStaMeilaErrorCode{

	@Override
	public String toString() {
		return "BaseBean [msg=" + msg + ", ret=" + String.valueOf(ret)
				+ ",data=" + String.valueOf(data) + "]";
	}

	public String msg;
	public String data;
	public int ret;
	
}
