package com.buang.welewolf.modules.guild;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.ContactInfo;
import com.buang.welewolf.base.bean.OnlineMemberListInfo;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

import java.util.ArrayList;

/**
 * Created by weilei on 17/5/27.
 */

public class GuildInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private LinearLayout mMemberPhoto;
    private OnlineMemberListInfo memberListInfo;

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
        view.findViewById(R.id.tvSetting).setOnClickListener(onClickListener);
        view.findViewById(R.id.title_bar_back).setOnClickListener(onClickListener);
        view.findViewById(R.id.rvMembers).setOnClickListener(onClickListener);
        mMemberPhoto = (LinearLayout) view.findViewById(R.id.ivMembers);
        initData();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tvSetting:
                    showFragment(GuildSettingFragment.newFragment(getActivity(), GuildSettingFragment.class, null));
                    break;
                case R.id.title_bar_back:
                    finish();
                    break;
                case R.id.rvMembers:
                    openMemberListFragment();
                    break;
            }
        }
    };

    private void initData() {
        memberListInfo = new OnlineMemberListInfo();
        memberListInfo.mMembers = new ArrayList<>();
        int size = 4;
        for (int i = 0; i < size; i++) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.name = "李一" + "i";
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
