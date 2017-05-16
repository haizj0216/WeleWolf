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
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/5/16.
 */

public class PhoneLoginFragment extends BaseUIFragment<UIFragmentHelper> {

    private EditText mPhoneEdit;
    private EditText mPswEdit;
    private Button mLogin;
    private TextView mForgetPsw;
    private TextView mNewUser;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("手机号登录");

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
                    openSetPswFragment();
                    break;
            }
        }
    };

    private void openSetPswFragment() {
        PswSetFragment fragment = PswSetFragment.newFragment(getActivity(), PswSetFragment.class, null);
        showFragment(fragment);
    }
}
