package com.dd.datastatistics.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;

import com.dd.datastatistics.biz.DataStatistics;
import com.dd.datastatistics.constant.DataStaMeilaConfig;
import com.dd.datastatistics.util.DataStaMeilaLog;


public class DataStaServer {
	private static final String TAG = "Server";
	
	private static DataStaServer sInstance = null;
	private static AndroidHttpClient m_HttpClient = createHttpClient();
	private static boolean isNewClient = false;
	
	private static final String User_Agent = "Mozilla/5.0 AppleWebKit/530.17(KHTML,like Gecko) Version/4.0 Mobile Safari/530.17";
	
	public static DataStaServer getInstance() {
		if (sInstance == null) {
			sInstance = new DataStaServer();
		}
		return sInstance;
	}
	
	private static final AndroidHttpClient createHttpClient() {
    	AndroidHttpClient httpClient = AndroidHttpClient.newInstance(User_Agent);
    	isNewClient = true;
        //濡傛灉proxy涓嶄负null锛屽垯浠ｈ〃鐢ㄦ埛浣跨敤鐨勬槸cmwap 鎴栬�ctwap
        HttpHost httpProxy = DataStatistics.getHttpProxy();
        if(httpProxy != null){
        	httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, httpProxy);
        }
        return httpClient;
    }
	
	public byte[] executeHttpRequest(HttpRequestBase httpRequest, String str)
			throws Exception {
		HttpResponse response = executeHttpRequest(httpRequest);
		if (response == null) {
			return null;
		}
		int statusCode = response.getStatusLine().getStatusCode();
		switch (statusCode) {
		case HttpStatus.SC_OK:
			// String content = EntityUtils.toString(response.getEntity());
			HttpEntity entity = response.getEntity();
			InputStream inStream = getUngzippedContent(entity);
			DataStaSendRequest.saveMUD(response);

			byte[] buffer = new byte[DataStaMeilaConfig.k_BUFFER_SIZE];

			byte rtn[] = null;
			if (inStream instanceof GZIPInputStream) {
				// got zipped input stream, decode it
				GZIPInputStream zipin = (GZIPInputStream) inStream;
				// decompress the file
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int length;
				while ((length = zipin.read(buffer, 0, DataStaMeilaConfig.k_BUFFER_SIZE)) != -1)
					baos.write(buffer, 0, length);
				rtn = baos.toByteArray();
				baos.close();

				zipin.close();
			} else {
				// not compressed input stream
				DataInputStream dis = null;
				dis = new DataInputStream(inStream);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// dis.avaiable does not return the length of the stream!!
				int length = 0;
				while ((length = dis.read(buffer)) != -1) {
					baos.write(buffer, 0, length);
				}

				rtn = baos.toByteArray();
				baos.close();
				dis.close();
			}
			return rtn;

		case HttpStatus.SC_BAD_REQUEST:
			DataStaMeilaLog.e(
					TAG,
					"HTTP Code: 400, response="
							+ response.getStatusLine().toString() + "entity="
							+ EntityUtils.toString(response.getEntity()));
			if (DataStaMeilaConfig.DEBUG) {
				throw new Exception(response.getStatusLine().toString());
			} else {
				throw new Exception("缃戠粶涓嶅彲鐢紝璇锋煡鐪嬫墜鏈虹殑缃戠粶璁剧疆");
			}

		case HttpStatus.SC_UNAUTHORIZED:
			response.getEntity().consumeContent();
			DataStaMeilaLog.e(
					TAG,
					"HTTP Code: 401, response="
							+ response.getStatusLine().toString());
			if (DataStaMeilaConfig.DEBUG) {
				throw new Exception(response.getStatusLine().toString());
			} else {
				throw new Exception("缃戠粶涓嶅彲鐢紝璇锋煡鐪嬫墜鏈虹殑缃戠粶璁剧疆");
			}

		case HttpStatus.SC_NOT_FOUND:
			response.getEntity().consumeContent();
			DataStaMeilaLog.e(
					TAG,
					"HTTP Code: " + statusCode + ", response="
							+ response.getStatusLine().toString());
			if (DataStaMeilaConfig.DEBUG) {
				throw new Exception(response.getStatusLine().toString());
			} else {
				throw new Exception("缃戠粶涓嶅彲鐢紝璇锋煡鐪嬫墜鏈虹殑缃戠粶璁剧疆");
			}
		case HttpStatus.SC_MOVED_TEMPORARILY:
			response.getEntity().consumeContent();
			DataStaMeilaLog.e(
					TAG,
					"HTTP Code: " + statusCode + ", response="
							+ response.getStatusLine().toString());
			if (DataStaMeilaConfig.DEBUG) {
				throw new Exception(response.getStatusLine().toString());
			} else {
				throw new Exception("缃戠粶涓嶅彲鐢紝璇锋煡鐪嬫墜鏈虹殑缃戠粶璁剧疆");
			}
		case HttpStatus.SC_MOVED_PERMANENTLY:
            Header[] headers = response.getHeaders("Location");
            if (headers != null && headers.length != 0
            		&& httpRequest.getMethod().equals(HttpGet.METHOD_NAME)
            		&& (str == null || !str.equals("redirect"))) {
                String newUrl = 
                    headers[headers.length - 1].getValue();
                // call again with new URL
                httpRequest.setURI(new URI(newUrl));
				response.getEntity().consumeContent();
                return executeHttpRequest(httpRequest, "redirect");
            } else {
    			response.getEntity().consumeContent();
    			DataStaMeilaLog.e(
    					TAG,
    					"HTTP Code: " + statusCode + ", response="
    							+ response.getStatusLine().toString());
    			if (DataStaMeilaConfig.DEBUG) {
    				throw new Exception(response.getStatusLine().toString());
    			} else {
    				throw new Exception("鑾峰彇鏁版嵁澶辫触锛岃绋嶅悗閲嶈瘯");
    			}
            }
		case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			response.getEntity().consumeContent();
			DataStaMeilaLog.e(
					TAG,
					"HTTP Code: 500, response="
							+ response.getStatusLine().toString());
			if (DataStaMeilaConfig.DEBUG) {
				throw new Exception(response.getStatusLine().toString());
			} else {
				throw new Exception("");
			}
		default:
			DataStaMeilaLog.e(
					TAG,
					"Default case for status code reached: HTTP Code: "
							+ statusCode + ", response="
							+ response.getStatusLine().toString());
			response.getEntity().consumeContent();
			if (DataStaMeilaConfig.DEBUG) {
				throw new Exception(response.getStatusLine().toString());
			} else {
				throw new Exception("绯荤粺閿欒锛岃绋嶅悗閲嶈瘯");
			}
		}
	}
	
	public HttpResponse executeHttpRequest(HttpRequestBase httpRequest) throws IOException {
        DataStaMeilaLog.d(TAG, "executing HttpRequest for: " + httpRequest.getURI().toString());
        try {
        	m_HttpClient.getConnectionManager().closeExpiredConnections();
        	//璁剧疆璇锋眰瓒呮椂
        	m_HttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
        	//璁剧疆璇诲彇瓒呮椂
        	m_HttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
        	isNewClient = false;
			return m_HttpClient.execute(httpRequest);
		} catch (IOException e) {
			DataStaMeilaLog.e(TAG, e);
			httpRequest.abort();
			synchronized (this) {
				if (isNewClient == false) {
					m_HttpClient.close();
					m_HttpClient = createHttpClient();
				}
			}
			throw e;
		} catch (NullPointerException ne) {
			DataStaMeilaLog.e(TAG, ne);
			httpRequest.abort();
			synchronized (this) {
				if (isNewClient == false) {
					m_HttpClient.close();
					m_HttpClient = createHttpClient();
				}
			}
			return null;
		} catch (Throwable t) {
			DataStaMeilaLog.e(TAG, t);
			httpRequest.abort();
			synchronized (this) {
				if (isNewClient == false) {
					m_HttpClient.close();
					m_HttpClient = createHttpClient();
				}
			}
			return null;
		}
    }
	
	public static InputStream getUngzippedContent(HttpEntity entity) throws IOException {
        InputStream responseStream = entity.getContent();
        if (responseStream == null) {
            return responseStream;
        }
        Header header = entity.getContentEncoding();
        if (header == null) {
            return responseStream;
        }
        String contentEncoding = header.getValue();
        if (contentEncoding == null) {
            return responseStream;
        }
        if (contentEncoding.contains("gzip")) {
            responseStream = new GZIPInputStream(responseStream);
        }
        return responseStream;
	}
}
