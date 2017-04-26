package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.base.database.bean.QuestionItem;

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
