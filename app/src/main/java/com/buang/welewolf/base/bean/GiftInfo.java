package com.buang.welewolf.base.bean;

import org.json.JSONObject;

/**
 * Created by weilei on 17/6/11.
 */

public class GiftInfo {
    public String name;
    public String image;
    public String giftID;
    public int count;
    public int popularity;

    public void parse(JSONObject jsonObject) {
        name = jsonObject.optString("name");
        image = jsonObject.optString("image");
        giftID = jsonObject.optString("giftID");
        count = jsonObject.optInt("count");
    }
}
