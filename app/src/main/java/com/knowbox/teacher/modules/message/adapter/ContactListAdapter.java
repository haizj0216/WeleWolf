/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.teacher.modules.message.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.modules.message.utils.Constant;
import com.knowbox.teacher.modules.message.utils.SmileUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.widgets.RoundDisplayer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class ContactListAdapter extends SingleTypeAdapter<ChatListItem> {

	public ContactListAdapter(Context context) {
		super(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.layout_message_row_chat_history, null);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.mDisabled = (ImageView) convertView.findViewById(R.id.msg_disabled);
			holder.mDisableTip = (ImageView) convertView.findViewById(R.id.unread_msg_disabled);
			convertView.setTag(holder);
		}
		
		// 获取与此用户/群组的会话
		final ChatListItem item = getItem(position);
		if(item.isNotice()){
			ImageFetcher.getImageFetcher().loadImage(item.mHeadPhoto, holder.avatar,
					R.drawable.icon_msg_notice, new RoundDisplayer());
		}else if(item.isGroup()){
			//TODO
			ImageFetcher.getImageFetcher().loadImage(item.mHeadPhoto, holder.avatar, 
					R.drawable.icon_class_default, new RoundDisplayer());
		} else if (item.isSelfTrain()) {
			ImageFetcher.getImageFetcher().loadImage(item.mHeadPhoto, holder.avatar,
					R.drawable.icon_self_train, new RoundDisplayer());
		}else{
			ImageFetcher.getImageFetcher().loadImage(item.mHeadPhoto, holder.avatar, 
					R.drawable.bt_message_default_head, new RoundDisplayer());
		}
		if(TextUtils.isEmpty(item.mUserName)){
			holder.name.setText("未知名称");
		}else{
			holder.name.setText(item.mUserName);
		}
		
		EMConversation conversation = getConversation(item.mUserId);
		if (conversation != null && conversation.getUnreadMsgCount() > 0 && !item.isDisabled) {
			if (item.isNotice()) {
				holder.unreadLabel.setBackgroundResource(R.drawable.bg_chat_noread_disabled);
				holder.unreadLabel.setText("");
			} else {
				// 显示与此用户的消息未读数
				holder.unreadLabel.setBackgroundResource(R.drawable.chat_unread_count_bg);
				holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
			}
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.GONE);
		}
		
		if(item.isDisabled) {
			holder.mDisabled.setVisibility(View.VISIBLE);
			if (conversation != null && conversation.getUnreadMsgCount() > 0){
				holder.mDisableTip.setVisibility(View.VISIBLE);
				holder.unreadLabel.setVisibility(View.GONE);
			} else {
				holder.mDisableTip.setVisibility(View.GONE);
			}
		} else {
			holder.mDisabled.setVisibility(View.GONE);
			holder.mDisableTip.setVisibility(View.GONE);
		}

		if (conversation != null && conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			String sendName = "";
			try {
				if (item.isGroup())
					sendName = lastMessage.getStringAttribute("userName") + ":";
			} catch (EaseMobException e) {
				e.printStackTrace();
			}
			holder.message.setText(sendName + SmileUtils.getSmiledText(mContext, getMessageDigest(lastMessage, mContext)),
					TextView.BufferType.SPANNABLE);
			holder.time.setText(DateUtil.getMessageTimeString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
		}else{
			holder.message.setText("");
			holder.msgState.setVisibility(View.GONE);
			holder.time.setText("");
		}
		convertView.findViewById(R.id.list_item_layout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mChatItemClickListener != null){
					mChatItemClickListener.onChatItemClick(item);
				}
			}
		});
		return convertView;
	}
	
	private ChatItemClickListener mChatItemClickListener;
	public void setChatItemClickListener(ChatItemClickListener listener){
		this.mChatItemClickListener = listener;
	}
	public static interface ChatItemClickListener {
		public void onChatItemClick(ChatListItem item);
	}
	
	@Override
	public void setItems(List<ChatListItem> items) {
		if(items != null){
			sortChatList(items);
		}
		super.setItems(items);
	}
	
	private void sortChatList(List<ChatListItem> items){
		if(items == null)
			return;
		
		Collections.sort(items, new Comparator<ChatListItem>() {
			@Override
			public int compare(ChatListItem lhs, ChatListItem rhs) {
				if(lhs == rhs){
					return 0;
				}
				
				EMConversation lhsCon = getConversation(lhs.mUserId);
				EMConversation rhsCon = getConversation(rhs.mUserId);
				if(lhsCon == rhsCon)
					return 0;
				
				if(lhsCon == null || lhsCon.getLastMessage() == null){
					return 1;
				}
				if(rhsCon == null || rhsCon.getLastMessage() == null){
					return -1;
				}
				return (int) (rhsCon.getLastMessage().getMsgTime() - lhsCon.getLastMessage().getMsgTime());
			}
		});
	}
	
	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		ImageView avatar;
		/** 最后一条消息的发送状态 */
		View msgState;
		/** 免打扰 */
		ImageView mDisabled;
		/** 免打扰提示 */
		ImageView mDisableTip;
	}

	/**
	 * 获得某个会话
	 * @param userId
	 * @return
	 */
	private EMConversation getConversation(String userId){
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		if(conversations == null) 
			return null;
		Iterator<String> it = conversations.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			EMConversation conversation = conversations.get(key);
			if (conversation.getAllMessages().size() != 0 && conversation.getUserName() != null 
					&& conversation.getUserName().equals(userId)){
				return conversation;
			}
		}
		return null;
	}
	
	public boolean isHasUnReadMsg(){
		for (int i = 0; i < getCount(); i++) {
			ChatListItem item = getItem(i);
			EMConversation conversation = 
					EMChatManager.getInstance().getConversation(item.mUserId);
			if(conversation.getUnreadMsgCount() > 0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				// 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
				digest = getStrng(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				digest = getStrng(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			digest = getStrng(context, R.string.picture);
			break;
		case VOICE:// 语音消息
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			}else{
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
			}
			break;
		case FILE: // 普通文件消息
			digest = getStrng(context, R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}
		return digest;
	}
	
	private String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}
}
