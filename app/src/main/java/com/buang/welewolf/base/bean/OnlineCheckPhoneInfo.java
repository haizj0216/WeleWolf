package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 17/6/12.
 */

public class OnlineCheckPhoneInfo extends BaseObject {
    public int status;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            status = data.optInt("status");
        }
    }
}
