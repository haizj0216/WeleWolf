package com.buang.welewolf.modules.guild;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineUserInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.utils.WolfUtils;
import com.buang.welewolf.widgets.CustomScrollView;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by weilei on 17/6/1.
 */

public class ContactInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_GET_INFO = 1;
    private static final int ACTION_DELETE = 2;
    private static final int ACTION_ADD = 3;
    private static final int ACTION_REPORT = 4;

    private UserItem mContactInfo;
    private OnlineUserInfo onlineUserInfo;
    private CustomScrollView mScrollView;
    private View mTopView;
    private View mBackView;
    private TextView mSettingView;
    private TextView mTopSetting;
    private TextView mTopName;
    private TextView mNameView;
    private TextView mLevelView;
    private TextView mIDView;
    private ImageView mHeadView;
    private ImageView mSexView;
    private ImageView mHeadBg;
    private TextView mSignView;

    private TextView mWinView;
    private TextView mTotalView;
    private TextView mLostView;
    private TextView mRateView;
    private TextView mPopView;
    private AccuracGridView gridView;
    private TextView mChatView;
    private TextView mFriendView;

    private View mGuildView;
    private ImageView mGuildHead;
    private ImageView mGuildLevel;
    private TextView mGuildName;
    private TextView mGuildJob;

    private Dialog mDialog;
    private ImageView mTopBg;

    private boolean isMySelf;
    private boolean isBlackList;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        setTitleStyle(STYLE_NO_TITLE);
        mContactInfo = (UserItem) getArguments().getSerializable(ConstantsUtils.KEY_BUNDLE_CONTACT_INFO);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_contact_info, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mSettingView = (TextView) view.findViewById(R.id.tvSetting);
        mTopSetting = (TextView) view.findViewById(R.id.tvTopSetting);
        mBackView = view.findViewById(R.id.title_bar_back);
        mTopSetting.setOnClickListener(onClickListener);
        mSettingView.setOnClickListener(onClickListener);
        view.findViewById(R.id.title_bar_back).setOnClickListener(onClickListener);
        view.findViewById(R.id.ivTopBack).setOnClickListener(onClickListener);
        mScrollView = (CustomScrollView) view.findViewById(R.id.svScrollview);
        mTopView = view.findViewById(R.id.rvTopLayou);
        gridView = (AccuracGridView) view.findViewById(R.id.giftGrid);

        mTopName = (TextView) view.findViewById(R.id.tvTopName);
        mNameView = (TextView) view.findViewById(R.id.tvName);
        mLevelView = (TextView) view.findViewById(R.id.tvLevel);
        mIDView = (TextView) view.findViewById(R.id.tvID);
        mSignView = (TextView) view.findViewById(R.id.tvSign);
        mLostView = (TextView) view.findViewById(R.id.tvLost);
        mWinView = (TextView) view.findViewById(R.id.tvWin);
        mTotalView = (TextView) view.findViewById(R.id.tvTotal);
        mRateView = (TextView) view.findViewById(R.id.tvRate);
        mPopView = (TextView) view.findViewById(R.id.tvWelefare);
        mChatView = (TextView) view.findViewById(R.id.tvChat);
        mFriendView = (TextView) view.findViewById(R.id.tvFriend);
        mHeadView = (ImageView) view.findViewById(R.id.ivHead);
        mSexView = (ImageView) view.findViewById(R.id.ivSex);
        mHeadBg = (ImageView) view.findViewById(R.id.ivHeadBg);
        mChatView.setOnClickListener(onClickListener);
        mFriendView.setOnClickListener(onClickListener);
        mTopBg = (ImageView) view.findViewById(R.id.ivTopBg);

        mGuildView = view.findViewById(R.id.rvGuild);
        mGuildHead = (ImageView) view.findViewById(R.id.ivGuildHead);
        mGuildName = (TextView) view.findViewById(R.id.tvGuildName);
        mGuildLevel = (ImageView) view.findViewById(R.id.ivGuildLevel);
        mGuildJob = (TextView) view.findViewById(R.id.tvGuildJob);

        ViewCompat.setAlpha(mTopBg, 0);
        mScrollView.setOnScrollListener(new CustomScrollView.OnCustomScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                int height = mTopView.getHeight();
                LogUtil.d("scroll:", " scrollY+" + scrollY);
                float alpha = (float) height / scrollY;
                mTopView.setVisibility(View.VISIBLE);
                ViewCompat.setAlpha(mTopBg, 1 - Math.abs(alpha));
                mBackView.setVisibility(View.GONE);
                mSettingView.setVisibility(View.GONE);
                if (scrollY > height) {
                    mTopName.setVisibility(View.VISIBLE);
                } else {
                    mTopName.setVisibility(View.GONE);
                }
            }
        });

        loadData(ACTION_GET_INFO, PAGE_FIRST, mContactInfo.userId);
        updateUser();
        if (isMySelf) {
            view.findViewById(R.id.rvChat).setVisibility(View.GONE);
        }
    }

    private void updateUser() {
        if (mContactInfo.userId.equals(Utils.getLoginUserItem().userId)) {
            isMySelf = true;
        } else {
            isMySelf = false;
        }
        int d = R.drawable.icon_menu_more;
        String text = "";
        if (isMySelf) {
            text = "编辑";
            d = R.drawable.icon_info_edit;
        }
        mSettingView.setText(text);
        mTopSetting.setText(text);
        Drawable drawable = getResources().getDrawable(d);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mSettingView.setCompoundDrawables(drawable, null, null, null);
        mTopSetting.setCompoundDrawables(drawable, null, null, null);

        RongIM.getInstance().getBlacklistStatus(mContactInfo.userId, new RongIMClient.ResultCallback<RongIMClient.BlacklistStatus>() {
            @Override
            public void onSuccess(RongIMClient.BlacklistStatus blacklistStatus) {
                if (blacklistStatus == RongIMClient.BlacklistStatus.IN_BLACK_LIST) {
                    isBlackList = true;
                } else {
                    isBlackList = false;
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.title_bar_back:
                case R.id.ivTopBack:
                    finish();
                    break;
                case R.id.tvSetting:
                case R.id.tvTopSetting:
                    if (isMySelf) {
                        ContactSettingFragment fragment = ContactSettingFragment.newFragment(
                                getActivity(), ContactSettingFragment.class, null);
                        showFragment(fragment);
                    } else {
                        showSettingDialog();
                    }
                    break;
                case R.id.tvChat:
                    RongIMService service = (RongIMService) getSystemService(RongIMService.SERVICE_NAME);
                    service.startConversation(getActivity(), Conversation.ConversationType.PRIVATE,
                            onlineUserInfo.mUserItem.userId, onlineUserInfo.mUserItem.userName);
                    break;
                case R.id.tvFriend:
                    loadData(ACTION_ADD, PAGE_MORE, onlineUserInfo.mUserItem.userId);
                    break;
            }
        }
    };

    /**
     * 设置
     */
    private void showSettingDialog() {
        final List<MenuItem> items = new ArrayList<MenuItem>();
        items.add(new MenuItem(1, "举报", ""));
        if (onlineUserInfo.mUserItem.isFriend) {
            items.add(new MenuItem(2, "解除好友关系", ""));
        } else {
            items.add(new MenuItem(2, "加为好友", ""));
        }
        if (isBlackList) {
            items.add(new MenuItem(3, "移除黑名单", ""));
        } else {
            items.add(new MenuItem(3, "加入黑名单", ""));
        }

        mDialog = DialogUtils.getListDialog(getActivity(), "", items,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        switch (position) {
                            case 0://举报
                                loadData(ACTION_REPORT, PAGE_MORE, onlineUserInfo.mUserItem.userId);
                                break;
                            case 1:
                                if (onlineUserInfo.mUserItem.isFriend) {
                                    loadData(ACTION_DELETE, PAGE_MORE, onlineUserInfo.mUserItem.userId);
                                } else {
                                    loadData(ACTION_ADD, PAGE_MORE, onlineUserInfo.mUserItem.userId);
                                }
                                break;
                            case 2:
                                doBlackList();
                                break;
                        }

                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }
                });
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void doBlackList() {
        if (isBlackList) {
            RongIM.getInstance().removeFromBlacklist(mContactInfo.userId, new RongIMClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    isBlackList = false;
                    ToastUtils.showShortToast(getActivity(), "移除黑名单");
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    ToastUtils.showShortToast(getActivity(), "移除黑名单失败");
                }
            });
        } else {
            RongIM.getInstance().addToBlacklist(mContactInfo.userId, new RongIMClient.OperationCallback() {
                @Override
                public void onSuccess() {
                    isBlackList = true;
                    ToastUtils.showShortToast(getActivity(), "移入黑名单");
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    ToastUtils.showShortToast(getActivity(), "移入黑名单失败");
                }
            });
        }
    }

    private void updateView() {
        UserItem userItem = onlineUserInfo.mUserItem;
        mNameView.setText(userItem.userName);
        mTopName.setText(userItem.userName);
        mLevelView.setText("Lv." + userItem.level);
        mIDView.setText(userItem.userId);
        ImageFetcher.getImageFetcher().loadImage(userItem.headPhoto, mHeadView, R.drawable.bt_message_default_head,
                new RoundDisplayer(), new ImageFetcher.ImageFetcherListener() {
                    @Override
                    public void onLoadComplete(String imageUrl, Bitmap bitmap, Object object) {
                        if (bitmap != null) {
                            mHeadBg.setImageBitmap(bitmap);
                        }
                    }
                });
        if (userItem.sex.equals("1")) {
            mSexView.setImageResource(R.drawable.icon_windows_male);
        } else {
            mSexView.setImageResource(R.drawable.icon_windows_female);
        }
        mTotalView.setText("狼人杀 " + userItem.recordInfo.total + "局");
        mLostView.setText("败 " + userItem.recordInfo.lost + "局");
        mWinView.setText("胜 " + userItem.recordInfo.win + "局");
        mRateView.setText("(胜率" + userItem.recordInfo.rate * 100 + "%）");
        mPopView.setText(userItem.popularity + "");
        mSignView.setText(userItem.sign + userItem.sign + userItem.sign + userItem.sign + userItem.sign + userItem.sign + userItem.sign + userItem.sign + userItem.sign + userItem.sign);
        if (userItem.mGifts != null && userItem.mGifts.size() > 0) {
            GiftAdapter adapter = new GiftAdapter(getActivity());
            adapter.setItems(userItem.mGifts);
            gridView.setAdapter(adapter);
        }
        if (userItem.isFriend) {
            mFriendView.setVisibility(View.GONE);
        } else {
            mFriendView.setVisibility(View.VISIBLE);
        }

        if (userItem.guildIno == null) {
            mGuildView.setVisibility(View.GONE);
        } else {
            mGuildView.setVisibility(View.VISIBLE);
            mGuildName.setText(userItem.guildIno.guildName);
            mGuildLevel.setImageResource(WolfUtils.getGuildLevel(userItem.guildIno.level));
            mGuildJob.setText(WolfUtils.getGuildJob(userItem.guildIno.job));
            ImageFetcher.getImageFetcher().loadImage(userItem.guildIno.mHeadPhoto, mGuildHead, R.drawable.bt_message_default_head, new RoundDisplayer());
        }
    }

    private void updateFriend() {
        if (onlineUserInfo.mUserItem.isFriend) {
            mChatView.setText("发起聊天");
            ToastUtils.showShortToast(getActivity(), "已加为好友");
        } else {
            mChatView.setText("加为好友");
            ToastUtils.showShortToast(getActivity(), "已解除好友关系");
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_GET_INFO) {
            String url = OnlineServices.getUserInfoUrl((String) params[0]);
            OnlineUserInfo result = new DataAcquirer<OnlineUserInfo>().acquire(url, new OnlineUserInfo(), -1);
            return result;
        } else if (action == ACTION_ADD) {
            String url = OnlineServices.getAddFriendUrl((String) params[0]);
            BaseObject result = new DataAcquirer<>().acquire(url, new BaseObject(), -1);
            return result;
        } else if (action == ACTION_DELETE) {
            String url = OnlineServices.getDeleteFriendUrl((String) params[0]);
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
        if (action == ACTION_GET_INFO) {
            onlineUserInfo = (OnlineUserInfo) result;
            updateView();
        } else if (action == ACTION_ADD) {
            onlineUserInfo.mUserItem.isFriend = true;
            updateFriend();
        } else if (action == ACTION_DELETE) {
            onlineUserInfo.mUserItem.isFriend = false;
            updateFriend();
        } else if (action == ACTION_REPORT) {
            ToastUtils.showShortToast(getActivity(), "举报成功");
        }
    }
}
