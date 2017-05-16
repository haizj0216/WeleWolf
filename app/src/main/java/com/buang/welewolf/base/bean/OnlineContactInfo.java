package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 联系人基本信息
 * @author Fanjb
 * @date 2015-3-18
 */
public class OnlineContactInfo extends BaseObject {

	public String studentName;
	public String headPhoto;
	public String school;
	public String sex;
	public List<ClassInfoItem> classItems;

	@Override
	public void parse(JSONObject data) {
		super.parse(data);
		if (isAvailable()) {
			JSONObject json = data.optJSONObject("data");
			studentName = json.optString("userName");
			headPhoto = json.optString("headPhoto");
			school = json.optString("schoolName");
			sex = json.optString("sex");
			if (json.has("classList")) {
				JSONArray array = json.optJSONArray("classList");
				classItems = new ArrayList<ClassInfoItem>();
				try {
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						ClassInfoItem item = new ClassInfoItem();
						item.classId = object.optString("classID");
						item.className = object.optString("className");
						item.subjectId = object.optInt("subjectCode");
						item.grade = object.optString("grade");
						classItems.add(item);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
