package com.dd.datastatistics.biz;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dd.datastatistics.bean.BtnClickEventData;
import com.dd.datastatistics.bean.DataStaMeilaErrorCode;
import com.dd.datastatistics.bean.ServerResult;
import com.dd.datastatistics.bean.UploadDataItem;
import com.dd.datastatistics.bean.UploadDataList;
import com.dd.datastatistics.constant.DataStaMeilaConfig;
import com.dd.datastatistics.dbutil.DataStaDBUtil;
import com.dd.datastatistics.net.DataStaSendRequest;
import com.dd.datastatistics.orm.DataStaDataManager;
import com.dd.datastatistics.util.DataStaCalendarUtils;
import com.dd.datastatistics.util.DataStaSPConfigUtil;


/**
 * 鎵归噺涓婁紶缁熻鏁版嵁
 * 
 * @author Darren
 *
 */
public class UploaDataTask {
	public final static String COL_UPDATE_TIME = "updateTime";
	public final static String COL_ID = "_id";
	public final static String ORDBY ="asc";
	
	public final static int MAX_SAVE_COUNT_IN_DB = 10000;
	private UploaDataThread mThread;
	private boolean isUploading = false;

	public boolean isDesc(){
		return "desc".equals(ORDBY);
	}

	private static UploaDataTask mUploaDataTask;
	public static UploaDataTask getInstance(){
		if(mUploaDataTask == null){
			mUploaDataTask = new UploaDataTask();
		}
		return mUploaDataTask;
	}
	private UploaDataTask(){
		Log.i("UploaDataTask","UploaDataTask:" + mThread );
	}	
	public void doUploadData(){
		if(!isUploading){
			if(mThread != null ){
				isUploading = true;
				mThread.start();
			} else {
				mThread = new UploaDataThread();
				isUploading = true;
				mThread.start();
			}
		}
	}

	public void closeUploaDataThread(){
		mThread = null;
		isUploading = false;
	}

	class UploaDataThread extends Thread{
		@Override
		public void run() {
			boolean uploadOK = false;
			try{
				Log.i("UploaDataThread","UploaDataThread:" );
				DataStaDataManager mgr = DataStaDBUtil.getDataManager();
				long oldtime = DataStaMeilaConfig.currentTimeSec() - DataStaMeilaConfig.OVER_DUE_TIME;
				//鍏堟妸澶辨晥鏁版嵁鍒犻櫎
				mgr.delete(BtnClickEventData.class, COL_UPDATE_TIME+"<?", new String[]{String.valueOf(oldtime)});

				//鍒ゆ柇鏄惁瓒呰繃浜�0000 鏉� 濡傛灉鏄秴杩囦簡  10000 鏉�鐩存帴鍏ㄩ儴娓呴櫎
				int count = mgr.getCount(BtnClickEventData.class);
				Log.i("UploaDataThread","mgr.getCount(BtnClickEventData.class);count:" + count);
				if(count >= MAX_SAVE_COUNT_IN_DB){
					boolean b = mgr.delete(BtnClickEventData.class, null, null);
					Log.i("UploaDataThread","mgr.getCount(BtnClickEventData.class);b:" + b);
				}

				//鍙栨暟鎹�
				List<BtnClickEventData> dataList = mgr.getList(BtnClickEventData.class, null, null,COL_UPDATE_TIME + " " + ORDBY, String.valueOf(DataStaMeilaConfig.UPLOAD_COUNT_EVERY_TIME));

				Log.i("UploaDataThread","dataList:" + dataList);
				if(DataStaMeilaConfig.isDebug){
					int index = 0;
					for(BtnClickEventData data : dataList){
						if(data != null){
							String s = DataStaCalendarUtils.getTime(data.updateTime);
							index ++ ;
						}
					}
				}
				if(dataList != null && dataList.size() > 0){
					//璁板綍鏈�悗涓�潯鏁版嵁  濡傛灉鏄崌搴�鍒欏垹闄� 鏃堕棿姣斾粬灏忕殑   濡傛灉鏄檷搴� 鍒犻櫎鏃堕棿姣斾粬澶х殑     
					String lastData = String.valueOf(dataList.get(dataList.size() - 1).updateTime );
					UploadDataList data = DBtoUpLoadData(dataList);
					String json = JSON.toJSONString(data);
					//涓婃姤鏁版嵁
					ServerResult rs = DataStaSendRequest.uploadData(json);

					//涓婃姤鎴愬姛  鍒犻櫎鏁版嵁
					if(rs != null && rs.ret == DataStaMeilaErrorCode.ret_ok){
						//涓婁紶鎴愬姛  闇�鏇存柊涓婁紶鏃堕棿  鍒犻櫎涓婁紶鐨勬暟鎹�
						DataStaSPConfigUtil.save(DataStaMeilaConfig.KEY_UPLOAD_TIME,String.valueOf(DataStaMeilaConfig.currentTimeSec()));
						boolean delOK = mgr.delete(BtnClickEventData.class, COL_UPDATE_TIME + (isDesc()? ">=?" : "<=?") , new String[]{lastData});
						Log.i("UploaDataThread","mgr.delete:delOK:" + delOK);	
						uploadOK = true;
					} 
				}			
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				mThread = null;
				isUploading = false;
				/**
				 * 濡傛灉鏄湪WiFi鎯呭喌涓嬪氨浼氫竴鐩翠笂浼� 鐩村埌 浠庢暟鎹簱涓彇涓嶅埌鏁版嵁浜�
				 */
				if(DataStatistics.isWiFi() && uploadOK){
					doUploadData();
				}
			}
		}		
	}



	/**
	 * 浠庢暟鎹簱涓緱鍒扮殑鏁版嵁灏佽鎴愭垚瑕佷笂浼犵殑鏁版嵁缁撴瀯
	 * 
	 * @param dataList   鏁版嵁搴撲腑鍙栧緱鐨�鏁版嵁 
	 * @return  涓婁紶鐨勬暟鎹粨鏋�
	 * 
	 * uploadList.client_id  闇�鍒濆鍖栨椂璁剧疆璇ュ弬鏁�  濡傛灉娌℃湁璁剧疆灏遍粯璁や负绌�
	 */
	public UploadDataList DBtoUpLoadData(List<BtnClickEventData> dataList){
		UploadDataList uploadList = new UploadDataList();
		uploadList.client_id = DataStaMeilaConfig.getUniqueId();
		List<UploadDataItem> itemList = new ArrayList<UploadDataItem>();
		for(BtnClickEventData eventItem : dataList ){
			UploadDataItem item = new UploadDataItem();
			if(eventItem != null){
				item.t = eventItem.updateTime;
				item.s = eventItem.data;
				itemList.add(item);
			}
		}
		uploadList.logs = itemList;
		return uploadList;		
	}
}
