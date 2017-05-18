package com.buang.welewolf.modules.message.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.buang.welewolf.App;
import com.buang.welewolf.base.bean.OnlineCompetitionListInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.updateclass.UpdateClassService;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.base.utils.ChannelUtils;
import com.buang.welewolf.modules.classes.AddGradeClassFragment;
import com.buang.welewolf.modules.homework.assign.AssignSelectPublisherFragment;
import com.buang.welewolf.modules.homework.competition.LoopDetailFragment;
import com.buang.welewolf.modules.homework.competition.SingleTestDetailFragment;
import com.buang.welewolf.modules.main.MainFragment;
import com.buang.welewolf.modules.profile.ActivityWebViewFragment;
import com.buang.welewolf.modules.profile.SettingsFragment;
import com.buang.welewolf.modules.profile.UserAuthNotyetFragment;
import com.buang.welewolf.modules.profile.UserInfoEditFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
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
import java.util.List;

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
        } else if ("Authentication".equals(methodName)){
            UserAuthNotyetFragment fragment = (UserAuthNotyetFragment) Fragment.
                    instantiate(mFragment.getActivity(), UserAuthNotyetFragment.class.getName());
            mFragment.showFragment(fragment);
        } else if("openNewWindow".equals(methodName)) {
            String title = paramsMap.get("title");
            String url = paramsMap.get("url");
            openNewWindow(url, title);
        } else if("createClass".equals(methodName)) {
            Bundle mBundle = new Bundle();
            mBundle.putInt("type", AddGradeClassFragment.TYPE_ADD_CLASS);
            AddGradeClassFragment fragment = (AddGradeClassFragment) Fragment
                    .instantiate(mFragment.getActivity(),
                            AddGradeClassFragment.class.getName(), mBundle);
            mFragment.showFragment(fragment);
        } else if("AssignHomework".equals(methodName)) {
            mFragment.removeAllFragment();
        } else if ("profileDetail".equals(methodName)) {
            UserInfoEditFragment fragment = UserInfoEditFragment.newFragment(mFragment.getActivity(),
                    UserInfoEditFragment.class, null);
            mFragment.showFragment(fragment);
        } else if ("openView".equals(methodName)) {
            String view = paramsMap.get("view");
            if ("root_index".equals(view)) {
                String classId = paramsMap.get("class_id");
                ActionUtils.notifyMainTab(MainFragment.TYPE_TAB_TIPS_CLASS, paramsMap);
            } else if ("root_gym".equals(view)) {
                ActionUtils.notifyMainTab(MainFragment.TYPE_TAB_TIPS_BANK, paramsMap);
            } else if ("root_me".equals(view)) {
                ActionUtils.notifyMainTab(MainFragment.TYPE_TAB_TIPS_PROFILE, paramsMap);
            } else if ("personal_setting_info".equals(methodName)) {
                UserInfoEditFragment fragment = UserInfoEditFragment.newFragment(mFragment.getActivity(), UserInfoEditFragment.class, null);
                mFragment.showFragment(fragment);
            } else if ("personal_setting_system".equals(methodName)) {
                SettingsFragment fragment = SettingsFragment.newFragment(mFragment.getActivity(), SettingsFragment.class, null);
                mFragment.showFragment(fragment);
            }
        } else if ("classDetail".equals(methodName)) {
            UpdateClassService updateClassService = (UpdateClassService) mFragment.
                    getSystemService(UpdateClassService.SERVICE_NAME);
            List<ClassInfoItem> lists = updateClassService.getAllClassInfoItem();
            if (lists.size() == 0) {
                openClassListFragment();
                return;
            }
            String classId = paramsMap.get("classID");
            if ("-1".equals(classId)) {
                openClassStudentListFragment(null);
            } else {
                ClassInfoItem classinfo = null;
                for (int i = 0; i < lists.size(); i++) {
                    ClassInfoItem item = lists.get(i);
                    if (item.classId.equals(classId)) {
                        classinfo = item;
                        break;
                    }
                }
                if (classinfo != null) {
                    openClassStudentListFragment(paramsMap);
                } else {
                    openClassListFragment();
                }
            }
        } else if ("chooseTeaching".equals(methodName)) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsUtils.CLAZZ_NAME, mFragment.getClass().getName());
            AssignSelectPublisherFragment fragment = AssignSelectPublisherFragment.newFragment(mFragment.getActivity(),
                    AssignSelectPublisherFragment.class, bundle);
            mFragment.showFragment(fragment);
        } else if ("gymDetail".equals(methodName)) {
            String matchId = paramsMap.get("matchID");
            String type = paramsMap.get("type");
            OnlineCompetitionListInfo.CompetitionItem item = new OnlineCompetitionListInfo.CompetitionItem();
            item.matchID = matchId;
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantsUtils.KEY_BUNDLE_CHAM_ITEM, item);
            if (OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH.equals(type)) {
                LoopDetailFragment fragment = LoopDetailFragment
                        .newFragment(mFragment.getActivity(), LoopDetailFragment.class, bundle);
                mFragment.showFragment(fragment);
            }else if (OnlineCompetitionListInfo.CompetitionItem.TYPE_SINGLE_TEST.equals(type)) {
                //单次测验
                mFragment.showFragment(SingleTestDetailFragment.newFragment(mFragment.getActivity(), SingleTestDetailFragment.class, bundle));
            }
        } else if ("showRedBadge".equals(methodName)) {
            String type = paramsMap.get("type");
            ActionUtils.notifyShowRedBadge(type);
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
