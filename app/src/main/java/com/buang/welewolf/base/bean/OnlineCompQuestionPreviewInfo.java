package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 17/2/18.
 */
public class OnlineCompQuestionPreviewInfo extends BaseObject {


    public List<QuestionTypeItem> questionTypeItems;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONArray data = json.optJSONArray("data");
        if (data.length() > 0) {
            questionTypeItems = new ArrayList<QuestionTypeItem>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.optJSONObject(i);
                QuestionTypeItem typeItem = new QuestionTypeItem();
                typeItem.parseData(object);
                questionTypeItems.add(typeItem);
            }
        }
    }

    public static class QuestionTypeItem implements Serializable{
        public String typeName;
        public int type;
        public String count;
        public String image;

        public void parseData(JSONObject object) {
            count = object.optString("count");
            type = object.optInt("typeId");
            typeName = object.optString("typeName");
            image = object.optString("image");
        }
    }

}
