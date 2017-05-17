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

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
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

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int what = message.what;
            switch (what) {
                case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                    break;
                case SMSSDK.EVENT_GET_VERIFICATION_CODE:
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
//        SMSSDK.initSDK(getActivity(), "1decfb2e248c0", "b0ec8a80a79abd480c71499893628bfe");
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

//        EventHandler eh = new EventHandler() {
//
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    mHandler.sendEmptyMessage(event);
//                } else {
//                    ((Throwable) data).printStackTrace();
//                }
//            }
//        };
//        SMSSDK.registerEventHandler(eh);

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
                    openEditInfoFragment();
                    break;
                case R.id.ivSendCode:
                    timer.start();
                    mSend.setEnabled(false);
//                    SMSSDK.getVerificationCode("ch", phoneNum);
                    break;
            }
        }
    };

    private void checkValid() {
        String code = mCodeEdit.getText().toString();
        String psw = mPswEdit.getText().toString();
        if (code.length() == 6 && psw.length() >= 2 && psw.length() <= 20) {
            mBtn.setEnabled(true);
        } else {
            mBtn.setEnabled(false);
        }
    }

    private void openEditInfoFragment() {
//        SMSSDK.submitVerificationCode("ch", phoneNum, mCodeEdit.getText().toString());
        UserInfoEditFragment fragment = UserInfoEditFragment.newFragment(getActivity(), UserInfoEditFragment.class, null);
        showFragment(fragment);
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
//        SMSSDK.unregisterAllEventHandler();
    }
}
