/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.services.update;

import com.buang.welewolf.base.bean.OnlineVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * 升级服务观察者
 * 
 * @author yangzc
 *
 */
public class UpdateServiceObserver {

	private List<CheckVersionListener> mVersionChangeListeners = new ArrayList<CheckVersionListener>();

	/**
	 * 添加版本变化监听器
	 * @param listener
	 */
	public void addVersionChangeListener(CheckVersionListener listener){
		if(!mVersionChangeListeners.contains(listener)){
			mVersionChangeListeners.add(listener);
		}
	}
	
	/**
	 * 移除版本变化监听器
	 * @param listener
	 */
	public void removeVersionChangeListener(CheckVersionListener listener){
		mVersionChangeListeners.remove(listener);
	}
	
	/**
	 * 通知版本变化
	 * @param version
	 */
	public void notifyVersionChange(boolean auto, OnlineVersion version){
		for(CheckVersionListener listener: mVersionChangeListeners){
			listener.onVersionChange(auto, version);
		}
	}
	
	/**
	 * 版本信息检查完成
	 * @param reason
	 */
	public void notifyCheckFinish(boolean auto, int reason){
		for(CheckVersionListener listener: mVersionChangeListeners){
			listener.onCheckFinish(auto, reason);
		}
	}
}
