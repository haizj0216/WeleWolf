package com.buang.welewolf.modules.services;

/**
 * Created by weilei on 17/7/14.
 */

public interface OnGVoiceListener {

    public void onJoinRoom(int i, String s, int i1);

    public void onQuitRoom(int i, String s);

    public void onStatusUpdate(int i, String s, int i1);

    public void onMemberVoice(int[] ints, int i);

    public void onUserSpeak();

    public void onUserSpeakEnd();
}
