package com.buang.welewolf.modules.login.services;

import com.buang.welewolf.base.bean.OnlineLoginInfo;

/**
 * 短信验证登录监听
 * Created by LiuYu on 2016/6/4.
 */
public interface SmsLoginListener {

	/**
	 * 开始登陆
	 */
	public void onSmsLoginStart();

	/**
	 * 登录成功
	 * @param loginInfo
	 */
	public void onSmsLoginSuccess(OnlineLoginInfo loginInfo);

	/**
	 * 登录失败
	 * @param message
	 */
	public void onSmsLoginFailed(String message);
}