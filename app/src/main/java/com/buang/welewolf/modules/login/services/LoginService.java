/**
 * Copyright (C) 2014 The knowbox_Teacher Project
 */
package com.buang.welewolf.modules.login.services;

import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.widgets.AdvanceTimer;
import com.hyena.framework.servcie.BaseService;

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

	public void clearUserInfo();

	public void setUserInfo(UserItem userInfo);

}
