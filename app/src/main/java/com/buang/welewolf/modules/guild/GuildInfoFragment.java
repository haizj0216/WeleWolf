package com.buang.welewolf.modules.guild;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineMemberListInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.widgets.CustomScrollView;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by weilei on 17/5/27.
 */

public class GuildInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private LinearLayout mMemberPhoto;
    private OnlineMemberListInfo memberListInfo;
    private CustomScrollView mScrollView;
    private View mTopView;
    private View mBackView;
    private View mSettingView;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        setTitleStyle(STYLE_NO_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_guild_info, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);

        mScrollView = (CustomScrollView) view.findViewById(R.id.svScrollview);
        mTopView = view.findViewById(R.id.rvTopLayou);

        view.findViewById(R.id.tvSetting).setOnClickListener(onClickListener);
        view.findViewById(R.id.title_bar_back).setOnClickListener(onClickListener);
        view.findViewById(R.id.rvMembers).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivTopBack).setOnClickListener(onClickListener);
        view.findViewById(R.id.tvTopSetting).setOnClickListener(onClickListener);

        mBackView = view.findViewById(R.id.title_bar_back);
        mSettingView = view.findViewById(R.id.tvSetting);
        mMemberPhoto = (LinearLayout) view.findViewById(R.id.ivMembers);
        initData();

        mScrollView.setOnScrollListener(new CustomScrollView.OnCustomScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                int height = UIUtils.dip2px(10);
                if (scrollY > height) {
                    float alpha = (float) height / scrollY;
                    LogUtil.d("scroll:", " +" + alpha);
                    mTopView.setVisibility(View.VISIBLE);
                    ViewCompat.setAlpha(mTopView, 1 - alpha);
                    mBackView.setVisibility(View.GONE);
                    mSettingView.setVisibility(View.GONE);
                } else {
                    mTopView.setVisibility(View.INVISIBLE);
                    mBackView.setVisibility(View.VISIBLE);
                    mSettingView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tvSetting:
                case R.id.tvTopSetting:
                    showFragment(GuildSettingFragment.newFragment(getActivity(), GuildSettingFragment.class, null));
                    break;
                case R.id.title_bar_back:
                    finish();
                    break;
                case R.id.rvMembers:
                    openMemberListFragment();
                    break;
                case R.id.ivTopBack:
                    finish();
                    break;
            }
        }
    };

    private void initData() {
        memberListInfo = new OnlineMemberListInfo();
        memberListInfo.mMembers = new ArrayList<>();
        int size = 4;
        for (int i = 0; i < size; i++) {
            UserItem contactInfo = new UserItem();
            contactInfo.userName = "李一" + "i";
            memberListInfo.mMembers.add(contactInfo);
        }
        addBuildMember();
    }

    private void addBuildMember() {
        mMemberPhoto.removeAllViews();
        for (int i = 0; i < memberListInfo.mMembers.size(); i++) {
            View view = View.inflate(getActivity(), R.layout.layout_welewolf_guild_member_item, null);
            mMemberPhoto.addView(view);
        }
    }

    private void openMemberListFragment() {
        GuildMemberFragment fragment = GuildMemberFragment.newFragment(getActivity(), GuildMemberFragment.class, null);
        showFragment(fragment);
    }
}
