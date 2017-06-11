package com.buang.welewolf.modules.guild;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.adapter.SimplePagerAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/6.
 */

public class FindGuildFragment extends BaseUIFragment<UIFragmentHelper> {

    private View mRecommend;
    private View mRank;
    private ImageView mCreate;
    private ImageView mFind;
    private ViewPager mViewPager;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_find_guild, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        mRecommend = view.findViewById(R.id.rvRecommend);
        mRank = view.findViewById(R.id.rvRank);
        mViewPager = (ViewPager) view.findViewById(R.id.rViewPager);
        mRecommend.setOnClickListener(onClickListener);
        mRank.setOnClickListener(onClickListener);
        view.findViewById(R.id.ivBack).setOnClickListener(onClickListener);

        mRecommend.setSelected(true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mRank.setSelected(false);
                    mRecommend.setSelected(true);
                } else if (position == 1) {
                    mRank.setSelected(true);
                    mRecommend.setSelected(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        SimplePagerAdapter<BaseUIFragment> pagerAdapter = new SimplePagerAdapter<>(getChildFragmentManager());
        RecommendGuildFragment activityFragment = RecommendGuildFragment.newFragment(getActivity(), RecommendGuildFragment.class, null, AnimType.ANIM_NONE);
        RankGuildFragment taskFragment = RankGuildFragment.newFragment(getActivity(), RankGuildFragment.class, null, AnimType.ANIM_NONE);

        List<BaseUIFragment> fragments = new ArrayList<>();
        fragments.add(activityFragment);
        fragments.add(taskFragment);
        pagerAdapter.setItems(fragments);

        mViewPager.setAdapter(pagerAdapter);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.rvRecommend:
                    mRank.setSelected(false);
                    mRecommend.setSelected(true);
                    mViewPager.setCurrentItem(0, true);
                    break;
                case R.id.rvRank:
                    mRank.setSelected(true);
                    mRecommend.setSelected(false);
                    mViewPager.setCurrentItem(1, true);
                    break;
                case R.id.ivBack:
                    finish();
                    break;
            }
        }
    };
}
