package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.buang.welewolf.R;

/**
 * Created by weilei on 17/6/22.
 */

public class MyRoleDialog extends BaseGameDialog {


    public MyRoleDialog(@NonNull Context context, int time) {
        super(context);
        setMaxTime(time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_my_role);
    }
}
