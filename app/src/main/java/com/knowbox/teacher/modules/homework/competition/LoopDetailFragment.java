package com.knowbox.teacher.modules.homework.competition;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineCompetitionListInfo;
import com.knowbox.teacher.base.bean.OnlineReportStudentListInfo;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.widgets.headerviewpager.InnerScrollerContainer;
import com.knowbox.teacher.widgets.headerviewpager.MagicHeaderViewPager;
import com.knowbox.teacher.widgets.headerviewpager.OuterPagerAdapter;
import com.knowbox.teacher.widgets.headerviewpager.OuterScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/6.
 */
public class LoopDetailFragment extends BaseUIFragment<UIFragmentHelper> {
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
    private TextView mHeaderRate;
    private TextView mHeaderScore;
    private TextView mHeaderTip;
    private TextView mHeaderRule;

    private Dialog mDialog;

    private OnlineReportStudentListInfo reportStudentListInfo;

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
        getUIFragmentHelper().getTitleBar().setTitle("比赛详情");
        mRootView = (RelativeLayout) view.findViewById(R.id.main_home_layout);
        pagerAdapter = new ItemAdapter(getChildFragmentManager());
        Bundle mBundle = new Bundle();
        mBundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, competitionItem.matchID);
        final LoopStudentFragment unitFrangment = LoopStudentFragment.newFragment(getActivity(), LoopStudentFragment.class, mBundle, AnimType.ANIM_NONE);
        LoopWordFragment studentFragment = LoopWordFragment.newFragment(getActivity(), LoopWordFragment.class, mBundle, AnimType.ANIM_NONE);
        List<BaseUIFragment> fragments = new ArrayList<BaseUIFragment>();
        fragments.add(unitFrangment);
        fragments.add(studentFragment);
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

        mHeadInfoView = (RelativeLayout) View.inflate(getActivity(), R.layout.layout_loop_detail_header, null);
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

        mHeaderCount = (TextView) mHeadInfoView.findViewById(R.id.loop_detail_header_count);
        mHeaderScore = (TextView) mHeadInfoView.findViewById(R.id.loop_detail_header_score);
        mClassName = (TextView) mHeadInfoView.findViewById(R.id.loop_detail_header_class);
        mHeaderRate = (TextView) mHeadInfoView.findViewById(R.id.loop_detail_header_rate);
        mClassImage = (ImageView) mHeadInfoView.findViewById(R.id.loop_detail_header_head);
        mHeaderTip = (TextView) mHeadInfoView.findViewById(R.id.loop_detail_header_tip);
        mHeaderRule = (TextView) mHeadInfoView.findViewById(R.id.loop_detail_header_rule);
        mHeaderRule.setOnClickListener(mOnclickListener);

        getUIFragmentHelper().getLoadingView().showLoading();
        unitFrangment.setOnDataLoadListener(new LoopStudentFragment.OnDataLoadListener() {
            @Override
            public void onDataLoad(OnlineReportStudentListInfo info) {
                showContent();
                if (info == null) {
                    getUIFragmentHelper().getEmptyView().showEmpty("获取数据失败");
                    return;
                }
                reportStudentListInfo = info;
                updateData();
            }
        });
        getUIFragmentHelper().getTitleBar().setRightMoreTxt("历史榜单", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistoryFragment();
            }
        });
    }

    /**
     * 刷新首页数据
     */
    private void updateData() {
        mClassName.setText(reportStudentListInfo.className);
        ImageFetcher.getImageFetcher().loadImage(reportStudentListInfo.classImage, mClassImage,
                R.drawable.bt_message_default_head, new RoundDisplayer());
        mHeaderCount.setText(reportStudentListInfo.averAnswerCount + "");
        mHeaderRate.setText(reportStudentListInfo.averRightRate + "");
        if (reportStudentListInfo.isLastRank()) {
            if (reportStudentListInfo.mStudyStudentList == null || reportStudentListInfo.mStudyStudentList.size() ==0) {
                mHeaderScore.setText("--");
            } else {
                mHeaderScore.setText(reportStudentListInfo.averControlRate + "");
                if (reportStudentListInfo.averControlRate < 60) {
                    mHeaderScore.setTextColor(getResources().getColor(R.color.color_button_red));
                } else if (reportStudentListInfo.averControlRate < 80) {
                    mHeaderScore.setTextColor(getResources().getColor(R.color.color_fcc622));
                } else {
                    mHeaderScore.setTextColor(getResources().getColor(R.color.color_main_app));
                }
            }

            mHeaderTip.setVisibility(View.INVISIBLE);
            getUIFragmentHelper().getTitleBar().setTitle("本次比赛结果");
        } else if (reportStudentListInfo.isUnBegining()){
            mHeaderScore.setText("--");
            mHeaderTip.setVisibility(View.VISIBLE);
            mHeaderTip.setText("(暂无数据)");
            getUIFragmentHelper().getTitleBar().setTitle("本场比赛未开始");
        } else {
            mHeaderScore.setText("--");
            mHeaderTip.setVisibility(View.VISIBLE);
            mHeaderTip.setText("(比赛完成后结算)");
            getUIFragmentHelper().getTitleBar().setTitle(DateUtil.getMonthDay(reportStudentListInfo.date) + "比赛日报");
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
                case R.id.loop_detail_header_rule:
                    showRuleDialog();
                    break;
            }
        }
    };

    private void showRuleDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        String msg = "单词练熟程度模型：采用100分制，从单词掌握、练习覆盖、题型平衡、参赛活跃、答题速度等5个维度，综合衡量学生的单词练习情况，比赛结束给出评分。";
        mDialog = DialogUtils.getNewMessageDialog(getActivity(),R.drawable.icon_dialog_rule, "" ,msg, "", "确定", new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void openHistoryFragment() {
        Bundle mBundle = new Bundle();
        mBundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, competitionItem.matchID);
        LoopHistoryFragment fragment = LoopHistoryFragment.newFragment(getActivity(),
                LoopHistoryFragment.class, mBundle);
        showFragment(fragment);
    }

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
