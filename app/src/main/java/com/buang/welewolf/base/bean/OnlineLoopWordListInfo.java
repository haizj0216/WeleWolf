package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/8.
 */
public class OnlineLoopWordListInfo extends BaseObject {

    private int type;
    private long date;
    public List<OnlineWordInfo> mWordInfos;
    public List<OnlineWordInfo> mTestWordInfos;
    public int lowRateCount;
    public int highRateCount;
    public int correctCount;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            type = data.optInt("type");
            date = data.optLong("date");
            if (data.has("wordList")) {
                JSONArray array = data.optJSONArray("wordList");
                mWordInfos = new ArrayList<OnlineWordInfo>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineWordInfo wordInfo = new OnlineWordInfo();
                    wordInfo.parseInfo(array.optJSONObject(i));
                    mWordInfos.add(wordInfo);
                }
            }

            if (data.has("highRateErrorList")) {
                JSONArray array = data.optJSONArray("highRateErrorList");
                if (null != array && array.length() > 0) {
                    highRateCount = array.length();
                    mTestWordInfos = new ArrayList<OnlineWordInfo>();
                    for (int i = 0; i < array.length(); i++) {
                        OnlineWordInfo wordInfo = new OnlineWordInfo();
                        wordInfo.parseInfo(array.optJSONObject(i));
                        if (i == 0) {
                            wordInfo.isHighRate = true;
                        }
                        mTestWordInfos.add(wordInfo);
                    }
                }
            }

            if (data.has("lowRateErrorList")) {
                JSONArray array = data.optJSONArray("lowRateErrorList");
                if (null != array && array.length() > 0) {
                    lowRateCount = array.length();
                    if (null == mTestWordInfos) {
                        mTestWordInfos = new ArrayList<OnlineWordInfo>();
                    }
                    for (int i = 0; i < array.length(); i++) {
                        OnlineWordInfo wordInfo = new OnlineWordInfo();
                        wordInfo.parseInfo(array.optJSONObject(i));
                        if (i == 0) {
                            wordInfo.isLowRate = true;
                        }
                        mTestWordInfos.add(wordInfo);
                    }
                }
            }

            if (data.has("correctList")) {
                JSONArray array = data.optJSONArray("correctList");
                if (null != array && array.length() > 0) {
                    correctCount = array.length();
                    if (null == mTestWordInfos) {
                        mTestWordInfos = new ArrayList<OnlineWordInfo>();
                    }
                    for (int i = 0; i < array.length(); i++) {
                        OnlineWordInfo wordInfo = new OnlineWordInfo();
                        wordInfo.parseInfo(array.optJSONObject(i));
                        if (i == 0) {
                            wordInfo.isCorrect = true;
                        }
                        mTestWordInfos.add(wordInfo);
                    }
                }
            }
        }
    }
}
