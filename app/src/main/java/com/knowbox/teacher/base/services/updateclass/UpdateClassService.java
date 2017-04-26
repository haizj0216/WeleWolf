package com.knowbox.teacher.base.services.updateclass;

import com.hyena.framework.servcie.BaseService;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;

import java.util.List;

/**
 * Created by LiuYu on 2016/4/22.
 */
public interface UpdateClassService extends BaseService {

    public static final String SERVICE_NAME = "com.knowbox.wb_updateclasses";

    public UpdateClassServiceObserver getObserver();

    public void addAllClassItem(List<ClassInfoItem> classInfoItems);

    public void clearAllClassItems();

    public void addClassItem(ClassInfoItem classInfoItem);

    public void removeClassItem(String classId);

    public void updateClassItem(ClassInfoItem classInfoItem);

    public ClassInfoItem getClassInfoItem(String classId);

    public List<ClassInfoItem> getAllClassInfoItem();

    public ClassInfoItem getClassInfoByGroupId(String groupId);

    public int getClassInfoItemIndex(String classId);

    public int getClassInfoIndexByGroup(String groupId);

    public void updateClassInfo();

    public void updateClassCount(String classId, int count);

}
