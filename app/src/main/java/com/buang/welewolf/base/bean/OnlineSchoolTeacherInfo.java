package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 同校教师
 * Created by weilei on 15/7/3.
 */
public class OnlineSchoolTeacherInfo extends BaseObject {
    public List<TeacherInfo> mHighTeachers = new ArrayList<TeacherInfo>();
    public List<TeacherInfo> mMiddleTeachers = new ArrayList<TeacherInfo>();
    public List<TeacherInfo> mGradeTeachers = new ArrayList<TeacherInfo>();
    public List<TeacherInfo> mAllTeachers = new ArrayList<TeacherInfo>();
    public TeacherInfo mTeacherInfo;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONArray array = json.optJSONArray("data");
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object.has("gradePart")) {
                    String gradePart = object.optString("gradePart");
                    if ("High".equals(gradePart)) {
                        JSONArray arrayHigh = object.optJSONArray("list");
                        for (int j = 0; j < arrayHigh.length(); j++) {
                            TeacherInfo info = new TeacherInfo();
                            info.parse(arrayHigh.optJSONObject(j));
                            mHighTeachers.add(info);
                        }
                    } else if ("Middle".equals(gradePart)) {
                        JSONArray arrayMiddle = object.optJSONArray("list");
                        for (int j = 0; j < arrayMiddle.length(); j++) {
                            TeacherInfo info = new TeacherInfo();
                            info.parse(arrayMiddle.optJSONObject(j));
                            mMiddleTeachers.add(info);
                        }
                    }else if ("Grade".equals(gradePart)) {
                        JSONArray arrayGrade = object.optJSONArray("list");
                        for (int j = 0; j < arrayGrade.length(); j++) {
                            TeacherInfo info = new TeacherInfo();
                            info.parse(arrayGrade.optJSONObject(j));
                            mGradeTeachers.add(info);
                        }
                    }
                } else {
                    TeacherInfo info = new TeacherInfo();
                    info.parse(object);
                    mAllTeachers.add(info);
                }
            }
        }else {
            JSONObject data = json.optJSONObject("data");
            mTeacherInfo = new TeacherInfo();
            mTeacherInfo.parse(data);
        }

    }

    public static class TeacherInfo extends BaseObject implements Serializable {

        private static final long serialVersionUID = 5054566189324126816L;
        public String mTeacherId;
        public String mUserName;
        public String mMobile;
        public String mHeadPhoto;
        public String mSex;
        public String mGradePart;
        public String mSubject;
        public String mClassId;
        public String mClassName;
        public String mAddTime;
        public String mSchool;
        public String mGrade;

        public void parse(JSONObject json) {
            if (json == null)
                return;
            if (json.has("teacherID"))
                mTeacherId = json.optString("teacherID");
            if (json.has("toTeacherID"))
                mTeacherId = json.optString("toTeacherID");
            mUserName = json.optString("userName");
            mMobile = json.optString("mobile");
            mHeadPhoto = json.optString("headPhoto");
            mSex = json.optString("sex");
            mGradePart = json.optString("gradePart");
            mSubject = json.optString("subject");
            mClassId = json.optString("classID");
            mClassName = json.optString("className");
            mAddTime = json.optString("addTime");
            mSchool = json.optString("schoolName");
            mGrade = json.optString("grade");
        }
    }
}
