/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.teacher.modules.message.services;

import android.text.TextUtils;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.util.EasyUtils;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.message.utils.MessageNotifier;

import java.util.List;

/**
 * 环信消息服务实现类
 *
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

    public EMChatServiceImpl() {
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

        // 设置沙箱模式 慎用
//		EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(true);
        // 设置notification消息点击时，跳转的intent为自定义的intent

        EMClient.getInstance().init(BaseApp.getAppContext(), options);

        EMClient.getInstance().setDebugMode(false);
        mNotifier = new MessageNotifier();
        mNotifier.init(BaseApp.getAppContext());
        registReceiver();
        mSdkInited = true;
        return true;
    }

    @Override
    public void loginEMChat() {
        if (EMClient.getInstance().isLoggedInBefore()) {
            initEmchat();
            return;
        }

        LoginService service = (LoginService) BaseApp.getAppContext().getSystemService(LoginService.SERVICE_NAME);
        if (service == null) {
            return;
        }

        if (service.isLogin()) {
            final UserItem userItem = service.getLoginUser();
            if (TextUtils.isEmpty(userItem.userId) || TextUtils.isEmpty(userItem.loginName)) {
                getObserver().notifyEMChatLoginError(EMError.USER_NOT_FOUND, "");
                return;
            }
            EMClient.getInstance().login(userItem.userId, userItem.loginName, new EMCallBack() {
                @Override
                public void onSuccess() {
                    getObserver().notifyEMChatLoginSuccess();
                    initEmchat();
                }

                @Override
                public void onError(int i, String s) {
                    getObserver().notifyEMChatLoginError(i, s);
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }

    private void initEmchat() {
        try {
//			final UserItem userItem = Utils.getLoginUserItem();

            //第一次登录或者之前logout后再登录，加载所有本地群和回话
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            //获取群聊列表(群聊里只有GroupId和GroupName等简单信息，不包含members),SDK会把群组存入到内存和DB中
//			try {
//				EMGroupManager.getInstance().getGroupsFromServer();
//			} catch (EaseMobException e) {
//				e.printStackTrace();
//			}
            //注册连接状态监听器
            EMClient.getInstance().addConnectionListener(mEmConnectionListener);
            //添加联系人监听器
            EMClient.getInstance().contactManager().setContactListener(mEmContactListener);
            //注册群组状态变化监听器
            EMClient.getInstance().groupManager().addGroupChangeListener(mGroupChangeListener);
            //更新昵称
//			EMChatManager.getInstance().updateCurrentUserNick(userItem.userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logoutEMChat() {
        EMClient.getInstance().logout(true, new EMCallBack() {
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
        EMClient.getInstance().removeConnectionListener(mEmConnectionListener);
        //添加联系人监听器
        EMClient.getInstance().contactManager().removeContactListener(mEmContactListener);
        //注册群组状态变化监听器
        EMClient.getInstance().groupManager().removeGroupChangeListener(mGroupChangeListener);
    }

    @Override
    public EMChatServiceObserver getObserver() {
        return mChatServiceObserver;
    }

    private void registReceiver() {
        EMClient.getInstance().chatManager().addMessageListener(
                mListener);
    }

    private void unRegistReceiver() {
        //MsgCenter.unRegisterGlobalReceiver(mBroadcastReceiver);
        EMClient.getInstance().chatManager().removeMessageListener(mListener);
    }

    @Override
    public void releaseAll() {
        unRegistReceiver();
    }

    private EMMessageListener mListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> list) {
            for (EMMessage message : list) {
                sendNotifition(message);
                getObserver().notifyNewMessage(message);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageRead(List<EMMessage> list) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    private void sendNotifition(EMMessage message) {
        String username = null;
        //群组消息
        if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
            username = message.getTo();
        } else {
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
        public void onContactAdded(String s) {

        }

        @Override
        public void onContactDeleted(String s) {

        }

        @Override
        public void onContactInvited(String username, String reason) {
        }

        @Override
        public void onFriendRequestAccepted(String s) {

        }

        @Override
        public void onFriendRequestDeclined(String s) {

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

    private EMGroupChangeListener mGroupChangeListener = new EMGroupChangeListener() {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
        }

        @Override
        public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onRequestToJoinAccepted(String s, String s1, String s2) {

        }

        @Override
        public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {

        }

        @Override
        public void onInvitationAccepted(String s, String s1, String s2) {

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            // 提示用户被T了，demo省略此步骤
        }

        @Override
        public void onGroupDestroyed(String s, String s1) {

        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAdminAdded(String s, String s1) {

        }

        @Override
        public void onAdminRemoved(String s, String s1) {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {

        }

        @Override
        public void onMemberJoined(String s, String s1) {

        }

        @Override
        public void onMemberExited(String s, String s1) {

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
