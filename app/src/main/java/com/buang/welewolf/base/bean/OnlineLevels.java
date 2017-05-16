package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/11/30.
 */
public class OnlineLevels extends BaseObject {

    public static final int TITLE = 0x0;
    public static final int VALUE = 0x1;

    public static final int BG_GRAY = 0x0;
    public static final int BG_WHITE = 0x1;

    public int mTotalCount;
    public int mNextPage;

    public List<LevelInfo> list = new ArrayList<LevelInfo>();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        json = json.optJSONObject("data");
        mTotalCount = json.optInt("totalCount");
        mNextPage = json.optInt("nextPage");
        JSONArray jsonArray = json.optJSONArray("expList");
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i ++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                LevelInfo levelInfo = new LevelInfo();
                levelInfo.behaviorId = jsonObject.optInt("expBehaviorId");
                levelInfo.name = jsonObject.optString("description");
                levelInfo.value = jsonObject.optString("expValue");
                levelInfo.time = jsonObject.optString("createTime");
                levelInfo.type = VALUE;
                levelInfo.description = jsonObject.optString("description");
                levelInfo.expText = jsonObject.optString("expText");
                list.add(levelInfo);
            }
        }
    }

    public static class LevelInfo implements Comparable<LevelInfo> {
        public int behaviorId;
        public int type;
        public String name;
        public String value;
        public String time;
        public int background;
        public String description;
        public String expText;

        @Override
        public int compareTo(LevelInfo another) {
            return another.time.compareTo(this.time);
        }
    }

}
