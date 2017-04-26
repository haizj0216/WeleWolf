/*
 * Copyright (c) 2013 Baidu Inc.
 */
package com.knowbox.teacher.base.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.network.HttpExecutor;
import com.hyena.framework.network.NetworkSensor;
import com.hyena.framework.servcie.debug.DebugService;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认网络感应器
 * @author yangzc
 */
public class BoxNetworkSensor implements NetworkSensor {

	private static final String TAG = "DefaultNetworkSensor";
	private boolean mIsNetAvailable = false;

	public BoxNetworkSensor(Context context){
		mIsNetAvailable = isNetworkAvailable(BaseApp.getAppContext());
		registerNetworkStateReceiver();
	}

	@Override
	public boolean isNetworkAvailable() {
		return isNetworkAvailable(BaseApp.getAppContext());
	}

	@Override
	public HttpExecutor.ProxyHost getProxyHost(String s, boolean b) {
		HttpExecutor.ProxyHost proxy = getProxy(s, b);
		return proxy;
	}

	@Override
	public List<KeyValuePair> getCommonHeaders(String s, boolean b) {
		List<KeyValuePair> headers = new ArrayList<KeyValuePair>();
		return headers;
	}

	@Override
	public void updateFlowRate(long len) {
	}

	@Override
	public ConnectivityManager getConnectivityManager(Context context){
		if(mConnectivityManager == null){
			mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return mConnectivityManager;
	}

	@Override
	public String rebuildUrl(String url) {
		url = url.replace(" ", "%20").replace("\"", "%22")
				.replace("#", "%23").replace("(", "%28")
				.replace(")", "%29").replace("+", "%2B").replace(",", "%2C")
				.replace(";", "%3B").replace("<", "%3C")
				.replace(">", "%3E").replace("@", "%40")
				.replace("\\", "%5C").replace("|", "%7C");
		try {
			DebugService service = (DebugService) BaseApp.getAppContext()
					.getSystemService(DebugService.SERVICE_NAME);
			service.showDebugMsg(url);
		} catch (Exception e) {
		}
		return url;
	}

	private ConnectivityManager mConnectivityManager;
	private boolean isNetworkAvailable(Context context) {
		if (context == null)
			return false;

		try{
			if (getConnectivityManager(context) == null) {
				LogUtil.d(TAG, "+++couldn't get connectivity manager");
			} else {
				NetworkInfo[] info = getConnectivityManager(context).getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							LogUtil.d(TAG, "+++network is available");
							return true;
						}
					}
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
		}
		LogUtil.d(TAG, "+++network is not available");
		return false;
	}

	private HttpExecutor.ProxyHost getProxy(String url, boolean isProxy) {
		return null;
	}

	private void registerNetworkStateReceiver() {
		if (mNetworkStateReceiver != null) {
			IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
			MsgCenter.registerGlobalReceiver(mNetworkStateReceiver, intentFilter);
		}
	}

	private void unRegisterNetworkStateReceiver() {
		if (mNetworkStateReceiver != null) {
			MsgCenter.unRegisterGlobalReceiver(mNetworkStateReceiver);
		}
	}

	public void release(){
		unRegisterNetworkStateReceiver();
	}

	private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				mIsNetAvailable = isNetworkAvailable(BaseApp.getAppContext());
			}
		}
	};
}
