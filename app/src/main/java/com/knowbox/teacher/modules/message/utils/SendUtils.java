/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.message.utils;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.Utils;

import java.io.File;

/**
 * 发送消息通用类
 * @author yangzc
 */
public class SendUtils {

	private static void appendCommons(EMMessage message, ChatListItem item){
		if(item != null){
			UserItem user = Utils.getLoginUserItem();
			if(!TextUtils.isEmpty(user.userName))
				message.setAttribute("userName", user.userName);
			if(!TextUtils.isEmpty(user.headPhoto)){
				message.setAttribute("userPhoto", user.headPhoto);
			}
			
			if(!TextUtils.isEmpty(item.mUserName))
				message.setAttribute("toUserName", item.mUserName);
			if(!TextUtils.isEmpty(item.mHeadPhoto)){
				message.setAttribute("toUserPhoto", item.mHeadPhoto);
			}
		}
	}
	
	/**
	 * 发送文本
	 * @param userId
	 * @param content
	 */
	public static void sendText(String userId, ChatListItem item, String content) {
		if (content.length() > 0) {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			// 如果是群聊，设置chattype,默认是单聊
			if (item.isGroup())
				message.setChatType(ChatType.GroupChat);
			TextMessageBody txtBody = new TextMessageBody(content);
			// 设置消息body
			message.addBody(txtBody);
			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(userId);
			// 把messgage加到conversation中
			appendCommons(message, item);
			EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
			conversation.addMessage(message);
		}
	}
	
	/**
	 * 发送图片
	 * @param userId
	 * @param filePath
	 */
	public static void sendPicture(String userId, ChatListItem item, String filePath) {
		// create and add image message in view
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype,默认是单聊
		if (item.isGroup())
			message.setChatType(ChatType.GroupChat);
		message.setReceipt(userId);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		appendCommons(message, item);
		EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
		conversation.addMessage(message);
	}
	
	/**
	 * 发送图片
	 * @param userId
	 * @param selectedImage
	 */
	public static void sendPicByUri(String userId, ChatListItem item, Uri selectedImage) {
		Cursor cursor = BaseApp.getAppContext().getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			String picturePath = null;
			try {
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex("_data");
				picturePath = cursor.getString(columnIndex);
			} catch (Exception e) {
			} finally {
				cursor.close();
				cursor = null;
			}
			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(BaseApp.getAppContext(), "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(userId, item, picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(BaseApp.getAppContext(), "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(userId, item, file.getAbsolutePath());
		}
	}

	/**
	 * 发送表情
	 * @param userId
	 * @param item
	 * @param name
	 * @param identityCode
	 */
	public static void sendBigExpressionMessage(String userId, ChatListItem item, String name, String identityCode){
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);;
		if(identityCode != null){
			message.setAttribute("em_expression_id", identityCode);
		}
		if (item.isGroup())
			message.setChatType(ChatType.GroupChat);
		TextMessageBody txtBody = new TextMessageBody("["+name+"]");
		// 设置消息body
		message.addBody(txtBody);
		// 设置要发给谁,用户username或者群聊groupid
		message.setReceipt(userId);
		message.setAttribute("em_is_big_expression", true);
		appendCommons(message, item);
		EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
		conversation.addMessage(message);
	}

	/**
	 * 发送文件
	 * @param userId
	 * @param uri
	 */
	public static void sendFile(String userId, ChatListItem item, Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;
			try {
				cursor = BaseApp.getAppContext().getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			ToastUtils.showShortToast(BaseApp.getAppContext(), "文件不存在");
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			ToastUtils.showShortToast(BaseApp.getAppContext(), "文件不能大于10M");
			return;
		}
		// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		// 如果是群聊，设置chattype,默认是单聊
		if (item.isGroup())
			message.setChatType(ChatType.GroupChat);
		message.setReceipt(userId);
		// add message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
		message.addBody(body);
		appendCommons(message, item);
		EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
		conversation.addMessage(message);
	}
	
	/**
	 * 发送语音
	 * @param userId
	 * @param filePath
	 * @param length
	 */
	public static void sendVoice(String userId, ChatListItem item, String filePath, String length) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
			// 如果是群聊，设置chattype,默认是单聊
			if (item.isGroup())
				message.setChatType(ChatType.GroupChat);
			message.setReceipt(userId);
			int len = Integer.parseInt(length);
			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
			message.addBody(body);
			appendCommons(message, item);
			EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
			conversation.addMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送位置信息
	 * @param userId
	 * @param latitude
	 * @param longitude
	 * @param locationAddress
	 */
	public static void sendLocationMsg(String userId, ChatListItem item,
			double latitude, double longitude, String locationAddress) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
		// 如果是群聊，设置chattype,默认是单聊
		if (item.isGroup())
			message.setChatType(ChatType.GroupChat);
		LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
		message.addBody(locBody);
		message.setReceipt(userId);
		appendCommons(message, item);
		EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
		conversation.addMessage(message);
	}
	
	/**
	 * 发送视频消息
	 */
	public static void sendVideo(String userId, ChatListItem item, final String filePath,
								 final String thumbPath, final int length) {
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
		if(videoFile.length() > 10 * 1024 * 1024) {
			ToastUtils.showShortToast(BaseApp.getAppContext(), "发送视频超过大小限制，最大可发送10M的视频");
		}
		try {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
			// 如果是群聊，设置chattype,默认是单聊
			if (item.isGroup())
				message.setChatType(ChatType.GroupChat);
			message.setReceipt(userId);
			VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
			message.addBody(body);
			appendCommons(message, item);
			EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
			conversation.addMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 重发消息
	 * @param userId
	 */
	public static void resendMessage(String userId, EMMessage message) {
//		EMConversation conversation = EMChatManager.getInstance().getConversation(userId);
//		EMMessage msg = conversation.getMessage(resendPos);
		message.status = EMMessage.Status.CREATE;
	}
}
