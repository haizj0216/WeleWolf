package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/19.
 */

public class OnlineRoomInfo extends BaseObject implements Serializable {
    public String roomID;
    public int roomType;
    public int levelLimit;
    public boolean allowSpects;
    public boolean isAnonymous;

    public List<OnlineRoleInfo> roleInfos;
    public OnlineRoleInfo myRole;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            roomID = data.optString("roomID");
            roomType = data.optInt("roomType");
            levelLimit = data.optInt("levelLimit");
            allowSpects = data.optInt("allowSpects") == 1;
            isAnonymous = data.optInt("isAnonymous") == 1;
            JSONArray array = data.optJSONArray("userList");
            if (array != null) {
                roleInfos = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    OnlineRoleInfo roleInfo = new OnlineRoleInfo();
                    roleInfo.parseInfo(array.optJSONObject(i));
                    if (roleInfo.isMySelf) {
                        myRole = roleInfo;
                    }
                    roleInfos.add(roleInfo);
                }
            }
        }
    }
}
