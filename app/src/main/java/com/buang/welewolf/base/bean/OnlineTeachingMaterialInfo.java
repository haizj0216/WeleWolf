/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.apache.commons.httpclient.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OnlineTeachingMaterialInfo extends BaseObject implements Serializable {
	private static final long serialVersionUID = -3855112595836568566L;

//	public static final int QUESTION_LIBRARY_TYPE_KNOWBOX = 0;
//	public static final int QUESTION_LIBRARY_TYPE_MINE = 1;
//	public static final int QUESTION_LIBRARY_TYPE_SCHOOL = 2;
//	public static final int QUESTION_LIBRARY_TYPE_ZONE = 3;

	public List<ChooseItem> mChooseItemPairs;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		if (isAvailable()) {
			JSONArray questionList = json.optJSONArray("list");
			if(questionList == null) {
				JSONObject data = json.optJSONObject("data");
				questionList = data.optJSONArray("list");
			}
			mChooseItemPairs = new ArrayList<ChooseItem>();
			for (int i = 0; i < questionList.length(); i++) {
				ChooseItem teach = new ChooseItem();
				teach.mType = ChooseItem.ITEM_TYPE_TEACH;
				JSONObject teachJson = questionList.optJSONObject(i);
				if (teachJson != null) {
					teach.setName(teachJson.optString("editionName"));
					teach.setValue(teachJson.optString("editionID"));
					List<ChooseItem> subList = new ArrayList<ChooseItem>();
					JSONArray textBookList = teachJson.optJSONArray("list");
					for (int j = 0; j < textBookList.length(); j++) {
						ChooseItem textbook = new ChooseItem();
						textbook.mType = ChooseItem.ITEM_TYPE_BOOK;
						textbook.mParent = teach;
						JSONObject textbookJson = textBookList.optJSONObject(j);
//						if (textbookJson != null) {
							textbook.setName(textbookJson.optString("bookName"));
							textbook.setValue(textbookJson.optString("bookID"));
							/*List<ChooseItem> subSubList = new ArrayList<ChooseItem>();
							JSONArray assistList = textbookJson.optJSONArray("list");
							for (int k = 0; k < assistList.length(); k++) {
								ChooseItem assist = new ChooseItem();
								ChooseItem subType = new ChooseItem();
								assist.mType = ChooseItem.ITEM_TYPE_ASSIST;
								assist.mParent = textbook;
								JSONObject assistJson = assistList.optJSONObject(k);
								if (assistJson != null) {
									assist.setName(assistJson.optString("teachingAssistName"));//最终的课本
									assist.setValue(assistJson.optString("teachingAssistID"));
									assist.isArea = assistJson.optInt("isArea");
									assist.isSync = assistJson.optInt("isSync") == 1;
								}
								subSubList.add(assist);
							}
							textbook.mSubList = subSubList;*/
//						}
						subList.add(textbook);
					}
					teach.mSubList = subList;
				}
				mChooseItemPairs.add(teach);
			}
		}
	}

	public static class ChooseItem extends NameValuePair implements Serializable{
		private static final long serialVersionUID = -3707425100673291728L;
		public static final int ITEM_TYPE_TEACH = 0;
		public static final int ITEM_TYPE_BOOK = 1;
		public static final int ITEM_TYPE_ASSIST = 2;
		public int mType;
		public int isArea;
		public ChooseItem mParent;
		public boolean isSync;
		public List<ChooseItem> mSubList = new ArrayList<ChooseItem>();
		
		public ChooseItem() {
			
		}
		
		public ChooseItem(String name, String value) {
			setName(name);
			setValue(value);
		}
	}

}
