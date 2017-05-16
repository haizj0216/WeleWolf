package com.buang.welewolf.modules.homework.competition;

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
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineLoopWordDetailInfo;
import com.buang.welewolf.base.bean.OnlineWordInfo;
import com.buang.welewolf.base.bean.OptionsItemInfo;
import com.buang.welewolf.base.database.bean.QuestionItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.SubjectUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 17/4/14.
 */
public class SingleTestWordInfoFragment extends BaseUIFragment<UIFragmentHelper> {
    private String matchID;
    private OnlineWordInfo mWordInfo;
    private OnlineLoopWordDetailInfo mWordDetailInfo;

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

    private void initData() {
        showContent();
        mWordDetailInfo = new OnlineLoopWordDetailInfo();

        mWordDetailInfo.translation = "n.汉语翻译";
        mWordDetailInfo.questionDimInfos = new ArrayList<OnlineLoopWordDetailInfo.QuestionDimInfo>();
        for (int i = 0; i < 4; i++) {
            OnlineLoopWordDetailInfo.QuestionDimInfo dimInfo = new OnlineLoopWordDetailInfo.QuestionDimInfo();
            dimInfo.dimID = i + 1;
            dimInfo.totalWrongCount = 100 + i * 7;
            dimInfo.questionItems = buildQuestions(i);
            mWordDetailInfo.questionDimInfos.add(dimInfo);
        }
        updateData();
    }

    private List<QuestionItem> buildQuestions(int index) {
        List<QuestionItem> questionList = new ArrayList<QuestionItem>();
        for (int i = 0; i < 2; i++) {
            QuestionItem question = new QuestionItem();
            question.wrongCount = 10 + i * 6;
            question.gymText = "Hello World" + index;
            question.itemList = new ArrayList<OptionsItemInfo>();
            question.itemList.add(new OptionsItemInfo("A", "你好" + i));
            question.itemList.add(new OptionsItemInfo("B", "好的"));
            question.itemList.add(new OptionsItemInfo("C", "不是"));
            question.itemList.add(new OptionsItemInfo("D", "不好"));
            question.mRightAnswer = "A";
            questionList.add(question);
        }
        return questionList;
    }

    private void updateData() {
        if (TextUtils.isEmpty(mWordDetailInfo.translation)) {
            mTranslation.setVisibility(View.GONE);
        } else {
            mTranslation.setText(mWordDetailInfo.translation);
        }
        if (mWordDetailInfo.questionDimInfos != null && mWordDetailInfo.questionDimInfos.size() > 0) {
            int size = mWordDetailInfo.questionDimInfos.size();
            List<LoopWordDetailItemFragment> fragments = new ArrayList<LoopWordDetailItemFragment>();
            for (int i = 0; i < size; i++) {
                OnlineLoopWordDetailInfo.QuestionDimInfo dimInfo = mWordDetailInfo.questionDimInfos.get(i);
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
                LoopWordDetailItemFragment fragment = newFragment(getActivity(),
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
        String url = OnlineServices.getSingletestWordDetailUrl(matchID, mWordInfo.wordID);
        return new DataAcquirer<OnlineLoopWordDetailInfo>().acquire(url, new OnlineLoopWordDetailInfo(), -1);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mWordDetailInfo = (OnlineLoopWordDetailInfo) result;
        updateData();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
//        initData();
    }
}
