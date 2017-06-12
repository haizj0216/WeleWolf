package com.buang.welewolf.welewolf.login;

import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/5/15.
 */

public class LoginFragment extends BaseUIFragment<UIFragmentHelper> {

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_login, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        view.findViewById(R.id.ivRegister).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivLogin).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivRegister:
                    openRegisterFragment();
                    break;
                case R.id.ivLogin:
                    openLoginFragment();
                    break;
            }
        }
    };

    private void openLoginFragment() {
        PhoneLoginFragment fragment = PhoneLoginFragment.newFragment(getActivity(), PhoneLoginFragment.class, null);
        showFragment(fragment);
    }

    private void openRegisterFragment() {
        Bundle mBundle = new Bundle();
        mBundle.putInt("from", PhoneRegisterFragment.FROM_REGISTER);
        PhoneRegisterFragment fragment = PhoneRegisterFragment.newFragment(getActivity(), PhoneRegisterFragment.class, mBundle);
        showFragment(fragment);
    }

}
