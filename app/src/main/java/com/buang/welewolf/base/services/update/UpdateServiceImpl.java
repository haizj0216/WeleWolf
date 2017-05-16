/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.services.update;

import android.text.TextUtils;

import com.buang.welewolf.base.bean.OnlineVersion;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.PreferencesController;
import com.hyena.framework.datacache.DataAcquirer;

/**
 * 升级服务实现类
 * 
 * @author yangzc
 * 
 */
public class UpdateServiceImpl implements UpdateService {

	private UpdateServiceObserver mUpdateServiceObserver = new UpdateServiceObserver();
	// 最新版本
	private OnlineVersion mLastVersion;

	public UpdateServiceImpl() {
		super();
	}
	
	@Override
	public void init() {
		String versionInfo = PreferencesController
				.getStringValue(PreferencesController.PREF_VERSION_INFO);
		if (!TextUtils.isEmpty(versionInfo)) {
			mLastVersion = new OnlineVersion();
			mLastVersion.parse(versionInfo);
		}
	}

	@Override
	public OnlineVersion getLastVersion() {
		return mLastVersion;
	}

	@Override
	public void releaseAll() {

	}

	@Override
	public void checkVersion(final boolean auto) {
		new Thread() {
			public void run() {
				checkVersionImpl(auto);
			};
		}.start();
	}

	@Override
	public UpdateServiceObserver getObserver() {
		return mUpdateServiceObserver;
	}

	private void checkVersionImpl(boolean auto) {
//		LoginService loginService = (LoginService) BaseApp.getAppContext()
//				.getSystemService(LoginService.SERVICE_NAME);
//		String token = "";
//		if (loginService != null && loginService.getLoginUser() != null) {
//			token = loginService.getLoginUser().token;
//		}
		String url = OnlineServices.getCheckVersionUrl();
		OnlineVersion version = new DataAcquirer<OnlineVersion>().get(url, new OnlineVersion());
//		int currentVersion = VersionUtils
//				.getVersionCode(BaseApp.getAppContext());
		if (version.isAvailable()) {
//			if (version.version != null) {
				if (version.type == OnlineVersion.FLAG_UPDATE_LASTEST) {
					mLastVersion = null;
					getObserver().notifyCheckFinish(auto,
							CheckVersionListener.REASON_NORMAL);
				} else {
					this.mLastVersion = version;
					getObserver().notifyVersionChange(auto, version);
				}
//				int remoteCode = -1;
//				try {
//					remoteCode = Integer.valueOf(version.version.replace(".",
//							""));
//				} catch (Exception e) {
//				}
//				if (remoteCode < 0) {
//					mLastVersion = null;
//					PreferencesController.setStringValue(
//							PreferencesController.PREF_VERSION_INFO, "");
//					getObserver().notifyCheckFinish(auto,
//							CheckVersionListener.REASON_NORMAL);
//					return;
//				}
//
//				if (currentVersion < remoteCode) {
//					PreferencesController.setStringValue(
//							PreferencesController.PREF_VERSION_INFO,
//							version.toString());
//					this.mLastVersion = version;
//					getObserver().notifyVersionChange(auto, version);
//				} else {
//					PreferencesController.setStringValue(
//							PreferencesController.PREF_VERSION_INFO, "");
//					mLastVersion = null;
//					getObserver().notifyCheckFinish(auto,
//							CheckVersionListener.REASON_NORMAL);
//				}
//			} else {
//				// 服务器出问题了，认为升级服务器无法处理升级
//				PreferencesController.setStringValue(
//						PreferencesController.PREF_VERSION_INFO, "");
//				mLastVersion = null;
//				getObserver().notifyCheckFinish(auto,
//						CheckVersionListener.REASON_ERROR);
//			}
		} else {
			getObserver().notifyCheckFinish(auto,
					CheckVersionListener.REASON_ERROR);
		}
	}
}
