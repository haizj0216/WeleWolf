/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * 聊天时发送的题目链接
 */
public class OnlineQuestionLink extends BaseObject {

    public String mAnswerId;
    public String mHomeworkId;
    public String mClassId;
    public String mStartTime;
    public String mEndTime;
    public int mOrderNum;
    public int mQuestionType;
    public int mSubjectCode;
    public String mQuestionId;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (isAvailable()) {
            JSONObject data = json.optJSONObject("data");
            if (data != null) {
                mAnswerId = data.optString("answerID");
                mHomeworkId = data.optString("homeworkID");
                mClassId = data.optString("classID");
                mStartTime = data.optString("startTime");
                mEndTime = data.optString("endTime");
                mOrderNum = data.optInt("orderNum");
                mQuestionType = data.optInt("questionType");
                mSubjectCode = data.optInt("subject");
                mQuestionId = data.optString("questionID");
            }
        }
    }
}
