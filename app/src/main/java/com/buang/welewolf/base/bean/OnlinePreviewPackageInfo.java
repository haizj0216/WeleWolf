package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.bean.OnlineHomeworkSectionsInfo.SectionInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 2016/8/25.
 */
public class OnlinePreviewPackageInfo extends BaseObject {

    public List<SectionInfo> mSectionInfos;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONArray array = json.optJSONArray("data");
        if (null != array) {
            mSectionInfos = new ArrayList<SectionInfo>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject section = array.optJSONObject(i);
                if(section != null){
                    SectionInfo info = new SectionInfo();
                    info.mSectionId = section.optString("courseSectionID");
                    info.mSectionName = section.optString("sectionName");
                    JSONArray packageArr = section.optJSONArray("packageList");
                    if (null != packageArr) {
                        info.mGroups = new ArrayList<OnlineGroupItemInfo>();
                        for (int j = 0; j < packageArr.length(); j++) {
                            JSONObject packageGroup = packageArr.optJSONObject(j);
                            OnlineGroupItemInfo groupItemInfo = new OnlineGroupItemInfo();
                            groupItemInfo.mGroupID = packageGroup.optString("packageID");
                            groupItemInfo.mGroupName = packageGroup.optString("title");
                            groupItemInfo.mCount = packageGroup.optString("count");
                            groupItemInfo.mSourceId = packageGroup.optString("sourceID");
                            groupItemInfo.mTeacherName = packageGroup.optString("teacherName");
                            groupItemInfo.mTeacherSchool = packageGroup.optString("schoolName");
                            groupItemInfo.mHot = packageGroup.optInt("hot");
                            groupItemInfo.mType = packageGroup.optInt("groupType");
                            info.mGroups.add(groupItemInfo);
                        }
                    }
                    mSectionInfos.add(info);
                }
            }
        }
    }
}
