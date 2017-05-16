package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/15.
 */
public class OnlineUnitListInfo extends BaseObject {

    public String classID;
    public List<OnlineUnitInfo> mUnitList = new ArrayList<OnlineUnitInfo>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data.has("unitList")) {
            JSONArray array = data.optJSONArray("unitList");
            for (int i = 0; i < array.length(); i++) {
                OnlineUnitInfo unitInfo = new OnlineUnitInfo();
                JSONObject object = array.optJSONObject(i);
                unitInfo.parseInfo(object);
                mUnitList.add(unitInfo);
            }
        }
    }
}
