package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/17.
 */
public class OnlineAssignUnitInfo extends BaseObject {

    public OnlineUnitInfo mWeekUnit;
    public List<OnlineUnitInfo> mUnitInfos;
    public List<ClassInfoItem> mClassInfos;
    public List<OnlineCompQuestionPreviewInfo.QuestionTypeItem> mTypeItems;

    public int minMatchDays;
    public int startClock;
    public int endClock;
    public int minWordCount;
    public int maxWordCount;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        minMatchDays = data.optInt("minMatchDays");
        startClock = data.optInt("startClock");
        endClock = data.optInt("endClock");
        minWordCount = data.optInt("minWordCount");
        maxWordCount = data.optInt("maxWordCount");
        if (data.has("weakWordsList")) {
            mWeekUnit = new OnlineUnitInfo();
            JSONArray array = data.optJSONArray("weakWordsList");
            mWeekUnit.mWordInfos = new ArrayList<OnlineWordInfo>();
            for (int i = 0; i < array.length(); i++) {
                OnlineWordInfo wordInfo = new OnlineWordInfo();
                wordInfo.parseInfo(array.optJSONObject(i));
                mWeekUnit.mWordInfos.add(wordInfo);
            }
            mWeekUnit.mUnitName = "薄弱单词";
            mWeekUnit.isWeak = true;
        }
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
        if (data.has("classList")) {
            JSONArray array = data.optJSONArray("classList");
            if (array.length() > 0) {
                mClassInfos = new ArrayList<ClassInfoItem>();
                for (int i = 0; i < array.length(); i++) {
                    ClassInfoItem classInfo = new ClassInfoItem();
                    classInfo.parseItem(classInfo, array.optJSONObject(i));
                    mClassInfos.add(classInfo);
                }
            }
        }
        if (data.has("typeList")) {
            JSONArray array = data.optJSONArray("typeList");
            if (array.length() > 0) {
                mTypeItems = new ArrayList<OnlineCompQuestionPreviewInfo.QuestionTypeItem>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineCompQuestionPreviewInfo.QuestionTypeItem classInfo = new OnlineCompQuestionPreviewInfo.QuestionTypeItem();
                    classInfo.parseData(array.optJSONObject(i));
                    mTypeItems.add(classInfo);
                }
            }
        }
    }
}
