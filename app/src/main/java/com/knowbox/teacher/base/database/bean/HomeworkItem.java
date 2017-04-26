/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.base.database.bean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业数据
 * @author yangzc
 *
 */
public class HomeworkItem extends BaseItem implements Serializable {

	private static final long serialVersionUID = -8480455509042344619L;
	
	public static final int STATUS_INIT = 1;
	public static final int STATUS_DOWNLOADING = 2;
	public static final int STATUS_PAUSE = 3;
	public static final int STATUS_DONE = 4;

	public String mHomeworkTitle;
	public String mHomeworkId;
	public long mCreateTs;
	public long mDeadLineTs;
	public String mClassId;//班级ID
	public String mClassName;
	public String mGrade;
	public int mStudentCnt;
	public int mCorrectedNum;
	public int mNeedCorrectNum;
	public int mCommitedNum;
	public double mRightRate;
	public List<String> mSectionName;
	public List<String> mKnowledgeName;
	public String mHomeworkIcon;
	public String mHomeworkDesc;
	public boolean mNeedCorrect;
	public String mQuestionNum;//作业题目数
	public String mCrontabId;//待发布作业id
	public boolean mIsCrontab;//是否是待发布作业
	public long mPublishTs;//发布时间
	public boolean mIsCorrectTab;//是否是待批改tab下面的作业
	//下载进度
	public float mDlProgress;
	public int mDownloadStatus = STATUS_INIT;

	public boolean isVirtual;
	public boolean isEmpty;
	public boolean lastMonthCorrected;

	public void parseItem(HomeworkItem homework, JSONObject item) {
		homework.mHomeworkTitle = item.optString("homeworkTitle");
		homework.mHomeworkId = item.optString("homeworkID");
		homework.mCreateTs = item.optLong("addTime");
		homework.mClassName = item.optString("className");
		homework.mGrade = item.optString("grade");
		homework.mStudentCnt = item.optInt("studentCount");
		homework.mCorrectedNum = item.optInt("correctedNum");
		homework.mNeedCorrectNum = item.optInt("needCorrectNum");
		homework.mCommitedNum = item.optInt("doCount");
		homework.mRightRate = item.optDouble("rightRate", 0.0);
		homework.mDeadLineTs = item.optLong("endTime");
		homework.mClassId = item.optString("classID");
		homework.mQuestionNum = item.optString("questionNum");
		homework.mHomeworkIcon = item.optString("homeworkIcon");
		homework.mHomeworkDesc = item.optString("homeworkIconDesc");
		homework.mNeedCorrect = item.optInt("needCorrect") == 1 ? true : false;
		JSONArray arrays;
		try {
			homework.mSectionName = new ArrayList<String>();
			String sectionName = item.optString("sectionName");
			if (!TextUtils.isEmpty(sectionName)) {
				String[] sectionNames = sectionName.split(",");
				for (int j = 0; j < sectionNames.length; j++) {
					homework.mSectionName.add(sectionNames[j]);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			homework.mKnowledgeName = new ArrayList<String>();
			arrays = item.optJSONArray("knowledgeName");
			if (arrays != null) {
				for (int j = 0;j<arrays.length();j++) {
					homework.mKnowledgeName.add(arrays.getString(j));
				}
			} else {
				String knowledge = item.optString("knowledgeName");
				if (!TextUtils.isEmpty(knowledge)) {
					homework.mKnowledgeName.add(knowledge);
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
}
