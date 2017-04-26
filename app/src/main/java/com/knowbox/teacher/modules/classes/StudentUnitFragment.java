package com.knowbox.teacher.modules.classes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.base.bean.OnlineStudentUnitListInfo;
import com.knowbox.teacher.base.bean.OnlineUnitInfo;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.widgets.PinnedScrollView;
import com.knowbox.teacher.widgets.WaveView;

import java.util.ArrayList;

/**
 * Created by weilei on 17/2/16.
 */
public class StudentUnitFragment extends BaseUIFragment<UIFragmentHelper> {

    private ImageView mHead;
    private TextView mName;
    private TextView mLearned;
    private TextView mLearning;
    private TextView mWeekLearned;
    private TextView mWeekLearning;
    private TextView mLastLearned;
    private TextView mLastLearning;
    private TextView mBookView;
    private AccuracGridView mGridView;
    private ClassInfoItem mClassInfoItem;
    private OnlineStudentInfo mStudentInfo;
    private StudentUnitAdapter mAdapter;
    private PinnedScrollView mScrollView;
    private TextView mPinnedName;

    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        setTitleStyle(STYLE_NO_TITLE);
        mClassInfoItem = getArguments().getParcelable(ConstantsUtils.KEY_CLASSINFOITEM);
        mStudentInfo = (OnlineStudentInfo) getArguments().getSerializable(ConstantsUtils.KEY_STUDENTINFO);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_student_unit_info, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mHead = (ImageView) view.findViewById(R.id.student_unit_header_head);
        mName = (TextView) view.findViewById(R.id.student_unit_header_name);
        mLearned = (TextView) view.findViewById(R.id.student_unit_header_learned);
        mLearning = (TextView) view.findViewById(R.id.student_unit_header_learning);
        mWeekLearned = (TextView) view.findViewById(R.id.student_unit_header_learned_week);
        mWeekLearning = (TextView) view.findViewById(R.id.student_unit_header_learning_week);
        mLastLearned = (TextView) view.findViewById(R.id.student_unit_header_learned_lastday);
        mLastLearning = (TextView) view.findViewById(R.id.student_unit_header_learning_lastday);
        mGridView = (AccuracGridView) view.findViewById(R.id.student_unit_list);
        mBookView = (TextView) view.findViewById(R.id.student_unit_book);
        view.findViewById(R.id.student_unit_header_back).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.student_unit_header_learned_rule).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.student_unit_header_learning_rule).setOnClickListener(mOnClickListener);

        mScrollView = (PinnedScrollView) view.findViewById(R.id.student_unit_scrollview);
        View pinnedView = View.inflate(getActivity(), R.layout.layout_student_unit_pinned_header, null);
        pinnedView.findViewById(R.id.student_unit_header_pinned_back).setOnClickListener(mOnClickListener);
        mPinnedName = (TextView) pinnedView.findViewById(R.id.student_unit_header_pinned_name);
        mScrollView.setPinnedView(pinnedView);
        mScrollView.markPinnedViewStartLocationRef(mName);

        mName.setText(mStudentInfo.mStudentName);
        mPinnedName.setText(mStudentInfo.mStudentName);
        ImageFetcher.getImageFetcher().loadImage(mStudentInfo.mHeadPhoto, mHead, R.drawable.default_img, new RoundDisplayer());

        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDefaultData(PAGE_FIRST, mClassInfoItem.classId, Utils.getLoginUserItem().mBookId,
                        mStudentInfo.mStudentId, Utils.getLoginUserItem().mEditionId);
            }
        }, 200);

        mBookView.setText("- " + Utils.getLoginUserItem().mBookName + Utils.getLoginUserItem().mEditionName + " -");
        mAdapter = new StudentUnitAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
    }

    private void showRuleDialog(int type) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        String desc;
        if (type ==0) {
            desc = "答题正确，增加熟练度\n答题错误，减少熟练度\n达到所需熟练度，为已练熟单词";
        } else {
            desc = "已练习，未达所需熟练度\n" +
                    "为练习中的单词";
        }
        mDialog = DialogUtils.getNewMessageDialog(getActivity(),R.drawable.icon_dialog_rule, "", desc, "",  "确定",
                new DialogUtils.OnDialogButtonClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
    }

    private void initData() {
        showContent();
        OnlineStudentUnitListInfo result = new OnlineStudentUnitListInfo();
        result.studentName = "王大锤";
        result.learnedCount = 100;
        result.learningCount = 50;
        result.weekLearned = 20;
        result.weekLearning = 10;
        result.lastDayLearned = 9;
        result.lastDayLearning = 8;

        result.mUnitInfos = new ArrayList<OnlineUnitInfo>();
        for (int i = 0; i < 11; i++) {
            OnlineUnitInfo unitInfo = new OnlineUnitInfo();
            unitInfo.mUnitName = "Unit" + i;
            unitInfo.wordsCount = 20;
            unitInfo.learnedCount = 10 + i;
            result.mUnitInfos.add(unitInfo);
        }

        updateUnitList(result);
    }

    private void updateUnitList(OnlineStudentUnitListInfo result) {
        mLearned.setText(result.learnedCount + "");
        mLearning.setText(result.learningCount + "");
        mWeekLearned.setText(result.weekLearned + "");
        mWeekLearning.setText(result.weekLearning + "");
        mLastLearned.setText(result.lastDayLearned + "");
        mLastLearning.setText(result.lastDayLearning + "");

        int learnedUp = result.weekLearned;
        mWeekLearned.setText("" + learnedUp);
        Drawable learnDb = null;
        if (learnedUp > 0) {
            learnDb = getResources().getDrawable(R.drawable.icon_student_rank_up);
            learnDb.setBounds(0, 0, learnDb.getIntrinsicWidth(), learnDb.getIntrinsicHeight());
            mWeekLearned.setTextColor(getResources().getColor(R.color.color_button_red));
        } else if (learnedUp < 0) {
            learnDb = getResources().getDrawable(R.drawable.icon_student_rank_down);
            learnDb.setBounds(0, 0, learnDb.getIntrinsicWidth(), learnDb.getIntrinsicHeight());
            mWeekLearned.setTextColor(getResources().getColor(R.color.color_main_app));
        } else {
            mWeekLearned.setText("-");
            mWeekLearned.setTextColor(getResources().getColor(R.color.color_text_primary));
        }
        mWeekLearned.setCompoundDrawables(learnDb, null, null, null);

        int learningUp = result.weekLearning;
        mWeekLearning.setText("" + learningUp);
        Drawable learningDb = null;
        if (learningUp > 0) {
            learningDb = getResources().getDrawable(R.drawable.icon_student_rank_up);
            learningDb.setBounds(0, 0, learningDb.getIntrinsicWidth(), learningDb.getIntrinsicHeight());
            mWeekLearning.setTextColor(getResources().getColor(R.color.color_button_red));
        } else if (learningUp < 0) {
            learningDb = getResources().getDrawable(R.drawable.icon_student_rank_down);
            learningDb.setBounds(0, 0, learningDb.getIntrinsicWidth(), learningDb.getIntrinsicHeight());
            mWeekLearning.setTextColor(getResources().getColor(R.color.color_main_app));
        } else {
            mWeekLearning.setText("-");
            mWeekLearning.setTextColor(getResources().getColor(R.color.color_text_primary));
        }
        mWeekLearning.setCompoundDrawables(learningDb, null, null, null);

        int learnedLastUp = result.lastDayLearned;
        mLastLearned.setText("" + learnedLastUp);
        Drawable learnLastDb = null;
        if (learnedLastUp > 0) {
            learnLastDb = getResources().getDrawable(R.drawable.icon_student_rank_up);
            learnLastDb.setBounds(0, 0, learnLastDb.getIntrinsicWidth(), learnLastDb.getIntrinsicHeight());
            mLastLearned.setTextColor(getResources().getColor(R.color.color_button_red));
        } else if (learnedLastUp < 0) {
            learnLastDb = getResources().getDrawable(R.drawable.icon_student_rank_down);
            learnLastDb.setBounds(0, 0, learnLastDb.getIntrinsicWidth(), learnLastDb.getIntrinsicHeight());
            mLastLearned.setTextColor(getResources().getColor(R.color.color_main_app));
        } else {
            mLastLearned.setText("-");
            mLastLearned.setTextColor(getResources().getColor(R.color.color_text_primary));
        }
        mLastLearned.setCompoundDrawables(learnLastDb, null, null, null);

        int learningLastUp = result.lastDayLearning;
        mLastLearning.setText("" + learningLastUp);
        Drawable learningLastDb = null;
        if (learningLastUp > 0) {
            learningLastDb = getResources().getDrawable(R.drawable.icon_student_rank_up);
            learningLastDb.setBounds(0, 0, learningLastDb.getIntrinsicWidth(), learningLastDb.getIntrinsicHeight());
            mLastLearning.setTextColor(getResources().getColor(R.color.color_button_red));
        } else if (learningLastUp < 0) {
            learningLastDb = getResources().getDrawable(R.drawable.icon_student_rank_down);
            learningLastDb.setBounds(0, 0, learningLastDb.getIntrinsicWidth(), learningLastDb.getIntrinsicHeight());
            mLastLearning.setTextColor(getResources().getColor(R.color.color_main_app));
        } else {
            mLastLearning.setText("-");
            mLastLearning.setTextColor(getResources().getColor(R.color.color_text_primary));
        }
        mLastLearning.setCompoundDrawables(learningLastDb, null, null, null);

        mAdapter.setItems(result.mUnitInfos);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.student_unit_header_back:
                    finish();
                    break;
                case R.id.student_unit_header_pinned_back:
                    finish();
                    break;
                case R.id.student_unit_header_learned_rule:
                    showRuleDialog(0);
                    break;
                case R.id.student_unit_header_learning_rule:
                    showRuleDialog(1);
                    break;
            }
        }
    };

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        String url = OnlineServices.getStudentUnitUrl((String) params[0], (String) params[1], (String) params[2], (String) params[3]);
        return new UrlModelPair(url, new OnlineStudentUnitListInfo());
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getStudentUnitUrl((String) params[0], (String) params[1], (String) params[2], (String) params[3]);
        OnlineStudentUnitListInfo result = new DataAcquirer<OnlineStudentUnitListInfo>().acquire(url, new OnlineStudentUnitListInfo(), -1);
        return result;
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        updateUnitList((OnlineStudentUnitListInfo) result);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        updateUnitList((OnlineStudentUnitListInfo) result);
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
    }

    class StudentUnitAdapter extends SingleTypeAdapter<OnlineUnitInfo> {

        public StudentUnitAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_student_unit_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mUnitName = (TextView) convertView.findViewById(R.id.student_unit_item_name);
                viewHolder.mWaveView = (WaveView) convertView.findViewById(R.id.circle_progress);
                viewHolder.mLearned = (TextView) convertView.findViewById(R.id.student_unit_item_learned);
                viewHolder.mWordsCount = (TextView) convertView.findViewById(R.id.student_unit_item_wordscount);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            OnlineUnitInfo unitInfo = getItem(position);
            viewHolder.mUnitName.setText(unitInfo.mUnitName);
            int progress = unitInfo.learnedCount * 100 / unitInfo.wordsCount;
            viewHolder.mWaveView.setProgress(progress);

            viewHolder.mLearned.setText("" + unitInfo.learnedCount);
            viewHolder.mWordsCount.setText("/" + unitInfo.wordsCount);
            return convertView;
        }
    }

    class ViewHolder {
        public WaveView mWaveView;
        public TextView mUnitName;
        public TextView mLearned;
        public TextView mWordsCount;
    }
}
