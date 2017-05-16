package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

public class OnlineQuestionTypes extends BaseObject {

	public String mQuestionTypes;

	public void parse(org.json.JSONObject json) {
		super.parse(json);
		if (isAvailable()) {
			mQuestionTypes = json.optString("questionType");
		}
	};
}
