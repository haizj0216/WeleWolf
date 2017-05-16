package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 15/7/11.
 */
public class OnlineGlobalInfo extends BaseObject {

    public static final int TYPE_CLASS_TRANSFER_NOTIFYTION = 0;
    public static final int TYPE_CLASS_TRANSFER_REFUE = 1;
    public static final int TYPE_CLASS_TRANSFER_SUCCESS = 2;
    public static final int TYPE_ACTIVITY = 4;

    public List<OnlineGlobalInfoItem> mInfos = new ArrayList<OnlineGlobalInfoItem>();
    public boolean isEarnGoldOpen = false;

    public List<OnlineGreetItem> mGreets = new ArrayList<OnlineGreetItem>();
    public int maxQuestionNumPerHomework;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (null != data) {
            if (data.has("switchcoin")) {
                isEarnGoldOpen = data.optInt("switchcoin") == 1;
            } else {
                isEarnGoldOpen = false;
            }
            JSONObject greet = data.optJSONObject("greeting");
            if (null != greet) {
                JSONObject images = greet.optJSONObject("images");
                if (images != null) {
                    OnlineGreetItem item = new OnlineGreetItem();
                    item.mJumpUrl = images.optString("jumpUrl");
                    item.mImageUrl = images.optString("imageUrl");
                    item.timestamp = images.optLong("timestamp");
                    mGreets.add(item);
                }

            }

            if (data.has("tiku")) {
                JSONObject tiku = data.optJSONObject("tiku");
                if (null != tiku) {
                    maxQuestionNumPerHomework = tiku.optInt("maxQuestionNumPerHomework");
                }
            }

            JSONArray infos = data.optJSONArray("info");
            if (null != infos) {
                for (int i = 0; i < infos.length(); i++) {
                    JSONObject info = infos.optJSONObject(i);
                    OnlineGlobalInfoItem item = new OnlineGlobalInfoItem();
                    item.parse(info);
                    mInfos.add(item);
                }
            }
        }
    }

    public static class OnlineGlobalInfoItem implements Serializable {
        public int mInfoID;
        public int mInfoType;
        public String mMessage;
        public OnlineSchoolTeacherInfo.TeacherInfo mObject;
        public String mImg;
        public String mTitle;
        public String mUrl;
        public String mDescription;

        public void parse(JSONObject json) {
            mInfoID = json.optInt("infoID");
            mInfoType = json.optInt("infoType");
            JSONObject memo = json.optJSONObject("memo");
            mMessage = memo.optString("txt");
            if (mInfoType == TYPE_CLASS_TRANSFER_NOTIFYTION || mInfoType == TYPE_CLASS_TRANSFER_REFUE
                    || mInfoType == TYPE_CLASS_TRANSFER_SUCCESS) {
                mObject = new OnlineSchoolTeacherInfo.TeacherInfo();
                mObject.parse(memo);
            }

            mImg = memo.optString("img");
            mTitle = memo.optString("title");
            mUrl = memo.optString("url");
            mDescription = memo.optString("description");
        }
    }

    public static class OnlineGreetItem {
        public long timestamp;
        public String mJumpUrl;
        public String mImageUrl;
    }
}
