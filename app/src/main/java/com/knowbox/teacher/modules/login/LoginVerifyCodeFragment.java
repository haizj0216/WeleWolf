package com.knowbox.teacher.modules.login;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineLoginInfo;
import com.knowbox.teacher.base.bean.OnlineSmsCodeInfo;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.database.tables.UserTable;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.login.services.SmsLoginListener;
import com.knowbox.teacher.modules.login.services.UserStateChangeListener;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.AdvanceTimer;
import com.knowbox.teacher.widgets.CleanableEditText;
import com.knowbox.teacher.widgets.ResizeLayout;

/**
 * Created by LiuYu on 2016/6/3.
 */
public class LoginVerifyCodeFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final String USER_PHONENUMBER = "knowbox_user_phonenumber";

    private static final int ACTION_SEND_CODE = 0;

    private TextView mPhoneNumber;
    private CleanableEditText mVerifyCode;
    private TextView mVerifyCodeSend;
    private TextView mLoginBtn;

    private ResizeLayout mMainLayout;
    private View mDividert;
    private View mDividerm;
    private View mDividerb;
    private LoginService mLoginService;

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            checkVerifyCodeValid();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private String mPhoneNum;

    private void checkVerifyCodeValid() {
        String verifyCode = mVerifyCode.getText();
        if (verifyCode.length() == 4) {
            mLoginBtn.setEnabled(true);
        }else {
            mLoginBtn.setEnabled(false);
        }
    }

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        setTitleStyle(STYLE_NO_TITLE);
        mPhoneNum = getArguments().getString("phone_number");
        mLoginService = (LoginService) getActivity().getSystemService(LoginService.SERVICE_NAME);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_login_verify_phonenumber, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        ImageButton mBackView = (ImageButton) view.findViewById(R.id.title_bar_back);
        mBackView.setOnClickListener(mOnclickListener);

        mDividert = view.findViewById(R.id.divider_hint_top);
        mDividerm = view.findViewById(R.id.divider_hint_middle);
        mDividerb = view.findViewById(R.id.divider_buttom_hint);

        mMainLayout = (ResizeLayout) view.findViewById(R.id.login_verify_layout);
        mPhoneNumber = (TextView) view.findViewById(R.id.login_phone_number);
        StringBuffer buffer = new StringBuffer(mPhoneNum);
        buffer.insert(3, " ").insert(8, " ");
        mPhoneNumber.setText(buffer.toString());
        mVerifyCode = (CleanableEditText) view.findViewById(R.id.login_verify_code);
        mVerifyCodeSend = (TextView) view.findViewById(R.id.login_verifycode_cend);
        mLoginBtn = (TextView) view.findViewById(R.id.login_btn);
        mLoginBtn.setEnabled(false);

        mVerifyCode.setMaxLength(4);
        mVerifyCode.setHint("请输入4位验证码");
        mVerifyCode.setHintTextColor(getActivity().getResources().getColor(R.color.color_text_third));
        mVerifyCode.getEditText().setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
        mVerifyCode.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_TEXT_VARIATION_PHONETIC);
        mVerifyCode.addTextChangedListener(mTextWatcher);

        mMainLayout.setOnResizeListener(new ResizeLayout.OnResizeListener() {
            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {
                if (h < oldh) {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Gone
                            mDividert.setVisibility(View.GONE);
                            mDividerm.setVisibility(View.GONE);
                            mDividerb.setVisibility(View.GONE);
                        }
                    });
                } else {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //visible
                            mDividert.setVisibility(View.VISIBLE);
                            mDividerm.setVisibility(View.VISIBLE);
                            mDividerb.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
        mMainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UIUtils.hideInputMethod(getActivity());
                return true;
            }
        });

        if(null == mLoginService.getReSendSmsCodeTimer() || null == mLoginService.getSmsCodeValidTimer()) {
            mLoginService.executeCountDown(60, getArguments().getString("last_time"), mTimeChangerListener);
        }else {
            mLoginService.continueTimer(mTimeChangerListener);
            if (null != mLoginService.getReSendSmsCodeTimer() && mLoginService.getReSendSmsCodeTimer().getCurSeconds() <= 0) {
                mVerifyCodeSend.setText("重新获取验证码");
                mVerifyCodeSend.setTextColor(getActivity().getResources().getColor(R.color.color_main_app));
                mVerifyCodeSend.setClickable(true);
            }
        }

        mVerifyCodeSend.setOnClickListener(mOnclickListener);
        mLoginBtn.setOnClickListener(mOnclickListener);
        mLoginService.getServiceObvserver().addUserStateChangeListener(userStateChangeListener);
    }

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn:
                    smsLoginImpl();
                    break;
                case R.id.login_verifycode_cend:
                    loadData(ACTION_SEND_CODE, PAGE_MORE);
                    break;
                case R.id.title_bar_back:
                    finish();
            }
        }
    };

    UserStateChangeListener userStateChangeListener = new UserStateChangeListener() {
        @Override
        public void onLogin(UserItem user) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }

        @Override
        public void onLogout(UserItem user) {

        }
    };

    private void smsLoginImpl() {
        UIUtils.hideInputMethod(getActivity());
        UmengConstant.reportUmengEvent(UmengConstant.EVENT_VERIFYCODE_LOGIN_BTN_CLICK, null);
        mLoginService.smsLogin(mPhoneNum, mVerifyCode.getText(), mSmsLoginListener);
    }

    private SmsLoginListener mSmsLoginListener = new SmsLoginListener() {
        @Override
        public void onSmsLoginStart() {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLoginBtn.setEnabled(false);
                    getUIFragmentHelper().getLoadingView().showLoading();
                }
            });

        }

        @Override
        public void onSmsLoginSuccess(OnlineLoginInfo loginInfo) {
            if (loginInfo != null && loginInfo.mUserItem != null) {
                PreferencesController.setStringValue(USER_PHONENUMBER, loginInfo.mUserItem.loginName);
                // 保存用户信息、班级列表到数据库
                DataBaseManager.getDataBaseManager().getTable(UserTable.class)
                        .addUserInfo(loginInfo.mUserItem);
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLoginBtn.setEnabled(true);
                        showContent();
                        ToastUtils.showShortToast(getActivity(), "验证登录成功！");
                    }
                });
                VirtualClassUtils.getInstance(getActivity()).setVirtualToken(false);
            }
        }

        @Override
        public void onSmsLoginFailed(final String message) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLoginBtn.setEnabled(true);
                    showContent();
                    Dialog dialog = DialogUtils.getMessageDialog(getActivity(),
                            "提示", "确认", "", message,
                            new DialogUtils.OnDialogButtonClickListener() {
                                @Override
                                public void onItemClick(Dialog dialog, int btnId) {
                                    dialog.dismiss();
                                }
                            });
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }
            });
        }
    };

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        if (action == ACTION_SEND_CODE) {
            String url = OnlineServices.getSendSmsCode4Regist(mPhoneNum, "1");
            return new DataAcquirer<OnlineSmsCodeInfo>().get(url, new OnlineSmsCodeInfo());
        }
        return null;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_SEND_CODE) {
            OnlineSmsCodeInfo mSmsCodeInfo = (OnlineSmsCodeInfo) result;
            if (mSmsCodeInfo.isAvailable()) {
                ToastUtils.showShortToast(getActivity(), "已发送验证码");
                mLoginService.executeCountDown(60, mSmsCodeInfo.lastTime, mTimeChangerListener);
            }
        }
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (mLoginService != null) {
            mLoginService.getServiceObvserver().removeUserStateChangeListener(userStateChangeListener);
        }
    }

    /**
     * 监听计时变化
     */
    private AdvanceTimer.TimeChangeListener mTimeChangerListener = new AdvanceTimer.TimeChangeListener() {
        @Override
        public void onPreChanged(int mCurSeconds) {
            mVerifyCodeSend.setClickable(false);
        }

        @Override
        public void onTimeChanged(final int mCurSeconds) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mVerifyCodeSend.setText(mCurSeconds + "s后重新发送");
                    mVerifyCodeSend.setTextColor(getActivity().getResources().getColor(R.color.color_text_primary));
                    mVerifyCodeSend.setClickable(false);
                }
            });
        }

        @Override
        public void onFinish(int mCurSeconds) {
            mVerifyCodeSend.setText("重新获取验证码");
            mVerifyCodeSend.setTextColor(getActivity().getResources().getColor(R.color.color_main_app));
            mVerifyCodeSend.setClickable(true);
        }
    };


}
