/**
 * 
 */
package com.dd.datastatistics.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dd.datastatistics.util.DataStaMeilaLog;


/**
 * sqlite 鏁版嵁绠＄悊鍣�
 * 
 * 
 */
public abstract class DataStaDataManager {

	private static final String TAG = "DataManager";

	private static final String OBJECT_CAN_NOT_NULL = "鎻掑叆鐨勫璞′笉鑳戒负绌恒�";
	private static final String ID_CAN_NOT_NULL = "浣跨敤Update鏂规硶鏃讹紝_id蹇呴』鏈夊�";
	private static final String WHERE_CLAUSE_CAN_NOT_NULL = "鏇存柊鏉′欢涓嶈兘涓虹┖锛屽惁鍒欎細鏇存柊鏁翠釜鏁版嵁搴撱�";
	private static final String ID_WHERE_CLAUSE = "_id = ?";
	private static final String COLUMN_NOT_EXISITS = "鎸囧畾鐨勫垪涓嶅瓨鍦";
	private static final String SET_DATABASE_FIRST = "Set database first";

	private DataStaDatabase mDatabase;
	private Context mContext;
	private String mDBName;
	private int mDBVersion;
	private DataStaDatabaseBuilder mDatabaseBuilder;

	/**
	 * 鍒濆鍖栨暟鎹簱
	 * 
	 * @param context
	 *            褰撳墠涓婁笅鏂�
	 * @param dbName
	 *            鏁版嵁搴撳悕绉�
	 * @param dbVersion
	 *            鏁版嵁搴撶増鏈�
	 * @param databaseBuilder
	 *            鏁版嵁搴撹〃鐨勬弿杩扮被
	 */
	protected DataStaDataManager(Context context, String dbName, int dbVersion, DataStaDatabaseBuilder databaseBuilder) {
		this.mContext = context;
		this.mDBName = dbName;
		this.mDBVersion = dbVersion;
		this.mDatabaseBuilder = databaseBuilder;

		setDatabase();
	}

	protected void setDatabase() {
		if (mDatabase == null) {
			mDatabase = new DataStaDatabase(mContext, mDBName, mDBVersion, mDatabaseBuilder);
		}
	}

	/**
	 * 寮�惎浜嬪姟銆傝鏂规硶浼氳皟鐢╫pen鏂规硶锛岃嚜鍔ㄦ墦寮�暟鎹簱杩炴帴
	 */
	public void beginTransaction() {
		// open();
		mDatabase.beginTransaction();
	}

	/**
	 * 缁撴潫浜嬪姟銆傝鏂规硶浼氳皟鐢╟lose鏂规硶锛岃嚜鍔ㄥ叧闂暟鎹簱杩炴帴
	 */
	public void endTransaction() {
		mDatabase.endTransaction();
		// close();
	}

	/**
	 * 鍥炴粴浜嬪姟
	 */
	public void rollBack() {
		mDatabase.rollTransaction();
		// close();
	}

	public void open() {
		// if(!mDatabase.isOpen()){
		// mDatabase.open();
		// }
	}

	public void close() {
		// mDatabase.close();
	}

	public void firstOpen() {
		mDatabase.open();
	}

	public void lastClose() {
		mDatabase.close();
	}
	

	/**
	 * 鏍规嵁鎸囧畾鐨勬潯浠讹紝鑾峰彇璇ヨ〃鐨勬暟鎹潯鏁�
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫�
	 * @param type
	 * @return 杩斿洖璇ヨ〃鐨勬暟鎹潯鏁�
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> int getCount(Class<T> type)
			throws DataStaDataAccessException {
		if (mDatabase == null) {
			throw new DataStaDataAccessException(SET_DATABASE_FIRST);
		}

		String sql = "Select * from " + Utils.getTableName(type);

		Cursor c = mDatabase.rawQuery(sql, null);
		int count = 0;
		try {
			if( c != null){
//				  c.moveToFirst();
				  count =  c.getCount();
			}
			
		} catch (Exception e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		} finally {
			c.close();
		}

		return count;		
	}

	/**
	 * 鏍规嵁鎸囧畾鐨勬潯浠讹紝鑾峰彇鍗曚釜瀹炰綋銆傚鏋滃瓨鍦ㄥ鏉＄鍚堟潯浠剁殑璁板綍锛屽垯鍙繑鍥炵涓�潯璁板綍
	 * 
	 * @param <T>
	 *            瀹炰綋绫诲瀷
	 * @param type
	 *            鎸囧畾鐨勫疄浣撶被鍨�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鍙傛暟锛屼粬浼氭寜鐓ч『搴忔浛鎹�whereClause 涓棿鐨勶紵鍙�
	 * @return 杩斿洖鍗曚釜瀹炰綋銆傚鏋滃瓨鍦ㄥ鏉＄鍚堟潯浠剁殑璁板綍锛屽垯鍙繑鍥炵涓�潯璁板綍
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> T get(Class<T> type, String whereClause, String[] whereArgs)
			throws DataStaDataAccessException {
		if (mDatabase == null) {
			throw new DataStaDataAccessException(SET_DATABASE_FIRST);
		}
		T entity = null;

		Cursor c = null;
		try {
			c = mDatabase.query(false, Utils.getTableName(type), null, whereClause, whereArgs, null, null, null, "1");
			if (c.moveToNext()) {
				entity = type.newInstance();
				entity = Utils.inflate(c, entity);
			}
		} catch (IllegalAccessException e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		} catch (InstantiationException e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}

		return entity;
	}

	/**
	 * 鏍规嵁鎸囧畾鐨勬潯浠讹紝鑾峰彇瀹炰綋鐨勫垪琛�
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫�
	 * @param type
	 *            鎸囧畾鐨勫疄浣撶被鍨�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鍙傛暟锛屼粬浼氭寜鐓ч『搴忔浛鎹�whereClause 涓棿鐨勶紵鍙�
	 * @return 杩斿洖瀹炰綋鍒楄〃
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> List<T> getList(Class<T> type, String whereClause, String[] whereArgs)
			throws DataStaDataAccessException {
		return getList(type, false, whereClause, whereArgs, null, null, null, null);
	}

	/**
	 * 鏍规嵁鎸囧畾鐨勬潯浠讹紝鑾峰彇瀹炰綋鐨勫垪琛�
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫�
	 * @param type
	 *            鎸囧畾鐨勫疄浣撶被鍨�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鍙傛暟锛屼粬浼氭寜鐓ч『搴忔浛鎹�whereClause 涓棿鐨勶紵鍙�
	 * @param pageSize
	 *            姣忛〉璁板綍鏉℃暟锛屽彇姝ｅ�
	 * @param pageIndex
	 *            鍙栫鍑犻〉鐨勮褰曪紝鍙栨鍊硷紝浠�寮�
	 * @return 杩斿洖瀹炰綋鍒楄〃
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> List<T> getList(Class<T> type, String whereClause, String[] whereArgs, int pageSize,
			int pageIndex) throws DataStaDataAccessException {
		return getList(type, false, whereClause, whereArgs, null, null, null, pageSize, pageIndex);
	}

	/**
	 * 鏍规嵁鎸囧畾鐨勬潯浠讹紝鑾峰彇瀹炰綋鐨勫垪琛�
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫�
	 * @param type
	 *            鎸囧畾鐨勫疄浣撶被鍨�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鍙傛暟锛屼粬浼氭寜鐓ч『搴忔浛鎹�whereClause 涓棿鐨勶紵鍙�
	 * @param orderBy
	 *            鎺掑簭瀛楁
	 * @param limit
	 *            鍙栨暟鎹殑鏉℃暟
	 * @return 杩斿洖瀹炰綋鍒楄〃
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> List<T> getList(Class<T> type, String whereClause, String[] whereArgs, String orderBy,
			String limit) throws DataStaDataAccessException {
		return getList(type, false, whereClause, whereArgs, null, null, orderBy, limit);
	}

	/**
	 * 鏍规嵁鎸囧畾鐨勬潯浠讹紝鑾峰彇瀹炰綋鐨勫垪琛�
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫�
	 * @param type
	 *            鎸囧畾鐨勫疄浣撶被鍨�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鍙傛暟锛屼粬浼氭寜鐓ч『搴忔浛鎹�whereClause 涓棿鐨勶紵鍙�
	 * @param orderBy
	 *            鎺掑簭瀛楁
	 * @param pageSize
	 *            姣忛〉璁板綍鏉℃暟锛屽彇姝ｅ�
	 * @param pageIndex
	 *            鍙栫鍑犻〉鐨勮褰曪紝鍙栨鍊硷紝浠�寮�
	 * @return 杩斿洖瀹炰綋鍒楄〃
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> List<T> getList(Class<T> type, String whereClause, String[] whereArgs, String orderBy,
			int pageSize, int pageIndex) throws DataStaDataAccessException {
		return getList(type, false, whereClause, whereArgs, null, null, orderBy, pageSize, pageIndex);
	}

	/**
	 * 鏍规嵁鎸囧畾鐨勬潯浠讹紝鑾峰彇瀹炰綋鐨勫垪琛�
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫�
	 * @param type
	 *            鎸囧畾鐨勫疄浣撶被鍨�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鍙傛暟锛屼粬浼氭寜鐓ч『搴忔浛鎹�whereClause 涓棿鐨勶紵鍙�
	 * @param groupBy
	 *            鍒嗙粍鏉′欢
	 * @param having
	 *            鍒嗙粍鏉′欢
	 * @param orderBy
	 *            鎺掑簭瀛楁
	 * @param limit
	 *            鍙栨暟鎹殑鏉℃暟
	 * @return 杩斿洖瀹炰綋鍒楄〃
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> List<T> getList(Class<T> type, boolean distinct, String whereClause,
			String[] whereArgs, String groupBy, String having, String orderBy, String limit) throws DataStaDataAccessException {
		if (mDatabase == null) {
			throw new DataStaDataAccessException(SET_DATABASE_FIRST);
		}

		List<T> resultList = new ArrayList<T>();

		Cursor c = mDatabase.query(distinct, Utils.getTableName(type), null, whereClause, whereArgs, groupBy, having,
				orderBy, limit);
		try {
			while (c.moveToNext()) {
				// T entity = EntitiesMap.instance().get(type,
				// c.getLong(c.getColumnIndex("_id")));
				// if (entity == null) {
				T entity = type.newInstance();
				entity = Utils.inflate(c, entity);
				// }
				resultList.add(entity);
			}
		} catch (IllegalAccessException e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		} catch (InstantiationException e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		} finally {
			c.close();

		}

		return resultList;
	}

	/**
	 * 鏍规嵁鎸囧畾鐨勬潯浠讹紝鑾峰彇瀹炰綋鐨勫垪琛�
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫�
	 * @param type
	 *            鎸囧畾鐨勫疄浣撶被鍨�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鍙傛暟锛屼粬浼氭寜鐓ч『搴忔浛鎹�whereClause 涓棿鐨勶紵鍙�
	 * @param groupBy
	 *            鍒嗙粍鏉′欢
	 * @param having
	 *            鍒嗙粍鏉′欢
	 * @param orderBy
	 *            鎺掑簭瀛楁
	 * @param pageSize
	 *            姣忛〉璁板綍鏉℃暟锛屽彇姝ｅ�
	 * @param pageIndex
	 *            鍙栫鍑犻〉鐨勮褰曪紝鍙栨鍊硷紝浠�寮�
	 * @return 杩斿洖瀹炰綋鍒楄〃
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> List<T> getList(Class<T> type, boolean distinct, String whereClause,
			String[] whereArgs, String groupBy, String having, String orderBy, int pageSize, int pageIndex)
			throws DataStaDataAccessException {
		if (mDatabase == null) {
			throw new DataStaDataAccessException(SET_DATABASE_FIRST);
		}

		List<T> resultList = new ArrayList<T>();

		if (pageSize <= 0 || pageIndex <= 0) {
			return resultList;
		}
		String limit = Integer.valueOf(pageSize * pageIndex).toString();

		Cursor c = mDatabase.query(distinct, Utils.getTableName(type), null, whereClause, whereArgs, groupBy, having,
				orderBy, limit);
		try {

			// skip pages before pageIdex
			c.moveToPosition(pageSize * (pageIndex - 1) - 1);

			// begin to get data
			while (c.moveToNext()) {
				// T entity = EntitiesMap.instance().get(type,
				// c.getLong(c.getColumnIndex("_id")));
				// if (entity == null) {
				T entity = type.newInstance();
				entity = Utils.inflate(c, entity);
				// }
				resultList.add(entity);
			}
		} catch (IllegalAccessException e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		} catch (InstantiationException e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		} finally {
			c.close();

		}

		return resultList;
	}

	/**
	 * 
	 * @param <T>
	 * @param <T2>
	 * @param distinct
	 *            鏄惁鍘绘帀缁撴灉涓殑閲嶅椤�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鍙傛暟锛屼粬浼氭寜鐓ч『搴忔浛鎹�whereClause 涓棿鐨勶紵鍙�
	 * @param groupBy
	 *            鍒嗙粍瀛楁
	 * @param having
	 *            鍒嗙粍杩囨护鏉′欢
	 * @param orderBy
	 *            鎺掑簭瀛楁
	 * @param limit
	 *            鍙栨暟鎹殑鏉℃暟
	 * @param type1
	 *            瑕佸叧鑱旂殑绗竴涓〃
	 * @param type2
	 *            瑕佸叧鑱旂殑绗簩涓〃
	 * @return T绫诲瀷鐨勫垪琛�
	 * @throws DataStaDataAccessException
	 */
	public <T extends DataStaBaseModel, T2 extends DataStaBaseModel> List<T> getList(boolean distinct, String whereClause,
			String[] whereArgs, String groupBy, String having, String orderBy, String limit, Class<T> type1,
			Class<T2> type2) throws DataStaDataAccessException {
		if (mDatabase == null) {
			throw new DataStaDataAccessException(SET_DATABASE_FIRST);
		}

		List<T> resultList = new ArrayList<T>();
		Class<T> returnType = type1;
		DataStaBaseModel returnModel;

		try {
			returnModel = returnType.newInstance();

			// for column_name
			List<Field> columns = returnModel.getColumnFields();
			StringBuffer returnCols = new StringBuffer();
			String returnTableName = Utils.getTableName(returnType);
			for (int i = 0; i < columns.size(); ++i) {
				Field column = columns.get(i);
				String colName = Utils.toSQLName(column.getName());
				if (i != columns.size() - 1) {
					returnCols.append(returnTableName).append(".").append(colName + ", ");
				} else {
					returnCols.append(returnTableName).append(".").append(colName);
				}
			}
			StringBuffer sql = new StringBuffer("select");
			if (distinct) {
				sql.append(" distinct");
			}
			sql.append(" ").append(returnCols);
			sql.append(" from ");

			// for table_name
			sql.append(Utils.getTableName(type1)).append(", ");
			sql.append(Utils.getTableName(type2));

			// for where clause
			if (whereClause != null) {
				StringBuilder whereStr = new StringBuilder(whereClause);
				if (whereArgs != null) {
					for (int i = 0; i < whereArgs.length; ++i) {
						whereStr.replace(0, whereStr.length(), whereArgs[i]);
					}
				}
				sql.append(" where ").append(whereStr);
			}

			if (groupBy != null) {
				sql.append(" group by ").append(groupBy);
			}
			if (having != null) {
				sql.append(" having ").append(having);
			}
			if (orderBy != null) {
				sql.append(" order by ").append(orderBy);
			}
			if (limit != null) {
				sql.append(" limit ").append(limit);
			}
			// MeilaLog.d("debug", "getList, " + sql.toString());

			// execute
			Cursor c = mDatabase.rawQuery(sql.toString());
			// Cursor c = mDatabase.query(distinct,
			// Utils.getTableName(returnModel.getClass()), null, whereClause,
			// whereArgs, groupBy, having,
			// orderBy, limit);
			try {
				while (c.moveToNext()) {
					// T entity = EntitiesMap.instance().get(type,
					// c.getLong(c.getColumnIndex("_id")));
					// if (entity == null) {
					T entity = returnType.newInstance();
					entity = Utils.inflate(c, entity);
					// }
					resultList.add(entity);
				}
			} catch (IllegalAccessException e) {
				throw new DataStaDataAccessException(e.getLocalizedMessage());
			} catch (InstantiationException e) {
				throw new DataStaDataAccessException(e.getLocalizedMessage());
			} finally {
				c.close();

			}

		} catch (IllegalAccessException e1) {
			throw new DataStaDataAccessException(e1.getLocalizedMessage());
		} catch (InstantiationException e1) {
			throw new DataStaDataAccessException(e1.getLocalizedMessage());
		}

		return resultList;
	}

	/**
	 * 鎻掑叆璁板綍
	 * 
	 * @param model
	 *            瀹炰綋绫荤殑瀹炰緥
	 * @return 杩斿洖鎻掑叆璁板綍鐨刬d
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	@SuppressWarnings("unused")
	public void insert(DataStaBaseModel model) throws DataStaDataAccessException {
		if (model == null) {
			throw new DataStaDataAccessException(OBJECT_CAN_NOT_NULL);
		}

		StringBuilder sql = new StringBuilder();
		sql.append("insert into ").append(model.getTableName()).append(" (");		
		List<Object> argList = new ArrayList<Object>();
		List<Field> columns = model.getID() > 0 ? model.getColumnFields() : model.getColumnFieldsWithoutID();
		ContentValues values = new ContentValues();
//		for (Field column : columns) {
		for(int i=0; i<columns.size(); ++i){
			Field column = columns.get(i);
			
			try {
				// 瀛楁涓嶄负绌烘椂鎵嶆彃鍏ュ�
				Object fieldValue = column.get(model);
				if (fieldValue != null && column.getType().getSuperclass() != DataStaBaseModel.class) {
//					values.put(Utils.toSQLName(column.getName()), String.valueOf(fieldValue));
					argList.add(fieldValue);
					sql.append(Utils.toSQLName(column.getName()));
					if(i == columns.size()-1){
						sql.append(")");
					}else{
						sql.append(", ");
					}
				}
			} catch (IllegalAccessException e) {
				throw new DataStaDataAccessException(e.getLocalizedMessage());
			}
		}

		sql.append(" values(");
		for(int i=0; i<columns.size(); ++i){
			sql.append("?");
			if(i == columns.size()-1){
				sql.append(");");
			}else{
				sql.append(", ");
			}
		}
		
		long id;
		try {
//			id = mDatabase.insert(model.getTableName(), values);
			mDatabase.insert(sql.toString(), argList.toArray());
		} finally {

		}
//		return id;
	}

	/**
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫汇� 瀹炰緥model鐨刜id蹇呴』璧嬪�锛屽惁鍒欎笉鑳芥洿鏂�
	 * @param model
	 *            瀹炰綋鐨勫疄渚嬨� 瀹炰緥model鐨刜id蹇呴』璧嬪�锛屽惁鍒欎笉鑳芥洿鏂�
	 * @return 鍙楀奖鍝嶇殑璁板綍鏁�
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> int update(DataStaBaseModel model) throws DataStaDataAccessException {
		if (model == null) {
			throw new DataStaDataAccessException(OBJECT_CAN_NOT_NULL);
		}

		if (model.getID() <= 0) {
			throw new DataStaDataAccessException(ID_CAN_NOT_NULL);
		}

		List<Field> columns = model.getColumnFieldsWithoutID();
		ContentValues values = new ContentValues(columns.size());
		for (Field column : columns) {
			try {
				Object fieldValue = column.get(model);
				if (null != fieldValue) {
					values.put(Utils.toSQLName(column.getName()), String.valueOf(fieldValue));
				}
			} catch (IllegalArgumentException e) {
				throw new DataStaDataAccessException(COLUMN_NOT_EXISITS + column.getName());
			} catch (IllegalAccessException e) {
				throw new DataStaDataAccessException(COLUMN_NOT_EXISITS + column.getName());
			}
		}

		int r = updateByClause(model.getClass(), values, ID_WHERE_CLAUSE, new String[] { String.valueOf(model.getID())
		});

		return r;
	}

	/**
	 * 
	 * @param <T>
	 *            娉涘瀷瀹炰綋绫伙紝蹇呴』鏄疊aseModel鐨勫瓙绫汇� 瀹炰緥model鐨刜id鍙互娌℃湁璧嬪�
	 * @param model
	 *            瀹炰綋鐨勫疄渚�
	 * @return 鍙楀奖鍝嶇殑璁板綍鏁�
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撹闂紓甯�
	 */
	public <T extends DataStaBaseModel> int updateByClause(Class<T> type, DataStaBaseModel model, String whereClause,
			String[] whereArgs) throws DataStaDataAccessException {
		if (model == null) {
			throw new DataStaDataAccessException(OBJECT_CAN_NOT_NULL);
		}

		if (whereClause == null) {
			throw new DataStaDataAccessException(WHERE_CLAUSE_CAN_NOT_NULL);
		}

		List<Field> columns = model.getColumnFieldsWithoutID();
		ContentValues values = new ContentValues(columns.size());
		for (Field column : columns) {
			try {
				Object fieldValue = column.get(model);
				if (null != fieldValue) {
					values.put(Utils.toSQLName(column.getName()), String.valueOf(fieldValue));
				}
			} catch (IllegalArgumentException e) {
				throw new DataStaDataAccessException(COLUMN_NOT_EXISITS + column.getName());
			} catch (IllegalAccessException e) {
				throw new DataStaDataAccessException(COLUMN_NOT_EXISITS + column.getName());
			}
		}

		int r = updateByClause(model.getClass(), values, whereClause, whereArgs);
		return r;
	}

	/**
	 * 鏍规嵁鏉′欢淇敼澶氭潯鏁版嵁
	 * 
	 * @param <T>
	 *            娉涘瀷绫�
	 * @param type
	 *            闇�淇敼鐨勮〃鐨勫疄浣撶被
	 * @param values
	 *            闇�淇敼鐨勫唴瀹�
	 * @param whereClause
	 *            鏌ヨ鏉′欢
	 * @param whereArgs
	 *            鏌ヨ鏉′欢瀵瑰簲鐨勫�
	 * @return 杩斿洖鍙楀奖鍝嶇殑琛屾暟
	 */
	public <T extends DataStaBaseModel> int updateByClause(Class<T> type, ContentValues values, String whereClause,
			String[] whereArgs) {
		int rowAffect = 0;
		try {
			String table = Utils.getTableName(type);
			rowAffect = mDatabase.update(table, values, whereClause, whereArgs);
		} catch (Exception e) {
			DataStaMeilaLog.e(TAG, e);
		}
		return rowAffect;
	}

	/**
	 * 瀹炰綋淇濆瓨鏂规硶锛屽鏋滃凡缁忓瓨鍦ㄥ垯鏇存柊锛屽惁鍒欐彃鍏ユ柊绾綍
	 * 
	 * @param model
	 *            淇濆瓨鐨勫疄渚�
	 * @return 淇濆瓨瀹炰緥鐨勪富閿�
	 * @throws DataStaDataAccessException
	 *             鏁版嵁搴撳紓甯�
	 */
	public void save(DataStaBaseModel model) throws DataStaDataAccessException {
		if (model == null) {
			throw new DataStaDataAccessException(OBJECT_CAN_NOT_NULL);
		}

		long id = model.getID();
		if (id <= 0) {
//			id = insert(model);
			insert(model);
		} else {
			DataStaBaseModel existModel = get(model.getClass(), ID_WHERE_CLAUSE, new String[] { model.getID() + ""
			});
			if (existModel == null) {
//				id = insert(model);
				insert(model);
			} else {
				update(model);
			}
		}

//		return id;
	}

	/**
	 * 鍒犻櫎鎸囧畾涓婚敭鐨勫疄渚�
	 * 
	 * @param <T>
	 *            娉涘瀷绫�
	 * @param type
	 *            瑕佸垹闄ょ殑璁板綍绫诲瀷
	 * @param id
	 *            瑕佸垹闄よ褰曠殑涓婚敭
	 * @return 鍒犻櫎鎴愬姛鍚庯紝杩斿洖true锛涘惁鍒欒繑鍥瀎alse
	 */
	public <T extends DataStaBaseModel> boolean delete(Class<T> type, long id) {
		boolean toRet = delete(type, ID_WHERE_CLAUSE, new String[] { String.valueOf(id)
		});

		return toRet;
	}

	/**
	 * 鍒犻櫎鎸囧畾涓婚敭鐨勫疄渚�
	 * 
	 * @param <T>
	 *            娉涘瀷绫�
	 * @param type
	 *            瑕佸垹闄ょ殑璁板綍绫诲瀷
	 * @param whereClause
	 *            where鏉′欢
	 * @param whereArgs
	 *            where鍙傛暟
	 * @return 鍒犻櫎鎴愬姛鍚庯紝杩斿洖true锛涘惁鍒欒繑鍥瀎alse
	 */
	public <T extends DataStaBaseModel> boolean delete(Class<T> type, String whereClause, String[] whereArgs) {
		boolean result = true;
		try {
			mDatabase.delete(Utils.getTableName(type), whereClause, whereArgs);
		} catch (Exception e) {
			result = false;
			DataStaMeilaLog.e(TAG, e);
		}
		return result;
	}

	/**
	 * select 鏌ヨ鏂规硶锛屽彲浠ヨ繘琛岃法琛ㄦ煡璇�
	 * 
	 * @param sql
	 *            鏌ヨ鐨勮鍙�
	 * @return 绗﹀悎鏉′欢鐨凜ursor
	 */
	public Cursor rawQuery(String sql) {
		Cursor cursor = rawQuery(sql, null);

		return cursor;
	}

	/**
	 * select 鏌ヨ鏂规硶锛屽彲浠ヨ繘琛岃法琛ㄦ煡璇�
	 * 
	 * @param sql
	 *            鏌ヨ鐨勮鍙�
	 * @param selectionArgs
	 *            sql鏌ヨ璇彞涓弬鏁扮殑鍊�
	 * @return 绗﹀悎鏉′欢鐨凜ursor
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {

		Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);

		return cursor;
	}

	/**
	 * Execute a single SQL statement that is not a query. For example, CREATE
	 * TABLE, DELETE, INSERT, etc. Multiple statements separated by ;s are not
	 * supported. it takes a write lock
	 */
	public void execSQL(String sql) {
		mDatabase.execSQL(sql);
	}
}
