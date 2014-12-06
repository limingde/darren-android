package com.dd.whateat.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;

public class HttpUtils {
	public static final String TAG = "HttpUtils";
	final static int MAX_TRY = 1;
	
    public static InputStream getStreamFromURL(String imageURL) {
        InputStream in=null;
        
        int tryCount = 0;
        while(++tryCount<=MAX_TRY){
	        try {   
	            URL url=new URL(imageURL);
	            HttpURLConnection connection=(HttpURLConnection) url.openConnection();   
	            connection.setConnectTimeout(tryCount*5000);
	            connection.setReadTimeout(5000+(tryCount-1)*10000);
	            in=connection.getInputStream();
	            break;
	        } catch (Exception e) {
	            DdLog.e(TAG, e, "imageURL: "+imageURL);
	        }
	        
	        try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
	        DdLog.e(TAG, "retry to get image from "+imageURL);
        }
        return in;
           
    }
    
    public static InputStream getStreamFromURL(String imageURL, int[] param) {
        InputStream in=null;
        
        int tryCount = 0;
        while(++tryCount<=MAX_TRY){
	        try {   
	            URL url=new URL(imageURL);
	            HttpURLConnection connection=(HttpURLConnection) url.openConnection();   
	            connection.setConnectTimeout(tryCount*5000);
	            connection.setReadTimeout(5000+(tryCount-1)*10000);
	            in=connection.getInputStream();
	            if (param != null) {
	            	param[0] = connection.getContentLength();
	            }
	            break;
	        } catch (Exception e) {
	            DdLog.e(TAG, "imageURL: "+imageURL + "---------" + e.getMessage());
	        }
	        
	        try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
	        DdLog.e(TAG, "retry to get image from "+imageURL);
        }
        return in;
           
    }

    public static CharSequence formatString(final Context context, String htmlString) {

		CharSequence ch = Html.fromHtml(htmlString, new ImageGetter() {

			@Override
			public Drawable getDrawable(String source) {
				try{
					Drawable drawable = context.getResources().getDrawable(Utils.getResourceId(source));
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
					return drawable;
				}catch (Exception e) {
					DdLog.e(TAG, e);
				}
				return null;
			}
		}, null);

		return ch;
	}
    public static CharSequence formatString(final float textSize, final Context context, String htmlString) {

		CharSequence ch = Html.fromHtml(htmlString, new ImageGetter() {

			@Override
			public Drawable getDrawable(String source) {
				try{
					Drawable drawable = context.getResources().getDrawable(Utils.getResourceId(source));
					int dw = drawable.getIntrinsicWidth();
					int dh = drawable.getIntrinsicHeight();
					drawable.setBounds(0, 0, (int)(dw*textSize/dh), (int)(textSize));
					return drawable;
				}catch (Exception e) {
					DdLog.e(TAG, e);
				}
				return null;
			}
		}, null);

		return ch;
	}
    
    
    /**
     * 拼接完整的url，中间不带多余的/，因为有些页面带了多余的/会找不到，比如运营位
     * @param host
     * @param path
     * @return
     */
    public static String concatUrl(final String host, final String path){
    	if(TextUtils.isEmpty(host)){
    		return path;
    	}
    	if(TextUtils.isEmpty(path)){
    		return host;
    	}
    	if(path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")){
    		return path;
    	}
    	
    	StringBuilder sb = new StringBuilder(host);
    	if(sb.charAt(sb.length()-1) != '/'){
    		sb.append("/");
    	}
    	if(path.startsWith("/")){
    		sb.append(path.substring(1));
    	}else{
    		sb.append(path);
    	}
    	return sb.toString();
    }
}