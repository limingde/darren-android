package com.dd.whateat.constents;

public final class DdConstants {
	// app name
	public static final String APPLICATION_NAME = "DDAPP";

	// 获取用户图片
	public static final String MIME_TYPE_IMAGE = "image/*";

	// 临时文件
	public static final String EXTRA_TEMP_PHOTO_NAME = "meila_camera.jpg";

	// 头像像素大小512
	public static final int HEADER_PX = 512;

	// 临时图片
	public static final String CACHE_KEY_TEMP_IMG_URI = "cache_key_temp_img_uri";

	public static final int k_BUFFER_SIZE = 1024 * 4;

	// 评论字数限制 10 - 1000
	public static final int CONTENT_LENGTH_MIN = 0;
	public static final int CONTENT_LENGTH_MAX = 1000000;

	// 话题字数限制 10 - 1000
	public static final int HUATI_TITLE_LENGTH_MIN = 5;
	public static final int HUATI_TITLE_LENGTH_MAX = 100;
	public static final int HUATI_CONTENT_LENGTH_MIN = 0;
	public static final int HUATI_CONTENT_LENGTH_MAX = 1000000;
	public static final int PINGLUN_CONTENT_LENGTH_MIN = 0;
	public static final int PINGLUN_CONTENT_LENGTH_MAX = 1000000;
	public static final int HUIFU_CONTENT_LENGTH_MIN = 0;
	public static final int HUIFU_CONTENT_LENGTH_MAX = 1000000;

	//点评数超过20条，参与详情页排名
	public static final int PRODUCT_DETAIL_PAIMING_MIN_COMMENTED_COUNT = 20;

	//清除http缓存的时间间隔
	public static final int DEL_HTTP_CACHE_INTERVAL_IN_MS = 7*24*60*60*1000;
	//清除IMG缓存的时间间隔
	public static final int DEL_IMAGE_CACHE_INTERVAL_IN_MS = 7*24*60*60*1000;

	public static final String FEEDBACK_OFFICAIL_USER_SLUG = "18b12149";

	public static final int RELOAD_DELAY = 1*60*60;



	public static final int REQUEST_CODE_ADD_SHOW_PIC = 2014;//上传美妆图片

	public static final int REQUEST_CODE_PICK_ADDR = 2015;//选择地址

	public static final int REQUEST_CODE_VIDEO_FULL_SCREEN = 2016;//全屏播放

	public static final int REQUEST_CODE_PICK_TAG = 2017;//选择TAG
	
	// 美啦客服的slug
	public static final String MEILA_CUSTOMER_SERVICE = "美啦客服";
	
	public static final String MEILA_CUSTOMER_SERVICE_SLUG = "c3ca64ec";
}
