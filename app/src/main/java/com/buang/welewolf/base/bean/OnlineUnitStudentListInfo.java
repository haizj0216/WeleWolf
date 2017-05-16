package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/16.
 */
public class OnlineUnitStudentListInfo extends BaseObject {
    public String classID;
    public String bookID;
    public String sectionID;
    public int wordsCount;
    public int wordslearnedCount;
    public int studentCount;
    public int studentLearnedCount;
    public List<OnlineStudentInfo> mStudentInfos;

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
        studentLearnedCount = data.optInt("studentLearnedCount");

        if (data.has("studentList")) {
            JSONArray array = data.optJSONArray("studentList");
            if (array.length() > 0) {
                mStudentInfos = new ArrayList<OnlineStudentInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                    studentInfo.parseInfo(array.optJSONObject(i));
                    mStudentInfos.add(studentInfo);
                }
            }
        }
    }
}
