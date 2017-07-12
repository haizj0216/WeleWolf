package com.buang.welewolf.modules.services;

import android.text.TextUtils;

import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.base.bean.OnlineRoomInfo;
import com.buang.welewolf.modules.utils.WolfUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by weilei on 17/6/21.
 */

public class GameServiceImp implements GameService {
    private OnlineRoomInfo onlineRoomInfo;
    private HashMap<String, OnlineRoleInfo> roleInfoHashMap = new HashMap<>();
    private HashMap<String, List<OnlineRoleInfo>> mWolfKills = new HashMap<>();

    @Override
    public void releaseAll() {
        roleInfoHashMap.clear();
        mWolfKills.clear();
    }

    @Override
    public void setOnlineRoomInfo(OnlineRoomInfo roomInfo) {
        onlineRoomInfo = roomInfo;
        roleInfoHashMap.clear();
        if (onlineRoomInfo.roleInfos != null) {
            for (int i = 0; i < onlineRoomInfo.roleInfos.size(); i++) {
                OnlineRoleInfo roleInfo = onlineRoomInfo.roleInfos.get(i);
                roleInfoHashMap.put(roleInfo.userID, roleInfo);
            }
        }
    }

    @Override
    public OnlineRoomInfo getOnlineRoomInfo() {
        return onlineRoomInfo;
    }

    @Override
    public OnlineRoleInfo getRoleInfo(String userID) {
        if (roleInfoHashMap != null) {
            return roleInfoHashMap.get(userID);
        }
        return null;
    }

    @Override
    public OnlineRoleInfo getMyRoleInfo() {
        return onlineRoomInfo.myRole;
    }

    @Override
    public List<OnlineRoleInfo> getWolfInfos() {
        if (onlineRoomInfo.roleInfos != null) {
            List<OnlineRoleInfo> roleInfos = new ArrayList<>();
            for (int i = 0; i < onlineRoomInfo.roleInfos.size(); i++) {
                OnlineRoleInfo roleInfo = onlineRoomInfo.roleInfos.get(i);
                if (roleInfo.roleType == WolfUtils.GAME_ROLE_WOLF) {
                    roleInfos.add(roleInfo);
                }
            }
            return roleInfos;
        }
        return null;
    }

    @Override
    public void wolfKill(String from, String to) {
        OnlineRoleInfo wolf = roleInfoHashMap.get(from);
        String oldUser = wolf.killUserID;
        if (!TextUtils.isEmpty(oldUser)) {
            List<OnlineRoleInfo> oldRoles = killedByWolfList(oldUser);
            oldRoles.remove(wolf);
        }

        List<OnlineRoleInfo> list = mWolfKills.get(to);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(wolf);
        wolf.killUserID = to;
        mWolfKills.put(to, list);
    }

    @Override
    public List<OnlineRoleInfo> killedByWolfList(String userId) {
        return mWolfKills.get(userId);
    }
}
