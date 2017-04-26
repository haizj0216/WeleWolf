package com.knowbox.teacher.base.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by weilei on 15/12/29.
 */
public class OnlineSectionInfo implements Serializable {

    private static final long serialVersionUID = 5896384631933057208L;

    public static final int TYPE_CHAPTER = 0;//章
    public static final int TYPE_SECTION = 1;//节
    public static final int TYPE_KNOWLEDGE = 2;//知识点

    public String mSectionId;//分组ID
    public String mSectionName;//分组名称
    public int mSectionType;//节点信息
    public boolean hasQuestion;
    public OnlineSectionInfo mParentSection;//父分组
    public List<OnlineSectionInfo> mSubSections;//子分组
    public int mCount;
    public String mParentId;
    public String mLevel;

    @Override
    public boolean equals(Object o) {
        if (o instanceof OnlineSectionInfo) {
            OnlineSectionInfo item = (OnlineSectionInfo) o;
            return mSectionId.equalsIgnoreCase(item.mSectionId);
        }
        return super.equals(o);
    }
}
