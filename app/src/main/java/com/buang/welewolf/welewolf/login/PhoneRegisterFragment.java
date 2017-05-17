package com.buang.welewolf.welewolf.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/5/16.
 */

public class PhoneRegisterFragment extends BaseUIFragment<UIFragmentHelper> {

    private EditText mPhone;
    private Button mRegister;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("手机号注册");
        mPhone = (EditText) view.findViewById(R.id.ivPhoneEdit);
        mRegister = (Button) view.findViewById(R.id.ivRegister);
        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkPhonePsw(mPhone.getText().toString())) {
                    mRegister.setEnabled(true);
                } else {
                    mRegister.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO register
                openPswSetFragment(mPhone.getText().toString());
            }
        });
    }

    private boolean checkPhonePsw(String phone) {
        if (phone.length() == 11) {
            return true;
        }
        return false;
    }

    private void openPswSetFragment(String phone) {
        Bundle mBundle = new Bundle();
        mBundle.putString("phone", phone);
        PswSetFragment fragment = PswSetFragment.newFragment(getActivity(), PswSetFragment.class, mBundle);
        showFragment(fragment);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_phone_register, null);
    }
}
