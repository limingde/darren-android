package com.dd.datastatistics.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.dd.datastatistics.bean.JSONObjectBaseBean;

public class DataStaConvertJSON {
	public static JSONObjectBaseBean getBaseJSONObject(String strResult) {
		// Log.d("getBaseJSONObject", strResult);
		JSONObjectBaseBean base = new JSONObjectBaseBean();
		try {
			JSONObject jsonObj = new JSONObject(strResult);

			base.setMsg(jsonObj.optString("msg"));
			base.setRet(jsonObj.optInt("ret"));
			base.setData(jsonObj.optJSONObject("data"));

		} catch (JSONException e) {
			System.out.println("Json parse getBaseJSONObject error");
			e.printStackTrace();
		}

		return base;
	}

	public static int getInt(String strJOSN, String key) {
		int value = 0;
		JSONObjectBaseBean baseBean = getBaseJSONObject(strJOSN);
		if (baseBean.getRet() == 0) {

			value = baseBean.getData().optInt(key);

		}
		return value;
	}

	public static String getString(String strJOSN, String key) {
		String value = "";
		JSONObjectBaseBean baseBean = getBaseJSONObject(strJOSN);
		if (baseBean.getRet() == 0) {

			value = baseBean.getData().optString(key);

		}
		return value;
	}
	public static final int TOPIC_CONTENT_IMG_TYPE = 1;
	public static final int TOPIC_CONTENT_TEXT_TYPE = 2;

}
