package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/5/4.
 */
public class OnlineRecommendMoreListInfo extends BaseObject {
    public List<OnlineGroupItemInfo> moreRecommendlist = new ArrayList<OnlineGroupItemInfo>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = null;
        if (json.has("data")) {
            data = json.optJSONObject("data");
        }
        if (data != null) {
            JSONArray result = data.optJSONArray("result");
            for (int i = 0; i < result.length(); i++) {
                OnlineGroupItemInfo groupItem = new OnlineGroupItemInfo();
                JSONObject itemInfo = result.optJSONObject(i);
                String id = itemInfo.optString("packageID");
                if (TextUtils.isEmpty(id)) {
                    id = itemInfo.optString("topicID");
                }

                if (TextUtils.isEmpty(id)) {
                    id = itemInfo.optString("homeworkID");
                }

                if (TextUtils.isEmpty(id)) {
                    id = itemInfo.optString("paperID");
                }
                groupItem.mGroupID = id;
                groupItem.mGroupName = itemInfo.optString("title");
                groupItem.mTeacherName = itemInfo.optString("teacherName");
                groupItem.mTeacherSchool = itemInfo.optString("schoolName");
                groupItem.mHot = itemInfo.optInt("hot");
                groupItem.mTeacherHead = itemInfo.optString("head");
                groupItem.mGroupImage = itemInfo.optString("image");
                groupItem.mCount = itemInfo.optString("questionCount");
                groupItem.mAddTime = itemInfo.optLong("publishTime");
                groupItem.mPaperYear = itemInfo.optString("year");
                moreRecommendlist.add(groupItem);
            }
        }
    }
}
