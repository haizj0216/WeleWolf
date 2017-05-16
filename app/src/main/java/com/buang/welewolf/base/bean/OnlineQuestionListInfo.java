/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.buang.welewolf.base.database.bean.QuestionItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 答案列表数据
 *
 * @author yangzc
 */
public class OnlineQuestionListInfo extends BaseObject {

    public static final int TYPE_CHOICE = 0;// 选择题 done
    public static final int TYPE_MUTICHOICE = 1;// 多选题 done
    public static final int TYPE_ANSWER = 2;// 解答题
    public static final int TYPE_FILLIN = 3;// 填空
    public static final int TYPE_TRANSLATE = 4;// 翻译
    public static final int TYPE_CLOZE = 5;// 完型
    public static final int TYPE_COMPREHENISON = 6;// 阅读
    public static final int TYPE_RATIONAL = 7;// 语法
    public static final int TYPE_COMPOSITION = 8;// 作文
    public static final int TYPE_FILLIN_WITH_PIC = 9;// 带照片的填空题

    public String mQuestionTypes;
//    public String mHomeworkId;
    public int mCurrentPageNum;
    public int mTotalPageNum;
    public int mTotalCount;
    public int mTotalQuestionCount;
    public int mSubmitNum;
    public double mRightRate;
    public ArrayList<QuestionItem> mQuestions;
    public String mSectionID;
    public int mQuestionSourceType;
    public String mSourceId;
    public String mHomeworkIcon;
    public String mHomeworkIconDesc;
    public String mHomeworkProDesc;
    public String mHomeworkProUrl;
    public boolean mNeedCorrect;
    public String mShareName;
    public String mTeacherName;
    public String mAddTime;
    public long mPublishTime;
    public long mDeadLineTs;
    public String mClassID;
    public String mCrontabID;
    public String mDesc;
    public String mHomeworkID;
    public String mHomeworkName;

    /**
     *题包
     */
    public ArrayList<SubType> mSubTypes;
    public String mPackageID;
    public int mDifficulty;
    public int mHot;
    public int mCurrentSubType;
    public String mTeacherID;
    public String mSchoolName;
    public String mTeacherHead;
    public String mPackageName;
    public boolean isCollect;
    public boolean isFamous;

    public int mFromType = -1;
    public boolean hasUnkownQuestion;
    public String mNeedReviseNums;
    public String mWorkNeedReviseNums;
    public String mWorkHasReviseNums;
    public String mWorkErrorStudentNums;

    public OnlineQuestionListInfo(int fromType) {
//        this.mHomeworkId = homeworkId;
//        this.mSectionID = sectionId;
        mFromType = fromType;
    }

    public void setFromType(int fromType) {
        mFromType = fromType;
    }

    public void setSource(int type, String id) {
        mQuestionSourceType = type;
        mSourceId = id;
    }

    public RecommendTypes mRecommendTypes;
//
//    public OnlineQuestionListInfo(String mSourceId) {
//        this.mSourceId = mSourceId;
//    }

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (isAvailable()) {
            if (json.has("data")) {

                JSONObject data = json.optJSONObject("data");
                JSONArray questionList = null;
                if (data != null) {
                    mHomeworkIcon = data.optString("homeworkIcon");
                    mNeedReviseNums = data.optString("needReviseNums");
                    mHomeworkIconDesc = data.optString("homeworkIconDesc");
                    mHomeworkProDesc = data.optString("homeworkProDesc");
                    mHomeworkProUrl = data.optString("homeworkProUrl");
                    mQuestionTypes = data.optString("allQuestionType");
                    mCurrentPageNum = data.optInt("currentPageNum");
                    mTotalPageNum = data.optInt("totalPageNum");
                    if (data.has("totalQuestionNum"))
                        mTotalQuestionCount = data.optInt("totalQuestionNum");
                    else if (data.has("count")) {
                        mTotalQuestionCount = data.optInt("count");
                    }

                    if (data.has("reviseStatus")) {
                        JSONObject reviseObj = data.optJSONObject("reviseStatus");
                        mWorkHasReviseNums = reviseObj.optString("hasReviseNums");
                        mWorkErrorStudentNums= reviseObj.optString("allErrorStudentNums");
                        mWorkNeedReviseNums = reviseObj.optString("needReviseNums");
                    }

                    mNeedCorrect = data.optInt("needCorrect") == 1 ? true : false;
                    if (data.has("totalNum"))
                        mTotalCount = data.optInt("totalNum");
                    else if (data.has("studentCount"))
                        mTotalCount = data.optInt("studentCount");
                    if (data.has("submitNum")) {
                        mSubmitNum = data.optInt("submitNum");
                    } else if (data.has("doCount")) {
                        mSubmitNum = data.optInt("doCount");
                    }
                    if (data.has("homeworkID")) {
                        mHomeworkID = data.optString("homeworkID");
                    }
                    if (data.has("homeworkTitle")) {
                        mHomeworkName = data.optString("homeworkTitle");
                    }
                    mRightRate = data.optDouble("rightRate", 0.0);
                    try {
                        questionList = data.optJSONArray("list");
                    }catch (Exception e) {
                        JSONObject questionObj = data.optJSONObject("list");
                        mQuestionTypes = questionObj.optString("allQuestionType");
                        mCurrentPageNum = questionObj.optInt("currentPageNum");
                        mTotalPageNum = questionObj.optInt("totalPageNum");
                        if (questionObj.has("totalQuestionNum"))
                            mTotalQuestionCount = questionObj.optInt("totalQuestionNum");
                        questionList = questionObj.optJSONArray("list");
                    }
                    if (questionList == null) {
                        questionList = data.optJSONArray("questionList");
                    }
                    if (questionList == null) {
                        try {
                            JSONObject list = data.optJSONObject("questionList");
                            mCurrentPageNum = list.optInt("currentPageNum");
                            mTotalPageNum = list.optInt("totalPageNum");
                            if (data.has("totalQuestionNum"))
                                mTotalQuestionCount = list.optInt("totalQuestionNum");
                            else if (data.has("count")) {
                                mTotalQuestionCount = list.optInt("count");
                            }
                            questionList = list.optJSONArray("list");
                        }catch (Exception e) {

                        }

                    }
                    if (questionList == null) {
                        try {
                            JSONObject questionObj = data.optJSONObject("list");
                            mQuestionTypes = questionObj.optString("allQuestionType");
                            mCurrentPageNum = questionObj.optInt("currentPageNum");
                            mTotalPageNum = questionObj.optInt("totalPageNum");
                            if (questionObj.has("sourceID")) {
                                mSourceId = questionObj.optString("sourceID");
                            }
                            if (questionObj.has("sourceType")) {
                                mQuestionSourceType = questionObj.optInt("sourceType");
                            }
                            if (questionObj.has("totalQuestionNum"))
                                mTotalQuestionCount = questionObj.optInt("totalQuestionNum");
                            questionList = questionObj.optJSONArray("list");
                        }catch (Exception e){
                        }
                    }
                    mShareName = data.optString("shareName");
                    mTeacherName = data.optString("teacherName");
                    mAddTime = data.optString("addTime");
                    mCrontabID = data.optString("crontabID");
                    mClassID = data.optString("classID");
                    mPublishTime = data.optLong("publishTime");
                    mDeadLineTs = data.optLong("endTime");
                    mDesc = data.optString("desc");
                    mPackageID = data.optString("packageID");
                    mCurrentSubType = data.optInt("currentSubTypes");
                    if (data.has("sourceID")) {
                        mSourceId = data.optString("sourceID");
                    }
                    if (data.has("sourceType")) {
                        mQuestionSourceType = data.optInt("sourceType");
                    }
                    mHot = data.optInt("hot");
                    mDifficulty = data.optInt("difficulty");
                    mSchoolName = data.optString("schoolName");
                    mTeacherID = data.optString("teacherID");
                    mTeacherHead = data.optString("headPhoto");
                    mPackageName = data.optString("packageName");
                    if (data.has("isFamous")) {
                        isFamous = data.optInt("isFamous") == 1;
                    }
                    if (data.has("isCollect"))
                        isCollect = data.optInt("isCollect") == 1;

                    JSONObject recommend = data.optJSONObject("recommend");
                    if (null != recommend) {
                        mRecommendTypes = new RecommendTypes();
                        mRecommendTypes.mPackageId = recommend.optString("packageID");
                        mRecommendTypes.mSectionId = recommend.optString("sourceID");
                        mRecommendTypes.mSourceType = recommend.optString("sourceType");
                        mRecommendTypes.mPackageName = recommend.optString("packageName");
                        mRecommendTypes.mTeacherName = recommend.optString("teacherName");
                        mRecommendTypes.mSchoolName = recommend.optString("schoolName");
                        mRecommendTypes.mHot = recommend.optString("hot");
                        JSONArray recommendArray = recommend.optJSONArray("subTypes");
                        for (int i = 0; i < recommendArray.length(); i++) {
                            JSONObject subtype = recommendArray.optJSONObject(i);
                            SubType item = new SubType();
                            item.mTypeID = subtype.optInt("typeID");
                            item.mTypeCount = subtype.optInt("typeCount");
                            item.mTypeName = subtype.optString("typeName");
                            item.mShowName = subtype.optString("showName");
                            mRecommendTypes.recommendSubTypes.add(item);
                        }
                    }

                } else {
                    questionList = json.optJSONArray("data");
                }


                if (questionList != null) {
                    if (mQuestions != null) {
                        mQuestions.clear();
                    }
                    for (int i = 0; i < questionList.length(); i++) {
                        JSONObject question = questionList.optJSONObject(i);
                        if (question != null) {
                            if (mQuestions == null) {
                                mQuestions = new ArrayList<QuestionItem>();
                            }
                            QuestionItem item = new QuestionItem();
                            item = item.getQuestionItem(question);
                            if (item.hasUnkownQuestion) {
                                hasUnkownQuestion = true;
                            }
//                            item.mHomeworkId = mHomeworkId;
                            item.mSubmitNum = mSubmitNum;
                            item.mSectionId = mSectionID;
                            if (!item.hasSourceType) {
                                item.mQuestionSourceType = mQuestionSourceType;
                            }
                            if (TextUtils.isEmpty(item.mSourceId) && !TextUtils.isEmpty(mSourceId)) {
                                item.mSourceId = mSourceId;
                            }
                            if (mFromType != -1) {
                                item.mFromType = mFromType;
                            }
                            if (!TextUtils.isEmpty(mSectionID)) {
                                item.mSectionId = mSectionID;
                            }
                            if (item != null) {
                                mQuestions.add(item);
                            }
                        }
                    }
                }

                if (data != null && data.has("subTypes")) {
                    JSONArray subs = data.optJSONArray("subTypes");
                    mSubTypes = new ArrayList<SubType>();
                    for (int i = 0; i < subs.length(); i++) {
                        JSONObject sub = subs.optJSONObject(i);
                        SubType type = new SubType();
                        type.mTypeID = sub.optInt("typeID");
                        type.mTypeName = sub.optString("typeName");
                        type.mPackageId = mPackageID;
                        if (mCurrentSubType == type.mTypeID) {
                            type.mQuestions = mQuestions;
                            type.mCurrentPageNum = mCurrentPageNum;
                            type.mTotalPageNum = mTotalPageNum;
                            type.mTotalQuestionCount = mTotalQuestionCount;
                        }


                        mSubTypes.add(type);
                    }
                }
                return;
            }
//			mHomeworkId = json.optString("homeworkID");
            mCurrentPageNum = json.optInt("currentPageNum");
            mTotalPageNum = json.optInt("totalPageNum");
            mTotalCount = json.optInt("totalNum");
            mSubmitNum = json.optInt("submitNum");
            JSONArray questionList = json.optJSONArray("list");
            if (questionList != null) {
                if (mQuestions != null)
                    mQuestions.clear();
                for (int i = 0; i < questionList.length(); i++) {
                    JSONObject question = questionList.optJSONObject(i);
                    if (question != null) {
                        if (mQuestions == null)
                            mQuestions = new ArrayList<QuestionItem>();
                        QuestionItem item = new QuestionItem();
                        item = item.getQuestionItem(question);
//                        item.mHomeworkId = mHomeworkId;
                        if (item.hasUnkownQuestion) {
                            hasUnkownQuestion = true;
                        }
                        item.mSubmitNum = mSubmitNum;
                        item.mSectionId = mSectionID;
                        if (!item.hasSourceType){
                            item.mQuestionSourceType = mQuestionSourceType;
                        }
                        if (TextUtils.isEmpty(item.mSourceId) && !TextUtils.isEmpty(mSourceId)) {
                            item.mSourceId = mSourceId;
                        }

                        if (!TextUtils.isEmpty(mSectionID))
                            item.mSectionId = mSectionID;
                        if (item != null)
                            mQuestions.add(item);
                    }
                }
            }
        }
    }

    public static class RecommendTypes {
        public String mPackageId;
        public String mSectionId;
        public String mSourceType;
        public String mPackageName;
        public String mTeacherName;
        public String mSchoolName;
        public String mHot;

        public List<SubType> recommendSubTypes = new ArrayList<SubType>();
    }

    public static class SubType implements Serializable {

        private static final long serialVersionUID = 3813765919051513082L;
        public int mTypeID = 1;
        public String mTypeName;
        public String mPackageId;
        public List<QuestionItem> mQuestions;
        public int mCurrentPageNum;
        public int mTotalPageNum;
        public int mTotalQuestionCount;
        public int mTypeCount;
        public String mShowName;
    }

}
