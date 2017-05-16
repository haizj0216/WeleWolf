package com.buang.welewolf.modules.login.services;

import com.buang.welewolf.base.database.bean.UserItem;

/**
 * Created by LiuYu on 2016/8/11.
 */
public interface UpdateJiaocaiListener {

    public abstract void onUpdateSuccess(UserItem userItem, String clazzName);

    public abstract void onUpdateFailed(String error, String clazzName);
}
