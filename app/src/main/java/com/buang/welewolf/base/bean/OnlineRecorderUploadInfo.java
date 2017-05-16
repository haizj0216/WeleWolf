package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

public class OnlineRecorderUploadInfo extends BaseObject {

	public String mToken;
	public long mExpiredTime;
	public String mDomain;

	@Override
	public void parse(JSONObject json) {
		// TODO Auto-generated method stub
		super.parse(json);
		JSONObject data = json.optJSONObject("data");
		mToken = data.optString("token");
		mExpiredTime = data.optLong("expiredTimeStamp");
		mDomain = data.optString("domainName");
	}
}
