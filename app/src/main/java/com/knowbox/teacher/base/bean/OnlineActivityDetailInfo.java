package com.knowbox.teacher.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.base.service.share.ShareContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/12/1.
 * 红包赚金币活动详情
 */
public class OnlineActivityDetailInfo extends BaseObject {

    public List<JoinTeacherInfo> joinList = new ArrayList<JoinTeacherInfo>();
    public List<ActivityTaskInfo> tasks = new ArrayList<ActivityTaskInfo>();
    public ShareContent mShareInfo;

    public String startTime;
    public String endTime;
    public String sub;
    public String totalCoin;
    public String multiple;
    public String remainedDay;
    public String remainedHour;
    public String remainedMinute;
    public int mRemaindDay;
    public int mRemaindHour;
    public int mRemaindMinute;
    public String totalJoin;
    public String successJoin;
    public String ruleUrl;
    public boolean isCoreTeacher;
    public String desc;
    public int nextPage = -1;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        json = json.optJSONObject("data");
        startTime = json.optString("startTime");
        endTime = json.optString("endTime");
        JSONObject info = json.optJSONObject("titleInfo");
        if (info != null) {
            sub = info.optString("sub");
            desc = info.optString("join");
        }
        totalCoin = json.optString("totalCoin");
        multiple = json.optString("multiple");
        remainedDay = json.optString("rewardDay");
        mRemaindDay = json.optInt("rewardDay");
        if (TextUtils.isEmpty(remainedDay)) {
            remainedDay = "00";
        } else if (remainedDay.length() == 1) {
            remainedDay = "0" + remainedDay;
        }
        remainedHour = json.optString("rewardHour");
        mRemaindHour = json.optInt("rewardHour");
        if (TextUtils.isEmpty(remainedHour)) {
            remainedHour = "00";
        } else if (remainedHour.length() == 1) {
            remainedHour = "0" + remainedHour;
        }
        remainedMinute = json.optString("rewardMinute");
        mRemaindMinute = json.optInt("rewardMinute");
        if (TextUtils.isEmpty(remainedMinute)) {
            remainedMinute = "00";
        } else if (remainedMinute.length() == 1) {
            remainedMinute = "0" + remainedMinute;
        }
        totalJoin = json.optString("totalJoin");
        successJoin = json.optString("successJoin");
        ruleUrl = json.optString("ruleUrl");
        isCoreTeacher = json.optInt("isKernelTeacher") == 1;

        if (json.has("nextPage")) {
            nextPage = json.optInt("nextPage");
        }

        if (json.has("shareContent")) {
            JSONObject shareInfo = json.optJSONObject("shareContent");
            if (shareInfo != null) {
                mShareInfo = new ShareContent();
                mShareInfo.mShareUrl = shareInfo.optString("url");
                mShareInfo.mShareTitleUrl = shareInfo.optString("titleUrl");
                mShareInfo.mUrlImage = shareInfo.optString("imageUrl");
                mShareInfo.mSiteUrl = shareInfo.optString("siteUrl");
                mShareInfo.mShareTitle = shareInfo.optString("title");
                mShareInfo.mDescription = shareInfo.optString("text");
                mShareInfo.mShareContent = shareInfo.optString("text");
                mShareInfo.mSiteName = shareInfo.optString("site");
            }
        }

        JSONArray taskList = json.optJSONArray("taskList");
        if (taskList != null) {
            for (int i = 0; i < taskList.length(); i++) {
                JSONObject jsonObject = taskList.optJSONObject(i);
                ActivityTaskInfo task = new ActivityTaskInfo(jsonObject);
                tasks.add(task);
            }
        }

        JSONArray jsonArray = json.optJSONArray("joinList");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            JoinTeacherInfo joinTeacherInfo = new JoinTeacherInfo(jsonObject);
            joinList.add(joinTeacherInfo);
        }
//        if (jsonArray.length() == 0) {
//            for (int i = 0; i < 10; i++) {
//                JoinTeacherInfo joinTeacherInfo = new JoinTeacherInfo();
//                joinTeacherInfo.isStandard = i % 2 == 0;
//                joinTeacherInfo.mGold = (100 + i) + "";
//                joinTeacherInfo.mJoinTeacherName = "刘禹锡";
//                joinTeacherInfo.mJoinTeacherPhoto = "";
//                joinList.add(joinTeacherInfo);
//            }
//        }
    }

    /**
     * 达标class
     */
    public static class JoinTeacherInfo {
        public String mJoinTeacherPhoto;
        public String mJoinTeacherName;
        public String mGold;
        public boolean isStandard;

        public JoinTeacherInfo() {}

        public JoinTeacherInfo(JSONObject jsonObject) {
            this.mJoinTeacherPhoto = jsonObject.optString("headPhoto");
            this.mJoinTeacherName = jsonObject.optString("userName");
            this.mGold = jsonObject.optString("coin");
            this.isStandard = jsonObject.optInt("status") != 0;
        }
    }

    /**
     * 任务class
     */
    public static class ActivityTaskInfo {
        public String dailyTaskId;
        public String name;
        public String icon;
        public String finishTimes;
        public String totalTimes;
        public String coin;
        public String description;
        public String behaviorId;
        public String activityId;
        public String activityType;
        public String requireTimes;
        public String taskName;

        public ActivityTaskInfo(JSONObject jsonObject) {
            this.dailyTaskId = jsonObject.optString("dailyTaskId");
            this.name = jsonObject.optString("name");
            this.icon = jsonObject.optString("icon");
            this.finishTimes = jsonObject.optString("finishTimes");
            this.totalTimes = jsonObject.optString("totalTimes");
            this.coin = jsonObject.optString("coin");
            this.description = jsonObject.optString("desc");
            this.behaviorId = jsonObject.optString("behaviorId");
            this.activityId = jsonObject.optString("activityId");
            this.activityType = jsonObject.optString("activityType");
            this.requireTimes = jsonObject.optString("requireTimes");
            this.taskName = jsonObject.optString("taskName");
        }
    }
}
