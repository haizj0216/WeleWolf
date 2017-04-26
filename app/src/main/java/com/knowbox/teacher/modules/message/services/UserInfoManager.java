/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.message.services;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

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
	
	public void initConversation(String userId){
		EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
		if(conversation != null){
			for(int i=0; i< conversation.getMsgCount(); i++){
				EMMessage message = conversation.getMessage(i);
				UserInfo userInfo = new UserInfo();
				userInfo.mHeadPhoto = message.getStringAttribute("userPhoto", "");
				userInfo.mUserName = message.getStringAttribute("userName", "");
				updateUserInfo(userId, userInfo);
			}
		}
	}
	
	public static class UserInfo {
		public String mUserName;
		public String mHeadPhoto;
	}
}
