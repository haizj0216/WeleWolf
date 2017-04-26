/**
 * Copyright (c) 2014 KI.Inc
 * 
 * @author Zhouhenglei
 * @date 2014.9.1
 * @version 1.0.0
 */
package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 作业排行
 * @author Fanjb
 * @date 2015年5月28日
 */
public class OnlineHomeworkRankInfo extends BaseObject {

	public class RankItem {
		public String mStudentID;
		public String mStudentName;
		public String mHeadPhoto;
		public String mRate;
		public String mUserID;

		public void parse(JSONObject json) {
			this.mStudentID = json.optString("studentID");
			this.mStudentName = json.optString("studentName");
			this.mHeadPhoto = json.optString("headPhoto");
			this.mRate = json.optString("rightRate");
			this.mUserID = json.optString("userID");
		}
	}

	private List<RankItem> mRankList;

	public List<RankItem> getRankList() {
		return this.mRankList;
	}

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		JSONArray list = json.optJSONArray("list");
		if (list == null) {
			list = json.optJSONArray("data");
		}
		if (list != null) {
			mRankList = new ArrayList<RankItem>();
			for (int i = 0; i < list.length(); i++) {
				JSONObject item = list.optJSONObject(i);
				if (item != null) {
					RankItem rankItem = new RankItem();
					rankItem.parse(item);
					mRankList.add(rankItem);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "HomeworkRankInfo [mRankList=" + mRankList + "]";
	}
}
