package com.dd.whateat.utils;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BmpMemCache {
	final String TAG = "BmpMemCache";
	private static BmpMemCache instance = null;
	public static BmpMemCache getCache(){
		if(instance == null){
			initInstance();
		}
		return instance;
	}
	private static synchronized void initInstance(){
		if(instance == null){
			instance = new BmpMemCache();
		}
	}

	private BmpMemCache(){
		initMemoryCache();
	}
	
//	private HashMap<String, SoftReference<Bitmap>> imageCache = null;
	private LruCache<String, SoftReference<Bitmap>> imageCache = null;
	
	private void initMemoryCache(){
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 4;
	    DdLog.d(TAG, "initMemoryCache, cacheSize: "+cacheSize);
	    
//		imageCache = new LruCache<String, SoftReference<Bitmap>>(cacheSize){
//			@Override
//	        protected int sizeOf(String key, SoftReference<Bitmap> bitmap) {
//				int size = 0;
//				try{
//	            // The cache size will be measured in kilobytes rather than
//	            // number of items.
////	            return bitmap.getByteCount() / 1024;
//				size = bitmap.get().getRowBytes() * bitmap.get().getHeight() / 1024;
//				}catch (Exception e) {
//				}
//				DdLog.d(TAG, "sizeOf, size: "+size+", key: "+key);
//				return size;
//	        }
//		};
	    imageCache = new LruCache<String, SoftReference<Bitmap>>(cacheSize);
//	    imageCache = new HashMap<String, SoftReference<Bitmap>>(cacheSize);
	}
	
	public Bitmap getFromMemoryCache(final String key){
		try{
			return imageCache.get(key).get();
		}catch (Throwable e) {
//			DdLog.e(TAG, e);
		}
		return null;
	}
	public void addBmpToMemoryCache(final String key, final Bitmap bitmap){
		try{
			imageCache.put(key, new SoftReference<Bitmap>(bitmap));
		}catch (Throwable e) {
			DdLog.e(TAG, e);
		}
	}
	
	public void clearCache() {
		DdLog.e("clearCache", "clearCache size before:" + imageCache.size());
		if (imageCache != null && imageCache.size() > 0) {
			imageCache.evictAll();
		}
		
		DdLog.e("clearCache", "clearCache size after:" + imageCache.size());
	}
}
