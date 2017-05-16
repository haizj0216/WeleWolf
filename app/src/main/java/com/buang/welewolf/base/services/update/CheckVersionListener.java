/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.services.update;

import com.buang.welewolf.base.bean.OnlineVersion;


/**
 * 检查版本监听器
 * @author yangzc
 */
public interface CheckVersionListener {

	public static final int REASON_NORMAL = 1;//正常完成，没有版本信息变化
	public static final int REASON_ERROR = 2;//检查信息失败
	
	/**
	 * 服务端版本变化
	 * @param auto
	 * @param version
	 */
	public void onVersionChange(boolean auto, OnlineVersion version);
	
	/**
	 * @param auto
	 * 版本检查完成，没有版本信息变化
	 */
	public void onCheckFinish(boolean auto, int reason);
}
