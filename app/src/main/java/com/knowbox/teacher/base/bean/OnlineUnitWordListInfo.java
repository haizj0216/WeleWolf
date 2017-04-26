package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/16.
 */
public class OnlineUnitWordListInfo extends BaseObject {

    public String classID;
    public String bookID;
    public String sectionID;
    public int wordsCount;
    public int wordslearnedCount;
    public int studentCount;
    public List<OnlineWordInfo> mWordInfos;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        classID = data.optString("classID");
        bookID = data.optString("bookID");
        sectionID = data.optString("sectionID");
        wordsCount = data.optInt("wordsCount");
        wordslearnedCount = data.optInt("wordsLearnedCount");
        studentCount = data.optInt("studentCount");

        if (data.has("wordList")) {
            JSONArray array = data.optJSONArray("wordList");
            if (array.length() > 0) {
                mWordInfos = new ArrayList<OnlineWordInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineWordInfo wordInfo = new OnlineWordInfo();
                    wordInfo.parseInfo(array.optJSONObject(i));
                    wordInfo.studentCount = studentCount;
                    mWordInfos.add(wordInfo);
                }
            }
        }
    }
}
