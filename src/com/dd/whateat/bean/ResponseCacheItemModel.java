package com.dd.whateat.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.text.TextUtils;

import com.dd.whateat.comfig.DdConfig;
import com.dd.whateat.constents.DdConstants;
import com.dd.whateat.db.BaseModel;
import com.dd.whateat.db.DataManager;
import com.dd.whateat.db.NotStore;
import com.dd.whateat.utils.DBUtil;
import com.dd.whateat.utils.DdLog;
import com.dd.whateat.utils.DdResource;
import com.dd.whateat.utils.FileUtil;
import com.dd.whateat.utils.Md5Util;
import com.dd.whateat.utils.SPConfigUtil;

/**
 * 缓存http请求的响应，response存文件中，其他存数据库
 * @author Administrator
 *
 */
public class ResponseCacheItemModel extends BaseModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String TAG = "ResponseCacheItemModel";
	final static long OVER_DUE = 15*24*3600;//15天过期
	
	public final static String COL_MD5 = "md5";
	public final static String COL_UPDATETIME = "upadtetime";
	
	
	@NotStore
	public String url = "";
	
	public String etag= "";
	
	public String md5 = "";//对url做16位md5，取1，2位作为一级目录，3，4位作为二级目录
	public String dir1= "";//存放目录
	public String dir2= "";
	public String filename= "";
	public long upadtetime;
	
	@NotStore
	public String response;
	
	public boolean save(){
		if(DdConfig.ENABLE_HTTP_CACHE || DdConfig.SAVE_HTTP_CACHE){
			initVal();
			if(saveToFile()){
				return saveToDB();
			}
		}
		return false;
	}
	
	void initVal(){
		if(TextUtils.isEmpty(url)){
			DdLog.e(TAG, "initVal, url is null");
			return;
		}
		//先对url做md5，然后取前两位做第一级目录，接着的2位做第2组目录，后面的做文件名
		md5 = Md5Util.strToMd5(url);
		initValFromMd5();
	}
	void initValFromMd5(){
		dir1 = md5.substring(0, 2);
		dir2 = md5.substring(2, 4);
		filename = md5.substring(4);
		upadtetime = DdConst.currentTimeSec();
	}
	boolean saveToDB(){
		try{
			DataManager mgr = DBUtil.getDataManager();
			ResponseCacheItemModel findItem = mgr.get(ResponseCacheItemModel.class, COL_MD5+"=?", new String[]{md5});
			if(findItem != null){
				findItem.dir1 = dir1;
				findItem.dir2 = dir2;
				findItem.filename = filename;
				findItem.md5 = md5;
				findItem.upadtetime = upadtetime;
				findItem.etag = etag;
				mgr.save(findItem);
			}else{
				mgr.save(this);
			}
			return true;
		}catch (Exception e) {
			DdLog.e(TAG, e);
		}
		return false;
	}
	boolean saveToFile(){
		try{
			//创建目录
			File dir = getCacheSubDir();
			if(dir.exists() && !dir.isDirectory()){
				dir.delete();
			}
			if(!dir.exists()){
				dir.mkdirs();
			}
			
			//创建文件并保存缓存数据
			File cacheFile = new File(dir, filename);
			FileOutputStream os = new FileOutputStream(cacheFile);
			os.write(response.getBytes());
			os.flush();
			os.close();
			
			return true;
		}catch (Exception e) {
			DdLog.e(TAG, e);
		}
		return false;
	}
	
	public static File getCacheRootDir(){
		File dir = new File(Environment.getExternalStorageDirectory()+"/"+DdResource.HTTP_CACHE_DIR);
		return dir;
	}
	File getCacheSubDir(){
		File dir = new File(Environment.getExternalStorageDirectory()+"/"+DdResource.HTTP_CACHE_DIR+"/"+dir1+"/"+dir2);
		return dir;
	}
	File getCacheFile(){
		File dir = getCacheSubDir();
		File cacheFile = new File(dir, filename);
		return cacheFile;
	}
	boolean getResponse(){
		if(TextUtils.isEmpty(md5)){
			DdLog.e(TAG, "getResponse, md5 is null");
			return false;
		}
		
		initValFromMd5();
		File cacheFile = getCacheFile();
		try {
			FileInputStream in = new FileInputStream(cacheFile);
			int len = (int) cacheFile.length();
			if(len<=0){
				DdLog.e(TAG, "has etag, but response file has been deleted");
				return false;
			}
			
			byte[] buf = new byte[len];
			in.read(buf, 0, len);
			in.close();
			response = new String(buf);
			DdLog.d(TAG, "getResponse");
			DdLog.d(TAG, ""+response);
			return true;
		} catch (FileNotFoundException e) {
			DdLog.e(TAG, e);
		} catch (IOException e) {
			DdLog.e(TAG, e);
		} catch (OutOfMemoryError e) {
			DdLog.e(TAG, e);
		}
		return false;
	}
	
	static {
		initDir();
	}
	static void initDir(){
		File cacheDir = getCacheRootDir();
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}
	
	static public ResponseCacheItemModel getCacheItem(String url){
		ResponseCacheItemModel findItem = null;
		try{
			String md5Str = Md5Util.strToMd5(url);
			DataManager mgr = DBUtil.getDataManager();
			findItem = mgr.get(ResponseCacheItemModel.class, COL_MD5+"=?", new String[]{md5Str});
			
			if(findItem != null && !TextUtils.isEmpty(findItem.md5)){
				boolean isOk = findItem.getResponse();
				//如果缓存文件不在了，则从数据库中删除对应的数据项：url, etag等
				if(!isOk){
					mgr.delete(ResponseCacheItemModel.class, COL_MD5+"=?", new String[]{md5Str});
					return null;
				}
				
				//更新时间
				findItem.upadtetime = DdConst.currentTimeSec();
				mgr.save(findItem);
			}
		}catch (Exception e) {
			DdLog.e(TAG, e);
		}
		return findItem;
	}
	
	static public void deleteOverDue(){
		final long lastTime = SPConfigUtil.loadLong(DdResource.KEY_DEL_HTTP_CACHE_TIMESTAMP, 0);
		final long now = System.currentTimeMillis();
		if(now - lastTime> DdConstants.DEL_HTTP_CACHE_INTERVAL_IN_MS){
			new Thread(){
				public void run() {
					try{
						DataManager mgr = DBUtil.getDataManager();
						
						long oldtime = DdConst.currentTimeSec() - OVER_DUE;
						List<ResponseCacheItemModel> overdueList = mgr.getList(ResponseCacheItemModel.class, COL_UPDATETIME+"<?", new String[]{""+oldtime});
						mgr.delete(ResponseCacheItemModel.class, COL_UPDATETIME+"<?", new String[]{""+oldtime});
						if(overdueList != null && overdueList.size()>0){
							for(int i=0; i<overdueList.size(); ++i){
								try{
									ResponseCacheItemModel item = overdueList.get(i);
									File cacheFile = item.getCacheFile();
									cacheFile.delete();
								}catch (Exception e) {
								}
							}
						}
						
						//删除空目录
						FileUtil.deleteEmptyDirsRecursive(getCacheRootDir());
						
						SPConfigUtil.save(DdResource.KEY_DEL_HTTP_CACHE_TIMESTAMP, ""+now);
					}catch (Exception e) {
						DdLog.e(TAG, e);
					}
				}
			}.start();
		}
	}
	
	static public void clear(){
		try{
			DataManager mgr = DBUtil.getDataManager();
			mgr.delete(ResponseCacheItemModel.class, null, null);
			
			FileUtil.deleteDirRecursive(getCacheRootDir());
		}catch (Exception e) {
			DdLog.e(TAG, e);
		}
	}
}
