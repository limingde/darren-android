package com.dd.datastatistics.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;

import android.text.TextUtils;

import com.dd.datastatistics.bean.DataStaQueryString;
import com.dd.datastatistics.util.DataStaNetUtil;
import com.pinhui.tools.sig.SigUtils;

public class DataQueryStringHelper {
	final String TAG = "QueryStringHelper";
	
	private List<DataStaQueryString> list;
	private List<NameValuePair> postParams;
	private String key;
	private String path;
	private String urlQueryString;
	private String sigQueryString;

	public DataQueryStringHelper(DataQueryStringHelper other) {
		if(other == null){
			return;
		}
		
		if(other.list != null){
			list = new ArrayList<DataStaQueryString>();
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
	public DataQueryStringHelper(String key, String path) {

		this.list = new ArrayList<DataStaQueryString>();
		this.key = key;
		this.path = path;
	}
	
	public DataQueryStringHelper() {
		this.list = new ArrayList<DataStaQueryString>();
	}

	public boolean add(String name, String value) {
		if(name == null || value == null){
			
			return false;
		}
		
		DataStaQueryString qs = new DataStaQueryString();
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

	@SuppressWarnings("unchecked")
	public String getUrlQueryString() {
		urlQueryString = "";

		if (this.list != null) {
			Collections.sort(this.list);

			for (int i = 0; i < this.list.size(); i++) {
				urlQueryString += this.list.get(i).toStringAddAnd(
						i == 0 ? false : true);
			}
		}
		
		return urlQueryString;
	}

	private String getSigQueryString() {

		String sig = SigUtils.getSig(path, key, urlQueryString, getPostParams());
		
//		MeilaLog.d(TAG, "key = " + key + ", url = " + url + ", sig=" + sig);
//		Log.d("鍔犲瘑娈�, "sig=" + sig);
		
		
		DataStaQueryString qs = new DataStaQueryString();
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
				postParamsString.append(DataStaNetUtil.urlEncode(tmp.getValue()));
				printByte(tmp.getValue().getBytes());
				
				
				// 鏈�悗涓�釜涓嶅姞&
				if (i < size - 1) {					
					postParamsString.append("&");
				}
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			if (!TextUtils.isEmpty(postParamsString)) {
				return postParamsString.toString();
			}
		}
		return "";
	}
	
	public String getResultQueryString()
	{
		return getUrlQueryString();
	}
	
	public void printByte(final byte[] bytes) {
		if (bytes == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; ++i) {
			sb.append(String.format("%2x ", bytes[i]));
		}
		sb = null;
	}
}
