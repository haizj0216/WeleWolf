package com.knowbox.teacher.modules.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.BuildConfig;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.utils.ActionUtils;
import com.knowbox.teacher.base.utils.DebugUtils;
import com.knowbox.teacher.base.utils.DirContext;
import com.knowbox.teacher.base.utils.FileUtils;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.login.forgetpass.ForgetPasswordFragment;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.login.services.LogoutListener;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fanjb
 * @name 系统设置页面
 * @date 2015-3-17
 */
public class SettingsFragment extends BaseUIFragment<UIFragmentHelper> {
    public static final String ISNOTIFY = "IS_NOTIFY";

    private static String apiNameDev = "开发环境";
    private static String apiNameQA = "QA环境";
    private static String apiNamePreview = "预览环境";
    private static String apiNameRelease = "线上环境";
    private static String apiNamebyWrite = "我要自己输入";

    private LoginService mLoginService;
    private UserItem mUserItem;
    private TextView mCacheSizeText;
    private Dialog mDialog;
    private RelativeLayout mSwitchAPIlayout;
    private TextView mCurrentAPI;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mLoginService = (LoginService) BaseApp.getAppContext()
                .getSystemService(LoginService.SERVICE_NAME);
        mUserItem = mLoginService.getLoginUser();
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("设置");
        if (mUserItem == null) {
            return null;
        }
        View view = View.inflate(getActivity(), R.layout.layout_settings, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.settings_user_phone_number))
                .setText(mUserItem.loginName);
        mCacheSizeText = (TextView) view.findViewById(R.id.settings_cache_size);
        view.findViewById(R.id.settings_modify_password_layout)
                .setOnClickListener(mOnClickListener);
        view.findViewById(R.id.settings_ablout_layout).setOnClickListener(
                mOnClickListener);
        view.findViewById(R.id.settings_clear_cache).setOnClickListener(
                mOnClickListener);
        view.findViewById(R.id.exit_app_layout).setOnClickListener(
                mOnClickListener);
        view.findViewById(R.id.profile_settings_notify_toggle).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.settings_feedback_layout).setOnClickListener(mOnClickListener);

        mSwitchAPIlayout = (RelativeLayout) view.findViewById(R.id.settings_switchapi_layout);
        mCurrentAPI = (TextView) view.findViewById(R.id.type_api);
        if (BuildConfig.LOG_DEBUG) {
            mSwitchAPIlayout.setVisibility(View.VISIBLE);
            mSwitchAPIlayout.setOnClickListener(mOnClickListener);

            String apiName = apiNameDev;
            int mAPIType = PreferencesController.getInt(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_API, 0);
            if (mAPIType == 1) {
                apiName = apiNameQA;
            }else if (mAPIType == 3) {
                apiName = apiNamePreview;
            } else if (mAPIType == 4) {
                apiName = apiNameRelease;
            } else if (mAPIType == 5) {
                apiName = "自己输入的";
            }
            mCurrentAPI.setText(apiName);
        } else {
            mSwitchAPIlayout.setVisibility(View.GONE);
        }

        boolean notify = PreferencesController.getBoolean(ISNOTIFY, true);
        ((ImageView) view.findViewById(R.id.profile_settings_notify_toggle)).setImageResource(notify
                ? R.drawable.btn_toggle_button_cache_on
                : R.drawable.btn_toggle_button_cache_off);

        refreshCacheSize();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 修改密码
                case R.id.settings_modify_password_layout: {
                    if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
                        ActionUtils.notifyVirtualTip();
                        return;
                    }
                    ForgetPasswordFragment fragment = (ForgetPasswordFragment) Fragment
                            .instantiate(getActivity(), ForgetPasswordFragment.class.getName());
                    showFragment(fragment);
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_MODIFYPWD, null);
                    break;
                }
                // 关于
                case R.id.settings_ablout_layout: {
                    BaseUIFragment fragment = (BaseUIFragment) Fragment
                            .instantiate(getActivity(),
                                    AboutAppFragment.class.getName());
                    showFragment(fragment);
                    break;
                }
                // 退出
                case R.id.exit_app_layout:
                    showLoginOutDialog();
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_LOGINOUT, null);
                    break;

                // 清除缓存
                case R.id.settings_clear_cache:
                    DebugUtils.clearAllCache();
                    refreshCacheSize();
                    ToastUtils.showShortToast(getActivity(), "缓存已清空");
                    break;
                case R.id.profile_settings_notify_toggle:
                    updateNotify(v);
                    break;

                case R.id.settings_feedback_layout:
                    openServiceChatFragment();
                    break;
                case R.id.settings_switchapi_layout:
                    //切换api
                    switchAPI();
                    break;

                default:
                    break;
            }
        }
    };

    private void openServiceChatFragment() {
        Bundle bundle = new Bundle();
        ChatListItem item = new ChatListItem();
        item.mUserId = "10";
        item.mUserName = "单词部落客服";
        item.mHeadPhoto = "http://file.knowbox.cn/upload/service/head_photo.png";
        bundle.putSerializable("chatItem", item);
    }

    private void switchAPI() {
        if (BuildConfig.LOG_DEBUG) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            List<MenuItem> items = new ArrayList<MenuItem>();
            items.add(new MenuItem(0, apiNameDev, ""));
            items.add(new MenuItem(1, apiNameQA, ""));
            items.add(new MenuItem(2, apiNamePreview, ""));
            items.add(new MenuItem(3, apiNameRelease, ""));
            String api = PreferencesController.getStringValue(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_APIPREFIX);
            items.add(new MenuItem(4, apiNamebyWrite, api));
            mDialog = DialogUtils.getListDialog(getActivity(), "修改API接口", items,
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            if (mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                            String apiName = apiNameDev;
                            if (position == 0) {
                                PreferencesController.setInt(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_API, 0);
                                apiName = apiNameDev;
                            } else if (position == 1) {
                                PreferencesController.setInt(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_API, 1);
                                apiName = apiNameQA;
                            } else if (position == 2) {
                                PreferencesController.setInt(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_API, 2);
                                apiName = apiNamePreview;
                            } else if (position == 3) {
                                PreferencesController.setInt(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_API, 3);
                                apiName = apiNameRelease;
                            } else if (position == 4) {
                                WriteAPIFragment fragment = (WriteAPIFragment) Fragment.instantiate(getActivity(), WriteAPIFragment.class.getName(), null);
                                fragment.setOnAPIWriteListener(new WriteAPIFragment.APIwriteListener() {
                                    @Override
                                    public void apiWrited(String api) {
                                        PreferencesController.setInt(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_API, 4);
                                        PreferencesController.setStringValue(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_APIPREFIX, api);
                                        DebugUtils.clearAllCache();
                                        refreshCacheSize();
                                        ToastUtils.showShortToast(getActivity(), "切换API后, 请重启应用");
                                    }
                                });
                                showFragment(fragment);
                            }
                            if (position != 4) {
                                mCurrentAPI.setText(apiName);
                            } else {
                                mCurrentAPI.setText("自己输入的");
                            }

                            //清除缓存
                            DebugUtils.clearAllCache();
                            refreshCacheSize();
                            ToastUtils.showShortToast(getActivity(), "切换API后, 请重启应用");
                        }
                    });
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
        }
    }

    private void updateNotify(View v) {
        boolean notify = PreferencesController.getBoolean(ISNOTIFY, true);
        PreferencesController.setBoolean(ISNOTIFY, !notify);
        ((ImageView) v).setImageResource(!notify ? R.drawable.btn_toggle_button_cache_on
                : R.drawable.btn_toggle_button_cache_off);
        if (notify) {
            LogUtil.d("uMengStatistics:", "b_message_note_on");
            MobclickAgent.onEvent(BaseApp.getAppContext(),
                    UmengConstant.EVENT_MESSAGE_NOTE_ON);
        } else {
            LogUtil.d("uMengStatistics:", "b_message_note_off");
            MobclickAgent.onEvent(BaseApp.getAppContext(),
                    UmengConstant.EVENT_MESSAGE_NOTE_OFF);
        }
    }

    private void showLoginOutDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getMessageDialog(getActivity(), "确认退出", "确定", "取消", "确定退出当前账号？", new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                    mLoginService.logout(new LogoutListener() {
                        @Override
                        public void onLogoutSuccess() {
                            showContent();
                        }

                        @Override
                        public void onLogoutStart() {
                            getLoadingView().showLoading("正在退出登录...");
                        }

                        @Override
                        public void onLogoutFailed() {
                            showContent();
                        }
                    });
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void refreshCacheSize() {
        new Thread() {
            public void run() {
                calculateCacheSize();
            }
        }.start();
    }

    private void calculateCacheSize() {
        long size = 0;
        try {
            size = FileUtils.getFolderSize(DirContext.getImageCacheDir());
        } catch (Exception e) {
        }
        final String sizeFormatStr = FileUtils.byteCountToDisplaySize(size, 2);
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCacheSizeText != null && sizeFormatStr != null) {
                    mCacheSizeText.setText("当前缓存" + sizeFormatStr);
                }
            }
        });
    }
}
