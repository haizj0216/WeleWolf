package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.modules.services.GameService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.utils.WolfUtils;
import com.buang.welewolf.widgets.roundedimageview.RoundedImageView;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.ImageFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/22.
 */

public class HunterKillDialog extends BaseGameDialog {

    private GameService gameService;
    private GridView mGridView;
    private RoleAdapter roleAdapter;
    private TextView mConfirm;

    private OnlineRoleInfo selectRole;

    public HunterKillDialog(@NonNull Context context, int time) {
        super(context);
        gameService = (GameService) context.getSystemService(GameService.SERVICE_NAME);
        setMaxTime(time);
        setCanceled();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_hunterkill);
        mGridView = (GridView) findViewById(R.id.gvGrid);
        roleAdapter = new RoleAdapter(getContext());
        mConfirm = (TextView) findViewById(R.id.tvConfirm);
        roleAdapter.setItems(getRoleList());
        mGridView.setAdapter(roleAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnlineRoleInfo roleInfo = roleAdapter.getItem(position);
                selectRole = roleInfo;
                roleAdapter.notifyDataSetChanged();
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnGameDialogListener != null && selectRole != null) {
                    mOnGameDialogListener.onGameDialogClick(HunterKillDialog.this, ConstantsUtils.DIALOG_PARAMS_HUNTERSKILL, selectRole.userID);
                }
                dismiss();
            }
        });
    }

    /**
     * 获取未死亡和非己的玩家
     * @return
     */
    private List<OnlineRoleInfo> getRoleList() {
        List<OnlineRoleInfo> roleInfos = new ArrayList<>();
        for (int i = 0; i < gameService.getOnlineRoomInfo().roleInfos.size(); i++) {
            OnlineRoleInfo roleInfo = gameService.getOnlineRoomInfo().roleInfos.get(i);
            if (roleInfo == gameService.getOnlineRoomInfo().myRole) {
                continue;
            }
            if (roleInfo.isDeath) {
                continue;
            }
            roleInfos.add(roleInfo);
        }
        return roleInfos;
    }

    @Override
    public void updateTime() {
        super.updateTime();
        mConfirm.setText("确定(" + getMaxTime() + ")");
    }

    @Override
    public void onTimeEnd() {
        super.onTimeEnd();
        dismiss();
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

            if (selectRole == roleInfo) {
                viewHolder.mSelect.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mSelect.setVisibility(View.GONE);
            }

            return convertView;
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
