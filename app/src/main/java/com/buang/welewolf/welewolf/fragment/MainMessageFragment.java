package com.buang.welewolf.welewolf.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.guild.FindGuildFragment;
import com.buang.welewolf.modules.guild.GuildInfoFragment;
import com.buang.welewolf.modules.message.ContactsFragment;
import com.buang.welewolf.modules.services.OnRongIMConnectListener;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by weilei on 17/4/24.
 */
public class MainMessageFragment extends BaseUIFragment<UIFragmentHelper> {

    private final int ACTION_GET_CONVERSATION = 1;

    private ConversationListFragment mConversationListFragment;
    private RongIMService rongIMService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
        rongIMService = (RongIMService) getSystemService(RongIMService.SERVICE_NAME);
        rongIMService.getObserver().addOnRongIMConnectListener(onRongIMConnectListener);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_main_message, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        rongIMService.connect();
        if (mConversationListFragment == null) {
            mConversationListFragment = (ConversationListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.ivConversationList);
        }
        view.findViewById(R.id.ivGuild).setOnClickListener(onClickListener);
        view.findViewById(R.id.main_message_contacts).setOnClickListener(onClickListener);
        view.findViewById(R.id.main_message_search).setOnClickListener(onClickListener);
    }

    private void initFragment() {
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();

        mConversationListFragment.setUri(uri);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivGuild:
                    showFragment(GuildInfoFragment.newFragment(getActivity(), GuildInfoFragment.class, null));
                    break;
                case R.id.main_message_contacts:
                    showFragment(ContactsFragment.newFragment(getActivity(), ContactsFragment.class, null));
                    break;
                case R.id.main_message_search:
                    showFragment(FindGuildFragment.newFragment(getActivity(), FindGuildFragment.class, null));
                    break;
            }
        }
    };

    OnRongIMConnectListener onRongIMConnectListener = new OnRongIMConnectListener() {
        @Override
        public void onLoginSuccess(String s) {
            ToastUtils.showShortToast(getActivity(), "连接成功" + s);
            initFragment();
        }

        @Override
        public void onLoginError(RongIMClient.ErrorCode errorCode) {

        }

        @Override
        public void onLoginOut() {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onConnectStatus(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {

        }
    };


    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_GET_CONVERSATION) {

        }
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        rongIMService.getObserver().removeOnRongIMConnectListener(onRongIMConnectListener);
    }
}
