package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 17/6/7.
 */

public class OnlineUploadInfo extends BaseObject {
    public String mToken;
    public long mExpiredTime;
    public String mDomain;

    public OnlineUploadInfo() {
    }

    public void parse(JSONObject json) {
        super.parse(json);
        if(json.has("data")) {
            json = json.optJSONObject("data");
        }

        this.mToken = json.optString("token");
        this.mExpiredTime = json.optLong("expiredTimeStamp");
        this.mDomain = json.optString("domainName");
    }
}