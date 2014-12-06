package com.dd.whateat.net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.util.EntityUtils;

import android.net.http.AndroidHttpClient;

import com.dd.whateat.DdApplication;
import com.dd.whateat.comfig.DdConfig;
import com.dd.whateat.constents.DdConstants;
import com.dd.whateat.net.error.DdException;
import com.dd.whateat.utils.DdLog;


public class Server {
	private static final String TAG = "Server";
	
	private static Server sInstance = null;
	private static AndroidHttpClient m_HttpClient = createHttpClient();
	private static boolean isNewClient = false;
	
	private static final String User_Agent = "Mozilla/5.0 AppleWebKit/530.17(KHTML,like Gecko) Version/4.0 Mobile Safari/530.17";
	
	public static Server getInstance() {
		if (sInstance == null) {
			sInstance = new Server();
		}
		return sInstance;
	}
	
	private static final AndroidHttpClient createHttpClient() {
    	AndroidHttpClient httpClient = AndroidHttpClient.newInstance(User_Agent);
    	isNewClient = true;
        //如果proxy不为null，则代表用户使用的是cmwap 或者ctwap
        HttpHost httpProxy = DdApplication.getHttpProxy();
        if(httpProxy != null){
        	httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, httpProxy);
        }
        return httpClient;
    }
	
	public byte[] executeHttpRequest(HttpRequestBase httpRequest, String str)
			throws DdException, IOException, URISyntaxException {
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

			byte[] buffer = new byte[DdConstants.k_BUFFER_SIZE];

			byte rtn[] = null;
			if (inStream instanceof GZIPInputStream) {
				// got zipped input stream, decode it
				GZIPInputStream zipin = (GZIPInputStream) inStream;
				// decompress the file
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int length;
				while ((length = zipin.read(buffer, 0, DdConstants.k_BUFFER_SIZE)) != -1)
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
			DdLog.e(
					TAG,
					"HTTP Code: 400, response="
							+ response.getStatusLine().toString() + "entity="
							+ EntityUtils.toString(response.getEntity()));
			if (DdConfig.DEBUG) {
				throw new DdException(response.getStatusLine().toString(),
						EntityUtils.toString(response.getEntity()));
			} else {
				throw new DdException("网络不可用，请查看手机的网络设置");
			}

		case HttpStatus.SC_UNAUTHORIZED:
			response.getEntity().consumeContent();
			DdLog.e(
					TAG,
					"HTTP Code: 401, response="
							+ response.getStatusLine().toString());
			if (DdConfig.DEBUG) {
				throw new DdException(response.getStatusLine().toString());
			} else {
				throw new DdException("网络不可用，请查看手机的网络设置");
			}

		case HttpStatus.SC_NOT_FOUND:
			response.getEntity().consumeContent();
			DdLog.e(
					TAG,
					"HTTP Code: " + statusCode + ", response="
							+ response.getStatusLine().toString());
			if (DdConfig.DEBUG) {
				throw new DdException(response.getStatusLine().toString());
			} else {
				throw new DdException("网络不可用，请查看手机的网络设置");
			}
		case HttpStatus.SC_MOVED_TEMPORARILY:
			response.getEntity().consumeContent();
			DdLog.e(
					TAG,
					"HTTP Code: " + statusCode + ", response="
							+ response.getStatusLine().toString());
			if (DdConfig.DEBUG) {
				throw new DdException(response.getStatusLine().toString());
			} else {
				throw new DdException("网络不可用，请查看手机的网络设置");
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
    			DdLog.e(
    					TAG,
    					"HTTP Code: " + statusCode + ", response="
    							+ response.getStatusLine().toString());
    			if (DdConfig.DEBUG) {
    				throw new DdException(response.getStatusLine().toString());
    			} else {
    				throw new DdException("获取数据失败，请稍后重试");
    			}
            }
		case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			response.getEntity().consumeContent();
			DdLog.e(
					TAG,
					"HTTP Code: 500, response="
							+ response.getStatusLine().toString());
			if (DdConfig.DEBUG) {
				throw new DdException(response.getStatusLine().toString());
			} else {
				throw new DdException("服务器故障，请稍后重试");
			}
		default:
			DdLog.e(
					TAG,
					"Default case for status code reached: HTTP Code: "
							+ statusCode + ", response="
							+ response.getStatusLine().toString());
			response.getEntity().consumeContent();
			if (DdConfig.DEBUG) {
				throw new DdException(response.getStatusLine().toString());
			} else {
				throw new DdException("系统错误，请稍后重试");
			}
		}
	}
	
	public HttpResponse executeHttpRequest(HttpRequestBase httpRequest) throws IOException {
        DdLog.d(TAG, "executing HttpRequest for: " + httpRequest.getURI().toString());
        try {
        	m_HttpClient.getConnectionManager().closeExpiredConnections();
        	isNewClient = false;
			return m_HttpClient.execute(httpRequest);
		} catch (IOException e) {
			DdLog.e(TAG, e);
			httpRequest.abort();
			synchronized (this) {
				if (isNewClient == false) {
					m_HttpClient.close();
					m_HttpClient = createHttpClient();
				}
			}
			throw e;
		} catch (NullPointerException ne) {
			DdLog.e(TAG, ne);
			httpRequest.abort();
			synchronized (this) {
				if (isNewClient == false) {
					m_HttpClient.close();
					m_HttpClient = createHttpClient();
				}
			}
			return null;
		} catch (Throwable t) {
			DdLog.e(TAG, t);
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
