package com.buang.welewolf.welewolf.fragment;

import android.os.Bundle;
import android.view.View;

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
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_main_game, null);
    }
}
