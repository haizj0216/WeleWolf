package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by weilei on 17/6/6.
 */

public class OnlineGuildListInfo extends BaseObject {

    public List<OnlineGuildInfo> mGuilds;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
    }
}
