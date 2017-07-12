package com.buang.welewolf.modules.services;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.buang.welewolf.modules.message.bean.CmdMEssage;
import com.buang.welewolf.modules.message.bean.CustomizeMessage;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.hyena.framework.clientlog.LogUtil;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by weilei on 17/5/17.
 */

public class RongIMServiceImp implements RongIMService {

    private static final String TAG = "RongIMServiceImp";
    private static String token = "lhWoeEat+RRfp7qIKUPZYdsXl1pzRNjW6JeLItQRxhIGTg9hcL5uTaRwli+WfUp1x5LXci0Iu8vYYXzvQu/OXg==";
    private RongIMObserver observer = new RongIMObserver();

    @Override
    public RongIMObserver getObserver() {
        return observer;
    }

    @Override
    public void connect() {
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                observer.notifyOnConnectError(null);
            }

            @Override
            public void onSuccess(String s) {
                observer.notifyOnConnectSuccess(s);
                initRongIM();
                LogUtil.d("RongUserID", "targetID:" + s);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                observer.notifyOnConnectError(errorCode);
            }
        });
    }

    private void initRongIM() {
        RongIM.registerMessageType(CmdMEssage.class);
        RongIM.registerMessageType(CustomizeMessage.class);
        RongIM.setConnectionStatusListener(connectionStatusListener);
        RongIM.getInstance().setSendMessageListener(new RongIM.OnSendMessageListener() {
            @Override
            public Message onSend(Message message) {
                getObserver().notifyMessageSend(message);
                return message;
            }

            @Override
            public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
                getObserver().notifyMessageSent(message, sentMessageErrorCode);
                if (message.getSentStatus() == Message.SentStatus.FAILED) {
                    if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_CHATROOM) {
                        //不在聊天室
                    } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_DISCUSSION) {
                        //不在讨论组
                    } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_GROUP) {
                        //不在群组
                    } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.REJECTED_BY_BLACKLIST) {
                        //你在他的黑名单中
                    }
                }

                MessageContent messageContent = message.getContent();

                if (messageContent instanceof TextMessage) {//文本消息
                    TextMessage textMessage = (TextMessage) messageContent;
                    Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
                } else if (messageContent instanceof ImageMessage) {//图片消息
                    ImageMessage imageMessage = (ImageMessage) messageContent;
                    Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
                } else if (messageContent instanceof VoiceMessage) {//语音消息
                    VoiceMessage voiceMessage = (VoiceMessage) messageContent;
                    Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
                } else if (messageContent instanceof RichContentMessage) {//图文消息
                    RichContentMessage richContentMessage = (RichContentMessage) messageContent;
                    Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
                } else {
                    Log.d(TAG, "onSent-其他消息，自己来判断处理");
                }
                return false;
            }
        });
        RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                getObserver().notifyMessageReceived(message, i);
                return false;
            }
        });
    }

    RongIMClient.ConnectionStatusListener connectionStatusListener = new RongIMClient.ConnectionStatusListener() {

        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
            observer.notifyConnectStatus(connectionStatus);
        }
    };

    /**
     * 断开连接，扔可收到推送
     */
    @Override
    public void disConnect() {
        RongIM.getInstance().disconnect();
        getObserver().notifyDisConnect();
    }

    /**
     * 退出登录，无法接受推送
     */
    @Override
    public void logout() {
        RongIM.getInstance().logout();
        getObserver().notifyLogout();
    }

    @Override
    public void sendMessage(MessageContent messageContent) {
    }

    @Override
    public void startConversation(Context context, Conversation.ConversationType conversationType, String targetId, String title) {
        RongIM.getInstance().startConversation(context, conversationType, targetId, title);
    }

    @Override
    public void releaseAll() {

    }
}
