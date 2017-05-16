package com.buang.welewolf.base.services.updateclass;

import com.buang.welewolf.base.database.bean.ClassInfoItem;

/**
 * Created by LiuYu on 2016/4/22.
 */
public interface ClassGroupsChangeListener {

    public void refreshClassGroupList();

    public void createClassGroupSuccess(ClassInfoItem classInfoItem);

    public void deleteClassGroupSuccess(ClassInfoItem classInfoItem);

    public void updateClassGroupSuccess(ClassInfoItem classInfoItem);

    public void onClassSelectChanged(ClassInfoItem classInfoItem);

}
