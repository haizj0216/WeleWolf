package com.buang.welewolf.welewolf.login;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineLoginInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.database.tables.UserTable;
import com.buang.welewolf.base.utils.StringUtils;
import com.buang.welewolf.modules.login.services.LoginListener;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.login.services.UserStateChangeListener;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.utils.UiThreadHandler;

/**
 * Created by weilei on 17/5/16.
 */

public class PhoneLoginFragment extends BaseUIFragment<UIFragmentHelper> {

    private EditText mPhoneEdit;
    private EditText mPswEdit;
    private Button mLogin;
    private TextView mForgetPsw;
    private TextView mNewUser;
    private LoginService mLoginService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mLoginService = (LoginService) getActivity().getSystemService(
                LoginService.SERVICE_NAME);
        mLoginService.getServiceObvserver().addUserStateChangeListener(userStateChangeListener);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("手机号登录");
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        mPhoneEdit = (EditText) view.findViewById(R.id.ivPhoneEdit);
        mPswEdit = (EditText) view.findViewById(R.id.ivPswEdit);
        mLogin = (Button) view.findViewById(R.id.ivLogin);
        mForgetPsw = (TextView) view.findViewById(R.id.ivForgetPsw);
        mNewUser = (TextView) view.findViewById(R.id.ivNewUser);
        mNewUser.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mNewUser.setOnClickListener(clickListener);
        mLogin.setOnClickListener(clickListener);
        mForgetPsw.setOnClickListener(clickListener);

        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginBtn(mPhoneEdit.getText().toString(), mPswEdit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPswEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginBtn(mPhoneEdit.getText().toString(), mPswEdit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_phone_login, null);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivForgetPsw:
                    openRegisterFragment(PhoneRegisterFragment.FROM_FORGET_PSW);
                    break;
                case R.id.ivLogin:
                    loginImp();
                    break;
                case R.id.ivNewUser:
                    openRegisterFragment(PhoneRegisterFragment.FROM_REGISTER);
                    break;
            }
        }
    };

    private void loginImp() {
        mLoginService.login(mPhoneEdit.getText().toString(), mPswEdit.getText().toString(), mLoginListener);
    }

    private UserStateChangeListener userStateChangeListener = new UserStateChangeListener() {
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

    private LoginListener mLoginListener = new LoginListener() {
        @Override
        public void onLoginStart() {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLogin.setEnabled(false);
                    getUIFragmentHelper().getLoadingView().showLoading();
                }
            });
        }

        @Override
        public void onLoginSuccess(OnlineLoginInfo loginInfo) {
            if (loginInfo != null && loginInfo.mUserItem != null) {
                DataBaseManager.getDataBaseManager().getTable(UserTable.class)
                        .addUserInfo(loginInfo.mUserItem);
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showContent();
                        mLogin.setEnabled(true);
                        ToastUtils.showShortToast(getActivity(), "登录成功！");
                    }
                });
            }
        }

        @Override
        public void onLoginFailed(final String message) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLogin.setEnabled(true);
                    showContent();
                    ToastUtils.showShortToast(getActivity(), message);
                }
            });
        }
    };

    private void updateLoginBtn(String phone, String password) {
        mLogin.setEnabled(checkPhonePsw(phone, password));
    }

    private boolean checkPhonePsw(String phone, String password) {
        if (StringUtils.isPhone(phone) && password.length() > 2 && password.length() < 20) {
            return true;
        }
        return false;
    }

    private void openRegisterFragment(int from) {
        Bundle mBundle = new Bundle();
        mBundle.putInt("from", from);
        PhoneRegisterFragment fragment = PhoneRegisterFragment.newFragment(getActivity(), PhoneRegisterFragment.class, mBundle);
        showFragment(fragment);
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (mLoginService != null) {
            mLoginService.getServiceObvserver().removeUserStateChangeListener(userStateChangeListener);
        }
    }
}
