/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.message.services;


import java.util.HashMap;

public class UserInfoManager {

	private static UserInfoManager instance;
	
	private HashMap<String, UserInfo> mUserMap;
	
	private UserInfoManager(){
		mUserMap = new HashMap<String, UserInfo>();
	}
	
	public static UserInfoManager getInstance(){
		if(instance == null)
			instance = new UserInfoManager();
		return instance;
	}
	
	public UserInfo getUserInfo(String userId){
		return mUserMap.get(userId);
	}
	
	public void updateUserInfo(String userId, 
			UserInfo userInfo){
		mUserMap.put(userId, userInfo);
	}
	
	public static class UserInfo {
		public String mUserName;
		public String mHeadPhoto;
	}
}
