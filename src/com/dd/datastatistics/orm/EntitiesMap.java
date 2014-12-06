package com.dd.datastatistics.orm;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *         <p>
 *         杩欎釜绫荤敤浜庣紦瀛樺疄浣擄紝鍑忓皯瀵规暟鎹簱鐨勮闂�
 *         </p>
 */
class EntitiesMap {
	private static EntitiesMap ENTITIES_MAP = new EntitiesMap();

	private Map<String, WeakReference<DataStaBaseModel>> mTypeModelMap = new HashMap<String, WeakReference<DataStaBaseModel>>();
	static EntitiesMap instance() {
		return ENTITIES_MAP;
	}

	@SuppressWarnings("unchecked")
	<T extends DataStaBaseModel> T get(Class<T> c, long id) {
		String key = makeKey(c, id);
		WeakReference<DataStaBaseModel> i = mTypeModelMap.get(key);
		if (i == null) {
			return null;
		}
		return (T) i.get();
	}

	void set(DataStaBaseModel e) {
		String key = makeKey(e.getClass(), e.getID());
		mTypeModelMap.put(key, new WeakReference<DataStaBaseModel>(e));
	}

	@SuppressWarnings("rawtypes")
	private String makeKey(Class entityType, long id) {
		StringBuilder sb = new StringBuilder();
		sb.append(entityType.getName()).append(id);
		return sb.toString();
	}
}
