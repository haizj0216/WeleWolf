package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

public class OnlineResetStudentPswInfo extends BaseObject{
	public String newPassword = "666666";

	@Override
	public void parse(JSONObject json) {
		// TODO Auto-generated method stub
		super.parse(json);
		JSONObject data = json.optJSONObject("data");
		if (data != null && data.has("password"))
		    newPassword = data.optString("password");
	}
}
