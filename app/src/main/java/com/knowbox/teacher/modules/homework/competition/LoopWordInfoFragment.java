package com.knowbox.teacher.modules.homework.competition;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SimplePagerAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineLoopWordDetailInfo;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.base.bean.OnlineWordInfo;
import com.knowbox.teacher.base.bean.OptionsItemInfo;
import com.knowbox.teacher.base.database.bean.QuestionItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/8.
 */
public class LoopWordInfoFragment extends BaseUIFragment<UIFragmentHelper> {
    private String matchID;
    private OnlineWordInfo mWordInfo;
    private OnlineLoopWordDetailInfo mLoopWordDetailInfo;

    private TextView mTranslation;
    private LinearLayout mDimLayout;
    private ViewPager mViewPager;
    private HorizontalScrollView mScrollView;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        matchID = getArguments().getString(ConstantsUtils.KEY_BUNDLE_MATCHID);
        mWordInfo = (OnlineWordInfo) getArguments().getSerializable(ConstantsUtils.KEY_WORDINFO);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_loop_word_detail, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle(mWordInfo.content);
        getUIFragmentHelper().getTitleBar().setDividerVisible(View.GONE);
        mTranslation = (TextView) view.findViewById(R.id.loop_word_detail_translation);
        mDimLayout = (LinearLayout) view.findViewById(R.id.loop_word_detail_dim);
        mViewPager = (ViewPager) view.findViewById(R.id.loop_word_detail_viewpager);
        mScrollView = (HorizontalScrollView) view.findViewById(R.id.loop_word_detail_dim_scroll);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectDim(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDefaultData(PAGE_FIRST);
            }
        }, 200);
    }

    private void updateData() {
        if (TextUtils.isEmpty(mLoopWordDetailInfo.translation)) {
            mTranslation.setVisibility(View.GONE);
        } else {
            mTranslation.setText(mLoopWordDetailInfo.translation);
        }
        if (mLoopWordDetailInfo.questionDimInfos != null && mLoopWordDetailInfo.questionDimInfos.size() > 0) {
            int size = mLoopWordDetailInfo.questionDimInfos.size();
            List<LoopWordDetailItemFragment> fragments = new ArrayList<LoopWordDetailItemFragment>();
            for (int i = 0; i < size; i++) {
                OnlineLoopWordDetailInfo.QuestionDimInfo dimInfo = mLoopWordDetailInfo.questionDimInfos.get(i);
                final View view = View.inflate(getActivity(), R.layout.layout_loop_word_detail_dim_item, null);
                TextView dimTitle = (TextView) view.findViewById(R.id.loop_word_detail_dim_title);
                TextView dimCount = (TextView) view.findViewById(R.id.loop_word_detail_dim_count);
                ImageView dimImage = (ImageView) view.findViewById(R.id.loop_word_detail_dim_image);
                dimImage.setImageResource(SubjectUtils.getQuestionTypeIcon(dimInfo.dimID));
                dimTitle.setText(dimInfo.dimTitle);
                dimCount.setText("(" + dimInfo.totalWrongCount + "次)");
                mDimLayout.addView(view);

                final int finalI = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectDimView(finalI);
                    }
                });

                Bundle mBundle = new Bundle();
                mBundle.putSerializable(ConstantsUtils.KEY_BUNDLE_QUESTION_LIST, (ArrayList<QuestionItem>) dimInfo.questionItems);
                LoopWordDetailItemFragment fragment = LoopWordDetailItemFragment.newFragment(getActivity(),
                        LoopWordDetailItemFragment.class, mBundle, AnimType.ANIM_NONE);
                fragments.add(fragment);
            }
            SimplePagerAdapter<LoopWordDetailItemFragment> adapter = new SimplePagerAdapter<LoopWordDetailItemFragment>(getChildFragmentManager());
            adapter.setItems(fragments);
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(fragments.size() / 2);
            mDimLayout.getChildAt(0).setSelected(true);
        }
    }

    private void selectDim(int position) {
        for (int i = 0; i < mDimLayout.getChildCount(); i++) {
            if (i == position) {
                mDimLayout.getChildAt(i).setSelected(true);
            } else {
                mDimLayout.getChildAt(i).setSelected(false);
            }
        }

        View currentView = mDimLayout.getChildAt(position);
        int left = currentView.getLeft();     //获取点击控件与父控件左侧的距离
        int width = currentView.getMeasuredWidth();   //获得控件本身宽度
        int toX = left + width / 2 - UIUtils.getWindowWidth(getActivity()) / 2;
        mScrollView.smoothScrollTo(toX, 0);
    }

    private void selectDimView(int position) {
        mViewPager.setCurrentItem(position, true);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getReportWordDetailUrl(matchID, mWordInfo.wordID);
        OnlineLoopWordDetailInfo result = new DataAcquirer<OnlineLoopWordDetailInfo>().
                acquire(url, new OnlineLoopWordDetailInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mLoopWordDetailInfo = (OnlineLoopWordDetailInfo) result;
        updateData();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
    }
}
