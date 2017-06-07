package com.buang.welewolf.modules.message;

import android.content.Context;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Created by weilei on 17/6/7.
 */

public class SealNotificationReceiver extends PushMessageReceiver{
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
        //TODO 自己处理通知点击
        return true;
    }
}
