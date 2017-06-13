/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.database.bean;

import com.buang.welewolf.base.bean.GiftInfo;
import com.buang.welewolf.base.bean.OnlineGuildInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
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
    public String sign;
    public int level;
    public int exp;
    public int coinCount;
    public String rcToken;
    public String voipToken;
    public RecordInfo recordInfo;
    public int popularity;
    public boolean isFriend;
    public List<GiftInfo> mGifts;
    public OnlineGuildInfo guildIno;

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
        popularity = json.optInt("popularity");
        coinCount = json.optInt("coinCount");
        sign = json.optString("sign");
        isFriend = json.optInt("isFriend") == 1;
        if (json.has("gifts")) {
            JSONArray array = json.optJSONArray("gifts");
            mGifts = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                GiftInfo giftInfo = new GiftInfo();
                giftInfo.parse(array.optJSONObject(i));
                mGifts.add(giftInfo);
            }
        }
        if (json.has("record")) {
            JSONObject record = json.optJSONObject("record");
            recordInfo = new RecordInfo();
            recordInfo.total = record.optInt("total");
            recordInfo.win = record.optInt("win");
            recordInfo.lost = record.optInt("lost");
            recordInfo.rate = (float) record.optDouble("rate");
        }
        if (json.has("unions")) {
            guildIno = new OnlineGuildInfo();
        }
    }
}
