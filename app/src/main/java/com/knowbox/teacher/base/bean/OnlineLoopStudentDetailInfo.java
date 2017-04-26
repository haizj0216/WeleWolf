package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/8.
 */
public class OnlineLoopStudentDetailInfo extends BaseObject {

    public int questionCount;
    public int rightRate;
    public int[] questionTypes;
    public List<OnlineWordInfo> mWords;
    public long startTime;
    public long endTime;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            questionCount = data.optInt("questionCount");
            rightRate = (int) (data.optDouble("rightRate") * 100);
            startTime = data.optLong("startTime");
            endTime = data.optLong("stopTime");
            if (data.has("type")) {
                JSONArray array = data.optJSONArray("type");
                questionTypes = new int[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    questionTypes[i] = array.optInt(i);
                }
            }
            if (data.has("wordList")) {
                JSONArray array = data.optJSONArray("wordList");
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
