package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.database.bean.ClassInfoItem;

import org.json.JSONObject;

public class OnlineAddClassInfo extends BaseObject {

	public ClassInfoItem info;
	public String mMessage;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		if (isAvailable()) {
			if (json.has("data")) {
				json = json.optJSONObject("data");
			}
			info = new ClassInfoItem();
			String classID = json.optString("classID");
			String classCode = json.optString("classCode");
			String className = json.optString("className");
			String subject = json.optString("subject");
			String courseID = json.optString("courseID");
			int studentNum = json.optInt("studentCount");
			int isClose = json.optInt("isClose");
			String createTime = json.optString("createTime");
			String groupId = json.optString("groupID");
			String grade = json.optString("grade");
			String image = json.optString("image");

			// TODO 有可能classId classCode courseId都为空的情况
			info.classId = classID;
			info.className = className;
			info.classCode = classCode;
			info.subject = subject;
			info.courseId = courseID;
			info.studentNum = studentNum;
			info.createTime = createTime;
			info.subjectId = json.optInt("subjectCode");
			info.state = isClose;
			info.groupId = groupId;
			info.grade = grade;
			info.mHeadPhoto = image;
		} else {
			mMessage = json.optString("message");
		}
	}

}
