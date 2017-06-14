package com.buang.welewolf.modules.guild;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.buang.welewolf.base.bean.OnlineGuildListInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.services.GuildService;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.WelewolfUtils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;

import java.util.ArrayList;

/**
 * Created by weilei on 17/6/6.
 */

public class RecommendGuildFragment extends BaseUIFragment<UIFragmentHelper> {
    private OnlineGuildListInfo onlineGuildListInfo;
    private ListView mListView;
    private GuildAdapter mAdapter;
    private View guildView;
    private View mDivider;

    private GuildService guildService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        guildService = (GuildService) getSystemService(GuildService.SERVICE_NAME);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_guild_recom_guild, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.lvListView);
        guildView = view.findViewById(R.id.rvGuild);
        mDivider = view.findViewById(R.id.vDivider);
        mAdapter = new GuildAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openGuildFragment(mAdapter.getItem(position));
            }
        });
        updateSelfGuild(view);
        loadDefaultData(PAGE_FIRST);
    }

    private void openGuildFragment(OnlineGuildInfo guildInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putString("guildID", guildInfo.guildID);
        showFragment(GuildInfoFragment.newFragment(getActivity(), GuildInfoFragment.class, mBundle));
    }

    private void updateSelfGuild(View view) {
        if (guildService.getGuildInfo() == null) {
            guildView.setVisibility(View.GONE);
            mDivider.setVisibility(View.GONE);
        } else {
            guildView.setVisibility(View.VISIBLE);
            mDivider.setVisibility(View.VISIBLE);
            ImageView headView = (ImageView) view.findViewById(R.id.ivHead);
            TextView mName = (TextView) view.findViewById(R.id.tvName);
            ImageView mLevel = (ImageView) view.findViewById(R.id.tvLevel);
            TextView mCount = (TextView) view.findViewById(R.id.tvMemberNumber);
            final OnlineGuildInfo guildInfo = guildService.getGuildInfo();
            ImageFetcher.getImageFetcher().loadImage(guildInfo.mHeadPhoto, headView, R.drawable.icon_guild_default, new RoundDisplayer());
            mName.setText(guildInfo.guildName);
            mLevel.setImageResource(WelewolfUtils.getGuildLevel(guildInfo.level));
            mCount.setText(guildInfo.curCount + "/" + guildInfo.maxCount);
            guildView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGuildFragment(guildInfo);
                }
            });
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getGuildListUrl(1);
        OnlineGuildListInfo result = new DataAcquirer<OnlineGuildListInfo>().acquire(url, new OnlineGuildListInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        OnlineGuildListInfo guildListInfo = (OnlineGuildListInfo) result;
        mAdapter.setItems(guildListInfo.mGuilds);
    }
}
