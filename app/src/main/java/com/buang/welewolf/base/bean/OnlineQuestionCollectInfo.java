package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

public class OnlineQuestionCollectInfo extends BaseObject {

	public String mIsCollect;

	@Override
	public void parse(JSONObject json) {
		// TODO Auto-generated method stub
		super.parse(json);
		if (json.has("isCollect")) {
			mIsCollect = json.optString("isCollect");
		}
	}
}
