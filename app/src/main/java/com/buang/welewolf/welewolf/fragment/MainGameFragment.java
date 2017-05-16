package com.buang.welewolf.welewolf.fragment;

import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.welewolf.login.LoginFragment;
import com.buang.welewolf.welewolf.login.PhoneLoginFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

/**
 * Created by weilei on 17/4/24.
 */
public class MainGameFragment extends BaseUIFragment<UIFragmentHelper> {

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_main_game, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);

        view.findViewById(R.id.ivUserView).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivUserView:
                    LoginFragment fragment = LoginFragment.newFragment(getActivity(), LoginFragment.class, null);
                    showFragment(fragment);
                    break;
            }
        }
    };
}
