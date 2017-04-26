package com.knowbox.teacher.modules.message.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UiThreadHandler;
import com.hyena.framework.utils.VersionUtils;
import com.hyphenate.chat.EMMessage;
import com.knowbox.teacher.App;
import com.knowbox.teacher.base.bean.OnlineCompetitionListInfo;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.services.updateclass.UpdateClassService;
import com.knowbox.teacher.base.utils.ActionUtils;
import com.knowbox.teacher.base.utils.ChannelUtils;
import com.knowbox.teacher.modules.classes.AddGradeClassFragment;
import com.knowbox.teacher.modules.homework.assign.AssignSelectPublisherFragment;
import com.knowbox.teacher.modules.homework.competition.LoopDetailFragment;
import com.knowbox.teacher.modules.homework.competition.SingleTestDetailFragment;
import com.knowbox.teacher.modules.main.MainFragment;
import com.knowbox.teacher.modules.profile.ActivityWebViewFragment;
import com.knowbox.teacher.modules.profile.SettingsFragment;
import com.knowbox.teacher.modules.profile.UserAuthNotyetFragment;
import com.knowbox.teacher.modules.profile.UserInfoEditFragment;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.UmengConstant;

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

    public void handleMessage(EMMessage message) {
        if (message.getType() == EMMessage.Type.TXT) {
            if (!TextUtils.isEmpty(message.getStringAttribute("apns", ""))) {
                handleUrlLoading(message.getStringAttribute("apns", ""));
                message.setUnread(false);
                return;
            }
            String customType = message.getStringAttribute("type", "");//自定义类型
            if (TextUtils.isEmpty(customType)) {
                customType = String.valueOf(message.getIntAttribute("type", -1));
            }
            if (!TextUtils.isEmpty(customType)) {
                try {
                    int type = Integer.parseInt(customType);
                    switch (type) {
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//                openEmChatFragment(message);
            }
        } else {
//            openEmChatFragment(message);
        }
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

    /**
     * 打开聊天内容
     * @param message
     */
    private void openEmChatFragment(EMMessage message) {
        final ChatListItem item = new ChatListItem();
        item.mUserType = (message.getChatType() == EMMessage.ChatType.Chat) ?
                ChatListItem.USER_TYPE_STUDENT : ChatListItem.USER_TYPE_GROUP;
        if (message.getChatType() == EMMessage.ChatType.Chat) {
            item.mUserId = message.getFrom();
        } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            item.mUserId = message.getTo();
        }
        item.mUserName = message.getStringAttribute("userName", "");
        item.mHeadPhoto = message.getStringAttribute("userPhoto", "");

        Bundle bundle = new Bundle();
        bundle.putSerializable("chatItem", item);

    }

    /**
     * 显示通知、活动、认证消息
     * @param message
     */
    private void showNoticeFragment(EMMessage message) {
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
            buffer.append("&token=").append(com.knowbox.teacher.modules.utils.Utils.getToken());
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
