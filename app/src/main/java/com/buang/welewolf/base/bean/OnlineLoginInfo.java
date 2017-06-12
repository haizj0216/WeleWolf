/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.utils.ConstantsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线登录信息
 * @author yangzc
 */
public class OnlineLoginInfo extends BaseObject {
	
	public UserItem mUserItem;
	public String mMessage;
	//{"code":99999,"message":"success","requestId":"58a7eeb68cfae242","data":[]}
	
	@Override
	public void parse(JSONObject data) {
		super.parse(data);
		if (isAvailable()) {
			JSONObject json = data.optJSONObject("data");
			if (json == null) {
				return;
			}
			mUserItem = new UserItem();
			mUserItem.parseData(json);
		}else{
			String result = data.optString("code");
			if (result.equals("failure")) {
				mMessage = data.optString("message");
			}else if (result.equals("20014")) {
				//mMessage = "邀请码错误";
				JSONObject errorJson = data.optJSONObject("data");
				mMessage = errorJson.optString("msg");
			}
		}
	}
}
