package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by weilei on 17/2/14.
 */
public class OnlineWordInfo implements Serializable {

    private static final long serialVersionUID = 1278006257477714692L;
    public String wordID;
    public String content;
    public String wordExplain;
    public int studentCount;
    public int learnedCount;
    public int learnStatus;
    public int wrongCount;
    public int[] wrongCountArray;
    public int totalWrongCount;
    public int errorStudentCount;
    public boolean isHighRate;
    public boolean isLowRate;
    public boolean isCorrect;
    public boolean isRight;//0错误; 1正确;
    public int knowledgeDim;//
    public int isRevise;//0 未订正;  1 已订正;

    public void parseInfo(JSONObject object) {
        wordID = object.optString("wordID");
        if (TextUtils.isEmpty(wordID)) {
            wordID = object.optString("wordId");
        }
        content = object.optString("content");
        if (TextUtils.isEmpty(content)) {
            content = object.optString("wordContent");
        }
        isRight = object.optInt("isRight") == 1;
        knowledgeDim = object.optInt("knowledgeDim");
        isRevise = object.optInt("isRevise");
        learnedCount = object.optInt("learnedCount");
        errorStudentCount = object.optInt("errorStudentCount");
        if (object.has("wrongCount")) {
            JSONArray array = object.optJSONArray("wrongCount");
            if (array == null) {
                wrongCount = object.optInt("wrongCount");
            } else {
                wrongCountArray = new int[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    wrongCountArray[i] = array.optInt(i);
                    totalWrongCount += array.optInt(i);
                }
            }
        }
    }

    public boolean isLearned() {
        return studentCount == learnedCount && studentCount != 0;
    }
}
