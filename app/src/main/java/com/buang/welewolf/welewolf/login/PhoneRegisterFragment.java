package com.buang.welewolf.welewolf.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineCheckPhoneInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;

/**
 * Created by weilei on 17/5/16.
 */

public class PhoneRegisterFragment extends BaseUIFragment<UIFragmentHelper> {

    public static final int FROM_REGISTER = 0;
    public static final int FROM_FORGET_PSW = 1;

    private EditText mPhone;
    private Button mRegister;
    private int from;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        from = getArguments().getInt("from", FROM_REGISTER);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("手机号注册");
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
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
                loadDefaultData(PAGE_MORE, mPhone.getText().toString(), from);
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
        mBundle.putInt("from", from);
        PswSetFragment fragment = PswSetFragment.newFragment(getActivity(), PswSetFragment.class, mBundle);
        showFragment(fragment);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_phone_register, null);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getCheckPhoneUrl((String) params[0], (int) params[1]);
        OnlineCheckPhoneInfo result = new DataAcquirer<OnlineCheckPhoneInfo>().acquire(url, new OnlineCheckPhoneInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        OnlineCheckPhoneInfo onlineCheckPhoneInfo = (OnlineCheckPhoneInfo) result;
        if (onlineCheckPhoneInfo.status == 0) {
            openPswSetFragment(mPhone.getText().toString());
        } else {
            ToastUtils.showShortToast(getActivity(), "该手机已注册");
        }
    }
}
