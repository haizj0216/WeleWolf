package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 15/7/3.
 */
public class OnlineClassListInfo extends BaseObject {
    public List<ClassInfoItem> mClassItems = new ArrayList<ClassInfoItem>();
    public String mGradePart;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = null;
        if (json.has("data")) {
            data = json.optJSONObject("data");
        }else {
            data = json;
        }
        if (data != null) {
            mGradePart = data.optString("gradePart");
            JSONArray array = data.optJSONArray("classList");
            if (array == null) {
                array = data.optJSONArray("myClasses");
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject infoJson = array.optJSONObject(i);
                ClassInfoItem info = new ClassInfoItem();
                info.parseItem(info, infoJson);
                mClassItems.add(info);
            }
        }

    }
}
