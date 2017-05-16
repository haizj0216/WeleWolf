package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.database.bean.QuestionItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 拍照出题
 * 
 * @author weilei
 * 
 */
public class OnlinePhotoQuestionInfo extends BaseObject {

	public QuestionItem mQuestionItem;

	public void parse(JSONObject json) {
		super.parse(json);
		if (isAvailable()) {
			mQuestionItem = new QuestionItem();
			JSONObject question = json.optJSONObject("data");
			mQuestionItem.mQuestionId = question.optString("questionID");
			mQuestionItem.mQuestionType = question.optInt("questionType");
			mQuestionItem.mContent = question.optString("content");
			if (TextUtils.isEmpty(mQuestionItem.mContent)) {
				mQuestionItem.mContent = question.optString("question");
			}
			mQuestionItem.mQuestionIndex = question.optString("questionNo");
			mQuestionItem.mAnswerExplain = question.optString("answerExplain");
			mQuestionItem.mRightAnswer = question.optString("rightAnswer");
			mQuestionItem.mDifficulty = question.optInt("difficulty");
			mQuestionItem.mHot = question.optLong("hot");
			mQuestionItem.mKnowledgeName = new ArrayList<String>();
			JSONArray arrays;
			try {
				arrays = question.optJSONArray("knowList");
				if (arrays != null) {
					for (int i = 0; i < arrays.length(); i++) {
						mQuestionItem.mKnowledgeName.add(arrays.optString(i));
					}
				}else {
					arrays = question.optJSONArray("knowledgeName");
					if (arrays != null) {
						for (int i = 0; i < arrays.length(); i++) {
							mQuestionItem.mKnowledgeName.add(arrays.optString(i));
						}
					}
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			mQuestionItem.mSectionName = question.optString("sectionName");
			try {
				mQuestionItem.mRightRate = (float) question.optDouble("rightRate", 0.0);
			} catch (Exception e) {
			}
			try {
				mQuestionItem.mCorrectedNum = question.optInt("correctedNum");
			} catch (Exception e) {
			}
			mQuestionItem.mIsOut = question.optString("isOut").equals("1");
			mQuestionItem.mIsCollect = question.optString("isCollect").equals(
					"1");
			if (question.has("groupInfo")) {
				JSONArray groups = question.optJSONArray("groupInfo");
				if (groups != null && groups.length() > 0) {
					JSONObject group = groups.optJSONObject(0);
					mQuestionItem.mGroupID = group.optString("groupId");
					mQuestionItem.mGroupName = group.optString("name");
				}

			}
		}
	};
}
