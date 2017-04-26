package com.knowbox.teacher.modules.homework.assign;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineCompetitionListInfo;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.profile.UserInfoEditFragment;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.Utils;

import java.util.Calendar;

/**
 * Created by LiuYu on 2015/12/10.
 */
public class AssignSuccessFragment extends BaseUIFragment<UIFragmentHelper> implements View.OnClickListener{

    private View mDownloadView;
    private TextView mDownload;
    private Dialog mDialog;
    private TextView mPriseDesc;
//    private TextView mTvInviteStudent;
    private TextView mTvGameType;
    private TextView mtvClassNames;
    private TextView mTvSuccessNotice;
    private OnlineCompetitionListInfo.CompetitionItem competitionItem;
    private long mStartDate;
    private long mEndDate;
    private String mClassNames;
    private String mGameType;

    private TextView mTypeTitle;
    private TextView mNameTitle;
    private TextView mDateTitle;
    private TextView mTimeTitle;
    private TextView mClassTitle;

    private static final int ACTION_INVITE_STUDENT = 1;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        if (getArguments() == null) {
            return;
        }
        mGameType = getArguments().getString(ConstantsUtils.KEY_ASSIGN_TYPE);
        mStartDate = getArguments().getLong(ConstantsUtils.KEY_SUBMIT_START_DATE);
        mEndDate = getArguments().getLong(ConstantsUtils.KEY_SUBMIT_END_DATE);
        mClassNames = getArguments().getString(ConstantsUtils.KEY_SUBMIT_CLASS_NAMES);
        mGameType = getArguments().getString(ConstantsUtils.KEY_ASSIGN_TYPE);
        competitionItem = (OnlineCompetitionListInfo.CompetitionItem) getArguments().getSerializable(ConstantsUtils.KEY_SUBMIT_COMP);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getUIFragmentHelper().getTitleBar().setTitle("发布成功");
        getUIFragmentHelper().getTitleBar().setBackBtnVisible(false);
        return View.inflate(getActivity(), R.layout.layout_makeout_success, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);

        mTypeTitle = (TextView) view.findViewById(R.id.tv_game_type_title);
        mNameTitle = (TextView) view.findViewById(R.id.tv_game_name_title);
        mDateTitle = (TextView) view.findViewById(R.id.tv_game_date_title);
        mTimeTitle = (TextView) view.findViewById(R.id.tv_game_time_title);
        mClassTitle = (TextView) view.findViewById(R.id.tv_game_class_title);

        //初始化、
        mPriseDesc = (TextView) view.findViewById(R.id.success_desc);
        if (competitionItem.rewardCoin != 0) {
            mPriseDesc.setVisibility(View.VISIBLE);
            mPriseDesc.setText("学生完成本次比赛\n最多可获得" + competitionItem.rewardCoin + "金币");
        }else {
            mPriseDesc.setVisibility(View.GONE);
        }
        mTvGameType = (TextView) view.findViewById(R.id.tv_game_type);
//        mTvInviteStudent = (TextView) view.findViewById(R.id.tv_invite_student);
        mtvClassNames = (TextView) view.findViewById(R.id.tv_game_class);
        mtvClassNames.setText(mClassNames);
        mTvSuccessNotice = (TextView) view.findViewById(R.id.success_notice);

        String date;

        if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
            mTvSuccessNotice.setText(getString(R.string.tv_release_game_success));
//            mTvInviteStudent.setVisibility(View.GONE);
            mTvGameType.setText(getString(R.string.tv_circle_practice_game));
            date = DateUtil.getYearMonthDayString(mStartDate / 1000) + "-" + DateUtil.getYearMonthDayString(mEndDate / 1000);
        } else {
            mTvSuccessNotice.setText(getString(R.string.tv_release_word_test_success));
//            mTvInviteStudent.setVisibility(View.VISIBLE);
//            mTvInviteStudent.setOnClickListener(this);
            mTvGameType.setText(getString(R.string.tv_single_test_game));
            date = DateUtil.getYearMonthDayString(mStartDate / 1000);
        }
        ((TextView)view.findViewById(R.id.time_homework_submmit_name)).setText(competitionItem.name);
        ((TextView)view.findViewById(R.id.time_homework_submmit_date)).setText(date);
        Calendar startC = Calendar.getInstance();
        startC.setTimeInMillis(mStartDate);
        Calendar endC = Calendar.getInstance();
        endC.setTimeInMillis(mEndDate);

        if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
            String time = "每日" + DateUtil.getHourMintute(startC) + "-" + DateUtil.getHourMintute(endC);
            ((TextView)view.findViewById(R.id.time_homework_submmit_time)).setText(time);
        } else {
            String time = DateUtil.getHourMintute(startC) + "-" + DateUtil.getHourMintute(endC);
            ((TextView)view.findViewById(R.id.time_homework_submmit_time)).setText(time);
        }
        mDownloadView = view.findViewById(R.id.download_student_layout);
        if (PreferencesController.getBoolean(ConstantsUtils.PREFS_DOWNLOAD_TIP, false)) {
            mDownloadView.setVisibility(View.GONE);
        } else {
            mDownloadView.setVisibility(View.VISIBLE);
        }
        mDownload = (TextView) view.findViewById(R.id.download_student);
        mDownload.setOnClickListener(this);
        view.findViewById(R.id.success_confirm).setOnClickListener(this);
        view.findViewById(R.id.download_student_close).setOnClickListener(this);

        if (!PreferencesController.getBoolean(ConstantsUtils.MAKEOUT_SUCCESS_DIALOG, false)) {
            UiThreadHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDialog();
                    PreferencesController.setBoolean(ConstantsUtils.MAKEOUT_SUCCESS_DIALOG, true);
                }
            }, 500);
        }

        initDefaultTypeGameViewAndData();
    }

    /**
     * 根据比赛类型的不同，初始化不同的View和Data
     */
    private void initDefaultTypeGameViewAndData() {
        if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
        } else {
            // 比赛日期
            mTypeTitle.setText("测验类型");
            mNameTitle.setText("测验名称");
            mDateTitle.setText("测验日期");
            mTimeTitle.setText("测验时间");
            mClassTitle.setText("测验班级");
        }
    }

    private void showDialog() {
        UserItem userItem = Utils.getLoginUserItem();
        if (!TextUtils.isEmpty(userItem.schoolName) &&
                !TextUtils.isEmpty(userItem.headPhoto) && !TextUtils.isEmpty(userItem.sex)) {
            return;
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getNewMessageDialog(getActivity(),
                R.drawable.icon_dialog_message,
                "一切为了学生",
                "在”我的“页面点击头像完善个人信息,有助于学生加入班群。",
                "取消", "去设置", new DialogUtils.OnDialogButtonClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                            openSettingsFragment();
                        }
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
    }

    private void openSettingsFragment() {
        UserInfoEditFragment fragment = UserInfoEditFragment.newFragment(getActivity(),
                UserInfoEditFragment.class, null);
        showFragment(fragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.download_student:
                downloadStudentApk();
                PreferencesController.setBoolean(ConstantsUtils.PREFS_DOWNLOAD_TIP, true);
                break;
            case R.id.download_student_close:
                mDownloadView.setVisibility(View.GONE);
                PreferencesController.setBoolean(ConstantsUtils.PREFS_DOWNLOAD_TIP, true);
                break;
            case R.id.success_confirm:
                finish();
                break;
//            case R.id.tv_invite_student:        //邀请学生
//                loadData(ACTION_INVITE_STUDENT, PAGE_MORE);
//                break;
            default:
                break;
        }
    }

    private void downloadStudentApk() {
        try {
            String url = getArguments().getString(ConstantsUtils.KEY_SUBMIT_JUMPURL);
            if (TextUtils.isEmpty(url)) {
                url = OnlineServices.getWordWebUrl();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                removeAllFragment();
            }
        });
    }
}
