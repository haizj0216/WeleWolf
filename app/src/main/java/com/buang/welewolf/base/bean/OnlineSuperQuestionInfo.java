/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.QuestionItem;
import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.modules.utils.ConstantsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 超级账号搜题题目
 * 
 * @author LiuYu
 *
 */
public class OnlineSuperQuestionInfo extends BaseObject {

	public List<QuestionItem> mQuestions;
	public int mQuestionSourceType;
	public String mSourceId;
	public String mErrorMassage;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		if (isAvailable()) {
			if(json.has("data")){
				JSONArray questionList = json.optJSONArray("data");
				if (questionList != null) {
					if(mQuestions != null)
						mQuestions.clear();
					for (int i = 0; i < questionList.length(); i++) {
						JSONObject question = questionList.optJSONObject(i);
						if (question != null) {
							if (mQuestions == null)
								mQuestions = new ArrayList<QuestionItem>();
							QuestionItem item = new QuestionItem();
							item = item.getQuestionItem(question);
							item.mQuestionSourceType = mQuestionSourceType;
							item.mSourceId = mSourceId;
							item.mFromType = ConstantsUtils.FROM_TYPE_BASIC;
							if (item != null)
								mQuestions.add(item);
						}
					}
				}
			}
		}else{
			String result = json.optString("code");
			if (result.equals("20006")) {
				mErrorMassage = json.optString("data");
			}else{
				
			}
		}
	}
}
