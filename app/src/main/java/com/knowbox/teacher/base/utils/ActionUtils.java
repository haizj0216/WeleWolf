package com.knowbox.teacher.base.utils;

import android.content.Intent;

import com.hyena.framework.utils.MsgCenter;

import java.util.Hashtable;

/**
 * Created by weilei on 15/9/23.
 */
public class ActionUtils {

    public static final String ACTION_VIRTUAL_TIPS = "com.knowbox.teacher.action_virtualtip";
    public static final String ACTION_SUBJECTINFO_CHANGE = "com.knowbox.subjectinfo.changed";
    public static final String ACTION_MAIN_TAB = "com.knowbox.teacher.main_tab";
    public static final String ACTION_CLASS_TRANSFER_CHANGED = "com.knowbox.teacher_class_transfer_changed";
    public static final String ACTION_MATCH_CHANGED = "com.knowbox.teacher_match_changed";
    public static final String ACTION_SHOW_REDBADGE = "com.knowbox.teacher_red_badge";

    public static void notifyVirtualTip() {
        MsgCenter.sendLocalBroadcast(new Intent(ACTION_VIRTUAL_TIPS));
    }

    public static void notifySubjectPartChanged(String gradePart) {
        Intent intent = new Intent(ACTION_SUBJECTINFO_CHANGE);
        intent.putExtra("gradepart", gradePart);
        MsgCenter.sendLocalBroadcast(intent);
    }

    public static void notifyMainTab(int tab, Hashtable<String, String> paramsMap) {
        Intent intent = new Intent(ACTION_MAIN_TAB);
        intent.putExtra("tab", tab);
        intent.putExtra("params", paramsMap);
        MsgCenter.sendLocalBroadcast(intent);
    }

    public static void motifyClassTransferChanged() {
        MsgCenter.sendLocalBroadcast(new Intent(ACTION_CLASS_TRANSFER_CHANGED));
    }

    public static void notifyMatchChanged() {
        MsgCenter.sendLocalBroadcast(new Intent(ACTION_MATCH_CHANGED));
    }

    public static void notifyShowRedBadge(String type) {
        Intent intent = new Intent(ACTION_SHOW_REDBADGE);
        intent.putExtra("type", type);
        MsgCenter.sendLocalBroadcast(intent);
    }

}
