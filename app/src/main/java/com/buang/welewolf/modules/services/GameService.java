package com.buang.welewolf.modules.services;

import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.base.bean.OnlineRoomInfo;
import com.hyena.framework.servcie.BaseService;

import java.util.List;

/**
 * Created by weilei on 17/6/21.
 */

public interface GameService extends BaseService {
    public static final String SERVICE_NAME = "com.buang.welewolf_game";

    public void setOnlineRoomInfo(OnlineRoomInfo roomInfo);

    public OnlineRoomInfo getOnlineRoomInfo();

    public OnlineRoleInfo getRoleInfo(String userID);

    public OnlineRoleInfo getMyRoleInfo();

    public List<OnlineRoleInfo> getWolfInfos();

    public void wolfKill(String from, String to);

    public List<OnlineRoleInfo> killedByWolfList(String userId);
}
