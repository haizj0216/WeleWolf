package com.buang.welewolf.modules.guild;

import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/6/1.
 */

public class ContactInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        setTitleStyle(STYLE_NO_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_contact_info, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
    }
}
