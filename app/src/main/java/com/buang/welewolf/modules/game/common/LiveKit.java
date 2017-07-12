package com.buang.welewolf.modules.game.common;

import android.content.Context;
import android.os.Handler;

import com.buang.welewolf.modules.game.message.BaseMsgView;
import com.buang.welewolf.modules.game.message.GiftMessage;
import com.buang.welewolf.modules.game.message.GiftMsgView;
import com.buang.welewolf.modules.game.message.InfoMsgView;
import com.buang.welewolf.modules.game.message.TextMsgView;

import java.util.ArrayList;
import java.util.HashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

/**
 * LiveKit是融云直播聊天室Demo对IMLib库的接口封装类。目的是在IMLib库众多通用接口中，提炼出与直播聊天室应用相关的常用接口，
 * 方便开发者了解IMLib库的调用流程，降低学习成本。同时也开发者可以此为基础扩展，并快速移植到自己的应用中去。
 * <p/>
 * <strong>注意：</strong>此种封装并不是集成IMLib库的唯一方法，开发者可以根据自身需求添加修改，或者直接使用IMLib接口。
 */

public class LiveKit {

    /**
     * 消息类与消息UI展示类对应表
     */
    private static HashMap<Class<? extends MessageContent>, Class<? extends BaseMsgView>> msgViewMap = new HashMap<>();

    /**
     * 当前登录用户id
     */
    private static UserInfo currentUser;

    /**
     * 当前聊天室房间id
     */
    private static String currentRoomId;

    /**
     * 初始化方法，在整个应用程序全局只需要调用一次，建议在Application 继承类中调用。
     * <p/>
     * <strong>注意：</strong>其余方法都需要在这之后调用。
     *
     * @param context Application类的Context
     */
    public static void init(Context context) {
        EmojiManager.init(context);

        registerMessageType(GiftMessage.class);
        registerMessageView(TextMessage.class, TextMsgView.class);
        registerMessageView(InformationNotificationMessage.class, InfoMsgView.class);
        registerMessageView(GiftMessage.class, GiftMsgView.class);
    }

    /**
     * 设置当前登录用户，通常由注册生成，通过应用服务器来返回。
     *
     * @param user 当前用户
     */
    public static void setCurrentUser(UserInfo user) {
        currentUser = user;
    }

    /**
     * 获得当前登录用户。
     *
     * @return
     */
    public static UserInfo getCurrentUser() {
        return currentUser;
    }

    /**
     * 注册自定义消息。
     *
     * @param msgType 自定义消息类型
     */
    public static void registerMessageType(Class<? extends MessageContent> msgType) {
        try {
            RongIMClient.registerMessageType(msgType);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册消息展示界面类。
     *
     * @param msgContent 消息类型
     * @param msgView    对应的界面展示类
     */
    public static void registerMessageView(Class<? extends MessageContent> msgContent, Class<? extends BaseMsgView> msgView) {
        msgViewMap.put(msgContent, msgView);
    }

    /**
     * 获取注册消息对应的UI展示类。
     *
     * @param msgContent 注册的消息类型
     * @return 对应的UI展示类
     */
    public static Class<? extends BaseMsgView> getRegisterMessageView(Class<? extends MessageContent> msgContent) {
        return msgViewMap.get(msgContent);
    }

    /**
     * 加入聊天室。如果聊天室不存在，sdk 会创建聊天室并加入，如果已存在，则直接加入。加入聊天室时，可以选择拉取聊天室消息数目。
     *
     * @param roomId          聊天室 Id
     * @param defMessageCount 默认开始时拉取的历史记录条数
     * @param callback        状态回调
     */
    public static void joinChatRoom(String roomId, int defMessageCount, final RongIMClient.OperationCallback callback) {
        currentRoomId = roomId;
        RongIMClient.getInstance().joinChatRoom(currentRoomId, defMessageCount, callback);
    }

    /**
     * 退出聊天室，不在接收其消息。
     */
    public static void quitChatRoom(final RongIMClient.OperationCallback callback) {
        RongIMClient.getInstance().quitChatRoom(currentRoomId, callback);
    }

    /**
     * 向当前聊天室发送消息。
     * </p>
     * <strong>注意：</strong>此函数为异步函数，发送结果将通过handler事件返回。
     *
     * @param msgContent 消息对象
     */
    public static void sendMessage(final MessageContent msgContent) {
        if (currentUser == null) {
            throw new RuntimeException("currentUser should not be null.");
        }

        msgContent.setUserInfo(currentUser);
        Message msg = Message.obtain(currentRoomId, Conversation.ConversationType.CHATROOM, msgContent);
        RongIM.getInstance().sendMessage(msg, null, null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {

            }

            @Override
            public void onSuccess(Message message) {

            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {

            }
        });
    }

}
