package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.buang.welewolf.R;

/**
 * Created by weilei on 17/6/22.
 */

public class SheriffDialog extends BaseGameDialog {

    private Button mConfirm;
    private Button mCancel;

    public SheriffDialog(@NonNull Context context, int time) {
        super(context);
        setMaxTime(time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_witch_skill);
        mConfirm = (Button) findViewById(R.id.bvConfirm);
        mCancel = (Button) findViewById(R.id.bvCancel);

        mCancel.setOnClickListener(onClickListener);
        mConfirm.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.bvConfirm:
                    break;
                case R.id.bvCancel:
                    break;
            }
        }
    };
}
