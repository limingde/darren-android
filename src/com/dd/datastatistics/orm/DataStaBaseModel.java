package com.dd.datastatistics.orm;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 瀹炰綋绫诲熀绫伙紝闇�淇濆瓨鏁版嵁鍒皊qlite鐨勫疄浣擄紝閮介渶瑕佷粠璇ョ被缁ф壙
 * <p>
 * 鐩墠鏀寔涓嬮潰鐨勫嚑绉嶆暟鎹被鍨嬶細int, java.lang.Integer, long, java.lang.Long, java.lang.String<br>
 * </p>
 * 娉ㄦ剰锛�銆佸悇涓瓧娈电殑绫诲瀷鏈�ソ浣跨敤鍙┖绫诲瀷锛屽 java.lang.Integer 绛夛紱<br>
 * 2銆佷笉寤鸿浣跨敤Date绛夌被鍨嬶紝浣跨敤java.lang.Long鏇挎崲锛�br>
 * 3銆侀潤鎬佺殑鍜屽姞@NotStore娉ㄨВ鐨勪笉琚繚瀛樺埌鏁版嵁搴撲腑
 * 
 * 
 */
public class DataStaBaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1907916979605330513L;

	public long _id;

	/**
	 * This entities row id.
	 * 
	 * @return The SQLite row id.
	 */
	public long getID() {
		return _id;
	}

	/**
	 * This entities row id.
	 * 
	 * @param id
	 *            the specified entities row id
	 */
	public void setID(long id) {
		_id = id;
	}

	String getTableName() {
		return Utils.toSQLName(getClass().getSimpleName());
	}

	/**
	 * 鑾峰彇绫婚櫎id浠ュ鐨勫叕鏈夊瓧娈碉紝涓嶅寘鍚互 m_ 鎴�s_ 寮�ご鐨勫瓧娈�
	 * 
	 * @return 绫荤殑瀛楁鏁扮粍鍒楄〃.
	 */
	String[] getColumnsWithoutID() {
		List<String> columns = new ArrayList<String>();
		for (Field field : getColumnFieldsWithoutID()) {
			columns.add(field.getName());
		}

		return columns.toArray(new String[0]);
	}

	/**
	 * 鑾峰彇绫诲叕鏈夌殑瀛楁锛屼笉鍖呭惈浠�m_ 鎴�s_ 寮�ご鐨勫瓧娈碉紝濡傛灉瀛愮被鍜岀埗绫绘湁鐩稿悓鐨勫睘鎬у悕锛屼笉绠＄被鍨嬫槸鍚︿竴鏍凤紝鍙彇涓�釜锛屾槸鍝竴涓氨涓嶇‘瀹氫簡
	 * 
	 * @return An array of fields for this class.
	 */
	List<Field> getColumnFields() {
		Field[] fields = getClass().getFields();
		List<Field> columns = new ArrayList<Field>();
		List<String> columnNames = new ArrayList<String>();
		for (Field field : fields) {
			if (!field.isAnnotationPresent(NotStore.class) && !Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())
					&& columnNames.indexOf(field.getName()) == -1) {
				columns.add(field);
				columnNames.add(field.getName());
			}
		}

		return columns;
	}

	/**
	 * 鑾峰彇绫婚櫎id浠ュ鐨勫瓧娈碉紝涓嶅寘鍚互 m_ 鎴�s_ 寮�ご鐨勫瓧娈碉紝濡傛灉瀛愮被鍜岀埗绫绘湁鐩稿悓鐨勫睘鎬у悕锛屼笉绠＄被鍨嬫槸鍚︿竴鏍凤紝鍙彇涓�釜锛屾槸鍝竴涓氨涓嶇‘瀹氫簡
	 * 
	 * @return 绫荤殑瀛楁鍒楄〃.
	 */
	List<Field> getColumnFieldsWithoutID() {
		Field[] fields = getClass().getFields();
		List<Field> columns = new ArrayList<Field>();
		List<String> columnNames = new ArrayList<String>();
		for (Field field : fields) {
			if (!field.getName().startsWith("_id") && !field.isAnnotationPresent(NotStore.class) && !Modifier.isStatic(field.getModifiers())
					&& columnNames.indexOf(field.getName()) == -1) {
				columns.add(field);
				columnNames.add(field.getName());
			}
		}
		return columns;
	}
}
