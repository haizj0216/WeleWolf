/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.modules.utils;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.utils.BaseApp;
import com.buang.welewolf.modules.login.services.LoginService;

import java.util.UUID;

import cn.smssdk.SMSSDK;

public class Utils {

    public static UserItem getLoginUserItem() {
        LoginService service = (LoginService) BaseApp.getAppContext()
                .getSystemService(LoginService.SERVICE_NAME);
        if (service == null) {
            return null;
        }
        return service.getLoginUser();
    }

    public static String getToken() {
        UserItem user = getLoginUserItem();
        if (user == null) {
            return "";
        }
        return user.token;
    }

    public static UserItem getUserItem(OnlineRoleInfo roleInfo) {
        UserItem userItem = new UserItem();
        userItem.userName = roleInfo.userName;
        userItem.userId = roleInfo.userID;
        userItem.headPhoto = roleInfo.userPhoto;
        return userItem;
    }

    public static String getDeviceID(Activity activity) {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = MD5Util.encode(tm.getDeviceId());
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = getMyUUID(activity);
        }
        return deviceId;
    }

    private static String getMyUUID(Activity activity) {
        final TelephonyManager tm = (TelephonyManager) BaseApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    public static String getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
        }

        if (country == null) {
            country = SMSSDK.getCountry("42");
        }
        return country[1];
    }

    public static String getMCC() {
        TelephonyManager tm = (TelephonyManager) BaseApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }
        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }

}
