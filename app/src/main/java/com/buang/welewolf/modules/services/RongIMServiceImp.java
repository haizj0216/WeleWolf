package com.buang.welewolf.modules.services;

import android.content.Context;
import android.view.View;

import com.buang.welewolf.modules.utils.ToastUtils;
import com.hyena.framework.clientlog.LogUtil;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by weilei on 17/5/17.
 */

public class RongIMServiceImp implements RongIMService {

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
        RongIM.setConnectionStatusListener(connectionStatusListener);

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
    public void startConversation(Context context, Conversation.ConversationType conversationType, String targetId, String title) {
        RongIM.getInstance().startConversation(context, conversationType, targetId, title);
    }

    @Override
    public void releaseAll() {

    }
}
