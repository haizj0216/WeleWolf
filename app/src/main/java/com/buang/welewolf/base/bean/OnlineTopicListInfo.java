package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/1/9.
 */
public class OnlineTopicListInfo extends BaseObject {

    public List<OnlineTopicDetailInfo.OnlineTopicItemInfo> mTopics = new ArrayList<OnlineTopicDetailInfo.OnlineTopicItemInfo>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONArray data = json.optJSONArray("data");
        if (data.length() > 0) {
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.optJSONObject(i);
                OnlineTopicDetailInfo.OnlineTopicItemInfo info = new OnlineTopicDetailInfo.OnlineTopicItemInfo();
                info.mAddTime = object.optString("addTime");
                info.mDesc = object.optString("desc");
                info.mName = object.optString("name");
                info.mTopicID = object.optString("topicID");
                info.mPictureUrl = object.optString("pictureUrl");
                mTopics.add(info);
            }
        }
    }
}