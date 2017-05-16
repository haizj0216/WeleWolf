/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.buang.welewolf.modules.message.services;


import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

public class EMChatServiceObserver {

	private List<EMChatLoginListener> mChatLoginListeners;
	private List<EMNewMessageListener> mEmNewMessageListeners;
	private List<EMConnectionListener> mEmConnectionMessageListeners;
	
	public void addEMChatLoginListener(EMChatLoginListener listener){
		if(mChatLoginListeners == null){
			mChatLoginListeners = new ArrayList<EMChatLoginListener>();
		}
		if(!mChatLoginListeners.contains(listener))
			mChatLoginListeners.add(listener);
	}
	
	public void removeEMChatLoginListener(EMChatLoginListener listener){
		if(mChatLoginListeners == null)
			return;
		mChatLoginListeners.remove(listener);
	}
	
	public void notifyEMChatLoginSuccess(){
		if(mChatLoginListeners == null)
			return;
		for (int i = 0; i < mChatLoginListeners.size(); i++) {
			mChatLoginListeners.get(i).onSuccess();
		}
	}
	
	public void notifyEMChatLoginError(int code, String reason){
		if(mChatLoginListeners == null)
			return;
		for (int i = 0; i < mChatLoginListeners.size(); i++) {
			mChatLoginListeners.get(i).onError(code, reason);
		}
	}
	
	public void addEMNewMessageListener(EMNewMessageListener listener){
		if(mEmNewMessageListeners == null){
			mEmNewMessageListeners = new ArrayList<EMNewMessageListener>();
		}
		if(!mEmNewMessageListeners.contains(listener))
			mEmNewMessageListeners.add(listener);
	}
	
	public void removeEMNewMessageListener(EMNewMessageListener listener){
		if(mEmNewMessageListeners == null)
			return;
		mEmNewMessageListeners.remove(listener);
	}

	public void notifyNewApnsMessage(EMMessage message) {
		if(mEmNewMessageListeners == null)
			return;
		for (int i = 0; i < mEmNewMessageListeners.size(); i++) {
			mEmNewMessageListeners.get(i).onApnsMessage(message);
		}
	}
	
	public void notifyNewMessage(EMMessage message) {
		if(mEmNewMessageListeners == null)
			return;
		for (int i = 0; i < mEmNewMessageListeners.size(); i++) {
			mEmNewMessageListeners.get(i).onNewMessage(message);
		}
	}
	
	public void notifyClearMessage() {
		if(mEmNewMessageListeners == null)
			return;
		for (int i = 0; i < mEmNewMessageListeners.size(); i++) {
			mEmNewMessageListeners.get(i).onClearMessage();
		}
	}
	
	//
	public void addEMConnectionListener(EMConnectionListener listener){
		if(mEmConnectionMessageListeners == null){
			mEmConnectionMessageListeners = new ArrayList<EMConnectionListener>();
		}
		if(!mEmConnectionMessageListeners.contains(listener))
			mEmConnectionMessageListeners.add(listener);
	}
	
	public void removeEMConnectionListener(EMConnectionListener listener){
		if(mEmConnectionMessageListeners == null)
			return;
		mEmConnectionMessageListeners.remove(listener);
	}
	
	public void notifyEMConnectioned(){
		if(mEmConnectionMessageListeners == null)
			return;
		for (int i = 0; i < mEmConnectionMessageListeners.size(); i++) {
			mEmConnectionMessageListeners.get(i).onConnected();
		}
	}
	
	public void notifyEMDisConnection(int code){
		if(mEmConnectionMessageListeners == null)
			return;
		for (int i = 0; i < mEmConnectionMessageListeners.size(); i++) {
			mEmConnectionMessageListeners.get(i).onDisconnected(code);
		}
	}
}
