package com.buang.welewolf.modules.profile;

import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.login.services.LogoutListener;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/6/12.
 */

public class SystemSettingFragment extends BaseUIFragment<UIFragmentHelper> {

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("系统设置");
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        view.findViewById(R.id.tvLoginOut).setOnClickListener(clickListener);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_system_setting, null);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tvLoginOut:
                    LoginService loginService = (LoginService) getSystemService(LoginService.SERVICE_NAME);
                    loginService.logout(new LogoutListener() {
                        @Override
                        public void onLogoutStart() {
                            getUIFragmentHelper().getLoadingView().showLoading();
                        }

                        @Override
                        public void onLogoutSuccess() {
                            showContent();
                        }

                        @Override
                        public void onLogoutFailed() {
                            showContent();
                        }
                    });
                    break;
            }
        }
    };
}
