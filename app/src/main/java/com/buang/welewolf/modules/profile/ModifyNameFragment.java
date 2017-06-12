package com.buang.welewolf.modules.profile;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;

/**
 * Created by weilei on 17/6/11.
 */

public class ModifyNameFragment extends BaseUIFragment<UIFragmentHelper> {

    public static final int TYPE_USER_NAME = 1;
    public static final int TYPE_GUILD_NAME = 2;
    public static final int TYPE_USER_SIGN = 3;
    public static final int TYPE_GUILD_SIGN = 4;

    public EditText mEditView;
    private OnModifyNameListener mOnModifyNameListener;
    private String mText;
    private int type;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        type = getArguments().getInt("type");
        mText = getArguments().getString("name");
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_modify_name, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("修改名称");
        getUIFragmentHelper().setTintBar(R.color.color_title_bar);
        mEditView = (EditText) view.findViewById(R.id.evEdit);
        mEditView.setText(mText);
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEditView.requestFocus();
            }
        }, 200);
        getUIFragmentHelper().getTitleBar().setRightMoreTxt("完成", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mEditView.getText().toString();
                if (mOnModifyNameListener != null && !text.equals(mText)) {
                    mOnModifyNameListener.onModifyName(text);
                }
                finish();
            }
        });
        updateType();
    }

    /**
     * 根据类型定义展示
     */
    private void updateType() {
        int height = 0;
        int maxLength = 2;
        switch (type) {
            case TYPE_USER_NAME:
                height = UIUtils.dip2px(50);
                maxLength = 20;
                break;
            case TYPE_USER_SIGN:
                height = UIUtils.dip2px(200);
                maxLength = 200;
                break;
            case TYPE_GUILD_NAME:
                height = UIUtils.dip2px(50);
                maxLength = 20;
                break;
            case TYPE_GUILD_SIGN:
                height = UIUtils.dip2px(200);
                maxLength = 200;
                break;
        }
        mEditView.setHeight(height);
        mEditView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

    }

    @Override
    public void finish() {
        UIUtils.hideInputMethod(getActivity());
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ModifyNameFragment.super.finish();
            }
        }, 200);

    }

    public interface OnModifyNameListener {
        public void onModifyName(String name);
    }

    public void setOnModifyNameListener(OnModifyNameListener listener) {
        mOnModifyNameListener = listener;
    }
}
