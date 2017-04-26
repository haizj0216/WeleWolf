/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.teacher.modules.message.services;


import com.hyena.framework.servcie.BaseService;

public interface EMChatService extends BaseService {

	public static final String SERVICE_NAME = "com.knowbox.wb.student_emchatservice";

	public static final String SERVICE_ID = "10";//客服id
	
	public boolean initEMChat();
	
	public void loginEMChat();
	
	public void logoutEMChat();
	
	public void setCurrentUserId(String userId);
	
	public String getCurrentUserId();
	
	public EMChatServiceObserver getObserver();
}
