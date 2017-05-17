package com.buang.welewolf.modules.services;

import io.rong.imlib.RongIMClient;

/**
 * Created by weilei on 17/5/17.
 */

public class RongIMServiceImp implements RongIMService {

    private static String token = "WV/a5Hda3Gd21JuAXiMfyNsXl1pzRNjW6JeLItQRxhIGTg9hcL5uTR5QQR9bQCtheW8jQzFYzyGQDxbEeboS/w==";
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
