package com.dd.whateat.net;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.dd.datastatistics.bean.ResultBaseBean;
import com.dd.datastatistics.bean.ServerResult;
import com.dd.whateat.DdApplication;
import com.dd.whateat.MainActivity;
import com.dd.whateat.R;
import com.dd.whateat.bean.DdConst;
import com.dd.whateat.bean.DdErrorCode;
import com.dd.whateat.bean.GainScore;
import com.dd.whateat.bean.ResponseCacheItemModel;
import com.dd.whateat.comfig.DdConfig;
import com.dd.whateat.net.error.DdException;
import com.dd.whateat.utils.DdLog;
import com.dd.whateat.utils.DdResource;
import com.dd.whateat.utils.FConfigUtil;
import com.dd.whateat.utils.GZIPUtil;
import com.dd.whateat.utils.NetUtil;

public class SendRequest {

	/**
	 * 这个版本发现一个bug：有时候第一次请求后台数据，时间会很长，有时甚至超时，但是如果第二次再请求，就快了。
	 * 这里考虑将连接超时时间依次递增，尽快重试第二次。暂时没有考虑socket_timeout的问题
	 * 暂时不涉及支付，不会造成多次付款的问题
	 */
	private static final int CONNECT_TIMEOUT_START_IN_MS = 5000;//初始连接超时时间
	private static final int CONNECT_TIMEOUT_MAX_IN_MS = 17000;//最大连接超时时间5+10+15=30
	private static final int READ_DATA_TIMEOUT_IN_MS = 30000;//读数据超时时间

	private static String CHARSET = "UTF-8";
	private static int count_retry_getkey = 0;
	final static int max_retry_getkey = 2;
	final static int RETRY_DELAY_IN_MS = 100;
	private static String key = "";
	private static final String TAG = "SendRequest";
	public static final String MUD = "Mud";
	volatile public static String mudVal = "";
	private static final String EtagRequest = "If-None-Match";
	private static final String EtagResponse = "Etag";

	final static boolean accept_gzip = true;
	static final long SO_LONG_in_ms = 2000;

	static int requestCount = 0;

	static ClientConnectionManager manager;

	// fromCache可以直接返回已经存储的内容，不做网络请求
	public static String connServerForResultFromCache(String host, String path,
			final QueryStringHelper qsh, boolean fromCache) {
		return connServerForResult(host, path, qsh, CONNECT_TIMEOUT_START_IN_MS, true, true, true, fromCache);
	}

	public static String connServerForResult(String host, String path,
			final QueryStringHelper qsh) {
		return connServerForResult(host, path, qsh, CONNECT_TIMEOUT_START_IN_MS, true, true, true, false);
	}
	public static String connServerForResult(String host, String path,
			final QueryStringHelper qsh, boolean relogin) {
		return connServerForResult(host, path, qsh, CONNECT_TIMEOUT_START_IN_MS, true, true, relogin, false);
	}

	static class VolleyRs{
		String rs;
	}
	/**
	 * 解决以下问题：http返回408，重试之后请求不返回，而且超时失效
	 * @param url
	 * @param mud
	 * @return
	 */
	public static String getByVolley(final String url, final String mud) {
		final VolleyRs rs = new VolleyRs();
		StringRequest sRequest = new StringRequest(Request.Method.GET, url,
				new Listener<String>() {
			@Override
			public void onResponse(String response) {
				rs.rs = response;
				synchronized (rs) {
					rs.notifyAll();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				synchronized (rs) {
					rs.notifyAll();
				}
			}
		}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				// return super.getHeaders();
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put(MUD, mudVal);
				if (accept_gzip) {
					headers.put("Accept-Encoding", "gzip");
				}
				return headers;
			}

			@Override
			public RetryPolicy getRetryPolicy() {
				RetryPolicy retryPolicy = new DefaultRetryPolicy(
						DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
						DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
				return retryPolicy;
			}

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				try {
					if (GZIPUtil.isGzipFormat(response.data)) {
						String str = GZIPUtil.uncompressToString(response.data);
						return Response.success(str,
								HttpHeaderParser.parseCacheHeaders(response));
					} else {
						return super.parseNetworkResponse(response);
					}
				} catch (Exception e) {
					return Response.error(new ParseError(e));
				}
			}
		};
		sRequest.setShouldCache(false);
		DdApplication.CONTEXT.requestQueue.add(sRequest);

		synchronized (rs) {
			try {
				rs.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return rs.rs;
	}



	/**
	 * 
	 * @param host
	 * @param path
	 * @param urlQueryString
	 * @param enableCache 是否生效缓存
	 * @param returnCache 如果服务器没有返回304时，是否返回缓存的数据
	 * @return
	 */
	public static String connServerForResult(final String host, final String path,
			final QueryStringHelper qsh_passin, final int conn_timeout_in_ms, final boolean enableCache, final boolean returnCache, 
			final boolean relogin, final boolean fromCache) {
		QueryStringHelper qsh = new QueryStringHelper(qsh_passin);

		String urlQueryString = qsh.getResultQueryString();
		int count = (++requestCount);
		String url = host + path + "?" + urlQueryString;
		long startTime = System.currentTimeMillis();
		DdLog.d(TAG, "get, requestCount: "+count+", url: "+url);
		boolean hasEtag = false;


		//打开调试日志，参考http://hc.apache.org/httpclient-3.x/logging.html
		//		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		//		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
		//		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
		//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");

		//build the cacheitem
		ResponseCacheItemModel cacheItem = ResponseCacheItemModel.getCacheItem(url);
		// 如何直接需要拿cache的话就直接返回
		if (fromCache) {
			if (cacheItem != null) {
				return cacheItem.response;
			}

			return null;
		}
		if(cacheItem != null && cacheItem.etag != null && !cacheItem.etag.trim().equals("")){
			hasEtag = true;
		}else{
			cacheItem = new ResponseCacheItemModel();
		}
		cacheItem.url = url;

		// HttpGet对象
		HttpGet httpRequest = new HttpGet(url);
		DdLog.d(TAG, "HTTP-URL:" + url);
		httpRequest.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);		
		Log.i("<<<<<<<<<<<<<<<<>>>>>>>>>url:", url);
		String strJSON = "";
		//		AndroidHttpClient httpClient = null;//不能使用ThreadSafeClientConnManager
		DefaultHttpClient httpClient = null;//可以使用ThreadSafeClientConnManager
		try {
			// HttpClient对象

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, conn_timeout_in_ms);
			HttpConnectionParams.setSoTimeout(httpParameters, READ_DATA_TIMEOUT_IN_MS);

			//			httpParameters.setParameter(ConnRouteParams.DEFAULT_PROXY, null);
			//			HttpClient httpClient = new DefaultHttpClient(httpParameters);
			//			httpClient = AndroidHttpClient.newInstance("android");

			if (manager == null) {
				SchemeRegistry schReg = new SchemeRegistry();
				schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));//使用http协议的80端口，如果端口号变动，需要再添加一句register
				manager = new ThreadSafeClientConnManager(httpParameters, schReg);
			}
			httpClient = new DefaultHttpClient(manager, httpParameters);

			httpRequest.setParams(httpParameters);
			httpRequest.setHeader(MUD, mudVal);
			//			httpRequest.setHeader("Connection", "keep-alive");
			//			httpRequest.setHeader("Cache-Control", "max-age=0");
			if(DdConfig.ENABLE_HTTP_CACHE && enableCache && hasEtag){
				httpRequest.setHeader(EtagRequest, cacheItem.etag);
			}

			// 添加屏幕的尺寸以及网络状态信息
			if (DdApplication.mScreenWidth > 0) {
				httpRequest.setHeader("SW", String.valueOf(DdApplication.mScreenWidth));
			}

			if (DdApplication.mScreenHeight > 0) {
				httpRequest.setHeader("SH", String.valueOf(DdApplication.mScreenHeight));
			}

			if (NetUtil.isWifiAvailable()) {
				httpRequest.setHeader("NetS", "0");
			} else {
				httpRequest.setHeader("NetS", "1");
			}


			if(accept_gzip){
				httpRequest.addHeader("Accept-Encoding", "gzip");
				//				httpClient.modifyRequestToAcceptGzipResponse(httpRequest);
			}

			// 获得HttpResponse对象
			long t_exec1 = System.currentTimeMillis();

			DdLog.d("testvtalk", "before");
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			DdLog.d("testvtalk", "after");

			long exec_usetime = System.currentTimeMillis() - t_exec1;
			if(exec_usetime>SO_LONG_in_ms){
				DdLog.e(TAG, "execute end, use time: "+exec_usetime+" ms, requestCount: "+count+", url: "+url);
			}else{
				DdLog.d(TAG, "execute end, use time: "+exec_usetime+" ms, requestCount: "+count+", url: "+url);
			}

			--requestCount;
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				saveMUD(httpResponse);
				cacheItem.etag = getEtag(httpResponse);

				// 取得返回的数据
				if(accept_gzip){
					DdLog.d(TAG, "get response before gzip");
					strJSON = getJsonStringFromGZIP(httpResponse);
					//					strJSON = InputStreamUtils.InputStreamTOString(httpClient.getUngzippedContent(httpResponse.getEntity()));
				}else{					
					strJSON = EntityUtils.toString(httpResponse.getEntity());
				}

				if(enableCache){
					cacheItem.response = strJSON;
					cacheItem.save();
				}

				ResultBaseBean result = null;
				if (!TextUtils.isEmpty(strJSON)) {
					result = JSON.parseObject(strJSON, ResultBaseBean.class);
				}
				if (result != null && result.ret == ResultBaseBean.ret_key_timeout) {
					++count_retry_getkey;
					if(count_retry_getkey<max_retry_getkey){
						DdLog.d(TAG, "retry getkey, count_retry_getkey: "+count_retry_getkey);
						waitSeconds();
						resetKey();
						qsh_passin.resetKey(getKey(false));
						strJSON = connServerForResult(host, path, qsh_passin, conn_timeout_in_ms, enableCache, returnCache, relogin, false);
					}
				}else if (result != null && result.ret == ResultBaseBean.ret_login_timeout && relogin) {
//					if(MainActivity.INSTANCE != null){
//						MainActivity.INSTANCE.relogin();
//					}
				}else{
					if(!url.contains("/get_key")){
						count_retry_getkey = 0;
					}
				}
			} else if(statusCode == HttpStatus.SC_NOT_MODIFIED){
				DdLog.e(TAG, "connServerForResult 304, not modified, " + url);
				if(returnCache){
					strJSON = cacheItem.response;
				}
			} else if(statusCode == HttpStatus.SC_REQUEST_TIMEOUT){
				DdLog.e(TAG, "server return 408");
				//				throw new ConnectTimeoutException("server return 408");//如果抛出异常进行重试，会接着返回408，然后再重试，请求就不会返回了，超时也会失效
				strJSON = getByVolley(url, mudVal);

			}else{
				DdLog.e(TAG, "connServerForResult error, statusCode: "
						+ statusCode + ", " + url);
				if(returnCache){
					strJSON = cacheItem.response;
				}
			}
		} catch (ConnectTimeoutException e) {
			DdLog.e(TAG, "ConnectTimeoutException, timeout: "+conn_timeout_in_ms+", max_timeout: "+CONNECT_TIMEOUT_MAX_IN_MS);
			//如果有网络但是关掉代理，而且有缓存，就使用缓存，不重试
			if(returnCache && cacheItem != null && !TextUtils.isEmpty(cacheItem.response)){
				strJSON = cacheItem.response;

			}
			//否则，一般的超时，会有重试的机会
			else{
				if(conn_timeout_in_ms<=CONNECT_TIMEOUT_MAX_IN_MS){
					DdLog.e(TAG, "ConnectTimeoutException, retry connect: "+url);
					waitSeconds();
					strJSON = connServerForResult(host, path, qsh_passin, conn_timeout_in_ms*2, enableCache, returnCache, relogin, false);
				}else{
					DdLog.e(TAG, "ConnectTimeoutException, cannot retry connect: "+url);
					DdLog.e(TAG, e, false);
				}
			}
		} catch (ClientProtocolException e) {
			DdLog.e(TAG, e, false);
		} catch (IOException e) {
			DdLog.e(TAG, e, false);
			//如果断网，使用缓存
			if(returnCache){
				strJSON = cacheItem.response;
			}
		} catch (Exception e) {
			//			DdLog.e(TAG, e, true);
			DdException e2 = new DdException(e);
			e2.put("url", url);
			e2.put("mud", mudVal);
			e2.put("response", strJSON);
			DdLog.e(TAG, e2, true);
		}finally{
			try{
				if(httpClient != null){
					//					httpClient.close();
				}
			}catch (Throwable e) {
				DdLog.e(TAG, e);
			}
		}

		long useTime = System.currentTimeMillis() - startTime;
		if(useTime>SO_LONG_in_ms){
			DdLog.e(TAG, "get end, use time: "+useTime+" ms, requestCount: "+count+", url: "+url);
		}else{
			DdLog.d(TAG, "get end, use time: "+useTime+" ms, requestCount: "+count+", url: "+url);
		}
		return strJSON;
	}
	static void waitSeconds(){
		try {
			Thread.sleep(RETRY_DELAY_IN_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	public static String connServerPostForResult(String host, String path, final QueryStringHelper qsh_passin, 
			List<NameValuePair> postParams) {
		return connServerPostForResult(host, path, qsh_passin, CONNECT_TIMEOUT_START_IN_MS, postParams);
	}
	public static String connServerPostForResult(String host, String path, final QueryStringHelper qsh_passin, final int conn_timeout_in_ms,
			List<NameValuePair> postParams) {
		int count = (++requestCount);

		//添加统一的必须的参数
		QueryStringHelper qsh = new QueryStringHelper(qsh_passin);
		qsh.add(DdResource.CLIENT_ID_NAME, DdResource.getUniqueId());
		qsh.add(DdResource.VERSION_NAME, DdResource.getApplicationVersionCode());

		String sig = qsh.getResultQueryString();
		String url = host + path + "?" + sig;

		long startTime = System.currentTimeMillis();
		DdLog.d(TAG, "post, requestCount: "+count+", url: "+url);
		// HttpGet对象
		HttpPost httpRequest = new HttpPost(url);
		//		AndroidHttpClient httpClient = null;
		DefaultHttpClient httpClient = null;
		String strJSON = "";
		try {
			// HttpClient对象
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, conn_timeout_in_ms);
			HttpConnectionParams.setSoTimeout(httpParameters, READ_DATA_TIMEOUT_IN_MS);

			httpRequest.setHeader(MUD, mudVal);
			if(accept_gzip){
				httpRequest.addHeader("Accept-Encoding", "gzip");
				//				httpClient.modifyRequestToAcceptGzipResponse(httpRequest);
			}

			if (postParams != null && postParams.size() > 0) {
				printPostData(postParams);
				try {
					HttpEntity httpentity = new UrlEncodedFormEntity(postParams, CHARSET);
					httpRequest.setEntity(httpentity);
				} catch (UnsupportedEncodingException e) {
					DdLog.e(TAG, e);
				}
			}

			// 获得HttpResponse对象
			//			httpClient = AndroidHttpClient.newInstance("android");
			if (manager == null) {
				SchemeRegistry schReg = new SchemeRegistry();
				schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));//使用http协议的80端口，如果端口号变动，需要再添加一句register
				manager = new ThreadSafeClientConnManager(httpParameters, schReg);
			}
			httpClient = new DefaultHttpClient(manager, httpParameters);

			HttpResponse httpResponse = httpClient.execute(httpRequest);
			--requestCount;
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				saveMUD(httpResponse);

				// 取得返回的数据
				if(accept_gzip){
					DdLog.d(TAG, "get response before gzip");
					strJSON = getJsonStringFromGZIP(httpResponse);
					//					strJSON = InputStreamUtils.InputStreamTOString(httpClient.getUngzippedContent(httpResponse.getEntity()));
				}else{					
					strJSON = EntityUtils.toString(httpResponse.getEntity());
				}


				ResultBaseBean result = JSON.parseObject(strJSON, ResultBaseBean.class);
				if (result != null && result.ret == ResultBaseBean.ret_key_timeout) {
					++count_retry_getkey;
					if(count_retry_getkey<max_retry_getkey){
						DdLog.d(TAG, "retry getkey, count_retry_getkey: "+count_retry_getkey);
						waitSeconds();
						resetKey();
						qsh_passin.resetKey(getKey(false));
						strJSON = connServerPostForResult(host, path, qsh_passin, conn_timeout_in_ms, postParams);
					}
				}else if (result != null && result.ret == ResultBaseBean.ret_login_timeout) {
//					if(MainActivity.INSTANCE != null){
//						MainActivity.INSTANCE.relogin();
//					}
				}else {
					count_retry_getkey = 0;
				}
			} else {
				DdLog.e(TAG, "connServerPostForResult error, statusCode: "
						+ statusCode);
			}
		} catch (ConnectTimeoutException e) {
			if(conn_timeout_in_ms<=CONNECT_TIMEOUT_MAX_IN_MS){
				DdLog.e(TAG, "ConnectTimeoutException, timeout: "+conn_timeout_in_ms+", max_timeout: "+CONNECT_TIMEOUT_MAX_IN_MS+", retry connect: "+url);
				waitSeconds();
				strJSON = connServerPostForResult(host, path, qsh_passin, conn_timeout_in_ms*2, postParams);
			}else{
				DdLog.e(TAG, e, false);				
			}
		} catch (ClientProtocolException e) {
			DdLog.e(TAG, e, false);
		} catch (Exception e) {
			DdException e2 = new DdException(e);
			e2.put("url", url);
			e2.put("mud", mudVal);
			e2.put("response", strJSON);
			DdLog.e(TAG, e2, true);
		}finally{
			try{
				if(httpClient != null){
					//					httpClient.close();
				}
			}catch (Throwable e) {
				DdLog.e(TAG, e);
			}
		}		

		long useTime = System.currentTimeMillis() - startTime;
		if(useTime>SO_LONG_in_ms){
			DdLog.e(TAG, "post end, use time: "+useTime+" ms, requestCount: "+count+", url: "+url);
		}else{
			DdLog.d(TAG, "post end, use time: "+useTime+" ms, requestCount: "+count+", url: "+url);
		}

		GainScore.parseGainScore(strJSON);
		return strJSON;
	}

	private static String getJsonStringFromGZIP(HttpResponse response) {
		String jsonString = null;
		try {
			InputStream is = response.getEntity().getContent();
			BufferedInputStream bis = new BufferedInputStream(is);
			bis.mark(2);
			// 取前两个字节
			byte[] header = new byte[2];
			int result = bis.read(header);
			// reset输入流到开始位置
			bis.reset();
			// 判断是否是GZIP格式
			int headerData = getShort(header);
			// Gzip 流 的前两个字节是 0x1f8b
			if (result != -1 && headerData == 0x1f8b) { 
				DdLog.d("HttpTask", " use GZIPInputStream  ");
				is = new GZIPInputStream(bis);
			} else {
				DdLog.d("HttpTask", " not use GZIPInputStream");
				is = bis;
			}
			InputStreamReader reader = new InputStreamReader(is, "utf-8");
			char[] data = new char[100];
			int readSize;
			StringBuffer sb = new StringBuffer();
			while ((readSize = reader.read(data)) > 0) {
				sb.append(data, 0, readSize);
			}
			jsonString = sb.toString();
			bis.close();
			reader.close();
		} catch (Exception e) {
			DdLog.e("HttpTask", e);
		}

		DdLog.d("HttpTask", "getJsonStringFromGZIP net output : " + jsonString );
		return jsonString;
	}

	/**
	 * 参考 ，注意实际使用中，我发现gzip 流前两个字节是0x1e8b ，不是0x1f8b .后来检查一下code ，代码处理错误，加上第二个字节的时候需 &0xFF
	 * 0x1f8b 可参考标准 http://www.gzip.org/zlib/rfc-gzip.html#file-format
	 * @param data
	 * @return
	 */
	private static int getShort(byte[] data) {
		return (int)((data[0]<<8) | data[1]&0xFF);
	}

	static ServerResult jsonStrToServerResult(String strJSON, Class<?> cls){
		if(TextUtils.isEmpty(strJSON)){
			return null;
		}

		ServerResult rs = new ServerResult();
		try {
			ResultBaseBean result = JSON.parseObject(strJSON, ResultBaseBean.class);
			rs.ret = result.ret;
			rs.msg = result.msg;

			if (result.ret == DdErrorCode.ret_ok) {
				if(cls != null){
					rs.obj = JSON.parseObject(result.data, cls);
				}else{
					DdLog.e(TAG, "jsonStrToServerResult, no class");
				}
				return rs;
			}
		} catch (Exception e) {
			DdLog.e(TAG, e, false);
		}
		return rs;
	}

	private static void printPostData(List<NameValuePair> params) {
		if (params == null || params.size() < 1) {
			return;
		}
		DdLog.d(TAG, "PostData: " + params.size());
		for (int i = 0; i < params.size(); ++i) {
			DdLog.d(TAG, params.get(i).getName() + ":" + params.get(i).getValue());
		}
	}
	//	public static String connServerPostForResult(String host, String path,
	//			String urlQueryString) {
	//		String url = host + path + "?" + urlQueryString;
	//		Log.d(TAG, "post, url: "+url);
	//		// HttpGet对象
	//		HttpPost httpRequest = new HttpPost(url);
	//		String strJSON = "";
	//		try {
	//			// HttpClient对象
	//			HttpParams httpParameters = new BasicHttpParams();
	//			HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECT_TIMEOUT_IN_MS);
	//			HttpConnectionParams.setSoTimeout(httpParameters, CONNECT_TIMEOUT_IN_MS);
	//			HttpClient httpClient = new DefaultHttpClient(httpParameters);
	//			httpRequest.setHeader(MUD, mudVal);
	//
	//			// 获得HttpResponse对象
	//			HttpResponse httpResponse = httpClient.execute(httpRequest);
	//			int statusCode = httpResponse.getStatusLine().getStatusCode();
	//			if (statusCode == HttpStatus.SC_OK) {
	//				saveMUD(httpResponse);
	//
	//				// 取得返回的数据
	//				strJSON = EntityUtils.toString(httpResponse.getEntity());
	//				DdLog.d(TAG, "response: " + strJSON);
	//				ResultBaseBean result = JSON.parseObject(strJSON, ResultBaseBean.class);
	//				if (result != null && result.ret == ret_key_timeout) {
	//					++count_retry_getkey;
	//					if(count_retry_getkey<max_retry_getkey){
	//						DdLog.d(TAG, "retry getkey, count_retry_getkey: "+count_retry_getkey);
	//						resetKey();
	//						strJSON = connServerPostForResult(host, path, url);
	//					}
	//				}else{
	//					count_retry_getkey = 0;
	//				}
	//			} else {
	//				DdLog.e(TAG, "connServerPostForResult error, statusCode: "
	//						+ statusCode);
	//			}
	//		} catch (ClientProtocolException e) {
	//			DdLog.e(TAG, e, false);
	//		} catch (Exception e) {
	//			DdLog.e(TAG, e, false);
	//		}
	//		return strJSON;
	//	}

	synchronized public static String loadMUD() {
		mudVal = FConfigUtil.load(DdResource.KEY_HTTP_HEADER_MUD);
		DdLog.d(TAG, "loadMUD, "+mudVal);
		return mudVal;
	}

	synchronized private static void saveMUD(HttpResponse httpResponse) {
		Header header = httpResponse.getFirstHeader(MUD);
		if (header == null) {
			return;
		}
		String tmpMudVal = header.getValue();

		if (tmpMudVal == null || DdApplication.CONTEXT == null) {
			return;
		}
		if (!tmpMudVal.equals(mudVal)) {
			mudVal = tmpMudVal;
			FConfigUtil.save(DdResource.KEY_HTTP_HEADER_MUD, mudVal);			
			DdLog.d(TAG, "saveMUD, "+mudVal);
		}
	}
	synchronized public static void clearMUD() {
		mudVal = "";
		FConfigUtil.clear(DdResource.KEY_HTTP_HEADER_MUD);
	}
	private static String getEtag(HttpResponse httpResponse) {
		Header header = httpResponse.getFirstHeader(EtagResponse);
		if (header == null) {
			return "";
		}
		String tmpEtagVal = header.getValue();

		if (tmpEtagVal == null || DdApplication.CONTEXT == null) {
			return "";
		}
		DdLog.d(TAG, "getEtag: "+tmpEtagVal);
		return tmpEtagVal;
	}

	private static String getHeader(HttpResponse httpResponse, String name) {
		Header header = httpResponse.getFirstHeader(name);
		if (header == null) {
			return null;
		}
		return header.getValue();
	}

	public static String GetKeyWordsJOSNString() {
		String path = "/search/hot_keywords";
		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		qsh.add(DdResource.CLIENT_ID_NAME, DdResource.getUniqueId());
		qsh.add(DdResource.VERSION_NAME, DdResource.getApplicationVersionCode());

		String strJOSN = SendRequest.connServerForResult(
				DdConfig.JSON_URL, path, qsh);
		return strJOSN;
	}

	public static int GetScanPersonNum() {
		int num = 0;
		String path = "/search/get_scan_person_num";

		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		qsh.add(DdResource.CLIENT_ID_NAME, DdResource.getUniqueId());
		qsh.add(DdResource.VERSION_NAME, DdResource.getApplicationVersionCode());

		String strJOSN = SendRequest.connServerForResult(
				DdConfig.JSON_URL, path, qsh);

		try {
			num = ConvertJSON.getInt(strJOSN, "scan");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

	public static void resetKey(){
		key = "";
	}
	public synchronized static String getKey(boolean isSecond) {
		if(!TextUtils.isEmpty(key)){
			return key;
		}

		String path = "/get_key";
		QueryStringHelper qsh = new QueryStringHelper();
		qsh.add(DdResource.CLIENT_ID_NAME, DdResource.getUniqueId());
		qsh.add(DdResource.VERSION_NAME, DdResource.getApplicationVersionCode());

		String strJSON = SendRequest.connServerForResult(
				DdConfig.JSON_URL, path, qsh, CONNECT_TIMEOUT_START_IN_MS, true, true, false, false);
		try {
			key = ConvertJSON.getString(strJSON, "key");
			// 如果第一次取不到，则取第二次
			if (TextUtils.isEmpty(key) && !isSecond) {
				key = getKey(true);
			}
			DdLog.d(TAG, "key: "+key);
		} catch (Exception e) {
			DdLog.e(TAG, e);
		}
		return key;
	}

	public static ServerResult setAddProduct(String barcode, String name,
			String brand, String email, List<String> imgs) {
		String path = "/search/add_product";
		//		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		//		qsh.add(MeilaResource.CLIENT_ID_NAME, MeilaResource.getUniqueId());
		//		qsh.add(MeilaResource.VERSION_NAME, MeilaResource.getApplicationVersionCode());
		//		qsh.add("name", name);
		//		qsh.add("brand", brand);
		//		qsh.add("email", email);
		//		qsh.add("barcode", barcode);

		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		//		postParams.add(new BasicNameValuePair(MeilaResource.CLIENT_ID_NAME, MeilaResource.getUniqueId()));
		//		postParams.add(new BasicNameValuePair(MeilaResource.VERSION_NAME, MeilaResource.getApplicationVersionCode()));
		addPostParam(postParams, "name", name);
		addPostParam(postParams, "brand", brand);
		if(!TextUtils.isEmpty(email)){
			addPostParam(postParams, "email", email);
		}
		if(!TextUtils.isEmpty(barcode)){
			addPostParam(postParams, "barcode", barcode);
		}
		if(imgs != null && imgs.size()>0){
			for(String img:imgs){
				addPostParam(postParams, "imgs", img);
			}
		}

		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		qsh.setPostParams(postParams);

		String strJSON = SendRequest.connServerPostForResult(
				DdConfig.JSON_URL, path, qsh, postParams);

		ServerResult rs = new ServerResult();
		try {
			if (strJSON != null && !strJSON.equals("")) {
				ResultBaseBean result = JSON.parseObject(strJSON, ResultBaseBean.class);
				rs.ret = result.ret;
				rs.msg = result.msg;
			}
		} catch (Exception e) {
			DdLog.e(TAG, e);
		}
		return rs;
	}

	public static ServerResult setCommentProduct(String slug, int star,
			String comment) {
		String path = "/comment/post_comment";
		DdLog.d(TAG, "setCommentProduct, slug: " + slug + ", star: " + star
				+ ", comment: " + comment);

		//		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		//		qsh.add(MeilaResource.CLIENT_ID_NAME, MeilaResource.getUniqueId());
		//		qsh.add(MeilaResource.VERSION_NAME, MeilaResource.getApplicationVersionCode());
		//		qsh.add("slug", slug);
		//		qsh.add("star", "" + star);
		//		qsh.add("content", comment);

		boolean isSuccess = false;

		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		//		postParams.add(new BasicNameValuePair(MeilaResource.CLIENT_ID_NAME, MeilaResource.getUniqueId()));
		//		postParams.add(new BasicNameValuePair(MeilaResource.VERSION_NAME, MeilaResource.getApplicationVersionCode()));
		addPostParam(postParams, "slug", slug);
		addPostParam(postParams, "star", ""+star);
		addPostParam(postParams, "content", comment);

		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		qsh.setPostParams(postParams);

		String strJSON = SendRequest.connServerPostForResult(
				DdConfig.JSON_URL, path, qsh, postParams);

		return jsonStrToServerResult(strJSON, null);
	}

	public static ServerResult setCommentProduct(String slug, int star,
			String comment, List<String> imgUrlList) {
		DdLog.d(TAG, "setCommentProduct, slug: " + slug + ", star: " + star
				+ ", comment: " + comment);

		String path = "/comment/post_comment";
		//		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		//		qsh.add(MeilaResource.CLIENT_ID_NAME, MeilaResource.getUniqueId());
		//		qsh.add(MeilaResource.VERSION_NAME, MeilaResource.getApplicationVersionCode());
		//		qsh.add("slug", slug);
		//		qsh.add("star", "" + star);
		//		qsh.add("content", comment);

		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		//		postParams.add(new BasicNameValuePair(MeilaResource.CLIENT_ID_NAME, MeilaResource.getUniqueId()));
		//		postParams.add(new BasicNameValuePair(MeilaResource.VERSION_NAME, MeilaResource.getApplicationVersionCode()));
		addPostParam(postParams, "slug", slug);
		addPostParam(postParams, "star", ""+star);
		addPostParam(postParams, "content", comment);

		if (imgUrlList != null && imgUrlList.size() > 0) {
			for (int i = 0; i < imgUrlList.size(); ++i) {
				//				qsh.add("imgs", imgUrlList.get(i));
				postParams.add(new BasicNameValuePair("imgs", imgUrlList.get(i)));
			}
		}

		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		qsh.setPostParams(postParams);

		String strJSON = SendRequest.connServerPostForResult(
				DdConfig.JSON_URL, path, qsh, postParams);
		return jsonStrToServerResult(strJSON, null);
	}


	// public static boolean setCommentProduct(String slug, int star, String
	// comment, ArrayList<String> imgPathList) {
	// DdLog.d(TAG,
	// "setCommentProduct, slug: "+slug+", star: "+star+", comment: "+comment);
	//
	// String path = "/comment/post_comment";
	// QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
	// qsh.add(MeilaResource.CLIENT_ID_NAME, MeilaResource.getUniqueId());
	// qsh.add(MeilaResource.VERSION_NAME, MeilaResource.getApplicationVersionCode());
	// qsh.add("slug", slug);
	// qsh.add("star", ""+star);
	// qsh.add("content", comment);
	//
	// boolean isSuccess = false;
	// //
	// // String strJSON = SendRequest.connServerPostForResult(
	// // DdConfig.JSON_URL, path, qsh.getResultQueryString());
	//
	// String actionUrl = DdConfig.JSON_URL + path + "?"
	// + qsh.getResultQueryString();
	//
	//
	// String end = "\r\n";
	// String twoHyphens = "--";
	// String boundary = "*****";
	// String imgParam = "imgs";
	// StringBuffer responseStr = new StringBuffer();
	// try {
	// URL url = new URL(actionUrl);
	// HttpURLConnection con = (HttpURLConnection) url.openConnection();
	// /* 允许Input、Output，不使用Cache */
	// con.setDoInput(true);
	// con.setDoOutput(true);
	// con.setUseCaches(false);
	// /* 设置传送的method=POST */
	// con.setRequestMethod("POST");
	// /* setRequestProperty */
	// con.setRequestProperty("Connection", "Keep-Alive");
	// con.setRequestProperty("Charset", "UTF-8");
	// con.setRequestProperty(MUD, mudVal);
	// con.setRequestProperty("Content-Type",
	// "multipart/form-data;boundary=" + boundary);
	// /* 设置DataOutputStream */
	// DataOutputStream ds = new DataOutputStream(con.getOutputStream());
	//
	// //添加一个文件
	// for(int i=0; i<imgPathList.size(); ++i){
	//
	// ds.writeBytes(twoHyphens + boundary + end);
	// // ds.writeBytes("Content-Disposition: form-data; " +
	// "name=\""+imgParam+"\";filename=\"" + newName + "\"" + end);
	// ds.writeBytes("Content-Disposition: form-data; ");
	//
	//
	// String localImgPath = imgPathList.get(i);
	// String newName = localImgPath.substring(localImgPath.lastIndexOf("/") +
	// 1);
	// ds.writeBytes("name=\""+imgParam+"\";filename=\"" + newName + "\"" +
	// end);
	// ds.writeBytes(end);
	// /* 取得文件的FileInputStream */
	// FileInputStream fStream = new FileInputStream(localImgPath);
	// /* 设置每次写入1024bytes */
	// int bufferSize = 1024;
	// byte[] buffer = new byte[bufferSize];
	// int length = -1;
	// /* 从文件读取数据至缓冲区 */
	// while ((length = fStream.read(buffer)) != -1) {
	// /* 将资料写入DataOutputStream中 */
	// ds.write(buffer, 0, length);
	// }
	// ds.writeBytes(end);
	// fStream.close();
	//
	// ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
	// }
	//
	// /* close streams */
	// ds.flush();
	// /* 取得Response内容 */
	// InputStream is = con.getInputStream();
	// int ch;
	//
	// while ((ch = is.read()) != -1) {
	// responseStr.append((char) ch);
	// }
	// /* 将Response显示于Dialog */
	// DdLog.d(TAG, "上传成功, response: " + responseStr.toString().trim());
	// /* 关闭DataOutputStream */
	// ds.close();
	//
	// String strJSON = responseStr.toString().trim();
	// JOSNObjectBaseBean base = ConvertJSON.getBaseJSONObject(strJSON);
	// if (base.getRet() == 0) {
	// isSuccess = true;
	// }
	// // return ConvertJSON.getString(strJSON, "url");
	// } catch (Exception e) {
	// DdLog.e(TAG, "上传失败" + e);
	// }
	// return isSuccess;
	// }
	

	public static DdConst getServerConst() {
		DdLog.d(TAG, "getServerConst");
		String path = "/const";
		QueryStringHelper qsh = new QueryStringHelper(getKey(false), path);
		qsh.add(DdResource.CLIENT_ID_NAME, DdResource.getUniqueId());
		qsh.add(DdResource.VERSION_NAME, DdResource.getApplicationVersionCode());
		qsh.add("channel", DdApplication.CONTEXT.getResources().getString(R.string.channel));
		qsh.add("device_model", android.os.Build.MODEL);
		qsh.add("os_version", android.os.Build.VERSION.RELEASE);
		qsh.add("device_token", DdResource.getUniqueId());
		if(!TextUtils.isEmpty(DdResource.getPushToken())){
			qsh.add("push_token", DdResource.getPushToken());
		}
		qsh.add("mac_address", NetUtil.getMac(DdApplication.CONTEXT));
		qsh.add("imei", DdResource.getImei());

		String strJSON = SendRequest.connServerForResult(
				DdConfig.JSON_URL, path, qsh);
		try {
			if (strJSON == null || strJSON.equals("")) {
				DdLog.e(TAG, "getServerConst failed, return empty");
				return null;
			}

			ResultBaseBean result = JSON.parseObject(strJSON, ResultBaseBean.class);
			if (result != null && result.ret == DdErrorCode.ret_ok) {
				DdConst con = JSON.parseObject(result.data, DdConst.class);
				if (con != null) {
					DdConst.set(con);
					con.checkServerTime();
				}
				return con;
			}

		} catch (Exception e) {
			DdLog.e(TAG, e);
		}
		return null;
	}

	/**
	 * 上传单个文件至Server的方法
	 * 
	 * @param String
	 *            请求地址
	 * @param String
	 *            图片的本地路径
	 * @return 成功则返回图片的url，失败则返回错误信息
	 */
	static ServerResult uploadImage(String requestUrl, String requestDir, String localImgPath) {
		ServerResult rs = new ServerResult();

		QueryStringHelper qsh = new QueryStringHelper(getKey(false), requestUrl);
		qsh.add(DdResource.CLIENT_ID_NAME, DdResource.getUniqueId());
		qsh.add(DdResource.VERSION_NAME, DdResource.getApplicationVersionCode());
		qsh.add("dir", requestDir);
		// qsh.add("brand", brand);
		// qsh.add("email", email);
		// qsh.add("barcode", barcode);

		// boolean isSuccess = false;
		//
		// String strJSON = SendRequest.connServerPostForResult(
		// DdConfig.JSON_URL, path, qsh.getResultQueryString());

		String actionUrl = DdConfig.JSON_URL + requestUrl + "?"
				+ qsh.getResultQueryString();

		String newName = "XXX";
		//		try {
		//			newName = new String(localImgPath
		//					.substring(localImgPath.lastIndexOf("/") + 1).getBytes("UTF-8"), "UTF-8");
		//		} catch (Exception e1) {
		//			newName = "XXX";
		//		}


		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String imgParam = "img";
		String dirParam = "dir";
		StringBuffer responseStr = new StringBuffer();
		HttpURLConnection con = null;
		try {
			URL url = new URL(actionUrl);
			con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty(MUD, mudVal);
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " 
					+ "name=\""+ imgParam + "\";filename=\"" + newName + "\"; "
					+dirParam+"=\""+requestDir+"\""
					+ end);
			ds.writeBytes(end);
			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(localImgPath);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;

			while ((ch = is.read()) != -1) {
				responseStr.append((char) ch);
			}
			/* 将Response显示于Dialog */
			DdLog.d(TAG, "上传成功, response: " + responseStr.toString().trim());
			/* 关闭DataOutputStream */
			ds.close();

			String strJSON = responseStr.toString().trim();
			if (!strJSON.equals("")) {
				ResultBaseBean result = JSON.parseObject(strJSON, ResultBaseBean.class);
				rs.ret = result.ret;
				rs.msg = result.msg;

				if (result.ret == DdErrorCode.ret_ok) {
					String returnUrl = ConvertJSON.getString(strJSON, "url");
					rs.obj = returnUrl;
					DdLog.d(TAG, "uploadImage ok, " + returnUrl);
				} else {
					DdLog.d(TAG, "uploadImage failed");
				}
			} else {
				DdLog.e(TAG, "uploadImage failed, response empty");
			}
		} catch (Throwable e) {
			DdLog.e(TAG, "上传失败");
			DdLog.e(TAG, e);
		} finally {
			try {
				if (con != null) {
					con.disconnect();
				}
			} catch (Exception ex) {
				DdLog.e(TAG, ex.getMessage());
			}
		}
		return rs;
	}
	static void addPostParam(List<NameValuePair> postParams, String key, String val){
		if(postParams == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(val)){
			return;
		}
		postParams.add(new BasicNameValuePair(key, val));
	}
}