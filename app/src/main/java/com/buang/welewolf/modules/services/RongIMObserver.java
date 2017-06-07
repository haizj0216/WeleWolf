package com.buang.welewolf.modules.services;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by weilei on 17/5/17.
 */

public class RongIMObserver {

    public List<OnRongIMConnectListener> rongIMConnectListeners = new ArrayList<>();
    public List<OnRongIMMessageListener> rongIMMessageListeners = new ArrayList<>();

    public void addOnRongIMConnectListener(OnRongIMConnectListener listener) {
        rongIMConnectListeners.add(listener);
    }

    public void removeOnRongIMConnectListener(OnRongIMConnectListener listener) {
        rongIMConnectListeners.remove(listener);
    }

    public void addOnRongIMMessageListener(OnRongIMMessageListener listener) {
        rongIMMessageListeners.add(listener);
    }

    public void removeOnRongIMMessageListener(OnRongIMMessageListener listener) {
        rongIMMessageListeners.remove(listener);
    }

    public void notifyOnConnectSuccess(String s) {
        for (int i = 0; i < rongIMConnectListeners.size(); i++) {
            rongIMConnectListeners.get(i).onLoginSuccess(s);
        }
    }

    public void notifyOnConnectError(RongIMClient.ErrorCode e) {
        for (int i = 0; i < rongIMConnectListeners.size(); i++) {
            rongIMConnectListeners.get(i).onLoginError(e);
        }
    }

    public void notifyDisConnect() {
        for (int i = 0; i < rongIMConnectListeners.size(); i++) {
            rongIMConnectListeners.get(i).onDisconnect();
        }
    }

    public void notifyLogout() {
        for (int i = 0; i < rongIMConnectListeners.size(); i++) {
            rongIMConnectListeners.get(i).onLoginOut();
        }
    }
    public void notifyConnectStatus(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        for (int i = 0; i < rongIMConnectListeners.size(); i++) {
            rongIMConnectListeners.get(i).onConnectStatus(status);
        }
    }

    public void notifyMessageReceived(Message message, int left) {
        for (int i = 0; i < rongIMMessageListeners.size(); i++) {
            rongIMMessageListeners.get(i).onReceivedMessage(message, left);
        }
    }

    public void notifyMessageSend(Message message) {
        for (int i = 0; i < rongIMMessageListeners.size(); i++) {
            rongIMMessageListeners.get(i).onMessageSend(message);
        }
    }

    public void notifyMessageSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        for (int i = 0; i < rongIMMessageListeners.size(); i++) {
            rongIMMessageListeners.get(i).onMessageSent(message, sentMessageErrorCode);
        }
    }
}
