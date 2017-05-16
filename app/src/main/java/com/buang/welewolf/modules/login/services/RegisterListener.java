/**
 * Copyright (C) 2014 The knowbox_Teacher Project
 */
package com.buang.welewolf.modules.login.services;

import com.buang.welewolf.base.bean.OnlineLoginInfo;

/**
 * 注册监听器
 * @author yangzc
 *
 */
public interface RegisterListener {

	/**
	 * 开始注册
	 */
	public void onRegisterStart();

	/**
	 * 注册成功
	 */
	public void onRegisterSuccess(OnlineLoginInfo loginInfo);

	/**
	 * 注册失败
	 * @param message
	 */
	public void onRegisterFailed(String message);
}