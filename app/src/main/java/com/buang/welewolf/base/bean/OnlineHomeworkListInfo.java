/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.HomeworkItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业概览数据
 * @author yangzc
 */
public class OnlineHomeworkListInfo extends BaseObject {

	public List<HomeworkItem> mHomeworkItems = new ArrayList<HomeworkItem>();
	public List<HomeworkItem> mNoPublishItems = new ArrayList<HomeworkItem>();
	public List<HomeworkItem> mCorrectItems = new ArrayList<HomeworkItem>();
	public boolean hasMore;

	@Override
	public void parse(JSONObject data) {
		super.parse(data);
		if(isAvailable()){
			JSONObject jsonData = data.optJSONObject("data");
			if (jsonData == null) {
				mNoPublishItems.clear();
				JSONArray homeworkArray = data.optJSONArray("data");
				for(int i=0; i< homeworkArray.length(); i++){
					JSONObject item = homeworkArray.optJSONObject(i);
					if(item != null){

						HomeworkItem crontabItem = new HomeworkItem();
						crontabItem.mHomeworkTitle = item.optString("homeworkTitle");
						crontabItem.mCrontabId = item.optString("crontabID");
						crontabItem.mCreateTs = item.optLong("addTime");
						crontabItem.mClassName = item.optString("className");
						crontabItem.mDeadLineTs = item.optLong("endTime");
						crontabItem.mClassId = item.optString("classID");
						crontabItem.mPublishTs = item.optLong("publishTime");
						crontabItem.mQuestionNum = item.optString("questionNum");
						crontabItem.mIsCrontab = true;
						mNoPublishItems.add(crontabItem);

					}
				}
				return;
			}
			mHomeworkItems.clear();
			mCorrectItems.clear();
			if(jsonData != null){
				JSONArray crontabArray = jsonData.optJSONArray("correctingList");
				if (crontabArray != null) {
					for(int i=0; i< crontabArray.length(); i++){
						JSONObject item = crontabArray.optJSONObject(i);
						if(item != null){
							HomeworkItem homework = new HomeworkItem();
							homework.parseItem(homework, item);
							homework.mIsCrontab = false;
							homework.mIsCorrectTab = true;
							homework.lastMonthCorrected = true;
							mCorrectItems.add(homework);
						}
					}
				}

				JSONArray homeworkArray = jsonData.optJSONArray("homeworkList");
				if (homeworkArray != null) {
					for(int i=0; i< homeworkArray.length(); i++){
						JSONObject item = homeworkArray.optJSONObject(i);
						if(item != null){
							HomeworkItem homework = new HomeworkItem();
							homework.parseItem(homework, item);
							homework.mIsCrontab = false;
							mHomeworkItems.add(homework);
						}
					}
				}


			}
		}
	}
}
