package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineRoleInfo;
import com.buang.welewolf.modules.utils.WolfUtils;

/**
 * Created by weilei on 17/6/22.
 */

public class MyRoleDialog extends BaseGameDialog {
    private ImageView mRoleType;
    private TextView mRoleName;
    private TextView mRoleDesc;
    private TextView mRoleSkill;
    private Button mConfirm;

    public MyRoleDialog(@NonNull Context context, OnlineRoleInfo roleInfo, int time) {
        super(context);
        setMaxTime(time);
        setRoleInfo(roleInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_my_role);
        mRoleType = (ImageView) findViewById(R.id.ivHead);
        mRoleName = (TextView) findViewById(R.id.tvText);
        mRoleDesc = (TextView) findViewById(R.id.tvGoal);
        mRoleSkill = (TextView) findViewById(R.id.tvSkill);
        mConfirm = (Button) findViewById(R.id.bvConfirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        int role = getRoleInfo().roleType;
        mRoleType.setImageResource(WolfUtils.getRoleId(role));

        String roleName = "你的身份 : " + WolfUtils.getRoleName(role);
        SpannableStringBuilder nameSp = new SpannableStringBuilder(roleName);
        ForegroundColorSpan nameFC = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_504b8d));
        nameSp.setSpan(nameFC, roleName.indexOf(WolfUtils.getRoleName(role)), roleName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRoleName.setText(nameSp);

        String roleDesc = "目标 : " + WolfUtils.getRoleDesc(role);
        SpannableStringBuilder descSp = new SpannableStringBuilder(roleDesc);
        ForegroundColorSpan descFc = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_ffa030));
        descSp.setSpan(descFc, 0, "目标 : ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRoleDesc.setText(descSp);

        String roleSkill = "技能 : " + WolfUtils.getRoleSkill(role);
        SpannableStringBuilder skillSp = new SpannableStringBuilder(roleSkill);
        ForegroundColorSpan skillFc = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_ffa030));
        skillSp.setSpan(skillFc, 0, "技能 : ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRoleSkill.setText(skillSp);
    }

    @Override
    public void updateTime() {
        mConfirm.setText("确定(" + getMaxTime() + ")");
    }

    @Override
    public void onTimeEnd() {
        dismiss();
    }
}
