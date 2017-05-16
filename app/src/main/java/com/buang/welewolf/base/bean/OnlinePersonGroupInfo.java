package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OnlinePersonGroupInfo extends BaseObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6599663141887651652L;
    public List<OnlineGroupItemInfo> mGroups = new ArrayList<OnlineGroupItemInfo>();
    //	public List<OnlineGroupItemInfo> mShareGroups = new ArrayList<OnlineGroupItemInfo>();
    public OnlineGroupItemInfo mDefaultGroup = new OnlineGroupItemInfo();
    public OnlineGroupItemInfo mPhotoGroup = new OnlineGroupItemInfo();
    public int packageNum = 0;
    public int homeworkNum = 0;

    public OnlinePersonGroupInfo() {
        mDefaultGroup.mGroupID = "0";
        mDefaultGroup.mGroupName = "收藏的散题";
        mDefaultGroup.mIsDefault = 1;

        mPhotoGroup.mGroupID = "0";
        mPhotoGroup.mGroupName = "拍照题集";
        mPhotoGroup.mIsDefault = 2;

    }

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (isAvailable()) {
            try {
                JSONObject jsonInfo = json.optJSONObject("data");
                if (jsonInfo.has("my_collect_package")) {
                    JSONObject collectPage = jsonInfo.optJSONObject("my_collect_package");
                    if (collectPage != null) {
                        packageNum = collectPage.optInt("package_num");
                    }
                }

                if (jsonInfo.has("my_collect_homework")) {
                    JSONObject collectHomework = jsonInfo.optJSONObject("my_collect_homework");
                    if (collectHomework != null) {
                        homeworkNum = collectHomework.optInt("package_num");
                    }
                }

                JSONArray arrays = jsonInfo.optJSONArray("my_group");
                if (arrays != null) {
                    for (int i = 0; i < arrays.length(); i++) {
                        OnlineGroupItemInfo item = new OnlineGroupItemInfo();
                        JSONObject object;
                        object = arrays.getJSONObject(i);
                        item.mGroupName = object.optString("name");
                        item.mGroupID = object.optString("groupId");
                        item.mCount = object.optString("count");
                        item.mIsDefault = object.optInt("isDefault");
                        item.mAddTime = object.optLong("addTime");
                        if (item.mIsDefault == 1) {
                            item.mType = OnlineGroupItemInfo.TYPE_COLLECT;
                            mDefaultGroup = item;
                            continue;
                        } else if (item.mIsDefault == 2) {
                            item.mType = OnlineGroupItemInfo.TYPE_PHOTO;
                            mPhotoGroup = item;
                            continue;
                        }
                        item.mType = OnlineGroupItemInfo.TYPE_MY_GROUP;
                        mGroups.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
