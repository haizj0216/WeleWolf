/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业分组信息
 * @author yangzc
 *
 */
public class OnlineHomeworkSectionsInfo extends BaseObject implements Serializable{

    private static final boolean DEBUG = false;
	private static final long serialVersionUID = -435322133052480078L;
	public List<SectionInfo> mSections;
	public int mQuestionCount;
	
	@Override
	public void parse(JSONObject json) {
        super.parse(json);
        if(DEBUG){
            setErrorCode(OK);
            mSections = new ArrayList<SectionInfo>();
            for (int i = 0; i < 10; i++) {
                SectionInfo chapter = new SectionInfo();
                mSections.add(chapter);
                chapter.mSectionName = "Chapter " + i;
                chapter.mSectionType = SectionInfo.TYPE_CHAPTER;
                chapter.mSubSections = new ArrayList<SectionInfo>();
                for (int j = 0; j < 5; j++) {
                    SectionInfo section = new SectionInfo();
                    chapter.mSubSections.add(section);
                    section.mParentSection = chapter;
                    section.mSectionName = "Section " + j;
                    section.mSectionType = SectionInfo.TYPE_SECTION;
                    section.mSubSections = new ArrayList<SectionInfo>();
                    for (int k = 0; k < 3; k++) {
                        SectionInfo knowledge = new SectionInfo();
                        section.mSubSections.add(knowledge);
                        knowledge.mParentSection = section;
                        knowledge.mSectionName = "Knowledge" + k;
                        knowledge.mSectionType = SectionInfo.TYPE_KNOWLEDGE;
                        knowledge.hasQuestion = true;
                    }
                }
            }
            return;
        }
        
        if(isAvailable()){
	        mSections = new ArrayList<OnlineHomeworkSectionsInfo.SectionInfo>();
	        JSONArray array = json.optJSONArray("list");
	        if(array == null) {
	        	JSONObject data = json.optJSONObject("data");
				if (data != null) {
					array = data.optJSONArray("list");
					mQuestionCount = data.optInt("questionNum");
				}
	        }

			if (array == null) {
				array = json.optJSONArray("data");
			}
	        
	        getSectionList(array, mSections, null);
        }
	}
	
	private void getSectionList(JSONArray sectionItems, List<SectionInfo> sectionList, SectionInfo parent){
        if(sectionItems != null){
        	for (int i = 0; i < sectionItems.length(); i++) {
				JSONObject section = sectionItems.optJSONObject(i);
				if(section != null){
					SectionInfo info = new SectionInfo();
					String sectionId = section.optString("courseSectionID");
					if (TextUtils.isEmpty(sectionId)) {
						sectionId = section.optString("knowID");
					}
					if (TextUtils.isEmpty(sectionId)) {
						sectionId = section.optString("issueID");
					}
					String name = section.optString("sectionName");
					if (TextUtils.isEmpty(name)){
						name = section.optString("knowledgeName");
					}
					if (TextUtils.isEmpty(name)){
						name = section.optString("issueName");
					}
					String type = section.optString("type");
					if(TextUtils.isEmpty(type))
						type = section.optString("level");
					int count;
					if(section.has("count")){
						count = section.optInt("count");
					} else {
						String questionNum = section.optString("questionNum");
						if(TextUtils.isEmpty(questionNum))
							count = 0;
						else
							count = Integer.parseInt(questionNum);
					}
					String parentId = section.optString("parentID");
					info.mParentId = parentId;
					info.mSectionId = sectionId;
					info.mSectionName = name;
					info.mParentSection = parent;
					info.mCount = count;
					int sectionType = SectionInfo.TYPE_KNOWLEDGE;
					if("1".equals(type)){
						sectionType = SectionInfo.TYPE_CHAPTER;
					}else if("2".equals(type)){
						sectionType = SectionInfo.TYPE_SECTION;
					}else if("3".equals(type)){
						info.hasQuestion = true;
						sectionType = SectionInfo.TYPE_KNOWLEDGE;
					}
					info.mSectionType = sectionType;
					info.mSubSections = new ArrayList<OnlineHomeworkSectionsInfo.SectionInfo>();
					sectionList.add(info);
					
					JSONArray array = section.optJSONArray("list");
					getSectionList(array, info.mSubSections, info);
				}
			}
        }
	}
	
	/**
	 * 分组信息
	 * @author yangzc
	 */
	public static class SectionInfo implements Serializable{

		public static final int TYPE_CHAPTER = 0;//章
		public static final int TYPE_SECTION = 1;//节
		public static final int TYPE_KNOWLEDGE = 2;//知识点
		private static final long serialVersionUID = -6088486098590090760L;

		public String mSectionId;//分组ID
		public String mSectionName;//分组名称
		public int mSectionType;//节点信息
		public boolean hasQuestion;
		public SectionInfo mParentSection;//父分组
		public List<SectionInfo> mSubSections;//子分组
		public List<OnlineGroupItemInfo> mGroups;//章节下题包
		public int mCount;
		public String mParentId;
		public String mLevel;
		public String mOrderNum;
		public boolean isExpandable;
		public boolean isNull;
		public String mNullDesc;
		public boolean isUnset;//是否设置了teachingid和jiaocaiid

		@Override
		public boolean equals(Object o) {
			if (o instanceof SectionInfo) {
				SectionInfo item = (SectionInfo) o;
				return mSectionId.equalsIgnoreCase(item.mSectionId);
			}
			return super.equals(o);
		}
	}
}
