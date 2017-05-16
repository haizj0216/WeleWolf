/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;

import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.CrashHelper;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.modules.message.services.EMChatService;
import com.buang.welewolf.modules.utils.BoxErrorMap;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.clientlog.Logger;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.servcie.ServiceProvider;
import com.hyena.framework.utils.BaseApp;
import com.buang.welewolf.base.database.KnowboxDataBase;
import com.buang.welewolf.base.utils.BoxNetworkSensor;
import com.buang.welewolf.modules.utils.BoxServiceManager;

import java.util.Iterator;
import java.util.List;

/**
 * 应用上下文
 * @author yangzc
 */
public class App extends BaseApp {

	//是否可以缓存
	public static final boolean CACHEABLE = false;
	//不允许多端登录
	public static boolean ALLOW_MULTI_NODE = false;
	
	public static boolean mIsEmChatConntcted = true;
	private EMChatService mEMChatService;
//	public LocationService locationService;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("App", "onCreate");
		/************************************
		 * 初始化应用 请勿随意修改
		 ************************************/


//		locationService = new LocationService(getApplicationContext());

		//初始化崩溃统计
		CrashHelper.init();
		//在线服务
//		OnlineServices.enableDebug(false);
		OnlineServices.enableDebug(BuildConfig.LOG_DEBUG);
		//开启日志功能
//		LogUtil.setDebug(true);
		LogUtil.setDebug(BuildConfig.LOG_DEBUG);
		LogUtil.setLevel(Logger.DO_NOT_WRITE_LOG);
		//初始化底层服务配置
		FrameworkConfig.init(this).setAppRootDir(DirContext.getRootDir())
				.setGetEncodeKey("1qaz2wsx")
				.setPostEncodeKey("123qwe!@#").setAppType(FrameworkConfig.APP_BOX).setDebug(BuildConfig.LOG_DEBUG);
		//初始化数据库
		DataBaseManager.getDataBaseManager().registDataBase(new KnowboxDataBase());
		//注册网络服务
		NetworkProvider.getNetworkProvider().registNetworkSensor(new BoxNetworkSensor(this));
		//注册应用系统服务
		ServiceProvider.getServiceProvider().registServiceManager(new BoxServiceManager());
		//错误码
		ErrorManager.getErrorManager().registErrorMap(new BoxErrorMap());

		int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase("com.buang.welewolf")) {
            return;
        }
	}

	public void onAppStarted(){
	}
	
	private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator<RunningAppProcessInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }
	
	/**
	 * 退出应用
	 */
	public void exit(){
		try {
			//释放所有服务
			ServiceProvider.getServiceProvider().getServiceManager().releaseAll();
		} catch (Exception e) {
		}
	}
	
}
