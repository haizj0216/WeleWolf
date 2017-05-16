/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.modules.utils;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.buang.welewolf.base.database.bean.UserItem;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.utils.BaseApp;
import com.buang.welewolf.modules.login.services.LoginService;

import java.util.UUID;

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

}
