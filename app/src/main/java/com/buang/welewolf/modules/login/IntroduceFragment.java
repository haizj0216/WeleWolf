package com.buang.welewolf.modules.login;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

import com.buang.welewolf.welewolf.login.LoginFragment;
import com.hyena.framework.app.adapter.SimplePagerAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.BaseApp;
import com.buang.welewolf.App;
import com.buang.welewolf.R;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fanjb
 * @name 应用引导页面，左右滑动显示应用最新特性
 * @date 2015-3-13
 */
public class IntroduceFragment extends BaseUIFragment<UIFragmentHelper> {

    public static final String BACK_FROM_LOGIN_REGIST = "back_fromloginorregist";

    private ViewPager viewPager;

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_introduce, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);

        SimplePagerAdapter<BaseUIFragment> adapter = new SimplePagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.answer_viewpager);
        viewPager.setAdapter(adapter);

        List<BaseUIFragment> fragments = new ArrayList<BaseUIFragment>();
        LoginFragment loginFragment = newFragment(getActivity(), LoginFragment.class, null, AnimType.ANIM_NONE);
        if (!PreferencesController.getBoolean(BACK_FROM_LOGIN_REGIST, false)) {
            IntroduceImageFragment introduceImageFragment = newFragment(getActivity(), IntroduceImageFragment.class, null);
            fragments.add(introduceImageFragment);
        }
        fragments.add(loginFragment);
        adapter.setItems(fragments);
        adapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getActivity() != null) {
                ((App) BaseApp.getAppContext()).exit();
                getActivity().finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        if (viewPager != null) {
            viewPager.removeAllViews();
            viewPager = null;
        }
    }

}
