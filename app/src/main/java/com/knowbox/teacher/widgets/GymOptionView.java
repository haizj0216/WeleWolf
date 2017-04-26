package com.knowbox.teacher.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knowbox.teacher.R;
import com.knowbox.teacher.base.utils.StringUtils;


/**
 * Created by weilei on 16/10/17.
 */
public class GymOptionView extends RelativeLayout {

    private TextView mOptionCode;
    private TextView mOptionValue;
    private ImageView mOptionResult;

    public GymOptionView(Context context) {
        super(context);
    }

    public GymOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mOptionCode = (TextView) findViewById(R.id.gym_option_code);
        mOptionValue = (TextView) findViewById(R.id.gym_option_value);
        mOptionResult = (ImageView) findViewById(R.id.gym_option_result);
    }

    /**
     * 设置选项
     *
     * @param code
     * @param value
     */
    public void setOptionContent(String code, String value, int id) {
        if (mOptionCode != null) {
            StringUtils.setTextHtml(mOptionCode, code);
        }
        if (mOptionValue != null) {
            StringUtils.setTextHtml(mOptionValue, value);
        }
        setBackgroundResource(id);
    }

    /**
     * 选中答案
     *
     * @param isRight
     */
    public void setSelectResult(boolean isRight) {
        if (mOptionValue != null) {
            mOptionValue.setTextColor(getResources().getColor(R.color.white));
        }

        mOptionCode.setVisibility(INVISIBLE);
        mOptionResult.setVisibility(VISIBLE);
        if (isRight) {
            setBackgroundResource(R.drawable.bg_gym_question_option_right);
            mOptionResult.setImageResource(R.drawable.icon_gym_option_right);
        } else {
            setBackgroundResource(R.drawable.bg_gym_question_option_wrong);
            mOptionResult.setImageResource(R.drawable.icon_gym_option_wrong);
        }
    }

    public void setUnSelectRight() {
        mOptionResult.setVisibility(VISIBLE);
    }
}
