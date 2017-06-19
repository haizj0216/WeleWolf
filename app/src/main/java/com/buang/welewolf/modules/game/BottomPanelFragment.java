package com.buang.welewolf.modules.game;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.game.widget.InputPanel;


public class BottomPanelFragment extends LinearLayout {

    private static final String TAG = "BottomPanelFragment";

    private ImageView btnInput;
    private InputPanel inputPanel;
    private TextView mVoiceView;

    public BottomPanelFragment(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        btnInput = (ImageView) findViewById(R.id.btn_input);
        inputPanel = (InputPanel) findViewById(R.id.input_panel);
        mVoiceView = (TextView) findViewById(R.id.btn_voice);

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputPanel.getVisibility() == GONE) {
                    inputPanel.setVisibility(View.VISIBLE);
                    mVoiceView.setVisibility(View.GONE);
                } else {
                    inputPanel.setVisibility(View.GONE);
                    mVoiceView.setVisibility(View.VISIBLE);
                }

            }
        });
        mVoiceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPanel.setVisibility(View.VISIBLE);
                mVoiceView.setVisibility(View.GONE);
            }
        });
    }


    /**
     * back键或者空白区域点击事件处理
     *
     * @return 已处理true, 否则false
     */
    public boolean onBackAction() {
        if (inputPanel.onBackAction()) {
            return true;
        }
        return false;
    }

    public void setInputPanelListener(InputPanel.InputPanelListener l) {
        inputPanel.setPanelListener(l);
    }
}
