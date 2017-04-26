package com.knowbox.teacher.modules.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.CityModel;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.login.services.UpdateSchoolListener;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.widgets.CleanableEditText;

/**
 * Created by weilei on 15/10/26.
 */
public class CreateSchoolFragment extends BaseUIFragment<UIFragmentHelper> {
    private CityModel mCurCityModel;
    CleanableEditText mEditView;
    private String mFromClazzName;
    LoginService loginService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mCurCityModel = (CityModel) getArguments().getSerializable("city");
        mFromClazzName = getArguments().getString("from_clazzName");
        loginService = (LoginService) BaseApp.getAppContext()
                .getSystemService(LoginService.SERVICE_NAME);
        loginService.getServiceObvserver().addUpdateSchoolListener(updateSchoolListener);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_create_school, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("创建学校");
        TextView city = (TextView) view
                .findViewById(R.id.dialog_choose_school_not_found_city_name);
        city.setText(mCurCityModel.getFullName());

        mEditView = (CleanableEditText) view
                .findViewById(R.id.dialog_choose_school_not_found_create);
        mEditView.getEditText().setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
        mEditView.setHintTextColor(getActivity().getResources().getColor(R.color.color_text_third));

        getUIFragmentHelper().getTitleBar().setRightMoreTxt("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEditView.getText())) {
                    String keys[] = {"city_id", "school_name"};
                    String values[] = {mCurCityModel.getId(), mEditView.getText()};
                    loginService.updateSchool(keys, values, true);
                } else {
                    ToastUtils.showShortToast(getActivity(), "请输入学校名称");
                }

            }
        });

    }

    private UpdateSchoolListener updateSchoolListener = new UpdateSchoolListener() {
        @Override
        public void onUpdateSuccess(UserItem userItem, boolean isUserDefined) {
            if (isUserDefined) {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(getActivity(), "创建学校成功");
                    }
                });
            }
            finish();
        }

        @Override
        public void onUpdateFailed(final String error, boolean isUserDefined) {
            if (isUserDefined) {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(getActivity(), error);
                    }
                });

            }
            finish();
        }
    };

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (null != loginService) {
            loginService.getServiceObvserver().removeUpdateSchoolListener(updateSchoolListener);
        }
    }
}
