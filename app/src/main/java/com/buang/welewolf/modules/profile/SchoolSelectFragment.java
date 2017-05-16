package com.buang.welewolf.modules.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.buang.welewolf.base.bean.CityModel;
import com.buang.welewolf.base.bean.PinyinIndexModel;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.modules.login.searchschool.SchoolListLoader;
import com.buang.welewolf.modules.login.searchschool.SchoolLoadListener;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.login.services.UpdateSchoolListener;
import com.buang.welewolf.widgets.pinned_listview.IndexBarView;
import com.buang.welewolf.widgets.pinned_listview.PinnedHeaderAdapter;
import com.buang.welewolf.widgets.pinned_listview.PinnedHeaderListView;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 15/10/25.
 */
public class SchoolSelectFragment extends BaseUIFragment<UIFragmentHelper> {

    private CityModel mCurCityModel;
    private PinnedHeaderListView mListView;
    private TextView mCityNameView;
    private View mCreateView;
    private String mFromClazzName;
    private LoginService loginService;

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
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_school_select, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("选择学校");
        getUIFragmentHelper().getEmptyView().setEmptyMargin(78);
        mListView = (PinnedHeaderListView) view
                .findViewById(com.buang.welewolf.R.id.dialog_choose_school_list);
        mCityNameView = (TextView) view
                .findViewById(com.buang.welewolf.R.id.dialog_choose_school_city_name);
        mCityNameView.setText(mCurCityModel.getFullName());
        mListView.setOnItemClickListener(onItemClickListener);
        mCreateView = view.findViewById(com.buang.welewolf.R.id.dialog_choose_school_not_found);
        mCreateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateSchoolFragment();
            }
        });
        loadSchoolInfo();

    }

    private void openCreateSchoolFragment() {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("city", mCurCityModel);
        mBundle.putString("from_clazzName", mFromClazzName);
        CreateSchoolFragment fragment = CreateSchoolFragment.newFragment(getActivity(),
                CreateSchoolFragment.class, mBundle);
        showFragment(fragment);
    }

    private void loadSchoolInfo() {
        new SchoolListLoader(new SchoolLoadListener() {

            @Override
            public void onPreLoad() {
                getUIFragmentHelper().getLoadingView().showLoading();
            }

            @Override
            public void onPostLoad(List<PinyinIndexModel> data,
                                   ArrayList<Integer> sectionPos, boolean isfailed) {
                if (getActivity() == null) {
                    return;
                }
                if (data != null && data.size() > 0) {
                    showContent();
                    final float density = getResources().getDisplayMetrics().density;
                    PinnedHeaderAdapter adapter = new PinnedHeaderAdapter(
                            getActivity(), data, sectionPos);
                    mListView.setAdapter(adapter);
                    LayoutInflater inflater = (LayoutInflater) getActivity()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View pinnedHeaderView = inflater.inflate(
                            com.buang.welewolf.R.layout.dialog_choose_school_section_row_view,
                            mListView, false);
                    AbsListView.LayoutParams sectionParams = new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT,
                            (int) (23 * density));
                    pinnedHeaderView.setLayoutParams(sectionParams);
                    mListView.setPinnedHeaderView(pinnedHeaderView);
                    IndexBarView indexBarView = (IndexBarView) inflater
                            .inflate(com.buang.welewolf.R.layout.index_bar_view, mListView, false);
                    indexBarView.setData(mListView, data, sectionPos);
                    mListView.setIndexBarView(indexBarView);
                    View previewTextView = inflater.inflate(
                            com.buang.welewolf.R.layout.index_bar_preview_view, mListView, false);
                    mListView.setPreviewView(previewTextView);
                    mListView.setOnScrollListener(adapter);
                } else {
                    if (isfailed) {
                        getUIFragmentHelper().getEmptyView().showEmpty();
                    }else {
                        getUIFragmentHelper().getEmptyView().showEmpty("暂无学校");
                    }
                }

            }
        }).execute(mCurCityModel.getId());
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            PinyinIndexModel school = (PinyinIndexModel) parent.getItemAtPosition(position);
                String school_id = school.getId();
            String keys[] = {"school_id"};
            String values[] = {school_id};
            loginService.updateSchool(keys, values, false);
        }
    };

    private UpdateSchoolListener updateSchoolListener = new UpdateSchoolListener() {
        @Override
        public void onUpdateSuccess(UserItem userItem, boolean isUserDefined) {
            if (!isUserDefined) {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(getActivity(), "更新学校成功");
                    }
                });
            }
            finish();
        }

        @Override
        public void onUpdateFailed(final String error, boolean isUserDefined) {
            if (!isUserDefined) {
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
