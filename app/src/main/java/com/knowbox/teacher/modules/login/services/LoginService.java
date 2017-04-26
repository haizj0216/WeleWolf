/**
 * Copyright (C) 2014 The knowbox_Teacher Project
 */
package com.knowbox.teacher.modules.login.services;

import com.hyena.framework.servcie.BaseService;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.modules.login.services.LoginServiceImpl.RegistUserInfoBuilder;
import com.knowbox.teacher.widgets.AdvanceTimer;

/**
 * 用户相关服务
 * @author yangzc
 */
public interface LoginService extends BaseService {

	public static final String SERVICE_NAME = "com.knownbox.wb.teacher_login_service";
	
	/**
	 * 是否登录
	 * @return
	 */
	public boolean isLogin();

	/**
	 * 获得当前登录的用户
	 * @return
	 */
	public UserItem getLoginUser();
	
	/**
	 * 登录
	 * @param loginName
	 * @param passwd
	 * @return
	 */
	public void login(final String loginName, final String passwd,
			final LoginListener listener);

	/**
	 * 短信验证登录
	 * @param loginName
	 * @param smsCode
	 * @param listener
	 */
	public void smsLogin(final String loginName, final String smsCode,
						 final SmsLoginListener listener);

	/**
	 * 登出
	 * @return
	 */
	public void logout(final LogoutListener listener);
	
	/**
	 * 获得服务观察者
	 * @return
	 */
	public LoginServiceObserver getServiceObvserver();
	
	/**
	 * 获得用户信息构造器
	 * @return
	 */
	public RegistUserInfoBuilder getUserInfoBuilder();

	/**
	 * 执行倒计时
	 * @param time
	 * @param lastTime
	 * @param listener
	 */
	public void executeCountDown(int time, String lastTime, AdvanceTimer.TimeChangeListener listener);

	public void continueTimer(AdvanceTimer.TimeChangeListener listener);

	public AdvanceTimer getReSendSmsCodeTimer();

	public AdvanceTimer getSmsCodeValidTimer();

	public void releaseTimer();

	/**
	 *
	 * @param clazzName 发送的类
	 */
	public void updateJiaoCai(String clazzName);

	public void clearUserInfo();

	public void updateSchool(String[] keys, String[] values, boolean isUserDefined);

}
