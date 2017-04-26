/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.teacher.modules.message.services;

import android.text.TextUtils;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.util.EasyUtils;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.message.utils.MessageNotifier;

import java.util.List;

/**
 * 环信消息服务实现类
 * @author yangzc
 */
public class EMChatServiceImpl implements EMChatService {

	//SDK是否已经初始化
	private static boolean mSdkInited = false;
	//监听器
	private EMChatServiceObserver mChatServiceObserver = new EMChatServiceObserver();
	
	private MessageNotifier mNotifier;
	
	private String mUserId;

	private final String XIAOMI_PUSH_APP_ID = "2882303761517550899";
	private final String XIAOMI_PUSH_APP_KEY = "5451755029899";
	
	public EMChatServiceImpl(){
//		initEMChat();
//		registReceiver();
	}
	
	@Override
	public boolean initEMChat() {
		if (mSdkInited) {
			return true;
		}
		// check process
		// 初始化EM聊天
		EMChat.getInstance().init(BaseApp.getAppContext());
		// 设置沙箱模式 慎用
//		EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
		// 开始Debug模式
		EMChat.getInstance().setDebugMode(true);
		EMChat.getInstance().setAutoLogin(false);
		
		// 获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		// 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
		options.setUseRoster(false);
		// 设置收到消息是否有新消息通知(声音和震动提示)，默认为true
		options.setNotifyBySoundAndVibrate(true);
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(true);
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(true);
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(true);
		// 设置是否需要已读回执
		options.setRequireAck(true);
		// 设置是否需要已送达回执
		options.setRequireDeliveryAck(true);
		// 设置notification消息点击时，跳转的intent为自定义的intent
//		options.setOnNotificationClickListener(getNotificationClickListener());
		options.setNotifyText(new OnMessageNotifyListener() {
			@Override
			public int onSetSmallIcon(EMMessage message) {
				return 0;
			}
			
			@Override
			public String onSetNotificationTitle(EMMessage message) {
				return null;
			}

			@Override
			public String onLatestMessageNotify(EMMessage message, int arg1, int arg2) {
				return null;
			}
			
			@Override
			public String onNewMessageNotify(EMMessage message) {
				if(message == null)
					return null;
				
				return message.getStringAttribute("userName", "") + "发来一条消息";
			}
			
		});
		
		mNotifier = new MessageNotifier();
		mNotifier.init(BaseApp.getAppContext());
		registReceiver();
        mSdkInited = true;
		return true;
	}
	
	@Override
	public void loginEMChat() {
		if (EMChat.getInstance().isLoggedIn()) {
			initEmchat();
			return;
		}

		LoginService service = (LoginService) BaseApp.getAppContext().getSystemService(LoginService.SERVICE_NAME);
		if(service == null){
			return;
		}

		if(service.isLogin()) {
			final UserItem userItem = service.getLoginUser();
			if (TextUtils.isEmpty(userItem.userId) || TextUtils.isEmpty(userItem.loginName)) {
				getObserver().notifyEMChatLoginError(EMError.INVALID_PASSWORD_USERNAME, "");
				return;
			}
			EMChatManager.getInstance().login(userItem.userId, userItem.loginName, new EMCallBack() {
				@Override
				public void onSuccess() {
					getObserver().notifyEMChatLoginSuccess();
					initEmchat();
				}

				@Override
				public void onProgress(int arg0, String arg1) {}
				@Override
				public void onError(int errorCode, String message) {
					getObserver().notifyEMChatLoginError(errorCode, message);
				}
			});
		}
	}

	private void initEmchat() {
		try {
//			final UserItem userItem = Utils.getLoginUserItem();

			//第一次登录或者之前logout后再登录，加载所有本地群和回话
			EMGroupManager.getInstance().loadAllGroups();
			EMChatManager.getInstance().loadAllConversations();
			//获取群聊列表(群聊里只有GroupId和GroupName等简单信息，不包含members),SDK会把群组存入到内存和DB中
//			try {
//				EMGroupManager.getInstance().getGroupsFromServer();
//			} catch (EaseMobException e) {
//				e.printStackTrace();
//			}
			//注册连接状态监听器
			EMChatManager.getInstance().addConnectionListener(mEmConnectionListener);
			//小米手机推送配置
			EMChatManager.getInstance().setMipushConfig(XIAOMI_PUSH_APP_ID, XIAOMI_PUSH_APP_KEY);
			//添加联系人监听器
			EMContactManager.getInstance().setContactListener(mEmContactListener);
			//注册群组状态变化监听器
			EMGroupManager.getInstance().addGroupChangeListener(mGroupChangeListener);
			//通知SDK，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
			EMChat.getInstance().setAppInited();
			//更新昵称
//			EMChatManager.getInstance().updateCurrentUserNick(userItem.userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void logoutEMChat() {
		EMChatManager.getInstance().logout(true, new EMCallBack() {
			@Override
			public void onSuccess() {

			}

			@Override
			public void onError(int i, String s) {

			}

			@Override
			public void onProgress(int i, String s) {

			}
		});
		EMChatManager.getInstance().removeConnectionListener(mEmConnectionListener);
		//添加联系人监听器
		EMContactManager.getInstance().removeContactListener();
		//注册群组状态变化监听器
		EMGroupManager.getInstance().removeGroupChangeListener(mGroupChangeListener);
	}

	@Override
	public EMChatServiceObserver getObserver() {
		return mChatServiceObserver;
	}
	
	private void registReceiver(){
		EMChatManager.getInstance().registerEventListener(
				mListener,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventDeliveryAck,
						EMNotifierEvent.Event.EventReadAck,
						EMNotifierEvent.Event.EventNewCMDMessage });
		/*IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		MsgCenter.registerGlobalReceiver(mBroadcastReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		MsgCenter.registerGlobalReceiver(mBroadcastReceiver, ackMessageIntentFilter);
		
		//注册一个透传消息的BroadcastReceiver
		IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
		cmdMessageIntentFilter.setPriority(3);
		MsgCenter.registerGlobalReceiver(mBroadcastReceiver, cmdMessageIntentFilter);*/

	}
	
	private void unRegistReceiver(){
		//MsgCenter.unRegisterGlobalReceiver(mBroadcastReceiver);
		EMChatManager.getInstance().unregisterEventListener(mListener);
	}
	
	@Override
	public void releaseAll() {
		unRegistReceiver();
	}
	
	private EMEventListener mListener = new EMEventListener() {

		@Override
		public void onEvent(EMNotifierEvent event) {
			//获取到message
			EMMessage message = (EMMessage) event.getData();
			switch (event.getEvent()) {
			case EventNewMessage:
				sendNotifition(message);
				getObserver().notifyNewMessage(message);
				break;
			case EventDeliveryAck:
			case EventReadAck:
				if (message != null) {
					message.isAcked = true;
				}
				break;
			case EventNewCMDMessage:

				break;
			default:
				break;
			}
		}
	};
	
	private void sendNotifition(EMMessage message) {
        String username = null;
        //群组消息
        if(message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom){
            username = message.getTo();
        } else{
            //单聊消息
            username = message.getFrom();
        }

        //如果是当前会话的消息，刷新聊天页面
		if (EasyUtils.isAppRunningForeground(BaseApp.getAppContext())) {
			if (!TextUtils.isEmpty(message.getStringAttribute("apns", ""))
					&& message.getIntAttribute("status", -1) == 1) {
				getObserver().notifyNewApnsMessage(message);
			} else {
				mNotifier.onNewMsg(message);
			}
		} else {
			mNotifier.onNewMsg(message);
		}
	}
	
//	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			LogUtil.v("onReceive, action: " + intent.getAction());
//			if(EMChatManager.getInstance().getNewMessageBroadcastAction().equals(action)){
//				// 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看
////				String from = intent.getStringExtra("from");
//				// 消息id
//				String msgId = intent.getStringExtra("msgid");
//				EMMessage message = EMChatManager.getInstance().getMessage(msgId);
//				// 注销广播接收者，否则在ChatActivity中会收到这个广播
//				abortBroadcast();
////				String userId = message.getFrom();
////				UserInfo userInfo = new UserInfo();
////				userInfo.mHeadPhoto = message.getStringAttribute("userPhoto", "");
////				userInfo.mUserName = message.getStringAttribute("userName", "");
////				com.knowbox.wb.student.message.service.im.UserInfoManager.getInstance().updateUserInfo(userId, userInfo);
//				getObserver().notifyNewMessage(message);
//			}else if(EMChatManager.getInstance().getAckMessageBroadcastAction().equals(action)){
//				abortBroadcast();
//				String msgid = intent.getStringExtra("msgid");
//				String from = intent.getStringExtra("from");
//				EMConversation conversation = EMChatManager.getInstance().getConversation(from);
//				if (conversation != null) {
//					// 把message设为已读
//					EMMessage msg = conversation.getMessage(msgid);
//					if (msg != null) {
//						msg.isAcked = true;
//					}
//				}
//			}else if(EMChatManager.getInstance().getCmdMessageBroadcastAction().equals(action)){
//				EMMessage message = intent.getParcelableExtra("message");
//				PushInfoItem pInfo = new PushInfoItem();
//				pInfo.classId = message.getStringAttribute("classID", "");
//				pInfo.msgType = message.getStringAttribute("type", "");
//				pInfo.pushNum = "1";
//
//				PushService pushService = (PushService) BaseApp.getAppContext()
//						.getSystemService(PushService.SERVICE_NAME);
//				pushService.onReceivePushInfo(pInfo);
//			}
//		}
//	};
	
	private EMContactListener mEmContactListener = new EMContactListener() {
		
		@Override
		public void onContactAdded(List<String> usernameList) {
		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
		}

		@Override
		public void onContactInvited(String username, String reason) {
		}

		@Override
		public void onContactAgreed(String username) {
		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能
		}
	};
	
	private EMConnectionListener mEmConnectionListener = new EMConnectionListener() {
		
		@Override
		public void onDisconnected(final int error) {
			LogUtil.v("yangzc", "onDisconnected");
			getObserver().notifyEMDisConnection(error);
		}
		
		@Override
		public void onConnected() {
			getObserver().notifyEMConnectioned();
		}
	};
	
	private GroupChangeListener mGroupChangeListener = new GroupChangeListener() {
		
		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
			// 用户申请加入群聊
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {
			// 加群申请被同意
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}
	};

	@Override
	public void setCurrentUserId(String userId) {
		// TODO Auto-generated method stub
		mUserId = userId;
	}

	@Override
	public String getCurrentUserId() {
		// TODO Auto-generated method stub
		return mUserId;
	}
}
