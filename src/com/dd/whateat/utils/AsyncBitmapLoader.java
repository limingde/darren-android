package com.dd.whateat.utils;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dd.whateat.bean.DdConst;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class AsyncBitmapLoader {

	public final static String TAG = "AsyncBitmapLoader";

	private static final int SLEEP_TIME_FOR_GC_MS = 1000;

	public int maxW = 1024;
	public int maxH = 1024;
	private int qualityDefault = 80;
	final public int maxDownloadBmpFileSize = 0;// 从网上下载时，设置这个值，就对图片质量进行处理
	final long maxBmpSize = Runtime.getRuntime().maxMemory() / 10;// 加载到内存的单张图片所允许的最大占用内存，超过则通过设置quality来压缩
	public Config inPreferredConfig = Config.RGB_565;// 如果用RGB_565，某些图片的白色部分会有变色，不过换成ARGB_8888又比较占内存

	boolean isLocalBitmap = false;// 传进来的是url还是图片的本地path

	private float mRoundPx = 0;
	public static String ImgCacheDir = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/meila/cache/image/";
	private boolean mIsCache = true;
	public boolean needSetNullIfNotHitCache = false;// 图片没在内存缓存中时，是否立即设置图片为null

	private String mCacheExtName = null;

	private ImgCacheUtil mImgCacheUtil;

	// private byte[] lock = new byte[0];
	// private boolean isLocked = false;

	// class CacheItem{
	// String path;
	// SoftReference<Bitmap> bmp;
	// int maxW;
	// int maxH;
	// }

	/**
	 * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
	 */
	// ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
	int corePoolSize = 2, maximumPoolSize = 4, workQueueSize = 32;
	ThreadPoolExecutor fixedThreadPool;

	public AsyncBitmapLoader() {
		init();
	}

	public AsyncBitmapLoader(String cacheExtName) {
		mCacheExtName = cacheExtName;
		init();
	}

	public AsyncBitmapLoader(float roundPx, int width) {
		mRoundPx = roundPx;
		maxW = width;
		init();
	}

	public AsyncBitmapLoader(float roundPx, int width, boolean isCache) {
		mRoundPx = roundPx;
		maxW = width;
		mIsCache = isCache;
		init();
	}

	public AsyncBitmapLoader(int corePoolSize, int maximumPoolSize,
			int workQueueSize) {
		if (corePoolSize > 0) {
			this.corePoolSize = corePoolSize;
		}
		if (maximumPoolSize >= corePoolSize) {
			this.maximumPoolSize = maximumPoolSize;
		}
		if (workQueueSize >= 0) {
			this.workQueueSize = workQueueSize;
		}
		init();
	}

	void init() {
		mImgCacheUtil = new ImgCacheUtil();
		getWH();
		initThreadPool();
	}

	void initThreadPool() {
		fixedThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
						workQueueSize));
		fixedThreadPool
				.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	public void setMaxH(int h) {
		maxH = h;
	}

	public void setMaxW(int w) {
		maxW = w;
	}

	void getWH() {
		// try{
		// maxW =
		// MeilaApplication.CONTEXT.getResources().getDisplayMetrics().widthPixels*2;
		// maxH =
		// MeilaApplication.CONTEXT.getResources().getDisplayMetrics().heightPixels*2;
		// }catch (Exception e) {
		// DdLog.e(TAG, "cannot get DisplayMetrics");
		// }
	}

	public void setLocalBitmapFlag(boolean isLocal) {
		this.isLocalBitmap = isLocal;
	}

	public String parseImgUrlPrefix(String url) {
		if (TextUtils.isEmpty(url)) {
			return url;
		}

		String parsedUrl;
		if (!isLocalBitmap && !url.toLowerCase().startsWith("http:")
				&& !url.toLowerCase().startsWith("https:")) {
			if (DdConst.getConst() == null
					|| DdConst.getConst().ImgHttpPrefix == null) {
				parsedUrl = url;
			} else {
				parsedUrl = HttpUtils.concatUrl(
						DdConst.getConst().ImgHttpPrefix, url);
			}
		} else {
			parsedUrl = url;
		}
		return parsedUrl;
	}

	public String getBitmapName(String imageURL) {
		// final String bitmapName;
		// if (mCacheExtName != null) {
		// bitmapName = mCacheExtName
		// + imageURL.substring(imageURL.lastIndexOf("/") + 1);
		// } else {
		// bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
		// }

		String tmp = parseImgUrlPrefix(imageURL);
		return TextUtils.isEmpty(tmp) ? null : Md5Util.strToMd5(tmp);
	}

	public String getBitmapLocatPath(String imageURL) {
		final String bitmapName = getBitmapName(imageURL);
		final String path = createImgChildPath(bitmapName) + bitmapName;
		return path;
	}

	public boolean isBitmapExist(String imgLocalPath) {
		try {
			File temp = new File(imgLocalPath);
			if (temp.exists() && temp.isFile() && temp.length() > 100) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	// public void lock(){
	// isLocked = true;
	// }
	// public void unlock(){
	// synchronized (lock) {
	// isLocked = false;
	// lock.notifyAll();
	// }
	// }
	
	/**
	 * 获取网络图片下载的本地地址
	 * @param url
	 * @return
	 */
	public String getLoadImagePath(String imgUrl) {
		final String bitmapName = getBitmapName(imgUrl);
		String path = createImgChildPath(bitmapName)
				+ bitmapName;
		return path;
	}

	public Bitmap loadBitmap(final View imageView, final String url,
			final ImageCallBackWithParams callbackWithParams,
			final Object... params) {
		return loadBitmap(imageView, url, callbackWithParams, null, params);
	}

	public Bitmap loadBitmap(final View imageView, final String url,
			final ImageCallBackWithParams callbackWithParams,
			final DownloadProgressListener progressListener,
			final Object... params) {

		if (progressListener != null) {
			progressListener.onStart();
		}

		if (imageView != null && url != null) {
			imageView.setTag(url);
		}

		final int maxFileSize = qualityDefault >= 100 ? 0
				: maxDownloadBmpFileSize;

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (progressListener != null) {
					progressListener.onDone();
				}

				Bitmap bmp = (Bitmap) msg.obj;
				DdLog.d(TAG, "handleMessage, " + bmp + ", " + url);

				if (callbackWithParams != null) {
					callbackWithParams.imageLoad(imageView, bmp, params);
				}
			}
		};

		if (TextUtils.isEmpty(url)) {
			// DdLog.e(TAG, new Exception("invalid img url: "+url));
			DdLog.e(TAG, "invalid img url: " + url);
			Message msg = handler.obtainMessage(0, null);
			handler.sendMessage(msg);
			return null;
		}
		// 处理前缀
		final String imgUrl = parseImgUrlPrefix(url);

		final String bitmapName = isLocalBitmap ? imgUrl
				: getBitmapName(imgUrl);

		// 在内存缓存中，则返回Bitmap对象
		Bitmap bitmap = BmpMemCache.getCache().getFromMemoryCache(bitmapName);

		// 如果内存中有图片，就加载，如果用handler post在callback里面加载，会导致界面抖动
		if (needSetNullIfNotHitCache) {
			try {
				if (imageView != null && imageView instanceof ImageView) {
					if (bitmap == null) {
						((ImageView) imageView).setImageBitmap(null);
					} else {
						((ImageView) imageView).setImageBitmap(bitmap);
					}
				} else {
					DdLog.e(TAG, "loadBitmap imageview is null...");
				}
			} catch (Exception e) {
				DdLog.e(TAG, e);
			}
		}

		if (bitmap != null) {
			DdLog.d(TAG, "在内存中 " + bitmapName);

			Message msg = handler.obtainMessage(0, bitmap);
			handler.sendMessage(msg);

			return bitmap;
		} else {
			/**
			 * 加上一个对本地缓存的查找
			 */
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {

					Bitmap bitmap = null;

					String bitmapFullPath = isLocalBitmap ? bitmapName
							: (createImgChildPath(bitmapName) + bitmapName);
					File temp = new File(bitmapFullPath);
					if (temp.exists() && temp.isFile() && temp.length() > 100) {
						bitmap = decodeLocalFile(bitmapFullPath,
								maxW > 0 ? maxW : 0, maxH > 0 ? maxH : 0,
								maxFileSize);
					} else {
						if (temp.exists() && temp.isDirectory()) {
							deleteFileRecursive(temp);
						}
						if (temp.exists() && temp.isFile()
								&& temp.length() < 100) {
							temp.delete();
						}
					}

					if (bitmap != null) {
						Log.d(TAG, "在文件中 " + bitmapName + ", url: " + url
								+ ", path: " + bitmapFullPath);

						if (mRoundPx > 0) {
							bitmap = BitmapUtils.getRoundedCornerBitmap(bitmap,
									mRoundPx);
							DdLog.d(TAG, "mRoundPx: " + mRoundPx + ", "
									+ bitmap);
						}

						if (mIsCache) {
							BmpMemCache.getCache().addBmpToMemoryCache(
									bitmapName, bitmap);
						}
						Message msg = handler.obtainMessage(0, bitmap);
						handler.sendMessage(msg);
					} else {
						// 如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片
						DdLog.d(TAG, "网络加载 " + imgUrl);
						int[] param = new int[1];
						InputStream bitmapIs = HttpUtils.getStreamFromURL(
								imgUrl, param);
						
						File dir = new File(createImgChildPath(bitmapName));
						if (!dir.exists()) {
							boolean isOk = dir.mkdirs();
							if (!isOk) {
								DdLog.e(
										TAG,
										"mkdirs failed, "
												+ dir.getAbsolutePath());
							}
						}

						String path = createImgChildPath(bitmapName)
								+ bitmapName;
						boolean isSaveOk = BitmapUtils.saveBitmap(bitmapIs,
								path, param, progressListener);
						if (isSaveOk) {
							bitmap = decodeLocalFile(path, maxW > 0 ? maxW : 0,
									maxH > 0 ? maxH : 0, maxFileSize);
							mImgCacheUtil.updateImageCacheDB(bitmapName);
							DdLog.d(TAG, "decodeLocalFile after save, "
									+ bitmap + ", " + path + ", url: " + url);
						} else {
							DdLog.e(TAG, "saveBitmap, from web failed, "
									+ path + ", url: " + url);
						}

						try {
							if (mRoundPx > 0) {
								bitmap = BitmapUtils.getRoundedCornerBitmap(
										bitmap, mRoundPx);
								DdLog.d(TAG, "mRoundPx: " + mRoundPx + ", "
										+ bitmap);
							}

						} catch (Exception e) {
							DdLog.e(TAG, e);
						}
						if (mIsCache) {
							BmpMemCache.getCache().addBmpToMemoryCache(
									bitmapName, bitmap);
						}
						Message msg = handler.obtainMessage(0, bitmap);
						handler.sendMessage(msg);
					}
				}
			});

		}

		return null;
	}

	/**
	 * 加载不压缩的图片
	 * 
	 * @param imageView
	 * @param url
	 * @param callbackWithParams
	 * @param params
	 * @return
	 */
	public Bitmap loadQualityBitmap(final View imageView, final String url,
			final ImageCallBackWithParams callbackWithParams,
			final Object... params) {

		final int maxFileSize = 0;
		final int quality = 100;

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bitmap bmp = (Bitmap) msg.obj;
				DdLog.d(TAG, "handleMessage, " + bmp + ", " + url);

				if (callbackWithParams != null) {
					callbackWithParams.imageLoad(imageView, bmp, params);
				}
			}
		};

		if (TextUtils.isEmpty(url)) {
			DdLog.e(TAG, new Exception("invalid img url: " + url));

			Message msg = handler.obtainMessage(0, null);
			handler.sendMessage(msg);
			return null;
		}
		// 处理前缀
		final String imgUrl;
		if (!url.toLowerCase().startsWith("http:")
				&& !url.toLowerCase().startsWith("https:")) {
			imgUrl = HttpUtils.concatUrl(DdConst.getConst().ImgHttpPrefix,
					url);
		} else {
			imgUrl = url;
		}

		final String bitmapName = getBitmapName(imgUrl);

		// 在内存缓存中，则返回Bitmap对象
		Bitmap bitmap = BmpMemCache.getCache().getFromMemoryCache(bitmapName);
		if (bitmap != null) {
			DdLog.d(TAG, "在内存中 " + bitmapName);

			Message msg = handler.obtainMessage(0, bitmap);
			handler.sendMessage(msg);

			return bitmap;
		} else {

			/**
			 * 加上一个对本地缓存的查找
			 */
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {

					Bitmap bitmap = null;

					File temp = new File(createImgChildPath(bitmapName)
							+ bitmapName);
					if (temp.exists() && temp.isFile() && temp.length() > 100) {
						bitmap = decodeLocalFile(createImgChildPath(bitmapName)
								+ bitmapName, 0, 0, maxFileSize);
					} else {
						if (temp.exists() && temp.isDirectory()) {
							deleteFileRecursive(temp);
						}
						if (temp.exists() && temp.isFile()
								&& temp.length() < 100) {
							temp.delete();
						}
					}

					if (bitmap != null) {
						// Log.d(TAG, "在文件中 "+bitmapName);
						// Log.d(TAG,
						// "在文件中 "+bitmapName+", url: "+url+", path: "+temp.getAbsolutePath());
						if (mIsCache) {
							BmpMemCache.getCache().addBmpToMemoryCache(
									bitmapName, bitmap);
						}
						Message msg = handler.obtainMessage(0, bitmap);
						handler.sendMessage(msg);
					} else {
						// 如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片
						Log.d(TAG, "网络加载 " + imgUrl);
						InputStream bitmapIs = HttpUtils
								.getStreamFromURL(imgUrl);
						
						File dir = new File(createImgChildPath(bitmapName));
						if (!dir.exists()) {
							boolean isOk = dir.mkdirs();
							if (!isOk) {
								DdLog.e(
										TAG,
										"mkdirs failed, "
												+ dir.getAbsolutePath());
							}
						}

						String path = createImgChildPath(bitmapName)
								+ bitmapName;
						boolean isSaveOk = BitmapUtils.saveBitmap(bitmapIs,
								path);
						if (isSaveOk) {
							bitmap = decodeLocalFile(path, 0, 0, maxFileSize);
							mImgCacheUtil.updateImageCacheDB(bitmapName);
							// DdLog.d(TAG,
							// "decodeLocalFile after save, "+bitmap+", "+path);
						} else {
							DdLog.e(TAG, "saveBitmap, from web failed, "
									+ path);
						}
						if (mIsCache) {
							BmpMemCache.getCache().addBmpToMemoryCache(
									bitmapName, bitmap);
						}
						Message msg = handler.obtainMessage(0, bitmap);
						handler.sendMessage(msg);
					}
				}
			});

		}

		return null;
	}

	public void decodeLocalFileFromLocalPath(final View imageView,
			final String path,
			final ImageCallBackWithParams callbackWithParams,
			final Object... params) {

		if (imageView != null && path != null) {
			imageView.setTag(path);
		}

		final int maxFileSize = qualityDefault >= 100 ? 0
				: maxDownloadBmpFileSize;

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bitmap bmp = (Bitmap) msg.obj;
				DdLog.d(TAG, "handleMessage, " + bmp + ", " + path);

				if (callbackWithParams != null) {
					callbackWithParams.imageLoad(imageView, bmp, params);
				}
			}
		};

		if (TextUtils.isEmpty(path)) {
			// DdLog.e(TAG, new Exception("invalid img url: "+url));
			DdLog.e(TAG, "invalid img localpath: " + path);
			Message msg = handler.obtainMessage(0, null);
			handler.sendMessage(msg);
			return;
		}

		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = decodeLocalFile(path, maxW > 0 ? maxW : 0,
						maxH > 0 ? maxH : 0, maxFileSize);
				Message msg = handler.obtainMessage(0, bitmap);
				handler.sendMessage(msg);
			}
		});
	}

	public Bitmap decodeLocalFileFromUrl(String url, int requiredSizeW,
			int requiredSizeH, int maxFileSize) {
		String path = getBitmapLocatPath(url);
		return decodeLocalFile(path, requiredSizeW, requiredSizeH, maxFileSize);
	}

	/**
	 * 加载本地图片
	 * 
	 * @param path
	 * @param requiredSizeW
	 *            请求图片的最大宽，如果为0 ，则不限制
	 * @param requiredSizeH
	 *            请求图片的最大高，如果为0 ，则不限制
	 * @return
	 */
	public Bitmap decodeLocalFile(String path, int requiredSizeW,
			int requiredSizeH, int maxFileSize) {
		File f = new File(path);
		return decodeLocalFile(f, requiredSizeW, requiredSizeH, maxFileSize);
	}

	/**
	 * decodes image and scales it to reduce memory consumption
	 * 
	 * @param f
	 * @param requiredSizeW
	 *            请求图片的最大宽，如果为0 ，则不限制
	 * @param requiredSizeH
	 *            请求图片的最大高，如果为0 ，则不限制
	 * @return
	 */
	public Bitmap decodeLocalFile(File f, int requiredSizeW, int requiredSizeH,
			int maxFileSize) {
		try {
			String readPath = f.getAbsolutePath();

			// 对图片质量进行处理
			int bmpSize = getBitmapSize(readPath);
			if (bmpSize > maxBmpSize) {
				String savePath = getBitmapName(readPath);// 压缩图片放到图片缓存目录
				DdLog.d(TAG, "compress, " + readPath + " --> " + savePath);
				File saveFile = new File(savePath);

				if (saveFile.isFile() && saveFile.length() > 100) {
					readPath = savePath;
					DdLog.d(TAG, "has compressed");
				} else {
					if (saveFile.isDirectory()) {
						deleteFileRecursive(saveFile);
					}
					if (saveFile.isFile()) {
						saveFile.delete();
					}

					int quality = (int) (((float) maxBmpSize) / bmpSize * 100f);
					// DdLog.d(TAG,
					// "compressed quality: "+quality+", bmpSize: "+bmpSize+", maxBmpSize: "+maxBmpSize);
					boolean isResizeOk = BitmapUtils.reduceBmpQuality(readPath,
							savePath, quality);
					if (isResizeOk && saveFile.isFile()
							&& saveFile.length() > 100) {
						readPath = savePath;
						// DdLog.d(TAG,
						// "compress ok, old: "+f.getAbsolutePath()+", new: "+saveFile.getAbsolutePath());
					} else {
						DdLog.e(TAG, "compress failed");
					}
				}
			}

			// 对图片大小进行缩放处理
			Bitmap tmpBmp = BitmapUtils.resizeBmp(new File(readPath),
					requiredSizeW, requiredSizeH, inPreferredConfig);
			if (tmpBmp != null) {
				DdLog.d(TAG, "after decode, w: " + tmpBmp.getWidth()
						+ ", h: " + tmpBmp.getHeight() + ", size: "
						+ getBitmapSize(tmpBmp) + ", requiredSizeW: "
						+ requiredSizeW + ", requiredSizeH: " + requiredSizeH
						+ ", inPreferredConfig: " + inPreferredConfig);
			}

			return tmpBmp;
		} catch (Throwable e) {
			DdLog.e(TAG, e);
			if (e instanceof OutOfMemoryError) {
				System.gc();
				// try {
				// Thread.sleep(SLEEP_TIME_FOR_GC_MS);
				// } catch (InterruptedException e1) {
				// DdLog.e(TAG, e);
				// }
			}
		}

		return null;
	}

	/**
	 * 递归删除一个目录
	 * 
	 * @param dir
	 */
	protected void deleteFileRecursive(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.isFile()) {
				f.delete();
			} else {
				deleteFileRecursive(f);
			}
		}
		dir.delete();
	}

	/**
	 * 计算图片占用内存大小，单位：字节
	 * 
	 * @param bmp
	 *            要计算大小的图片
	 * @return 图片大小
	 */
	static int getBitmapSize(BitmapFactory.Options opts) {
		return opts == null ? 0 : opts.outWidth * opts.outHeight
				* getBytesPerPixel(opts.inPreferredConfig);
	}

	static int getBitmapSize(Bitmap bitmap) {
		return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight();
	}

	static int getBitmapSize(String path) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			return getBitmapSize(opts);
		} catch (Throwable e) {
			Log.e(TAG, "getBitmapSize failed", e);
		}
		return 0;
	}

	static int getBytesPerPixel(Config config) {
		if (config == null || config == Config.ARGB_8888) {
			return 4;
		} else if (config == Config.RGB_565) {
			return 2;
		} else if (config == Config.ARGB_4444) {
			return 2;
		} else if (config == Config.ALPHA_8) {
			return 1;
		}
		return 1;
	}

	/**
	 * 图片保存路径优化，按照首字母和 次字母创建文件夹
	 * 
	 * @param bitmapName
	 * @return
	 */
	public String createImgChildPath(String bitmapName) {
		String newPath = null;
		if (bitmapName == null || bitmapName.length() == 0) {
			return newPath = bitmapName;
		}
		if (bitmapName.length() == 1) {
			String newFirstPath = ImgCacheDir + bitmapName + "/";
			File f = new File(newFirstPath);
			if (f != null && !f.exists()) {
				f.mkdirs();
			}
			// DdLog.d(TAG, "decodeLocalFile after createImgChildPath:" +
			// newFirstPath);
			newPath = newFirstPath;
		} else if (bitmapName.length() == 2) {
			String newSecStringPath = ImgCacheDir + bitmapName.substring(0, 1)
					+ "/" + bitmapName.substring(0, 2) + "/";
			File f = new File(newSecStringPath);
			if (f != null && !f.exists()) {
				f.mkdirs();
			}
			// DdLog.d(TAG, "decodeLocalFile after createImgChildPath:" +
			// newSecStringPath);
			newPath = newSecStringPath;
		} else {
			String newThrStringPath = ImgCacheDir + bitmapName.substring(0, 1)
					+ "/" + bitmapName.substring(0, 2) + "/"
					+ bitmapName.substring(0, 3) + "/";
			File f = new File(newThrStringPath);
			if (f != null && !f.exists()) {
				f.mkdirs();
			}
			// DdLog.d(TAG, "decodeLocalFile after createImgChildPath:" +
			// newSecStringPath);
			newPath = newThrStringPath;
		}
		return newPath;
	}

	/**
	 * 回调接口
	 */

	public interface ImageCallBackWithParams {
		public void imageLoad(View imageView, Bitmap bitmap, Object... params);
	}

	public interface DownloadProgressListener {
		public void onStart();

		public void onProgress(int progress, int max);

		public void onDone();
	}
}