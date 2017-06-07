package com.buang.welewolf.modules.services;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Message;

/**
 * Created by weilei on 17/6/7.
 */

public interface OnRongIMMessageListener {

    public void onReceivedMessage(Message message, int i);

    public void onMessageSend(Message message);

    public void onMessageSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode);
}
