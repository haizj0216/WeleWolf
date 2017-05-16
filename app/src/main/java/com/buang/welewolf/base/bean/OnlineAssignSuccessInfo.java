package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 17/2/23.
 */
public class OnlineAssignSuccessInfo extends BaseObject {
    public OnlineCompetitionListInfo.CompetitionItem competitionItem = new OnlineCompetitionListInfo.CompetitionItem();
    public String jumpUrl;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        competitionItem.buildCompetition(data);
        jumpUrl = json.optString("jumpUrl");
    }
}
