/**
 * Copyright (C) 2014 The knowbox_Teacher Project
 */
package com.buang.welewolf.modules.login.services;

/**
 * 登出监听器
 * @author yangzc
 *
 */
public interface LogoutListener {

	/**
	 * 开始登出
	 */
	public void onLogoutStart();

	/**
	 * 登出成功
	 */
	public void onLogoutSuccess();

	/**
	 * 登出失败
	 */
	public void onLogoutFailed();
}