package com.knowbox.teacher.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.base.bean.OnlineHomeworkSectionsInfo.SectionInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineHomeworkKnownlegeInfo extends BaseObject {
	public List<SectionInfo> mSections;

	public void parse(JSONObject json) {
		if (isAvailable()) {
			mSections = new ArrayList<OnlineHomeworkSectionsInfo.SectionInfo>();
			JSONArray array = json.optJSONArray("list");
			if (array == null) {
				JSONObject data = json.optJSONObject("data");
				array = data.optJSONArray("list");
			}
			getSectionList(array, mSections, null);
		}
	};

	private void getSectionList(JSONArray sectionItems,
			List<SectionInfo> sectionList, SectionInfo parent) {
		if (sectionItems != null) {
			for (int i = 0; i < sectionItems.length(); i++) {
				JSONObject section = sectionItems.optJSONObject(i);
				if (section != null) {
					SectionInfo info = new SectionInfo();
					String sectionId = section.optString("knowID");
					String name = section.optString("knowledgeName");
					String type = section.optString("type");
					if(TextUtils.isEmpty(type))
						type = section.optString("level");
					int count = 0;
					if(section.has("count")){
						count = section.optInt("count");
					} else {
						String questionNum = section.optString("questionNum");
						if(TextUtils.isEmpty(questionNum))
							count = 0;
						else
							count = Integer.parseInt(questionNum);
					}
					info.mSectionId = sectionId;
					info.mSectionName = name;
					info.mParentSection = parent;
					info.mCount = count;
					int sectionType = SectionInfo.TYPE_KNOWLEDGE;
					if ("1".equals(type)) {
						sectionType = SectionInfo.TYPE_CHAPTER;
					} else if ("2".equals(type)) {
						sectionType = SectionInfo.TYPE_SECTION;
					} else if ("3".equals(type)) {
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
}
