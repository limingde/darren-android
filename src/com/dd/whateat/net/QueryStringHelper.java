package com.dd.whateat.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;

import android.text.TextUtils;

import com.dd.whateat.utils.DdLog;
import com.dd.whateat.utils.NetUtil;
import com.pinhui.tools.sig.SigUtils;

public class QueryStringHelper {
	final String TAG = "QueryStringHelper";
	
	private List<QueryString> list;
	private List<NameValuePair> postParams;
	private String key;
	private String path;
	private String urlQueryString;
	private String sigQueryString;

	public QueryStringHelper(QueryStringHelper other) {
		if(other == null){
			return;
		}
		
		if(other.list != null){
			list = new ArrayList<QueryString>();
			list.addAll(other.list);
		}
		
		if(other.postParams != null){
			postParams = new ArrayList<NameValuePair>();
			postParams.addAll(other.postParams);
		}
		
		key = other.key;
		path = other.path;
		urlQueryString = other.urlQueryString;
		sigQueryString = other.sigQueryString;
	}
	public QueryStringHelper(String key, String path) {

		this.list = new ArrayList<QueryString>();
		this.key = key;
		this.path = path;
	}
	
	public QueryStringHelper() {
		this.list = new ArrayList<QueryString>();
	}

	public boolean add(String name, String value) {
		if(name == null || value == null){
			DdLog.e(TAG, "add param, name or value is null, name: "+name+", value: "+value);
			return false;
		}
		
		QueryString qs = new QueryString();
		qs.setName(name);
		qs.setValue(value);

		this.list.add(qs);
		return true;
	}
	
	public void resetKey(String key){
		this.key = key;
	}
	public void setPostParams(List<NameValuePair> postParams) {
		this.postParams = postParams;
	}

	public String getUrlQueryString() {
		urlQueryString = "";

		if (this.list != null) {
			Collections.sort(this.list);

			for (int i = 0; i < this.list.size(); i++) {
				urlQueryString += this.list.get(i).toStringAddAnd(
						i == 0 ? false : true);
			}
		}
		DdLog.d(TAG, "getUrlQueryString: "+urlQueryString);
		
		return urlQueryString;
	}

	private String getSigQueryString() {

		String sig = SigUtils.getSig(path, key, urlQueryString, getPostParams());
		
//		DdLog.d(TAG, "key = " + key + ", url = " + url + ", sig=" + sig);
//		Log.d("加密段", "sig=" + sig);
		
		
		QueryString qs = new QueryString();
		qs.setName("sig");
		qs.setValue(sig);
		if (!TextUtils.isEmpty(urlQueryString)) {
			sigQueryString = qs.toStringAddAnd(true);
		} else {
			sigQueryString = qs.toStringAddAnd(false);
		}

		return sigQueryString;
	}
	
	private String getPostParams() {
		if (postParams != null && postParams.size() > 0) {
			int size = postParams.size();
			StringBuffer postParamsString = new StringBuffer();
			for (int i=0; i<size; i++) {
				try{
				NameValuePair tmp = postParams.get(i);
				postParamsString.append(tmp.getName());
				postParamsString.append("=");
				postParamsString.append(NetUtil.urlEncode(tmp.getValue()));
				DdLog.d(TAG, "getPostParams, key: "+tmp.getName()+", val: "+tmp.getValue());
				printByte(tmp.getValue().getBytes());
				
				
				// 最后一个不加&
				if (i < size - 1) {					
					postParamsString.append("&");
				}
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			if (!TextUtils.isEmpty(postParamsString)) {
				DdLog.e("getPostParams", postParamsString.toString());
				return postParamsString.toString();
			}
		}
		return "";
	}
	
	public String getResultQueryString()
	{
		return getUrlQueryString() + getSigQueryString();
	}
	
	public void printByte(final byte[] bytes) {
		if (bytes == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; ++i) {
			sb.append(String.format("%2x ", bytes[i]));
		}
		DdLog.d(TAG, sb.toString());
		sb = null;
	}
}
