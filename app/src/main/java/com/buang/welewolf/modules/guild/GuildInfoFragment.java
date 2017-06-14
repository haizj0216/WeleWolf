package com.buang.welewolf.modules.guild;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.buang.welewolf.base.bean.OnlineMemberListInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.services.GuildService;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.utils.DateUtil;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.WelewolfUtils;
import com.buang.welewolf.widgets.CustomScrollView;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UIUtils;

import java.util.ArrayList;

import io.rong.imlib.model.Conversation;

/**
 * Created by weilei on 17/5/27.
 */

public class GuildInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_GET_INFO = 1;
    private static final int ACTION_JOIN_GUILD = 2;

    private LinearLayout mMemberPhoto;
    private CustomScrollView mScrollView;
    private View mTopView;
    private View mBackView;
    private View mSettingView;
    private View mTopSetting;
    private View mChat;
    private TextView mChatView;
    private TextView mNameView;
    private ImageView mLevelView;
    private TextView mIDView;
    private ImageView mHeadView;
    private ImageView mHeadBg;
    private TextView mSignView;
    private TextView mCreateView;

    private String guildID;
    private boolean isMySelf;
    private GuildService guildService;
    private OnlineGuildInfo onlineGuildInfo;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        setTitleStyle(STYLE_NO_TITLE);
        guildID = getArguments().getString("guildID");
        guildService = (GuildService) getSystemService(GuildService.SERVICE_NAME);
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
        mChat = view.findViewById(R.id.rvChat);
        mChatView = (TextView) view.findViewById(R.id.tvChat);

        view.findViewById(R.id.tvSetting).setOnClickListener(onClickListener);
        view.findViewById(R.id.title_bar_back).setOnClickListener(onClickListener);
        view.findViewById(R.id.rvMembers).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivTopBack).setOnClickListener(onClickListener);
        view.findViewById(R.id.tvTopSetting).setOnClickListener(onClickListener);

        mNameView = (TextView) view.findViewById(R.id.tvName);
        mLevelView = (ImageView) view.findViewById(R.id.ivLevel);
        mIDView = (TextView) view.findViewById(R.id.tvGuildID);
        mSignView = (TextView) view.findViewById(R.id.tvSign);
        mHeadView = (ImageView) view.findViewById(R.id.ivHead);
        mHeadBg = (ImageView) view.findViewById(R.id.ivHeadBg);
        mCreateView = (TextView) view.findViewById(R.id.tvCreateTime);

        mBackView = view.findViewById(R.id.title_bar_back);
        mSettingView = view.findViewById(R.id.tvSetting);
        mTopSetting = view.findViewById(R.id.tvTopSetting);
        mMemberPhoto = (LinearLayout) view.findViewById(R.id.ivMembers);

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
                    if (mTopSetting.getVisibility() == View.VISIBLE) {
                        mSettingView.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        loadData(ACTION_GET_INFO, PAGE_FIRST, guildID);
    }

    private void updateGuildInfo() {
        mNameView.setText(onlineGuildInfo.guildName);
        ImageFetcher.getImageFetcher().loadImage(onlineGuildInfo.mHeadPhoto, mHeadView, R.drawable.bt_message_default_head, new RoundDisplayer(), new ImageFetcher.ImageFetcherListener() {
            @Override
            public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
                if (bitmap != null) {
                    mHeadBg.setImageBitmap(bitmap);
                }
            }
        });
        mLevelView.setImageResource(WelewolfUtils.getGuildLevel(onlineGuildInfo.level));
        mSignView.setText(onlineGuildInfo.sign);
        mIDView.setText(onlineGuildInfo.guildID);
        mCreateView.setText(DateUtil.getDayString(onlineGuildInfo.createTime));
        updateGuildChat();
        addBuildMember();
    }

    private void updateGuildChat() {
        if (guildService.getGuildInfo() == null) {
            mChatView.setText("加入公会");
            mChat.setVisibility(View.VISIBLE);
            mTopSetting.setVisibility(View.GONE);
            mSettingView.setVisibility(View.GONE);
        } else {
            OnlineGuildInfo guildInfo = guildService.getGuildInfo();
            if (guildInfo.guildID.equals(guildID)) { // 同一公会
                mChatView.setText("发起聊天");
                mChat.setVisibility(View.VISIBLE);
                mTopSetting.setVisibility(View.VISIBLE);
                mSettingView.setVisibility(View.VISIBLE);
            } else {
                mChat.setVisibility(View.GONE);
                mTopSetting.setVisibility(View.GONE);
                mSettingView.setVisibility(View.GONE);
            }
        }
    }

    private void doChat() {
        if (guildService.getGuildInfo() == null) {
            loadData(ACTION_JOIN_GUILD, PAGE_MORE, guildID);
        } else {
            OnlineGuildInfo guildInfo = guildService.getGuildInfo();
            if (guildInfo.guildID.equals(guildID)) { // 同一公会
                RongIMService rongIMService = (RongIMService) getSystemService(RongIMService.SERVICE_NAME);
                rongIMService.startConversation(getActivity(), Conversation.ConversationType.GROUP, guildID, onlineGuildInfo.guildName);
            }
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tvSetting:
                case R.id.tvTopSetting:
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("guild", onlineGuildInfo);
                    showFragment(GuildSettingFragment.newFragment(getActivity(), GuildSettingFragment.class, mBundle));
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
                case R.id.rvChat:
                    doChat();
                    break;
            }
        }
    };


    private void addBuildMember() {
        mMemberPhoto.removeAllViews();
        if (onlineGuildInfo.members != null) {
            for (int i = 0; i < onlineGuildInfo.members.size(); i++) {
                View view = View.inflate(getActivity(), R.layout.layout_welewolf_guild_member_item, null);
                ImageView headView = (ImageView) view.findViewById(R.id.ivHead);
                TextView nameView = (TextView) view.findViewById(R.id.tvName);
                ImageFetcher.getImageFetcher().loadImage(onlineGuildInfo.members.get(i).headPhoto,
                        headView, R.drawable.bt_message_default_head, new RoundDisplayer());
                nameView.setText(onlineGuildInfo.members.get(i).userName);
                mMemberPhoto.addView(view);
            }
        }

    }

    private void openMemberListFragment() {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("guild", onlineGuildInfo);
        GuildMemberFragment fragment = GuildMemberFragment.newFragment(getActivity(), GuildMemberFragment.class, mBundle);
        showFragment(fragment);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_GET_INFO) {
            String url = OnlineServices.getGuildInfoUrl((String) params[0]);
            OnlineGuildInfo result = new DataAcquirer<OnlineGuildInfo>().acquire(url, new OnlineGuildInfo(), -1);
            return result;
        } else if (action == ACTION_JOIN_GUILD) {
            String url = OnlineServices.getJoinGuildUrl((String) params[0]);
            OnlineGuildInfo guildInfo = new DataAcquirer<OnlineGuildInfo>().acquire(url, new OnlineGuildInfo(), -1);
            return guildInfo;
        }
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GET_INFO) {
            onlineGuildInfo = (OnlineGuildInfo) result;
            updateGuildInfo();
        } else if (action == ACTION_JOIN_GUILD) {
            onlineGuildInfo = (OnlineGuildInfo) result;
            guildService.setGuildInfo(onlineGuildInfo);
            updateGuildChat();
        }
    }
}
