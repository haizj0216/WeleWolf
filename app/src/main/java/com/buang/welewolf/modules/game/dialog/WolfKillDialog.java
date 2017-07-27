package com.buang.welewolf.modules.game.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
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
import com.buang.welewolf.modules.services.OnSkillUpdateListener;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.utils.WolfUtils;
import com.buang.welewolf.widgets.roundedimageview.RoundedImageView;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.UIUtils;

import java.util.List;

/**
 * Created by weilei on 17/6/21.
 */

public class WolfKillDialog extends BaseGameDialog {
    private GameService gameService;
    private GridView mGridView;
    private RoleAdapter roleAdapter;
    private TextView mTitle;
    private OnlineRoleInfo killRole;

    public WolfKillDialog(@NonNull Context context, int time) {
        super(context);
        gameService = (GameService) BaseApp.getAppContext().getSystemService(GameService.SERVICE_NAME);
        gameService.getObserver().addOnSkillUpdateListener(skillUpdateListener);
        setMaxTime(time);
        setCanceled();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_wolfkill);
        mGridView = (GridView) findViewById(R.id.gvGrid);
        roleAdapter = new RoleAdapter(getContext());
        mGridView.setAdapter(roleAdapter);
        roleAdapter.setItems(gameService.getOnlineRoomInfo().roleInfos);

        mTitle = (TextView) findViewById(R.id.tvTitle);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                gameService.getObserver().removeOnSkillUpdateListener(skillUpdateListener);
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnlineRoleInfo roleInfo = roleAdapter.getItem(position);
                if (roleInfo.roleType != WolfUtils.GAME_ROLE_WOLF) {
                    killRole(roleInfo);
                }
            }
        });
    }

    private void killRole(OnlineRoleInfo roleInfo) {
        killRole = roleInfo;
        roleAdapter.notifyDataSetChanged();
        if (mOnGameDialogListener != null) {
            mOnGameDialogListener.onGameDialogClick(WolfKillDialog.this, ConstantsUtils.DIALOG_PARAMS_WOLFSKILL, roleInfo.userID);
        }
    }

    private OnSkillUpdateListener skillUpdateListener = new OnSkillUpdateListener() {
        @Override
        public void onSkillUpdate(OnlineRoleInfo from, OnlineRoleInfo to) {
            roleAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void updateTime() {
        super.updateTime();
        mTitle.setText("选择要杀的玩家(" + getMaxTime() + ")");
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

            if (killRole == roleInfo) {
                viewHolder.mSelect.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mSelect.setVisibility(View.GONE);
            }

            List<OnlineRoleInfo> killRoles = gameService.killedByWolfList(roleInfo.userID);
            if (killRoles == null || killRoles.size() == 0) {
                viewHolder.mLayou.setVisibility(View.GONE);
            } else {
                updateKillWolf(viewHolder.mLayou, killRoles);
            }
            return convertView;
        }

        private void updateKillWolf(LinearLayout layout, List<OnlineRoleInfo> roleInfos) {
            layout.setVisibility(View.VISIBLE);
            layout.removeAllViews();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtils.dip2px(11), UIUtils.dip2px(11));
            params.leftMargin = params.rightMargin = UIUtils.dip2px(1);
            for (int i = 0; i < roleInfos.size(); i++) {
                TextView textView = new TextView(getContext());
                textView.setText("" + roleInfos.get(i).userIndex);
                textView.setTextSize(9);
                textView.setTextColor(getContext().getResources().getColor(R.color.white));
                textView.setGravity(Gravity.CENTER);
                layout.addView(textView, params);
            }
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
