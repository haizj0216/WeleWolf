package com.buang.welewolf.base.bean;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by weilei on 17/2/15.
 */
public class OnlineStudentInfo implements Serializable {
    private static final long serialVersionUID = 6888026106134815955L;
    public String mStudentId;
    public String mStudentName;
    public String mHeadPhoto;
    public int learnedCount;
    public int learningCount;
    public int weekLearned;
    public int weekLearning;
    public int unLearnCount;
    public boolean isGrant;
    public String score;
    public boolean isFooter;
    public boolean isUnstudy;
    public int answerCount;
    public int rightRate;
    public int controlRate;
    public int rankIndex;
    public int isRevise;// 0 未订正;1已订正;2 无订正
    public int errorCount;//待订正数量
    public long timeUsed;
    public boolean isShowTitle;
    public List<OnlineStudentInfo> unStudyList;

    public void parseInfo(JSONObject jsonObject) {
        mStudentId = jsonObject.optString("studentID");
        mHeadPhoto = jsonObject.optString("headPhoto");
        learnedCount = jsonObject.optInt("learnedCount");
        learningCount = jsonObject.optInt("learningCount");
        weekLearned = jsonObject.optInt("weekLearned");
        weekLearning = jsonObject.optInt("weekLearning");
        unLearnCount = jsonObject.optInt("unLearnCount");
        isGrant = jsonObject.optInt("isGrant") == 1;
        answerCount = jsonObject.optInt("answerCount");
        rightRate = (int) (jsonObject.optDouble("rightRate") * 100);
        controlRate = jsonObject.optInt("controlRate");
        mStudentName = jsonObject.optString("studentName");
        score = jsonObject.optString("score");
        isRevise = jsonObject.optInt("isRevise");
        errorCount = jsonObject.optInt("errorCount");
        timeUsed = jsonObject.optLong("timeUsed");

    }
}
