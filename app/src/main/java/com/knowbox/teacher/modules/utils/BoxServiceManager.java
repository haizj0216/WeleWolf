/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.modules.utils;

import com.hyena.framework.servcie.BaseServiceManager;
import com.hyena.framework.servcie.debug.DebugService;
import com.hyena.framework.servcie.debug.DebugServiceImpl;
import com.knowbox.base.service.share.ShareSDKService;
import com.knowbox.base.service.share.ShareService;
import com.knowbox.base.service.upload.QNUploadServiceImpl;
import com.knowbox.base.service.upload.UploadService;
import com.knowbox.base.service.upload.UploadTask;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.services.player.AudioPlayerService;
import com.knowbox.teacher.base.services.player.AudioPlayerServiceImp;
import com.knowbox.teacher.base.services.update.UpdateService;
import com.knowbox.teacher.base.services.update.UpdateServiceImpl;
import com.knowbox.teacher.base.services.updateclass.UpdateClassService;
import com.knowbox.teacher.base.services.updateclass.UpdateClassServiceImpl;
import com.knowbox.teacher.modules.homework.services.HomeworkService;
import com.knowbox.teacher.modules.homework.services.HomeworkServiceImp;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.login.services.LoginServiceImpl;
import com.knowbox.teacher.modules.message.services.EMChatService;
import com.knowbox.teacher.modules.message.services.EMChatServiceImpl;

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
