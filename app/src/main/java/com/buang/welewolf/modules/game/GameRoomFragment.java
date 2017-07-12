package com.buang.welewolf.modules.game;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.base.bean.OnlineRoomInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.game.common.LiveKit;
import com.buang.welewolf.modules.game.widget.InputPanel;
import com.buang.welewolf.modules.services.OnRongIMMessageListener;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UiThreadHandler;
import com.youme.voiceengine.YouMeCallBackInterface;
import com.youme.voiceengine.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.TextMessage;

/**
 * Created by weilei on 17/5/22.
 */

public class GameRoomFragment extends BaseUIFragment<UIFragmentHelper> {

    private final int ACTION_READY = 1;
    private final int ACTION_BEGIN = 2;
    private final int ACTION_SETTING = 3;
    private final int ACTION_QUIT = 4;
    private final int ACTION_LOCK = 5;
    private final int ACTION_SYNC = 6;

    private OnlineRoomInfo onlineRoomInfo;

    private ListView mLeftList;
    private ListView mRightList;
    private ListView mChatList;
    private GameRoleAdapter mLeftAdapter;
    private GameRoleAdapter mRightAdapter;
    private ChatListAdapter mChatAdapter;
    private BottomPanelFragment bottomPanel;
    private ImageView mCurRole;
    private ImageView mWatch;
    private ImageView mRoomBg;
    private ImageView mBackView;
    private ImageView mSettingView;
    private ImageView mHelpView;
    private ImageView mZhaoView;
    private Button mReady;
    private int orderNumber;

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
        mCurRole = (ImageView) view.findViewById(R.id.ivCurRole);
        mWatch = (ImageView) view.findViewById(R.id.ivWatch);
        mReady = (Button) view.findViewById(R.id.bvReady);
        mRoomBg = (ImageView) view.findViewById(R.id.ivRoomBg);
        mBackView = (ImageView) view.findViewById(R.id.ivBack);
        mSettingView = (ImageView) view.findViewById(R.id.ivSetting);
        mHelpView = (ImageView) view.findViewById(R.id.ivHelp);
        mZhaoView = (ImageView) view.findViewById(R.id.ivZhao);
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
        api.SetCallback(new YouMeCallBackInterface() {
            @Override
            public void onInitEvent(int i, int i1) {

            }

            @Override
            public void onCallEvent(int i, int i1) {

            }

            @Override
            public void OnCommonEventStatus(int i, String s, int i1) {

            }

            @Override
            public void OnMemberChangeMsg(String[] strings) {

            }
        });
        mWatch.setOnClickListener(clickListener);
        mReady.setOnClickListener(clickListener);
        mBackView.setOnClickListener(clickListener);
        mSettingView.setOnClickListener(clickListener);
        mHelpView.setOnClickListener(clickListener);
        mZhaoView.setOnClickListener(clickListener);
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

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivSetting:
                    break;
                case R.id.ivBack:
                    finish();
                    break;
                case R.id.ivZhao://召集令
                    break;
                case R.id.ivHelp:
                    break;
                case R.id.ivWatch:
                    break;
                case R.id.ivReady:
                    break;
            }
        }
    };

    private OnRongIMMessageListener rongIMMessageListener = new OnRongIMMessageListener() {
        @Override
        public void onReceivedMessage(final Message message, int i) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMessageReveived(message);
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

    private void onMessageReveived(Message message) {
        MessageContent content = message.getContent();
        if (content != null && content instanceof TextMessage) {
            LogUtil.d("received:", ((TextMessage) content).getExtra());
            String extra = ((TextMessage) content).getExtra();
            if (!TextUtils.isEmpty(extra)) {
                try {
                    handleUrlLoad(new JSONObject(extra));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        mChatAdapter.addMessage(message.getContent());
        mChatAdapter.notifyDataSetChanged();
    }

    private OnlineRoleInfo getPosition(String userID) {
        List<OnlineRoleInfo> roleInfos = onlineRoomInfo.roleInfos;
        for (int i = 0; i < roleInfos.size(); i++) {
            if (userID.equals(roleInfos.get(i).userID)) {
                return roleInfos.get(i);
            }
        }
        return null;
    }

    private void handleUrlLoad(JSONObject object) {
        String method = object.optString("action");
        JSONObject params = object.optJSONObject("params");
        int messageOrderNumber = object.optInt("messageOrderNumber");
        if (messageOrderNumber - orderNumber > 1) {
            //同步数据
        }
        orderNumber = messageOrderNumber;
        if ("getReady".equals(method)) { //准备，取消准备
            String userID = params.optString("userID");
            int isReady = params.optInt("isReady");
            int index = getPosition(userID).userIndex;
            if (index != -1) {
                OnlineRoleInfo onlineRoleInfo = onlineRoomInfo.roleInfos.get(index);
                onlineRoleInfo.isReady = isReady == 1;
                mLeftAdapter.notifyDataSetChanged();
                mRightAdapter.notifyDataSetChanged();
            }
        } else if ("buyRole".equals(method)) {
            String userID = object.optString("userID");
            int role = object.optInt("role");
            if (userID.equals(Utils.getLoginUserItem().userId)) { // 如果是自己

            } else {

            }
        } else if ("startGame".equals(method)) {
            JSONArray userList = object.optJSONArray("roleList");
            for (int i = 0; i < userList.length(); i++) {
                JSONObject user = userList.optJSONObject(i);
                String userId = user.optString("userID");
                if (userId.equals(Utils.getLoginUserItem().userId)) {
                    JSONArray roleList = user.optJSONArray("roleList");
                    ToastUtils.showShortToast(getActivity(), roleList.toString());
                    break;
                }
            }
        } else if ("receiveRole".equals(method)) {
            JSONArray roleList = object.optJSONArray("setRoleList");
            for (int i = 0; i < roleList.length(); i++) {
                JSONObject user = roleList.optJSONObject(i);
                String userId = user.optString("userID");
                getPosition(userId).roleType = user.optInt("role");
                if (userId.equals(Utils.getLoginUserItem().userId)) {
                    ToastUtils.showShortToast(getActivity(), "角色是" + user.optInt("role"));
                    break;
                }
            }
        } else if ("nightFall".equals(method)) {

        } else if ("wolfManUseSkill".equals(method)) {
            String fromID = object.optString("fromID");
            String toID = object.optString("toID");
            ToastUtils.showShortToast(getActivity(), "From:" + fromID + ",toID:" + toID);
        } else if ("wolfManUseSkill".equals(method)) {

        } else if ("nightDeathResult".equals(method)) {

        } else if ("vote".equals(method)) {

        } else if ("voteResult".equals(method)) {

        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_BEGIN) {
            String url = OnlineServices.getStartGameUrl((String) params[0]);
            BaseObject result = new DataAcquirer<>().get(url, new BaseObject());
            return result;
        } else if (action == ACTION_LOCK) {
            String url = OnlineServices.getLockSeatUrl((String) params[0], (int) params[1]);
            BaseObject result = new DataAcquirer<>().get(url, new BaseObject());
            return result;
        } else if (action == ACTION_QUIT) {
            String url = OnlineServices.getQuitRoomUrl((String) params[0]);
            BaseObject result = new DataAcquirer<>().get(url, new BaseObject());
            return result;
        } else if (action == ACTION_READY) {
            String url = OnlineServices.getGetReadyUrl((String) params[0], (int) params[1]);
            BaseObject result = new DataAcquirer<>().get(url, new BaseObject());
            return result;
        } else if (action == ACTION_SETTING) {
            String url = OnlineServices.getRoomSettingUrl((int) params[0], (int) params[1]);
            BaseObject result = new DataAcquirer<>().get(url, new BaseObject());
            return result;
        }
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_BEGIN) {
        } else if (action == ACTION_LOCK) {
        } else if (action == ACTION_QUIT) {
        } else if (action == ACTION_READY) {
        } else if (action == ACTION_SETTING) {
        }
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (rongIMService != null) {
            rongIMService.getObserver().removeOnRongIMMessageListener(rongIMMessageListener);
            rongIMService = null;
        }
    }

}
