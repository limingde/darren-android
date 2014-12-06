package com.dd.whateat.bean;

public interface DataStaMeilaErrorCode {
	//通用结果
	public static final int ret_ok = 0;
	public static final int ret_login_timeout = 20105;
	public static final int ret_key_timeout = -1;
	
	//条码与二维码扫描结果
	public static final int ret_not_found = 200001;
	public static final int ret_not_verified = 200002;
	public static final int ret_not_cosmetic = 200003;
	public static final int ret_jump = 200004;
	
	public static final int ret_mass_name_repeat = 20632;
}
