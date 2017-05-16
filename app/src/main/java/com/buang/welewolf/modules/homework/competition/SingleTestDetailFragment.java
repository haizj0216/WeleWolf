package com.buang.welewolf.modules.homework.competition;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.widgets.headerviewpager.InnerScrollerContainer;
import com.buang.welewolf.widgets.headerviewpager.MagicHeaderViewPager;
import com.buang.welewolf.widgets.headerviewpager.OuterScroller;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UIUtils;
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineCompetitionListInfo;
import com.buang.welewolf.base.bean.OnlineTestStudentListInfo;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.widgets.headerviewpager.OuterPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 17/4/12.
 */
public class SingleTestDetailFragment extends BaseUIFragment<UIFragmentHelper> {
    private OnlineCompetitionListInfo.CompetitionItem competitionItem;

    private RelativeLayout mRootView;
    private MagicHeaderViewPager mViewPager;
    private ItemAdapter pagerAdapter;
    private View tabWord;
    private View tabStudent;
    private RelativeLayout mHeadInfoView;
    private ImageView mClassImage;
    private TextView mClassName;
    private TextView mHeaderCount;
    private TextView mHeaderErrorCount;
    private TextView mHeaderScore;

    private OnlineTestStudentListInfo studentListInfo;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        if (getArguments() != null) {
            competitionItem = (OnlineCompetitionListInfo.CompetitionItem) getArguments()
                    .getSerializable(ConstantsUtils.KEY_BUNDLE_CHAM_ITEM);
        }
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_home, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("本次测验结果");
        mRootView = (RelativeLayout) view.findViewById(R.id.main_home_layout);
        pagerAdapter = new ItemAdapter(getChildFragmentManager());
        Bundle mBundle = new Bundle();
        mBundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, competitionItem.matchID);
        final SingleTestStudentFragment studentFrangment = SingleTestStudentFragment.newFragment(getActivity(), SingleTestStudentFragment.class, mBundle, AnimType.ANIM_NONE);
        SingleTestWordFragment wordFragment = SingleTestWordFragment.newFragment(getActivity(), SingleTestWordFragment.class, mBundle, AnimType.ANIM_NONE);
        List<BaseUIFragment> fragments = new ArrayList<BaseUIFragment>();
        fragments.add(studentFrangment);
        fragments.add(wordFragment);
        pagerAdapter.setItems(fragments);
        mViewPager = new MagicHeaderViewPager(getActivity()) {
            @Override
            protected void initTabsArea(LinearLayout container) {
                LinearLayout layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        UIUtils.dip2px(50));

                LinearLayout tab = (LinearLayout) View.inflate(getActivity(), R.layout.layout_loop_info_tab, null);

                tabWord = tab.findViewById(R.id.tab_word);
                tabStudent = tab.findViewById(R.id.tab_student);
                tabStudent.setSelected(true);

                tabWord.setOnClickListener(mOnclickListener);
                tabStudent.setOnClickListener(mOnclickListener);

                container.addView(layout, lp);
                layout.addView(tab, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                setTabsArea(layout);
                setPagerSlidingTabStrip(tab);
            }
        };
        mViewPager.setoffScreenPageLimit(1);
        mViewPager.setPagerAdapter(pagerAdapter);
        mRootView.addView(mViewPager, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        mHeadInfoView = (RelativeLayout) View.inflate(getActivity(), R.layout.layout_singletest_detail_header, null);
        mViewPager.addHeaderView(mHeadInfoView);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tabWord.setSelected(false);
                    tabStudent.setSelected(true);
                } else if (position == 1) {
                    tabWord.setSelected(true);
                    tabStudent.setSelected(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mHeaderCount = (TextView) mHeadInfoView.findViewById(R.id.singletest_detail_header_count);
        mHeaderScore = (TextView) mHeadInfoView.findViewById(R.id.singletest_detail_header_score);
        mClassName = (TextView) mHeadInfoView.findViewById(R.id.singletest_detail_header_class);
        mHeaderErrorCount = (TextView) mHeadInfoView.findViewById(R.id.singletest_detail_header_errorcount);
        mClassImage = (ImageView) mHeadInfoView.findViewById(R.id.singletest_detail_header_head);

        getUIFragmentHelper().getLoadingView().showLoading();
        studentFrangment.setOnDataLoadListener(new SingleTestStudentFragment.OnDataLoadListener() {
            @Override
            public void onDataLoad(OnlineTestStudentListInfo info) {
                showContent();
                if (info == null) {
                    getUIFragmentHelper().getEmptyView().showEmpty("获取数据失败");
                    return;
                }
                studentListInfo = info;
                updateData();
            }
        });
    }

    /**
     * 刷新首页数据
     */
    private void updateData() {
        if (studentListInfo.isNoStart()) {
            getUIFragmentHelper().getTitleBar().setTitle("本次测验未开始");
        }else {
            getUIFragmentHelper().getTitleBar().setTitle("本次测验结果");
        }
        mClassName.setText(studentListInfo.className);
        ImageFetcher.getImageFetcher().loadImage(studentListInfo.classImage, mClassImage,
                R.drawable.bt_message_default_head, new RoundDisplayer());
        mHeaderCount.setText(studentListInfo.joinerCount);
        mHeaderErrorCount.setText(studentListInfo.highErrorWordCount);

        mHeaderScore.setText(studentListInfo.avgScore);
        if (Integer.parseInt(studentListInfo.avgScore) < 60) {
            mHeaderScore.setTextColor(getResources().getColor(R.color.color_button_red));
        } else if (Integer.parseInt(studentListInfo.avgScore) < 80) {
            mHeaderScore.setTextColor(getResources().getColor(R.color.color_fcc622));
        } else {
            mHeaderScore.setTextColor(getResources().getColor(R.color.color_main_app));
        }

    }

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tab_student:
                    tabWord.setSelected(false);
                    tabStudent.setSelected(true);
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.tab_word:
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_STUDENT_TAB, null);
                    tabWord.setSelected(true);
                    tabStudent.setSelected(false);
                    mViewPager.setCurrentItem(1);
                    break;

            }
        }
    };


    class ItemAdapter extends FragmentPagerAdapter implements OuterPagerAdapter {
        private OuterScroller mOuterScroller;
        private List<BaseUIFragment> mFragments;
        private FragmentManager fm;

        public ItemAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public void setItems(List<BaseUIFragment> fragments) {
            if (null != mFragments) {
                FragmentTransaction transaction = fm.beginTransaction();
                for (BaseUIFragment fragment : mFragments) {
                    transaction.remove(fragment);
                }
                transaction.commit();
                transaction = null;
                fm.executePendingTransactions();
            }
            this.mFragments = fragments;
            notifyDataSetChanged();
        }

        @Override
        public BaseSubFragment getItem(int i) {
            if (null == mFragments || mFragments.size() == 0) {
                return null;
            }
            if (i < mFragments.size()) {
                return mFragments.get(i);
            } else {
                return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            InnerScrollerContainer fragment =
                    (InnerScrollerContainer) super.instantiateItem(container, position);
            if (null != mOuterScroller) {
                fragment.setOuterScroller(mOuterScroller, position);
            }
            return fragment;
        }

        @Override
        public void setOuterScroller(OuterScroller outerScroller) {
            mOuterScroller = outerScroller;
        }

        @Override
        public int getCount() {
            if (null == mFragments) {
                return 0;
            }
            return mFragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
