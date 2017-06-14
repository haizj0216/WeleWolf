package com.buang.welewolf.modules.guild;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.hyena.framework.app.adapter.SimplePagerAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/6.
 */

public class FindGuildFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_CREATE_GUILD = 1;
    private static final int ACTION_FIND_GUILD = 2;

    private View mRecommend;
    private View mRank;
    private ViewPager mViewPager;
    private Dialog mDialog;

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
        view.findViewById(R.id.ivCreate).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivFind).setOnClickListener(onClickListener);

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

    private void showSearchDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getFillBlackDialog(getActivity(), "请输入公会ID", "确定", "取消", "", InputType.TYPE_CLASS_NUMBER, new DialogUtils.OnFillDialogBtnClickListener() {
            @Override
            public void onItemClick(Dialog dialog, boolean isConfirm, String resutl) {
                if (isConfirm) {
                    loadData(ACTION_FIND_GUILD, PAGE_MORE, resutl);
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void showCreateDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getFillBlackDialog(getActivity(), "请输入公会名称", "确定", "取消", "", -1, new DialogUtils.OnFillDialogBtnClickListener() {
            @Override
            public void onItemClick(Dialog dialog, boolean isConfirm, String resutl) {
                if (isConfirm) {
                    loadData(ACTION_CREATE_GUILD, PAGE_MORE, resutl);
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
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
                case R.id.ivCreate:
                    if (Utils.getLoginUserItem().guildIno != null) {
                        ToastUtils.showShortToast(getActivity(), "你已经有公会了，无法再创建了");
                        return;
                    }
                    showCreateDialog();
                    break;
                case R.id.ivFind:
                    showSearchDialog();
                    break;
            }
        }
    };

    private void openGuildFragment(OnlineGuildInfo guildInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putString("guildID", guildInfo.guildID);
        showFragment(GuildInfoFragment.newFragment(getActivity(), GuildInfoFragment.class, mBundle));
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_CREATE_GUILD) {
            String url = OnlineServices.getCreateGuildUrl((String) params[0]);
            OnlineGuildInfo result = new DataAcquirer<OnlineGuildInfo>().acquire(url, new OnlineGuildInfo(), -1);
            return result;
        } else if (action == ACTION_FIND_GUILD) {
            String url = OnlineServices.getFindGuildUrl((String) params[0]);
            OnlineGuildInfo result = new DataAcquirer<OnlineGuildInfo>().acquire(url, new OnlineGuildInfo(), -1);
            return result;
        }
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_CREATE_GUILD || action == ACTION_FIND_GUILD) {
            OnlineGuildInfo guildInfo = (OnlineGuildInfo) result;
            openGuildFragment(guildInfo);
        } else if (action == ACTION_FIND_GUILD) {
            OnlineGuildInfo guildInfo = (OnlineGuildInfo) result;
            openGuildFragment(guildInfo);
        }
    }
}
