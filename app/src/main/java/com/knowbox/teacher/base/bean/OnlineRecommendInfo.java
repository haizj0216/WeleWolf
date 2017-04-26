package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 16/5/4.
 */
public class OnlineRecommendInfo extends BaseObject{

    public boolean isRecommend = false;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        isRecommend = data.optInt("recommend") == 1;
    }
}
