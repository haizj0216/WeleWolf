package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.bean.OnlineHomeworkSectionsInfo.SectionInfo;
import com.buang.welewolf.modules.utils.ConstantsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/5/4.
 */
public class OnlineRecommendListInfo extends BaseObject {

//    public List<RecommendGroupInfo> mRecommendGroup = new ArrayList<RecommendGroupInfo>();
    public List<RecommendListInfo> mRecommendListInfos = new ArrayList<RecommendListInfo>();
    public String teachingassistID;


    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = null;
        if (json.has("data")) {
            data = json.optJSONObject("data");
        }
        if (data != null) {

//            if (data.has("recommend")) {
//                JSONArray recommend = data.optJSONArray("recommend");
//                if (null != recommend) {
//                    for (int i = 0; i < recommend.length(); i++) {
//                        RecommendGroupInfo recommendGroupInfo = new RecommendGroupInfo();
//                        JSONObject info = recommend.optJSONObject(i);
//                        recommendGroupInfo.groupType = info.optString("groupType");
//                        recommendGroupInfo.groupName = info.optString("groupName");
//                        recommendGroupInfo.groupSeq = info.optString("groupSeq");
//                        recommendGroupInfo.count = info.optString("count");
//                        JSONArray groupResult = info.optJSONArray("groupResult");
//                        for (int j = 0; j < groupResult.length(); j++) {
//                            OnlineGroupItemInfo groupItem = new OnlineGroupItemInfo();
//                            JSONObject itemInfo = groupResult.optJSONObject(j);
//                            String id = itemInfo.optString("packageID");
//                            if (TextUtils.isEmpty(id)) {
//                                id = itemInfo.optString("topicID");
//                            }
//                            if (TextUtils.isEmpty(id)) {
//                                id = itemInfo.optString("homeworkID");
//                            }
//                            if (TextUtils.isEmpty(id)) {
//                                id = itemInfo.optString("paperID");
//                            }
//                            groupItem.mGroupID = id;
//                            groupItem.mGroupName = itemInfo.optString("title");
//                            groupItem.mTeacherName = itemInfo.optString("teacherName");
//                            groupItem.mTeacherSchool = itemInfo.optString("schoolName");
//                            groupItem.mHot = itemInfo.optInt("hot");
//                            groupItem.mTeacherHead = itemInfo.optString("head");
////                    groupItem.mGroupImage = itemInfo.optString("image");
//                            groupItem.mCount = itemInfo.optString("questionCount");
//                            groupItem.mAddTime = itemInfo.optLong("time");
//                            groupItem.mType = Integer.parseInt(recommendGroupInfo.groupType);
//                            recommendGroupInfo.groupResult.add(groupItem);
//                            if (ConstantsUtils.GROUP_TYPE_ISSUE == Integer.parseInt(recommendGroupInfo.groupType)
//                                    && recommendGroupInfo != null && recommendGroupInfo.groupResult.size() == 2){
//                                break;
//                            }else if (recommendGroupInfo != null && recommendGroupInfo.groupResult.size() == 3) {
//                                break;
//                            }
//                        }
//
//                        mRecommendGroup.add(recommendGroupInfo);
//                    }
//                }
//            }
//
//            if (data.has("coursesection")) {
//                JSONObject coursesection = data.optJSONObject("coursesection");
//                    if (null != coursesection) {
//                        JSONArray sectionlist = coursesection.optJSONArray("list");
//                            getSectionList(sectionlist, teachingAssistInfo.mSyncSectionInfo, null);
//                    }
//            }
//
//            if (data.has("teachingassist")) {
//                JSONObject teachingassist = data.optJSONObject("teachingassist");
//                if (null != teachingassist) {
//                    JSONArray array = teachingassist.optJSONArray("list");
//                    for (int i = 0; i < array.length(); i++) {
//                        TeachingItem item = new TeachingItem();
//                        JSONObject obj  = array.optJSONObject(i);
//                        item.teachingAssistID = obj.optString("teachingAssistID");
//                        item.teachingAssistName = obj.optString("teachingAssistName");
//                        item.isArea = obj.optInt("isArea") == 1;
//                        item.isSync = obj.optInt("isSync") == 1;
//                        teachingAssistInfo.teachingInfo.teachingItems.add(item);
//                    }
//                }
//            }

            if (data.has("list")) {
                JSONArray array = data.optJSONArray("list");
                if (null != array) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        if (object.has("recommend")) {
                            RecommendListInfo recommendListInfo = new RecommendListInfo();
                            recommendListInfo.recommendGroupInfo = new RecommendGroupInfo();
                            JSONObject info = object.optJSONObject("recommend");
                            recommendListInfo.recommendGroupInfo.groupType = info.optString("groupType");
                            recommendListInfo.recommendGroupInfo.groupName = info.optString("groupName");
                            recommendListInfo.recommendGroupInfo.groupSeq = info.optString("groupSeq");
                            recommendListInfo.recommendGroupInfo.count = info.optString("count");
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
                                groupItem.mCount = itemInfo.optString("questionCount");
                                groupItem.mAddTime = itemInfo.optLong("time");
                                groupItem.mType = Integer.parseInt(recommendListInfo.recommendGroupInfo.groupType);
                                recommendListInfo.recommendGroupInfo.groupResult.add(groupItem);
                                if (ConstantsUtils.GROUP_TYPE_ISSUE == Integer.parseInt(recommendListInfo.recommendGroupInfo.groupType)
                                        && recommendListInfo.recommendGroupInfo != null
                                        && recommendListInfo.recommendGroupInfo.groupResult.size() == 2){
                                    break;
                                }else if (recommendListInfo.recommendGroupInfo != null
                                        && recommendListInfo.recommendGroupInfo.groupResult.size() == 3) {
                                    break;
                                }
                            }

                            mRecommendListInfos.add(recommendListInfo);
                        }else if (object.has("coursesection")) {
                            RecommendListInfo recommendListInfo = new RecommendListInfo();
                            recommendListInfo.courceSectionInfo = new CourceSectionInfo();
                            JSONObject info = object.optJSONObject("coursesection");
                            JSONArray sectionlist = info.optJSONArray("list");
                            if (sectionlist.length() == 0) {
                                SectionInfo sectionInfo = new SectionInfo();
                                sectionInfo.isNull = true;
                                recommendListInfo.courceSectionInfo.courceSections.add(sectionInfo);
                            }else {
                                getSectionList(sectionlist, recommendListInfo.courceSectionInfo.courceSections, null);
                            }
                            mRecommendListInfos.add(recommendListInfo);
                        }else if (object.has("teachingassist")) {
                            RecommendListInfo recommendListInfo = new RecommendListInfo();
                            recommendListInfo.teachingAssistInfo = new TeachingAssistInfo();
                            JSONObject info = object.optJSONObject("teachingassist");
                            JSONArray teachinglist = info.optJSONArray("list");
                            if (teachinglist.length() == 0) {
                                TeachingAssistItem item = new TeachingAssistItem();
                                item.isNull = true;
                                recommendListInfo.teachingAssistInfo.teachingItems.add(item);
                            }else {
                                for (int j = 0; j < teachinglist.length(); j++) {
                                    TeachingAssistItem item = new TeachingAssistItem();
                                    JSONObject obj  = teachinglist.optJSONObject(j);
                                    item.teachingAssistID = obj.optString("teachingAssistID");
                                    item.teachingAssistName = obj.optString("teachingAssistName");
                                    item.isArea = obj.optInt("isArea") == 1;
                                    item.isSync = obj.optInt("isSync") == 1;
                                    recommendListInfo.teachingAssistInfo.teachingItems.add(item);
                                }
                            }
                            mRecommendListInfos.add(recommendListInfo);
                        }
                    }
                }
            }

            teachingassistID = data.optString("teachingassistID");
        }
    }

    private void getSectionList(JSONArray sectionItems, List<SectionInfo> sectionList, SectionInfo parent){
        if(sectionItems != null){
            for (int i = 0; i < sectionItems.length(); i++) {
                JSONObject section = sectionItems.optJSONObject(i);
                if(section != null){
                    SectionInfo info = new SectionInfo();
                    String courseSectionID = section.optString("courseSectionID");
                    String sectionName = section.optString("sectionName");
                    String level = section.optString("level");
                    int questionNum = section.optInt("questionNum");
                    String orderNum= section.optString("orderNum");
                    int sectionType = SectionInfo.TYPE_KNOWLEDGE;
                    if("1".equals(level)){
                        sectionType = SectionInfo.TYPE_CHAPTER;
                    }else if("2".equals(level)){
                        sectionType = SectionInfo.TYPE_SECTION;
                    }else if("3".equals(level)){
                        sectionType = SectionInfo.TYPE_KNOWLEDGE;
                    }
                    if (null != parent) {
                        info.mParentId = parent.mSectionId;
                    }
                    info.mSectionId = courseSectionID;
                    info.mSectionName = sectionName;
                    info.mParentSection = parent;
                    info.mCount = questionNum;
                    info.hasQuestion = info.mCount > 0;
                    info.mSectionType = sectionType;
                    info.mOrderNum = orderNum;
                    info.mSubSections = new ArrayList<SectionInfo>();
                    sectionList.add(info);

                    JSONArray array = section.optJSONArray("list");
                    getSectionList(array, info.mSubSections, info);
                }
            }
        }
    }

    public static class RecommendGroupInfo {//推荐
        public String groupType;//推荐题组list类型
        public String groupName;//题组list名称
        public String groupSeq;
        public String count;//题组list数目
        public ArrayList<OnlineGroupItemInfo> groupResult = new ArrayList<OnlineGroupItemInfo>();//题组list
    }

    public static class CourceSectionInfo {//同步章节
        public List<SectionInfo> courceSections = new ArrayList<SectionInfo>();
    }

    public static class TeachingAssistInfo {//教辅
        public List<TeachingAssistItem> teachingItems  = new ArrayList<TeachingAssistItem>();
    }

    public static class TeachingAssistItem {
        public String teachingAssistID;
        public String teachingAssistName;
        public boolean isArea;
        public boolean isSync;
        public boolean isNull;
    }

    public static class RecommendListInfo {
        public RecommendGroupInfo recommendGroupInfo;
        public CourceSectionInfo courceSectionInfo;
        public TeachingAssistInfo teachingAssistInfo;
    }
}
