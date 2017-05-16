package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.database.bean.QuestionItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 16/1/8.
 */
public class OnlineTopicDetailInfo extends BaseObject {

    public OnlineTopicItemInfo mNextTopicInfo;
    public OnlineTopicItemInfo mCurTopicInfo;
    public List<QuestionItem> mQuestions;
    public List<OnlineTopicItemInfo> mMoreTopic;
    public int mQuestionSourceType;
    public String mSourceId;
    public int mFromTYpe;
    public boolean isCollect;

    public OnlineTopicDetailInfo(int fromTYpe) {
        mFromTYpe = fromTYpe;
    }

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");

        if (data.has("sourceID")) {
            mSourceId = data.optString("sourceID");
        }
        if (data.has("sourceType")) {
            mQuestionSourceType = data.optInt("sourceType");
        }

        isCollect = data.optInt("isCollect") == 1;

        if (data.has("nextTopic")) {
            JSONObject nextObject = data.optJSONObject("nextTopic");
            if (nextObject != null) {
                mNextTopicInfo = new OnlineTopicItemInfo();
                mNextTopicInfo.mAddTime = nextObject.optString("addTime");
                mNextTopicInfo.mDesc = nextObject.optString("desc");
                mNextTopicInfo.mName = nextObject.optString("name");
                mNextTopicInfo.mTopicID = nextObject.optString("topicID");
                mNextTopicInfo.mPictureUrl = nextObject.optString("pictureUrl");
                mNextTopicInfo.mBannerUrl = nextObject.optString("bannerUrl");
                mNextTopicInfo.mBgColor = nextObject.optString("bgColor");
            }
        }

        if (data.has("currentTopic")) {
            JSONObject nextObject = data.optJSONObject("currentTopic");
            if (nextObject != null) {
                mCurTopicInfo = new OnlineTopicItemInfo();
                mCurTopicInfo.mAddTime = nextObject.optString("addTime");
                mCurTopicInfo.mDesc = nextObject.optString("desc");
                mCurTopicInfo.mName = nextObject.optString("name");
                mCurTopicInfo.mTopicID = nextObject.optString("topicID");
                mCurTopicInfo.mPictureUrl = nextObject.optString("pictureUrl");
                mCurTopicInfo.mBannerUrl = nextObject.optString("bannerUrl");
                mCurTopicInfo.mBgColor = nextObject.optString("bgColor");
                mCurTopicInfo.isCollect = isCollect;
            }

        }

        if (data.has("questionList")) {
            JSONArray arrays = data.optJSONArray("questionList");
            if (arrays != null && arrays.length() > 0) {
                mQuestions = new ArrayList<QuestionItem>();
                for (int i = 0; i < arrays.length(); i++) {
                    QuestionItem item = new QuestionItem();
                    item = item.getQuestionItem(arrays.optJSONObject(i));
                    item.mSourceId = mSourceId;
                    item.mQuestionSourceType = QuestionItem.SOURCE_TYPE_TOPIC;
                    item.mFromType = mFromTYpe;
                    mQuestions.add(item);
                }
            }
        }

        if (data.has("moreTopic")) {
            JSONArray array = data.optJSONArray("moreTopic");
            if (array != null && array.length() > 0) {
                mMoreTopic = new ArrayList<OnlineTopicItemInfo>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    OnlineTopicItemInfo info = new OnlineTopicItemInfo();
                    info.mAddTime = object.optString("addTime");
                    info.mDesc = object.optString("desc");
                    info.mName = object.optString("name");
                    info.mTopicID = object.optString("topicID");
                    info.mPictureUrl = object.optString("pictureUrl");
                    mMoreTopic.add(info);
                }
            }

        }
    }

    public static class OnlineTopicItemInfo implements Serializable{
        private static final long serialVersionUID = 8487046640334201334L;
        public String mTopicID;
        public String mName;
        public String mDesc;
        public String mPictureUrl;
        public String mAddTime;
        public String mBannerUrl;
        public String mBgColor;
        public boolean isCollect;
    }
}
