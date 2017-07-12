package com.buang.welewolf.welewolf.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoomInfo;
import com.buang.welewolf.base.bean.OnlineUserInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.game.GameHelpFrament;
import com.buang.welewolf.modules.game.GameRoomFragment;
import com.buang.welewolf.modules.game.dialog.RoomSettingDialog;
import com.buang.welewolf.modules.guild.ContactInfoFragment;
import com.buang.welewolf.modules.profile.SystemSettingFragment;
import com.buang.welewolf.modules.services.GuildService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.welewolf.login.LoginFragment;
import com.buang.welewolf.welewolf.login.PhoneLoginFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.GameFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;

/**
 * Created by weilei on 17/4/24.
 */
public class MainGameFragment extends BaseUIFragment<UIFragmentHelper> {

    private final int ACTION_GET_USERINFO = 1;
    private final int ACTION_FIND_ROOM = 2;
    private final int ACTION_CREATE_ROOM = 3;

    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_main_game, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        view.findViewById(R.id.ivUserView).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivSetting).setOnClickListener(onClickListener);
        view.findViewById(R.id.main_game_easy).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivHelp).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivCreateRoom).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivSearchRoom).setOnClickListener(onClickListener);
        loadData(ACTION_GET_USERINFO, PAGE_MORE, Utils.getLoginUserItem().userId);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivUserView:
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(ConstantsUtils.KEY_BUNDLE_CONTACT_INFO, Utils.getLoginUserItem());
                    ContactInfoFragment fragment = ContactInfoFragment.newFragment(getActivity(), ContactInfoFragment.class, mBundle);
                    showFragment(fragment);
                    break;
                case R.id.ivSetting:
                    showFragment(SystemSettingFragment.newFragment(getActivity(), SystemSettingFragment.class, null));
                    break;
                case R.id.main_game_easy:
                    loadData(ACTION_FIND_ROOM, PAGE_MORE, "10000");
                    break;
                case R.id.ivCreateRoom:
                    loadData(ACTION_CREATE_ROOM, PAGE_MORE, 3, 15, "6666");
                    break;
                case R.id.ivSearchRoom:
                    showSearchDialog();
//                    loadData(ACTION_FIND_ROOM, PAGE_MORE, "10000");
                    break;
                case R.id.ivHelp:
                    showFragment(GameHelpFrament.newFragment(getActivity(), GameHelpFrament.class, null, AnimType.ANIM_NONE));
                    break;
            }
        }
    };

    private void openGameRoom(OnlineRoomInfo roomInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("room", roomInfo);
        showFragment(GameRoomFragment.newFragment(getActivity(), GameRoomFragment.class, mBundle));
    }

    private void showSearchDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.show();
        }
        mDialog = new RoomSettingDialog(getActivity());
        mDialog.show();
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_GET_USERINFO) {
            String url = OnlineServices.getUserInfoUrl((String) params[0]);
            OnlineUserInfo result = new DataAcquirer<OnlineUserInfo>().acquire(url, new OnlineUserInfo(), -1);
            return result;
        } else if (action == ACTION_FIND_ROOM) {
            String url = OnlineServices.getFindRoomUrl((String) params[0]);
            OnlineRoomInfo result = new DataAcquirer<OnlineRoomInfo>().get(url, new OnlineRoomInfo());
            return result;
        } else if (action == ACTION_CREATE_ROOM) {
            String url = OnlineServices.getCreateRoomUrl((int) params[0], (int) params[1], (String) params[2]);
            OnlineRoomInfo result = new DataAcquirer<OnlineRoomInfo>().get(url, new OnlineRoomInfo());
            return result;
        }
        return super.onProcess(action, pageNo, params);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GET_USERINFO) {
            OnlineUserInfo userInfo = (OnlineUserInfo) result;
            if (userInfo.mUserItem.guildIno != null) {
                GuildService guildService = (GuildService) getSystemService(GuildService.SERVICE_NAME);
                guildService.setGuildInfo(userInfo.mUserItem.guildIno);
            }
        } else if (action == ACTION_FIND_ROOM) {
            openGameRoom((OnlineRoomInfo) result);
        } else if (action == ACTION_CREATE_ROOM) {

        }
    }
}
