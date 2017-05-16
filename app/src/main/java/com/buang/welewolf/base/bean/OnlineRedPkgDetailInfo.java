package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 红包详情
 * Created by LiuYu on 16/12/7.
 */
public class OnlineRedPkgDetailInfo extends BaseObject {

    public String mCoin;
    public int mTotalCount;
    public int mRewardCount;
    public String mHeadPhoto;
    public String mTitle;
//    public ActivityInfo mActivityInfo;
    public List<RedpkgItemInfo> redpkgItemInfos = new ArrayList<RedpkgItemInfo>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        try {
            JSONObject jsonData = json.getJSONObject("data");
            mCoin = jsonData.optString("coin");
            mTotalCount = jsonData.optInt("totalCount");
            mRewardCount = jsonData.optInt("rewardCount");
            mHeadPhoto = jsonData.optString("headPhoto");
            mTitle = jsonData.optString("title");
//            JSONObject jsonActivityInfo = jsonData.getJSONObject("activityInfo");
//            mActivityInfo = new ActivityInfo();
//            mActivityInfo.mIcon = jsonActivityInfo.optString("icon");
//            mActivityInfo.mStartTime = jsonActivityInfo.optString("startTime");
//            mActivityInfo.mEndTime = jsonActivityInfo.optString("endTime");
            JSONArray jsonArray = jsonData.optJSONArray("otherRewardList");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    RedpkgItemInfo redpkgItemInfo = new RedpkgItemInfo();
                    redpkgItemInfo.mUserId = jsonObject.optString("userId");
                    redpkgItemInfo.mCoin = jsonObject.optString("coin");
                    redpkgItemInfo.mIcon = jsonObject.optString("headPhoto");
                    redpkgItemInfo.mName = jsonObject.optString("userName");
                    redpkgItemInfo.mDateTime = jsonObject.getString("time");
                    redpkgItemInfo.mIsBest = jsonObject.optInt("isBest") == 1;
                    redpkgItemInfos.add(redpkgItemInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public static class ActivityInfo {
//        public String mIcon;
//        public String mStartTime;
//        public String mEndTime;
//
//    }

    public static class RedpkgItemInfo {
        public String mUserId;
        public String mName;
        public String mDateTime;
        public String mIcon;
        public String mCoin;
        public boolean mIsBest;

    }

}
