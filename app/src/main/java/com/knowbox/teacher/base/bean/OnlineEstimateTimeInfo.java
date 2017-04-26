package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 17/4/12.
 */
public class OnlineEstimateTimeInfo extends BaseObject {

    public int roughTime;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            roughTime = data.optInt("roughTime");
        }
    }
}
