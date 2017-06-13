package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.UserItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 17/6/12.
 */

public class OnlineUserInfo extends BaseObject {

    public UserItem mUserItem;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject jsonObject = json.optJSONObject("data");
        if (jsonObject != null) {
            mUserItem = new UserItem();
            mUserItem.parseData(jsonObject);
        }
    }
}
