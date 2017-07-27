package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.UserItem;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by weilei on 17/6/19.
 */

public class OnlineRoleInfo implements Serializable {

    public String userName;
    public String userID;
    public String userPhoto;
    public int level;
    public String sex;
    public boolean isOwner;
    public int userIndex;
    public int roleType;
    public boolean isAnonymous;
    public boolean isLock;
    public boolean isEmpty;
    public boolean isReady;
    public boolean isRaise;
    public boolean isDeath;
    public boolean isMyVoice;
    public boolean isMySelf;
    public boolean isVillage;
    public String killUserID;
    public UserItem.RecordInfo recordInfo;

    public void parseInfo(JSONObject object) {
        JSONObject player = object.optJSONObject("player");
        if (player != null) {
            userName = player.optString("userName");
            userID = player.optString("userID");
            userPhoto = player.optString("userPhoto");
            isOwner = player.optInt("isOwner") == 1;
            userIndex = player.optInt("userIndex");
            roleType = player.optInt("roleType");
            level = player.optInt("level");
            sex = player.optString("sex");
        }
        isAnonymous = object.optInt("isAnonymous") == 1;
        isLock = object.optInt("isLock") == 1;
        isEmpty = object.optInt("isEmpty") == 1;
        isReady = object.optInt("isReady") == 1;
        isRaise = object.optInt("isRaise") == 1;
        isDeath = object.optInt("isDeath") == 1;
        isMyVoice = object.optInt("isMyVoice") == 1;
        isMySelf = object.optInt("isMySelf") == 1;
        isVillage = object.optInt("isVillage") == 1;

        if (object.has("record")) {
            JSONObject record = object.optJSONObject("record");
            recordInfo = new UserItem.RecordInfo();
            recordInfo.total = record.optInt("total");
            recordInfo.win = record.optInt("win");
            recordInfo.lost = record.optInt("lost");
            recordInfo.rate = (float) record.optDouble("rate");
        }
    }
}
