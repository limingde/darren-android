package com.dd.datastatistics.biz;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.dd.datastatistics.bean.BtnClickEventData;
import com.dd.datastatistics.biz.SaveDataTask.WorkThread;
import com.dd.datastatistics.constant.DataStaMeilaConfig;
import com.dd.datastatistics.dbutil.DataStaDBUtil;
import com.dd.datastatistics.orm.DataStaDataManager;
import com.dd.datastatistics.util.DataStaMeilaLog;
public class SaveDataTask {
	public static String TAG = "SaveDataTask";
	public final static String COL_UPDATE_TIME = "updateTime";
	public static List<BtnClickEventData> taskList = new ArrayList<BtnClickEventData>();
	public static WorkThread taskThread = null;
	
	public static boolean IsOpenStat = false;

	public  static void saveToDB(String data){
		//蹇呴』瑕佸垵濮嬪寲涔嬪悗鎵嶈繘琛�缁熻
		if(!IsOpenStat){
			return;
		}
		if(!TextUtils.isEmpty(data)){
			BtnClickEventData item = new BtnClickEventData();
			item.data = data;
			item.updateTime = DataStaMeilaConfig.currentTimeSec();			
			addNewTask(item);
			Log.i("saveToDB","BtnClickEventData:" + item);
		}
	}

	public static void addNewTask(BtnClickEventData task){
		synchronized (taskList) {
			if(taskList != null){
			taskList.add(task);
			Log.i("addNewTask","BtnClickEventData:" + task);
			} else {
				taskList = new ArrayList<BtnClickEventData>();
				taskList.add(task);
			}
		}		
		startWork();
		
	} 

	String[] aa = {};

	
	public static void startWork() {
		synchronized (taskList) {
			if (taskThread == null) {
				taskThread = new WorkThread();
				taskThread.start();	
				Log.i("startWork","WorkThread:" + taskThread);
			} else {
				DataStaMeilaLog.d(TAG, "task is running");
			}
		}
	}

	static class WorkThread extends Thread{
		@Override
		public void run() {

			while(true){
				synchronized (taskList) {
					if(taskList != null && taskList.size() > 0){

						BtnClickEventData item =  taskList.remove(0);
						if(item != null){
							updateToDB(item);
						}
					} else{
						taskThread = null;
						break;
					}
				}				
			}
		}
	}

	public static boolean updateToDB(BtnClickEventData item){
		try{
			DataStaDataManager mgr = DataStaDBUtil.getDataManager();		
			mgr.insert(item);
			Log.i("updateToDB","BtnClickEventData:" + item);
			return true;
		}catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}
		return false;
	}
	
	public static void closeSaveToDbTask(){
		taskThread = null;
		taskList.clear();
		taskList = null;
	}
}
