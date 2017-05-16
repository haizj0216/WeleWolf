/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目详情页
 * @author yangzc
 *
 */
public class OnlineQuestionDetailInfo extends BaseObject {

	public String mRightAnswer;
	public int mQuestionType;
	public String mAnswerExplain;
	public String mContentSource;
	public List<String> mKnowledgeNames = new ArrayList<String>();
	public String mSectionName;
	public String mContent;
	public String mQuestionNo;
	
	public List<QuestionAnswer> mAnswers;
	public ArrayList<OnlineRecorderItemInfo> mQuestionAudios;
	
	@Override
	public void parse(JSONObject data) {
		super.parse(data);
		
		if(isAvailable()){
			JSONObject json = data.optJSONObject("data");
			mRightAnswer = json.optString("rightAnswer");
			mQuestionType = json.optInt("questionType");
			mContent = json.optString("content");
			mQuestionNo = json.optString("questionNo");
			this.mAnswerExplain = json.optString("answerExplain");
			this.mContentSource = json.optString("contentSource");
			JSONArray knowledge = json.optJSONArray("knowList");
			if(knowledge != null){
				mKnowledgeNames.clear();
				for (int i = 0; i < knowledge.length(); i++) {
					mKnowledgeNames.add(knowledge.optString(i));
				}
			}else {
				knowledge = json.optJSONArray("knowledgeName");
				if(knowledge != null){
					mKnowledgeNames.clear();
					for (int i = 0; i < knowledge.length(); i++) {
						mKnowledgeNames.add(knowledge.optString(i));
					}
				}
			}

			this.mSectionName = json.optString("sectionName");
			
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
			
			JSONArray items = json.optJSONArray("list");
			if (items == null) {
				items = json.optJSONArray("itemList");
			}
			mAnswers = new ArrayList<QuestionAnswer>();
			if(items != null && items.length() > 0){
				for(int i=0; i< items.length(); i++){
					JSONObject answerItem = items.optJSONObject(i);
					if(answerItem != null){
						QuestionAnswer answer = new QuestionAnswer();
						answer.mCode = answerItem.optString("itemCode");
						answer.mValue = answerItem.optString("questionItem");
						answer.mRate = (float) answerItem.optDouble("rightRate", 0.0);
						List<String> names = new ArrayList<String>();
						JSONArray studentArray = answerItem.optJSONArray("studentList");
						if(studentArray != null){
							for(int j=0; j< studentArray.length(); j++){
								String studentName = studentArray.optString(j);
								names.add(studentName);
							}
						}
						answer.mStudents = names;
						mAnswers.add(answer);
					}
				}
			}
		}
		
	}
	
	public static class QuestionAnswer {
		public String mCode;
		public String mValue;
		public float mRate;
		public List<String> mStudents;
	}
}
