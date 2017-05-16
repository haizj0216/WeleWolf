package com.buang.welewolf.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.base.database.bean.QuestionItem;
import com.knowbox.base.utils.UIUtils;

/**
 * Created by weilei on 16/12/26.
 */
public class GymWordBlankView extends RelativeLayout {
    private LinearLayout mBlankView;
    private LinearLayout mOptionGrid;

    public GymWordBlankView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBlankView = (LinearLayout) findViewById(com.buang.welewolf.R.id.gym_question_blank_layout);
        mOptionGrid = (LinearLayout) findViewById(com.buang.welewolf.R.id.gym_question_blank_gridview);
    }

    /**
     * 更新解析
     *
     * @param questionItem
     */
    public void updateAnlyze(QuestionItem questionItem) {
        mBlankView.removeAllViews();
        mOptionGrid.removeAllViews();
        mOptionGrid.setOrientation(LinearLayout.HORIZONTAL);
        String blackStr = questionItem.wordBlank;
        String rightAnswer = questionItem.mRightAnswer;
        int position = 0;
        for (int i = 0; i < blackStr.length(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.leftMargin = params.rightMargin = UIUtils.dip2px(1);
            TextView blankTxt = buildTextView();
            TextView answerTxt = buildTextView();
            answerTxt.setTextSize(23);
            if ("_".equals(blackStr.substring(i, i + 1))) {

                blankTxt.setText("_");
                blankTxt.setTextColor(getResources().getColor(com.buang.welewolf.R.color.white));
                blankTxt.setBackgroundResource(com.buang.welewolf.R.drawable.icon_gym_blank_wrong);

                answerTxt.setText(String.valueOf(rightAnswer.charAt(position)));
                answerTxt.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_main_app));

                position++;
            } else {
                blankTxt.setText(String.valueOf(blackStr.charAt(i)));
                answerTxt.setText(String.valueOf(blackStr.charAt(i)));
            }
            mBlankView.addView(blankTxt, params);
            mOptionGrid.addView(answerTxt, params);
        }
    }


    private TextView buildTextView() {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_gym_question_content));
        textView.setTextSize(27);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        return textView;
    }

}
