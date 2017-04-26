package com.knowbox.teacher.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineReviseStudentInfo extends BaseObject {

	public List<ReviseStudentItem> mReviseItems = new ArrayList<OnlineReviseStudentInfo.ReviseStudentItem>();
	public String mHomeworkID;


	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		try {
			if (isAvailable()) {
				JSONObject obj = json.optJSONObject("data");
				if (null != obj) {
					mHomeworkID = obj.optString("homeworkID");
					JSONArray arrays = obj.optJSONArray("list");
					if (arrays != null) {
						for (int i = 0; i < arrays.length(); i++) {
							JSONObject studentItem = arrays.optJSONObject(i);
							ReviseStudentItem item = new ReviseStudentItem();
							item.mStudentId = studentItem.optString("studentID");
							item.mUserId = studentItem.optString("userID");
							item.mUserName = studentItem.optString("userName");
							item.mHeadPhoto = studentItem.optString("headPhoto");
							item.mErrorCount = studentItem.optString("errorCount");
							item.mRedoCount = studentItem.optString("redoCount");
							item.mHasRemind = studentItem.optInt("hasRemind") != 0;
							if (!TextUtils.isEmpty(item.mRedoCount) && Integer.parseInt(item.mRedoCount) == 0) {
								item.mHasRemind = true;
							}
							mReviseItems.add(item);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class ReviseStudentItem {
		public String mUserId;
		public String mUserName;
		public String mStudentId;
		public String mHeadPhoto;
		public String mErrorCount;
		public String mRedoCount;
		public boolean mHasRemind;
	}

}
