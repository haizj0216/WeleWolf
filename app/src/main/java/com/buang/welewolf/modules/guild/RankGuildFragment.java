package com.buang.welewolf.modules.guild;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.buang.welewolf.base.bean.OnlineGuildListInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;

import java.util.ArrayList;

/**
 * Created by weilei on 17/6/6.
 */

public class RankGuildFragment extends BaseUIFragment<UIFragmentHelper> {

    private OnlineGuildListInfo onlineGuildListInfo;
    private ListView mListView;
    private GuildAdapter mAdapter;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_guild_recom_guild, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.lvListView);
        mAdapter = new GuildAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openGuildFragment(mAdapter.getItem(position));
            }
        });

        loadDefaultData(PAGE_FIRST);
    }

    private void openGuildFragment(OnlineGuildInfo guildInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putString("guildID", guildInfo.guildID);
        showFragment(GuildInfoFragment.newFragment(getActivity(), GuildInfoFragment.class, mBundle));
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getGuildListUrl(2);
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
