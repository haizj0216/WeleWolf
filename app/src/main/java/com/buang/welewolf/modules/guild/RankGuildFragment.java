package com.buang.welewolf.modules.guild;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.buang.welewolf.base.bean.OnlineGuildListInfo;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

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
        initData();
    }

    private void initData() {
        onlineGuildListInfo = new OnlineGuildListInfo();
        onlineGuildListInfo.mGuilds = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            onlineGuildListInfo.mGuilds.add(new OnlineGuildInfo());
        }
        mAdapter.setItems(onlineGuildListInfo.mGuilds);
    }
}
