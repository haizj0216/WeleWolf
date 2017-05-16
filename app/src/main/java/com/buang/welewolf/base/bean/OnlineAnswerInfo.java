package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.QuestionItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 16/1/21.
 */
public class OnlineAnswerInfo extends BaseObject{
    public QuestionItem mQuestionItem;
    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        mQuestionItem = new QuestionItem();
        mQuestionItem = mQuestionItem.getQuestionItem(data);
    }
}
