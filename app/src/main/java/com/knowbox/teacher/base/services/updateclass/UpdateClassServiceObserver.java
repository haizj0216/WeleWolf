package com.knowbox.teacher.base.services.updateclass;

import com.knowbox.teacher.base.database.bean.ClassInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 2016/4/22.
 */
public class UpdateClassServiceObserver {

    private List<ClassGroupsChangeListener> mClassGroupsChangeListeners = new ArrayList<ClassGroupsChangeListener>();

    public void addClassGroupChangeListener(ClassGroupsChangeListener listener) {
        if (null == mClassGroupsChangeListeners)
            mClassGroupsChangeListeners = new ArrayList<ClassGroupsChangeListener>();

        if (mClassGroupsChangeListeners.contains(listener))
            return;

        mClassGroupsChangeListeners.add(listener);
    }

    public void removeClassGroupChangeListener(ClassGroupsChangeListener listener) {
        if (null == mClassGroupsChangeListeners)
            return;
        mClassGroupsChangeListeners.remove(listener);
    }

    public void notifyOnRefreshClassList() {
        if (null == mClassGroupsChangeListeners)
            return;
        for (ClassGroupsChangeListener listener: mClassGroupsChangeListeners) {
            listener.refreshClassGroupList();
        }
    }

    public void notifyOnCreateClass(ClassInfoItem classInfoItem) {
        if (null == mClassGroupsChangeListeners)
            return;
        for (ClassGroupsChangeListener listener: mClassGroupsChangeListeners) {
            listener.createClassGroupSuccess(classInfoItem);
        }
    }

    public void notifyOnDeleteClass(ClassInfoItem classInfoItem) {
        if (null == mClassGroupsChangeListeners)
            return;
        for (ClassGroupsChangeListener listener: mClassGroupsChangeListeners) {
            listener.deleteClassGroupSuccess(classInfoItem);
        }
    }

    public void notifyOnUpdatedClass(ClassInfoItem classInfoItem) {
        if (null == mClassGroupsChangeListeners)
            return;
        for (ClassGroupsChangeListener listener: mClassGroupsChangeListeners) {
            listener.updateClassGroupSuccess(classInfoItem);
        }
    }

    public void notifyClassSelected(ClassInfoItem classInfoItem) {
        if (null == mClassGroupsChangeListeners)
            return;
        for (ClassGroupsChangeListener listener: mClassGroupsChangeListeners) {
            listener.onClassSelectChanged(classInfoItem);
        }
    }
}
