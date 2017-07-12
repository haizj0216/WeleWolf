package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.modules.services.GameService;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.utils.WolfUtils;
import com.buang.welewolf.widgets.roundedimageview.RoundedImageView;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.utils.ImageFetcher;

/**
 * Created by weilei on 17/6/22.
 */

public class HunterKillDialog extends BaseGameDialog {

    private GameService gameService;
    private GridView mGridView;
    private RoleAdapter roleAdapter;

    public HunterKillDialog(@NonNull Context context, int time) {
        super(context);
        setMaxTime(time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_wolfkill);
        mGridView = (GridView) findViewById(R.id.gvGrid);
        roleAdapter = new RoleAdapter(getContext());
    }

    class RoleAdapter extends SingleTypeAdapter<OnlineRoleInfo> {

        public RoleAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.layout_welewolf_dialog_wolfkill_role_item, null);
                viewHolder = new ViewHolder();

                viewHolder.mHead = (RoundedImageView) convertView.findViewById(R.id.ivHead);
                viewHolder.mSelect = (ImageView) convertView.findViewById(R.id.ivSelect);
                viewHolder.mWolf = (ImageView) convertView.findViewById(R.id.ivWolf);
                viewHolder.mIndex = (TextView) convertView.findViewById(R.id.tvNo);
                viewHolder.mLayou = (LinearLayout) convertView.findViewById(R.id.lvIndex);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            OnlineRoleInfo roleInfo = getItem(position);
            ImageFetcher.getImageFetcher().loadImage(roleInfo.userPhoto, viewHolder.mHead, R.drawable.touxxiaophai);
            viewHolder.mIndex.setText("" + roleInfo.userIndex);
            if (roleInfo.userID.equals(Utils.getLoginUserItem().userId)) {
                viewHolder.mIndex.setBackgroundResource(R.drawable.icon_dialog_role_no);
            } else {
                viewHolder.mIndex.setBackgroundResource(R.drawable.icon_dialog_role_no_my);
            }

            if (roleInfo.roleType == WolfUtils.GAME_ROLE_WOLF) {
                viewHolder.mWolf.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mWolf.setVisibility(View.GONE);
            }
            return null;
        }


    }

    class ViewHolder {
        public RoundedImageView mHead;
        public ImageView mSelect;
        public ImageView mWolf;
        public TextView mIndex;
        public LinearLayout mLayou;
    }
}
