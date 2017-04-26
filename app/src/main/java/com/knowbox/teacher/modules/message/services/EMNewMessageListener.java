/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.teacher.modules.message.services;

import com.easemob.chat.EMMessage;

public interface EMNewMessageListener {

	public void onNewMessage(EMMessage message);
	
	public void onClearMessage();

	public void onApnsMessage(EMMessage message);
	
}
