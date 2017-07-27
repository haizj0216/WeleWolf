package com.buang.welewolf.modules.services;

import android.text.TextUtils;

import com.buang.welewolf.MainActivity;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.base.bean.OnlineRoomInfo;
import com.buang.welewolf.modules.utils.WolfUtils;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.tencent.gcloud.voice.GCloudVoiceEngine;
import com.tencent.gcloud.voice.GCloudVoiceErrno;
import com.tencent.gcloud.voice.IGCloudVoiceNotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by weilei on 17/6/21.
 */

public class GameServiceImp implements GameService {

    private final String APP_ID = "1559197809";
    private final String APP_KEY = "6c98efa464319bd371c1f475171a7d46";

    private GameObserver observer = new GameObserver();

    private OnlineRoomInfo onlineRoomInfo;
    private HashMap<String, OnlineRoleInfo> roleInfoHashMap = new HashMap<>();
    private HashMap<String, List<OnlineRoleInfo>> mWolfKills = new HashMap<>();

    private GCloudVoiceEngine engine = null;

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
        getObserver().notifySkillUpdate(wolf, getRoleInfo(to));
    }

    @Override
    public List<OnlineRoleInfo> killedByWolfList(String userId) {
        return mWolfKills.get(userId);
    }

    @Override
    public void initGVoice() {
        if (engine == null) {
            engine = GCloudVoiceEngine.getInstance();
            engine.SetAppInfo(APP_ID, APP_KEY, Long.toString(System.currentTimeMillis()));
            engine.Init();
            engine.SetMode(GCloudVoiceEngine.Mode.RealTime);

            engine.SetNotify(new Notify());
        }
    }

    @Override
    public int joinGVoiceRoom(String roomID) {
        if (engine != null) {
            int result = engine.JoinTeamRoom(roomID, 10000);
            LogUtil.d("GameService", "joinGVoiceRoom:" + result);
            return result;
        }
        return -1;
    }

    @Override
    public int exitGVoiceRoom(String roomID) {
        if (engine != null) {
            return engine.QuitRoom(roomID, 10000);
        }
        return -1;
    }

    @Override
    public int openGVoiceMic() {
        if (engine != null) {
            int result = engine.OpenMic();
            LogUtil.d("GameService", "openGVoiceMic:" + result);
            return engine.OpenMic();
        }
        return -1;
    }

    @Override
    public int openGVoiceSpeak() {
        if (engine != null) {
            return engine.OpenSpeaker();
        }
        return -1;
    }

    @Override
    public int closeGVoiceMic() {
        if (engine != null) {
            return engine.CloseMic();
        }
        return -1;
    }

    @Override
    public int closeGVoiceSpeak() {
        if (engine != null) {
            int result = engine.CloseSpeaker();
            return result;
        }
        return -1;
    }

    @Override
    public void startSpeak() {
        getObserver().notifyUserSpeak();
    }

    @Override
    public void endSpeak() {
        getObserver().notifyUserSpeakEnd();
    }

    @Override
    public int getMinLevel() {
        if (engine != null) {
            return engine.GetMicLevel();
        }
        return 0;
    }

    @Override
    public GameObserver getObserver() {
        return observer;
    }

    class Notify implements IGCloudVoiceNotify {

        @Override
        public void OnJoinRoom(int i, String s, int i1) {
            getObserver().notifyOnJoinRoom(i, s, i1);
        }

        @Override
        public void OnStatusUpdate(int i, String s, int i1) {
            getObserver().notifyStatusUpdate(i, s, i1);
        }

        @Override
        public void OnQuitRoom(int i, String s) {
            getObserver().notifyOnQuitRoom(i, s);
        }

        @Override
        public void OnMemberVoice(int[] ints, int i) {
            getObserver().notifyOnMemberVoice(ints, i);
        }

        @Override
        public void OnUploadFile(int i, String s, String s1) {

        }

        @Override
        public void OnDownloadFile(int i, String s, String s1) {

        }

        @Override
        public void OnPlayRecordedFile(int i, String s) {

        }

        @Override
        public void OnApplyMessageKey(int i) {

        }

        @Override
        public void OnSpeechToText(int i, String s, String s1) {

        }

        @Override
        public void OnRecording(char[] chars, int i) {

        }

        @Override
        public void OnStreamSpeechToText(int i, int i1, String s) {

        }
    }
}
