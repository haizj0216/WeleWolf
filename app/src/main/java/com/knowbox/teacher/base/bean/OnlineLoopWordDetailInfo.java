package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.base.database.bean.QuestionItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/8.
 */
public class OnlineLoopWordDetailInfo extends BaseObject {

    public String wordID;
    public String content;
    public String translation;
    public List<QuestionDimInfo> questionDimInfos;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            wordID = data.optString("wordID");
            content = data.optString("content");
            translation = data.optString("translation");

            if (data.has("dimList")) {
                JSONArray dimList = data.optJSONArray("dimList");
                questionDimInfos = new ArrayList<QuestionDimInfo>();
                for (int i = 0; i < dimList.length(); i++) {
                    QuestionDimInfo dimInfo = new QuestionDimInfo();
                    JSONObject dimObject = dimList.optJSONObject(i);
                    dimInfo.dimID = dimObject.optInt("dimID");
                    dimInfo.dimTitle = dimObject.optString("dimTitle");
                    dimInfo.totalWrongCount = dimObject.optInt("totalWrongCount");
                    if (dimObject.has("questionList")) {
                        dimInfo.questionItems = new ArrayList<QuestionItem>();
                        JSONArray questionList = dimObject.optJSONArray("questionList");
                        for (int j = 0; j < questionList.length(); j++) {
                            QuestionItem questionItem = new QuestionItem();
                            questionItem = questionItem.getQuestionItem(questionList.optJSONObject(j));
                            dimInfo.questionItems.add(questionItem);
                        }
                    }
                    questionDimInfos.add(dimInfo);
                }
            }
        }
    }

    public static class QuestionDimInfo {
        public int dimID;
        public String dimTitle;
        public int totalWrongCount;
        public List<QuestionItem> questionItems;
    }
}
