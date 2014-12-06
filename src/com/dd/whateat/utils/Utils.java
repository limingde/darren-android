package com.dd.whateat.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dd.whateat.DdApplication;
import com.dd.whateat.R;

public class Utils {

	private static final String TAG = "Utils";

	public static void initInfoCacheFolder() {
		Utils.createSDFolder("", "/meila");
		Utils.createSDFolder("/meila", "/cache");
		Utils.createSDFolder("/meila/cache", "/image");
	}

	public static void initMusicCacheFolder() {
		Utils.createSDFolder("", "/meila");
		Utils.createSDFolder("/meila", "/cache");
		Utils.createSDFolder("/meila/cache", "/music");
	}

	public static void initPhotoCacheFolder() {
		Utils.createSDFolder("", "/meila");
		Utils.createSDFolder("/meila", "/photo");
	}

	public static void createSDFolder(String parentFolderName, String folderName) {
		File sd = Environment.getExternalStorageDirectory();
		String path = sd.getPath() + parentFolderName + folderName;
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
	}

	public static boolean isFolderExists(String strFolder) {
		Log.d("strFolder", strFolder);
		File file = new File(strFolder);
		if (file != null) {
			if (!file.exists()) {
				if (file.mkdirs()) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				return ;
			}
			if (!file.isDirectory()) {
				return ;
			}
			String[] tempList = file.list();
			File temp = null;
			if (tempList == null) {
				return ;
			}
			int size = tempList.length;
			for (int i = 0; i < size; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				}
				if (temp.isDirectory()) {
					delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
					delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				}
			}
		} catch (Exception ex) {
			DdLog.e(TAG, ex.getMessage());
		}
	}

	/**
	 * 判断存储卡是否存在
	 * 
	 * @return
	 */
	public static boolean isSDcardAvaliable() {
		return android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState());
	}

	// 提示方法
	public static void displayToastLong(final Activity act, final String mes) {
		if(TextUtils.isEmpty(mes)){
			return;
		}
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(act, mes, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, -60);
				toast.show();
			}
		});
	}
	public static void displayToast(final Activity act, final String mes) {
		if(TextUtils.isEmpty(mes)){
			return;
		}
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(act, mes, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, -60);
				toast.show();
			}
		});
	}
	public static void displayToast(final Activity act, final int res) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(act, act.getResources().getString(res), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, -60);
				toast.show();
			}
		});
	}
	
	public static void displayToastNormal(final Activity act, final String msg) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(act, msg, Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

	public static Date FormatDate(int seconds) {
		Calendar c = Calendar.getInstance();

		long millions = new Long(seconds).longValue() * 1000;
		c.setTimeInMillis(millions);

		return c.getTime();
	}

	public static long getTimeInSeconds(Calendar c) {
		long millions = c.getTimeInMillis();
		return millions / 1000;
	}

	public static String FormatDateString(Date d) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(d);
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = null;
		try {
			time = sdf.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public static String getDate(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = sdf.parse(time.replace("T", " "));
			String str = sdf.format(date1);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public static String timeAddDays(String time, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = sdf.parse(time);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			calendar.add(Calendar.DAY_OF_MONTH, days);
			Date date2 = calendar.getTime();
			String str = sdf.format(date2);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public static boolean isInSpecifiedPeriodOfTime(String strStartTime,
			String strEndTime) {
		boolean isDisableTime = false;
		String strCurrentTime = getCurrentTime();
		if (isTimeAfter(strCurrentTime, strStartTime)
				&& isTimeBefore(strCurrentTime, strEndTime)) {
			isDisableTime = true;
		}
		return isDisableTime;
	}

	public static boolean isTimeAfter(String time1, String time2) {
		boolean isAfter = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// Log.i("isTimeAfter()", "" + time1 + " --- " + time2);
			if (sdf.parse(time1).after(sdf.parse(time2))) {
				isAfter = true;
				// Log.i("isTimeAfter()", "" + time1 + "  与     " + time2 +
				// " 比较，"
				// + time1 + "在" + time2 + "之后...");
			} else {
				// Log.i("isTimeAfter()", "" + time1 + "  与     " + time2 +
				// " 比较，"
				// + time1 + "在" + time2 + "之前...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isAfter;
	}

	public static boolean isTimeBefore(String time1, String time2) {
		boolean isBefore = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// Log.i("isTimeBefore()", "" + time1 + " --- " + time2);
			if (sdf.parse(time1).before(sdf.parse(time2))) {
				isBefore = true;
				// Log.i("isTimeBefore()", "" + time1 + "  与     " + time2
				// + " 比较，" + time1 + "在" + time2 + "之前...");
			} else {
				// Log.i("isTimeBefore()", "" + time1 + "  与     " + time2
				// + " 比较，" + time1 + "在" + time2 + "之后...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isBefore;
	}

	public static String FormatDateString(int seconds) {
		return FormatDateString(FormatDate(seconds));
	}

	public static boolean CheckConnectInternet(Context context) {
		boolean isConnectInternet = false;
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetWorkInfo对象
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		// 获取当前的网络连接是否可用
		if (null == networkInfo) {
			isConnectInternet = false;
		} else {
			boolean available = networkInfo.isAvailable();
			if (available) {
				isConnectInternet = true;
			} else {
				isConnectInternet = false;
			}
		}
		return isConnectInternet;
	}

	/**
	 * 测试ConnectivityManager ConnectivityManager主要管理和网络连接相关的操作
	 * 相关的TelephonyManager则管理和手机、运营商等的相关信息；WifiManager则管理和wifi相关的信息。
	 * 想访问网络状态，首先得添加权限<uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE"/>
	 * NetworkInfo类包含了对wifi和mobile两种网络模式连接的详细描述,通过其getState()方法获取的State对象则代表着
	 * 连接成功与否等状态。
	 * 
	 */
	public static boolean CheckConnectInternet(Context context,
			Activity activity) {
		try {
			boolean isConnectInternet = false;
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			// 获取代表联网状态的NetWorkInfo对象
			NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
			// 获取当前的网络连接是否可用
			if (null == networkInfo) {
				Toast.makeText(context,
						context.getString(R.string.connect_time_out),
						Toast.LENGTH_SHORT).show();
				// 当网络不可用时，跳转到网络设置页面
				// activity.startActivityForResult(new Intent(
				// android.provider.Settings.ACTION_WIRELESS_SETTINGS), 1);

			} else {
				boolean available = networkInfo.isAvailable();
				if (available) {
					isConnectInternet = true;
					// Toast.makeText(context, "当前的网络连接可用",
					// Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context,
							context.getString(R.string.connect_time_out),
							Toast.LENGTH_SHORT).show();
				}
			}

			State state = connManager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState();
			if (State.CONNECTED == state) {
				isConnectInternet = true;
				// Toast.makeText(context, "GPRS网络已连接",
				// Toast.LENGTH_SHORT).show();
			}

			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			if (State.CONNECTED == state) {
				isConnectInternet = true;
				// Toast.makeText(context, "WIFI网络已连接",
				// Toast.LENGTH_SHORT).show();
			}

			// // 跳转到无线网络设置界面
			// startActivity(new
			// Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
			// // 跳转到无限wifi网络设置界面
			// startActivity(new
			// Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
			return isConnectInternet;
		} catch (Exception e) {
			return true;
		}
	}

	public static final float LONG_SCREEN_RATIO = (float) 1.7;

	/**
	 * 获取当前屏幕大小和密度
	 */
	public static float getScreenRatio(Activity activity) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);

		// CURRENT_DENSITY = displayMetrics.densityDpi;
		// DENSITY_RATIO = STANDARD_DENSITY / CURRENT_DENSITY;
		Log.d("屏幕比例",
				String.valueOf((float) displayMetrics.heightPixels
						/ (float) displayMetrics.widthPixels));
		return (float) displayMetrics.heightPixels
				/ (float) displayMetrics.widthPixels;

	}

	public static boolean IsLongScreen(Activity activity) {
		if (getScreenRatio(activity) > LONG_SCREEN_RATIO) {
			DdLog.d("屏幕比例", "长");
			return true;
		} else {
			DdLog.d("屏幕比例", "短");
			return false;
		}
	}

	public static void sharePhoto(String photoUri, final Activity activity) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		File file = new File(photoUri);
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		shareIntent.setType("image/jpeg");
		activity.startActivity(Intent.createChooser(shareIntent,
				activity.getTitle()));
	}

	public static boolean isServiceStart(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> infos = am.getRunningServices(30);
		// 30是最大值
		for (RunningServiceInfo info : infos) {
			if (info.service.getClassName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static int getViewMeasureHeight(View view) {
		if (view != null) {
			view.measure(0, 0);
			return view.getMeasuredHeight();
		}
		return 0;
	}

	/**
	 * 设置listview高度，以适应内容
	 * 
	 * @param listView
	 *            指定的listview
	 */
	public static void setListViewHeightMatchContent(ListView listView) {
		try {
			// 获取ListView对应的Adapter
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				return;
			}

			int totalHeight = 0;
			int length = listAdapter.getCount();
			for (int i = 0; i < length; i++) { // listAdapter.getCount()返回数据项的数目
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0); // 计算子项View 的宽高
				totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
				Log.d("debug", "setListViewHeightMatchContent, " + i + ", "
						+ listItem.getMeasuredHeight() + ", "
						+ listItem.getBackground().getIntrinsicHeight());
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			int tmpHeight = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			Log.d("debug", "setListViewHeightMatchContent, h: " + tmpHeight);
			tmpHeight = px2dip(DdApplication.CONTEXT, tmpHeight);
			Log.d("debug", "setListViewHeightMatchContent, h: " + tmpHeight);
			params.height = tmpHeight;
			// listView.getDividerHeight()获取子项间分隔符占用的高度
			// params.height最后得到整个ListView完整显示需要的高度
			listView.setLayoutParams(params);

		} catch (Exception e) {
		}
	}

	public static int getListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		return totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	}

	/**
	 * 将change等宽高比进行缩放，缩放到最大，且宽高均不超过base
	 * 
	 * @param base
	 * @param change
	 * @return
	 */
	public static Rect getScaleRect(Rect base, Rect change) {
		float scaleRate;
		float rate1 = ((float) base.height()) / base.width();
		float rate2 = ((float) change.height()) / change.width();
		if (rate1 > rate2) {
			// 以change的w为准进行等比缩放
			scaleRate = ((float) change.width()) / base.width();
		} else {
			// 以change的h为准进行等比缩放
			scaleRate = ((float) change.height()) / base.height();
		}
		int outw = (int) (change.width() / scaleRate);
		int outh = (int) (change.height() / scaleRate);
		Rect outRect = new Rect(0, 0, outw, outh);
		return outRect;
	}

	public static int checkItemHeight(ListView listview) {
		int itemHeight = 0;

		int headerFooterCount = listview.getHeaderViewsCount()
				+ listview.getFooterViewsCount();
		ListAdapter adapter = listview.getAdapter();
		if (adapter == null || adapter.getCount() == headerFooterCount) {
			itemHeight = 0;
			DdLog.d(TAG, "checkItemHeight, ih: " + itemHeight);
			return itemHeight;
		}

		int itemCount = adapter.getCount();

		// 有元素
		if (itemCount > headerFooterCount) {
			int firstPos = listview.getFirstVisiblePosition();
			int lastPos = listview.getLastVisiblePosition();

			boolean bFind = false;

			int childIdx;
			for (childIdx = firstPos; childIdx <= lastPos; ++childIdx) {
				if (childIdx >= listview.getHeaderViewsCount()
						&& childIdx < (itemCount - listview
								.getFooterViewsCount())) {
					bFind = true;
					break;
				}
			}
			if (bFind) {
				// 两种计算方法，getAdapter().getView似乎比getChildAt准确度高些
				// View itemView = getChildAt(childIdx);
				View itemView = listview.getAdapter().getView(childIdx, null,
						listview);
				if (itemView != null) {
					measureView(itemView);
					itemHeight = itemView.getMeasuredHeight();
					if (itemHeight <= 0) {
						itemView = listview.getChildAt(childIdx);
						if (itemView != null) {
							measureView(itemView);
							itemHeight = itemView.getMeasuredHeight();
						}
					}
				}
			}
		}

		DdLog.d(TAG, "checkItemHeight, ih: " + itemHeight);
		return itemHeight;
	}

	private static void measureView(View child) {
		try {
			ViewGroup.LayoutParams p = child.getLayoutParams();
			if (p == null) {
				p = new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
			}

			int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
					p.width);
			int lpHeight = p.height;
			int childHeightSpec;
			if (lpHeight > 0) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
						MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		} catch (Exception e) {
		}

	}

	public static File convertImageUriToFile(Uri imageUri, Activity activity) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID,
					MediaStore.Images.ImageColumns.ORIENTATION };
			cursor = activity.managedQuery(imageUri, proj, // Which columns to
															// return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null); // Order-by clause (ascending by name)
			int file_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			int orientation_ColumnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

			if (cursor.moveToFirst()) {
				String orientation = cursor.getString(orientation_ColumnIndex);
				return new File(cursor.getString(file_ColumnIndex));
			}
			return null;
		} catch (Exception e) {
			DdLog.d(TAG, e.getMessage());
			return null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public static String getPhotoPath() {
		File dir = new File(Environment.getExternalStorageDirectory(),
				"/meila/photo");
		if (!dir.exists()) {
			dir.mkdirs();
		} else if (!dir.isDirectory()) {
			if (dir.delete()) {
				dir.mkdirs();
			}
		}

		return dir.getAbsolutePath();
	}

	// 取随机大写字母
	public static String randomString() {
		StringBuffer result = new StringBuffer();
		String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		char[] c = text.toCharArray();
		Random random = new Random();
		for (int i = 0; i < 11; i++) {
			result.append(c[random.nextInt(c.length)]);
		}
		return result.toString();
	}

	public static void measureView(View child, ViewGroup.LayoutParams p) {
		try {
			int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
					p.width);
			int lpHeight = p.height;
			int childHeightSpec;
			if (lpHeight > 0) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
						MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		} catch (Exception e) {
		}

	}
	/**
	 * 有时候子view跟父view的touchEvent会冲突，导致子view的touchEvent事件不流畅，可以调用这个方法，让触摸在子view上时，父view不要拦截touchEvent
	 */
	public static void requestTouchEvent(final View childView, final int childViewId){
		if(childView == null || childViewId == 0){
			return;
		}
		childView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (v.getId() == childViewId) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    }
                }
//				childView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
			}
		});
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param act
	 */
	public static void hideSoftInput(Activity act) {
		try {
			if (act == null) {
				return;
			}
			final View v = act.getWindow().peekDecorView();
			if (v != null && v.getWindowToken() != null) {
				InputMethodManager imm = (InputMethodManager) act
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				// method 1
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				// method 2
				// imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),
				// InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 显示软键盘
	 * 
	 * @param context
	 */
	public static void showSoftInput(Context context) {
		try {
			InputMethodManager m = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
		}
	}

	/**
	 * 软键盘是否显示
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSoftInputShow(Context context) {
		try {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			return imm.isActive();
		} catch (Exception e) {
		}
		return false;
	}

	// 利用反射机制，通过资源名字得到资源的ID
	public static int getResourceId(String name) {
		try {
			Field field = R.drawable.class.getField(name);
			return Integer.parseInt(field.get(null).toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	public static String getRefreshTime(){
    	SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
    	Date now = new Date();
    	return format.format(now);
    }
	
	private static int curSDKVersion = -1;

	public static int getCurrentSDKVersion() {
		try {
			if (curSDKVersion == -1) {
				curSDKVersion = android.os.Build.VERSION.SDK_INT;
			}
		} catch (Exception ex) {
			DdLog.e(TAG, ex.getMessage());
		}
		return curSDKVersion;
	}
	
	public static String getSystemModel() {
		String model = "null";
		try {
			model = android.os.Build.MODEL;
		} catch (Exception ex) {
			DdLog.e(TAG, ex.getMessage());
		}
		return model;
	}
	
	public static String getSysRelOs() {
		String os = "null";
		try {
			os = android.os.Build.VERSION.RELEASE;
		} catch (Exception ex) {
			DdLog.e(TAG, ex.getMessage());
		}
		return os;
	}
	
	@SuppressLint("NewApi")
	public static void copyTextToClipboard(Context context, String text) {
		try{
			ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(text);
		}catch (Throwable e) {
		}
	}
	
	/**
	 * 比较两个字符串，如果两个都为null，则不判为相等
	 * @param s1
	 * @param s2
	 * @param ignoreCase
	 * @return
	 */
	public static boolean equalsNotNull(String s1, String s2, boolean ignoreCase){
		if(s1 == null){
			return false;
		}else{
			if(s2 == null){
				return false;
			}else{
				return ignoreCase?s1.equalsIgnoreCase(s2):s1.equals(s2);
			}
		}
	}
	/**
	 * 比较两个字符串，如果两个都为null，也认为相等
	 * @param s1
	 * @param s2
	 * @param ignoreCase
	 * @return
	 */
	public static boolean equals(String s1, String s2, boolean ignoreCase){
		if(s1 == null){
			return (s2 == null);
		}else{
			return ignoreCase?s1.equalsIgnoreCase(s2):s1.equals(s2);
		}
	}
	
	public static boolean isForXiaomi(){
		if(DdApplication.CONTEXT != null){
			String channal = DdApplication.CONTEXT.getResources().getString(R.string.channel);
			return ("xiaomi".equalsIgnoreCase(channal));
		}
		return false;
	}
	public static boolean isForTencent(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("tencent".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}

	public static boolean isForAnZhuo(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("anzhuo".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}
	public static boolean isForBaiDu(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("baidu".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}
	public static boolean isForVivo(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("vivo".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}
	public static boolean isForVMall(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("vmall".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}
	public static boolean isFor91(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("91sjzs".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}
	public static boolean isForLenovo(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("Lenovo".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}
	public static boolean is360sjzs(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("360sjzs".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}
	public static boolean isForLianhe(){
		return isForBaiDu() || isFor91() || isForAnZhuo();
//		if(DdApplication.CONTEXT != null){
//			try{
//				String channel = DdApplication.CONTEXT.getString(R.string.channel);
//				return ("Lianhe".equalsIgnoreCase(channel));
//			}catch (Exception e) {
//			}
//		}
//		return false;
	}

	public static boolean shouldHideMoreApp(){
		if(DdApplication.CONTEXT != null){
			try{
				String channel = DdApplication.CONTEXT.getString(R.string.channel);
				return ("AnZhi".equalsIgnoreCase(channel)
						|| "VMall".equalsIgnoreCase(channel)
						|| "Gfan".equalsIgnoreCase(channel));
			}catch (Exception e) {
			}
		}
		return false;
	}
	
	public static boolean isPackageAlreadyInstall(Context context, String package_name) {
		try {
			if (!TextUtils.isEmpty(package_name) && context != null) {
				List<PackageInfo> list = context.getPackageManager().getInstalledPackages(0);
				int size = list.size();
				
				for (int i=0; i<size; i++) {
					PackageInfo info = list.get(i);
					if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
						// 非系统应用
						if (package_name.equalsIgnoreCase(info.packageName)) {
							return true;
						}
					}
				}
			}
		} catch (Exception ex) {
			DdLog.e(TAG, ex.getMessage());
		}
		
		return false;
	}

    public static float pixelToDp(Context context, float val) {
        float density = context.getResources().getDisplayMetrics().density;
        return val * density;
    }
    
}
