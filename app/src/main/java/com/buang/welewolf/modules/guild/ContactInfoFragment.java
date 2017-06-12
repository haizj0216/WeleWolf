package com.buang.welewolf.modules.guild;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.ContactInfo;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/6/1.
 */

public class ContactInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private ContactInfo mContactInfo;
    private View mBack;
    private TextView mSettingView;

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
        view.findViewById(R.id.title_bar_back).setOnClickListener(onClickListener);
        mSettingView = (TextView) view.findViewById(R.id.tvSetting);
        mSettingView.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.title_bar_back:
                    finish();
                    break;
                case R.id.tvSetting:
                    ContactSettingFragment fragment = ContactSettingFragment.newFragment(
                            getActivity(), ContactSettingFragment.class, null);
                    showFragment(fragment);
                    break;
            }
        }
    };
}
