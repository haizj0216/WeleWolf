package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 15/11/12.
 */
public class OnlineHomeworkInfo extends BaseObject {


    public List<HomeworkMessageInfo> mHMKMessages = new ArrayList<HomeworkMessageInfo>();
    public String score;
    public String gold;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            JSONArray infos = data.optJSONArray("homework_info");
            if (infos == null) {
                infos = data.optJSONArray("homeworkInfo");
            }
            if (infos != null && infos.length() > 0) {
                for (int i = 0; i < infos.length(); i++) {
                    JSONObject info = infos.optJSONObject(i);
                    HomeworkMessageInfo messageInfo = new HomeworkMessageInfo();
                    messageInfo.mCurrentTs = info.optString("currentTime");
                    messageInfo.mHomeworkId = info.optString("homeworkID");
                    messageInfo.mCreateTs = info.optString("startTime");
                    messageInfo.mDeadLineTs = info.optString("endTime");
                    messageInfo.mTeacherName = info.optString("teacherName");
                    messageInfo.mClassId = info.optString("classID");
                    messageInfo.mType = info.optString("type");
                    messageInfo.mText = info.optString("txt");
                    messageInfo.mGroupID = info.optString("groupID");
                    messageInfo.mHomeworkTitle = info.optString("homeworkTitle");

                    mHMKMessages.add(messageInfo);
                }
            }

            if (data.has("homeworkReward")) {
                JSONObject rewardObj = data.optJSONObject("homeworkReward");
                score = rewardObj.optString("score");
                gold = rewardObj.optString("gold");
            }
        }
    }

    public class HomeworkMessageInfo {
        public String mHomeworkId;
        public String mCreateTs;
        public String mDeadLineTs;
        public String mClassId;//班级ID
        public String mTeacherName;
        public String mType;
        public String mText;
        public String mHomeworkTitle;
        public String mGroupID;
        public String mCurrentTs;
    }

}
