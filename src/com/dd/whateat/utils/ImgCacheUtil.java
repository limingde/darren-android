package com.dd.whateat.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.dd.whateat.bean.DdConst;
import com.dd.whateat.bean.ImgCacheItemModel;
import com.dd.whateat.constents.DdConstants;
import com.dd.whateat.db.DataManager;

/**
 * 缓存image 定期清楚
 * @author Darren
 *
 */
public class ImgCacheUtil {
	public final static String TAG = "ImgCacheItemModel";
	final static long OVER_DUE_SEC = 15*24*3600;//15天过期

	public final static String COL_MD5 = "md5";
	public final static String COL_UPDATETIME = "upadtetime";

	public  final static List<ImgCacheItemModel> taskList = new ArrayList<ImgCacheItemModel>();
	public boolean isLoop = true;
	public WorkThread taskThread;


	public ImgCacheUtil(){

	}

	public void updateImageCacheDB(String md5){

		if(!TextUtils.isEmpty(md5)){
			ImgCacheItemModel item = new ImgCacheItemModel();
			item.md5 = md5;
			String  bitmapName = md5;
			item.filename = md5;
			item.upadtetime = DdConst.currentTimeSec();

			if(md5.length() == 1){
				item.dir1 = md5;
			} else if(md5.length() == 2){
				item.dir1 =   bitmapName.substring(0, 1);
				item.dir2 = bitmapName.substring(0, 2);
			} else{
				item.dir1 =   bitmapName.substring(0, 1);
				item.dir2 = bitmapName.substring(0, 2);
				item.dir3 = bitmapName.substring(0, 3);
			}
			addNewTask(item);
		}
	}
	public void addNewTask(ImgCacheItemModel task){
		synchronized (taskList) {
			taskList.add(task);
		}		
		startWork();
	}
	public static boolean updateToDB(ImgCacheItemModel item){
		try{
			DataManager mgr = DBUtil.getDataManager();
			int count = mgr.updateByClause(ImgCacheItemModel.class,item, COL_MD5+"=?", new String[]{item.md5});

			if(count == 0){
				mgr.insert(item);
			}
			return true;
		}catch (Exception e) {
			DdLog.e(TAG, e);
		}
		return false;
	}


	public static void deleteOverDueImageCache(){
		try {
			final long lastTime = SPConfigUtil.loadLong(DdResource.KEY_DEL_IMAGE_CACHE_TIMESTAMP, 0);
			final long now = System.currentTimeMillis();
			
			if(now - lastTime > DdConstants.DEL_IMAGE_CACHE_INTERVAL_IN_MS){
				new Thread(){
					public void run() {
						try{
							DataManager mgr = DBUtil.getDataManager();
							long oldtime = DdConst.currentTimeSec() - OVER_DUE_SEC;
							List<ImgCacheItemModel> overdueList = mgr.getList(ImgCacheItemModel.class, COL_UPDATETIME+"<?", new String[]{""+oldtime});
							
							
							mgr.delete(ImgCacheItemModel.class, COL_UPDATETIME+"<?", new String[]{""+oldtime});
							if(overdueList != null && overdueList.size()>0){
								for(int i=0; i<overdueList.size(); ++i){
									try{
										ImgCacheItemModel item = overdueList.get(i);

										File cacheFile = getCacheFile(item);
										if(cacheFile != null && cacheFile.isFile() ){									
											boolean isok = cacheFile.delete();
											
										}
									}catch (Exception e) {
									}
								}
							}
							//删除空目录
							FileUtil.deleteEmptyDirsRecursive(getCacheRootDir());
							SPConfigUtil.save(DdResource.KEY_DEL_IMAGE_CACHE_TIMESTAMP, ""+now);
						}catch (Exception e) {
							DdLog.e(TAG, e);
						}
					}
				}.start();
			}
		} catch (Exception ex) {
			DdLog.e(TAG, ex.getMessage());
		}
	}

	public static File getCacheRootDir(){
		File dir = new File(Environment.getExternalStorageDirectory() + DdResource.FOLDER);
		return dir;
	}
	public static File getCacheSubDir(ImgCacheItemModel item){
		File dir = null ;
		if(item != null){			
			dir = new File(Environment.getExternalStorageDirectory()  + DdResource.FOLDER + "/" + item.dir1 + "/" + item.dir2 + "/" + item.dir3 +  "/");
		}
		return dir;
	}
	public static File getCacheFile(ImgCacheItemModel item){
		if(item == null){
			return null;
		}
		File dir = getCacheSubDir(item);
		File cacheFile = new File(dir, item.filename);
		return cacheFile;
	}

	public void startWork() {
		Log.d(TAG, " startWork():");
		synchronized (taskList) {
			if (taskThread == null) {
				taskThread = new WorkThread();
				taskThread.start();				
			} else {
				DdLog.d(TAG, "task is running");
			}
		}
	}
	/**
	 * 新起一个线程做DB操作
	 * 
	 */

	class WorkThread extends Thread{

		@Override
		public void run() {

			while(true){
				synchronized (taskList) {
					if(taskList != null && taskList.size() > 0){

						ImgCacheItemModel item =  taskList.remove(0);
						if(item != null){
							updateToDB(item);
						}

					} else{
						taskThread = null;
						return;
					}
				}				
			}
		}
	}

	public static boolean isNeedToClear(){
		final long lastTime = SPConfigUtil.loadLong(DdResource.KEY_DEL_IMAGE_CACHE_TIMESTAMP, 0);
		final long now = System.currentTimeMillis();
		Log.d(TAG, "isNeedToClear():now - lastTime:" + (now - lastTime));
		if(now - lastTime > DdConstants.DEL_IMAGE_CACHE_INTERVAL_IN_MS){
			Log.d(TAG, "isNeedToClear():(now - lastTime > DdConstants.DEL_IMAGE_CACHE_INTERVAL_IN_MS):" + (now - lastTime > DdConstants.DEL_IMAGE_CACHE_INTERVAL_IN_MS));
			return true;
		}
		return false;
	}

}
