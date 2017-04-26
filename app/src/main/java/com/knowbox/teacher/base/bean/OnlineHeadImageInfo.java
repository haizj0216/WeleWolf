package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * @name 图片基本信息实体
 * @author Fanjb
 * @date 2015-3-17
 */
public class OnlineHeadImageInfo extends BaseObject {

	public String url;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		if (isAvailable()) {
			url = json.optString("headPhoto");
		}
	}
}
