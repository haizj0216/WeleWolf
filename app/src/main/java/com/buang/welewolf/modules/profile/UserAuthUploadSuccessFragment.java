package com.buang.welewolf.modules.profile;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.MsgCenter;
import com.buang.welewolf.R;
import com.buang.welewolf.modules.main.MainProfileFragment;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

/**
 * @name 用户教师资格证上传照片页面
 * @author liuyu
 * @date 2015-8-7
 */
public class UserAuthUploadSuccessFragment extends BaseUIFragment<UIFragmentHelper> {
    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_profile_upload_document_success, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getTitleBar().setTitle("上传成功");
        getUIFragmentHelper().getTitleBar().setRightMoreTxt("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MsgCenter.sendLocalBroadcast(new Intent(
                MainProfileFragment.ACTION_USERINFO_CHANGE));
        MsgCenter.sendLocalBroadcast(new Intent(
                UserInfoEditFragment.ACTION_INFOEDIT_CHANGE));
    }
}
