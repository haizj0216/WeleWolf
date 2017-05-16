package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

public class OnlineGrantCoinInfo extends BaseObject {

	public int coinRemainCount;
	public String mStudentId;
	public int gainedCoinCount;
	public int totalCoin;
	public int gainedCoin;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		try {
			if (isAvailable()) {
				JSONObject obj = json.optJSONObject("data");
				if (null != obj) {
					gainedCoin = obj.optInt("gainedCoin");
					coinRemainCount = obj.optInt("coinRemainCount");
					mStudentId = obj.optString("studentID");
					gainedCoinCount = obj.optInt("gainedCoinCount");
					totalCoin = obj.optInt("totalCoin");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
