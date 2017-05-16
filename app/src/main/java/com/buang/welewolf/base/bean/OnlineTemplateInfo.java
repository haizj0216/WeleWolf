package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.security.MD5Util;

import org.json.JSONObject;

/**
 * Created by weilei on 16/5/5.
 */
public class OnlineTemplateInfo extends BaseObject{

    public String mJsUrl;
    public String mCssUrl;
    public String mTempleUrl;
    public String urlMD5;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);

        JSONObject data = json.optJSONObject("data");
        mJsUrl = data.optString("tiku_js");
        mCssUrl = data.optString("tiku_css");
        mTempleUrl = data.optString("assets");
        if (!TextUtils.isEmpty(mTempleUrl))
            urlMD5 = MD5Util.encode(mTempleUrl);
    }
}
