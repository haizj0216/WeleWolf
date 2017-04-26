package com.knowbox.teacher.modules.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.utils.PreferencesController;

/**
 * 虚拟班级
 * <p/>
 * Created by weilei on 15/9/25.
 */
public class VirtualClassUtils {

    public static final String PREFS_VIRTUAL_TOKEN = "prefs_virtual_token";
    public static final String PREFS_VIRTUAL_HOMEWORK = "prefs_virtual_homework";
    public static final String PREFS_VIRTUAL_CLASS_LIST = "prefs_virtual_class_list";
    public static final String PREFS_VIRTUAL_STUDENT_LIST = "prefs_virtual_student_list";
    public static final String PREFS_VIRTUAL_CHOICE_CORRECT = "prefs_virtual_choice_correct";
    public static final String PREFS_VIRTUAL_ANSWER_CORRECT = "prefs_virtual_answer_correct";
    public static final String PREFS_VIRTUAL_ANSWER_UNCORRECT = "prefs_virtual_answer_uncorrect";
    public static final String PREFS_VIRTUAL_CORRECT_ORIGINAL = "prefs_virtual_correct_original";

    private static VirtualClassUtils mInstance;
    private Activity mActivity;
    private Dialog mDialog;

    public static VirtualClassUtils getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new VirtualClassUtils(activity);
        }
        return mInstance;
    }

    public void initPrefs() {
//        PreferencesController.setBoolean(PREFS_VIRTUAL_HOMEWORK, false);
//        PreferencesController.setBoolean(PREFS_VIRTUAL_CLASS_LIST, false);
//        PreferencesController.setBoolean(PREFS_VIRTUAL_STUDENT_LIST, false);
//        PreferencesController.setBoolean(PREFS_VIRTUAL_CHOICE_CORRECT, false);
//        PreferencesController.setBoolean(PREFS_VIRTUAL_ANSWER_CORRECT, false);
//        PreferencesController.setBoolean(PREFS_VIRTUAL_ANSWER_UNCORRECT, false);
//        PreferencesController.setBoolean(PREFS_VIRTUAL_CORRECT_ORIGINAL, false);
    }

    private VirtualClassUtils(Activity activity) {
        mActivity = activity;
    }

    public boolean isVirtualToken() {
        return PreferencesController.getBoolean(PREFS_VIRTUAL_TOKEN, false);
    }

    public void setVirtualToken(boolean virtual) {
        PreferencesController.setBoolean(PREFS_VIRTUAL_TOKEN, virtual);
    }

    /**
     * 作业列表
     */
    public void showHomeworkVirtualDialog() {
        if (!isVirtualToken() || PreferencesController.getBoolean(PREFS_VIRTUAL_HOMEWORK, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dip2px(mActivity, 176);
        params.leftMargin = UIUtils.dip2px(mActivity, 7);
        params.rightMargin = UIUtils.dip2px(mActivity, 7);
//        showDialog(R.drawable.icon_virtual_homework_pic, R.drawable.icon_virtual_homework_txt, params);
        PreferencesController.setBoolean(PREFS_VIRTUAL_HOMEWORK, true);
    }

    /**
     * 班级列表
     */
    public void showClassListVirtualDialog() {
        if (!isVirtualToken() || PreferencesController.getBoolean(PREFS_VIRTUAL_CLASS_LIST, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dip2px(mActivity, 60);
        params.leftMargin = UIUtils.dip2px(mActivity, 7);
        params.rightMargin = UIUtils.dip2px(mActivity, 7);
//        showDialog(R.drawable.icon_virtual_class_list_pic,R.drawable.icon_virtual_class_list_txt, params);
        PreferencesController.setBoolean(PREFS_VIRTUAL_CLASS_LIST, true);
    }

    /**
     * 学生列表
     */
    public void showStudentListVirtualDialog() {
        if (!isVirtualToken() || PreferencesController.getBoolean(PREFS_VIRTUAL_STUDENT_LIST, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dip2px(mActivity, 222);
        params.leftMargin = UIUtils.dip2px(mActivity, 7);
        params.rightMargin = UIUtils.dip2px(mActivity, 7);
//        showDialog(R.drawable.icon_virtual_student_list_pic, R.drawable.icon_virtual_student_list_txt, params);
        PreferencesController.setBoolean(PREFS_VIRTUAL_STUDENT_LIST, true);
    }

    /**
     * 已批改解答题
     */
    public void showAnswerCorrectDialog() {
        if (!isVirtualToken() || PreferencesController.getBoolean(PREFS_VIRTUAL_ANSWER_CORRECT, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dip2px(mActivity, 240);
        params.leftMargin = UIUtils.dip2px(mActivity, 7);
//        showDialog(R.drawable.icon_virtual_answer_corrected,0, params);
        PreferencesController.setBoolean(PREFS_VIRTUAL_ANSWER_CORRECT, true);
    }

    /**
     * 单选题
     * @param listener
     */
    public void showChoiceCorrectDialog(DialogInterface.OnDismissListener listener) {
        if (!isVirtualToken() || PreferencesController.getBoolean(PREFS_VIRTUAL_CHOICE_CORRECT, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dip2px(mActivity, 240);
        params.leftMargin = UIUtils.dip2px(mActivity, 7);
//        showDialog(R.drawable.icon_virtual_choice, 0, params, listener, null);
        PreferencesController.setBoolean(PREFS_VIRTUAL_CHOICE_CORRECT, true);
    }

    /**
     * 未批改解答题
     */
    public void showAnswerUnCorrectDialog() {
        if (!isVirtualToken() || PreferencesController.getBoolean(PREFS_VIRTUAL_ANSWER_UNCORRECT, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dip2px(mActivity, 350);
        params.leftMargin = UIUtils.dip2px(mActivity, 7);
//        showDialog(R.drawable.icon_virtual_answer_uncorrected, 0,params);
        PreferencesController.setBoolean(PREFS_VIRTUAL_ANSWER_UNCORRECT, true);
    }

    /**
     * 批改原题页
     */
    public void showCorrectOriginalDialog() {
        if (!isVirtualToken() || PreferencesController.getBoolean(PREFS_VIRTUAL_CORRECT_ORIGINAL, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = UIUtils.dip2px(mActivity, 200);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        showDialog(R.drawable.icon_virtual_correct_original, 0, params);
        PreferencesController.setBoolean(PREFS_VIRTUAL_CORRECT_ORIGINAL, true);
    }

//    public void showDialog(int resId, int textId, int textId1, LinearLayout.LayoutParams params, DialogInterface.OnDismissListener listener, View.OnClickListener clickListener) {
//        if ((mDialog != null) && mDialog.isShowing()) {
//            mDialog.dismiss();
//        }
//        if (mActivity == null) {
//            return;
//        }
//        try {
//            mDialog = DialogUtils.getVirtualDialog(mActivity, resId, textId, textId1, params, clickListener);
//            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        return true;
//                    }
//                    return false;
//                }
//            });
//            mDialog.setCanceledOnTouchOutside(false);
//            mDialog.setOnDismissListener(listener);
//            mDialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    //3.2版本引导
    public void showOverViewChoiceDialog(Activity activity, View.OnClickListener listener) {
        showDialog(activity, R.drawable.icon_virtual_answer_corrected, UIUtils.dip2px(210), UIUtils.dip2px(120), listener);
    }

    public void showOverViewAnswerDialog(Activity activity, View.OnClickListener listener) {
        showDialog(activity, R.drawable.icon_virtual_answer_uncorrected, UIUtils.dip2px(330), UIUtils.dip2px(120), listener);
    }

    public void showDetailDialog(Activity activity, View.OnClickListener listener) {
        showDialog(activity, R.drawable.icon_virtual_startcorrect, 0, UIUtils.dip2px(70),  listener);
    }

    public void showCorrectDialog(Activity activity, View.OnClickListener listener) {
        showDialog(activity, R.drawable.icon_virtual_correct, 0, UIUtils.dip2px(76),  listener);
    }

    public void showCorrectCompletion(Activity activity, View.OnClickListener listener) {
        showDialog(activity, R.drawable.icon_virtual_correct_complition, 0, UIUtils.dip2px(70),  listener);
    }

    public void showCorrectSwap(Activity activity, View.OnTouchListener listener) {
        if ((mDialog != null) && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (activity == null || activity.isFinishing()) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mDialog = DialogUtils.getVirtualDialog(activity, R.drawable.icon_virtual_swap, 0, params, listener);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void showDialog(Activity activity, int resId, int top, int height, View.OnClickListener listener) {
        if ((mDialog != null) && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (activity == null || activity.isFinishing()) {
            return;
        }
        try {
            mDialog = DialogUtils.getVirtualDialog(activity, resId, top, height, listener);
            mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
