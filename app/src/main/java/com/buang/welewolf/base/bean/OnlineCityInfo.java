package com.buang.welewolf.base.bean;


import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 15/7/27.
 */
public class OnlineCityInfo extends BaseObject {

    public int mCityVersion;
    public String mCityUrl;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        mCityVersion = data.optInt("cityVersion");
        mCityUrl = data.optString("cityDataUrl");
    }
}
