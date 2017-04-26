package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 16/9/8.
 */
public class OnlineExportPdfInfo extends BaseObject {
    public String mUrl;
    public String mMD5;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        mUrl = data.optString("url");
        mMD5 = data.optString("md5");
    }
}
