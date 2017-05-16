package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 17/2/20.
 */
public class OnlineCompetitionWordsInfo extends BaseObject {

    public String classID;
    public String classCode;
    public int studentCount;

    public List<OnlineWordInfo> mMasterList;
    public List<OnlineWordInfo> mUnMasterList;
    public ClassInfoItem classInfoItem = new ClassInfoItem();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        classID = data.optString("classID");
        classCode = data.optString("classCode");
        studentCount = data.optInt("studentCount");
        classInfoItem.classId = classID;
        classInfoItem.classCode = classCode;

        if (data.has("unLearnedWord")) {
            JSONArray array = data.optJSONArray("unLearnedWord");
            if (array.length() > 0) {
                mUnMasterList = new ArrayList<OnlineWordInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineWordInfo wordInfo = new OnlineWordInfo();
                    JSONObject item = array.optJSONObject(i);
                    wordInfo.wordID = item.optString("wordID");
                    wordInfo.content = item.optString("content");
                    wordInfo.learnedCount = item.optInt("learnedCount");
                    wordInfo.studentCount = studentCount;
                    mUnMasterList.add(wordInfo);
                }
            }
        }

        if (data.has("learnedWord")) {
            JSONArray array = data.optJSONArray("learnedWord");
            if (array.length() > 0) {
                mMasterList = new ArrayList<OnlineWordInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineWordInfo wordInfo = new OnlineWordInfo();
                    JSONObject item = array.optJSONObject(i);
                    wordInfo.wordID = item.optString("wordID");
                    wordInfo.content = item.optString("content");
                    wordInfo.learnedCount = item.optInt("learnedCount");
                    wordInfo.studentCount = studentCount;
                    mMasterList.add(wordInfo);
                }
            }
        }




    }
}
