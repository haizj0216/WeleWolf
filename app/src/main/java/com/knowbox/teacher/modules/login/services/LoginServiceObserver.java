/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.modules.login.services;

import com.knowbox.teacher.base.database.bean.UserItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录服务监听器
 *
 * @author yangzc
 */
public class LoginServiceObserver {

    private List<UserStateChangeListener> mLoginStateListeners = new ArrayList<UserStateChangeListener>();
    private List<UpdateJiaocaiListener> mUpdateJiaocaiListeners = new ArrayList<UpdateJiaocaiListener>();
    private List<UpdateSchoolListener> mUpdateSchoolListeners = new ArrayList<UpdateSchoolListener>();

    public void addUserStateChangeListener(UserStateChangeListener listener) {
        if (!mLoginStateListeners.contains(listener)) {
            mLoginStateListeners.add(listener);
        }
    }

    public void removeUserStateChangeListener(UserStateChangeListener listener) {
        mLoginStateListeners.remove(listener);
    }

    public void addUpdatejiaocaiListener(UpdateJiaocaiListener listener) {
        if (!mUpdateJiaocaiListeners.contains(listener)) {
            mUpdateJiaocaiListeners.add(listener);
        }
    }

    public void removeUpdateJiaocaiListener(UpdateJiaocaiListener listener) {
        mUpdateJiaocaiListeners.remove(listener);
    }

    public void notifyUpdateJiaocaiSuccess(UserItem userItem, String clazzName) {
        for (UpdateJiaocaiListener listener : mUpdateJiaocaiListeners) {
            listener.onUpdateSuccess(userItem, clazzName);
        }
    }

    public void notifyUpdateJiaocaiFailed(String error, String clazzName) {
        for (UpdateJiaocaiListener listener : mUpdateJiaocaiListeners) {
            listener.onUpdateFailed(error, clazzName);
        }
    }

    public void addUpdateSchoolListener(UpdateSchoolListener listener) {
        if (!mUpdateSchoolListeners.contains(listener)) {
            mUpdateSchoolListeners.add(listener);
        }
    }

    public void removeUpdateSchoolListener(UpdateSchoolListener listener) {
        mUpdateSchoolListeners.remove(listener);
    }

    public void notifyUpdateSchoolSuccess(UserItem userItem, boolean isUserDefined) {
        for (UpdateSchoolListener listener : mUpdateSchoolListeners) {
            listener.onUpdateSuccess(userItem, isUserDefined);
        }
    }

    public void notifyUpdateSchoolFailed(String error, boolean isUserDefined) {
        for (UpdateSchoolListener listener : mUpdateSchoolListeners) {
            listener.onUpdateFailed(error, isUserDefined);
        }
    }

    public void notifyOnLogin(UserItem user) {
        for (UserStateChangeListener listener : mLoginStateListeners) {
            listener.onLogin(user);
        }
    }

    public void notifyOnLogout(UserItem user) {
        for (int i = 0; i < mLoginStateListeners.size(); i++) {
            UserStateChangeListener listener = mLoginStateListeners.get(i);
            if (listener != null) {
                listener.onLogout(user);
            }
        }
    }
}
