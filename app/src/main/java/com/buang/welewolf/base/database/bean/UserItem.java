/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.database.bean;

/**
 * 当前登录用户信息
 * @author yangzc
 *
 */
public class UserItem extends BaseItem{

	/*
     * 认证状态
     * certificateStatus 0 未认证，1认证成功，2认证失败,3审核中
     */
	public static final int AUTHENTICATION_NOT_YET = 0;
	public static final int AUTHENTICATION_SUCCESS = 1;
	public static final int AUTHENTICATION_FAIL = 2;
	public static final int AUTHENTICATION_UNDER_WAY = 3;
	
	//用户ID
	public String userId;
	//用户登录名
	public String loginName;
	//用户昵称
	public String userName;
	//学科
	public String subject;
	//学科Code[数学：0 语文:1 英语:2 物理:3 化学：4 生物：5 历史：6 地理：7 政治：8]
	public String subjectCode = "0";
	//任职学校
	public String school;
	public String city;
	//学校名称
	public String schoolName;
	//密码
	public String password;
	//唯一token
	public String token;
	//头像
	public String headPhoto;
	//性别
	public String sex;
	//生日
	public String birthday;
	//年级
	public String gradePart;
	//真实姓名(yu)
	public String realName;
	//教师资格证编号（yu）
	public String documentNumber;
	//认证状态(yu)
	public String authenticationStatus = "0";

	public String idCard;

	public String authImage;

	public String authError;

	public String mCertTime;

	public String mEditionId;

	public String mEditionName;

	public String mBookId;

	public String mBookName;

	public boolean mIsRecommend;


    //(yu)
	@Override
	public String toString() {
		return "UserItem{" +
				"userId='" + userId + '\'' +
				", loginName='" + loginName + '\'' +
				", userName='" + userName + '\'' +
				", subject='" + subject + '\'' +
				", subjectCode='" + subjectCode + '\'' +
				", school='" + school + '\'' +
				", city='" + city + '\'' +
				", schoolName='" + schoolName + '\'' +
				", password='" + password + '\'' +
				", token='" + token + '\'' +
				", headPhoto='" + headPhoto + '\'' +
				", sex='" + sex + '\'' +
				", birthday='" + birthday + '\'' +
				", gradePart='" + gradePart + '\'' +
				", realName='" + realName + '\'' +
				", documentNumber='" + documentNumber + '\'' +
				", authenticationStatus='" + authenticationStatus + '\'' +
				'}';
	}
}
