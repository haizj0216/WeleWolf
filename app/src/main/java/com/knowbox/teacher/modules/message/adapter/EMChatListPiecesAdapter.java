/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.message.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.hyena.framework.app.adapter.PiecesAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.modules.message.utils.Constant;
import com.knowbox.teacher.modules.message.utils.VoicePlayClickListener;

public class EMChatListPiecesAdapter extends PiecesAdapter {

	public static final int MESSAGE_TYPE_RECV_TXT = 0;
	public static final int MESSAGE_TYPE_SENT_TXT = 1;
	public static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	public static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	public static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	public static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	public static final int MESSAGE_TYPE_SENT_VOICE = 6;
	public static final int MESSAGE_TYPE_RECV_VOICE = 7;
	public static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	public static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	public static final int MESSAGE_TYPE_SENT_FILE = 10;
	public static final int MESSAGE_TYPE_RECV_FILE = 11;
	public static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
	public static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;

	public static final int MESSAGE_TYPE_PUBLIC_LIKE = 14;
	public static final int MESSAGE_TYPE_PRIVATE_QUESTION = 15;

	public static final int MESSAGE_TYPE_NOTICE = 16;
	public static final int MESSAGE_TYPE_CLASS_NOTICE = 17;
	public static final int MESSAGE_TYPE_HOMEWORK = 18;
	public static final int MESSAGE_TYPE_STUDENT_QUESTION = 20;

	public static final int MSG_TYPE_SET = 0;//出作业
	public static final int MSG_TYPE_RECOMMEND = 10;//推荐
	public static final int MSG_TYPE_CORRECT = 4;//批改完成
	public static final int MSG_TYPE_REMIND = 11;//催作业
	public static final int MSG_TYPE_PARISE = 9;//赞
	public static final int MSG_TYPE_CHATQUESTION = 12;//作业聊天
	public static final int MSG_TYPE_NOTICE = 14;//通知
	public static final int MSG_TYPE_AUTH = 15;//认证
	public static final int MSG_TYPE_CREATECLASS = 16;//建班成功，学生加入的发来的消息
	public static final int MSG_TYPE_NEWHOMEWORK = 17;//新作业
	public static final int MSG_TYPE_ACTIVITY = 18;//活动
	public static final int MSG_TYPE_SHAREHOMEWORK = 19;//分享作业
	public static final int MSG_TYPE_STUDENTQUESTION = 1000;
	public static final int MSG_TYPE_SELFTRAIN_RANK = 21;//学生自主练习排名
	public static final int MSG_TYPE_PRAISE_SELFTRAIN = 22;//学生自主练习被老师点赞
	public static final int MSG_TYPE_TELL_CLASSMATE_PK_RESULT=25;//PK结果超过同学
	public static final int MSG_TYPE_TELL_CLASSMATE_SCORE_RESULT=26;//积分超过同学

	private Context mContext;
	private ChatListItem mChatItem;
	private BaseUIFragment mFragment;
	private EMConversation mEmConversation;
	
	public EMChatListPiecesAdapter(Context context, ChatListItem chatItem, BaseUIFragment fragment) {
		this.mContext = context;
		this.mChatItem = chatItem;
		this.mFragment = fragment;
		mEmConversation = EMChatManager.getInstance().getConversation(chatItem.mUserId);
	}
	
	public EMConversation getEmConversation(){
		return mEmConversation;
	}
	
	@Override
	public int getViewTypeCount() {
		return 23;
	}
	
	public void refreshList() {
		if(mEmConversation == null)
			return;
		
		clearPieces();
		for (int i = 0; i < mEmConversation.getMsgCount(); i++) {
			EMMessage message = mEmConversation.getMessage(i);
			if (!TextUtils.isEmpty(message.getStringAttribute("apns", ""))) {
				continue;
			}
			int type = getItemType(message);
			if(type <= 13) {//通用聊天内容
				EMChatListAdapter adapter = new EMChatListAdapter(mContext, this, 
						type, mChatItem, mFragment, message, i);
				addAdapter(i, adapter);
			}
//			else if(type == MESSAGE_TYPE_PUBLIC_LIKE) {//自定义聊天内容
//				EMChatLikeAdapter adapter = new EMChatLikeAdapter(mContext, this, type, message, mChatItem, mFragment);
//				addAdapter(i, adapter);
//			}
			else if(type == MESSAGE_TYPE_PRIVATE_QUESTION){
				EMChatQuestionLinkAdapter adapter = new EMChatQuestionLinkAdapter(mContext, 
						this, type, mChatItem,mFragment, message, i);
				addAdapter(i, adapter);
			} else if (type == MESSAGE_TYPE_NOTICE) {
				EMChatNoticeAdapter adapter = new EMChatNoticeAdapter(mContext,
						this, type, mChatItem.mUserId, mFragment, message, i);
				addAdapter(i, adapter);
			} else if(type == MESSAGE_TYPE_CLASS_NOTICE) {
				EMChatTipAdapter adapter = new EMChatTipAdapter(mContext,
						this, type, mChatItem.mUserId, mFragment, message, i);
				addAdapter(i, adapter);
			} else if(type == MESSAGE_TYPE_HOMEWORK) {
				EmChatHomeworkAdapter adapter = new EmChatHomeworkAdapter(mContext,
						this, type, mChatItem,mFragment, message, i);
				addAdapter(i, adapter);
			} else if (type == MSG_TYPE_SHAREHOMEWORK) {
				EMChatShareHomeworkAdapter adapter = new EMChatShareHomeworkAdapter(mContext,
						this, type, mChatItem, mFragment,message, i);
				addAdapter(i, adapter);
			} else if (type == MESSAGE_TYPE_STUDENT_QUESTION) {
				EMChatStudentQuestionAdapter adapter = new EMChatStudentQuestionAdapter(mContext,
						this, type, mChatItem,mFragment, message, i);
				addAdapter(i, adapter);
			} else if (type == MSG_TYPE_SELFTRAIN_RANK) {
				//学生自主练习的消息适配器
				EMChatSelfTrainAdapter adapter = new EMChatSelfTrainAdapter(mContext,
						this, type, mChatItem, mFragment, message, i);
				addAdapter(i, adapter);
			} else if (type == MSG_TYPE_PRAISE_SELFTRAIN) {
				//自主练习排行榜老师点赞的消息
				EMChatPraiseAdapter adapter = new EMChatPraiseAdapter(mContext,
						this,type, mChatItem, mFragment,message, i);
				addAdapter(i, adapter);
			} else if(type==MSG_TYPE_TELL_CLASSMATE_PK_RESULT||type==MSG_TYPE_TELL_CLASSMATE_SCORE_RESULT){
				EMChatTellClassmateAdapter adapter=new EMChatTellClassmateAdapter(mContext,
						this,type,mChatItem,mFragment,message,i);
				addAdapter(i,adapter);
			}
		}
	}

	/**
	 * 播放语音时，暂停播放
	 */
	public void releaseAdapter() {
		try {
			if (VoicePlayClickListener.currentPlayListener != null && VoicePlayClickListener.isPlaying)
				VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getItemType(EMMessage message) {
		if (message.getType() == EMMessage.Type.TXT) {
			String customType = message.getStringAttribute("type", "");//自定义类型
			if (TextUtils.isEmpty(customType)) {
				customType = String.valueOf(message.getIntAttribute("type", -1));
			}
			if (!TextUtils.isEmpty(customType)) {
				try {
					int type = Integer.parseInt(customType);
					switch (type) {
						case MSG_TYPE_SET:
						case MSG_TYPE_RECOMMEND:
						case MSG_TYPE_CORRECT:
						case MSG_TYPE_REMIND:
						case MSG_TYPE_PARISE:
							return EMChatListPiecesAdapter.MESSAGE_TYPE_PUBLIC_LIKE;
						case MSG_TYPE_CHATQUESTION:
							return EMChatListPiecesAdapter.MESSAGE_TYPE_PRIVATE_QUESTION;
						case MSG_TYPE_NOTICE:
						case MSG_TYPE_AUTH:
						case MSG_TYPE_ACTIVITY:
							return EMChatListPiecesAdapter.MESSAGE_TYPE_NOTICE;
						case MSG_TYPE_CREATECLASS:
							return EMChatListPiecesAdapter.MESSAGE_TYPE_CLASS_NOTICE;//建班成功类型转换成txt
						case MSG_TYPE_NEWHOMEWORK:
							return EMChatListPiecesAdapter.MESSAGE_TYPE_HOMEWORK;
						case MSG_TYPE_SHAREHOMEWORK:
							return EMChatListPiecesAdapter.MSG_TYPE_SHAREHOMEWORK;
						case MSG_TYPE_STUDENTQUESTION:
							return EMChatListPiecesAdapter.MESSAGE_TYPE_STUDENT_QUESTION;
						case MSG_TYPE_SELFTRAIN_RANK:
							return EMChatListPiecesAdapter.MSG_TYPE_SELFTRAIN_RANK;
						case MSG_TYPE_PRAISE_SELFTRAIN:
							return EMChatListPiecesAdapter.MSG_TYPE_PRAISE_SELFTRAIN;
						case MSG_TYPE_TELL_CLASSMATE_PK_RESULT:
							return EMChatListPiecesAdapter.MSG_TYPE_TELL_CLASSMATE_PK_RESULT;
						case MSG_TYPE_TELL_CLASSMATE_SCORE_RESULT:
							return EMChatListPiecesAdapter.MSG_TYPE_TELL_CLASSMATE_SCORE_RESULT;
						default:
							break;
					}
				} catch (Exception e) {
				}
			}
			
			if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))//如果不是语音电话
				return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_TXT : 
					EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_TXT;
			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_VOICE_CALL : 
				EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_VOICE_CALL;//如果是语音电话
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_IMAGE : 
				EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_IMAGE;
		}
		if (message.getType() == EMMessage.Type.LOCATION) {
			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_LOCATION : 
				EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_LOCATION;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_VOICE : 
				EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.VIDEO) {
			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_VIDEO : 
				EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getType() == EMMessage.Type.FILE) {
			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_FILE : 
				EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_FILE;
		}
		return -1;
	}
}
