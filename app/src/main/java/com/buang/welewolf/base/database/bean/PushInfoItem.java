/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.database.bean;

/**
 * 推送信息
 * @author yangzc
 */
public class PushInfoItem {
	
	public static final String MSG_TYPE_JOIN_CLASS = "3";

	public String classId;
	public String pushNum;
	public String content;
	public String homeWorkId;

	public String memo;
	public String msgID;
	public String msgType;
	public String studentID;
	public String studentName;

	@Override
	public String toString() {
		return "PushInfo [classId=" + classId + ", pushNum=" + pushNum
				+ ", content=" + content + ", homeWorkId=" + homeWorkId
				+ ", memo=" + memo + ", msgID=" + msgID + ", msgType="
				+ msgType + ", studentID=" + studentID + ", studentName="
				+ studentName + "]";
	}

}
