package com.knowbox.teacher.modules.message.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.DateUtils;
import com.hyena.framework.app.adapter.SingleViewAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.message.EMChatFragment;
import com.knowbox.teacher.modules.message.utils.Constant;
import com.knowbox.teacher.modules.message.utils.EMChatMenuLongClickListener;
import com.knowbox.teacher.modules.message.utils.MessagePushUtils;
import com.knowbox.teacher.modules.message.utils.SendUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.DialogUtils.OnDialogButtonClickListener;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.Date;

/**
 * 聊题 adapter
 * @author weilei
 *
 */
public class EMChatQuestionLinkAdapter extends SingleViewAdapter<EMMessage> {

	private Context mContext;
	private ChatListItem mChatItem;
	private EMConversation mEmConversation;
	private LoginService mLoginService;
	private BaseUIFragment mFragment;
	private int mIndex;

	public EMChatQuestionLinkAdapter(Context context, BaseAdapter parent,
									 int type, ChatListItem chatItem, BaseUIFragment fragment,
									 EMMessage message, int index) {
		super(context, parent, type, message);
		mContext = context;
		this.mChatItem = chatItem;
		mFragment = fragment;
		this.mIndex = index;
		mEmConversation = EMChatManager.getInstance().getConversation(
				chatItem.mUserId);
		mLoginService = (LoginService) BaseApp.getAppContext()
				.getSystemService(LoginService.SERVICE_NAME);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		ChatType chatType = message.getChatType();
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.layout_row_sent_question_link, null);
			holder = new ViewHolder();
			holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
			holder.staus_iv = (ImageView) convertView
					.findViewById(R.id.msg_status);
			holder.head_iv = (ImageView) convertView
					.findViewById(R.id.iv_userhead);
			holder.tv_userId = (TextView) convertView
					.findViewById(R.id.tv_userid);

			holder.msg_chatcontent_container = (LinearLayout) convertView
					.findViewById(R.id.msg_chatcontent_container);
			holder.tv_chatcontent_left = (TextView) convertView
					.findViewById(R.id.tv_chatcontent_left);
			holder.tv_chatcontent_left_no = (TextView) convertView
					.findViewById(R.id.tv_chatcontent_left_no);
			holder.tv_chatcontent_below_type = (TextView) convertView
					.findViewById(R.id.tv_chatcontent_below_type);
			holder.tv_chatcontent_below = (TextView) convertView
					.findViewById(R.id.tv_chatcontent_below);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 如果是发送的消息并且不是群聊消息，显示已读textview
		if (message.direct == EMMessage.Direct.SEND
				&& chatType != ChatType.GroupChat) {
			holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
			holder.tv_delivered = (TextView) convertView
					.findViewById(R.id.tv_delivered);
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
			if ((message.getType() == Type.TXT || message.getType() == Type.LOCATION)
					&& !message.isAcked && chatType != ChatType.GroupChat) {
				// 不是语音通话记录
				if (!message.getBooleanAttribute(
						Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
					try {
						EMChatManager.getInstance().ackMessageRead(
								message.getFrom(), message.getMsgId());
						// 发送已读回执
						message.isAcked = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		handleQuestionLinkMessage(message, holder);

		if (message.direct == EMMessage.Direct.SEND) {
			// View statusView = convertView.findViewById(R.id.msg_status);
			// 重发按钮点击事件
			holder.staus_iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogUtils.getMessageDialog(mContext,
							mContext.getString(R.string.resend),
							mContext.getString(R.string.confirm_resend), "确定",
							"取消", new OnDialogButtonClickListener() {

								@Override
								public void onItemClick(Dialog dialog, int btnId) {
									if (btnId == OnDialogButtonClickListener.BUTTON_CONFIRM) {
										SendUtils.resendMessage(
												mChatItem.mUserId, message);
										MsgCenter
												.sendLocalBroadcast(new Intent(
														EMChatFragment.MSG_REFRESH_CHAT_LIST));
									}
									dialog.dismiss();
								}
							}).show();
				}
			});

			ImageFetcher.getImageFetcher().loadImage(
					mLoginService.getLoginUser() == null ? ""
							: mLoginService.getLoginUser().headPhoto,
					holder.head_iv, R.drawable.default_img,
					new RoundedBitmapDisplayer(84));
		} else {
			ImageFetcher.getImageFetcher().loadImage(
					chatType != ChatType.GroupChat ? mChatItem.mHeadPhoto
							: message.getStringAttribute("userPhoto", ""),
					holder.head_iv, R.drawable.default_img,
					new RoundedBitmapDisplayer(84));
		}
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);
		if (mIndex == 0) {
			timestamp.setText(DateUtils.getTimestampString(new Date(message
					.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			if (DateUtils.isCloseEnough(message.getMsgTime(), mEmConversation
					.getMessage(mIndex - 1).getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(DateUtils.getTimestampString(new Date(message
						.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}

	@SuppressLint("SimpleDateFormat")
	private void handleQuestionLinkMessage(final EMMessage message,
			ViewHolder holder) {

		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		menuItems.add(new MenuItem(
				EMChatMenuLongClickListener.TYPE_DELETE, 0, "删除消息", ""));
		EMChatMenuLongClickListener longClickListener = new EMChatMenuLongClickListener(
				mContext, (EMChatListPiecesAdapter) getParentAdapter(),
				message, menuItems);
		try {
			holder.tv_chatcontent_left.setText(message.getStringAttribute("homeworkTitle", "暂无作业标题"));
			holder.tv_chatcontent_left_no.setText(DateUtil.getMonthDayString(Long.parseLong(message.getStringAttribute("homeworkStartTime", ""))));
//			holder.tv_chatcontent_right.setText(HomeworkUtils
//					.getSubjectNameByCode(message
//							.getIntAttribute("subjectCode")));
			String no = (Integer.parseInt(message.getStringAttribute("questionIndex", "0")) + 1) + "/" +
					(Integer.parseInt(message.getStringAttribute("questionCount", "0")));
			holder.tv_chatcontent_below.setText(no);

			holder.tv_chatcontent_below_type.setText(SubjectUtils.getNameByQuestionType(message.getIntAttribute("questionType", 0)));

			holder.msg_chatcontent_container
					.setOnLongClickListener(longClickListener);

			holder.msg_chatcontent_container
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							MessagePushUtils messagePushUtils = new MessagePushUtils(mFragment);
							messagePushUtils.handleMessage(message);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	 * 发送消息
	 * 
	 * @param message
	 * @param holder
	 */
	public void sendMsgInBackground(final EMMessage message,
			final ViewHolder holder) {
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

	/**
	 * 更新ui上消息发送状态
	 * 
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message,
			final ViewHolder holder) {
		UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				if (message.status == EMMessage.Status.SUCCESS) {

				} else if (message.status == EMMessage.Status.FAIL) {
					ToastUtils.showShortToast(mContext,
							mContext.getString(R.string.send_fail)
									+ mContext.getString(R.string.connect_failuer_toast));
				}

				notifyDataSetChanged();
			}
		});
	}

	public static class ViewHolder {
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView head_iv;
		TextView tv_userId;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;
		LinearLayout msg_chatcontent_container;
		TextView tv_chatcontent_left;
		TextView tv_chatcontent_left_no;
		TextView tv_chatcontent_below_type;
		TextView tv_chatcontent_below;
	}

}
