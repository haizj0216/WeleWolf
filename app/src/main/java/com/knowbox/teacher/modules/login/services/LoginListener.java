/**
 * Copyright (C) 2014 The knowbox_Teacher Project
 */
package com.knowbox.teacher.modules.login.services;

import com.knowbox.teacher.base.bean.OnlineLoginInfo;

/**
 * 登录监听器
 * @author yangzc
 *
 */
public interface LoginListener {

	/**
	 * 开始登陆
	 */
	public void onLoginStart();

	/**
	 * 登录成功
	 * @param loginInfo
	 */
	public void onLoginSuccess(OnlineLoginInfo loginInfo);

	/**
	 * 登录失败
	 * @param message
	 */
	public void onLoginFailed(String message);
}