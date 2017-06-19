package com.buang.welewolf.modules.game;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoomInfo;
import com.buang.welewolf.modules.game.common.LiveKit;
import com.buang.welewolf.modules.game.widget.InputPanel;
import com.buang.welewolf.modules.services.OnRongIMMessageListener;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UiThreadHandler;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

/**
 * Created by weilei on 17/5/22.
 */

public class GameRoomFragment extends BaseUIFragment<UIFragmentHelper> {

    private OnlineRoomInfo onlineRoomInfo;

    private ListView mLeftList;
    private ListView mRightList;
    private ListView mChatList;
    private GameRoleAdapter mLeftAdapter;
    private GameRoleAdapter mRightAdapter;
    private ChatListAdapter mChatAdapter;
    private BottomPanelFragment bottomPanel;

    private RongIMService rongIMService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        rongIMService = (RongIMService) getSystemService(RongIMService.SERVICE_NAME);
        rongIMService.getObserver().addOnRongIMMessageListener(rongIMMessageListener);
        onlineRoomInfo = (OnlineRoomInfo) getArguments().getSerializable("room");
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_game_room, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mLeftList = (ListView) view.findViewById(R.id.lvLeftList);
        mRightList = (ListView) view.findViewById(R.id.lvRightList);
        mChatList = (ListView) view.findViewById(R.id.lvChatView);
        mLeftAdapter = new GameRoleAdapter(getActivity(), onlineRoomInfo, 0);
        mRightAdapter = new GameRoleAdapter(getActivity(), onlineRoomInfo, 1);
        mChatAdapter = new ChatListAdapter();
        mLeftList.setAdapter(mLeftAdapter);
        mRightList.setAdapter(mRightAdapter);
        mChatList.setAdapter(mChatAdapter);

        bottomPanel = (BottomPanelFragment) view.findViewById(R.id.bottom_bar);
        bottomPanel.setInputPanelListener(new InputPanel.InputPanelListener() {
            @Override
            public void onSendClick(String text) {
                final TextMessage content = TextMessage.obtain(text);
                LiveKit.sendMessage(content);
            }
        });

        LiveKit.init(getActivity());
        updateUserList();
        joinChatRoom("101");
    }

    private void joinChatRoom(final String roomId) {
        LiveKit.setCurrentUser(new UserInfo(RongIM.getInstance().getCurrentUserId(), "韦磊", Uri.parse("http://7xs9j5.com1.z0.glb.clouddn.com/liveapp/zhenhuan.jpg")));
        LiveKit.joinChatRoom(roomId, 2, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final InformationNotificationMessage content = InformationNotificationMessage.obtain("来啦");
                        LiveKit.sendMessage(content);
                    }
                });

            }

            @Override
            public void onError(final RongIMClient.ErrorCode errorCode) {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "聊天室加入失败! errorCode = " + errorCode, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateUserList() {
        int max = onlineRoomInfo.roleInfos.size() / 2;
        mLeftAdapter.setItems(onlineRoomInfo.roleInfos.subList(0, max));
        mRightAdapter.setItems(onlineRoomInfo.roleInfos.subList(max, onlineRoomInfo.roleInfos.size()));
    }

    private OnRongIMMessageListener rongIMMessageListener = new OnRongIMMessageListener() {
        @Override
        public void onReceivedMessage(final Message message, int i) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mChatAdapter.addMessage(message.getContent());
                    mChatAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onMessageSend(final Message message) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mChatAdapter.addMessage(message.getContent());
                    mChatAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void onMessageSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {

        }
    };

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (rongIMService != null) {
            rongIMService.getObserver().removeOnRongIMMessageListener(rongIMMessageListener);
            rongIMService = null;
        }
    }

}
