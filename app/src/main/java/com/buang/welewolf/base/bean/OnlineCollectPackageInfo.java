package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 16/5/11.
 */
public class OnlineCollectPackageInfo extends BaseObject {

    public List<OnlineGroupItemInfo> groupItemInfos = new ArrayList<OnlineGroupItemInfo>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject obj = json.optJSONObject("data");
        if (obj != null) {
            JSONArray array = obj.optJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                OnlineGroupItemInfo info = new OnlineGroupItemInfo();
//                sourceType: 3,
//                        sourceID: "11",
//                        addTime: 1450108800,
//                        sourceName: "热门知识点排行",
//                        title: "根据数轴上的点写数",
//                        teacherName: "盒子官方推荐",
//                        schoolName: "盒子学校",
//                        questionCount: "30"
                info.mType = object.optInt("sourceType");
                info.mGroupID = object.optString("sourceID");
                info.mGroupName = object.optString("title");
                info.mTeacherName = object.optString("teacherName");
                info.mTeacherSchool = object.optString("schoolName");
                info.mCount = object.optString("questionCount");
                info.mAddTime = object.optLong("addTime");
                info.mFromType = object.optInt("fromType");
                info.mSourceName = object.optString("sourceName");
                groupItemInfos.add(info);
            }
        }
    }
}
