package com.buang.welewolf.welewolf.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineUserInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.guild.ContactInfoFragment;
import com.buang.welewolf.modules.guild.FindGuildFragment;
import com.buang.welewolf.modules.guild.GuildInfoFragment;
import com.buang.welewolf.modules.message.ContactsFragment;
import com.buang.welewolf.modules.services.GuildService;
import com.buang.welewolf.modules.services.OnRongIMConnectListener;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by weilei on 17/4/24.
 */
public class MainMessageFragment extends BaseUIFragment<UIFragmentHelper> {

    private final int ACTION_GET_CONVERSATION = 1;
    private final int ACTION_FIND_USER = 2;

    private ConversationListFragment mConversationListFragment;
    private RongIMService rongIMService;

    private Dialog mDialog;

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

        view.findViewById(R.id.ivGuild).setOnClickListener(onClickListener);
        view.findViewById(R.id.main_message_contacts).setOnClickListener(onClickListener);
        view.findViewById(R.id.main_message_search).setOnClickListener(onClickListener);
    }

    private void initFragment() {
        if (mConversationListFragment == null) {
            mConversationListFragment = new ConversationListFragment();
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.ivConversationList, mConversationListFragment);
            transaction.commit();
//            mConversationListFragment = (ConversationListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.ivConversationList);
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话非聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                    .build();

            mConversationListFragment.setUri(uri);
        }
    }

    private void showSearchDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getFillBlackDialog(getActivity(), "请输入用户ID", "确定", "取消", "",
                InputType.TYPE_CLASS_NUMBER, new DialogUtils.OnFillDialogBtnClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, boolean isConfirm, String resutl) {
                        if (isConfirm) {
                            loadData(ACTION_FIND_USER, PAGE_MORE, resutl);
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
                case R.id.ivGuild:
                    if (Utils.getLoginUserItem().guildIno != null) {
                        Bundle mBundle = new Bundle();
                        GuildService guildService = (GuildService) getSystemService(GuildService.SERVICE_NAME);
                        mBundle.putString("guildID", guildService.getGuildInfo().guildID);
                        showFragment(GuildInfoFragment.newFragment(getActivity(), GuildInfoFragment.class, mBundle));
                    } else {
                        showFragment(FindGuildFragment.newFragment(getActivity(), FindGuildFragment.class, null));
                    }
                    break;
                case R.id.main_message_contacts:
                    showFragment(ContactsFragment.newFragment(getActivity(), ContactsFragment.class, null));
                    break;
                case R.id.main_message_search:
                    showSearchDialog();
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

        } else if (action == ACTION_FIND_USER) {
            String url = OnlineServices.getFindFriendUrl((String) params[0]);
            OnlineUserInfo result = new DataAcquirer<OnlineUserInfo>().acquire(url, new OnlineUserInfo(), -1);
            return result;
        }
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_FIND_USER) {
            OnlineUserInfo onlineUserInfo = (OnlineUserInfo) result;
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantsUtils.KEY_BUNDLE_CONTACT_INFO, onlineUserInfo.mUserItem);
            showFragment(ContactInfoFragment.newFragment(getActivity(), ContactInfoFragment.class, bundle));
        }
    }


    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        rongIMService.getObserver().removeOnRongIMConnectListener(onRongIMConnectListener);
    }
}
