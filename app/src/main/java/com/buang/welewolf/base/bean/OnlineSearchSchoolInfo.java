/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.buang.welewolf.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineSearchSchoolInfo extends BaseObject {

    public List<PinyinIndexModel> mSchools;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (isAvailable()) {
//			JSONArray listItems = json.optJSONArray("list");
//			if (listItems == null) {
            JSONArray listItems = json.optJSONArray("data");
//			}
            if (listItems != null) {
                mSchools = new ArrayList<PinyinIndexModel>();
                for (int i = 0; i < listItems.length(); i++) {
                    JSONObject item = listItems.optJSONObject(i);
//					if (item != null) {
                    PinyinIndexModel school = new PinyinIndexModel(
                            item.optString("schoolID"),
                            item.optString("schoolName"));
                    school.setPinyin(item.optString("pinyin"));
                    String firstPinyin = item.optString("firstPinyin");
                    school.setShortPinyin(firstPinyin);
                    String firstLetter = item.optString("firstLetter");
                    if (TextUtils.isEmpty(firstLetter) && !TextUtils.isEmpty(firstPinyin)) {
                        firstLetter = firstPinyin.substring(0, 1);
                    }
                    // 正则表达式，判断首字母是否是英文字母
                    if (firstLetter.matches("[a-zA-Z]")) {
                        firstLetter = firstLetter.toUpperCase();
                        school.setFirstLetter(firstLetter);
                    } else {
                        school.setFirstLetter("#");
                    }
                    mSchools.add(school);
//					}
                }
            }
        }
    }
}
