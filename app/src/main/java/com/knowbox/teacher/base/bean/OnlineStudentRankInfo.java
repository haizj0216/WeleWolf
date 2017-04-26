/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生自主练习排名
 */
public class OnlineStudentRankInfo extends BaseObject {

    public List<OnlineStudentInfo> mRankListInfos;
    public String classID;
    public int studentCount;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (json.has("data")) {
            JSONObject data = json.optJSONObject("data");
            classID = data.optString("classID");
            studentCount = data.optInt("studentCount");
            if (data.has("studentList")) {
                JSONArray array = data.optJSONArray("studentList");
                if (array.length() > 0) {
                    mRankListInfos = new ArrayList<OnlineStudentInfo>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                        studentInfo.parseInfo(object);
                        mRankListInfos.add(studentInfo);
                    }
                }
            }
        }
    }
}
