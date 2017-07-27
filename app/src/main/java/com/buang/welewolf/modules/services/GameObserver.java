package com.buang.welewolf.modules.services;

import com.buang.welewolf.base.bean.OnlineRoleInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/7/14.
 */

public class GameObserver {

    public List<OnGVoiceListener> gVoiceListeners = new ArrayList<>();
    public List<OnSkillUpdateListener> onSkillUpdateListener = new ArrayList<>();

    public void addOnSkillUpdateListener(OnSkillUpdateListener listener) {
        onSkillUpdateListener.add(listener);
    }

    public void removeOnSkillUpdateListener(OnSkillUpdateListener listener) {
        onSkillUpdateListener.remove(listener);
    }

    public void notifySkillUpdate(OnlineRoleInfo from, OnlineRoleInfo to) {
        for (int i = 0; i < onSkillUpdateListener.size(); i++) {
            onSkillUpdateListener.get(i).onSkillUpdate(from, to);
        }
    }

    public void addOnGVoiceListener(OnGVoiceListener listener) {
        gVoiceListeners.add(listener);
    }

    public void removeOnGVoiceListener(OnGVoiceListener listener) {
        gVoiceListeners.remove(listener);
    }

    public void notifyOnJoinRoom(int i, String s, int i1) {
        for (int j = 0; j < gVoiceListeners.size(); j++) {
            gVoiceListeners.get(j).onJoinRoom(i, s, i1);
        }
    }

    public void notifyOnQuitRoom(int i, String s) {
        for (int j = 0; j < gVoiceListeners.size(); j++) {
            gVoiceListeners.get(j).onQuitRoom(i, s);
        }
    }

    public void notifyStatusUpdate(int i, String s, int i1) {
        for (int j = 0; j < gVoiceListeners.size(); j++) {
            gVoiceListeners.get(j).onStatusUpdate(i, s, i1);
        }
    }

    public void notifyOnMemberVoice(int[] ints, int i) {
        for (int j = 0; j < gVoiceListeners.size(); j++) {
            gVoiceListeners.get(j).onMemberVoice(ints, i);
        }
    }

    public void notifyUserSpeak() {
        for (int i = 0; i < gVoiceListeners.size(); i++) {
            gVoiceListeners.get(i).onUserSpeak();
        }
    }

    public void notifyUserSpeakEnd() {
        for (int i = 0; i < gVoiceListeners.size(); i++) {
            gVoiceListeners.get(i).onUserSpeakEnd();
        }
    }
}
