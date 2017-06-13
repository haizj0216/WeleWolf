package com.buang.welewolf.welewolf.fragment;

import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineUserInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.profile.SystemSettingFragment;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.welewolf.login.LoginFragment;
import com.buang.welewolf.welewolf.login.PhoneLoginFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;

/**
 * Created by weilei on 17/4/24.
 */
public class MainGameFragment extends BaseUIFragment<UIFragmentHelper> {

    private final int ACTION_GET_USERINFO = 1;

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
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        view.findViewById(R.id.ivUserView).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivSetting).setOnClickListener(onClickListener);
//        loadData(ACTION_GET_USERINFO, PAGE_MORE, Utils.getLoginUserItem().userId);
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
                case R.id.ivSetting:
                    showFragment(SystemSettingFragment.newFragment(getActivity(), SystemSettingFragment.class, null));
                    break;
            }
        }
    };

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_GET_USERINFO) {
            String url = OnlineServices.getUserInfoUrl((String) params[0]);
            OnlineUserInfo result = new DataAcquirer<OnlineUserInfo>().acquire(url, new OnlineUserInfo(), -1);
            return result;
        }
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
    }
}
