package com.buang.welewolf.modules.message.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.buang.welewolf.App;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.base.utils.ChannelUtils;
import com.buang.welewolf.modules.main.MainFragment;
import com.buang.welewolf.modules.profile.ActivityWebViewFragment;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UiThreadHandler;
import com.hyena.framework.utils.VersionUtils;

import org.apache.http.protocol.HTTP;

import java.net.URLDecoder;
import java.util.Hashtable;

/**
 * Created by weilei on 16/2/26.
 */
public class MessagePushUtils {

    private BaseUIFragment mFragment;

    public MessagePushUtils(BaseUIFragment fragment) {
        mFragment = fragment;
    }

    private void handleUrlLoading(String url) {
        try {
            LogUtil.d("BaseWebViewFragment", "handleUrlLoading:" + url);
            String body = url.replace("apns://", "");
            if (body.indexOf("?") != -1) {
                final String method = body.substring(0, body.indexOf("?"));
                String query = body.replace(method + "?", "");
                String paramsArray[] = query.split("&");
                final Hashtable<String, String> valueMap = new Hashtable<String, String>();
                for (int i = 0; i < paramsArray.length; i++) {
                    String params[] = paramsArray[i].split("=");
                    String key = URLDecoder.decode(params[0], HTTP.UTF_8);
                    if (params.length > 1) {
                        String value = URLDecoder.decode(params[1], HTTP.UTF_8);
                        valueMap.put(key, value);
                    } else {
                        valueMap.put(key, "");
                    }
                }
                UiThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onCallMethod(method, valueMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 200);

            } else {
                final String methodName = url.replace("apns://", "");
                UiThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onCallMethod(methodName, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Web页面请求方法调用
     *
     * @param methodName
     * @param paramsMap
     */
    public void onCallMethod(String methodName, Hashtable<String, String> paramsMap) throws Exception {
        LogUtil.v("ActivityWeb", "onCallMethod : " + methodName);
        UmengConstant.reportUmengEvent(UmengConstant.EVENT_OPENAPP_FROM_OUTSIDE, null);
        if ("openBrowser".equals(methodName)) {
            String url = paramsMap.get("url");
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mFragment.getActivity().startActivity(intent);
            }
        } else if("openNewWindow".equals(methodName)) {
            String title = paramsMap.get("title");
            String url = paramsMap.get("url");
            openNewWindow(url, title);
        } else if ("openView".equals(methodName)) {
            String view = paramsMap.get("view");
            if ("root_index".equals(view)) {
                String classId = paramsMap.get("class_id");
                ActionUtils.notifyMainTab(MainFragment.TYPE_TAB_TIPS_CLASS, paramsMap);
            } else if ("root_gym".equals(view)) {
                ActionUtils.notifyMainTab(MainFragment.TYPE_TAB_TIPS_BANK, paramsMap);
            } else if ("root_me".equals(view)) {
                ActionUtils.notifyMainTab(MainFragment.TYPE_TAB_TIPS_PROFILE, paramsMap);
            }
        }
    }

    private void openClassStudentListFragment(Hashtable<String, String> paramsMap) {
        ActionUtils.notifyMainTab(MainFragment.TYPE_TAB_TIPS_CLASS, paramsMap);
    }

    private void openClassListFragment() {
        ActionUtils.notifyMainTab(MainFragment.TYPE_TAB_TIPS_CLASS, null);
    }

    private void openNewWindow(String url, String title) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(url);
        if (!url.contains("token=")) {
            if (url.indexOf("?") != -1) {
                buffer.append("&source=").append(OnlineServices.SOURCE);
            } else {
                buffer.append("?source=").append(OnlineServices.SOURCE);
            }
            buffer.append("&version=").append(VersionUtils.getVersionCode(App.getAppContext()));
            buffer.append("&token=").append(com.buang.welewolf.modules.utils.Utils.getToken());
            buffer.append("&channel=").append(ChannelUtils.getChannel(BaseApp.getAppContext()));
        }
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(title)) {
            title = "活动";
        }
        bundle.putString("title", title);
        bundle.putString("url", buffer.toString());
        BaseSubFragment fragment = (BaseSubFragment) Fragment.instantiate(mFragment.getActivity(),
                ActivityWebViewFragment.class.getName(), bundle);
        mFragment.showFragment(fragment);
    }
}
