/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.database.bean.ClassInfoItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 奖励班级信息
 * 
 * @author LiuYu
 */
public class OnlineClassPropInfo extends BaseObject {

	public List<ClassInfoItem> mClassItems;

	@Override
	public void parse(JSONObject data) {
		super.parse(data);
		if (isAvailable()) {
			JSONArray classArray = data.optJSONArray("data");
			mClassItems = new ArrayList<ClassInfoItem>();
			for (int i = 0; i < classArray.length(); i++) {
				ClassInfoItem info = new ClassInfoItem();
				JSONObject classItemJson = classArray.optJSONObject(i);
				String classID = classItemJson.optString("classID");
				String isProp = classItemJson.optString("isProp");
				info.classId = classID;
				info.isProp = isProp;
				mClassItems.add(info);
			}
		}
	}
}
