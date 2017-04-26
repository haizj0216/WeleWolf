/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.base.bean;

import android.text.TextUtils;

import com.hyena.framework.datacache.BaseObject;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.utils.ConstantsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线登录信息
 * @author yangzc
 */
public class OnlineLoginInfo extends BaseObject {
	
	public UserItem mUserItem;
	public List<ClassInfoItem> mClassItems;
	public String mMessage;
	//{"code":99999,"message":"success","requestId":"58a7eeb68cfae242","data":[]}
	
	@Override
	public void parse(JSONObject data) {
		super.parse(data);
		if (isAvailable()) {
			JSONObject json = data.optJSONObject("data");
			if (json == null) {
				return;
			}
			String userid = json.optString("userID");
			String token = json.optString("token");
			String userName = json.optString("userName");
			String sex = json.optString("sex");
			String birthday = json.optString("birthday");
			String school = json.optString("schoolID");
			String headPhoto = json.optString("headPhoto");
			String subject = json.optString("subject");
			String subjectCode = json.optString("subjectCode");
			String gradePart = json.optString("gradePart");
			if (gradePart.equals("0")) {
				gradePart = "";
			}
			//认证相关
			String realName = json.optString("realName");
			String certificateStatus = json.optString("certificateStatus");
			String certificateError = json.optString("certificateError");
			String certificateImg = json.optString("certificateImg");
			String certificateCode = json.optString("certificateCode");
			String idCard = json.optString("idCard");
			String schoolName = json.optString("schoolName");
			if (TextUtils.isEmpty(schoolName)) {
				schoolName = json.optString("school");
			}
			String certificateTime = json.optString("certificateTime");
			String mobile = json.optString("mobile");
			String jiaoCaiId = json.optString("bookID");
			if (jiaoCaiId.equals("0")) {
				jiaoCaiId = "";
			}
			String teachingName = json.optString("editionName");
			if (teachingName.equals("null")) {
				teachingName = "";
			}
			String teachingID = json.optString("editionID");
			if (teachingID.equals("0")) {
				teachingID = "";
			}
			String jiaoCaiName = json.optString("bookName");
			if (jiaoCaiName.equals("null")){
				jiaoCaiName = "";
			}
			int isRecommend = json.optInt("is_recommend");

			mUserItem = new UserItem();
			mUserItem.userId = userid;
			mUserItem.userName = userName;
			mUserItem.sex = sex;
			mUserItem.birthday = birthday;
			mUserItem.headPhoto = headPhoto;
			mUserItem.school = school;
			mUserItem.schoolName = schoolName;
			mUserItem.token = token;
//			mUserItem.subjectCode = subject;
			mUserItem.gradePart = gradePart;
			//认证相关
			mUserItem.authenticationStatus = certificateStatus;
			mUserItem.realName = realName;
			mUserItem.documentNumber = certificateCode;
			mUserItem.authError = certificateError;
			mUserItem.authImage = certificateImg;
			mUserItem.idCard = idCard;
			mUserItem.mCertTime = certificateTime;
			mUserItem.mEditionId = teachingID;
			mUserItem.mEditionName = teachingName;
			mUserItem.mBookName = jiaoCaiName;
			mUserItem.mBookId = jiaoCaiId;
			mUserItem.mIsRecommend = isRecommend == 1 ? true : false;

			if (!TextUtils.isEmpty(mobile)) {
				mUserItem.loginName = mobile;
			}

			if (json.has("isNew")) {
				boolean isNew = json.optInt("isNew") == 1 ? true :false;
				PreferencesController.setBoolean(ConstantsUtils.PREFS_ISNEW_TEACHER, isNew);
			}

			if(json.has("classList")){
				JSONArray bArray = json.optJSONArray("classList");
				mClassItems = new ArrayList<ClassInfoItem>();
				for (int i = 0; i < bArray.length(); i++) {
					ClassInfoItem info = new ClassInfoItem();
					JSONObject bitemJson = bArray.optJSONObject(i);
					String classID = bitemJson.optString("classID");
					String classCode = bitemJson.optString("classCode");
					String className = bitemJson.optString("className");
					String _subject = bitemJson.optString("subject");
					String courseID = bitemJson.optString("courseID");
					int studentNum = bitemJson.optInt("studentCount");
					int isClose = bitemJson.optInt("isClose");
					String groupId = bitemJson.optString("groupID");
					String grade = bitemJson.optString("grade");
					String createTime = bitemJson.optString("createTime");
					String image = bitemJson.optString("image");
					
					info.subjectId = bitemJson.optInt("subjectCode");
					info.classId = classID;
					info.className = className;
					info.classCode = classCode;
					info.subject = _subject;
					info.courseId = courseID;
					info.studentNum = studentNum;
					info.groupId = groupId;
					info.grade = grade;
					info.createTime = createTime;
					info.mHeadPhoto = image;
					info.state = isClose;
					mClassItems.add(info);
				}
			}
		}else{
			String result = data.optString("code");
			if (result.equals("failure")) {
				mMessage = data.optString("message");
			}else if (result.equals("20014")) {
				//mMessage = "邀请码错误";
				JSONObject errorJson = data.optJSONObject("data");
				mMessage = errorJson.optString("msg");
			}
		}
	}
}
