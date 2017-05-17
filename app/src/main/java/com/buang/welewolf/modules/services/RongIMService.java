package com.buang.welewolf.modules.services;

import com.hyena.framework.servcie.BaseService;

import java.util.List;

import io.rong.imlib.model.Conversation;

/**
 * Created by weilei on 17/5/17.
 */

public interface RongIMService extends BaseService {

    public static final String SERVICE_NAME = "com.buang_welewolf_rongim_service";

    public RongIMObserver getObserver();

    public void connect();

    public void disConnect();
}
