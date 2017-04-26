package com.knowbox.teacher.modules.homework.competition;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.app.widget.AccuracListView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineLoopStudentDetailInfo;
import com.knowbox.teacher.base.bean.OnlineReportStudentListInfo;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.base.bean.OnlineWordInfo;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.widgets.PolygonChart;

import java.util.ArrayList;

/**
 * Created by weilei on 17/4/8.
 */
public class LoopStudentInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private String matchID;
    private OnlineStudentInfo mStudentInfo;
    private OnlineReportStudentListInfo mMatchDetailInfo;
    private OnlineLoopStudentDetailInfo mLoopStudentDetailInfo;

    private TextView mCountView;
    private TextView mRateView;
    private TextView mCountTitle;
    private TextView mRateTitle;
    private TextView mScoreView;
    private TextView mTitleView;
    private View mTitleLayout;
    private View mEmptyView;
    private TextView mTypeTitle;
    private LinearLayout mTypeView;
    private AccuracListView mListView;

    private int titleWidth;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        matchID = getArguments().getString(ConstantsUtils.KEY_BUNDLE_MATCHID);
        mStudentInfo = (OnlineStudentInfo) getArguments().getSerializable(ConstantsUtils.KEY_STUDENTINFO);
        mMatchDetailInfo = (OnlineReportStudentListInfo) getArguments().getSerializable(ConstantsUtils.KEY_BUNDLE_MATCH_DETAIL);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_loop_student_info, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle(mStudentInfo.mStudentName + "的练习详情");
        mCountView = (TextView) view.findViewById(R.id.loop_student_info_count);
        mRateView = (TextView) view.findViewById(R.id.loop_student_info_rate);
        mCountTitle = (TextView) view.findViewById(R.id.loop_student_info_count_title);
        mRateTitle = (TextView) view.findViewById(R.id.loop_student_info_rate_title);
        mTypeView = (LinearLayout) view.findViewById(R.id.loop_student_question_type);
        mScoreView = (TextView) view.findViewById(R.id.loop_student_info_score);
        mTypeTitle = (TextView) view.findViewById(R.id.loop_student_question_type_title);
        mTitleView = (TextView) view.findViewById(R.id.loop_student_info_title);
        mEmptyView = view.findViewById(R.id.loop_student_info_empty);
        mTitleLayout = view.findViewById(R.id.loop_student_info_title_layout);
        mListView = (AccuracListView) view.findViewById(R.id.loop_student_info_listview);
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDefaultData(PAGE_FIRST);
            }
        }, 200);

        titleWidth = UIUtils.getWindowWidth(getActivity()) * 200 / 750;
    }

    private void updateData() {
        if (mMatchDetailInfo.isLastRank()) {
            mScoreView.setText(mStudentInfo.controlRate + "");
            mScoreView.setVisibility(View.VISIBLE);
            mCountTitle.setText("总答题数");
            mRateTitle.setText("总正确率");
            mTitleView.setText(DateUtil.getMonthDay(mLoopStudentDetailInfo.startTime) + " - " + DateUtil.getMonthDay(mLoopStudentDetailInfo.endTime) + " 练习情况");
        } else {
            mScoreView.setText("比赛完成后结算");
            mScoreView.setTextSize(11);
            mCountTitle.setText("今日答题数");
            mRateTitle.setText("今日正确率");
            mTitleView.setText(DateUtil.getMonthDay(mMatchDetailInfo.date) + " 练习情况");
        }
        mCountView.setText(mStudentInfo.answerCount + "");
        mRateView.setText(mStudentInfo.rightRate + "");
        mTypeTitle.setWidth(titleWidth);

        if (mLoopStudentDetailInfo.mWords != null && mLoopStudentDetailInfo.mWords.size() > 0) {
            for (int i = 0; i < mLoopStudentDetailInfo.questionTypes.length; i++) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageResource(SubjectUtils.getQuestionTypeIcon(mLoopStudentDetailInfo.questionTypes[i]));
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
                mTypeView.addView(imageView, params);
            }

            StudentDetailAdapter adapter = new StudentDetailAdapter(getActivity());
            adapter.setItems(mLoopStudentDetailInfo.mWords);
            mListView.setAdapter(adapter);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mTitleLayout.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getReportStudentDetailUrl(matchID, String.valueOf(mMatchDetailInfo.date), mMatchDetailInfo.type, mStudentInfo.mStudentId);
        OnlineLoopStudentDetailInfo result = new DataAcquirer<OnlineLoopStudentDetailInfo>()
                .acquire(url, new OnlineLoopStudentDetailInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mLoopStudentDetailInfo = (OnlineLoopStudentDetailInfo) result;
        updateData();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
    }

    class StudentDetailAdapter extends SingleTypeAdapter<OnlineWordInfo> {

        public StudentDetailAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_loop_student_detail_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mWordName = (TextView) convertView.findViewById(R.id.loop_student_detail_item_word);
                viewHolder.mWordIndex = (TextView) convertView.findViewById(R.id.loop_student_detail_item_index);
                viewHolder.mDataLayout = (LinearLayout) convertView.findViewById(R.id.loop_student_detail_item_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final OnlineWordInfo wordInfo = getItem(position);
            viewHolder.mWordIndex.setText("" + (position + 1));
            viewHolder.mWordName.setText(wordInfo.content);
            viewHolder.mWordName.setWidth(titleWidth - UIUtils.dip2px(12));
            viewHolder.mWordName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShortToast(getActivity(), wordInfo.content);
                }
            });

            viewHolder.mDataLayout.removeAllViews();
            for (int i = 0; i < wordInfo.wrongCountArray.length + 1; i++) {
                TextView textView = new TextView(getActivity());
                textView.setTextColor(getResources().getColor(R.color.color_text_primary));
                textView.setTextSize(15);
                textView.setGravity(Gravity.CENTER);

                if (i == 0) {
                    textView.setText(wordInfo.totalWrongCount + "");
                } else {
                    textView.setText(wordInfo.wrongCountArray[i - 1] + "");
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
                viewHolder.mDataLayout.addView(textView, params);
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mWordName;
        public TextView mWordIndex;
        public LinearLayout mDataLayout;
    }
}
