package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 15/9/8.
 */
public class OnlineInviteCodeInfo extends BaseObject {

	public static final String INVITE_CODE_TYPE_SUCC = "succ";

	public String resultType;
	public String msg;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		if (isAvailable()) {
			JSONObject object = json.optJSONObject("data");
			if (null != object) {
				resultType = object.optString("result_type");
				msg = object.optString("msg");
			}
		}
	}
}
