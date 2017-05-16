package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.database.bean.QuestionItem;
import com.buang.welewolf.modules.utils.SubjectUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineStudentHomeListInfo extends BaseObject {

	public String mQuestionTypes;
	public String mHomeworkId;
	public int mCurrentPageNum;
	public int mTotalPageNum;
	public int mTotalCount;
	public int mSubmitNum;
	public List<QuestionItem> mQuestions;
	public String mSectionID;

	public OnlineStudentHomeListInfo(String homeworkId, String sectionId) {
		this.mHomeworkId = homeworkId;
		this.mSectionID = sectionId;
	}

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		if (isAvailable()) {
			if (json.has("data")) {
				JSONObject data = json.optJSONObject("data");
				JSONArray questionList = data.optJSONArray("questionList");
				if (questionList != null) {
					if (mQuestions != null)
						mQuestions.clear();
					mTotalCount = 0;
					for (int i = 0; i < questionList.length(); i++) {
						JSONObject question = questionList.optJSONObject(i);
						if (question != null) {
							if (mQuestions == null)
								mQuestions = new ArrayList<QuestionItem>();
							QuestionItem item = new QuestionItem();
							item = item.getQuestionItem(question);
							if (item != null)
								mQuestions.add(item);
							if (SubjectUtils.isMultiQuestionType(item.mQuestionType)) {
								if (item.mSubQuestions != null)
									mTotalCount += item.mSubQuestions.size();
							} else {
								mTotalCount ++;
							}
						}
					}
				}
				return;
			}

		}
	}
}
