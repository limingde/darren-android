package com.dd.whateat.net;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.dd.whateat.utils.DdLog;
import com.dd.whateat.utils.Utils;

/**
 * 用法：
 * 1, 定义一个DownAppTask类<br/>
 * 2, 调用registerDownloadBroadcast注册广播接收两个消息DownloadManager.ACTION_DOWNLOAD_COMPLETE, DownloadManager.ACTION_NOTIFICATION_CLICKED<br/>
 * 3, 调用downloadApk方法<br/>
 * 4, 在广播接收器中调用parseDownloadBroadcast解析<br/>
 * 5, 在对应该取消注册广播的地方调用unregisterDownloadBroadcast取消广播注册<br/>
 * @author Administrator
 *
 */
public class DownAppTask {
	private static final String TAG = "DownAppTask";
	
	private List<AppDownTask> downTaskList;
	private Activity act;
	
	public DownAppTask(Activity act){
		this.act = act;
		downTaskList = new ArrayList<DownAppTask.AppDownTask>();
	}
	
	public void registerDownloadBroadcast(BroadcastReceiver downloadReceiver){
		try{
			IntentFilter downloadIntentFilter = new IntentFilter();
			downloadIntentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			downloadIntentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
			act.registerReceiver(downloadReceiver, downloadIntentFilter);
		}catch (Throwable e) {			
		}
	}
	public void unregisterDownloadBroadcast(BroadcastReceiver downloadReceiver){
		try{
			act.unregisterReceiver(downloadReceiver);
		}catch (Throwable e) {			
		}
	}
	@SuppressLint("NewApi")
	public void downloadApk(String apkUrl, String apkName) {
		DdLog.d(TAG, "downloadApk, " + apkUrl);
		AppDownTask task = getTask(apkUrl);
		
		if(task != null && task.downState != AppDownTask.state_ready){
			DdLog.e(TAG, "downloadApk, repeat, "+task.downState+", "+apkUrl);
			return;
		}
		if(task == null){
			task = new AppDownTask();
			task.url = apkUrl;
		}
		
		try {
			Request request = new Request(Uri.parse(apkUrl));  

			request.setAllowedNetworkTypes(
					DownloadManager.Request.NETWORK_MOBILE
							| DownloadManager.Request.NETWORK_WIFI)
					.setAllowedOverRoaming(false) // 缺省是true
					.setTitle("更新") // 用于信息查看
					.setDescription("下载apk") // 用于信息查看
					.setDestinationInExternalPublicDir(
							Environment.DIRECTORY_DOWNLOADS, apkName+"."+System.currentTimeMillis() + ".apk");

			DownloadManager downloadManager = (DownloadManager) act.getSystemService(Context.DOWNLOAD_SERVICE);
			task.downId = downloadManager.enqueue(request); // 加入下载队列
			task.downState = AppDownTask.state_downloading;
			downTaskList.add(task);
			startQuery(task.downId);
		} catch (Throwable e) {
			downloadApkUseExplorer(apkUrl);
		}
		
	}
	
	public void parseDownloadBroadcast(Context context, Intent intent){
		try {
			String action = intent.getAction();
			
			//下载完成的广播
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {				
				long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
				
				AppDownTask task = getTask(downloadId);
				if(task != null){
					queryState(downloadId);
					stopQuery();
//					removeDownload(downloadId);
					install(task.localUri);
					task.downState = AppDownTask.state_down_complete;
				}else{
					DdLog.e(TAG, "downloadCompleteReceiver, get task null, cannot install");
				}					
			}
			
			//点击了通知栏的项的广播
			if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
				lookDownload();
			}
		} catch (Throwable e) {
			DdLog.e(TAG, e);
		}
	}
	
	void lookDownload() {
		act.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
	}
	
	void install(final String apkUri) {
		DdLog.d(TAG, "install, " + apkUri);
		handler.postDelayed(new Runnable() {			
			@Override
			public void run() {
				if (TextUtils.isEmpty(apkUri)) {
					return ;
				}
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(apkUri),
						"application/vnd.android.package-archive");
				act.startActivity(intent);
			}
		}, 1000);		
	}
	
	@SuppressLint("NewApi")
	private int removeDownload(long downloadId) {
		try {
			DownloadManager downloadManager = (DownloadManager) act.getSystemService(Context.DOWNLOAD_SERVICE);
			return downloadManager.remove(downloadId);
		} catch (Throwable e) {
			DdLog.e(TAG, e);
		}
		return -1;
	}
	
	void downloadApkUseExplorer(String url) {
		DdLog.d(TAG, "downloadApkUseExplorer, " + url);
		try {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			act.startActivity(intent);
		} catch (Throwable e) {
			DdLog.e(TAG, e);
		}
	}
	private void startQuery(long downloadId) {
		if (downloadId != 0) {
			runnable.DownID = downloadId;
			handler.postDelayed(runnable, step);
		}
	};
	private void stopQuery() {
		handler.removeCallbacks(runnable);
	}
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	
	int step = 1000;
	QueryRunnable runnable = new QueryRunnable();

	class QueryRunnable implements Runnable {
		public long DownID;

		@Override
		public void run() {
			queryState(DownID);
			handler.postDelayed(runnable, step);
		}
	};
	
	@SuppressLint("NewApi")
	private void queryState(long downID) {
		try {
			DownloadManager downloadManager = (DownloadManager) act.getSystemService(Context.DOWNLOAD_SERVICE);
			AppDownTask task = getTask(downID);
			if(task == null){
				DdLog.e(TAG, "queryState, cannot get task for downloadId: "+downID);
				return;
			}
			
			// 关键：通过ID向下载管理查询下载情况，返回一个cursor
			Cursor c = downloadManager.query(new DownloadManager.Query()
					.setFilterById(downID));
			if (c == null) {
				Utils.displayToast(act, "网络君抽风，请稍后重试~");
			} else { // 以下是从游标中进行信息提取
				if (!c.moveToFirst()) {
					c.close();
					return;
				}
				DdLog.d(TAG, "Column_id : " + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
				DdLog.d(TAG, "Column_bytes_downloaded so far : " + c.getLong(c
										.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
				DdLog.d(TAG, "Column last modified timestamp : " + c.getLong(c
										.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));

				task.localUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				DdLog.d(TAG, "Column local uri : " + task.localUri);

				DdLog.d(TAG, "Column statue : "+ c.getInt(c
										.getColumnIndex(DownloadManager.COLUMN_STATUS)));
				DdLog.d(TAG, "Column reason : " + c.getInt(c
										.getColumnIndex(DownloadManager.COLUMN_REASON)));

				int st = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
				// Toast.makeText(this, statusMessage(st),
				// Toast.LENGTH_LONG).show();
				DdLog.d(TAG, statusMessage(st));

				c.close();
			}
		} catch (Throwable e) {
			DdLog.e(TAG, e);
		}
	}
	AppDownTask getTask(long downloadId){
		for(int i=0; i<downTaskList.size(); ++i){
			if(downloadId == downTaskList.get(i).downId){
				return downTaskList.get(i);
			}
		}
		return null;
	}
	AppDownTask getTask(String apkUrl){
		if(TextUtils.isEmpty(apkUrl)){
			return null;
		}
		
		for(int i=0; i<downTaskList.size(); ++i){
			if(apkUrl.equals(downTaskList.get(i).url)){
				return downTaskList.get(i);
			}
		}
		return null;
	}
	private String statusMessage(int st) {
		switch (st) {
		case DownloadManager.STATUS_FAILED:
			return "Download failed";
		case DownloadManager.STATUS_PAUSED:
			return "Download paused";
		case DownloadManager.STATUS_PENDING:
			return "Download pending";
		case DownloadManager.STATUS_RUNNING:
			return "Download in progress!";
		case DownloadManager.STATUS_SUCCESSFUL:
			return "Download finished";
		default:
			return "Unknown Information";
		}
	}
	
	
	class AppDownTask{
		static final int state_ready = 1;
		static final int state_downloading = 2;
		static final int state_down_complete = 3;
		
		long downId;
		String url;
		String localUri;
		int downState;
	}
}
