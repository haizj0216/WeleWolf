/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.base.database.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.knowbox.teacher.base.bean.OnlineSchoolTeacherInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 班级信息
 * @author yangzc
 *
 */
public class ClassInfoItem implements Parcelable {

	/**
	 * 
	 */
//	private static final long serialVersionUID = 606422874060350059L;
	public static final int STATE_OPENED = 0;
	public static final int STATE_CLOSED = 1;

	public static final int STATUS_TRANSFER_NONE = 0;
	public static final int STATUS_TRANSFER_SEND = 1;
	public static final int STATUS_TRANSFER_RECEIVE = 2;

	public static final int TEXT_WORD_TRAINING = 1;
	
	/**
	 * 班级id
	 */
	public String classId;
	/**
	 * 班级名称
	 */
	public String className;
	/**
	 * 学科
	 */
	public String subject;
	//科目ID
	public int subjectId;
	/**
	 * 班级代码
	 */
	public String classCode;
	/**
	 * 课程id
	 */
	public String courseId;
	/**
	 * 学生数量
	 */
	public int studentNum;
	/**
	 * 班级状态
	 */
	public int state;
	
	/**
	 * GroupId
	 */
	public String groupId;
	
	/**
	 * 是否更新
	 */
	public String isupdate;
	//年级
	public String grade;
	
	public String isProp;
	
	/**
	 * 创建时间
	 */
	public String createTime;

	public String mHeadPhoto;

	public int transferStatus;

	/*班级详情排行榜是否显示单词部落
	 * 0：显示练习日榜  1：显示单词部落
	 */
	public int typeMark;

	public OnlineSchoolTeacherInfo.TeacherInfo mTransferInfo;

	public List<NameValuePair> mStatisticData = new ArrayList<NameValuePair>();
	public List<HomeworkItem> mHomeworkItems = new ArrayList<HomeworkItem>();
	public int mCrontabHomeworkCount;
	public ClassInfoItem() {
		super();
	}

	protected ClassInfoItem(Parcel in) {
		classId = in.readString();
		className = in.readString();
		subject = in.readString();
		subjectId = in.readInt();
		classCode = in.readString();
		courseId = in.readString();
		studentNum = in.readInt();
		state = in.readInt();
		groupId = in.readString();
		isupdate = in.readString();
		grade = in.readString();
		isProp = in.readString();
		createTime = in.readString();
		mHeadPhoto = in.readString();
		transferStatus = in.readInt();
		typeMark = in.readInt();
	}

	public static final Creator<ClassInfoItem> CREATOR = new Creator<ClassInfoItem>() {
		@Override
		public ClassInfoItem createFromParcel(Parcel in) {
			return new ClassInfoItem(in);
		}

		@Override
		public ClassInfoItem[] newArray(int size) {
			return new ClassInfoItem[size];
		}
	};

	@Override
	public String toString() {
		return "ClassInfo [classId=" + classId + ", className=" + className
				+ ", subject=" + subject + ", classCode=" + classCode
				+ ", courseId=" + courseId + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(classId);
		dest.writeString(className);
		dest.writeString(subject);
		dest.writeInt(subjectId);
		dest.writeString(classCode);
		dest.writeString(courseId);
		dest.writeInt(studentNum);
		dest.writeInt(state);
		dest.writeString(groupId);
		dest.writeString(isupdate);
		dest.writeString(grade);
		dest.writeString(isProp);
		dest.writeString(createTime);
		dest.writeString(mHeadPhoto);
		dest.writeInt(transferStatus);
		dest.writeInt(typeMark);
	}

	public void parseItem(ClassInfoItem info, JSONObject infoJson) {
		int transferStatus = infoJson.optInt("transferStatus");

		if (transferStatus != 0 && infoJson.has("transferInfo")) {
			info.mTransferInfo = new OnlineSchoolTeacherInfo.TeacherInfo();
			info.mTransferInfo.parse(infoJson.optJSONObject("transferInfo"));
		}

		info.subjectId = infoJson.optInt("subjectCode");
		info.classId = infoJson.optString("classID");
		info.className = infoJson.optString("className");
		info.classCode = infoJson.optString("classCode");
		info.subject = infoJson.optString("subject");
		info.courseId = infoJson.optString("courseID");
		info.studentNum = infoJson.optInt("studentCount");
		info.createTime = infoJson.optString("createTime");
		info.transferStatus = transferStatus;
		info.state = infoJson.optInt("isClose");
		info.groupId = infoJson.optString("groupID");
		info.grade = infoJson.optString("grade");
		info.mHeadPhoto = infoJson.optString("image");
		info.typeMark = infoJson.optInt("typeMark");

		JSONArray statistic = infoJson.optJSONArray("statisticData");
		if (statistic != null) {
			for (int j = 0; j < statistic.length(); j++) {
				JSONObject statisticObject = statistic.optJSONObject(j);
				NameValuePair nameValuePair = new BasicNameValuePair(
						statisticObject.optString("itemKey"), statisticObject.optString("itemValue"));
				mStatisticData.add(nameValuePair);
			}
		}

		JSONArray homeworkList = infoJson.optJSONArray("homeworkList");
		if (homeworkList != null) {
			for (int m = 0; m < homeworkList.length(); m++) {
				JSONObject homework = homeworkList.optJSONObject(m);
				HomeworkItem item = new HomeworkItem();
				item.parseItem(item, homework);
				mHomeworkItems.add(item);
			}
		}
		mCrontabHomeworkCount = infoJson.optInt("crontabHomeworkCount");

	}
}
