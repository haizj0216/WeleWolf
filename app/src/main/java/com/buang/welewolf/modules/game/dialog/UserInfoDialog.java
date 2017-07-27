package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.modules.guild.ContactInfoFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.Utils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;

/**
 * Created by weilei on 17/7/8.
 */

public class UserInfoDialog extends BaseGameDialog {

    private ImageView mHead;
    private TextView mName;
    private TextView mLevel;
    private ImageView mSex;
    private TextView mWin;
    private TextView mLost;
    private TextView mRate;
    private BaseUIFragment mFragment;

    public UserInfoDialog(@NonNull Context context, OnlineRoleInfo roleInfo, BaseUIFragment fragment) {
        super(context);
        setRoleInfo(roleInfo);
        mFragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_user_info);
        mSex = (ImageView) findViewById(R.id.ivSex);
        mHead = (ImageView) findViewById(R.id.ivImage);
        mName = (TextView) findViewById(R.id.tvName);
        mLevel = (TextView) findViewById(R.id.tvLevel);
        mWin = (TextView) findViewById(R.id.tvWin);
        mLost = (TextView) findViewById(R.id.tvLost);
        mRate = (TextView) findViewById(R.id.tvRate);

        findViewById(R.id.rl_userInfo).setOnClickListener(onClickListener);
        findViewById(R.id.ivReport).setOnClickListener(onClickListener);
        findViewById(R.id.tvFriend).setOnClickListener(onClickListener);
        updateInfo();
    }

    private void updateInfo() {
        mName.setText(getRoleInfo().userName);
        mLevel.setText("Lv." + getRoleInfo().level);
        if (getRoleInfo().recordInfo != null) {
            mWin.setText(getRoleInfo().recordInfo.win + "");
            mLost.setText(getRoleInfo().recordInfo.lost + "");
            mRate.setText(getRoleInfo().recordInfo.rate * 100 + "%");
        }
        ImageFetcher.getImageFetcher().loadImage(getRoleInfo().userPhoto, mHead, R.drawable.touxxiaophai, new RoundDisplayer());
        if (getRoleInfo().sex.equals("1")) {
            mSex.setImageResource(R.drawable.icon_windows_male);
        } else {
            mSex.setImageResource(R.drawable.icon_windows_female);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.rl_userInfo:
                    openContactInfo();
                    break;
                case R.id.ivReport:
                    if (mOnGameDialogListener != null) {
                        mOnGameDialogListener.onGameDialogClick(UserInfoDialog.this, ConstantsUtils.DIALOG_PARAMS_REPORT, getRoleInfo().userID);
                    }
                    break;
                case R.id.tvFriend:
                    if (mOnGameDialogListener != null) {
                        mOnGameDialogListener.onGameDialogClick(UserInfoDialog.this, ConstantsUtils.DIALOG_PARAMS_ADDFRIEND, getRoleInfo().userID);
                    }
                    break;
            }
        }
    };

    private void openContactInfo() {
        UserItem userItem = Utils.getUserItem(getRoleInfo());
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_BUNDLE_CONTACT_INFO, userItem);
        ContactInfoFragment fragment = ContactInfoFragment.newFragment(mFragment.getActivity(), ContactInfoFragment.class, mBundle);
        mFragment.showFragment(fragment);
        dismiss();
    }
}
