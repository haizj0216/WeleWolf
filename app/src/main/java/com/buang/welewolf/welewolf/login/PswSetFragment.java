package com.buang.welewolf.welewolf.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/5/16.
 */

public class PswSetFragment extends BaseUIFragment<UIFragmentHelper> {

    private Button mBtn;
    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_psw_set, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("设置密码");
        mBtn = (Button) view.findViewById(R.id.ivConfirm);
        mBtn.setOnClickListener(onClickListener);
        mBtn.setEnabled(true);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivConfirm:
                    openEditInfoFragment();
                    break;
            }
        }
    };

    private void openEditInfoFragment() {
        UserInfoEditFragment fragment = UserInfoEditFragment.newFragment(getActivity(), UserInfoEditFragment.class, null);
        showFragment(fragment);
    }
}
