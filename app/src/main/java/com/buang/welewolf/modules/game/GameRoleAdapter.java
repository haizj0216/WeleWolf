package com.buang.welewolf.modules.game;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.base.bean.OnlineRoomInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.modules.utils.WolfUtils;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.utils.ImageFetcher;

/**
 * Created by weilei on 17/6/15.
 */

public class GameRoleAdapter extends SingleTypeAdapter<OnlineRoleInfo> {
    private int direction = 0;
    private OnlineRoomInfo onlineRoomInfo;

    public GameRoleAdapter(Context context, OnlineRoomInfo roomInfo, int dire) {
        super(context);
        direction = dire;
        onlineRoomInfo = roomInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (direction == 0) {
                convertView = View.inflate(mContext, R.layout.layout_welewolf_game_role_item_left, null);
            } else {
                convertView = View.inflate(mContext, R.layout.layout_welewolf_game_role_item_right, null);
            }

            viewHolder = new ViewHolder();
            viewHolder.mHead = (ImageView) convertView.findViewById(R.id.ivHead);
            viewHolder.mCover = (ImageView) convertView.findViewById(R.id.ivCover);
            viewHolder.mDead = (ImageView) convertView.findViewById(R.id.ivDead);
            viewHolder.mHandUp = (ImageView) convertView.findViewById(R.id.ivHandUp);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.mSpeaking = (ImageView) convertView.findViewById(R.id.ivSpeaking);
            viewHolder.mWolf = (ImageView) convertView.findViewById(R.id.ivWolf);
            viewHolder.mReady = (ImageView) convertView.findViewById(R.id.ivReady);
            viewHolder.mLove = (ImageView) convertView.findViewById(R.id.ivLove);
            viewHolder.mIndex = (TextView) convertView.findViewById(R.id.tvIndex);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OnlineRoleInfo roleInfo = getItem(position);
        viewHolder.mIndex.setText(roleInfo.userIndex + "");
        if (roleInfo.isEmpty) {
            if (direction == 0) {
                convertView.setBackgroundResource(R.drawable.bg_game_role_empty_left);
            } else {
                convertView.setBackgroundResource(R.drawable.bg_game_role_empty_right);
            }
            viewHolder.mHead.setVisibility(View.GONE);
            viewHolder.mCover.setVisibility(View.GONE);
            viewHolder.mDead.setVisibility(View.GONE);
            viewHolder.mHandUp.setVisibility(View.GONE);
            viewHolder.mName.setVisibility(View.GONE);
            viewHolder.mSpeaking.setVisibility(View.GONE);
            viewHolder.mWolf.setVisibility(View.GONE);
            viewHolder.mReady.setVisibility(View.GONE);
            viewHolder.mLove.setVisibility(View.GONE);
        } else {

            viewHolder.mCover.setVisibility(View.GONE);
            viewHolder.mDead.setVisibility(View.GONE);
            viewHolder.mHandUp.setVisibility(View.GONE);
            viewHolder.mSpeaking.setVisibility(View.GONE);
            viewHolder.mWolf.setVisibility(View.GONE);
            viewHolder.mReady.setVisibility(View.GONE);
            viewHolder.mLove.setVisibility(View.GONE);

            if (direction == 0) {
                convertView.setBackgroundResource(R.drawable.bg_game_role_left);
            } else {
                convertView.setBackgroundResource(R.drawable.bg_game_role_right);
            }
            ImageFetcher.getImageFetcher().loadImage(roleInfo.userPhoto, viewHolder.mHead, R.drawable.touxxiaophai);
            viewHolder.mName.setText(roleInfo.userName);

            if (roleInfo.isOwner) {
                viewHolder.mReady.setImageResource(R.drawable.icon_owner);
                viewHolder.mReady.setVisibility(View.VISIBLE);
            } else if (roleInfo.isReady) {
                viewHolder.mReady.setImageResource(R.drawable.room_ready);
                viewHolder.mReady.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mReady.setVisibility(View.VISIBLE);
            }
            if (roleInfo.roleType == WolfUtils.GAME_ROLE_WOLF && onlineRoomInfo.myRole.roleType == WolfUtils.GAME_ROLE_WOLF) {
                viewHolder.mWolf.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mWolf.setVisibility(View.GONE);
            }
            if (roleInfo.isLock) {
                viewHolder.mCover.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mCover.setVisibility(View.GONE);
            }
        }


        return convertView;
    }

    class ViewHolder {
        private ImageView mHead;
        private ImageView mDead;
        private ImageView mCover;
        private ImageView mHandUp;
        private TextView mName;
        private ImageView mSpeaking;
        private ImageView mWolf;
        private ImageView mReady;
        private ImageView mLove;
        private TextView mIndex;
    }
}
