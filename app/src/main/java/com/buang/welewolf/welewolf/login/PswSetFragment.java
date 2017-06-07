package com.buang.welewolf.welewolf.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.hyena.framework.app.fragment.BaseUIFragment;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by weilei on 17/5/16.
 */

public class PswSetFragment extends BaseUIFragment<UIFragmentHelper> {

    private Button mBtn;
    private CheckBox mEye;
    private Button mSend;
    private EditText mCodeEdit;
    private EditText mPswEdit;

    private String phoneNum;
    private String country;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int what = message.what;
            switch (what) {
                case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                    ToastUtils.showShortToast(getActivity(), "验证码发送成功");
                    break;
                case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                    openEditInfoFragment();
                    break;
                case 101:
                    ToastUtils.showShortToast(getActivity(), "验证码发送失败");
                    break;
                case 100:
                    ToastUtils.showShortToast(getActivity(), "验证码错误");
                    break;
            }
        }
    };

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        phoneNum = getArguments().getString("phone");
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_psw_set, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("设置密码");
        SMSSDK.initSDK(getActivity(), "1decfb2e248c0", "b0ec8a80a79abd480c71499893628bfe");
        mEye = (CheckBox) view.findViewById(R.id.ivEye);
        mBtn = (Button) view.findViewById(R.id.ivConfirm);
        mSend = (Button) view.findViewById(R.id.ivSendCode);
        mCodeEdit = (EditText) view.findViewById(R.id.ivCodeEdit);
        mPswEdit = (EditText) view.findViewById(R.id.ivPswEdit);

        mSend.setOnClickListener(onClickListener);
        mBtn.setOnClickListener(onClickListener);

        ((TextView) view.findViewById(R.id.ivPhoneNum)).setText(phoneNum);

        mEye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPswEdit.setInputType(mEye.isChecked() ? InputType.TYPE_TEXT_VARIATION_NORMAL : InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    mHandler.sendEmptyMessage(event);
                } else {
                    ((Throwable) data).printStackTrace();
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        mHandler.sendEmptyMessage(100);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        mHandler.sendEmptyMessage(101);
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh);
        SMSSDK.getSupportedCountries();
        country = Utils.getCurrentCountry();
        mCodeEdit.addTextChangedListener(textWatcher);
        mPswEdit.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkValid();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mSend.setText(millisUntilFinished / 1000 + "");
        }

        @Override
        public void onFinish() {
            mSend.setEnabled(true);
            mSend.setText("重新发送");
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivConfirm:
                    SMSSDK.submitVerificationCode(country, phoneNum, mCodeEdit.getText().toString());
                    break;
                case R.id.ivSendCode:
                    timer.start();
                    mSend.setEnabled(false);
                    SMSSDK.getVerificationCode(country, phoneNum);
                    break;
            }
        }
    };

    private void checkValid() {
        String code = mCodeEdit.getText().toString();
        String psw = mPswEdit.getText().toString();
        if (code.length() > 0 && psw.length() >= 2 && psw.length() <= 20) {
            mBtn.setEnabled(true);
        } else {
            mBtn.setEnabled(false);
        }
    }

    private void openEditInfoFragment() {
        UserInfoEditFragment fragment = UserInfoEditFragment.newFragment(getActivity(), UserInfoEditFragment.class, null);
        showFragment(fragment);
    }


    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        SMSSDK.unregisterAllEventHandler();
    }
}
