package com.dd.datastatistics.net;
import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dd.datastatistics.bean.DataStaMeilaErrorCode;
import com.dd.datastatistics.bean.ResultBaseBean;
import com.dd.datastatistics.bean.ServerResult;
import com.dd.datastatistics.biz.DataStatistics;
import com.dd.datastatistics.constant.DataStaMeilaConfig;
import com.dd.datastatistics.util.DataStaFConfigUtil;
import com.dd.datastatistics.util.DataStaMeilaLog;

public class DataStaSendRequest{
	public static String TAG = "SendRequest";
	public static final String MUD = "Mud";
	volatile public static String mudVal = "";
	
	private static String CHARSET = "UTF-8";
	private static int count_retry_upload = 0;
	final static int max_retry_getkey = 2;
	final static int RETRY_DELAY_IN_MS = 100; 
/**
	 * 涓婁紶鍥剧墖
	 * @param aURL
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static ServerResult uploadData( String host,String name,String fileName,final byte []data) throws IOException {
		ServerResult rs = new ServerResult();		
		String returnString = "";
		try{
			DataQueryStringHelper qsh = new DataQueryStringHelper();
			qsh.add(DataStaMeilaConfig.CLIENT_ID_NAME, DataStaMeilaConfig.getUniqueId());
			qsh.add(DataStaMeilaConfig.VERSION_NAME, DataStaMeilaConfig.getApplicationVersionCode());

			String actionUrl = DataStaMeilaConfig.JSON_URL + host + "?"
					+ qsh.getResultQueryString();

			HttpPost httpPost = new HttpPost(actionUrl);
			
			String boundary = "*****";
			httpPost.addHeader("Content-Type", "multipart/form-data;boundary="+boundary);
			httpPost.addHeader("Connection", "Keep-Alive");
			httpPost.addHeader("Charset", CHARSET);
			httpPost.addHeader(MUD, mudVal);

			ByteArrayBody contentBody = new ByteArrayBody(data, "application/octet-stream", fileName);
			MultipartEntity multipartContent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, boundary, null);
			multipartContent.addPart(name, contentBody);
			httpPost.setEntity(multipartContent);

			/*
			 * Expect:100-Continue锛屾槸鐢ㄥ湪 瀹㈡埛绔悜 WEB SERVER绔彂閫丳OST鏁版嵁鐨勬儏褰笅锛�
			 * 鍦≒OST鏁版嵁鍙戦�鍓嶏紝瀹㈡埛绔細鍏堝彂閫佷竴涓狧EAD鎸囦护鍒癝ERVER锛孲ERVER濡傛灉杩斿洖100锛�
			 * 鍒欏鎴风鎵嶇湡姝ｇ殑POST鏁版嵁鍒癝ERVER锛屽惁鍒欏氨琛ㄧずSERVER绔笉瀛樺湪銆�
			 * 鍦ㄦ煇浜沨ttp鐗堟湰鐨剆erver绔笉浼氱悊浼�00杩欎釜璇锋眰锛屾墍浠ュ鑷村鎴风鍦ㄦ病鏈夋帴鏀跺埌鏈嶅姟绔繑鍥炵殑100鏃朵細鏃犳灏界殑绛夊緟銆�
			 */
			httpPost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			byte[] rtn = DataStaServer.getInstance().executeHttpRequest(httpPost, null);
			
			//璁板綍閫熷害鏃ュ織
			if(rtn != null){
				returnString = new String(rtn, HTTP.UTF_8);
			}
		} catch(Exception e) {
			DataStaMeilaLog.e(TAG, e);
			returnString = "";
			throw new SocketTimeoutException();
		}

		try{
			if (!returnString.equals("")) {
				ResultBaseBean result = JSON.parseObject(returnString, ResultBaseBean.class);
				rs.ret = result.ret;
				rs.msg = result.msg;

				if (result.ret == DataStaMeilaErrorCode.ret_ok) {
					String returnUrl = DataStaConvertJSON.getString(returnString, "url");
					rs.obj = returnUrl;
					DataStaMeilaLog.d(TAG, "uploadImage ok, " + returnUrl);
				} else {
					DataStaMeilaLog.d(TAG, "uploadImage failed");
					doRetry(host,name,fileName,data);
				}
			} else {
				doRetry(host,name,fileName,data);
				DataStaMeilaLog.e(TAG, "uploadImage failed, response empty");
			}
		}catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}
		return rs;
	}
	
	/**
	 * 濡傛灉涓婁紶澶辫触  浼氶噸鏂颁笂浼� 鐩村埌涓婁紶鍒版渶澶ч檺鍒舵鏁�
	 * 
	 * @param host
	 * @param name
	 * @param fileName
	 * @param data
	 */
	public static void doRetry(String host,String name,String fileName,final byte []data){
		if(count_retry_upload < max_retry_getkey){
			 try {
				 count_retry_upload ++;
				 waitSeconds();
				uploadData(host,name,fileName,data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static ServerResult uploadData(String data) {
		String host = "/log/report";
		String name = "report";
		String fileName = "uploaddata";
		try{
			if(!TextUtils.isEmpty(data)){
				byte[] dataByte = new byte[data.length()];
				dataByte = data.getBytes(CHARSET);
				return  uploadData(host,name,fileName,dataByte);
			}
		} catch(Exception e){
			e.printStackTrace();
		}	
		return null;
	}
	static void waitSeconds(){
		try {
			Thread.sleep(RETRY_DELAY_IN_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	synchronized public static String loadMUD() {
		mudVal = DataStaFConfigUtil.load(DataStaMeilaConfig.KEY_HTTP_HEADER_MUD);
		DataStaMeilaLog.d(TAG, "loadMUD, "+mudVal);
		return mudVal;
	}

	synchronized public static void saveMUD(HttpResponse httpResponse) {
		Header header = httpResponse.getFirstHeader(MUD);
		if (header == null) {
			return;
		}
		String tmpMudVal = header.getValue();

		if (tmpMudVal == null || DataStatistics.CONTEXT == null) {
			return;
		}
		if (!tmpMudVal.equals(mudVal)) {
			mudVal = tmpMudVal;
			DataStaFConfigUtil.save(DataStaMeilaConfig.KEY_HTTP_HEADER_MUD, mudVal);			
			DataStaMeilaLog.d(TAG, "saveMUD, "+mudVal);
		}
	}
	synchronized public static void clearMUD() {
		mudVal = "";
		DataStaFConfigUtil.clear(DataStaMeilaConfig.KEY_HTTP_HEADER_MUD);
	}
}