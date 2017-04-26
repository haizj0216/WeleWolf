package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

import java.io.Serializable;

public class OnlineGroupItemInfo extends BaseObject implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -4292784969304925259L;
	//groupType:1 名师推荐作业；2 其他老师作业；3 热门知识点排行；4 highlight考点；5 精选专题；6 名校试卷；7 精品试卷；
	public static final int TYPE_COLLECT = 100;
	public static final int TYPE_PHOTO = 200;
	public static final int TYPE_MY_GROUP = 300;
	public static final int TYPE_PHOTO_DEFAULT = 0;

	public String mGroupID;
	public String mGroupName;
	public int mIsDefault;
	public String mCount = "0";
	public long mAddTime;
	public int mType;
	public String mTeacherName;
	public String mTeacherSchool;
	public String mRefer;
	public String mTeacherHead;
	public int mDifficulty;
	public int mHot;
	public String mPaperYear;
	public String mGroupImage;
	public int mFromType = -1;
	public String mSourceName;
	public String mSourceId;

	public boolean isEmpty;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		JSONObject data = json.optJSONObject("data");
		if (data != null) {
			if (data.has("groupID"))
				mGroupID = data.optString("groupID");
			if (data.has("groupId")) {
				mGroupID = data.optString("groupId");
			}
			if (data.has("name"))
				mGroupName = data.optString("name");
		}
	}

//	public boolean isRecommend() {
//		return mType != TYPE_COLLECT && mType != TYPE_PHOTO && mType != TYPE_MY_GROUP && mType != TYPE_PHOTO_DEFAULT;
//	}

	public boolean isSingleType() {
		return mType == TYPE_COLLECT || mType == TYPE_PHOTO || mType == TYPE_MY_GROUP || mType == TYPE_PHOTO_DEFAULT;
	}
}
