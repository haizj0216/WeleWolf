package com.knowbox.teacher.modules.homework.assign;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineCompetitionListInfo;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.widgets.CleanableEditText;
import com.knowbox.teacher.widgets.CleanableEditText.EmojiFilter;

/**
 * Created by LiuYu on 16/2/26.
 */
public class AssignNameFragment extends BaseUIFragment<UIFragmentHelper> {

    public static final String NAME = "name";

    private CleanableEditText mEditText;
    private String mGameType;
    private String mGameName;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        initArguments();
        setTitle();
        return View.inflate(getActivity(), R.layout.layout_homework_name, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mEditText = (CleanableEditText) view.findViewById(R.id.homework_edit_name);
        mEditText.getEditText().setTextColor(getResources().getColor(R.color.color_text_main));
        mEditText.getEditText().setText(mGameName);
        mEditText.addFilter(new EmojiFilter());
        mEditText.setMaxLength(20);
        mEditText.setFocusable(true);

        getUIFragmentHelper().getTitleBar().setRightMoreTxt("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserName(mEditText.getText());
            }
        });

        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }

    @Override
    public void finish() {
        UIUtils.hideInputMethod(getActivity());
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AssignNameFragment.super.finish();
            }
        }, 200);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();

    }

    private void updateUserName(String userName) {
        UIUtils.hideInputMethod(getActivity());
        if (TextUtils.isEmpty(userName)) {
            String toastContent;
            if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
                toastContent = getString(R.string.toast_homework_name_can_not_be_empty);
            } else {
                toastContent = getString(R.string.toast_single_test_name_can_not_be_empty);
            }
            ToastUtils.showShortToast(getActivity(), toastContent);
            return;
        }

        if (userName.length() < 2) {
            String toastContent;
            if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
                toastContent = getString(R.string.toast_homework_name_can_not_be_two_byte);
            } else {
                toastContent = getString(R.string.toast_single_test_name_can_not_be_two_byte);
            }
            ToastUtils.showShortToast(getActivity(), toastContent);
            return;
        }
        if (mOnModifyHomeworkNameListener != null) {
            mOnModifyHomeworkNameListener.onUserNameEdit(userName);
        }
        finish();
    }

    private void initArguments() {
        if (getArguments() == null) {
            return;
        }
        mGameName = getArguments().getString(NAME);
        mGameType =  getArguments().getString(ConstantsUtils.KEY_ASSIGN_TYPE);
    }

    private void setTitle() {
        String title;

        if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
            title = getString(R.string.title_homework_name);
        } else {
            title = getString(R.string.title_single_test_game_name);
        }

        getTitleBar().setTitle(title);
    }


    interface OnModifyHomeworkNameListener {
        void onUserNameEdit(String newName);
    }

    private OnModifyHomeworkNameListener mOnModifyHomeworkNameListener;

    public void setOnModifyHomeworkNameListener(OnModifyHomeworkNameListener mOnModifyHomeworkNameListener) {
        this.mOnModifyHomeworkNameListener = mOnModifyHomeworkNameListener;
    }
}
