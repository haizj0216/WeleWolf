package com.buang.welewolf.modules.game;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.base.bean.OnlineRoomInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.game.common.LiveKit;
import com.buang.welewolf.modules.game.dialog.BaseGameDialog;
import com.buang.welewolf.modules.game.dialog.HunterKillDialog;
import com.buang.welewolf.modules.game.dialog.MyRoleDialog;
import com.buang.welewolf.modules.game.dialog.RoomSettingDialog;
import com.buang.welewolf.modules.game.dialog.SheriffDialog;
import com.buang.welewolf.modules.game.dialog.UserInfoDialog;
import com.buang.welewolf.modules.game.dialog.WitchSkillDialog;
import com.buang.welewolf.modules.game.dialog.WolfKillDialog;
import com.buang.welewolf.modules.game.widget.InputPanel;
import com.buang.welewolf.modules.services.GameService;
import com.buang.welewolf.modules.services.OnGVoiceListener;
import com.buang.welewolf.modules.services.OnRongIMMessageListener;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UiThreadHandler;
import com.tencent.gcloud.voice.GCloudVoiceErrno;

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
    private final int ACTION_REPORT = 7;
    private final int ACTION_ADDFRIEND = 8;
    private final int ACTION_SKILL = 9;

    private final int MSG_GET_MIC = 1;

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
    private View mRecordingContainer;
    private Drawable[] mMicImages;
    private ImageView mMicImage;
    private TextView mRecordingHint;
    private int orderNumber;

    private Dialog mDialog;

    private RongIMService rongIMService;
    private GameService gameService;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_GET_MIC) {
                int mic = gameService.getMinLevel();
                LogUtil.d("GameRoomFragment", "min:" + mic);
                sendEmptyMessageDelayed(MSG_GET_MIC, 1000);
            }
        }
    };

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        rongIMService = (RongIMService) getSystemService(RongIMService.SERVICE_NAME);
        rongIMService.getObserver().addOnRongIMMessageListener(rongIMMessageListener);
        onlineRoomInfo = (OnlineRoomInfo) getArguments().getSerializable("room");
        gameService = (GameService) getSystemService(GameService.SERVICE_NAME);
        gameService.getObserver().addOnGVoiceListener(mOnGVoiceListener);
        gameService.initGVoice();

        mMicImages = new Drawable[]{
                getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10),
                getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12),
                getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14),
                getResources().getDrawable(R.drawable.record_animate_15),
                getResources().getDrawable(R.drawable.record_animate_16),
                getResources().getDrawable(R.drawable.record_animate_17),
                getResources().getDrawable(R.drawable.record_animate_18),
                getResources().getDrawable(R.drawable.record_animate_19),
                getResources().getDrawable(R.drawable.record_animate_20)
        };
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
        mRecordingContainer = view.findViewById(R.id.recording_container);
        mMicImage = (ImageView) view.findViewById(R.id.mic_image);
        mRecordingHint = (TextView) view.findViewById(R.id.recording_hint);
        mLeftAdapter = new GameRoleAdapter(getActivity(), onlineRoomInfo, 0);
        mRightAdapter = new GameRoleAdapter(getActivity(), onlineRoomInfo, 1);
        mChatAdapter = new ChatListAdapter();
        mLeftList.setAdapter(mLeftAdapter);
        mRightList.setAdapter(mRightAdapter);
        mChatList.setAdapter(mChatAdapter);
        mLeftList.setOnItemClickListener(onItemClickListener);
        mRightList.setOnItemClickListener(onItemClickListener);

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
                gameService.joinGVoiceRoom(roomId);
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

    OnGVoiceListener mOnGVoiceListener = new OnGVoiceListener() {
        @Override
        public void onJoinRoom(int i, String s, int i1) {
            ToastUtils.showShortToast(getActivity(), "success:" + s);
            gameService.openGVoiceMic();
        }

        @Override
        public void onQuitRoom(int i, String s) {

        }

        @Override
        public void onStatusUpdate(int i, String s, int i1) {

        }

        @Override
        public void onMemberVoice(int[] ints, int i) {
            LogUtil.d("onMemberVoice", "length" + i);
        }

        @Override
        public void onUserSpeak() {
            mRecordingContainer.setVisibility(View.VISIBLE);
            mRecordingHint
                    .setText(getString(R.string.move_up_to_cancel));
            mRecordingHint.setBackgroundColor(Color.TRANSPARENT);
            mHandler.sendEmptyMessage(MSG_GET_MIC);
        }

        @Override
        public void onUserSpeakEnd() {
            mRecordingContainer.setVisibility(View.INVISIBLE);
            mHandler.removeMessages(MSG_GET_MIC);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            OnlineRoleInfo roleInfo = ((GameRoleAdapter) parent.getAdapter()).getItem(position);
            switch (position) {
                case 0:
                    showContactInfoDialog(roleInfo);
                    break;
                case 1:
                    showMyRoleDialog(roleInfo);
                    break;
                case 2:
                    showWolfKillDialog();
                    break;
                case 3:
                    showHunterSkillDialog();
                    break;
                case 4:
                    showWitchSkillDialog(roleInfo);
                    break;
                case 5:
                    showSheriffDialog();
                    break;
            }

        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivSetting:
                    showSettingDialog();
                    break;
                case R.id.ivBack:
                    finish();
                    break;
                case R.id.ivZhao://召集令
                    break;
                case R.id.ivHelp:
                    showFragment(GameHelpFrament.newFragment(getActivity(), GameHelpFrament.class, null));
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
                    onMessageReceived(message);
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

    BaseGameDialog.OnGameDialogListener gameDialogListener = new BaseGameDialog.OnGameDialogListener() {
        @Override
        public void onGameDialogClick(Dialog dialog, Object... params) {
            String method = (String) params[0];
            if (ConstantsUtils.DIALOG_PARAMS_REPORT.equals(method)) {
                String userId = (String) params[1];
                loadData(ACTION_REPORT, PAGE_MORE, userId);
            } else if (ConstantsUtils.DIALOG_PARAMS_ADDFRIEND.equals(method)) {
                String userId = (String) params[1];
                loadData(ACTION_ADDFRIEND, PAGE_MORE, userId);
            } else if (ConstantsUtils.DIALOG_PARAMS_WOLFSKILL.equals(method)) {

            } else if (ConstantsUtils.DIALOG_PARAMS_HUNTERSKILL.equals(method)) {

            } else if (ConstantsUtils.DIALOG_PARAMS_SHERIFF.equals(method)) {

            } else if (ConstantsUtils.DIALOG_PARAMS_WITCHSKILL.equals(method)) {

            }
            dialog.dismiss();
        }
    };

    private void showSettingDialog() {
        RoomSettingDialog mDialog = new RoomSettingDialog(getActivity(), new RoomSettingDialog.OnRoomSettingListener() {
            @Override
            public void onRoomSetting(int level, String psw, boolean isWatch) {

            }
        });
        mDialog.show();
    }

    private void showSheriffDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new SheriffDialog(getActivity(), 20);
        ((SheriffDialog) mDialog).setOnGameDialogListener(gameDialogListener);
        mDialog.show();
    }

    /**
     * 女巫使用技能
     * @param roleInfo
     */
    private void showWitchSkillDialog(OnlineRoleInfo roleInfo) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new WitchSkillDialog(getActivity(), roleInfo, 20);
        ((WitchSkillDialog) mDialog).setOnGameDialogListener(gameDialogListener);
        mDialog.show();
    }

    /**
     * 猎人技能
     */
    private void showHunterSkillDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new HunterKillDialog(getActivity(), 20);
        ((HunterKillDialog) mDialog).setOnGameDialogListener(gameDialogListener);
        mDialog.show();
    }

    /**
     * 狼人技能
     */
    private void showWolfKillDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new WolfKillDialog(getActivity(), 20);
        ((WolfKillDialog) mDialog).setOnGameDialogListener(gameDialogListener);
        mDialog.show();
    }

    /**
     * 显示个人角色对话框
     *
     * @param roleInfo
     */
    private void showMyRoleDialog(OnlineRoleInfo roleInfo) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new MyRoleDialog(getActivity(), roleInfo, 10);
        mDialog.show();
    }

    /**
     * 显示其他用户信息
     *
     * @param roleInfo
     */
    private void showContactInfoDialog(OnlineRoleInfo roleInfo) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = new UserInfoDialog(getActivity(), roleInfo, this);
        ((UserInfoDialog) mDialog).setOnGameDialogListener(gameDialogListener);
        mDialog.show();
    }

    private void onMessageReceived(Message message) {
        MessageContent content = message.getContent();
        if (content != null && content instanceof TextMessage) {
            String extra = ((TextMessage) content).getExtra();
            LogUtil.d("received:", extra);
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
        } else if (action == ACTION_ADDFRIEND) {
            String url = OnlineServices.getAddFriendUrl((String) params[0]);
            BaseObject result = new DataAcquirer<>().acquire(url, new BaseObject(), -1);
            return result;
        } else if (action == ACTION_REPORT) {
            String url = OnlineServices.getReportFriendUrl((String) params[0]);
            BaseObject result = new DataAcquirer<>().acquire(url, new BaseObject(), -1);
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
        } else if (action == ACTION_ADDFRIEND) {
            ToastUtils.showShortToast(getActivity(), "添加成功");
        } else if (action == ACTION_REPORT) {
            ToastUtils.showShortToast(getActivity(), "举报成功");
        }
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (rongIMService != null) {
            rongIMService.getObserver().removeOnRongIMMessageListener(rongIMMessageListener);
            rongIMService = null;
        }
        if (gameService != null) {
            gameService.getObserver().removeOnGVoiceListener(mOnGVoiceListener);
        }
    }

}
