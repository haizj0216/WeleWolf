/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineUploadInfo extends BaseObject {

	public String mErrorMsg;
	public List<AnswerResult> mResultItems;
	
	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		if(isAvailable()){
			mResultItems = new ArrayList<OnlineUploadInfo.AnswerResult>();
			JSONArray answerList = json.optJSONArray("answerList");
			if(answerList != null){
				for (int i = 0; i < answerList.length(); i++) {
					JSONObject answerItem = answerList.optJSONObject(i);
					if(answerItem != null){
						AnswerResult result = new AnswerResult();
						result.mAnswerId = answerItem.optString("answerID");
						result.mScore = answerItem.optString("correctScore");
						
						JSONArray correctPicJsonArray = answerItem.optJSONArray("correctAnswers");
						if(correctPicJsonArray != null){
							StringBuffer buffer = new StringBuffer();
							for (int j = 0; j < correctPicJsonArray.length(); j++) {
								if(j == 0){
									buffer.append(correctPicJsonArray.optString(j));
								}else{
									buffer.append("," + correctPicJsonArray.optString(j));
								}
							}
							result.mCorrectAnswers = buffer.toString();
						}
						mResultItems.add(result);
					}
				}
			}
		}else{
			this.mErrorMsg = json.optString("message");
		}
	}
	
	public static class AnswerResult {
		public String mAnswerId;
		public String mScore;
		public String mCorrectAnswers;
	}
}
