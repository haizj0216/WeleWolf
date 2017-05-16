package com.buang.welewolf.modules.login.services;

import com.buang.welewolf.base.database.bean.UserItem;

/**
 * Created by LiuYu on 2017/2/15.
 */
public interface UpdateSchoolListener {

    public abstract void onUpdateSuccess(UserItem userItem, boolean isUserDefined);

    public abstract void onUpdateFailed(String error, boolean isUserDefined);
}
