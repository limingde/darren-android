package com.dd.whateat.bean;

import java.io.Serializable;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dd.whateat.DdApplication;
import com.dd.whateat.constents.IntentExtra;
import com.dd.whateat.utils.DdLog;

/**
 * 积分
 * @author Administrator
 *
 */
public class GainScore implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7314430003518644218L;

	static final String TAG = "GainScore";
	
	public int gain_score_count;
	public String gain_score_msg;
	
	public static void parseGainScore(String strJSON){
		if(TextUtils.isEmpty(strJSON)){
			DdLog.e(TAG, "parseGainScore failed, strJSON is null");		
			return ;
		}
	
		try{
			ResultBaseBean result = JSON.parseObject(strJSON, ResultBaseBean.class);
			if (result != null && result.data != null) {
//				JSONObject jobj = new JSONObject(result.data);
				GainScore score = JSON.parseObject(result.data, GainScore.class);
				if(score != null && score.gain_score_count != 0 && DdApplication.CONTEXT != null){
					Intent intent = new Intent(IntentExtra.ACTION_GAINT_SCORE);
					intent.putExtra(IntentExtra.EXTRA_GAINT_SCORE, score);
					DdApplication.CONTEXT.sendBroadcast(intent);
				}
			}
		}catch (Exception e) {
		}
	}
}
