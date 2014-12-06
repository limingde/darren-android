package com.dd.whateat.utils;

import android.content.Context;

import com.dd.whateat.DdApplication;
import com.dd.whateat.comfig.DdConfig;
import com.dd.whateat.db.DataManager;
import com.dd.whateat.db.DatabaseBuilder;

/**
 * 
 * 
 */
public class DBUtil {
	private static DatabaseBuilder DATABASE_BUILDER;
	private static PDWBusinessDataManager INSTANCE;
	// 获取数据库实例
	static {
		if (DATABASE_BUILDER == null) {
			DATABASE_BUILDER = getDatabaseBuilder();
			// // 添加model，生成数据库表
		}

	}

	/**
	 * 
	 * @return 数据库管理器，用于操作操作库
	 */
	public static DataManager getDataManager() {
		if (INSTANCE == null) {
			INSTANCE = new PDWBusinessDataManager(DdApplication.CONTEXT,
					DATABASE_BUILDER);
		}
		return INSTANCE;
	}

	/**
	 * 数据库构建类
	 * 
	 * @return 返回一个数据构建器
	 */
	public static DatabaseBuilder getDatabaseBuilder() {
		if (DATABASE_BUILDER == null) {
			DATABASE_BUILDER = new DatabaseBuilder(DdConfig.DB_NAME);
		}
		return DATABASE_BUILDER;
	}

	static class PDWBusinessDataManager extends DataManager {

		protected PDWBusinessDataManager(Context context,
				DatabaseBuilder databaseBuilder) {
			super(context, DdConfig.DB_NAME, DdConfig.DB_VERSION,
					databaseBuilder);
		}

	}
}
