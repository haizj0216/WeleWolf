package com.knowbox.teacher.base.bean;

import java.io.Serializable;

/**
 * @name 作业基本信息
 * @author Fanjb
 * @date 2015年6月2日
 */
public class HomeworkBasicInfo implements Serializable {
	public static final long serialVersionUID = 3007738282982208L;
	public String mStudentName;
	public String mStudentId;
	public String mClassId;
	public int mSubjectCode;
	public String mHomeworkId;
	public long mIsDate;
	public String mAnswerId;
	public String mQuestionID;
}