package com.buang.welewolf.base.bean;


import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 15/9/25.
 */
public class OnlineExpTokenInfo extends BaseObject {

    public String mMobile;
    public String mPswd;
    public long expiredTimeStamp;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        mMobile = data.optString("mobile");
        mPswd = data.optString("password");
        expiredTimeStamp = data.optLong("expiredTimeStamp");
    }
}
