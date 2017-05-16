package com.buang.welewolf.welewolf.login;

import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/5/16.
 */

public class PhoneRegisterFragment extends BaseUIFragment<UIFragmentHelper> {

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("手机号注册");

    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_phone_register, null);
    }
}
