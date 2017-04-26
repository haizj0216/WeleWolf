package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

public class OnlineNewTeacherTaskInfo extends BaseObject {

    public TaskInfoItem mTaskInfoItem;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        try {
            if (isAvailable()) {
                JSONObject jsonObject = json.getJSONObject("data");
                mTaskInfoItem = new TaskInfoItem();
                mTaskInfoItem.mMsg1 = jsonObject.optString("msg1");
                mTaskInfoItem.mMsg2 = jsonObject.optString("msg2");
                mTaskInfoItem.mCount = jsonObject.optInt("count");
                mTaskInfoItem.mImage = jsonObject.optString("img");
                mTaskInfoItem.mUrl = jsonObject.optString("url");
                mTaskInfoItem.mType = jsonObject.optInt("type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TaskInfoItem {
        public String mMsg1;
        public String mMsg2;
        public int mCount;
        public int mType;
        public String mImage;
        public String mUrl;
    }

}
