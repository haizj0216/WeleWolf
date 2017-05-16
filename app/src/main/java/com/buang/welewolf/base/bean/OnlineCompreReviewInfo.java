package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by LiuYu on 16/7/15.
 */
public class OnlineCompreReviewInfo extends BaseObject {

    public CompreReviewInfo compreReviewInfo = new CompreReviewInfo();

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = null;
        if (json.has("data")) {
            data = json.optJSONObject("data");
        }
        if (data != null) {

            if (data.has("paper")) {
                JSONArray paperArray = data.optJSONArray("paper");
                if (null != paperArray) {
                    for (int i = 0; i < paperArray.length(); i++) {
                        JSONObject paperObj = paperArray.optJSONObject(i);
                        PaperGroupItem item = new PaperGroupItem();
                        item.groupName = paperObj.optString("groupName");
                        item.groupType = paperObj.optString("groupType");
                        item.groupSeq = paperObj.optString("groupSeq");
                        item.paperGroupType = paperObj.optString("paper_group_type");
                        compreReviewInfo.mPaperGroupInfo.add(item);
                    }
                }
            }

            if (data.has("knowledge")) {
                JSONObject knowObj = data.optJSONObject("knowledge");
                if (null != knowObj) {
                    compreReviewInfo.mKnowledgeInfo.title = knowObj.optString("title");
                    compreReviewInfo.mKnowledgeInfo.type = knowObj.optString("type");
                    compreReviewInfo.mKnowledgeInfo.msg = knowObj.optString("msg");
                    compreReviewInfo.mKnowledgeInfo.updateTime = knowObj.optString("updateTime");
                    JSONArray array = knowObj.optJSONArray("list");
                    if (array.length() == 0) {
                        OnlineHomeworkSectionsInfo.SectionInfo item = new OnlineHomeworkSectionsInfo.SectionInfo();
                        item.isNull = true;
                        compreReviewInfo.mKnowledgeInfo.knowledgeItems.add(item);
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject knowItem = array.optJSONObject(i);
                        OnlineHomeworkSectionsInfo.SectionInfo item = new OnlineHomeworkSectionsInfo.SectionInfo();
                        item.mSectionId = knowItem.optString("knowID");
                        item.mSectionName = knowItem.optString("knowledgeName");
                        item.mLevel = knowItem.optString("level");
                        item.mCount = knowItem.optInt("questionNum");
                        compreReviewInfo.mKnowledgeInfo.knowledgeItems.add(item);
                    }

                }
            }

            if (data.has("topic")) {
                JSONObject info = data.optJSONObject("topic");
                if (null != info) {
                    compreReviewInfo.mTopicRecommendInfo.groupType = info.optString("groupType");
                    compreReviewInfo.mTopicRecommendInfo.groupName = info.optString("groupName");
                    compreReviewInfo.mTopicRecommendInfo.groupSeq = info.optString("groupSeq");
                    compreReviewInfo.mTopicRecommendInfo.count = info.optString("count");
                    JSONArray groupResult = info.optJSONArray("groupResult");
                    for (int j = 0; j < groupResult.length(); j++) {
                        OnlineGroupItemInfo groupItem = new OnlineGroupItemInfo();
                        JSONObject itemInfo = groupResult.optJSONObject(j);
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
//                    groupItem.mGroupImage = itemInfo.optString("image");
                        groupItem.mCount = itemInfo.optString("questionCount");
                        groupItem.mAddTime = itemInfo.optLong("time");
                        groupItem.mType = Integer.parseInt(compreReviewInfo.mTopicRecommendInfo.groupType);
                        compreReviewInfo.mTopicRecommendInfo.groupResult.add(groupItem);
                        if (ConstantsUtils.GROUP_TYPE_ISSUE == Integer.parseInt(compreReviewInfo.mTopicRecommendInfo.groupType)
                                && compreReviewInfo.mTopicRecommendInfo != null
                                && compreReviewInfo.mTopicRecommendInfo.groupResult.size() == 2) {
                            break;
                        } else if (compreReviewInfo.mTopicRecommendInfo != null
                                && compreReviewInfo.mTopicRecommendInfo.groupResult.size() == 3) {
                            break;
                        }
                    }
                }
            }

        }
    }

    public static class PaperGroupItem implements Serializable{
        private static final long serialVersionUID = -7032597656973311745L;
        public String groupType;
        public String groupName;
        public String groupSeq;
        public String paperGroupType;
    }

    public static class KnowledgeInfo {
        public String title;
        public String type;
        public String msg;
        public String updateTime;
        public ArrayList<OnlineHomeworkSectionsInfo.SectionInfo> knowledgeItems = new ArrayList<OnlineHomeworkSectionsInfo.SectionInfo>();
    }

    public static class CompreReviewInfo {
        public ArrayList<PaperGroupItem> mPaperGroupInfo = new ArrayList<PaperGroupItem>();
        public KnowledgeInfo mKnowledgeInfo = new KnowledgeInfo();
        public OnlineRecommendListInfo.RecommendGroupInfo mTopicRecommendInfo = new OnlineRecommendListInfo.RecommendGroupInfo();
    }

}
