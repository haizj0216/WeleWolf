package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.UserItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by weilei on 17/6/5.
 */

public class OnlineMemberListInfo extends BaseObject {

    public List<UserItem> mMembers;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
    }
}
