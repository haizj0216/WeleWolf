package com.knowbox.teacher.modules.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.CityModel;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.modules.login.searchschool.CityListAdapter;
import com.knowbox.teacher.modules.login.searchschool.CityListLoader;
import com.knowbox.teacher.modules.login.searchschool.CityLoadListener;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.login.services.UpdateSchoolListener;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

/**
 * Created by weilei on 15/10/24.
 */
public class CitySelectFragment extends BaseUIFragment<UIFragmentHelper> {

    private CityModel mCurCityModel;
    private TextView mCityNameView;
    private ListView mListView;
    private CityListAdapter mAdapter;
    private CityListLoader sCityListLoader;
    private String mFromClazzName;
    private LoginService loginService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        if (getArguments() != null) {
            mFromClazzName = getArguments().getString("from_clazzName");
            mCurCityModel = (CityModel) getArguments().getSerializable("city");
            loginService = (LoginService) BaseApp.getAppContext()
                    .getSystemService(LoginService.SERVICE_NAME);
            loginService.getServiceObvserver().addUpdateSchoolListener(updateSchoolListener);
        }
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_city_choose, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("选择地区");
        mCityNameView = (TextView) view
                .findViewById(R.id.dialog_choose_city_name);
        mListView = (ListView) view
                .findViewById(R.id.dialog_choose_city_list);
        mAdapter = new CityListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(onItemClickListener);
        updateCityNameView();
        sCityListLoader = new CityListLoader(getActivity());
        sCityListLoader.setLoadListener(mCityLoadListener);
        if (mCurCityModel != null && mCurCityModel.getChildren() != null) {
            mAdapter.setItems(mCurCityModel.getChildren());
        } else {
            UiThreadHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sCityListLoader.loadOnlineCityPlist();
                }
            }, 200);
        }

    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CityModel city = (CityModel) parent.getItemAtPosition(position);
            if (city != null && city.getChildren() != null
                    && city.getChildren().size() > 0) {
                Bundle mBundle = new Bundle();
                if (mCurCityModel != null) {
                    mBundle.putSerializable(mCurCityModel.getFullName(), mCurCityModel);
                }
                mBundle.putSerializable("city", city);
                mBundle.putString("from_clazzName", mFromClazzName);
                CitySelectFragment fragment = CitySelectFragment.newFragment
                        (getActivity(), CitySelectFragment.class, mBundle);
                showFragment(fragment);
            } else {
                Bundle mBundle = new Bundle();
                mBundle.putString("from_clazzName", mFromClazzName);
                mBundle.putSerializable("city", city);
                SchoolSelectFragment fragment = SchoolSelectFragment.newFragment
                        (getActivity(), SchoolSelectFragment.class, mBundle);
                showFragment(fragment);
            }
        }
    };

    CityLoadListener mCityLoadListener = new CityLoadListener() {
        @Override
        public void onPreLoad() {
            getUIFragmentHelper().getLoadingView().showHomeworkLoading();
        }

        @Override
        public void onPostLoad(CityModel cities) {
            CityModel temp = cities;
            if (temp != null && temp.getChildren() != null) {
                if (temp.getChildren().size() > 0) {
                    showContent();
                    mAdapter.setItems(temp.getChildren());
                } else {
                    getUIFragmentHelper().getEmptyView().showEmpty();
                }
            } else {
                getUIFragmentHelper().getEmptyView().showEmpty();
            }
        }
    };

    private void updateCityNameView() {
        if (mCurCityModel == null) {
            mCityNameView.setVisibility(View.GONE);
        } else {
            mCityNameView.setVisibility(View.VISIBLE);
            mCityNameView.setText(mCurCityModel.getFullName());
        }
    }

    private UpdateSchoolListener updateSchoolListener = new UpdateSchoolListener() {
        @Override
        public void onUpdateSuccess(UserItem userItem, boolean isUserDefined) {
            finish();
        }

        @Override
        public void onUpdateFailed(String error, boolean isUserDefined) {
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
