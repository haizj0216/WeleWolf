package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by weilei on 17/4/24.
 */
public class OnlineRankListInfo extends BaseObject {

    public List<BannerInfoItem> mBannerInfos;
    public List<OnlineRankItemInfo> mRankInfos;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (json.has("data")) {
            JSONObject data = json.optJSONObject("data");

        }
    }
}
