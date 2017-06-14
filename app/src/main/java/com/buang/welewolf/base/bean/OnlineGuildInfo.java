package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.UserItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/6.
 */

public class OnlineGuildInfo extends BaseObject implements Serializable {

    public String mHeadPhoto;
    public int level;
    public int maxCount;
    public int curCount;
    public String guildID;
    public String guildName;
    public String sign;
    public int job;
    public long createTime;
    public List<UserItem> members;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data == null) {
            data = json;
        }
        if (data != null) {
            guildID = data.optString("guildID");
            guildName = data.optString("guildName");
            mHeadPhoto = data.optString("headPhoto");
            level = data.optInt("level");
            sign = data.optString("sign");
            maxCount = data.optInt("maxCount");
            curCount = data.optInt("curCount");
            createTime = data.optLong("createTime");
            job = data.optInt("job");
            if (data.has("members")) {
                JSONArray array = data.optJSONArray("members");
                members = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    UserItem userItem = new UserItem();
                    userItem.parseData(array.optJSONObject(i));
                    members.add(userItem);
                }
            }
        }
    }
}
