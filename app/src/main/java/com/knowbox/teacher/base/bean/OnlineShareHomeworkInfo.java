/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.base.service.share.ShareContent;

import org.json.JSONObject;

/**
 * 奖励班级信息
 * 
 * @author LiuYu
 */
public class OnlineShareHomeworkInfo extends BaseObject {

	public ShareContent shareContent;

	@Override
	public void parse(JSONObject data) {
		super.parse(data);
		if (isAvailable()) {

			JSONObject shareData = data.optJSONObject("data");
			shareContent = new ShareContent();
			shareContent.mSiteName = shareData.optString("site");
			shareContent.mSiteUrl = shareData.optString("siteUrl");
			shareContent.mUrlImage = shareData.optString("imageUrl");
			shareContent.mShareContent = shareData.optString("text");
			shareContent.mShareTitle = shareData.optString("title");
			shareContent.mDescription = shareData.optString("text");
			shareContent.mShareTitleUrl = shareData.optString("titleUrl");
			shareContent.mShareUrl = shareData.optString("url");

		}
	}
}
