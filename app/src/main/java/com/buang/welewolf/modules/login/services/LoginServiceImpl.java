/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.modules.login.services;

import android.text.TextUtils;

import com.buang.welewolf.base.bean.OnlineLoginInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.database.tables.UserTable;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DeviceUtils;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.widgets.AdvanceTimer;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.datacache.db.DataCacheTable;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.utils.BaseApp;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录实现类
 *
 * @author yangzc
 */
public class LoginServiceImpl implements LoginService {

    // 当前登录的用户信息
    private UserItem mLoginUser;
    // 服务观察者
    private LoginServiceObserver mLoginServiceObserver = new LoginServiceObserver();

    //登录验证码倒计时
    private AdvanceTimer mReSendSmsCodeTimer;
    private AdvanceTimer mSmsCodeValidTimer;

    @Override
    public void executeCountDown(int time, String lastTime, AdvanceTimer.TimeChangeListener listener) {
        // 启动60s重新获取倒计时
        mReSendSmsCodeTimer = new AdvanceTimer();
        mReSendSmsCodeTimer.setCurSeconds(time);
        mReSendSmsCodeTimer.setTimeChangeListener(listener);
        mReSendSmsCodeTimer.start();

        // 启动300s验证码有效时间倒计时
        mSmsCodeValidTimer = new AdvanceTimer();
        mSmsCodeValidTimer.setCurSeconds(Integer
                .parseInt(lastTime));
        mSmsCodeValidTimer.start();
    }

    @Override
    public void continueTimer(AdvanceTimer.TimeChangeListener listener) {
        if (null != mReSendSmsCodeTimer) {
            mReSendSmsCodeTimer.setTimeChangeListener(listener);
        }
    }

    @Override
    public AdvanceTimer getReSendSmsCodeTimer() {
        return mReSendSmsCodeTimer;
    }

    @Override
    public AdvanceTimer getSmsCodeValidTimer() {
        return mSmsCodeValidTimer;
    }

    @Override
    public void releaseTimer() {
        if (null != mReSendSmsCodeTimer) {
            mReSendSmsCodeTimer.destory();
            mReSendSmsCodeTimer = null;
        }
        if (null != mSmsCodeValidTimer) {
            mSmsCodeValidTimer.destory();
            mSmsCodeValidTimer = null;
        }
    }

    @Override
    public void clearUserInfo() {
        mLoginUser = null;
    }

    @Override
    public void setUserInfo(UserItem userInfo) {
        mLoginUser = userInfo;
    }

    @Override
    public void releaseAll() {
        mLoginUser = null;
    }

    @Override
    public boolean isLogin() {
        getLoginUser();
        return mLoginUser != null;
    }

    @Override
    public UserItem getLoginUser() {
        if (mLoginUser != null) {
            return mLoginUser;
        }
        UserTable table = DataBaseManager.getDataBaseManager().getTable(
                UserTable.class);
        if (table != null) {
            List<UserItem> users = table.queryAll();
            if (users != null && users.size() > 0) {
                UserItem user = users.get(0);
                this.mLoginUser = user;
                return user;
            }
        }
        return null;
    }

    @Override
    public void login(final String phone, final String pwd,
                      final LoginListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> umengCount = new HashMap<String, String>();
                long reqTime = System.currentTimeMillis();
                if (listener != null) {
                    listener.onLoginStart();
                }

                // 登录要求实时性高，不能使用缓存
                String loginUrl = OnlineServices.getLoginUrl();
                JSONObject object = new JSONObject();
                try {
                    object.put("phoneNum", phone);
                    object.put("password", pwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String data = object.toString();
                OnlineLoginInfo loginInfo = new DataAcquirer<OnlineLoginInfo>()
                        .post(loginUrl, data, new OnlineLoginInfo());
                if (loginInfo.isAvailable()) {
                    releaseTimer();
                    umengCount.put(UmengConstant.RESULT, UmengConstant.SUCCESS);
                    try {
                        loginInfo.mUserItem.loginName = phone;
                    } catch (Exception e) {
                    }
                    mLoginUser = loginInfo.mUserItem;
                    if (listener != null) {
                        listener.onLoginSuccess(loginInfo);
                    }
                    getServiceObvserver().notifyOnLogin(mLoginUser);
                } else {
                    String message = "网络连接异常，请稍后再试";
                    if (!TextUtils.isEmpty(loginInfo.getRawResult())) {
                        if ("20201".equals(loginInfo.getRawResult())) {
                            message = "用户名或密码错误";
                        } else {
                            message = ErrorManager.getErrorManager().getErrorHint(loginInfo.getRawResult(), loginInfo.getErrorDescription());
                        }

                    }
                    if (listener != null) {
                        listener.onLoginFailed(message);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("(statusCode = ").append(
                            loginInfo.getStatusCode());
                    if (!TextUtils.isEmpty(loginInfo.getRawResult())) {
                        sb.append(" , rawCode = ")
                                .append(loginInfo.getRawResult()).append(")");
                    } else {
                        sb.append(")");
                    }
                    umengCount.put(UmengConstant.RESULT, UmengConstant.FAIL
                            + sb.toString());
                }
                reqTime = System.currentTimeMillis() - reqTime;
                umengCount.put(UmengConstant.REQ_TIME, "" + (reqTime / 1000.0));
                MobclickAgent.onEvent(BaseApp.getAppContext(),
                        UmengConstant.LOGIN, umengCount);
            }
        }).start();

    }

    @Override
    public void logout(final LogoutListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onLogoutStart();
                }
                try {
                    //清缓存
                    DataCacheTable cacheTable = DataBaseManager
                            .getDataBaseManager().getTable(DataCacheTable.class);
                    cacheTable.deleteByCase(null, null);
                } catch (Exception e) {
                }

                UserTable table = DataBaseManager.getDataBaseManager()
                        .getTable(UserTable.class);
                table.deleteByCase(null, null);
                if (listener != null) {
                    listener.onLogoutSuccess();
                }
                String token = "";
                if (mLoginUser != null) {
                    token = mLoginUser.token;
                    getServiceObvserver().notifyOnLogout(mLoginUser);
                    mLoginUser = null;
                }

                if (!TextUtils.isEmpty(token)) {
                    String logoutUrl = OnlineServices
                            .getLogOutUrl();
                    new DataAcquirer<BaseObject>().acquire(logoutUrl,
                            new BaseObject(), -1);
                }

            }
        }).start();

    }

    @Override
    public LoginServiceObserver getServiceObvserver() {
        return mLoginServiceObserver;
    }

}
