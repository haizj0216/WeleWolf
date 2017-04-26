package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.base.bean.OnlineActivityDetailInfo.JoinTeacherInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/12/6.
 * 红包活动参与老师的信息
 */
public class OnlineJoinTeacherInfo extends BaseObject {

    public List<JoinTeacherInfo> joinList = new ArrayList<JoinTeacherInfo>();

    public int nextPage;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        nextPage = data.optInt("nextPage");

        JSONArray jsonArray = data.optJSONArray("joinList");
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
}
