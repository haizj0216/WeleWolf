/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * 版本信息
 * @author yangzc
 */
public class OnlineVersion extends BaseObject {
	public static final int FLAG_UPDATE_LASTEST = 0;
	public static final int FLAG_UPDATE_OPTIONAL = 1;
	public static final int FLAG_UPDATE_FORCE = 2;

	public int type;			//0,1,2  0表示最新版本；1表示可选；2表示强制
	public String version;
	public String name;
	public String pubDate;
	public String newFeature;
	public String description;
	public String downloadUrl;
	
//	@Override
//	public void parse(JSONObject json) {
//		super.parse(json);
//		this.code = json.optString("code");
//		this.type = json.optInt("type");
//		this.version = json.optString("version");
//		this.name = json.optString("name");
//		this.pubDate = json.optString("pubDate");
//		this.newFeature = json.optString("newFeature");
//		this.description = json.optString("description");
//		this.downloadUrl = json.optString("downloadUrl");
//	}


	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		JSONObject data = json.optJSONObject("data");
		if (data != null){
			this.type = Integer.parseInt(data.optString("type"));
			this.version = data.optString("version");
			this.name = data.optString("name");
			this.pubDate = data.optString("pubDate");
			this.newFeature = data.optString("newFeature");
			this.description = data.optString("description");
			this.downloadUrl = data.optString("downloadUrl");
		}
	}

	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		try {
			json.put("type", type);
			json.put("version", version);
			json.put("name", name);
			json.put("pubDate", pubDate);
			json.put("newFeature", newFeature);
			json.put("description", description);
			json.put("downloadUrl", downloadUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
}
