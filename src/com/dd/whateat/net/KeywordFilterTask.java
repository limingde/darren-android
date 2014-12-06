package com.dd.whateat.net;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import com.dd.whateat.utils.DdLog;

public class KeywordFilterTask extends Thread {
	final String TAG = "KeywordFilterTask";
	public static final int MSG_KEYWORD_FILTER = 101;
	
	List<String> requestList = new ArrayList<String>();
	boolean canRunning = false;
	Handler handler;
	
	public KeywordFilterTask(Handler handler){
		this.handler = handler;
	}
	public void setRunningFlag(boolean running){
		canRunning = running;
	}
	
	public void addTask(String keyword){
		synchronized (requestList) {
			if(requestList.indexOf(keyword) == -1){
				requestList.add(keyword);
				requestList.notifyAll();
				DdLog.d(TAG, "add task, "+keyword);
			}else{
				DdLog.d(TAG, "repeat task, "+keyword);
			}
		}
	}
	
	@Override
	public void run() {
		while(canRunning){
			String keyword = null;
			synchronized (requestList) {
				if(requestList.size()>0){
					keyword = requestList.remove(0);
				}else{
					try {
						requestList.wait();
					} catch (Exception e) {
						
					}
					continue;
				}
			}
			DdLog.d(TAG, "KeywordFilterTask, "+keyword+", "+requestList.size());				
//			ServerResult rs = SendRequest.FilterKeyword(keyword);
			DdLog.d(TAG, "KeywordFilterTask for "+keyword+" finished, "+requestList.size());
			
		}
	}
}
