package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 红包
 * Created by LiuYu on 16/12/7.
 */
public class OnlineRedPkgInfo extends BaseObject {

    public String pkgId;
    public String activityId;
    public String type;
    public String desc;
    public String schoolId;
    public String coin;
    public String count;
    public boolean isHaveRedPkg;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        try {
            JSONObject data = json.getJSONObject("data");
            if (data != null) {
                JSONArray jsonArray = data.optJSONArray("pkgList");
                if (jsonArray != null && jsonArray.length() > 0) {
                    JSONObject jsonObject = jsonArray.optJSONObject(0);
                    isHaveRedPkg = true;
                    pkgId = jsonObject.optString("pkgId");
                    activityId = jsonObject.optString("activityId");
                    type = jsonObject.optString("type");
                    desc = jsonObject.optString("desc");
                    schoolId = jsonObject.optString("schoolId");
                    coin = jsonObject.optString("coin");
                    count = jsonObject.optString("count");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
