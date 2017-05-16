package com.buang.welewolf.modules.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.widgets.headerviewpager.InnerScrollerContainer;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UIUtils;
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineClassListInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.updateclass.ClassGroupsChangeListener;
import com.buang.welewolf.base.services.updateclass.UpdateClassService;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.classes.AddGradeClassFragment;
import com.buang.welewolf.modules.classes.ClassInfoFragment;
import com.buang.welewolf.modules.classes.ClassStudentFragment;
import com.buang.welewolf.modules.classes.ClassUnitFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.SubjectUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.widgets.RoundDisplayer;
import com.buang.welewolf.widgets.headerviewpager.MagicHeaderViewPager;
import com.buang.welewolf.widgets.headerviewpager.OuterPagerAdapter;
import com.buang.welewolf.widgets.headerviewpager.OuterScroller;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by weilei on 16/7/6.
 */
public class MainHomeFragment extends BaseUIFragment<UIFragmentHelper> {

    private RelativeLayout mRootView;
    private MagicHeaderViewPager mViewPager;
    private ItemAdapter pagerAdapter;
    private View tabSync;
    private View tabStudent;
    private RelativeLayout mHeadInfoView;
    private PopupWindow popupWindow;

    private UpdateClassService updateClassService;
    private List<ClassInfoItem> classInfoItems;
    private ClassInfoItem mCurClass;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_WITH_TITLE);
        updateClassService = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
        updateClassService.getObserver().addClassGroupChangeListener(classGroupsChangeListener);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_home, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mRootView = (RelativeLayout) view.findViewById(R.id.main_home_layout);
        pagerAdapter = new ItemAdapter(getChildFragmentManager());
        final BaseUIFragment unitFrangment = ClassUnitFragment.newFragment(getActivity(), ClassUnitFragment.class, null);
        BaseUIFragment studentFragment = ClassStudentFragment.newFragment(getActivity(), ClassStudentFragment.class, null);
        List<BaseUIFragment> fragments = new ArrayList<BaseUIFragment>();
        fragments.add(studentFragment);
        fragments.add(unitFrangment);
        pagerAdapter.setItems(fragments);
        mViewPager = new MagicHeaderViewPager(getActivity()) {
            @Override
            protected void initTabsArea(LinearLayout container) {
                LinearLayout layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        UIUtils.dip2px(50));

                LinearLayout tab = (LinearLayout) View.inflate(getActivity(), R.layout.layout_home_tab, null);

                tabSync = tab.findViewById(R.id.tab_sync);
                tabStudent = tab.findViewById(R.id.tab_syudent);

                tabSync.setOnClickListener(mOnclickListener);
                tabStudent.setOnClickListener(mOnclickListener);
                tabStudent.setSelected(true);

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

        mHeadInfoView = (RelativeLayout) View.inflate(getActivity(), R.layout.layout_home_header, null);
        mViewPager.addHeaderView(mHeadInfoView);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    tabSync.setSelected(true);
                    tabStudent.setSelected(false);
                } else if (position == 0) {
                    tabSync.setSelected(false);
                    tabStudent.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getUIFragmentHelper().getTitleBar().setTitle("");
        getUIFragmentHelper().getTitleBar().setBackBtnVisible(false);
        getUIFragmentHelper().getTitleBar().setRightMoreTxt("建班", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_CREATE_CLASS, null);
                Bundle mBundle = new Bundle();
                mBundle.putInt("type", AddGradeClassFragment.TYPE_ADD_CLASS);
                AddGradeClassFragment fragment = (AddGradeClassFragment) Fragment
                        .instantiate(getActivity(),
                                AddGradeClassFragment.class.getName(), mBundle);
                fragment.setOnConfirmClickListenr(new AddGradeClassFragment.OnAddClassSucessListenr() {
                    @Override
                    public void onAddClassSucess() {
                    }

                    @Override
                    public void onChangeClassName(String grade, String name) {

                    }
                });
                showFragment(fragment);

//                AssignUnitFragment fragment = AssignUnitFragment.newFragment(getActivity(), AssignUnitFragment.class, null);
//                showFragment(fragment);
            }
        });
        loadDefaultData(PAGE_FIRST);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActionUtils.ACTION_MAIN_TAB);
        MsgCenter.registerLocalReceiver(mReceiver, intentFilter);
    }

    private void updateClassInfo(final ClassInfoItem classInfoItem, boolean isUpdate) {
        if (mCurClass == classInfoItem && !isUpdate) {
            return;
        }
        PreferencesController.setStringValue(ConstantsUtils.PREFS_SELECT_CLASS, classInfoItem.classId);
        mCurClass = classInfoItem;
        if (classInfoItems != null && classInfoItems.size() > 0) {
            for (int i = 0; i < classInfoItems.size(); i++) {
                if (classInfoItems.get(i).classId.equals(classInfoItem.classId)) {
                    classInfoItems.get(i).className = classInfoItem.className;
                    break;
                }
            }
        }
        if (mHeadInfoView != null) {
            ImageView mHead = (ImageView) mHeadInfoView.findViewById(R.id.class_item_header_head);
            final ImageView mClassHeadBg = (ImageView) mHeadInfoView.findViewById(R.id.item_head_bg);
            ImageFetcher.getImageFetcher().loadImage(classInfoItem.mHeadPhoto, mHead, R.drawable.icon_class_item_default_head,
                    new RoundDisplayer(Color.WHITE, 1), new ImageFetcher.ImageFetcherListener() {
                        @Override
                        public void onLoadComplete(String s, final Bitmap bitmap, Object o) {
                            if (null != bitmap) {
                                mClassHeadBg.setImageBitmap(bitmap);
                            } else {
                                mClassHeadBg.setImageResource((R.drawable.img_default_bg));
                            }
                        }
                    });
            mHead.setOnClickListener(mOnclickListener);
            ((TextView) mHeadInfoView.findViewById(R.id.class_item_header_code)).setText("班级部落号:" + classInfoItem.classCode);
            ((TextView) mHeadInfoView.findViewById(R.id.class_item_header_grade)).setText("年级:" + SubjectUtils.getGradeName(classInfoItem.grade));
            ((TextView) mHeadInfoView.findViewById(R.id.class_item_header_count)).setText("班级人数:" + classInfoItem.studentNum);

            getUIFragmentHelper().getTitleBar().setTitle(R.drawable.bt_question_link_array_down, classInfoItem.className, mOnclickListener);
        }
    }

    private void selectClass() {
        if (classInfoItems == null || classInfoItems.size() == 0) {
            return;
        }
        getUIFragmentHelper().getTitleBar().setTitleDrawable(R.drawable.bt_question_link_array_up);
        List<MenuItem> menuItems = new ArrayList<MenuItem>();
        int index = -1;
        for (int i = 0; i < classInfoItems.size(); i++) {
            ClassInfoItem classInfoItem = classInfoItems.get(i);
            menuItems.add(new MenuItem(0, classInfoItem.className, classInfoItem.studentNum + ""));
            if (mCurClass != null && mCurClass == classInfoItem) {
                index = i;
            }
        }
        popupWindow = DialogUtils.getClassSelectPopWindow(getActivity(), index, menuItems,
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ClassInfoItem classInfoItem = classInfoItems.get(position);
                        updateClassService.getObserver().notifyClassSelected(classInfoItem);
                        popupWindow.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                getUIFragmentHelper().getTitleBar().setTitleDrawable(R.drawable.bt_question_link_array_down);
            }
        });
        popupWindow.showAsDropDown(getUIFragmentHelper().getTitleBar());
    }

    private void openClassInfoFragment() {
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("class", mCurClass);
        ClassInfoFragment fragment = (ClassInfoFragment) Fragment
                .instantiate(getActivity(),
                        ClassInfoFragment.class.getName(), mBundle);
        showFragment(fragment);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ActionUtils.ACTION_MAIN_TAB)) {
                int tab = intent.getIntExtra("tab", -1);
                if (tab == MainFragment.TYPE_TAB_TIPS_CLASS) {
                    Hashtable<String, String> paramsMap = (Hashtable<String, String>) intent.getSerializableExtra("params");
                    if (paramsMap != null) {
                        String classId = paramsMap.get("classID");
                        ClassInfoItem classInfoItem = updateClassService.getClassInfoItem(classId);
                        if (classInfoItem != null) {
                            updateClassService.getObserver().notifyClassSelected(classInfoItem);
                        }
                    }
                }
            }
        }
    };

    private View.OnClickListener mOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tab_sync:
                    tabSync.setSelected(true);
                    tabStudent.setSelected(false);
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.tab_syudent:
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_STUDENT_TAB, null);
                    tabSync.setSelected(false);
                    tabStudent.setSelected(true);
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.title_bar_title:
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_SWITCH_CLASS, null);
                    selectClass();
                    break;
                case R.id.class_item_header_head:
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_LOOK_CLASSINFO, null);
                    openClassInfoFragment();
                    break;
            }
        }
    };

    private ClassGroupsChangeListener classGroupsChangeListener = new ClassGroupsChangeListener() {
        @Override
        public void refreshClassGroupList() {
            classInfoItems = updateClassService.getAllClassInfoItem();
            if (classInfoItems == null || classInfoItems.size() == 0) {
                mCurClass = null;
                updateClassService.getObserver().notifyClassSelected(null);
            } else if (mCurClass == null) {
                String classId = PreferencesController.getStringValue(ConstantsUtils.PREFS_SELECT_CLASS);
                int index = 0;
                if (!TextUtils.isEmpty(classId)) {
                    for (int i = 0; i < classInfoItems.size(); i++) {
                        if (classId.equals(classInfoItems.get(i).classId)) {
                            index = i;
                            break;
                        }
                    }
                }
                ClassInfoItem mCurClass = classInfoItems.get(index);
                updateClassService.getObserver().notifyClassSelected(mCurClass);
            }
        }

        @Override
        public void createClassGroupSuccess(ClassInfoItem classInfoItem) {
            classInfoItems = updateClassService.getAllClassInfoItem();
            updateClassService.getObserver().notifyClassSelected(classInfoItem);
        }

        @Override
        public void deleteClassGroupSuccess(ClassInfoItem classInfoItem) {
            classInfoItems = updateClassService.getAllClassInfoItem();
            if (mCurClass != null && classInfoItem != null && mCurClass == classInfoItem) {
                if (classInfoItems.size() > 0) {
                    ClassInfoItem mCurClass = classInfoItems.get(0);
                    updateClassService.getObserver().notifyClassSelected(mCurClass);
                } else {
                    mCurClass = null;
                    updateClassService.getObserver().notifyClassSelected(null);
                }
            }
        }

        @Override
        public void updateClassGroupSuccess(ClassInfoItem classInfoItem) {
            updateClassInfo(classInfoItem, true);
        }

        @Override
        public void onClassSelectChanged(ClassInfoItem classInfoItem) {
            updateClassInfo(classInfoItem, false);
        }
    };

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getClassDetailUrl();
        OnlineClassListInfo result = new DataAcquirer<OnlineClassListInfo>().acquire(url, new OnlineClassListInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        OnlineClassListInfo classListInfo = (OnlineClassListInfo) result;
        updateClassService.addAllClassItem(classListInfo.mClassItems);
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
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

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        MsgCenter.unRegisterLocalReceiver(mReceiver);
        if (updateClassService != null) {
            updateClassService.getObserver().removeClassGroupChangeListener(classGroupsChangeListener);
        }
    }
}
