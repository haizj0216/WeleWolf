package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineClassWorkRankInfo extends BaseObject {

	public List<HomeworkRankItem> mHomeworkRankItems = new ArrayList<OnlineClassWorkRankInfo.HomeworkRankItem>();


	public int mCoinRemainCount;
	public boolean mFinishedTesk;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		try {
			if (isAvailable()) {
				JSONObject obj = json.optJSONObject("data");
				if (null != obj) {
					mCoinRemainCount = obj.optInt("coinRemainCount");
					mFinishedTesk = obj.optBoolean("finishedTask", false);
					JSONArray arrays = obj.optJSONArray("studentList");
					if (arrays != null) {
						for (int i = 0; i < arrays.length(); i++) {
							JSONObject studentItem = arrays.optJSONObject(i);
							HomeworkRankItem item = new HomeworkRankItem();
							item.mStudentId = studentItem.optString("studentID");
							item.mUserId = studentItem.optString("userID");
							item.mScore = studentItem.optString("score");
							item.mUserName = studentItem.optString("userName");
							item.mHeadPhoto = studentItem.optString("headPhoto");
							item.mSex = studentItem.optString("sex");
							item.mGainedCoinCount = studentItem.optInt("gainedCoinCount");
							item.mGainedCoin = studentItem.optInt("gainedCoin") == 1;
							item.mSubmitRate = studentItem.optDouble("submitRate", 0.0);
							item.mRightRate = studentItem.optDouble("rightRate", 0.0);
							item.mSchoolID = studentItem.optString("schoolID");
							item.mSchoolName = studentItem.optString("schoolName");
							item.mRanking = String.valueOf(i + 1);
							mHomeworkRankItems.add(item);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class HomeworkRankItem {
		public String mStudentId;
		public String mScore;
		public String mUserName;
		public String mHeadPhoto;
		public String mSex;
		public int mGainedCoinCount;
		public boolean mGainedCoin;
		public double mSubmitRate;
		public double mRightRate;
		public String mRanking;
		public boolean isEmpty;
		public boolean isFooter;
		public String mUserId;
		public String mSchoolID;
		public String mSchoolName;
	}

}
