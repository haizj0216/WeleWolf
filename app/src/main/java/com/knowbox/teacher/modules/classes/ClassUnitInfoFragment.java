package com.knowbox.teacher.modules.classes;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hyena.framework.app.adapter.SimplePagerAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineUnitInfo;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/14.
 */
public class ClassUnitInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private OnlineUnitInfo mUnitInfo;
    private ClassInfoItem mClassInfoItem;
    private View mTabWord;
    private View mTabStudent;
    private ViewPager mViewPager;
    private SimplePagerAdapter<BaseUIFragment> mAdapter;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mUnitInfo = (OnlineUnitInfo) getArguments().getSerializable(ConstantsUtils.KEY_UNITINFO);
        mClassInfoItem = getArguments().getParcelable(ConstantsUtils.KEY_CLASSINFOITEM);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_class_unit_info, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle(mUnitInfo.mUnitName + mUnitInfo.mSubName);
        mTabStudent = view.findViewById(R.id.tab_student);
        mTabWord = view.findViewById(R.id.tab_word);
        mTabStudent.setSelected(true);
        mTabWord.setOnClickListener(mOnClickListener);
        mTabStudent.setOnClickListener(mOnClickListener);

        mViewPager = (ViewPager) view.findViewById(R.id.unit_info_viewpager);
        mViewPager.setOffscreenPageLimit(1);
        mAdapter = new SimplePagerAdapter<BaseUIFragment>(getChildFragmentManager());

        List<BaseUIFragment> fragments = new ArrayList<BaseUIFragment>();
        UnitWordListFragment wordListFragment = UnitWordListFragment.newFragment(getActivity(), UnitWordListFragment.class, getArguments());
        UnitStudentListFragment studentListFragment = UnitStudentListFragment.newFragment(getActivity(), UnitStudentListFragment.class, getArguments());
        fragments.add(studentListFragment);
        fragments.add(wordListFragment);
        mAdapter.setItems(fragments);
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mTabWord.setSelected(false);
                    mTabStudent.setSelected(true);
                } else if (position == 1) {
                    mTabWord.setSelected(true);
                    mTabStudent.setSelected(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tab_student:
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_UNIT_STUDENT_TAB, null);
                    mViewPager.setCurrentItem(0, true);
                    break;
                case R.id.tab_word:
                    mViewPager.setCurrentItem(1, true);
                    break;
            }
        }
    };
}
