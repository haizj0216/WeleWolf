package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 17/4/14.
 */
public class OnlineTestStudentDetailInfo extends BaseObject {

    public List<OnlineWordInfo> mWords;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {

            if (data.has("answerList")) {
                JSONArray array = data.optJSONArray("answerList");
                if (array != null && array.length() > 0) {
                    mWords = new ArrayList<OnlineWordInfo>();
                    for (int i = 0; i < array.length(); i++) {
                        OnlineWordInfo wordInfo = new OnlineWordInfo();
                        wordInfo.parseInfo(array.optJSONObject(i));
                        mWords.add(wordInfo);
                    }
                }
            }
        }
    }
}
