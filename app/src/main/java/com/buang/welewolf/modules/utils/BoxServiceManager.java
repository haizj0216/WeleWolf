/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.modules.utils;

import com.buang.welewolf.base.services.player.AudioPlayerService;
import com.buang.welewolf.base.services.player.AudioPlayerServiceImp;
import com.buang.welewolf.base.services.share.ShareSDKService;
import com.buang.welewolf.base.services.share.ShareService;
import com.buang.welewolf.base.services.update.UpdateService;
import com.buang.welewolf.base.services.update.UpdateServiceImpl;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.login.services.LoginServiceImpl;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.services.RongIMServiceImp;
import com.hyena.framework.servcie.BaseServiceManager;
import com.hyena.framework.servcie.debug.DebugService;
import com.hyena.framework.servcie.debug.DebugServiceImpl;

/**
 * 业务相关服务管理器
 * @author yangzc
 *
 */
public class BoxServiceManager extends BaseServiceManager {

	public BoxServiceManager() {
		super();
		//初始化所有服务
		initFrameServices();
		initServices();

	}
	
	@Override
	public Object getService(String name) {
		return super.getService(name);
	}
	
	@Override
	public void releaseAll() {
		super.releaseAll();
	}
	
	/**
	 * 初始化所有服务
	 */
	private void initServices(){
		registService(LoginService.SERVICE_NAME, new LoginServiceImpl());//登录服务
		registService(DebugService.SERVICE_NAME, new DebugServiceImpl());//Debug服务
		registService(UpdateService.SERVICE_NAME, new UpdateServiceImpl());
		registService(AudioPlayerService.SERVICE_NAME, new AudioPlayerServiceImp());
		registService(RongIMService.SERVICE_NAME, new RongIMServiceImp());
		registService(ShareService.SERVICE_NAME, new ShareSDKService());
	}
}
