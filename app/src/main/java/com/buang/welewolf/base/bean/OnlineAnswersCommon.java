/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.buang.welewolf.base.database.bean.AnswerItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 在线题目信息
 * 
 * @author yangzc
 *
 */
public class OnlineAnswersCommon extends BaseObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3752508552578745685L;
	// 题干
	public String mQuestion;
	// 答案解析
	public String mAnswerExplain;
	// 题目来源
	public String mContentSource;
	// 正确答案
	public String mRightAnswer;
	// 章节名
	public String mSectionName;
	// 知识点
	public List<String> mKnowledgeNames = new ArrayList<String>();
	// 答案列表
	public List<AnswerItem> mAnswerList;
	// 错误信息
	public String mMessage;
	private String mHomeworkId;
	public String mQuestionId;
	public String mAnswerType;
	
	public ArrayList<OnlineRecorderItemInfo> mQuestionAudios;

	public OnlineAnswersCommon(String homeworkId, String questionId) {
		this.mHomeworkId = homeworkId;
		this.mQuestionId = questionId;
	}

	public OnlineAnswersCommon(String homeworkId, String questionId,
			String answerType) {
		this.mHomeworkId = homeworkId;
		this.mQuestionId = questionId;
		this.mAnswerType = answerType;
	}

	@Override
	public void parse(JSONObject data) {
		super.parse(data);
		JSONObject json = null;
		if (data.has("data")) {
			json = data.optJSONObject("data");
		} else {
			json = data;
		}
		this.mMessage = json.optString("message");
		if (isAvailable()) {
			mQuestion = json.optString("question");
			if(TextUtils.isEmpty(mQuestion)) {
				mQuestion = json.optString("content");
			}
			mRightAnswer = json.optString("rightAnswer");
			mAnswerExplain = json.optString("answerExplain");
			mContentSource = json.optString("contentSource");
			JSONArray knowledgeArray = json.optJSONArray("knowledgeName");
			if (knowledgeArray != null) {
				mKnowledgeNames.clear();
				for (int i = 0; i < knowledgeArray.length(); i++) {
					mKnowledgeNames.add(knowledgeArray.optString(i));
				}
			}
			mSectionName = json.optString("sectionName");
			JSONArray questionAudios = json.optJSONArray("questionAudio");
			if(questionAudios != null) {
				mQuestionAudios = new ArrayList<OnlineRecorderItemInfo>();
				for (int j = 0; j < questionAudios.length(); j ++) {
					JSONObject audio = questionAudios.optJSONObject(j);
					OnlineRecorderItemInfo info = new OnlineRecorderItemInfo();
					info.mOnlineUrl = audio.optString("src");
					info.mDuration = audio.optInt("duration");
					mQuestionAudios.add(info);
				}
			}
			JSONArray array = json.optJSONArray("studentList");
			if (mAnswerList == null)
				mAnswerList = new ArrayList<AnswerItem>();
			mAnswerList.clear();
			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					AnswerItem item = new AnswerItem();
					JSONObject answer = array.optJSONObject(i);
					item.studentId = answer.optString("studentID");
					item.userId = answer.optString("userID");
					item.studenetName = answer.optString("studentName");
					item.headPhoto = answer.optString("headPhoto");
					item.correctScore = answer.optString("correctScore");
					item.correctRate = answer.optString("correctRate");
					item.isPraise = answer.optString("isPraise", "");
					item.isSuggest = answer.optString("isSuggest", "");
					item.answerId = answer.optString("answerID");

					if (answer.has("answers")) {
						JSONArray answerPicJsonArray = answer
								.optJSONArray("answers");
						if (answerPicJsonArray != null) {
							StringBuffer buffer = new StringBuffer();
							for (int j = 0; j < answerPicJsonArray.length(); j++) {
								if (j == 0) {
									buffer.append(answerPicJsonArray
											.optString(j));
								} else {
									buffer.append(","
											+ answerPicJsonArray.optString(j));
								}
							}
							item.answers = buffer.toString();
						}
					} else {
						item.answers = answer.optString("answer");
					}

					JSONArray correctPicJsonArray = answer
							.optJSONArray("correctAnswers");
					if (correctPicJsonArray != null) {
						StringBuffer buffer = new StringBuffer();
						for (int j = 0; j < correctPicJsonArray.length(); j++) {
							if (j == 0) {
								buffer.append(correctPicJsonArray.optString(j));
							} else {
								buffer.append(","
										+ correctPicJsonArray.optString(j));
							}
						}
						item.correctAnswers = buffer.toString();
					}

					item.answerExplain = mAnswerExplain;
					item.question = mQuestion;
					if (!TextUtils.isEmpty(mAnswerType)) {
						item.answerType = mAnswerType;
					}
					JSONArray subAnswerArray = answer
							.optJSONArray("answerList");
					StringBuffer subAnswerIdBuffer = new StringBuffer();
					if (subAnswerArray != null) {
						List<AnswerItem.FillinWithPicAnswerItem> subItems = new ArrayList<AnswerItem.FillinWithPicAnswerItem>(
								subAnswerArray.length());
						for (int j = 0; j < subAnswerArray.length(); j++) {
							JSONObject subItemJson = subAnswerArray
									.optJSONObject(j);
							AnswerItem.FillinWithPicAnswerItem subItem = new AnswerItem.FillinWithPicAnswerItem();
							subItem.answerId = subItemJson
									.optString("answerID");
							subItem.questionNo = subItemJson
									.optString("questionNo");
							subItem.correctScore = subItemJson
									.optString("correctScore");
							subItems.add(subItem);
							if (j == 0) {
								subAnswerIdBuffer.append(subItem.answerId);
							} else {
								subAnswerIdBuffer
										.append("," + subItem.answerId);
							}
						}
						item.items = subItems;
					}
					//支持标签解析作文
					JSONArray jsonNegative = answer.optJSONArray("negativeList");
					JSONArray jsonPositive = answer.optJSONArray("positiveList");
					JSONArray answerAudios = answer.optJSONArray("answerAudio");
					if(answerAudios != null) {
						item.mAnswerAudios = new ArrayList<OnlineRecorderItemInfo>();
						for (int j = 0; j < answerAudios.length(); j ++) {
							JSONObject audio = answerAudios.optJSONObject(j);
							OnlineRecorderItemInfo info = new OnlineRecorderItemInfo();
							info.mOnlineUrl = audio.optString("src");
							info.mDuration = audio.optInt("duration");
							item.mAnswerAudios.add(info);
						}
					}
					// ========================
					item.mHomeworkId = mHomeworkId;
					item.mQuestionId = mQuestionId;
					if (TextUtils.isEmpty(item.answerId)) {
						item.answerId = subAnswerIdBuffer.toString();
					}
					mAnswerList.add(item);
				}
			}

		}
	}
}
