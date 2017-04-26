/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.message.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.FileUtils;
import com.easemob.util.LatLng;
import com.easemob.util.TextFormater;
import com.hyena.framework.app.adapter.SingleViewAdapter;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.utils.DirContext;
import com.knowbox.teacher.base.utils.HttpHelper;
import com.knowbox.teacher.modules.image.ImagePreviewerEditFragment;
import com.knowbox.teacher.modules.message.ContactInfoFragment;
import com.knowbox.teacher.modules.message.ShowNormalFileFragment;
import com.knowbox.teacher.modules.message.ShowVideoActivity;
import com.knowbox.teacher.modules.message.services.EMChatService;
import com.knowbox.teacher.modules.message.utils.Constant;
import com.knowbox.teacher.modules.message.utils.EMChatMenuLongClickListener;
import com.knowbox.teacher.modules.message.utils.ImageCache;
import com.knowbox.teacher.modules.message.utils.ImageUtils;
import com.knowbox.teacher.modules.message.utils.LoadImageTask;
import com.knowbox.teacher.modules.message.utils.LoadVideoImageTask;
import com.knowbox.teacher.modules.message.utils.SendUtils;
import com.knowbox.teacher.modules.message.utils.SmileUtils;
import com.knowbox.teacher.modules.message.utils.VoicePlayClickListener;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.DialogUtils.OnDialogButtonClickListener;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.widgets.RoundDisplayer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 聊天信息展示适配器
 * @author yangzc
 *
 */
public class EMChatListAdapter extends SingleViewAdapter<EMMessage> {

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";
	
	private Context mContext;
	private ChatListItem mChatItem;
	private BaseSubFragment mFragment;
	private EMConversation mEmConversation;
	private Map<String, Timer> timers = new Hashtable<String, Timer>();
	private EMChatListPiecesAdapter mParent;
	private int mIndex;

//	private Dialog mRelayDialog = null;
	
	public EMChatListAdapter(Context context, EMChatListPiecesAdapter parent, int type,
							 ChatListItem chatItem, BaseSubFragment fragment, EMMessage message, int index) {
		super(context, parent, type, message);
		this.mContext = context;
		this.mChatItem = chatItem;
		this.mFragment = fragment;
		this.mParent = parent;
		this.mIndex = index;
		mEmConversation = EMChatManager.getInstance().getConversation(chatItem.mUserId);
	}
	
//	@Override
//	public int getCount() {
//		if(mEmConversation == null)
//			return 0;
//		return mEmConversation.getMsgCount();
//	}
//
//	@Override
//	public EMMessage getItem(int position) {
//		return mEmConversation.getMessage(position);
//	}
	
	public EMConversation getEmConversation(){
		return mEmConversation;
	}

//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
	
//	@Override
//	public int getItemViewType(int position) {
//		EMMessage message = getItem(position);
//		if (message.getType() == EMMessage.Type.TXT) {
//			if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
//				return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_TXT : EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_TXT;
//			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_VOICE_CALL : EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_VOICE_CALL;
//		}
//		if (message.getType() == EMMessage.Type.IMAGE) {
//			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_IMAGE : EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_IMAGE;
//		}
//		if (message.getType() == EMMessage.Type.LOCATION) {
//			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_LOCATION : EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_LOCATION;
//		}
//		if (message.getType() == EMMessage.Type.VOICE) {
//			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_VOICE : EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_VOICE;
//		}
//		if (message.getType() == EMMessage.Type.VIDEO) {
//			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_VIDEO : EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_VIDEO;
//		}
//		if (message.getType() == EMMessage.Type.FILE) {
//			return message.direct == EMMessage.Direct.RECEIVE ? EMChatListPiecesAdapter.MESSAGE_TYPE_RECV_FILE : EMChatListPiecesAdapter.MESSAGE_TYPE_SENT_FILE;
//		}
//		return -1;
//	}
	
//	@Override
//	public int getViewTypeCount() {
//		return 14;
//	}
	
	private View createViewByMessage(EMMessage message, int position) {
		switch (message.getType()) {
		case LOCATION:
			return message.direct == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.layout_message_row_received_location, null) : View.inflate(
					mContext, R.layout.layout_message_row_sent_location, null);
		case IMAGE:
			return message.direct == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.layout_message_row_received_picture, null) : View.inflate(
					mContext, R.layout.layout_message_row_sent_picture, null);
		case VOICE:
			return message.direct == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.layout_message_row_received_voice, null) : View.inflate(
					mContext, R.layout.layout_message_row_sent_voice, null);
		case VIDEO:
			return message.direct == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.layout_message_row_received_video, null) : View.inflate(
					mContext, R.layout.layout_message_row_sent_video, null);
		case FILE:
			return message.direct == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.layout_message_row_received_file, null) : View.inflate(
					mContext, R.layout.layout_message_row_sent_file, null);
		default:
			// 语音电话
			if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
				return message.direct == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.layout_message_row_received_voice_call, null) : View.inflate(
						mContext, R.layout.layout_message_row_sent_voice_call, null);
			}
			
//			try {
//				if (String.valueOf(EMChatListPiecesAdapter.MSG_TYPE_CREATECLASS).equals(message.getStringAttribute("type"))){//建班成功收到消息
//					if(message.direct == EMMessage.Direct.RECEIVE )
//						return View.inflate(mContext, R.layout.layout_message_row_received_createclass, null) ;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			//文本
			return message.direct == EMMessage.Direct.RECEIVE ? View.inflate(mContext, R.layout.layout_message_row_received_message, null) : View.inflate(
					mContext, R.layout.layout_message_row_sent_message, null);
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		final EMMessage message = getItem(position);
		ChatType chatType = message.getChatType();
		final ViewHolder holder;
		if (convertView == null) {
			convertView = createViewByMessage(message, position);
			holder = new ViewHolder();
			if (message.getType() == EMMessage.Type.IMAGE) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.TXT) {
				try {
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					// 这里是文字内容
					holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.mEmotionView = (GifImageView) convertView.findViewById(R.id.image);
					holder.mEmotionPb = (ProgressBar) convertView.findViewById(R.id.pb_experssion);
				} catch (Exception e) {
				}
				// 语音通话
				if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
					holder.iv = (ImageView) convertView.findViewById(R.id.iv_call_icon);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
				}
//				try {
//					if (String.valueOf(EMChatListPiecesAdapter.MSG_TYPE_CREATECLASS).equals(message.getStringAttribute("type"))) {
//						holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
//					}
//				} catch (EaseMobException e) {
//				}


			} else if (message.getType() == EMMessage.Type.VOICE) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
					holder.iv_container = convertView.findViewById(R.id.iv_contaienr);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.LOCATION) {
				try {
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_location);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.VIDEO) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.chatting_content_iv));
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.size = (TextView) convertView.findViewById(R.id.chatting_size_iv);
					holder.timeLength = (TextView) convertView.findViewById(R.id.chatting_length_iv);
					holder.playBtn = (ImageView) convertView.findViewById(R.id.chatting_status_btn);
					holder.container_status_btn = (LinearLayout) convertView.findViewById(R.id.container_status_btn);
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);

				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.FILE) {
				try {
					holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv_file_name = (TextView) convertView.findViewById(R.id.tv_file_name);
					holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_file_download_state = (TextView) convertView.findViewById(R.id.tv_file_state);
					holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_file_container);
					// 这里是进度值
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
				} catch (Exception e) {
				}
				try {
					holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
				} catch (Exception e) {
				}
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 群聊时，显示接收的消息的发送人的名称
		if (chatType == ChatType.GroupChat && message.direct == EMMessage.Direct.RECEIVE){
			if(holder.tv_userId!=null){
				try {
					holder.tv_userId.setText(message.getStringAttribute("userName"));
				} catch (EaseMobException e) {
					e.printStackTrace();
				}
			}
		}

		// 如果是发送的消息并且不是群聊消息，显示已读textview
		if (message.direct == EMMessage.Direct.SEND && chatType != ChatType.GroupChat) {
			holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
			holder.tv_delivered = (TextView) convertView.findViewById(R.id.tv_delivered);
			if (holder.tv_ack != null) {
				if (message.isAcked) {
					if (holder.tv_delivered != null) {
						holder.tv_delivered.setVisibility(View.INVISIBLE);
					}
					holder.tv_ack.setVisibility(View.VISIBLE);
				} else {
					holder.tv_ack.setVisibility(View.INVISIBLE);

					// check and display msg delivered ack status
					if (holder.tv_delivered != null) {
						if (message.isDelivered) {
							holder.tv_delivered.setVisibility(View.VISIBLE);
						} else {
							holder.tv_delivered.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
		} else {
			// 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
			if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION) && !message.isAcked && chatType != ChatType.GroupChat) {
				// 不是语音通话记录
				if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
					try {
						EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
						// 发送已读回执
						message.isAcked = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		switch (message.getType()) {
		// 根据消息type显示item
		case IMAGE: // 图片
			handleImageMessage(message, holder, position, convertView);
			break;
		case TXT: // 文本
			if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
				// 语音电话
				handleVoiceCallMessage(message, holder, position);
			} else{
//				try {
//					if(String.valueOf(EMChatListPiecesAdapter.MSG_TYPE_CREATECLASS).equals(message.getStringAttribute("type"))){
//						handleCreateClassMessage(message, holder, position);
//						break;
//					}
//				} catch (EaseMobException e) {
//				}
				handleTextMessage(message, holder, position);
			}

			break;
		case LOCATION: // 位置
			handleLocationMessage(message, holder, position, convertView);
			break;
		case VOICE: // 语音
			handleVoiceMessage(message, holder, position, convertView);
			break;
		case VIDEO: // 视频
			handleVideoMessage(message, holder, position, convertView);
			break;
		case FILE: // 一般文件
			handleFileMessage(message, holder, position, convertView);
			break;
		default:
			// not supported
		}

		if (message.direct == EMMessage.Direct.SEND) {
//			View statusView = convertView.findViewById(R.id.msg_status);
			// 重发按钮点击事件
			holder.staus_iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 显示重发消息的自定义alertdialog
//					if(mRelayDialog != null && mRelayDialog.isShowing()){
//						mRelayDialog.dismiss();
//					}
					DialogUtils.getMessageDialog(mContext, mContext.getString(R.string.resend), "确定", "取消",
							mContext.getString(R.string.confirm_resend), new OnDialogButtonClickListener() {
								@Override
								public void onItemClick(Dialog dialog, int btnId) {
									if(btnId == BUTTON_CONFIRM){
										SendUtils.resendMessage(mChatItem.mUserId, message);
										getParentAdapter().notifyDataSetChanged();
									}
									if(dialog.isShowing()){
										dialog.dismiss();
									}
								}
							}).show();

//					mRelayDialog = DialogUtils.getSelectDialog(mContext, mContext.getString(R.string.resend),
//							mContext.getString(R.string.confirm_resend),
//							"确定", "取消", new OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									SendUtils.resendMessage(mChatItem.mUserId, position);
//									if(mRelayDialog != null && mRelayDialog.isShowing()){
//										mRelayDialog.dismiss();
//									}
//								}
//							}, new OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									if(mRelayDialog != null && mRelayDialog.isShowing()){
//										mRelayDialog.dismiss();
//									}
//								}
//							});
//					mRelayDialog.show();
				}
			});
			UserItem userItem = Utils.getLoginUserItem();
			if(userItem != null){
				ImageFetcher.getImageFetcher().loadImage(userItem.headPhoto,
						holder.head_iv, R.drawable.bt_chat_teacher_default, new RoundDisplayer());
			}
		} else {
			if( holder.head_iv != null ){
				if(mChatItem.isGroup()){
					ImageFetcher.getImageFetcher().loadImage(message.getStringAttribute("userPhoto", ""), holder.head_iv,
							R.drawable.default_img, new RoundDisplayer());
				}else if (mChatItem.mUserId.equals(EMChatService.SERVICE_ID)){
					ImageFetcher.getImageFetcher().loadImage("http://file.knowbox.cn/upload/service/head_photo.png", holder.head_iv,
							R.drawable.default_img, new RoundDisplayer());
				} else {
					ImageFetcher.getImageFetcher().loadImage(mChatItem.mHeadPhoto, holder.head_iv,
							R.drawable.default_img, new RoundDisplayer());
				}
				// 长按头像，移入黑名单
				holder.head_iv.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						return true;
					}
				});
				holder.head_iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mChatItem == null)
							return;
						if (mChatItem.mUserId.equals(EMChatService.SERVICE_ID)) {
							return;
						}
						Bundle bundle = new Bundle();
						mChatItem.mUserId = message.getFrom();
						bundle.putSerializable("userItem", mChatItem);
						ContactInfoFragment fragment = (ContactInfoFragment) Fragment.instantiate(mContext,
								ContactInfoFragment.class.getName(), bundle);
						mFragment.showFragment(fragment);
					}
				});
			}
		}
		TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

		if (mIndex == 0) {
			timestamp.setText(DateUtil.getMessageTimeString(new Date(message.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			if(mEmConversation != null) {
				EMMessage lastMsg = mEmConversation.getMessage(mIndex - 1);
				if (lastMsg != null && DateUtils.isCloseEnough(message.getMsgTime(), lastMsg.getMsgTime())) {
					timestamp.setVisibility(View.GONE);
				} else {
					timestamp.setText(DateUtil.getMessageTimeString(new Date(message.getMsgTime())));
					timestamp.setVisibility(View.VISIBLE);
				}
			}
		}

		return convertView;
	}

	private void handleExpressionMessage(final EMMessage message, final ViewHolder holder, final int position) {
		final String emojiconId = message.getStringAttribute(Constant.MESSAGE_ATTR_EXPRESSION_ID, "");
		final String filePath = DirContext.getImageCacheDir() + File.separator + new Md5FileNameGenerator().generate(emojiconId);
		if (new File(filePath).exists()) {
			try {
				GifDrawable d = new GifDrawable(filePath);
				holder.mEmotionView.setImageDrawable(d);
				holder.mEmotionView.setVisibility(View.VISIBLE);
				holder.mEmotionPb.setVisibility(View.GONE);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return;
		}
		HttpHelper.storeFile(emojiconId, filePath, new HttpHelper.ProgressListener() {
			@Override
			public void onStart(long total) {
				holder.mEmotionPb.setVisibility(View.VISIBLE);
				holder.mEmotionView.setVisibility(View.GONE);
			}

			@Override
			public void onAdvance(long len, long total) {

			}

			@Override
			public void onComplete(boolean isSuccess) {
				if (isSuccess) {
					try {
						GifDrawable d = null;
						File mFile = new File(filePath);
						if (mFile != null && mFile.exists()) {
							d = new GifDrawable(mFile);
						}
						if (d != null) {
							holder.mEmotionView.setImageDrawable(d);
						} else {
							holder.mEmotionView.setImageResource(R.drawable.default_image);
							holder.mEmotionView.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View view) {
									handleExpressionMessage(message, holder, position);
								}
							});
						}
						holder.mEmotionView.setVisibility(View.VISIBLE);
						holder.mEmotionPb.setVisibility(View.GONE);
					} catch (Exception e) {
						e.printStackTrace();
						holder.mEmotionView.setVisibility(View.VISIBLE);
						holder.pb.setVisibility(View.GONE);
						holder.mEmotionView.setImageResource(R.drawable.default_image);
						holder.mEmotionView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								handleExpressionMessage(message, holder, position);
							}
						});
					}
				} else {
					holder.mEmotionView.setVisibility(View.VISIBLE);
					holder.mEmotionView.setImageResource(R.drawable.default_image);
					holder.mEmotionPb.setVisibility(View.GONE);
					holder.mEmotionView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							handleExpressionMessage(message, holder, position);
						}
					});
				}
			}
		});
//
//		ImageFetcher.getImageFetcher().loadImage(emojiconId, "", new ImageFetcher.ImageFetcherListener() {
//			@Override
//			public void onLoadComplete(String s, Bitmap bitmap, Object o) {
//
//
//			}
//
//			@Override
//			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//				super.onLoadingFailed(imageUri, view, failReason);
//
//			}
//
//			@Override
//			public void onLoadStarted(String imageUrl, View view, Object tag) {
//				super.onLoadStarted(imageUrl, view, tag);
//
//			}
//		});
	}

	/**
	 * 文本消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(final EMMessage message, final ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		// 设置内容
		if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
			holder.mEmotionPb.setVisibility(View.VISIBLE);
			holder.mEmotionView.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.GONE);
			handleExpressionMessage(message, holder, position);
		} else {
			holder.mEmotionPb.setVisibility(View.GONE);
			holder.mEmotionView.setVisibility(View.GONE);
			holder.tv.setVisibility(View.VISIBLE);

			Spannable span = SmileUtils.getSmiledText(mContext, txtBody.getMessage());
			holder.tv.setText(span, BufferType.SPANNABLE);

			ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
			menuItems.add(new MenuItem(EMChatMenuLongClickListener.TYPE_COPY, -1, "复制消息", null));
			menuItems.add(new MenuItem(EMChatMenuLongClickListener.TYPE_DELETE, -1, "删除消息", null));
			holder.tv.setOnLongClickListener(new EMChatMenuLongClickListener(mContext, mParent, message, menuItems));
		}
		// 设置长按事件监听

		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
	}

	/**
	 * 语音通话记录
	 *
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleVoiceCallMessage(EMMessage message, ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		holder.tv.setText(txtBody.getMessage());

	}


	/**
	 * 建班成功消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleCreateClassMessage(EMMessage message, ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		holder.tv.setText(txtBody.getMessage());

	}

	/**
	 * 图片消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		holder.pb.setTag(position);
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		menuItems.add(new MenuItem(EMChatMenuLongClickListener.TYPE_DELETE, -1, "删除", null));
		holder.iv.setOnLongClickListener(new EMChatMenuLongClickListener(mContext, mParent, message, menuItems));

		// 接收方向的消息
		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";
				holder.iv.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);
				// downloadImage(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.iv.setImageResource(R.drawable.default_image);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();
					String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// 发送的消息
		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists()) {
			showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
		} else {
			showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, IMAGE_DIR, message);
		}

		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					UiThreadHandler.post(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.VISIBLE);
							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								ToastUtils.showShortToast(mContext, mContext.getString(R.string.send_fail)
										+ mContext.getString(R.string.connect_failuer_toast));
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			sendPictureMessage(message, holder);
		}
	}

	/**
	 * 视频消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVideoMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {

		VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
		// final File image=new File(PathUtil.getInstance().getVideoPath(),
		// videoBody.getFileName());
		String localThumb = videoBody.getLocalThumb();
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		menuItems.add(new MenuItem(EMChatMenuLongClickListener.TYPE_DELETE, -1, "删除视频", null));
		holder.iv.setOnLongClickListener(new EMChatMenuLongClickListener(mContext, mParent, message, menuItems));

		if (localThumb != null) {

			showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
		}
		if (videoBody.getLength() > 0) {
			String time = DateUtils.toTimeBySecond(videoBody.getLength());
			holder.timeLength.setText(time);
		}
		holder.playBtn.setImageResource(R.drawable.chat_video_download_btn_nor);

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (videoBody.getVideoFileLength() > 0) {
				String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
				holder.size.setText(size);
			}
		} else {
			if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
				String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
				holder.size.setText(size);
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {

			// System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				// System.err.println("!!!! back receive");
				holder.iv.setImageResource(R.drawable.default_img);
				showDownloadImageProgress(message, holder);

			} else {
				// System.err.println("!!!! not back receive, show image directly");
				holder.iv.setImageResource(R.drawable.default_img);
				if (localThumb != null) {
					showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
				}

			}

			return;
		}
		holder.pb.setTag(position);

		// until here ,deal with send video msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.tv.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					UiThreadHandler.post(new Runnable() {

						@Override
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.VISIBLE);
							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
								// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
								// message.setProgress(0);
								holder.staus_iv.setVisibility(View.VISIBLE);
								ToastUtils.showShortToast(mContext, mContext.getString(R.string.send_fail)
										+ mContext.getString(R.string.connect_failuer_toast));
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			// sendMsgInBackground(message, holder);
			sendPictureMessage(message, holder);

		}

	}

	/**
	 * 语音消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
		holder.tv.setText(voiceBody.getLength() + "\"");
		int winHalf = mContext.getResources().getDisplayMetrics().widthPixels/2;
		int p = (int) (voiceBody.getLength() * winHalf/60.0f);

		if(message.direct == EMMessage.Direct.SEND){
			if(holder.iv_container.getPaddingLeft() + p > winHalf){
				p = winHalf;
			}else{
				p = (int) (mContext.getResources().getDisplayMetrics().density * 48 + p);
			}
//			holder.iv_container.setPadding(p, holder.iv_container.getPaddingTop(),
//					holder.iv_container.getPaddingRight(), holder.iv_container.getPaddingBottom());
			holder.iv_container.getLayoutParams().width = p;
		}else{
			if(holder.iv_container.getPaddingRight() + p > winHalf){
				p = winHalf;
			}else{
				p = (int) (mContext.getResources().getDisplayMetrics().density * 48 + p);
			}
//			holder.iv_container.setPadding(holder.iv_container.getPaddingLeft(),
//					holder.iv_container.getPaddingTop(), p, holder.iv_container.getPaddingBottom());
			holder.iv_container.getLayoutParams().width = p;
		}

		holder.iv_container.setOnClickListener(new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, mContext, mChatItem.mUserId));

		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		menuItems.add(new MenuItem(EMChatMenuLongClickListener.TYPE_DELETE, -1, "删除语音", null));
		holder.tv.setOnLongClickListener(new EMChatMenuLongClickListener(mContext, mParent, message, menuItems));

		if (VoicePlayClickListener.playMsgId != null && VoicePlayClickListener.playMsgId.equals(message
						.getMsgId()) && VoicePlayClickListener.isPlaying) {
			AnimationDrawable voiceAnimation;
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.anim.voice_from_icon);
			} else {
				holder.iv.setImageResource(R.anim.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();
		} else {
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
			} else {
				holder.iv.setImageResource(R.drawable.chatto_voice_playing);
			}
		}


		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (message.isListened()) {
				// 隐藏语音未听标志
				holder.iv_read_status.setVisibility(View.INVISIBLE);
			} else {
				holder.iv_read_status.setVisibility(View.VISIBLE);
			}
			System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				holder.pb.setVisibility(View.VISIBLE);
				System.err.println("!!!! back receive");
				((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

					@Override
					public void onSuccess() {
						UiThreadHandler.post(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
								notifyDataSetChanged();
							}
						});

					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(int code, String message) {
						UiThreadHandler.post(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
							}
						});

					}
				});

			} else {
				holder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 文件消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleFileMessage(final EMMessage message, final ViewHolder holder, int position, View convertView) {
		final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
		final String filePath = fileMessageBody.getLocalUrl();
		holder.tv_file_name.setText(fileMessageBody.getFileName());
		holder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
		holder.ll_container.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				File file = new File(filePath);
				if (file != null && file.exists()) {
					// 文件存在，直接打开
					FileUtils.openFile(file, (Activity) mContext);
				} else {
					// 下载
					if (mFragment != null) {
						Bundle bundle = new Bundle();
						bundle.putParcelable("msgbody", fileMessageBody);
						BaseSubFragment fragment = (BaseSubFragment) Fragment.instantiate(mContext, ShowNormalFileFragment.class.getName(), bundle);
						mFragment.showFragment(fragment);
					}
				}
				if (message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
					try {
						EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
						message.isAcked = true;
					} catch (EaseMobException e) {
						e.printStackTrace();
					}
				}
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) { // 接收的消息
			System.err.println("it is receive msg");
			File file = new File(filePath);
			if (file != null && file.exists()) {
				holder.tv_file_download_state.setText("已下载");
			} else {
				holder.tv_file_download_state.setText("未下载");
			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.INVISIBLE);
			holder.tv.setVisibility(View.INVISIBLE);
			holder.staus_iv.setVisibility(View.INVISIBLE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.INVISIBLE);
			holder.tv.setVisibility(View.INVISIBLE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			if (timers.containsKey(message.getMsgId()))
				return;
			// set a timer
			final Timer timer = new Timer();
			timers.put(message.getMsgId(), timer);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					UiThreadHandler.post(new Runnable() {

						@Override
						public void run() {
							holder.pb.setVisibility(View.VISIBLE);
							holder.tv.setVisibility(View.VISIBLE);
							holder.tv.setText(message.progress + "%");
							if (message.status == EMMessage.Status.SUCCESS) {
								holder.pb.setVisibility(View.INVISIBLE);
								holder.tv.setVisibility(View.INVISIBLE);
								timer.cancel();
							} else if (message.status == EMMessage.Status.FAIL) {
								holder.pb.setVisibility(View.INVISIBLE);
								holder.tv.setVisibility(View.INVISIBLE);
								holder.staus_iv.setVisibility(View.VISIBLE);
								ToastUtils.showShortToast(mContext,mContext.getString(R.string.send_fail)
										+ mContext.getString(R.string.connect_failuer_toast));
								timer.cancel();
							}

						}
					});

				}
			}, 0, 500);
			break;
		default:
			// 发送消息
			sendMsgInBackground(message, holder);
		}

	}

	/**
	 * 处理位置消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleLocationMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		TextView locationView = ((TextView) convertView.findViewById(R.id.tv_location));
		LocationMessageBody locBody = (LocationMessageBody) message.getBody();
		locationView.setText(locBody.getAddress());
		LatLng loc = new LatLng(locBody.getLatitude(), locBody.getLongitude());
		locationView.setOnClickListener(new MapClickListener(loc, locBody.getAddress()));
		locationView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
//				activity.startActivityForResult(
//						(new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
//								EMMessage.Type.LOCATION.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);
				return false;
			}
		});

		if (message.direct == EMMessage.Direct.RECEIVE) {
			return;
		}
		// deal with send message
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 发送消息
	 *
	 * @param message
	 * @param holder
	 */
	public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);
		// 调用sdk发送异步发送方法
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				updateSendedView(message, holder);
			}

			@Override
			public void onError(int code, String error) {
				updateSendedView(message, holder);
			}

			@Override
			public void onProgress(int progress, String status) {
			}

		});

	}

	/*
	 * chat sdk will automatic download thumbnail image for the image message we
	 * need to register callback show the download progress
	 */
	private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
		System.err.println("!!! show download image progress");
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();
		if(holder.pb!=null)
		holder.pb.setVisibility(View.VISIBLE);
		if(holder.tv!=null)
		holder.tv.setVisibility(View.VISIBLE);

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						// message.setBackReceive(false);
						if (message.getType() == EMMessage.Type.IMAGE) {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
						notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					UiThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							holder.tv.setText(progress + "%");
						}
					});
				}

			}

		});
	}

	private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
		try {
			// before send, update ui
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			holder.tv.setText("0%");
			// if (chatType == ChatActivity.CHATTYPE_SINGLE) {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
				@Override
				public void onSuccess() {
					Log.d("yangzc", "send image message successfully");
					UiThreadHandler.post(new Runnable() {
						public void run() {
							// send success
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
					});
				}
				@Override
				public void onError(int code, String error) {
					UiThreadHandler.post(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
							// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
							holder.staus_iv.setVisibility(View.VISIBLE);
							ToastUtils.showShortToast(mContext,
									mContext.getString(R.string.send_fail) + mContext.getString(R.string.connect_failuer_toast));
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					UiThreadHandler.post(new Runnable() {
						public void run() {
							holder.tv.setText(progress + "%");
						}
					});
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新ui上消息发送状态
	 *
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message, final ViewHolder holder) {
		UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				if (message.status == EMMessage.Status.SUCCESS) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// holder.staus_iv.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// holder.staus_iv.setVisibility(View.GONE);
					// }

				} else if (message.status == EMMessage.Status.FAIL) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// }
					// holder.staus_iv.setVisibility(View.VISIBLE);
					ToastUtils.showShortToast(mContext, mContext.getString(R.string.send_fail)
							+ mContext.getString(R.string.connect_failuer_toast));
				}

				notifyDataSetChanged();
			}
		});
	}

	/**
	 * load image into image view
	 *
	 * @param thumbernailPath
	 * @param iv
	 * @return the image exists or not
	 */
	private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
			final EMMessage message) {
		final String remote = remoteDir;
		EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
		if (bitmap != null) {
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.err.println("image view on click");
					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
							&& message.getChatType() != ChatType.GroupChat) {
						try {
							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(mFragment != null){
						showAllPictures(message);
					}
				}
			});
			return true;
		} else {
			new LoadImageTask(mFragment, mChatItem).execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, mContext, message);
			return true;
		}

	}

	private void showAllPictures(EMMessage curMessage) {
		List<EMMessage> mMessages = mEmConversation.getAllMessages();
		ArrayList<String> allPaths = new ArrayList<String>();
		int index = 0;
		for (int i = 0; i < mMessages.size(); i++) {
			EMMessage message = mMessages.get(i);
			if (message.getType() == EMMessage.Type.IMAGE) {
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (message.direct == EMMessage.Direct.RECEIVE) {
					if (imgBody.getLocalUrl() != null) {
						String remotePath = imgBody.getRemoteUrl();
						String filePath = ImageUtils.getImagePath(remotePath);
						if (new File(filePath).exists()) {
							allPaths.add("file://" + filePath);
						} else {
							allPaths.add(remotePath);
						}
						if (curMessage.equals(message)) {
							index = allPaths.size() - 1;
						}
					}
				} else {
					if (imgBody.getLocalUrl() != null) {
						if (new File(imgBody.getLocalUrl()).exists()){
							allPaths.add("file://" + imgBody.getLocalUrl());
							if (curMessage.equals(message)) {
								index = allPaths.size() - 1;
							}
						}

					}
				}

			}
		}

		Bundle bundle = new Bundle();
		bundle.putStringArrayList("list", allPaths);
		bundle.putInt("index", index);
		bundle.putBoolean("showPreview", true);
		ImagePreviewerEditFragment fragment = (ImagePreviewerEditFragment) Fragment
				.instantiate(mFragment.getActivity(),
						ImagePreviewerEditFragment.class.getName(), bundle);
		fragment.setOnImageEditListener(new ImagePreviewerEditFragment.OnImageEditListener() {

			@Override
			public void onImageEdit(List<String> paths) {

			}
		});
		mFragment.showFragment(fragment);
	}

	/**
	 * 展示视频缩略图
	 * @param localThumb 本地缩略图路径
	 * @param iv
	 * @param thumbnailUrl 远程缩略图路径
	 * @param message
	 */
	private void showVideoThumbView(String localThumb, ImageView iv, String thumbnailUrl, final EMMessage message) {
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(localThumb);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					System.err.println("video view is on click");
					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
							&& message.getChatType() != ChatType.GroupChat) {
						message.isAcked = true;
						try {
							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
					Intent intent = new Intent(mContext, ShowVideoActivity.class);
					intent.putExtra("localpath", videoBody.getLocalUrl());
					intent.putExtra("secret", videoBody.getSecret());
					intent.putExtra("remotepath", videoBody.getRemoteUrl());
					mContext.startActivity(intent);

				}
			});

		} else {
			new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv, mContext, message, this);
		}

	}

	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView head_iv;
		TextView tv_userId;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;

		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;
		View iv_container;
		int position;
		GifImageView mEmotionView;
		ProgressBar mEmotionPb;
	}

	/*
	 * 点击地图消息listener
	 */
	class MapClickListener implements OnClickListener {

		LatLng location;
		String address;

		public MapClickListener(LatLng loc, String address) {
			location = loc;
			this.address = address;

		}

		@Override
		public void onClick(View v) {
//			Intent intent;
//			intent = new Intent(context, BaiduMapActivity.class);
//			intent.putExtra("latitude", location.latitude);
//			intent.putExtra("longitude", location.longitude);
//			intent.putExtra("address", address);
//			activity.startActivity(intent);
		}

	}
}
