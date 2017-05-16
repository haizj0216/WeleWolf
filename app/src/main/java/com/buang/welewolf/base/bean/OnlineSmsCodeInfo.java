package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fanjb
 * @name 短信验证码
 * @date 2015-3-23
 */
public class OnlineSmsCodeInfo extends BaseObject {

    public List<String> mobileCode = new ArrayList<String>();
    public String currectTime;
    public String lastTime;
    public String token;
    public int existed = -1;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (json.has("data")) {
            JSONObject data = json.optJSONObject("data");

            if (data.has("mobileCode"))
                mobileCode.add(data.optString("mobileCode"));
            if (data.has("code")) {
                JSONArray code = data.optJSONArray("code");
                for (int i = 0; i < code.length(); i++) {
                    mobileCode.add(code.optString(i));
                }
            }
            currectTime = data.optString("currectTime");
            lastTime = data.optString("lastTime");
            token = data.optString("token");
            existed = data.optInt("existed");
        }
    }

}
