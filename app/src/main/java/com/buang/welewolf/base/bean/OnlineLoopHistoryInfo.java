package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/10.
 */
public class OnlineLoopHistoryInfo extends BaseObject {

    public List<OnlineReportInfo> mReports;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            if (data.has("reportlist")) {
                mReports = new ArrayList<OnlineReportInfo>();
                JSONArray array = data.optJSONArray("reportlist");
                for (int i = 0; i < array.length(); i++) {
                    OnlineReportInfo reportInfo = new OnlineReportInfo();
                    reportInfo.parseData(array.optJSONObject(i));
                    mReports.add(reportInfo);
                }
            }
        }
    }

    public static class OnlineReportInfo implements Serializable {
        public String reportName;
        public long date;
        public int averAnswerCount;
        public int averRightRate;
        public int averControlRate;
        public int type;
        public boolean hasMore;
        public List<OnlineStudentInfo> mAnswerStudents;
        public List<OnlineStudentInfo> mUnAnswerStudents;
        public boolean isEmpty;

        public void parseData(JSONObject object) {
            reportName = object.optString("reportName");
            date = object.optLong("date");
            averAnswerCount = object.optInt("averAnswerCount");
            averRightRate = (int) (object.optDouble("averRightRate") * 100);
            averControlRate = object.optInt("averControlRate");
            type = object.optInt("type");
            hasMore = object.optInt("hasMore") == 1;
            if (object.has("studentList")) {
                JSONArray array = object.optJSONArray("studentList");
                for (int i = 0; i < array.length(); i++) {
                    OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                    studentInfo.parseInfo(array.optJSONObject(i));
                    if (studentInfo.answerCount < 0) {
                        if (mUnAnswerStudents == null) {
                            mUnAnswerStudents = new ArrayList<OnlineStudentInfo>();
                        }
                        mUnAnswerStudents.add(studentInfo);
                    } else {
                        if (mAnswerStudents == null) {
                            mAnswerStudents = new ArrayList<OnlineStudentInfo>();
                        }
                        mAnswerStudents.add(studentInfo);
                    }
                }
            }
        }
    }
}
