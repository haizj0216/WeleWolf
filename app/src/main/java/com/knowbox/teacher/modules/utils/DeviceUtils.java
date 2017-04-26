package com.knowbox.teacher.modules.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.hyena.framework.utils.BaseApp;

import java.lang.reflect.Method;

/**
 * Created by LiuYu on 2016/12/12.
 */
public class DeviceUtils {

    public static String getSafeDeviceID() {
        String deviceId = getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = getMacAddress();
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = getSerialNo();
            }
        }
        return deviceId;
    }

    public static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) BaseApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            String deviceId = tm.getDeviceId();
            if (!TextUtils.isEmpty(deviceId)) {
                return deviceId;
            }
        }
        return null;
    }


    public static String getMacAddress() {
        WifiManager wm = (WifiManager) BaseApp.getAppContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            String macAddress = wm.getConnectionInfo().getMacAddress();
            if (!TextUtils.isEmpty(macAddress)) {
                return macAddress;
            }
        }
        return null;
    }

    public static String getSerialNo() {
        String serialNo = null;
        if (Build.VERSION.SDK_INT >= 9) {
            try {
                Class t = Class.forName("android.os.SystemProperties");
                Method get = t.getMethod("get", new Class[]{String.class, String.class});
                serialNo = (String) get.invoke(t, new Object[]{"ro.serialno", "unknown"});
            } catch (Throwable e) {
                serialNo = null;
            }
        }

        return serialNo;
    }
}
