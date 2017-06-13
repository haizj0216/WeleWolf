package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.UserItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/12.
 */

public class OnlineFriendListInfo extends BaseObject {

    public List<UserItem> userItems;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONArray array = json.optJSONArray("data");
        if (array != null) {
            userItems = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                UserItem userItem = new UserItem();
                userItem.parseData(array.optJSONObject(i));
                userItems.add(userItem);
            }
        }
    }
}
