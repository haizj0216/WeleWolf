/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.buang.welewolf.modules.message.services;


import com.hyphenate.chat.EMMessage;

public interface EMNewMessageListener {

	public void onNewMessage(EMMessage message);
	
	public void onClearMessage();

	public void onApnsMessage(EMMessage message);
	
}
