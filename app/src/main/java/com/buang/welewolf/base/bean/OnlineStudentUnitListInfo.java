package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/16.
 */
public class OnlineStudentUnitListInfo extends BaseObject {

    public String classID;
    public String studentID;
    public String studentName;
    public String headPhoto;
    public int learnedCount;
    public int learningCount;
    public int weekLearned;
    public int weekLearning;
    public int lastDayLearned;
    public int lastDayLearning;

    public List<OnlineUnitInfo> mUnitInfos;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);

        JSONObject data = json.optJSONObject("data");
        classID = data.optString("classID");
        studentID = data.optString("studentID");
        studentName = data.optString("studentName");
        headPhoto = data.optString("headPhoto");
        learnedCount = data.optInt("learnedCount");
        learningCount = data.optInt("learningCount");
        weekLearned = data.optInt("weekLearned");
        weekLearning = data.optInt("weekLearning");
        lastDayLearned = data.optInt("lastDayLearned");
        lastDayLearning = data.optInt("lastDayLearning");
        if (data.has("unitList")) {
            JSONArray array = data.optJSONArray("unitList");
            if (array.length() > 0) {
                mUnitInfos = new ArrayList<OnlineUnitInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineUnitInfo unitInfo = new OnlineUnitInfo();
                    unitInfo.parseInfo(array.optJSONObject(i));
                    mUnitInfos.add(unitInfo);
                }
            }
        }

    }
}
