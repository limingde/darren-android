package com.dd.whateat.net;

import com.dd.whateat.utils.NetUtil;

public class QueryString implements Comparable {
	public static final String TAG = "QueryString";
	
	private String name;
	private String value;
	
	@Override
	public String toString() {
		return name + "=" + value;
	}
	
	public String toStringAddAnd(boolean isAddAnd) {
		return  (isAddAnd ? "&" : "") + name + "=" + NetUtil.urlEncode(value);
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int compareTo(Object qs) {
		QueryString queryString = (QueryString) qs;
		String name1 = queryString.getName();
		String name2 = this.getName();

		return name1.compareTo(name2) < 0 ? 1 : -1; //字符串排序
	}
}
