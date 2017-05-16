package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/7.
 */
public class OnlineReportStudentListInfo extends BaseObject implements Serializable {

    private static final long serialVersionUID = -9076157455280026575L;
    public String classId;
    public String className;
    public String classImage;
    public String classCode;
    public int averAnswerCount;
    public int averControlRate;
    public int averRightRate;
    public int type;
    public long date;

    public List<OnlineStudentInfo> mStudyStudentList;
    public List<OnlineStudentInfo> mUnStudyStudentList;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            classId = data.optString("classID");
            className = data.optString("className");
            classImage = data.optString("classImage");
            classCode = data.optString("classCode");
            averAnswerCount = data.optInt("averAnswerCount");
            averControlRate = data.optInt("averControlRate");
            averRightRate = (int) (data.optDouble("averRightRate") * 100);
            type = data.optInt("type");
            date = data.optLong("date");
            if (data.has("studentList")) {
                mStudyStudentList = new ArrayList<OnlineStudentInfo>();
                mUnStudyStudentList = new ArrayList<OnlineStudentInfo>();
                JSONArray array = data.optJSONArray("studentList");
                for (int i = 0; i < array.length(); i++) {
                    OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                    studentInfo.parseInfo(array.optJSONObject(i));
                    studentInfo.rankIndex = i + 1;
                    if (studentInfo.answerCount < 0) {
                        mUnStudyStudentList.add(studentInfo);
                    } else {
                        mStudyStudentList.add(studentInfo);
                    }

                }
            }
        }
    }

    public boolean isLastRank() {
        return type == 1;
    }

    public boolean isUnBegining() {
        return type == -1;
    }
}
