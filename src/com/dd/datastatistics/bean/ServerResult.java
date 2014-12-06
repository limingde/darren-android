package com.dd.datastatistics.bean;

/**
 * 服务顺返回的错误码的消息
 * 
 * @author Administrator
 * 
 */
public class ServerResult {

	@Override
	public String toString() {
		// return "ret: " + ret+", msg: "+msg;
		return "" + msg;
	}

	public ServerResult() {
		ret = -1;
	}

	public String msg;
	public int ret;
	public Object obj;
	public Object obj2;
	public Object obj3;
	public Object obj4;
	public Object obj5;
	public Object obj6;
	public Object obj7;
}
