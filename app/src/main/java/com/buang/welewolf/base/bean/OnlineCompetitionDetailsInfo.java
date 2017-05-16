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
public class OnlineCompetitionDetailsInfo extends BaseObject implements Serializable {

    private static final long serialVersionUID = -2339660908997576605L;
    public String name;
    public String matchID;

    public String wordCount;
    public String wordLearnedCount;


    public List<OnlineStudentInfo> mJoinList;
    public List<OnlineStudentInfo> mUnJoinList;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data.has("detail")) {
            JSONObject detail = data.optJSONObject("detail");
            name = detail.optString("name");
            matchID = detail.optString("matchID");
            wordCount = detail.optString("wordCount");
            wordLearnedCount = detail.optString("wordLearnedCount");
        }

        if (data.has("joinList")) {
            JSONArray array = data.optJSONArray("joinList");
            if (array.length() > 0) {
                mJoinList = new ArrayList<OnlineStudentInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                    JSONObject item = array.optJSONObject(i);
                    studentInfo.mStudentId = item.optString("studentID");
                    studentInfo.mStudentName = item.optString("studentName");
                    studentInfo.learnedCount = item.optInt("learnedCount");
                    studentInfo.mHeadPhoto = item.optString("headPhoto");
                    studentInfo.score = item.optString("score");
                    mJoinList.add(studentInfo);
                }
            }
        }

        if (data.has("unJoinList")) {
            JSONArray array = data.optJSONArray("unJoinList");
            if (array.length() > 0) {
                mUnJoinList = new ArrayList<OnlineStudentInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                    JSONObject item = array.optJSONObject(i);
                    studentInfo.mStudentId = item.optString("studentID");
                    studentInfo.mStudentName = item.optString("studentName");
                    studentInfo.mHeadPhoto = item.optString("headPhoto");
                    mUnJoinList.add(studentInfo);
                }
            }
        }
    }
}
