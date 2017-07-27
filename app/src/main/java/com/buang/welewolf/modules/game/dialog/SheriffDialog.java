package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.ConstantsUtils;

/**
 * Created by weilei on 17/6/22.
 */

public class SheriffDialog extends BaseGameDialog {

    private Button mConfirm;
    private Button mCancel;

    public SheriffDialog(@NonNull Context context, int time) {
        super(context);
        setMaxTime(time);
        setCanceled();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_sheriff);
        mConfirm = (Button) findViewById(R.id.bvConfirm);
        mCancel = (Button) findViewById(R.id.bvCancel);

        mCancel.setOnClickListener(onClickListener);
        mConfirm.setOnClickListener(onClickListener);
    }

    @Override
    public void updateTime() {
        super.updateTime();
        mConfirm.setText("参选(" + getMaxTime() + ")");
        mCancel.setText("放弃(" + getMaxTime() + ")");
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
                    if (mOnGameDialogListener != null) {
                        mOnGameDialogListener.onGameDialogClick(SheriffDialog.this, ConstantsUtils.DIALOG_PARAMS_SHERIFF);
                    }
                    dismiss();
                    break;
                case R.id.bvCancel:
                    dismiss();
                    break;
            }
        }
    };
}
