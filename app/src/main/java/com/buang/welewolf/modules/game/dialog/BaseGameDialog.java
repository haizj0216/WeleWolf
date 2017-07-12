package com.buang.welewolf.modules.game.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.buang.welewolf.R;

/**
 * Created by weilei on 17/6/22.
 */

public class BaseGameDialog extends Dialog {

    private int maxTime;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (maxTime != 0) {
                updateTime();
                maxTime--;
                mHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                onTimeEnd();
            }

        }
    };

    public BaseGameDialog(@NonNull Context context) {
        super(context, R.style.IphoneDialog);
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mHandler.sendEmptyMessage(0);
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.removeMessages(0);
                mHandler = null;
            }
        });
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        setCanceledOnTouchOutside(false);
    }

    public void setMaxTime(int time) {
        maxTime = time;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void updateTime() {

    }

    public void onTimeEnd() {

    }

    public interface OnGameDialogListener {
        public void onGameDialogClick(Object... params);
    }

    public OnGameDialogListener mOnGameDialogListener;

    public void setOnGameDialogListener(OnGameDialogListener listener) {
        mOnGameDialogListener = listener;
    }

}
