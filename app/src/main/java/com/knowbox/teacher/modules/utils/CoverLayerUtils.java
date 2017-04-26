package com.knowbox.teacher.modules.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.RelativeLayout;

import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.utils.PreferencesController;

/**
 * Created by LiuYu on 2016/1/21.
 */
public class CoverLayerUtils {
    private static final String PREFS_ADD_QUESTION_GUIDE = "prefs_add_question_guide";
    private static final String PREFS_CHECK_EXPAND_GUIDE = "prefs_check_expand_guide";
    private static final String PREFS_INTO_QUESTIONLIST_GUIDE = "prefs_into_questionlist_guide";
    private static final String PREFS_SHARE_QUESTIONGROUP_GUIDE = "prefs_share_questiongroup_guide";
    private static final String PREFS_SHARE_HOMEWORK_GUIDE = "prefs_share_homework_guide";
    private static final String PREFS_MAKEOUT_HOMEWORK_GUIDE = "prefs_makeout_homework_guide";
    private static final String PREFS_BATCH_MANAGEMENT_GUIDE = "prefs_batch_management_guide";
    private static final String PREFS_CAREFULSELECT_QUESTION_GUIDE = "prefs_share_carefullyselect_guide";
    private static final String PREFS_SELECT_GROUPS_GUIDE = "prefs_share_select_groups_guide";
    private static final String PREFS_ERROR_BOOK_GUIDE = "prefs_classinfo_wrongbook_guide";

    private static CoverLayerUtils sInstance;
    private Activity mActivity;
    private Dialog mDialog;

    public static CoverLayerUtils getInstance(Activity activity) {
        if (sInstance == null) {
            sInstance = new CoverLayerUtils(activity);
        }
        return sInstance;
    }

    private CoverLayerUtils(Activity activity) {
        mActivity = activity;
    }

    public void showAddQuestionDialog(DialogInterface.OnDismissListener listener) {
        if (PreferencesController.getBoolean(PREFS_ADD_QUESTION_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = UIUtils.dip2px(mActivity, 10);
        showDialog(R.drawable.bg_guide_question_add_basket, 0, params, listener);
        PreferencesController.setBoolean(PREFS_ADD_QUESTION_GUIDE, true);
    }

    public void showShareGroupDialog(DialogInterface.OnDismissListener listener) {
        if (PreferencesController.getBoolean(PREFS_SHARE_QUESTIONGROUP_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.topMargin = UIUtils.dip2px(mActivity, 3);
        params.rightMargin = UIUtils.dip2px(mActivity, 3);
        showDialog(R.drawable.bg_guide_sharequestion_group, 0, params, listener);
        PreferencesController.setBoolean(PREFS_SHARE_QUESTIONGROUP_GUIDE, true);
    }

    public void showShareHomeworkDialog() {
        if (PreferencesController.getBoolean(PREFS_SHARE_HOMEWORK_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.topMargin = UIUtils.dip2px(mActivity, 3);
        params.rightMargin = UIUtils.dip2px(mActivity, 3);
        showDialog(R.drawable.bg_guide_sharehomework, 0, params);
        PreferencesController.setBoolean(PREFS_SHARE_HOMEWORK_GUIDE, true);
    }

    public void showMakeOutHomeworkDialog(int marginTop, DialogInterface.OnDismissListener listener) {
        if (PreferencesController.getBoolean(PREFS_MAKEOUT_HOMEWORK_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.topMargin = marginTop;
        params.rightMargin = UIUtils.dip2px(mActivity, 5);
        showDialog(R.drawable.bg_guide_makeouthomework, 0, params, listener);
        PreferencesController.setBoolean(PREFS_MAKEOUT_HOMEWORK_GUIDE, true);
    }

    public void showBatchManagementDialog(int marginTop) {
        if (PreferencesController.getBoolean(PREFS_BATCH_MANAGEMENT_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.topMargin = marginTop;
        params.rightMargin = UIUtils.dip2px(mActivity, 9);
        showDialog(R.drawable.bg_guide_batch_management, 0, params);
        PreferencesController.setBoolean(PREFS_BATCH_MANAGEMENT_GUIDE, true);
    }

    public void showSelectGroupsDialog(int marginTop, DialogInterface.OnDismissListener listener) {
        if (PreferencesController.getBoolean(PREFS_SELECT_GROUPS_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = marginTop;
        showDialog(R.drawable.bg_guide_select_groups, 0, params, listener);
        PreferencesController.setBoolean(PREFS_SELECT_GROUPS_GUIDE, true);
    }

    /**
     * 错题本蒙层引导
     * @param marginTop
     * @param marginLeft
     * @param listener
     */
    public void showWrongBookDialog(int marginTop, int marginLeft, DialogInterface.OnDismissListener listener) {
        if (PreferencesController.getBoolean(PREFS_ERROR_BOOK_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = marginTop;
        params.leftMargin = marginLeft;
        showDialog(R.drawable.bg_guide_show_wrongbook, 0, params, listener);
        PreferencesController.setBoolean(PREFS_ERROR_BOOK_GUIDE, true);
    }

    public void showCarefulSelectDialog(int marginTop, DialogInterface.OnDismissListener listener) {
        if (PreferencesController.getBoolean(PREFS_CAREFULSELECT_QUESTION_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.topMargin = UIUtils.dip2px(mActivity, marginTop);
        params.leftMargin = UIUtils.dip2px(mActivity, 10);
        showDialog(R.drawable.bg_guide_carefully_selected, 0, params, listener);
        PreferencesController.setBoolean(PREFS_CAREFULSELECT_QUESTION_GUIDE, true);
    }

    public void showCheckExpandDialog(int marginTop, DialogInterface.OnDismissListener listener) {
        if (PreferencesController.getBoolean(PREFS_CHECK_EXPAND_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.topMargin = marginTop - UIUtils.dip2px(15);
        showDialog(R.drawable.bg_guide_expand_item, 0, params, listener);
        PreferencesController.setBoolean(PREFS_CHECK_EXPAND_GUIDE, true);
    }

    public void showCheckExpandDialog(int marginTop) {
        if (PreferencesController.getBoolean(PREFS_CHECK_EXPAND_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.topMargin = marginTop - UIUtils.dip2px(15);
        showDialog(R.drawable.bg_guide_expand_item, 0, params);
        PreferencesController.setBoolean(PREFS_CHECK_EXPAND_GUIDE, true);
    }

    public void showIntoQuestionlistDialog(int top) {
        if (PreferencesController.getBoolean(PREFS_INTO_QUESTIONLIST_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.topMargin = top - UIUtils.dip2px(15);
        params.rightMargin = UIUtils.dip2px(mActivity, 10);
        showDialog(R.drawable.bg_guide_gointo_questionlist, 0, params);
        PreferencesController.setBoolean(PREFS_INTO_QUESTIONLIST_GUIDE, true);
    }

    public void showPreviewGuideDialog() {
        if (PreferencesController.getBoolean(ConstantsUtils.PREFS_HOMEWORK_PREVIEW_GUIDE, false)) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.topMargin = UIUtils.dip2px(10);
        params.rightMargin = UIUtils.dip2px(10);
        showDialog(R.drawable.icon_preview_guide, 0, params);
        PreferencesController.setBoolean(ConstantsUtils.PREFS_HOMEWORK_PREVIEW_GUIDE, true);
    }

    public void showDialog(int resId, int textId, RelativeLayout.LayoutParams params, DialogInterface.OnDismissListener listener) {
        if ((mDialog != null) && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (mActivity == null) {
            return;
        }
        try {
            /**
             * resId 源imgID
             * textId 源描述imgID,传 0 GONE掉。
             */
            mDialog = DialogUtils.getGuideDialog(mActivity, resId, textId, params, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                }
            });
            mDialog.setOnDismissListener(listener);
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showDialog(int resId, int textId,  RelativeLayout.LayoutParams params) {
        showDialog(resId, textId, params, null);
    }
}
