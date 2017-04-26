package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by weilei on 15/8/31.
 */
public class OnlineTeacherWebInfo extends BaseObject {

    public static final int STATUS_ADD_CLASS = 1;
    public static final int STATUS_ADD_STUDENT = 2;
    public static final int STATUS_MAKE_HOMEWORK = 3;

    public OnlineTeacherWebItem mMsgItem;
    public OnlineTeacherWebItem mHomeworkItem;
    public OnlineTeacherWebItem mClassItem;
    public OnlineTeacherWebItem mUserItem;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (isAvailable()) {
            JSONObject data = json.optJSONObject("data");
            if (data.has("msg")) {
                JSONObject msg = data.optJSONObject("msg");
                mMsgItem = new OnlineTeacherWebItem();
                mMsgItem.mUrl = msg.optString("url");
                mMsgItem.mAlert = msg.optString("alert");
                mMsgItem.isRemind = msg.optString("isRemind").equals("0") ? false : true;
                mMsgItem.mTime = msg.optInt("times");
                mMsgItem.mEventID = msg.optString("eventID");
            }

            if (data.has("homework")) {
                JSONObject msg = data.optJSONObject("homework");
                mHomeworkItem = new OnlineTeacherWebItem();
                mHomeworkItem.mUrl = msg.optString("url");
                mHomeworkItem.mAlert = msg.optString("alert");
                mHomeworkItem.isRemind = msg.optString("isRemind").equals("0") ? false : true;
                mHomeworkItem.mTime = msg.optInt("times");
                mHomeworkItem.mEventID = msg.optString("eventID");
            }

            if (data.has("class")) {
                JSONObject msg = data.optJSONObject("class");
                mClassItem = new OnlineTeacherWebItem();
                mClassItem.mUrl = msg.optString("url");
                mClassItem.mAlert = msg.optString("alert");
                mClassItem.isRemind = msg.optString("isRemind").equals("0") ? false : true;
                mClassItem.mTime = msg.optInt("times");
                mClassItem.mEventID = msg.optString("eventID");
            }

            if (data.has("user")) {
                JSONObject msg = data.optJSONObject("user");
                mUserItem = new OnlineTeacherWebItem();
                mUserItem.mUrl = msg.optString("url");
                mUserItem.mAlert = msg.optString("alert");
                mUserItem.isRemind = msg.optString("isRemind").equals("0") ? false : true;
                mUserItem.mTime = msg.optInt("times");
                mUserItem.mEventID = msg.optString("eventID");
            }
        }
    }

    public class OnlineTeacherWebItem {
        public String mUrl;
        public String mAlert;
        public boolean isRemind;
        public String mEventID;
        public int mTime;
    }
}
