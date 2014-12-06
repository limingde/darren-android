package com.dd.datastatistics.dbutil;

import android.content.Context;

import com.dd.datastatistics.bean.BtnClickEventData;
import com.dd.datastatistics.biz.DataStatistics;
import com.dd.datastatistics.constant.DataStaMeilaConfig;
import com.dd.datastatistics.orm.DataStaDataManager;
import com.dd.datastatistics.orm.DataStaDatabaseBuilder;


/**
 * 
 * 
 */
public class DataStaDBUtil {
	private static DataStaDatabaseBuilder DATABASE_BUILDER;
	private static PDWBusinessDataManager INSTANCE;
	// 鑾峰彇鏁版嵁搴撳疄锟�
	static {
		if (DATABASE_BUILDER == null) {
			DATABASE_BUILDER = getDatabaseBuilder();
			// // 娣诲姞model锛岀敓鎴愭暟鎹簱锟�
			DATABASE_BUILDER.addClass(BtnClickEventData.class);
		}

	}	

	/**
	 * 鏁版嵁搴撴瀯寤虹被
	 * 
	 * @return 杩斿洖锟�锟斤拷鏁版嵁鏋勫缓锟�
	 */
	public static DataStaDatabaseBuilder getDatabaseBuilder() {
		if (DATABASE_BUILDER == null) {
			DATABASE_BUILDER = new DataStaDatabaseBuilder(DataStaMeilaConfig.DB_NAME);
		}
		return DATABASE_BUILDER;
	}
	
	/**
	 * 
	 * @return 鏁版嵁搴撶鐞嗗櫒锛岀敤浜庢搷浣滄搷浣滃簱
	 */
	public static DataStaDataManager getDataManager() {
		if (INSTANCE == null) {
			INSTANCE = new PDWBusinessDataManager(DataStatistics.CONTEXT,
					DATABASE_BUILDER);
		}
		return INSTANCE;
	}

	static class PDWBusinessDataManager extends DataStaDataManager {

		protected PDWBusinessDataManager(Context context,
				DataStaDatabaseBuilder databaseBuilder) {
			super(context, DataStaMeilaConfig.DB_NAME, DataStaMeilaConfig.DB_VERSION,
					databaseBuilder);
		}

	}
}
