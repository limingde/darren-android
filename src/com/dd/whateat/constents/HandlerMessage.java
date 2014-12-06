package com.dd.whateat.constents;

public final class HandlerMessage {
	// message
	public final static int INIT_DATA = 0;

	// 跳转
	public final static int SEND_JUMP = 1;

	// 关注
	public final static int ATTEN_OTHER_USER = 2;

	// 跳转查看大图
	public final static int BIG_IMAGE = 3;

	// 刷新数据
	public final static int REFRESH_DATA = 4;

	// 刷新tab
	public final static int INIT_TAB = 5;

	// 刷新关注新消息
	public final static int REFRESH_FAN_NEW_MSGNUM = 6;
	// 刷新关注新消息
	public final static int REFRESH_USER_NEW_MSGNUM = 8;

	public final static int SEARCH_FANS = 9;

	// 跳转到打分页
	public final static int JUMP_RANK = 11;

	// 跳转到上传图片页
	public final static int JUMP_TO_UPLOAD_IMG = 12;

	// 跳转到历史记录页
	public final static int JUMP_TO_HISTORY = 13;

	// 分享
	public final static int SHARE = 14;

	// 跳转到用户
	public final static int JUMP_USER = 15;

	// 显示新手引导
	public final static int SHOW_GUIDE = 16;

	// 添加圈子
	public final static int ADD_MASS = 17;

	// 列表第一列数据
	public final static int TAB_ONE_LIST = 18;

	// 列表第一列数据
	public final static int TAB_TWO_LIST = 19;

	// 列表第一列数据
	public final static int TAB_THREE_LIST = 20;

	// 列表选中的项目
	public final static int LIST_SELECTED_INDEX = 21;

	// 获取列表
	public final static int GET_LIST_DATA = 22;

	// 获取子列表
	public final static int GET_SUB_LIST_DATA = 23;

	// 跳转到圈子详情
	public final static int JUMP_MASS_DETAIL = 24;

	// 只是刷新adapter
	public final static int REFRESH_ADAPTER = 25;

	// 创建圈子
	public final static int CREATE_MASS = 26;

	// 删除圈子
	public final static int DELETE_MASS = 27;

	// 置顶圈子
	public final static int TOP_MASS = 28;

	// 剔除圈成员
	public final static int DELETE_MASS_MEMBER = 29;

	// 审核成员
	public final static int CHECK_MASS_MEMBER = 30;

	//移除黑名单
	public final static int REMOVE_BLACK_LIST = 31;

	//添加黑名单
	public final static int ADD_BLACK_LIST = 32;

	// 跳转的签到页面
	public final static int JUMP_TO_CHECK_ACTIVITY = 33;

	// 美甲产品
	public final static int JUMP_TO_NAIL_PRODUCT_ACTIVITY = 34;

	// 添加tag
	public final static int ADD_TAG = 35;
	// 移除tag
	public final static int DEL_TAG = 36;

	// 跳转到款式选择
	public final static int JUMP_TO_STYLE_SELECT= 37;

	// 关注话题
	public final static int ATTEN_TOPIC = 38;

	// 喜欢
	public final static int ADD_LIKE_NAIL = 39;

	// 不喜欢
	public final static int CANCEL_LIKE_NAIL = 40;

	// 选择的tag
	public final static int SELECTED_TAG = 41;

	// 选择的tag
	public final static int GET_MY_MASS_TOPIC = 42;
	// 获取化妆包 和秀美装
	public final static int GET_OTHER_USER_SHOW = 43;
	// 想要的产品
	public final static int WANT_TO_GET_PRODUCT = 44;
	// 显示软键盘
	public final static int SHOW_SOFTINPUT = 45;

	// 获得邀请人
	public final static int GET_INVITE_USER = 46;

	// 设置邀请人
	public final static int SET_INVITE_USER = 47;

	// 重新发送聊天
	public final static int SEND_CHAT_AGAIN = 48;
	// 重新发送聊天
	public final static int SEND_CHAT = 49;
	// 上传下一个
	public final static int NEXT_ITEM = 50;
	// 获取商城商品
	public final static int GET_COIN_MALL_PRODUCT = 51;
	// 获取商城商品
	public final static int COLLECT_WEAR_ALBUM = 52;
	// 展开圈子列表
	public final static int SHOW_MORE_MASS = 53;
	// 收起圈子列表
	public final static int DONT_SHOW_MORE_MASS = 54;
	// 上传试用报告
	public final static int REPORT_TRIAL = 56;
	
	public final static int CLICK_DATE = 57;
	
	// 获取period数据
	public final static int GET_PERIOD_INFO = 58;
	// 设置period数据
	public final static int SET_PERIOD_INFO = 59;
	// 完成
	public final static int FINISH = 60;
	
	public final static int NEED_REFRESH = 61;
	// 获取邮寄地址
	public final static int PAY_ADDRESS_GET = 62;
	// 创建订单
	public final static int PAY_ORDER_CREATE = 63;
	// 订单详情
	public final static int ORDER_DETAIL = 64;
	// 订单取消
	public final static int ORDER_CLOSE = 65;
	// 提醒发货
	public final static int DELIVERY_REMIND = 66;
	// 确认收货
	public final static int GOOD_CONFIRM = 67;
	// 客户端支付成功订单回调
	public final static int ORDER_PAY_SUCCESS_CALLBACK = 68;
}
