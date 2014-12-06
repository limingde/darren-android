package com.dd.datastatistics.orm;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;

import android.database.Cursor;

import com.dd.datastatistics.util.DataStaMeilaLog;


/**
 * ORM鐨勮緟鍔╂柟娉曠被
 * 
 */
public class Utils {
	public static final String TAG = "ORM";
	public static boolean UPPER_COL_NAME = false;

	public static final String ERR_DB_IS_NOT_OPEN = "鏁版嵁搴撳浜庡叧闂姸鎬侊紝璇风‘璁ゆ槸鍚﹀凡鎵撳紑鏁版嵁搴";
	public static final String ERR_INSERT_FAILED = "鎻掑叆鎿嶄綔澶辫触. Internal error.";
	public static final String ID = "_id";

	/**
	 * 灏唈ava灞炴�鍚嶇О杞寲涓簊ql鍒楃殑鍛藉悕锛屽锛歟ntityIsAName 涓�entity_IS_A_NAME銆�
	 * <p>
	 * 濡傛灉鏄皬鍐欙紝鍒欒浆鍖栦负澶у啓锛涜嫢鏄ぇ鍐欙紝鍒欏湪瀛楁瘝鍓嶅姞涓嬪垝绾库�_鈥濓紝濡傦細<br>
	 * "AbCd"->"AB_CD" <br>
	 * "ABCd"->"AB_CD"<br>
	 * "AbCD"->"AB_CD"<br>
	 * "ShowplaceDetailsVO"->"SHOWPLACE_DETAILS_VO"<br>
	 * </p>
	 * 
	 * @param java鍚嶇О
	 * @return 杞寲鍚庣殑Sql鍚嶇О
	 */
	public static String toSQLName(String javaNotation) {
		if (ID.equalsIgnoreCase(javaNotation)) {
			return ID;
		}
		
		if(!UPPER_COL_NAME){
			return javaNotation;
		}
		
		StringBuilder sb = new StringBuilder();
		char[] buf = javaNotation.toCharArray();

		for (int i = 0; i < buf.length; i++) {
			char prevChar = (i > 0) ? buf[i - 1] : 0;
			char c = buf[i];
			char nextChar = (i < buf.length - 1) ? buf[i + 1] : 0;
			boolean isFirstChar = (i == 0) ? true : false;

			if (isFirstChar || Character.isLowerCase(c)) {
				sb.append(Character.toUpperCase(c));
			} else if (Character.isUpperCase(c)) {
				if (Character.isLetterOrDigit(prevChar)) {
					if (Character.isLowerCase(prevChar)) {
						sb.append('_').append(Character.toUpperCase(c));
					} else if (nextChar > 0 && Character.isLowerCase(nextChar)) {
						sb.append('_').append(Character.toUpperCase(c));
					} else {
						sb.append(c);
					}
				} else {
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 灏唖ql鍒楀悕绉拌浆鍖栦负java鐨勫睘鎬ф垨鑰呮柟娉曞懡鍚嶏紝濡傦細entity_IS_A_NAME 涓�entityIsAName銆�
	 * <p>
	 * 濡傛灉瀛楁瘝鏈変笅鍒掔嚎鈥淿鈥濆垯杞负澶у啓锛涘惁鍒欒浆鍖栦负灏忓啓
	 * </p>
	 * 
	 * @param sqlNotation
	 *            Sql鍚嶇О
	 * @return 杞寲鍚庣殑java鍚嶇О
	 */
	public static String toJavaMethodName(String sqlNotation) {
		
		if(!UPPER_COL_NAME){
			return sqlNotation;
		}
		
		StringBuilder dest = new StringBuilder();
		char[] src = sqlNotation.toCharArray();

		for (int i = 0; i < src.length; i++) {
			char c = src[i];
			boolean isFirstChar = (i == 0) ? true : false;

			if (isFirstChar || c != '_') {
				dest.append(Character.toLowerCase(c));
			} else {
				i++;
				if (i < src.length) {
					dest.append(src[i]);
				}
			}
		}

		return dest.toString();
	}

	/**
	 * 灏哠ql鐨勮〃鍚嶈浆鍖栦负绫诲悕锛屽 entity_IS_A_NAME to entityIsAName;
	 * <p>
	 * 濡傛灉娌℃湁涓嬪垝绾匡紝鍒欒浆鍖栦负灏忓啓锛涘鏋滄湁涓嬪垝绾匡紝鍒欏拷鐣ヤ笅鍒掔嚎锛屽苟涓斿皢涓嬩竴涓瓧姣嶈浆鍖栦负澶у啓
	 * </p>
	 * 
	 * @param sqlNotation
	 *            Sql琛ㄥ悕
	 * @return 杞负鍚庣殑绫诲悕绉�
	 */
	public static String toJavaClassName(String sqlNotation) {
		if(!UPPER_COL_NAME){
			return sqlNotation;
		}
		
		StringBuilder sb = new StringBuilder();
		char[] buf = sqlNotation.toCharArray();
		for (int i = 0; i < buf.length; i++) {
			char c = buf[i];
			if (i == 0) {
				sb.append(buf[i]);
			} else if (c != '_') {
				sb.append(Character.toLowerCase(c));
			} else {
				i++;
				if (i < buf.length) {
					sb.append(buf[i]);
				}
			}
		}
		return sb.toString();
	}

	static <T extends DataStaBaseModel> String getTableName(Class<T> clazz) {
		return toSQLName(clazz.getSimpleName());
	}

	/**
	 * Inflate entity entity using the current row from the given cursor.
	 * 
	 * @param cursor
	 *            The cursor to get object data from.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	static <T extends DataStaBaseModel> T inflate(Cursor cursor, DataStaBaseModel entity) throws DataStaDataAccessException {
		try {
			entity = entity.getClass().newInstance();
		} catch (IllegalAccessException e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		} catch (InstantiationException e) {
			throw new DataStaDataAccessException(e.getLocalizedMessage());
		}

		for (Field field : entity.getColumnFields()) {
			String typeString = null;
			String colName = null;
			try {
				typeString = field.getType().getName();
				colName = toSQLName(field.getName());
				if (cursor.isNull(cursor.getColumnIndex(colName))) {
					continue;
				}
				if (typeString.equals("long") || typeString.equals("java.lang.Long")) {
					field.set(entity, cursor.getLong(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("java.lang.String")) {
					field.set(entity, cursor.getString(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("double") || typeString.equals("java.lang.Double")) {
					field.set(entity, cursor.getDouble(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("java.lang.Boolean") || typeString.equals("boolean")) {
					String fieldValue = cursor.getString(cursor.getColumnIndex(colName));
					field.set(entity, fieldValue != null && fieldValue.equals("true"));
				} else if (typeString.equals("[B")) {
					field.set(entity, cursor.getBlob(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("int") || typeString.equals("java.lang.Integer")) {
					field.set(entity, cursor.getInt(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("float") || typeString.equals("java.lang.Float")) {
					field.set(entity, cursor.getFloat(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("short") || typeString.equals("java.lang.Short")) {
					field.set(entity, cursor.getShort(cursor.getColumnIndex(colName)));
				} else if (typeString.equals("java.sql.timestamp")) {
					long l = cursor.getLong(cursor.getColumnIndex(colName));
					field.set(entity, new Timestamp(l));
				} else if (typeString.equals("java.util.Date")) {
					long l = cursor.getLong(cursor.getColumnIndex(colName));
					field.set(entity, new Date(l));
				} else {
					throw new DataStaDataAccessException("Class cannot be read from Sqlite3 database.");
				}
			} catch (IllegalArgumentException e) {
				throw new DataStaDataAccessException(e.getLocalizedMessage());
			} catch (IllegalAccessException e) {
				throw new DataStaDataAccessException(e.getLocalizedMessage());
			} catch (Exception e) {
				DataStaMeilaLog.e(TAG, e);
			}
		}

		// EntitiesMap.instance().set(entity);

		return (T) entity;
	}

	/**
	 * 鏍规嵁java瀛楁绫诲瀷锛岃浆鍖栦负sqlite鐨勫垪鐨勭被鍨�
	 * 
	 * @param c
	 *            瀛楁绫诲瀷瀹氫箟
	 * @return sqlite鍒楃殑绫诲瀷
	 */
	static String getSQLiteTypeString(Class<?> c) {
		String result = "text";
		String name = c.getName();

		if ("java.lang.String".equals(name) || "string".equals(name)) {
			result = "text";
		} else if ("short".equals(name) || "int".equals(name) || "java.lang.Integer".equals(name)
				|| "long".equals(name) || "java.lang.Long".equals(name) || "java.sql.Timestamp".equals(name)
				|| "java.util.Date".equals(name)) {
			result = "int";
		} else if ("double".equals(name) || "java.lang.Double".equals(name) || "float".equals(name)
				|| "java.lang.Float".equals(name)) {
			result = "real";
		} else if ("[B".equals(name)) {
			result = "blob";
		} else if ("java.lang.Boolean".equals(name) || "boolean".equals(name)) {
			result = "bool";
		} else {
			DataStaMeilaLog.e("getSQLiteTypeString", "绫讳笉鑳戒繚瀛樺埌Sqlite鏁版嵁搴擄紝璇锋煡鐪嬫槸鍚﹀瓨鍦ㄤ笉鏀寔鐨勭被鍨嬶紒");
//			throw new IllegalArgumentException("绫讳笉鑳戒繚瀛樺埌Sqlite鏁版嵁搴擄紝璇锋煡鐪嬫槸鍚﹀瓨鍦ㄤ笉鏀寔鐨勭被鍨嬶紒");
		}

		return result;
	}
}
