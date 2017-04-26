/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.base.database.bean;

import java.io.Serializable;

public class ChatListItem extends BaseItem implements Serializable {

	public static final int USER_TYPE_PUBLIC = 0;
	public static final int USER_TYPE_TEACHER = 1;
	public static final int USER_TYPE_STUDENT = 2;
	public static final int USER_TYPE_SYSTEM = 3;
	public static final int USER_TYPE_GROUP = 4;
	public static final int USER_TYPE_SELFTRAIN = 21;

	public static final String ITEM_MESSAGE_HOMEWORK = "1";
	public static final String ITEM_GOOD_HOMEWORK = "2";
	public static final String ITEM_BUSINESS_HOMEWORK = "3";
	public static final String ITEM_SHARE_HOMEWORK = "4";

	/**
	 * 
	 */
	private static final long serialVersionUID = 3641361665218612356L;
	public String mUserId;
	public String mUserName;
	public String mHeadPhoto;
	public int mSubjectCode;
	public int mUserType = -1;
	public boolean isDisabled = false;
	public long lastTime;
	
	/**
	 * 是否是公众账号
	 * @return
	 */
	public boolean isPublic(){
		return mUserType == USER_TYPE_PUBLIC;
	}

	public boolean isNotice() {
		return mUserType == USER_TYPE_SYSTEM;
	}

	/**
	 * 是否是组群
	 * @return
	 */
	public boolean isGroup(){
		return mUserType == USER_TYPE_GROUP;
	}

	public boolean isSelfTrain() {
		return  mUserType == USER_TYPE_SELFTRAIN;
	}
}
