package com.buang.welewolf.modules.homework.assign;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineCompQuestionPreviewInfo;
import com.buang.welewolf.base.bean.OnlineWordInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.base.utils.PreferencesController;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ToastUtils;
import com.knowbox.base.utils.ImageFetcher;
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineAssignSuccessInfo;
import com.buang.welewolf.base.bean.OnlineCompetitionListInfo;
import com.buang.welewolf.base.bean.OnlineEstimateTimeInfo;
import com.buang.welewolf.base.bean.OnlineUnitInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.services.updateclass.UpdateClassService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DateUtil;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.SubjectUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.widgets.RoundDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by weilei on 17/2/20.
 */
public class AssignSubmitFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_GET_COST = 2;

    private ListView mGradeClassListView;
    private GradeClassListAdapter adapter;
    private List<ClassInfoItem> classItems;
    private ArrayList<OnlineUnitInfo> mUnitInfos;
    private ArrayList<OnlineWordInfo> mWordInfos;
    private ArrayList<OnlineCompQuestionPreviewInfo.QuestionTypeItem> mQuestionDims;
    private TextView mTitleView;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mStartTime;
    private TextView mEndTime;
    private TextView mTvGameType;
    private LinearLayout mLlCirclePracticeGameDate;
    private TextView mTvSingleTestGameDate;
    private TextView mTvTipTestTime;
    private TextView mTvMakeConfirmOk;
    private TextView mTypeTitle;
    private TextView mNameTitle;
    private TextView mDateTitle;
    private TextView mTimeTitle;
    private TextView mEveryDay;
    private Dialog mDatePickerDialog;

    private UpdateClassService mUpdateClassService;

    private Calendar mSDL = Calendar.getInstance();
    private Calendar mEDL = Calendar.getInstance();
    private long minDate = 24 * 60 * 60 * 6 * 1000;
    private long minTime = 1;
    private int startClock;
    private int endClock;
    private long minDay;
    private String mGameType;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mUpdateClassService = (UpdateClassService) getActivity().getSystemService(UpdateClassService.SERVICE_NAME);
        classItems = mUpdateClassService.getAllClassInfoItem();
        mUnitInfos = (ArrayList<OnlineUnitInfo>) getArguments().getSerializable(ConstantsUtils.KEY_ASSIGN_UNITLIST);
        mWordInfos = (ArrayList<OnlineWordInfo>) getArguments().getSerializable(ConstantsUtils.KEY_ASSIGN_WORDLIST);
        mQuestionDims = (ArrayList<OnlineCompQuestionPreviewInfo.QuestionTypeItem>) getArguments().getSerializable(ConstantsUtils.KEY_ASSIGN_DIMLIST);
        minDay = (getArguments().getInt(ConstantsUtils.KEY_BUNDLE_MINDAY));
        startClock = getArguments().getInt(ConstantsUtils.KEY_BUNDLE_START_CLOCK);
        endClock = getArguments().getInt(ConstantsUtils.KEY_BUNDLE_END_CLOCK);
        mGameType = getArguments().getString(ConstantsUtils.KEY_ASSIGN_TYPE);
        minDate = (minDay - 1) * 24 * 60 * 60 * 1000;
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        setTitle();
        return View.inflate(getActivity(), R.layout.layout_makehomework_confirm, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        if (null == classItems || classItems.size() == 0) {
            mUpdateClassService.updateClassInfo();
        }
        mGradeClassListView = (ListView) view.findViewById(R.id.make_confirm_gradelist);
        setMyAdapter();

        mTitleView = (TextView) view.findViewById(R.id.make_confirm_title);
        mStartDate = (TextView) view.findViewById(R.id.com_start_date);
        mEndDate = (TextView) view.findViewById(R.id.com_end_date);
        mStartTime = (TextView) view.findViewById(R.id.com_start_time);
        mEndTime = (TextView) view.findViewById(R.id.com_end_time);
        mTvGameType = (TextView) view.findViewById(R.id.tv_game_type);
        mTypeTitle = (TextView) view.findViewById(R.id.tv_game_type_title);
        mNameTitle = (TextView) view.findViewById(R.id.homework_title);
        mDateTitle = (TextView) view.findViewById(R.id.tv_game_date_title);
        mTimeTitle = (TextView) view.findViewById(R.id.tv_game_time_title);
        mEveryDay = (TextView) view.findViewById(R.id.tv_game_time_everyday);
        mLlCirclePracticeGameDate = (LinearLayout) view.findViewById(R.id.ll_circle_practice_game_date);
        mTvSingleTestGameDate = (TextView) view.findViewById(R.id.tv_single_test_game_date);
        mTvTipTestTime = (TextView) view.findViewById(R.id.tv_tip_test_time);


        mTvMakeConfirmOk =  (TextView) view.findViewById(R.id.make_confirm_ok);
        mTvMakeConfirmOk.setOnClickListener(mOnClickListener);
        view.findViewById(R.id.confirm_title_layout).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.confirm_makeout_layout).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.make_confirm_deadline_layout).setOnClickListener(mOnClickListener);
        mStartDate.setOnClickListener(mOnClickListener);
        mEndDate.setOnClickListener(mOnClickListener);
        mStartTime.setOnClickListener(mOnClickListener);
        mEndTime.setOnClickListener(mOnClickListener);
        mLlCirclePracticeGameDate.setOnClickListener(mOnClickListener);
        mTvSingleTestGameDate.setOnClickListener(mOnClickListener);


        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < mUnitInfos.size(); i++) {
            buffer.append(mUnitInfos.get(i).mUnitName);
            if (i != mUnitInfos.size() - 1) {
                buffer.append("、");
            }
        }
        mTitleView.setText(buffer.toString());

        if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_SINGLE_TEST)) {
            loadEstimateTime();
        }

        initPublicDate();
        initDefaultTypeGameViewAndData();
    }

    /**
     * 设置标题栏标题
     */
    private void setTitle() {
        String title = "";
        if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
            title = getString(R.string.title_release_circle_practice_game);
        } else {
            title = getString(R.string.title_release_single_test_game);
        }
        getTitleBar().setTitle(title);
    }

    /**
     * 根据比赛类型的不同，初始化不同的View和Data
     */
    private void initDefaultTypeGameViewAndData() {
        String gameType = "";
        String btnSubmitContent = "";

        if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
            mTvSingleTestGameDate.setVisibility(View.GONE);
            mLlCirclePracticeGameDate.setVisibility(View.VISIBLE);
            mTvTipTestTime.setVisibility(View.GONE);
            mStartDate.setText(DateUtil.getMonthDay(mSDL));
            mEndDate.setText(DateUtil.getMonthDay(mEDL));
            // 比赛类型
            gameType = getString(R.string.tv_circle_practice_game);
            //发布按钮文本
            btnSubmitContent = getString(R.string.tv_release_game);
        } else {
            // 比赛日期
            mTvSingleTestGameDate.setVisibility(View.VISIBLE);
            mLlCirclePracticeGameDate.setVisibility(View.GONE);
            mTvTipTestTime.setVisibility(View.VISIBLE);
            mTvSingleTestGameDate.setText(DateUtil.getMonthDay(mSDL));
            mEveryDay.setVisibility(View.GONE);
            mTypeTitle.setText("测验类型");
            mNameTitle.setText("测验名称");
            mDateTitle.setText("测验日期");
            mTimeTitle.setText("测验时间");

            // 比赛类型
            gameType = getString(R.string.tv_single_test_game);
            //发布按钮文本
            btnSubmitContent = getString(R.string.tv_release_word_test);
        }
        //比赛时间
        mStartTime.setText(DateUtil.getHourMintute(mSDL));
        mEndTime.setText(DateUtil.getHourMintute(mEDL));
        //比赛类型
        mTvGameType.setText(gameType);
        //发布按钮
        mTvMakeConfirmOk.setText(btnSubmitContent);
    }

    /**
     * 初始化 循环/单次测验 比赛的日期、时间
     */
    private void initPublicDate() {
        long startDate = PreferencesController.getLongValue(ConstantsUtils.PREFS_SUBMIT_START);
        long endDate = PreferencesController.getLongValue(ConstantsUtils.PREFS_SUBMIT_END);
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTimeInMillis(startDate);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(endDate);

        if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_SINGLE_TEST)) {
            long startTime = System.currentTimeMillis();
            mSDL.setTimeInMillis(startTime);
            mEDL.setTimeInMillis(startTime + (30 * 60 * 1000));
        } else {
            long startTime = System.currentTimeMillis();
            mSDL.setTime(new Date(startTime));
            mSDL.set(Calendar.HOUR_OF_DAY, startDate == -1 ? 21 : calendarStart.get(Calendar.HOUR_OF_DAY));
            mSDL.set(Calendar.MINUTE, startDate == -1 ? 0 : calendarStart.get(Calendar.MINUTE));
            mSDL.set(Calendar.SECOND, 0);

            long endTime = System.currentTimeMillis();
            if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)){
                endTime = System.currentTimeMillis() + minDate;
            }
            mEDL.setTime(new Date(endTime));
            mEDL.set(Calendar.HOUR_OF_DAY, endDate == -1 ? 21 : calendarEnd.get(Calendar.HOUR_OF_DAY));
            mEDL.set(Calendar.MINUTE, endDate == -1 ? 30 : calendarEnd.get(Calendar.MINUTE));
            mEDL.set(Calendar.SECOND, 0);
        }


    }

    private void setMyAdapter() {
        adapter = new GradeClassListAdapter(getActivity());
        adapter.setItems(getClassItems());
        mGradeClassListView.setAdapter(adapter);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.make_confirm_ok:
                    if (adapter.getSelectedItems().size() == 0) {
                        ToastUtils.showShortToast(getActivity(), "请选择班级");
                        return;
                    }
                    if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
                        if (isCircleDateValid()) {
                            submitMatch();
                        }
                    } else {
                        if (isSingleDateValid()) {
                            submitMatch();
                        }
                    }
                    break;
                case R.id.confirm_title_layout:     //比赛名称
                    ModifyHomeworkName(mTitleView.getText().toString(), mTitleView);
                    break;
                case R.id.com_start_date:
                    ModifyMakeOutTime(ConstantsUtils.TYPE_DATA_PICKER_START);
                    break;
                case R.id.com_end_date:
                    ModifyMakeOutTime(ConstantsUtils.TYPE_DATA_PICKER_END);
                    break;
                case R.id.com_start_time:
                    ModifyMakeOutTime(ConstantsUtils.TYPE_TIME_PICKER_START);
                    break;
                case R.id.com_end_time:
                    ModifyMakeOutTime(ConstantsUtils.TYPE_TIME_PICKER_END);
                    break;
                case R.id.tv_single_test_game_date:     //单次测验赛日期
                    ModifyMakeOutTime(ConstantsUtils.TYPE_DATE_SINGLE_TEST_GAME_PICK);
                default:
                    break;
            }
        }
    };

    private void submitMatch() {
        JSONObject object = new JSONObject();
        try {
            object.put("type", mGameType);
            object.put("name", mTitleView.getText().toString());
            object.put("begin_time", mSDL.getTimeInMillis() / 1000);
            object.put("end_time", mEDL.getTimeInMillis() / 1000);
            JSONArray classes = new JSONArray();
            for (int i = 0; i < adapter.getSelectedItems().size(); i++) {
                classes.put(adapter.getSelectedItems().get(i).classId);
            }
            object.put("class_ids", classes.toString());

            JSONArray words = new JSONArray();
            for (int i = 0; i < mWordInfos.size(); i++) {
                OnlineWordInfo unitInfo = mWordInfos.get(i);
                words.put(unitInfo.wordID);
            }
            object.put("word_ids", words.toString());
            JSONArray types = new JSONArray();
            for (int i = 0; i < mQuestionDims.size(); i++) {
                OnlineCompQuestionPreviewInfo.QuestionTypeItem type = mQuestionDims.get(i);
                types.put(type.type);
            }
            object.put("question_dims", types.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadDefaultData(PAGE_MORE, object.toString());
    }

    private void loadEstimateTime() {
        JSONObject object = new JSONObject();
        try {
            JSONArray words = new JSONArray();
            for (int i = 0; i < mWordInfos.size(); i++) {
                OnlineWordInfo unitInfo = mWordInfos.get(i);
                words.put(unitInfo.wordID);
            }
            object.put("word_ids", words.toString());
            JSONArray types = new JSONArray();
            for (int i = 0; i < mQuestionDims.size(); i++) {
                OnlineCompQuestionPreviewInfo.QuestionTypeItem type = mQuestionDims.get(i);
                types.put(type.type);
            }
            object.put("question_dims", types.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadData(ACTION_GET_COST, PAGE_MORE, object.toString());
    }

    /**
     * 修改发布时间
     */
    private void ModifyMakeOutTime(final int type) {
        if (mDatePickerDialog != null && mDatePickerDialog.isShowing()) {
            mDatePickerDialog.dismiss();
        }
        long nowTime = mSDL.getTimeInMillis();
        switch (type) {
            case ConstantsUtils.TYPE_DATA_PICKER_START:
                break;
            case ConstantsUtils.TYPE_DATA_PICKER_END:
                nowTime = mEDL.getTimeInMillis();
                break;
            case ConstantsUtils.TYPE_TIME_PICKER_START:
                break;
            case ConstantsUtils.TYPE_TIME_PICKER_END:
                nowTime = mEDL.getTimeInMillis();
                break;
            case ConstantsUtils.TYPE_DATE_SINGLE_TEST_GAME_PICK:
                break;
        }
        mDatePickerDialog = DialogUtils.getAssignDateDialog(getActivity(), nowTime, type,
                startClock, endClock, new DialogUtils.OnDataTimePickerSelectListener() {
                    @Override
                    public void onSelectDateTime(long time) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(time));
                        switch (type) {
                            case ConstantsUtils.TYPE_DATA_PICKER_START:
                                mSDL.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                                mSDL.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                                mSDL.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                                mStartDate.setText(DateUtil.getMonthDay(mSDL));
                                break;
                            case ConstantsUtils.TYPE_DATA_PICKER_END:
                                mEDL.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                                mEDL.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                                mEDL.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                                mEndDate.setText(DateUtil.getMonthDay(mEDL));
                                break;
                            case ConstantsUtils.TYPE_TIME_PICKER_START:
                                mSDL.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                                mSDL.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                                mStartTime.setText(DateUtil.getHourMintute(mSDL));
                                break;
                            case ConstantsUtils.TYPE_TIME_PICKER_END:
                                mEDL.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                                mEDL.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                                mEndTime.setText(DateUtil.getHourMintute(mEDL));
                                break;
                            case ConstantsUtils.TYPE_DATE_SINGLE_TEST_GAME_PICK:
                                mSDL.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                                mSDL.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                                mSDL.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                                mEDL.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                                mEDL.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                                mEDL.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                                mTvSingleTestGameDate.setText(DateUtil.getMonthDay(mSDL));
                                break;
                        }

                        mDatePickerDialog.dismiss();
                    }
                });
        mDatePickerDialog.show();
    }

    /**
     * 单次测验赛的日期是否合法
     */
    private boolean isSingleDateValid() {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        if (DateUtil.compMonthDay(mSDL, now)) {
            ToastUtils.showShortToast(getActivity(), "开始日期不能小于今天");
            return false;
        }

        return autoUpdateTime();
    }

    /**
     * 循环测验赛的日期是否合法
     */
    private boolean isCircleDateValid() {
        long day = 1000 * 60 * 60 * 24;

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        if (DateUtil.compMonthDay(mSDL, now)) {
            ToastUtils.showShortToast(getActivity(), "开始日期不能小于今天");
            return false;
        }

        Calendar sc = Calendar.getInstance();
        Calendar ec = Calendar.getInstance();
        sc.set(mSDL.get(Calendar.YEAR), mSDL.get(Calendar.MONTH), mSDL.get(Calendar.DAY_OF_MONTH), 0, 0);
        ec.set(mEDL.get(Calendar.YEAR), mEDL.get(Calendar.MONTH), mEDL.get(Calendar.DAY_OF_MONTH), 0, 0);
        long start = sc.getTimeInMillis() / day ;
        long end = ec.getTimeInMillis() / day;
        if (end - start < (minDay - 1)) {
            ToastUtils.showShortToast(getActivity(), "比赛日期最少选择" + minDay + "天时间");
            return false;
        }
        return autoUpdateTime();
    }

    private boolean autoUpdateTime() {
        int min = 1000 * 60;
        Calendar sc = Calendar.getInstance();
        sc.setTimeInMillis(mSDL.getTimeInMillis());
        Calendar ec = Calendar.getInstance();
        ec.setTimeInMillis(mEDL.getTimeInMillis());

        ec.set(Calendar.YEAR, sc.get(Calendar.YEAR));
        ec.set(Calendar.MONTH, sc.get(Calendar.MONTH));
        ec.set(Calendar.DAY_OF_MONTH, sc.get(Calendar.DAY_OF_MONTH));
        long start = sc.getTimeInMillis() / min;
        long end = ec.getTimeInMillis() / min;

        if (end - start < minTime) {
            ToastUtils.showShortToast(getActivity(), "比赛时间最少" + minTime + "分钟");
            return false;
        }
        if (sc.get(Calendar.HOUR_OF_DAY) < startClock || sc.get(Calendar.HOUR_OF_DAY) > endClock ||
                (sc.get(Calendar.MINUTE) > 0 && (sc.get(Calendar.HOUR_OF_DAY) == startClock || sc.get(Calendar.HOUR_OF_DAY) == endClock))) {
            ToastUtils.showShortToast(getActivity(), "比赛时间段为" + startClock + "：00-" + endClock + "：00");
            return false;
        }
        if (ec.get(Calendar.HOUR_OF_DAY) < startClock || ec.get(Calendar.HOUR_OF_DAY) > endClock ||
                (ec.get(Calendar.MINUTE) > 0 && (ec.get(Calendar.HOUR_OF_DAY) == startClock || ec.get(Calendar.HOUR_OF_DAY) == endClock))) {
            ToastUtils.showShortToast(getActivity(), "比赛时间段为" + startClock + "：00-" + endClock + "：00");
            return false;
        }
        return true;
    }

    /**
     * 获取所有班级(按照低到高年级排序)
     *
     * @return
     */
    public List<ClassInfoItem> getClassItems() {
        if (classItems == null || classItems.size() == 0) {
            classItems = mUpdateClassService.getAllClassInfoItem();
        }
        return classItems;
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_DEFAULT) {
            String url = OnlineServices.getLaunchMatchUrl();
            return new DataAcquirer<OnlineAssignSuccessInfo>().post(url,
                    (String) params[0], new OnlineAssignSuccessInfo());
        } else if (action == ACTION_GET_COST) {
            String url = OnlineServices.getEstimateTimeUrl();
            return new DataAcquirer<OnlineEstimateTimeInfo>().post(url,
                    (String) params[0], new OnlineEstimateTimeInfo());
        }
        return null;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_DEFAULT) {
            OnlineAssignSuccessInfo mOnlineAssignSuccessInfo = (OnlineAssignSuccessInfo) result;
            showMakeOutSuccessFragment(mOnlineAssignSuccessInfo);
            if (mGameType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
                PreferencesController.setLongValue(ConstantsUtils.PREFS_SUBMIT_START, mSDL.getTimeInMillis());
                PreferencesController.setLongValue(ConstantsUtils.PREFS_SUBMIT_END, mEDL.getTimeInMillis());
            }
        } else if (action == ACTION_GET_COST){
            OnlineEstimateTimeInfo estimateTimeInfo = (OnlineEstimateTimeInfo)result;
            mTvTipTestTime.setText("根据所选单词数量和难度以自动设置答题时长" + estimateTimeInfo.roughTime + "分钟");
        }

    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        if (action != ACTION_GET_COST) {
            super.onFail(action, pageNo, result, params);
        }
    }

    /**
     * 年级班级适配器
     */
    private class GradeClassListAdapter extends SingleTypeAdapter<ClassInfoItem> {

        private ArrayList<ClassInfoItem> mSelected = new ArrayList<ClassInfoItem>();

        GradeClassListAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = View.inflate(getActivity(), R.layout.layout_makeout_questions_class_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mGradeTextView = (TextView) convertView.findViewById(R.id.confirm_makequestion_grade);
                viewHolder.mClassTextView = (TextView) convertView.findViewById(R.id.confirm_makequestion_class);
                viewHolder.mSelectView = (ImageView) convertView.findViewById(R.id.icon_class_selected);
                viewHolder.mClassItemView = convertView.findViewById(R.id.confirm_makeout_question_class);
                viewHolder.mClassPhotoView = (ImageView) convertView.findViewById(R.id.confirm_class_photo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final ClassInfoItem classInfoItem = getItem(position);
            viewHolder.mGradeTextView.setText(SubjectUtils.getGradeName(classInfoItem.grade));
            viewHolder.mClassTextView.setText(classInfoItem.className);
            if (classInfoItem.mHeadPhoto != null) {
                ImageFetcher.getImageFetcher().loadImage(classInfoItem.mHeadPhoto,
                        viewHolder.mClassPhotoView, R.drawable.icon_class_default, new RoundDisplayer());
            }

            if (mSelected.contains(classInfoItem)) {
                viewHolder.mGradeTextView.setSelected(true);
                viewHolder.mClassTextView.setSelected(true);
                viewHolder.mSelectView.setSelected(true);
            } else {
                viewHolder.mGradeTextView.setSelected(false);
                viewHolder.mClassTextView.setSelected(false);
                viewHolder.mSelectView.setSelected(false);
            }

            viewHolder.mClassItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!v.isEnabled())
                        return;
                    if (mSelected.contains(classInfoItem)) {
                        mSelected.remove(classInfoItem);
                    } else {
                        mSelected.add(classInfoItem);
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        @Override
        public void setItems(List<ClassInfoItem> items) {
            if (items != null && items.size() == 1) {
                mSelected.add(items.get(0));
            }
            super.setItems(items);
        }


        ArrayList<ClassInfoItem> getSelectedItems() {
            return mSelected;
        }

        private class ViewHolder {
            TextView mGradeTextView;
            TextView mClassTextView;
            ImageView mSelectView;
            View mClassItemView;
            ImageView mClassPhotoView;
        }
    }

    /**
     * 显示出题成功页
     */
    private void showMakeOutSuccessFragment(OnlineAssignSuccessInfo info) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_SUBMIT_COMP, info.competitionItem);
        mBundle.putLong(ConstantsUtils.KEY_SUBMIT_START_DATE, mSDL.getTimeInMillis());
        mBundle.putLong(ConstantsUtils.KEY_SUBMIT_END_DATE, mEDL.getTimeInMillis());
        mBundle.putString(ConstantsUtils.KEY_SUBMIT_JUMPURL, info.jumpUrl);
        mBundle.putSerializable(ConstantsUtils.KEY_ASSIGN_TYPE, mGameType);
        mBundle.putSerializable(ConstantsUtils.KEY_SUBMIT_CLASS_NAMES, getClassNames());

        AssignSuccessFragment fragment = newFragment(getActivity(),
                        AssignSuccessFragment.class, mBundle, AnimType.BOTTOM_TO_TOP);
        showFragment(fragment);

        ActionUtils.notifyMatchChanged();
    }

    private String getClassNames() {
        String classNames = "";
        if (adapter.getSelectedItems() == null || adapter.getSelectedItems().size() == 0) {
            return classNames;
        }
        for (ClassInfoItem item : adapter.getSelectedItems()) {
            if (TextUtils.isEmpty(classNames)) {
                classNames = item.className;
            } else {
                classNames = classNames + "、" + item.className;
            }
        }
        return classNames;
    }

    /**
     * 修改作业名称
     *
     * @param homeworkName
     */
    private void ModifyHomeworkName(String homeworkName, final TextView view) {
        Bundle bundle = new Bundle();
        bundle.putString(AssignNameFragment.NAME, homeworkName);
        bundle.putString(ConstantsUtils.KEY_ASSIGN_TYPE, mGameType);
        AssignNameFragment fragment = (AssignNameFragment) Fragment
                .instantiate(getActivity(), AssignNameFragment.class.getName(), bundle);
        fragment.setOnModifyHomeworkNameListener(new AssignNameFragment.OnModifyHomeworkNameListener() {
            @Override
            public void onUserNameEdit(String newName) {
                view.setText(newName);
            }
        });
        showFragment(fragment);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
    }
}
