package com.buang.welewolf.modules.game;

import android.content.Context;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.game.widget.InputPanel;
import com.buang.welewolf.modules.services.GameService;


public class BottomPanelFragment extends RelativeLayout {

    private static final String TAG = "BottomPanelFragment";

    private ImageView btnInput;
    private InputPanel inputPanel;
    private TextView mVoiceView;
    private ImageView mEmjoy;
    private GameService gameService;

    public BottomPanelFragment(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gameService = (GameService) context.getSystemService(GameService.SERVICE_NAME);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        btnInput = (ImageView) findViewById(R.id.btn_input);
        inputPanel = (InputPanel) findViewById(R.id.input_panel);
        mVoiceView = (TextView) findViewById(R.id.btn_voice);
        mEmjoy = (ImageView) findViewById(R.id.input_emoji_btn);
        mEmjoy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputPanel.updateEmojiBoard();
            }
        });
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
        mVoiceView.setOnTouchListener(new PressToSpeakListener());
    }

    /**
     * 按住说话listener
     */
    class PressToSpeakListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    try {
                        v.setPressed(true);
                        gameService.startSpeak();
                    } catch (Exception e) {
                        v.setPressed(false);
                        gameService.endSpeak();
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    gameService.endSpeak();
                    return true;
                default:
                    gameService.endSpeak();
                    return false;
            }
        }
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
