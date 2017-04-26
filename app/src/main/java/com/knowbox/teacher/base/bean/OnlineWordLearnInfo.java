package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.modules.utils.ConstantsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/15.
 */
public class OnlineWordLearnInfo extends BaseObject {

    public String classID;
    public String wordID;
    public int studentCount;
    public int learnStatus;
    public String translation;
    public String learnRemind;

    public List<LearnStudentList> learnStudentLists = new ArrayList<LearnStudentList>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        classID = data.optString("classID");
        wordID = data.optString("wordID");
        studentCount = data.optInt("studentCount");
        learnStatus = data.optInt("learnStatus");
        learnRemind = data.optString("learnRemind");
        translation = data.optString("translation");

        if (data.has("unLearn")) {
            List<OnlineStudentInfo> mUnLearnList = new ArrayList<OnlineStudentInfo>();
            JSONArray array = data.optJSONArray("unLearn");
            for (int i = 0; i < array.length(); i++) {
                JSONObject studentObject = array.optJSONObject(i);
                OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                studentInfo.parseInfo(studentObject);
                mUnLearnList.add(studentInfo);
            }
            if (mUnLearnList.size() > 0) {
                LearnStudentList learnStudentList = new LearnStudentList();
                learnStudentList.learnStatus = ConstantsUtils.LEARN_STATUS_UNLEARN;
                learnStudentList.studentList = mUnLearnList;
                learnStudentLists.add(learnStudentList);
            }
        }

        if (data.has("learning")) {
            List<OnlineStudentInfo> mLearningList = new ArrayList<OnlineStudentInfo>();
            JSONArray array = data.optJSONArray("learning");
            for (int i = 0; i < array.length(); i++) {
                JSONObject studentObject = array.optJSONObject(i);
                OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                studentInfo.parseInfo(studentObject);
                mLearningList.add(studentInfo);
            }
            if (mLearningList.size() > 0) {
                LearnStudentList learnStudentList = new LearnStudentList();
                learnStudentList.learnStatus = ConstantsUtils.LEARN_STATUS_LEARNING;
                learnStudentList.studentList = mLearningList;
                learnStudentLists.add(learnStudentList);
            }

        }

        if (data.has("learned")) {
            List<OnlineStudentInfo> mLearnedList = new ArrayList<OnlineStudentInfo>();
            JSONArray array = data.optJSONArray("learned");
            for (int i = 0; i < array.length(); i++) {
                JSONObject studentObject = array.optJSONObject(i);
                OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                studentInfo.parseInfo(studentObject);
                mLearnedList.add(studentInfo);
            }
            if (mLearnedList.size() > 0) {
                LearnStudentList learnStudentList = new LearnStudentList();
                learnStudentList.learnStatus = ConstantsUtils.LEARN_STATUS_LEARNED;
                learnStudentList.studentList = mLearnedList;
                learnStudentLists.add(learnStudentList);
            }
        }
    }

    public static class LearnStudentList {
        public int learnStatus;
        public List<OnlineStudentInfo> studentList = new ArrayList<OnlineStudentInfo>();
    }
}
