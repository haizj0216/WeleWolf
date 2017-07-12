package com.buang.welewolf.modules.game.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.buang.welewolf.R;

/**
 * Created by weilei on 17/6/22.
 */

public class RoomSettingDialog extends BaseGameDialog {

    private EditText mEditView;
    private TextView mLevel0;
    private TextView mLevel5;
    private TextView mLevel10;
    private TextView mLevel15;
    private TextView mLevel20;

    private LinearLayout mPswLayout;

    private ToggleButton mPsw;
    private ToggleButton mVisit;

    private int mLevel;

    public RoomSettingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welewolf_room_setting);
        mEditView = (EditText) findViewById(R.id.evEdit);
        mLevel0 = (TextView) findViewById(R.id.tvLevel0);
        mLevel5 = (TextView) findViewById(R.id.tvLevel5);
        mLevel10 = (TextView) findViewById(R.id.tvLevel10);
        mLevel15 = (TextView) findViewById(R.id.tvLevel15);
        mLevel20 = (TextView) findViewById(R.id.tvLevel20);

        findViewById(R.id.bvConfirm).setOnClickListener(onClickListener);
        mLevel0.setOnClickListener(onClickListener);
        mLevel10.setOnClickListener(onClickListener);
        mLevel15.setOnClickListener(onClickListener);
        mLevel20.setOnClickListener(onClickListener);
        mLevel5.setOnClickListener(onClickListener);

        mPswLayout = (LinearLayout) findViewById(R.id.lvPsw);
        mPsw = (ToggleButton) findViewById(R.id.tvPsw);
        mVisit = (ToggleButton) findViewById(R.id.tvVisit);


        mEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePsw(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPsw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updatePsw("");
                    mEditView.setText("");
                    mEditView.setEnabled(true);
                } else {
                    mEditView.setEnabled(false);
                    updatePsw(null);
                    mEditView.setText("");
                }
            }
        });
    }

    private void updatePsw(String psw) {
        int size = mPswLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            TextView textview = (TextView) mPswLayout.getChildAt(i);
            if (psw == null) {
                textview.setText("");
            } else {
                if (i < psw.length()) {
                    textview.setText(psw.charAt(i) + "");
                } else {
                    textview.setText("");
                }
            }
        }
    }

    private void updateLevelView(int level) {
        mLevel0.setSelected(false);
        mLevel5.setSelected(false);
        mLevel10.setSelected(false);
        mLevel15.setSelected(false);
        mLevel20.setSelected(false);
        mLevel = level;
        switch (level) {
            case 0:
                mLevel0.setSelected(true);
                break;
            case 5:
                mLevel5.setSelected(true);
                break;
            case 10:
                mLevel10.setSelected(true);
                break;
            case 15:
                mLevel15.setSelected(true);
                break;
            case 20:
                mLevel20.setSelected(true);
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.bvConfirm:
                    dismiss();
                    break;
                case R.id.tvLevel0:
                    updateLevelView(0);
                    break;
                case R.id.tvLevel5:
                    updateLevelView(5);
                    break;
                case R.id.tvLevel10:
                    updateLevelView(10);
                    break;
                case R.id.tvLevel15:
                    updateLevelView(15);
                    break;
                case R.id.tvLevel20:
                    updateLevelView(20);
                    break;
            }
        }
    };
}
