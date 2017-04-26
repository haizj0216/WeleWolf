package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/11/30.
 */
public class OnlineActivitiesInfo extends BaseObject {

    public static final int TYPE_INVITE = 1;
    public static final int TYPE_REDPACK = 2;

    public String mTotalCoin;
    public String mCoinMallUrl;
    public List<ActivityInfo> mActivitiyInfos = new ArrayList<ActivityInfo>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data.has("userInfo")) {
            JSONObject userInfo = data.optJSONObject("userInfo");
            mTotalCoin = userInfo.optString("coin");
            mCoinMallUrl = userInfo.optString("coinMallUrl");
        }
        JSONArray jsonArray = data.optJSONArray("activityList");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            ActivityInfo info = new ActivityInfo(jsonObject);
            mActivitiyInfos.add(info);
        }
    }

    public static class ActivityInfo implements Serializable {
        public String activityId;
        public int type;
        public String url;
        public String mJoinText;
        public String mbackImage;
        public String mTitle;
        public String mDesc;
        public String mStatusInfo;
        public int mJoinCount;
        public String mHeaderPhoto1;
        public String mHeaderPhoto2;
        public String mHeaderPhoto3;

        public ActivityInfo(JSONObject json) {
            activityId = json.optString("activityId");
            type = json.optInt("type");
            url = json.optString("joinButtonUrl");
            mJoinText = json.optString("joinButtonTxt");
            mbackImage = json.optString("backImage");
            mTitle = json.optString("name");
            mDesc = json.optString("desc");
            mStatusInfo = json.optString("message");
            mJoinCount = json.optInt("joinCount");
            JSONArray headers = json.optJSONArray("joinUserList");
            if (headers.length() > 0) {
                mHeaderPhoto1 = headers.optJSONObject(0).optString("headPhoto");
            }
            if (headers.length() > 1) {
                mHeaderPhoto2 = headers.optJSONObject(1).optString("headPhoto");
            }
            if (headers.length() > 2) {
                mHeaderPhoto3 = headers.optJSONObject(2).optString("headPhoto");
            }
        }
    }
}
