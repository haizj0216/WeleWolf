package com.buang.welewolf.modules.services;

import com.buang.welewolf.modules.utils.ToastUtils;
import com.hyena.framework.clientlog.LogUtil;

import io.rong.imlib.RongIMClient;

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
                LogUtil.d("RongUserID", "targetID:" + s);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                observer.notifyOnConnectError(errorCode);
            }
        });
    }

    @Override
    public void disConnect() {

    }

    @Override
    public void releaseAll() {

    }
}
