package com.knowbox.teacher.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.modules.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/1/9.
 */
public class OnlineCompetitionListInfo extends BaseObject {

    public List<CompetitionItem> mCompetitions = new ArrayList<CompetitionItem>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            if (data.has("matchList")) {
                JSONArray array = data.optJSONArray("matchList");
                if (array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        mCompetitions.add(new CompetitionItem().buildCompetition(object));
                    }
                }
            }

        }

    }

    public static class CompetitionItem implements Serializable{
        private static final long serialVersionUID = -9168483345793262535L;
        public int STATUS_NOTSTART = 1;
        public int STATUS_FINISHED = 5;
        public static final String TYPE_SINGLE_TEST = "4";
        public static final String TYPE_LOOP_MATCH = "1";
        public String name;
        public String matchID;
        public int status;
        public String joinUser;
        public String classId;
        public String totalUser;
        public String className;
        public String beginTime;
        public String stopTime;
        public String matchTimeDesc;
        public String singletestTime;
        public int rewardCoin;
        public boolean isSelected;
        public String type;

        public CompetitionItem buildCompetition(JSONObject object) {
            this.name = object.optString("name");
            this.matchID = object.optString("matchId");
            this.status = object.optInt("status");
            this.totalUser = object.optString("totalUser");
            this.joinUser = object.optString("joinUser");
            this.className = object.optString("className");
            this.classId = object.optString("classID");
            this.beginTime = object.optString("beginTime");
            this.stopTime = object.optString("stopTime");
            this.rewardCoin = object.optInt("rewardCoin");
            this.type = object.optString("type");
            try {
                if (!TextUtils.isEmpty(this.beginTime) && !TextUtils.isEmpty(this.stopTime)) {
                    matchTimeDesc = DateUtil.getMonthDay(Long.parseLong(this.beginTime))
                            + "-" + DateUtil.getMonthDay(Long.parseLong(this.stopTime))
                            + " 每天" + DateUtil.getHourMintute(Long.parseLong(this.beginTime))
                            + "-" + DateUtil.getHourMintute(Long.parseLong(this.stopTime));

                    singletestTime = DateUtil.getMonthDay(Long.parseLong(this.beginTime))
                            + " " + DateUtil.getHourMintute(Long.parseLong(this.beginTime))
                            + "-" + DateUtil.getHourMintute(Long.parseLong(this.stopTime));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

    }
}
