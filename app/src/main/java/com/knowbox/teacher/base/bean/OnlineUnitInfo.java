package com.knowbox.teacher.base.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/13.
 */
public class OnlineUnitInfo implements Serializable {
    private static final long serialVersionUID = 2008133754956360458L;
    public String mUnitName;
    public String mUnitID;
    public String mSubName;
    public int mIndex;
    public int wordsCount;
    public int learnedCount;
    public int learningCount;
    public int unLearnCount;
    public int studentsLearnedCount;
    public List<OnlineWordInfo> mWordInfos;
    public boolean isWeak;
    public String unitNum;
    public String unitAbbr;
    public int mSelectCount;

    public void parseInfo(JSONObject object) {
        mUnitID = object.optString("sectionID");
        mUnitName = object.optString("title");
        mSubName = object.optString("name");
        wordsCount = object.optInt("wordsCount");
        learnedCount = object.optInt("learnedCount");
        studentsLearnedCount = object.optInt("studentsLearnedCount");
        learningCount = object.optInt("learningCount");
        unLearnCount = object.optInt("unLearnCount");
        unitNum = object.optString("unitNum");
        unitAbbr = object.optString("unitAbbr");

        if (object.has("wordList")) {
            JSONArray array = object.optJSONArray("wordList");
            if (array.length() > 0) {
                mWordInfos = new ArrayList<OnlineWordInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineWordInfo wordInfo = new OnlineWordInfo();
                    wordInfo.parseInfo(array.optJSONObject(i));
                    mWordInfos.add(wordInfo);
                }
            }
        }
    }
}
