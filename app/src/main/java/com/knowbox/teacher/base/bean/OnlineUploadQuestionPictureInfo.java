package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class OnlineUploadQuestionPictureInfo extends BaseObject {

	public String mServerID;
	public List<UploadPictureItemInfo> mPictures;

	@Override
	public void parse(JSONObject json) {
		// TODO Auto-generated method stub
		super.parse(json);
		if (isAvailable()) {
			mServerID = json.optString("serverID");
			JSONArray array = json.optJSONArray("pictureUrlList");
			for (int i = 0; i < array.length(); i++) {
				if (mPictures != null && mPictures.get(i) != null)
					mPictures.get(i).mPicUrl = array.optString(i);
			}
		}
	}

	public static class UploadPictureItemInfo {
		public int[] mBitmapSize;
		public String mPicUrl;
	}
}
