package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.widgets.roundedimageview.RoundedImageView;

/**
 * Created by weilei on 17/6/22.
 */

public class WitchSkillDialog extends BaseGameDialog {

    private RoundedImageView mHead;
    private TextView mText;
    private Button mConfirm;
    private Button mCancel;
    private OnlineRoleInfo onlineRoleInfo;

    public WitchSkillDialog(@NonNull Context context, OnlineRoleInfo skillRole, int maxTime) {
        super(context);
        setMaxTime(maxTime);
        setCanceled();
        onlineRoleInfo = skillRole;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_witch_skill);
        mHead = (RoundedImageView) findViewById(R.id.ivHead);
        mText = (TextView) findViewById(R.id.tvText);
        mConfirm = (Button) findViewById(R.id.bvConfirm);
        mCancel = (Button) findViewById(R.id.bvCancel);

        mCancel.setOnClickListener(onClickListener);
        mConfirm.setOnClickListener(onClickListener);

        String text = "今晚被杀的是 " + (onlineRoleInfo.userIndex + 1) + "号玩家";
        SpannableStringBuilder sp = new SpannableStringBuilder(text);
        ForegroundColorSpan fc = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_504b8d));
        sp.setSpan(fc, "今晚被杀的是 ".length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mText.setText(sp);
    }

    @Override
    public void updateTime() {
        super.updateTime();
        int time = getMaxTime();
        mConfirm.setText("救(" + time + ")");
        mCancel.setText("不救(" + time + ")");
    }

    @Override
    public void onTimeEnd() {
        super.onTimeEnd();
        dismiss();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.bvConfirm:
                    onSkillUsed(true);
                    break;
                case R.id.bvCancel:
                    onSkillUsed(false);
                    break;
            }
        }
    };

    private void onSkillUsed(boolean isUsed) {
        if (mOnGameDialogListener != null) {
            mOnGameDialogListener.onGameDialogClick(WitchSkillDialog.this, ConstantsUtils.DIALOG_PARAMS_WITCHSKILL, onlineRoleInfo.userID, isUsed);
        }
        dismiss();
    }
}
