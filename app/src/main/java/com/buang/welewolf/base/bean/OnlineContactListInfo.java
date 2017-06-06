package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by weilei on 17/6/1.
 */

public class OnlineContactListInfo extends BaseObject {

    public List<ContactInfo> mContacts;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
    }
}
