/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.modules.utils;

import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.update.UpdateService;
import com.buang.welewolf.base.services.update.UpdateServiceImpl;
import com.buang.welewolf.base.services.updateclass.UpdateClassServiceImpl;
import com.buang.welewolf.modules.homework.services.HomeworkService;
import com.buang.welewolf.modules.homework.services.HomeworkServiceImp;
import com.buang.welewolf.modules.message.services.EMChatService;
import com.buang.welewolf.modules.message.services.EMChatServiceImpl;
import com.hyena.framework.servcie.BaseServiceManager;
import com.hyena.framework.servcie.debug.DebugService;
import com.hyena.framework.servcie.debug.DebugServiceImpl;
import com.knowbox.base.service.share.ShareSDKService;
import com.knowbox.base.service.share.ShareService;
import com.knowbox.base.service.upload.QNUploadServiceImpl;
import com.knowbox.base.service.upload.UploadService;
import com.knowbox.base.service.upload.UploadTask;
import com.buang.welewolf.base.services.player.AudioPlayerService;
import com.buang.welewolf.base.services.player.AudioPlayerServiceImp;
import com.buang.welewolf.base.services.updateclass.UpdateClassService;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.login.services.LoginServiceImpl;

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
		registService(EMChatService.SERVICE_NAME, new EMChatServiceImpl());//消息服务
		registService(DebugService.SERVICE_NAME, new DebugServiceImpl());//Debug服务
		registService(ShareService.SERVICE_NAME, new ShareSDKService());
		registService(HomeworkService.SERVICE_NAME, new HomeworkServiceImp());
		registService(UploadService.SERVICE_NAME_QINIU, new QNUploadServiceImpl(){
			@Override
			public String getPicTokenUrl() {
				return OnlineServices.getVoiceUploadTokenUrl(UploadTask.TYPE_PICTURE);
			}

			@Override
			public String getRecordTokenUrl() {
				return OnlineServices.getVoiceUploadTokenUrl(UploadTask.TYPE_RECORDER);
			}
		});
		registService(UpdateService.SERVICE_NAME, new UpdateServiceImpl());
		registService(UpdateClassService.SERVICE_NAME, new UpdateClassServiceImpl());
		registService(AudioPlayerService.SERVICE_NAME, new AudioPlayerServiceImp());
	}
}
