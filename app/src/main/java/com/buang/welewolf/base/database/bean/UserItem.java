/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.database.bean;

import com.buang.welewolf.base.bean.ContactInfo;
import com.buang.welewolf.base.bean.GiftInfo;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户信息
 *
 * @author yangzc
 */
public class UserItem extends BaseItem implements Serializable {

    public String userId;
    public String loginName;
    public String userName;
    public String password;
    public String token;
    public String headPhoto;
    public String sex;
    public String birthday;
    public int level;
    public int exp;
    public int coinCount;
    public String rcToken;
    public String voipToken;
    public ContactInfo.RecordInfo recordInfo;
    public int popularity;
    public List<GiftInfo> mGifts;

    public class RecordInfo {
        public int win;
        public int lost;
        public int total;
        public float rate;
    }

    public void parseData(JSONObject json) {
        userId = json.optString("userID");
        userName = json.optString("userName");
        sex = json.optString("sex");
        headPhoto = json.optString("headPhoto");
        token = json.optString("token");
        level = json.optInt("level");
        exp = json.optInt("exp");
        rcToken = json.optString("rcToken");
        voipToken = json.optString("voipToken");
    }
}
