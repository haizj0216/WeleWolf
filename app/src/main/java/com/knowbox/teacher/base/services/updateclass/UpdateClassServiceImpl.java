package com.knowbox.teacher.base.services.updateclass;

import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.base.bean.OnlineClassListInfo;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.http.services.OnlineServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 2016/4/22.
 */
public class UpdateClassServiceImpl implements UpdateClassService {

    private UpdateClassServiceObserver mUpdateClassServiceObserver = new UpdateClassServiceObserver();

    private List<ClassInfoItem> mClassInfoItems = new ArrayList<ClassInfoItem>();

    @Override
    public UpdateClassServiceObserver getObserver() {
        return mUpdateClassServiceObserver;
    }

    @Override
    public void addAllClassItem(List<ClassInfoItem> classInfoItems) {
        if (null == mClassInfoItems)
            mClassInfoItems = new ArrayList<ClassInfoItem>();
        if (null != classInfoItems) {
            clearAllClassItems();
            mClassInfoItems.addAll(classInfoItems);
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    getObserver().notifyOnRefreshClassList();
                }
            });
        }
    }

    @Override
    public void clearAllClassItems() {
        if (null != mClassInfoItems) {
            mClassInfoItems.clear();
        }
    }

    @Override
    public void addClassItem(ClassInfoItem classInfoItem) {
        if (null == mClassInfoItems)
            mClassInfoItems = new ArrayList<ClassInfoItem>();
        if (mClassInfoItems.contains(classInfoItem))
            return;
        mClassInfoItems.add(0, classInfoItem);
        getObserver().notifyOnCreateClass(classInfoItem);

    }

    @Override
    public void removeClassItem(String classId) {
        if (null == mClassInfoItems)
            return;
        ClassInfoItem item = getClassInfoItem(classId);
        if (null == item)
            return;
        mClassInfoItems.remove(getClassInfoItemIndex(classId));
        getObserver().notifyOnDeleteClass(item);
    }

    @Override
    public void updateClassItem(ClassInfoItem classInfoItem) {
        if (null == mClassInfoItems)
            return;
        getObserver().notifyOnUpdatedClass(classInfoItem);
    }

    @Override
    public ClassInfoItem getClassInfoItem(String classId) {
        if (null == mClassInfoItems)
            return null;
        if (getClassInfoItemIndex(classId) < 0)
            return null;
        return mClassInfoItems.get(getClassInfoItemIndex(classId));
    }

    @Override
    public int getClassInfoItemIndex(String classId) {
        if (null == mClassInfoItems)
            return -1;
        int index = -1;
        for (int i = 0; i < mClassInfoItems.size(); i++) {
            if (mClassInfoItems.get(i).classId.equals(classId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public int getClassInfoIndexByGroup(String groupId) {
        if (null == mClassInfoItems)
            return -1;
        int index = -1;
        for (int i = 0; i < mClassInfoItems.size(); i++) {
            if (mClassInfoItems.get(i).groupId.equals(groupId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void updateClassInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = OnlineServices.getClassDetailUrl();
                OnlineClassListInfo onlineInfo = new DataAcquirer<OnlineClassListInfo>()
                        .acquire(url, new OnlineClassListInfo(), -1);
                if (onlineInfo.isAvailable()) {
                    addAllClassItem(onlineInfo.mClassItems);
                }
            }
        }).start();
    }

    @Override
    public void updateClassCount(String classId, int count) {
        ClassInfoItem classInfoItem = getClassInfoItem(classId);
        if (classInfoItem != null) {
            classInfoItem.studentNum = count;
            getObserver().notifyOnUpdatedClass(classInfoItem);
        }
    }

    @Override
    public List<ClassInfoItem> getAllClassInfoItem() {
        return mClassInfoItems;
    }

    @Override
    public ClassInfoItem getClassInfoByGroupId(String groupId) {
        if (null == mClassInfoItems)
            return null;
        if (getClassInfoIndexByGroup(groupId) < 0)
            return null;
        return mClassInfoItems.get(getClassInfoIndexByGroup(groupId));
    }

    @Override
    public void releaseAll() {
        if (null == mClassInfoItems)
            return;
        mClassInfoItems.clear();
    }
}
