/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.base.database.bean;

import android.text.TextUtils;

import com.knowbox.teacher.base.bean.OnlineRecorderItemInfo;
import com.knowbox.teacher.base.bean.OptionsItemInfo;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.TemplateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目项
 * 
 * @author yangzc
 *
 */
public class QuestionItem extends BaseItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8612317434058727283L;

//	public static final int TYPE_CHOICE = 0;// 选择题 done
//	public static final int TYPE_MUTICHOICE = 1;// 多选题 done
//	public static final int TYPE_ANSWER = 2;// 解答题
//	public static final int TYPE_FILLIN = 3;// 填空
//	public static final int TYPE_TRANSLATE = 4;// 翻译
//	public static final int TYPE_CLOZE = 5;// 完型
//	public static final int TYPE_COMPREHENISON = 6;// 阅读
//	public static final int TYPE_RATIONAL = 7;// 语法
//	public static final int TYPE_COMPOSITION = 8;// 作文
//	public static final int TYPE_FILLIN_WITH_PIC = 9;// 带照片的填空题

	public static final int FLAG_RIGHT_ABSOLUTELY = 2;
	public static final int FLAG_RIGHT_PARTIALLY = 1;
	public static final int FLAG_WRONG = 0;
	public static final int FLAG_UNCORRECT = -1;

	public static final int SOURCE_TYPE_SECTION = 1;
	public static final int SOURCE_TYPE_KNOWLEDGE = 2;
	public static final int SOURCE_TYPE_PAPER = 3;
	public static final int SOURCE_TYPE_PERSONGROUP = 4;
	public static final int SOURCE_TYPE_PHOTO = 5;
	public static final int SOURCE_TYPE_SUPER = 6;
	public static final int SOURCE_TYPE_SHARE = 7;
	public static final int SOURCE_TYPE_SYNC = 8;
	public static final int SOURCE_TYPE_TOPIC = 9;
	public static final int SOURCE_TYPE_ISSUE = 10;
	public static final int SOURCE_TYPE_ERROR = 100;

	public int mQuestionSourceType;
	public String mSourceId;
	public int mQuestionType;// 题目类型
	public int mParentType;
	public String mQuestionId;// 题目Id
	public String mQuestionIndex;
	public String mHomeworkId;// 作业Id
	public String mContent;// 内容
	public String mContentSource;//内容来源
	public float mRightRate;// 正确率
	public int mSubmitNum;
	public int mCorrectedNum;// 已经批改数
	public String mSectionId;
	public String mParentQuestionId;// 父ID
	public boolean mIsOut;
	public boolean mIsCollect;
	public int mDifficulty;
	public long mHot;
	public boolean wellChosen;
	public int mShowIndex;
	public String mLastErrorRate;
	public String mCountryErrorRate;

	// 下载进度
	public float mDlProgress;

	// 子题目
	public List<QuestionItem> mSubQuestions;

	public String mRightAnswer;
	public String mAnswerExplain;
	public List<String> mKnowledgeName;
	public String mSectionName;
	public String mGroupName = "我的收藏";
	public String mGroupID = "0";

	public ArrayList<OptionsItemInfo> itemList = new ArrayList<OptionsItemInfo>();
	// -1 未批改 0错 1半对，2全对
	public int correctScore;
	// 收藏状态，0：未处理，1：收藏，2：忽略
	public int favorStatus;
	public String mAnswer;//选择题的学生答案
	public List<String> mAnswers;//解答题的学生答案
	public List<String> mCorrectAnswers;//解答题老师批改过的答案
	public int index;// 位于全部集合里的index
	public int tempIndex;// 临时集合里的index
	public ArrayList<OnlineRecorderItemInfo> mQuestionAudios;
	public ArrayList<OnlineRecorderItemInfo> mAnswerAudios;

	public boolean mNeedCorrected = false;

	public int mFromType = ConstantsUtils.FROM_TYPE_BASIC;

	public boolean isSpecial = false;

	public String mCategory;

	public String mTag;

	public String mChineseGrade;//语基题目 年级

	public boolean hasSourceType;

	public boolean hasUnkownQuestion;

	public boolean isDone() {
		return !TextUtils.isEmpty(mAnswer);
	}

	public int wrongCount;
	public String questionDim;
	public String gymText;
	public String gymImage;
	public String gymAudio;
	public String wordBlank;
	public String options;

	/**
	 * 获得题目类型名称
	 * 
	 * @return
	 */
	public String getQuestionTypeName() {
		return SubjectUtils.getNameByQuestionType(mQuestionType);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof QuestionItem) {
			QuestionItem item = (QuestionItem) o;
			return mQuestionId.equalsIgnoreCase(item.mQuestionId);
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return 0;
	}

	/**
	 * 解析题目
	 * 
	 * @param question
	 */
	public QuestionItem getQuestionItem(JSONObject question) {
		return parseSingleQuestion(question);
	}

	private QuestionItem parseSingleQuestion(JSONObject question) {
		int type = question.optInt("questionType");
		QuestionItem item = new QuestionItem();
		item.mQuestionType = type;
		item.mQuestionId = question.optString("questionID");
		item.mContent = question.optString("content");
		item.mContentSource = question.optString("contentSource");
		if (TextUtils.isEmpty(item.mContent)) {
			item.mContent = question.optString("question");
		}
		item.mContent = TemplateUtils.clearExtraLlktCss(item.mContent);
		item.correctScore = question.optInt("correctScore");
		item.mQuestionIndex = question.optString("questionNo");
		item.mAnswerExplain = question.optString("answerExplain");
		item.mRightAnswer = question.optString("rightAnswer");
		item.mAnswer = question.optString("answer");
		item.mAnswers = new ArrayList<String>();
		JSONArray answerArrays = question.optJSONArray("answers");
		if (answerArrays != null) {
			for (int i = 0; i < answerArrays.length(); i++) {
				item.mAnswers.add(answerArrays.optString(i, ""));
			}
		}
		item.mCorrectAnswers = new ArrayList<String>();
		JSONArray mCorrentArrays = question.optJSONArray("correctAnswers");
		if (mCorrentArrays != null) {
			for (int i = 0; i < mCorrentArrays.length(); i++) {
				item.mCorrectAnswers.add(mCorrentArrays.optString(i, ""));
			}
		}
		item.mKnowledgeName = new ArrayList<String>();
		JSONArray arrays = question.optJSONArray("knowList");
		if (arrays != null) {
			for (int i = 0; i < arrays.length(); i++) {
				item.mKnowledgeName.add(arrays.optString(i, ""));
			}
		}else {
			arrays = question.optJSONArray("knowledgeName");
			if (arrays != null) {
				for (int i = 0; i < arrays.length(); i++) {
					item.mKnowledgeName.add(arrays.optString(i, ""));
				}
			}
		}

		item.mSectionName = question.optString("sectionName");
		try {
			if (question.has("rightRate"))
				item.mRightRate = (float) question.optDouble("rightRate", 0.0);
		} catch (Exception e) {
		}
		try {
			if (question.has("correctedNum"))
				item.mCorrectedNum = question.optInt("correctedNum");
			if (question.has("correctedCount"))
				item.mCorrectedNum = question.optInt("correctedCount");
		} catch (Exception e) {
		}
		item.mDifficulty = question.optInt("difficulty");
		item.mHot = question.optLong("hot");
		item.mIsOut = question.optString("isOut").equals("1");
		item.mIsCollect = question.optString("isCollect").equals("1");
		item.wellChosen = question.optInt("wellChosen") == 1;
		if (question.has("groupInfo")) {
			JSONObject group = question.optJSONObject("groupInfo");
			JSONArray groups = question.optJSONArray("groupInfo");
			if (group != null) {
				item.mGroupID = group.optString("groupId");
				item.mGroupName = group.optString("name");
			} else if (groups != null && groups.length() > 0) {
				group = groups.optJSONObject(0);
				if (group != null) {
					item.mGroupID = group.optString("groupId");
					item.mGroupName = group.optString("name");
				}
			}

		}
		if (question.has("itemList")) {
			JSONArray itemList = question.optJSONArray("itemList");
			for (int j = 0; j < itemList.length(); j++) {
				JSONObject object = itemList.optJSONObject(j);
				if (object.has("code")) {
					OptionsItemInfo info = new OptionsItemInfo();
					info.setCode(object.optString("code"));
					info.setValue(object.optString("value"));
					item.itemList.add(info);
				} else if (object.has("itemCode")) {
					OptionsItemInfo info = new OptionsItemInfo();
					info.setCode(object.optString("itemCode"));
					info.setValue(object.optString("questionItem"));
					item.itemList.add(info);
				}else
					break;
			}

		}
		JSONArray questionAudios = question.optJSONArray("questionAudio");
		if(questionAudios != null) {
			item.mQuestionAudios = new ArrayList<OnlineRecorderItemInfo>();
			for (int j = 0; j < questionAudios.length(); j ++) {
				JSONObject audio = questionAudios.optJSONObject(j);
				OnlineRecorderItemInfo info = new OnlineRecorderItemInfo();
				info.mOnlineUrl = audio.optString("src");
				if (audio.has("duration"))
					info.mDuration = audio.optInt("duration");
				if (audio.has("len"))
					info.mDuration = audio.optInt("len");
				item.mQuestionAudios.add(info);
				item.index = j;
			}
		}

		JSONArray answerAudio = question.optJSONArray("answerAudio");
		if(answerAudio != null) {
			item.mAnswerAudios = new ArrayList<OnlineRecorderItemInfo>();
			for (int j = 0; j < answerAudio.length(); j ++) {
				JSONObject audio = answerAudio.optJSONObject(j);
				OnlineRecorderItemInfo info = new OnlineRecorderItemInfo();
				info.mOnlineUrl = audio.optString("src");
				if (audio.has("duration"))
					info.mDuration = audio.optInt("duration");
				if (audio.has("len"))
					info.mDuration = audio.optInt("len");
				item.mAnswerAudios.add(info);
				item.index = j;
			}
		}

		if (question.has("sourceID")) {
			item.mSourceId = question.optString("sourceID");
		}

		if (question.has("sourceType")) {
			item.mQuestionSourceType = question.optInt("sourceType");
			item.hasSourceType = true;
		}

		if (question.has("category")) {
			item.mCategory = question.optString("category");
			item.isSpecial = true;
		}

		if (question.has("tag")) {
			item.mTag = question.optString("tag");
		}

		if (question.has("grade")) {
			item.mChineseGrade = question.optString("grade");
		}
		if (question.has("homeworkID")) {
			item.mHomeworkId = question.optString("homeworkID");
		}

		if (question.has("lastErrorRate")) {
			item.mLastErrorRate = question.optString("lastErrorRate");
		}
		if (question.has("countryErrorRate")) {
			item.mCountryErrorRate = question.optString("countryErrorRate");
		}

		if (question.has("fromType")) {
			item.mFromType = question.optInt("fromType");
		}

		item.wrongCount = question.optInt("wrongCount");

		item.questionDim = question.optString("questionDim");
		if (question.optJSONObject("content") != null) {
			JSONObject gymContent = question.optJSONObject("content");
			item.gymText = gymContent.optString("text");
			item.gymAudio = gymContent.optString("audio");
			item.gymImage = gymContent.optString("image");
			item.wordBlank = gymContent.optString("wordBlank");
			item.options = gymContent.optString("options");
		}

		return item;
	}

	public boolean isBlank() {
		if (TextUtils.isEmpty(questionDim)) {
			return false;
		}
		if (questionDim.equals(String.valueOf(SubjectUtils.QUESTION_DIM_QUANPIN))
				|| questionDim.equals(String.valueOf(SubjectUtils.QUESTION_DIM_WAKOONG))) {
			return true;
		}
		return false;
	}
}
