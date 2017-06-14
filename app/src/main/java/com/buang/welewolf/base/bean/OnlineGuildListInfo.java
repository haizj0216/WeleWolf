package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/6.
 */

public class OnlineGuildListInfo extends BaseObject {

    public List<OnlineGuildInfo> mGuilds;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONArray array = json.optJSONArray("data");
        if (array != null) {
            mGuilds = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                OnlineGuildInfo guildInfo = new OnlineGuildInfo();
                guildInfo.parse(array.optJSONObject(i));
                mGuilds.add(guildInfo);
            }
        }
    }
}
