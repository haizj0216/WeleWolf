package com.buang.welewolf.base.http.services;

import android.os.Build;
import android.text.TextUtils;

import com.buang.welewolf.base.utils.PreferencesController;
import com.hyena.framework.utils.VersionUtils;
import com.buang.welewolf.App;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.Utils;
import com.umeng.analytics.AnalyticsConfig;

/**
 * 网络接口提供其
 *
 * @author yangzc
 */
public class OnlineServices {

    public final static String SOURCE = "AndroidWerewolf";

    //是否开启debug模式
    private static boolean DEBUG = false;

    public static void enableDebug(boolean debug) {
        DEBUG = debug;
    }

    public static boolean getDebugMode() {
        return DEBUG;
    }

    /**
     * 获取新接口URL前缀
     * T
     *
     * @return
     * @author weilei
     */
    public static String getPhpUrlPrefix() {
        StringBuffer prefix = new StringBuffer();
//        if (DEBUG) {
//            int debugType = PreferencesController.getInt(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_API, 1);
//            if (debugType == 0) {
//                prefix.append("http://cow.test.knowbox.cn/");//单词部落联调环境
//            } else if (debugType == 1) {
//                prefix.append("http://beta.cow.api.knowbox.cn/"); //测试环境
//            } else if (debugType == 2) {
//                prefix.append("http://preview.cow.api.knowbox.cn/"); //预览环境
//            } else if (debugType == 3) {
//                prefix.append("http://cow.knowbox.cn/"); //正式环境
//            } else if (debugType == 4) {
//                String apiPrefix = PreferencesController.getStringValue(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_APIPREFIX);
//                if (TextUtils.isEmpty(apiPrefix)) {
//                    apiPrefix = "http://cow.knowbox.cn/";
//                }
//                prefix.append(apiPrefix);
//            }
//        } else {
//            prefix.append("http://cow.knowbox.cn/");
////            prefix.append("http://beta.cow.api.knowbox.cn/");
//        }
        prefix.append("http://47.93.44.115/");
        return prefix.toString();
    }

    private static StringBuffer getPhpCommonUrl(String commonUrl) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getPhpUrlPrefix());
        buffer.append(commonUrl);
        try {
            buffer.append("source=" + SOURCE);
            buffer.append("&version=" + VersionUtils.getVersionCode(App.getAppContext()));
            String channel = AnalyticsConfig.getChannel(App.getAppContext());
            if ("${UMENG_CHANNEL_VALUE}".equals(channel)) {
                channel = "buang";
            }
            buffer.append("&channel=" + channel);
            if (!TextUtils.isEmpty(Utils.getToken())) {
                buffer.append("&token=" + Utils.getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 获得登录URL
     *
     * @return
     */
    public static String getLoginUrl() {
        StringBuffer buffer = getPhpCommonUrl("wolf/user/player/login?");
        return buffer.toString();
    }

    /**
     * 获得短信验证登录URL
     *
     * @return
     */
    public static String getSmsLoginUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/code-login?");
        return buffer.toString();
    }

    /**
     * 获得登出Url
     *
     * @return
     */
    public static String getLogOutUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/login-out?");
        return buffer.toString();
    }

    /**
     * 检查版本url
     *
     * @return
     */
    public static String getCheckVersionUrl() {
        StringBuffer buffer = getPhpCommonUrl("common/app/check-version?");
        return buffer.toString();
    }


    /**
     * 获得修改个人信息URL
     *
     * @return
     */
    public static String getUpdateUserInfo() {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/update-info?");
        return buffer.toString();
    }


    public static String getTeacherLogUrl() {
        StringBuffer buffer = getPhpCommonUrl("common/app/log-triger?");
        try {
            String token = Utils.getToken();
            if (!TextUtils.isEmpty(token)) {
                buffer.append("&token=" + token);
            }
            buffer.append("&mobileBrand=" + android.os.Build.BRAND);
            buffer.append("&mobileVersion=" + Build.VERSION.RELEASE);
            buffer.append("&mobileModel=" + Build.MODEL);
            buffer.append("&mobileSDK=" + Build.VERSION.SDK_INT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    public static String getQiniuUploadTokenUrl(int type) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("http://cow.knowbox.cn/");
        buffer.append("common/app/get-bucket-token?");
        buffer.append("source=androidTeacher");
        buffer.append("&version=" + VersionUtils.getVersionCode(App.getAppContext()));
        String channel = AnalyticsConfig.getChannel(App.getAppContext());
        if ("${UMENG_CHANNEL_VALUE}".equals(channel)) {
            channel = "buang";
        }
        buffer.append("&channel=" + channel);
        if (!TextUtils.isEmpty(Utils.getToken())) {
            buffer.append("&token=" + Utils.getToken());
        }
        buffer.append("&resource_type=" + type);
        return buffer.toString();
    }

    /**
     * 检查手机号
     *
     * @param phoneNum
     * @param type
     * @return
     */
    public static String getCheckPhoneUrl(String phoneNum, int type) {
        StringBuffer buffer = getPhpCommonUrl("wolf/user/player/check?");
        buffer.append("&phoneNum=" + phoneNum);
        buffer.append("&checkType=" + type);
        return buffer.toString();
    }

    /**
     * 注册接口
     *
     * @return
     */
    public static String getRegisterUrl() {
        StringBuffer buffer = getPhpCommonUrl("wolf/user/player/register?");
        return buffer.toString();
    }

    /**
     * 忘记密码
     *
     * @return
     */
    public static String getForgetPswUrl() {
        StringBuffer buffer = getPhpCommonUrl("wolf/user/player/resetPassword?");
        return buffer.toString();
    }

    /**
     * 初始化个人资料
     *
     * @return
     */
    public static String getFormatUserInfoUrl() {
        StringBuffer buffer = getPhpCommonUrl("wolf/user/player/formatUserInfo?");
        return buffer.toString();
    }

}
