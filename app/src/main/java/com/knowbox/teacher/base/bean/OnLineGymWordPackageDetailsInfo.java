package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SummerRC on 16/10/21.
 * description: 词包详情对应的数据实体
 */
public class OnLineGymWordPackageDetailsInfo extends BaseObject {
    public String wpId;
    public String wpName;
    public String imgUrl;
    public int totalCount;
    public int unLockedCount;
    public int level;
    public String levelName;
    public int nextLevel;
    public String nextLevelName;
    public String controlledWordCount;
    public int upgradeWordCount;
    public int knowWellWordCount;

    public List<GymWordInfo> unLockedWordList;
    public List<GymWordInfo> lockedWordList;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (isAvailable() && json.has("data")) {
            JSONObject object = json.optJSONObject("data");
            wpId = object.optString("wpID");
            wpName = object.optString("wpName");
            imgUrl = object.optString("imgUrl");
            totalCount = object.optInt("totalCount");
            levelName = object.optString("levelName");
            level = object.optInt("level");
            nextLevel = object.optInt("nextLevel");
            nextLevelName = object.optString("nextLevelName");
            upgradeWordCount = object.optInt("upgradeWordCount");
            controlledWordCount = object.optString("controlledWordCount");

            unLockedWordList = new ArrayList<GymWordInfo>();
            lockedWordList = new ArrayList<GymWordInfo>();
            JSONArray array = object.optJSONArray("unlockedWordList");
            if (array != null && array.length() > 0) {
                unLockedCount = array.length();
                for (int i = 0; i < array.length(); i++) {
                    GymWordInfo gymWordInfo = new GymWordInfo();
                    JSONObject wordItem = array.optJSONObject(i);
                    gymWordInfo.wordID = wordItem.optInt("wordID");
                    gymWordInfo.content = wordItem.optString("wordContent");
                    gymWordInfo.learnStatus = wordItem.optInt("learnStatus");
                    gymWordInfo.maxStatus = wordItem.optInt("maxLearnStatus");
                    if (gymWordInfo.learnStatus == gymWordInfo.maxStatus) {
                        knowWellWordCount++;
                        gymWordInfo.isKnowWell = true;
                    }
                    gymWordInfo.isUnLocked = true;
                    unLockedWordList.add(gymWordInfo);
                }
            }
            int lockedCount = totalCount - unLockedCount;     //未解锁单词数量
            if (lockedCount > 0) {
                for (int i=0; i<lockedCount; i++) {
                    GymWordInfo gymWordInfo = new GymWordInfo();
                    gymWordInfo.content = "? ? ? ?";
                    gymWordInfo.isUnLocked = false;
                    lockedWordList.add(gymWordInfo);
                }
            }
        }
    }

    public static class GymWordInfo {
        public int wordID;
        public String content;
        public int learnStatus;
        public int maxStatus;
        public boolean isUnLocked;      //是否解锁
        public boolean isKnowWell;
    }
}
