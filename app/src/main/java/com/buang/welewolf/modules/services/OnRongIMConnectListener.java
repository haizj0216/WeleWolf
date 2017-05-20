package com.buang.welewolf.modules.services;

import io.rong.imlib.RongIMClient;

/**
 * Created by weilei on 17/5/17.
 */

public interface OnRongIMConnectListener {

    public void onLoginSuccess(String s);

    public void onLoginError(RongIMClient.ErrorCode errorCode);

    public void onLoginOut();

    public void onDisconnect();

    public void onConnectStatus(RongIMClient.ConnectionStatusListener.ConnectionStatus status);
}
