package com.dd.whateat.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.dd.whateat.bean.JSONArrayBaseBean;
import com.dd.whateat.bean.JSONObjectBaseBean;
import com.dd.whateat.bean.KeyWords;

public class ConvertJSON {
	private final static String TAG = "ConvertJSON";

	// 解析热门关键词的Json信息
	public static List<KeyWords> getKeyWordsList(String strJOSN) {
		List<KeyWords> keywords = new ArrayList<KeyWords>();
		if(TextUtils.isEmpty(strJOSN)){
			return keywords;
		}
		JSONArrayBaseBean baseBean = getBaseJSONArray(strJOSN);
		if (baseBean != null && baseBean.getRet() == 0) {
			JSONArray jsonObjs = baseBean.getData();
			for (int i = 0; jsonObjs != null && i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));

				KeyWords keyword = new KeyWords();
				keyword.setName(jsonObj.optString("name"));
				keyword.setWeight(jsonObj.optInt("weight"));
				Log.i("keyword信息", keyword.toString());
				keywords.add(keyword);
			}
		}
		Collections.sort(keywords);
		return keywords;
	}

	// 普通Json数据解析
	public static JSONArrayBaseBean getBaseJSONArray(String strResult) {
		JSONArrayBaseBean base = new JSONArrayBaseBean();
		try {
			JSONObject jsonObj = new JSONObject(strResult);

			base.setMsg(jsonObj.optString("msg"));
			base.setRet(jsonObj.optInt("ret"));
			base.setData(jsonObj.optJSONArray("data"));

		} catch (JSONException e) {
			System.out.println("Json parse getBaseJSONArray error");
			e.printStackTrace();
		}

		return base;
	}

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

	public static boolean getServerJsonRet(String strJSON) {
		JSONObjectBaseBean baseBean = getBaseJSONObject(strJSON);
		boolean isSuccess;

		if (baseBean.getRet() == 0) {
			isSuccess = true;
		} else {
			isSuccess = false;
		}
		return isSuccess;
	}

	private static String[] getJSONArrayOptString(JSONArray jsonArray) {
		String[] stringArray = null;
		if (jsonArray != null) {
			stringArray = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				stringArray[i] = jsonArray.optString(i);
			}
		}
		return stringArray;
	}

	private static List<String> getRealshotList(JSONArray jsonRealshot) {
		if (jsonRealshot != null && jsonRealshot.length() > 0) {
			int size = jsonRealshot.length();
			List<String> realshotList = new ArrayList<String>(size);
			for (int i = 0; i < size; i++) {
				String url = jsonRealshot.optString(i);

				if (url != null && !TextUtils.isEmpty(url)) {
					realshotList.add(url);
				}
			}

			if (realshotList.size() > 0) {
				return realshotList;
			}
		}
		return null;
	}
}
