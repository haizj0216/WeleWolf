/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.modules.login.services;

import com.knowbox.teacher.base.database.bean.UserItem;

/**
 * 用户状态改变回调
 * @author yangzc
 *
 */
public interface UserStateChangeListener {

	/**
	 * 登录成功
	 * @param user
	 */
	public void onLogin(UserItem user);
	
	/**
	 * 登录失败
	 * @param user
	 */
	public void onLogout(UserItem user);
}
