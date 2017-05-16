/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.services.update;

import com.hyena.framework.servcie.BaseService;
import com.buang.welewolf.base.bean.OnlineVersion;

/**
 * 升级服务
 * @author yangzc
 */
public interface UpdateService extends BaseService {

	public static final String SERVICE_NAME = "com.knowbox.wb_update";
	
	public void init();
	
	/**
	 * 检查版本
	 * @param auto 是否是自动检查
	 */
	public void checkVersion(boolean auto);
	
	/**
	 * 获得最新版本
	 * @return
	 */
	public OnlineVersion getLastVersion();
	
	/**
	 * 获得升级服务观察者
	 * @return
	 */
	public UpdateServiceObserver getObserver();
}
