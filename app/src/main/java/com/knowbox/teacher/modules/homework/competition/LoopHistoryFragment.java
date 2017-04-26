package com.knowbox.teacher.modules.homework.competition;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.app.widget.AccuracListView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineLoopHistoryInfo;
import com.knowbox.teacher.base.bean.OnlineLoopStudentDetailInfo;
import com.knowbox.teacher.base.bean.OnlineReportStudentListInfo;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.classes.adapter.WordLearnStudentAdapter;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/10.
 */
public class LoopHistoryFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_GET_HISTORY = 1;
    private static final int ACTION_GET_SINGLE = 2;

    private ListView mListView;
    private String matchID;
    private OnlineLoopHistoryInfo mHistoryInfo;
    private OnlineLoopHistoryInfo.OnlineReportInfo mReportInfo;

    private int titleWidth;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        matchID = getArguments().getString(ConstantsUtils.KEY_BUNDLE_MATCHID);
        mReportInfo = (OnlineLoopHistoryInfo.OnlineReportInfo) getArguments().getSerializable(ConstantsUtils.KEY_BUNDLE_MATCH_REPORT);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_loop_history, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("历史榜单");
        mListView = (ListView) view.findViewById(R.id.loop_history_listview);

        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mReportInfo == null) {
                    loadData(ACTION_GET_HISTORY, PAGE_FIRST);
                } else {
                    loadData(ACTION_GET_SINGLE, PAGE_FIRST, matchID, String.valueOf(mReportInfo.date), mReportInfo.type);
                }
            }
        }, 200);

        titleWidth = UIUtils.getWindowWidth(getActivity()) * 260 / 750;
    }

    private void updateData() {
        HistoryAdapter adapter = new HistoryAdapter(getActivity());
        adapter.setItems(mHistoryInfo.mReports);
        mListView.setAdapter(adapter);

        if (adapter.getCount() == 0) {
            getUIFragmentHelper().getEmptyView().showEmpty("还没有历史榜单哦~");
        }
    }

    private void updateSingleData(OnlineReportStudentListInfo result) {
        mReportInfo.mAnswerStudents = result.mStudyStudentList;
        mReportInfo.mUnAnswerStudents = result.mUnStudyStudentList;

        List<OnlineLoopHistoryInfo.OnlineReportInfo> reportInfos = new ArrayList<OnlineLoopHistoryInfo.OnlineReportInfo>();
        reportInfos.add(mReportInfo);
        HistoryAdapter adapter = new HistoryAdapter(getActivity());
        adapter.isSingle = true;
        adapter.setItems(reportInfos);
        mListView.setAdapter(adapter);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_GET_HISTORY) {
            String url = OnlineServices.getReportHistoryListUrl(matchID);
            OnlineLoopHistoryInfo result = new DataAcquirer<OnlineLoopHistoryInfo>().acquire(url, new OnlineLoopHistoryInfo(), -1);
            return result;
        } else if (action == ACTION_GET_SINGLE) {
            String url = OnlineServices.getReportStudentListUrl((String) params[0], (String) params[1], (Integer) params[2]);
            OnlineReportStudentListInfo result = new DataAcquirer<OnlineReportStudentListInfo>().acquire(url, new OnlineReportStudentListInfo(), -1);
            return result;
        }
        return null;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GET_HISTORY) {
            mHistoryInfo = (OnlineLoopHistoryInfo) result;
            updateData();
        } else {
            OnlineReportStudentListInfo reportStudentListInfo = (OnlineReportStudentListInfo) result;
            updateSingleData(reportStudentListInfo);
        }

    }

    class HistoryAdapter extends SingleTypeAdapter<OnlineLoopHistoryInfo.OnlineReportInfo> {

        public HistoryAdapter(Context context) {
            super(context);
        }

        public boolean isSingle;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.layout_loop_history_item, null);

                viewHolder.mDate = (TextView) convertView.findViewById(R.id.loop_history_item_date);
                viewHolder.mReportName = (TextView) convertView.findViewById(R.id.loop_history_item_name);
                viewHolder.mCountView = (TextView) convertView.findViewById(R.id.loop_detail_header_count);
                viewHolder.mRateView = (TextView) convertView.findViewById(R.id.loop_detail_header_rate);
                viewHolder.mScoreView = (TextView) convertView.findViewById(R.id.loop_detail_header_score);
                viewHolder.mScoreRule = (TextView) convertView.findViewById(R.id.loop_detail_header_rule);
                viewHolder.mRateTitle = (TextView) convertView.findViewById(R.id.loop_history_item_rate_title);
                viewHolder.mCountTitle = (TextView) convertView.findViewById(R.id.loop_history_item_count_title);
                viewHolder.mMoreView = (LinearLayout) convertView.findViewById(R.id.loop_history_item_more);
                viewHolder.mScoreLayout = convertView.findViewById(R.id.loop_detail_header_score_layout);
                viewHolder.mHeaderStudent = (TextView) convertView.findViewById(R.id.loop_history_item_header_student);
                viewHolder.mListView = (AccuracListView) convertView.findViewById(R.id.loop_history_item_student_list);

                viewHolder.mUnStudyCount = (TextView) convertView.findViewById(R.id.loop_history_item_unstudy_count);
                viewHolder.mUnStudyGrid = (AccuracGridView) convertView.findViewById(R.id.loop_history_item_unstudy_grid);
                viewHolder.mUnStudyView = convertView.findViewById(R.id.loop_history_item_unstudy);

                viewHolder.mStudent = convertView.findViewById(R.id.loop_detail_header_student);
                viewHolder.mEmpty = convertView.findViewById(R.id.loop_detail_header_empty);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final OnlineLoopHistoryInfo.OnlineReportInfo reportInfo = getItem(position);
            viewHolder.mHeaderStudent.setWidth(titleWidth);

            if (reportInfo.mAnswerStudents != null && reportInfo.mAnswerStudents.size() > 0) {
                viewHolder.mStudent.setVisibility(View.VISIBLE);
                viewHolder.mListView.setVisibility(View.VISIBLE);
                viewHolder.mEmpty.setVisibility(View.GONE);
                viewHolder.mDate.setText(DateUtil.getMonthDay(reportInfo.date));
                viewHolder.mReportName.setText(reportInfo.reportName);
                viewHolder.mCountView.setText(reportInfo.averAnswerCount + "");
                viewHolder.mRateView.setText(reportInfo.averRightRate + "");
                if (reportInfo.type == 1) {
                    viewHolder.mScoreLayout.setVisibility(View.VISIBLE);
                    viewHolder.mScoreView.setText(reportInfo.averControlRate + "");
                    viewHolder.mRateTitle.setVisibility(View.GONE);
                    viewHolder.mCountTitle.setText("熟练程度");
                    viewHolder.mScoreRule.setCompoundDrawables(null, null, null, null);
                } else {
                    viewHolder.mScoreLayout.setVisibility(View.GONE);
                    viewHolder.mRateTitle.setVisibility(View.VISIBLE);
                    viewHolder.mCountTitle.setText("答题数");
                }

                LoopStudentAdapter adapter = new LoopStudentAdapter(getActivity());
                adapter.setItems(reportInfo.mAnswerStudents);
                adapter.setLastRank(reportInfo.type == 1);
                viewHolder.mListView.setAdapter(adapter);

                viewHolder.mMoreView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openMoreFragment(reportInfo);
                    }
                });

                if (isSingle) {
                    viewHolder.mMoreView.setVisibility(View.GONE);
                    if (reportInfo.mUnAnswerStudents != null && reportInfo.mUnAnswerStudents.size() > 0) {
                        viewHolder.mUnStudyView.setVisibility(View.VISIBLE);
                        viewHolder.mUnStudyCount.setText("(" + reportInfo.mUnAnswerStudents.size() + "人)");
                        WordLearnStudentAdapter studentAdapter = new WordLearnStudentAdapter(getActivity());
                        studentAdapter.setItems(reportInfo.mUnAnswerStudents);
                        viewHolder.mUnStudyGrid.setAdapter(studentAdapter);
                    } else {
                        viewHolder.mUnStudyView.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.mUnStudyView.setVisibility(View.GONE);
                    if (reportInfo.hasMore) {
                        viewHolder.mMoreView.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mMoreView.setVisibility(View.GONE);
                    }

                }
            } else {
                viewHolder.mStudent.setVisibility(View.GONE);
                viewHolder.mEmpty.setVisibility(View.VISIBLE);
                viewHolder.mMoreView.setVisibility(View.GONE);
                viewHolder.mUnStudyView.setVisibility(View.GONE);
                viewHolder.mListView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    class LoopStudentAdapter extends SingleTypeAdapter<OnlineStudentInfo> {

        private boolean isLastRank;

        public void setLastRank(boolean lastRank) {
            isLastRank = lastRank;
        }

        public LoopStudentAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_common_student_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mEncourage = (TextView) convertView.findViewById(R.id.student_encourage);
                viewHolder.mRankIndex = (TextView) convertView.findViewById(R.id.student_ranking_index);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.student_name);
                viewHolder.mHead = (ImageView) convertView.findViewById(R.id.student_head_photo);
                viewHolder.mRankImg = (ImageView) convertView.findViewById(R.id.rank_img);
                viewHolder.mStudyCount = (TextView) convertView.findViewById(R.id.student_study_count);
                viewHolder.mStudyScore = (TextView) convertView.findViewById(R.id.student_study_rate);
                viewHolder.mUserView = (LinearLayout) convertView.findViewById(R.id.student_user_layout);
                viewHolder.mDivider = convertView.findViewById(R.id.student_divider);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final OnlineStudentInfo studentInfo = getItem(position);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.mUserView.getLayoutParams();
            params.width = titleWidth;
            viewHolder.mUserView.setLayoutParams(params);

            int rankIndex = position + 1;
            switch (rankIndex) {
                case 1:
                    viewHolder.mRankImg.setVisibility(View.VISIBLE);
                    viewHolder.mRankIndex.setVisibility(View.GONE);
                    viewHolder.mRankImg.setImageResource(R.drawable.icon_rank_first);
                    break;
                case 2:
                    viewHolder.mRankImg.setVisibility(View.VISIBLE);
                    viewHolder.mRankIndex.setVisibility(View.GONE);
                    viewHolder.mRankImg.setImageResource(R.drawable.icon_rank_second);
                    break;
                case 3:
                    viewHolder.mRankImg.setVisibility(View.VISIBLE);
                    viewHolder.mRankIndex.setVisibility(View.GONE);
                    viewHolder.mRankImg.setImageResource(R.drawable.icon_rank_third);
                    break;
                default:
                    viewHolder.mRankImg.setVisibility(View.GONE);
                    viewHolder.mRankIndex.setVisibility(View.VISIBLE);
                    viewHolder.mRankIndex.setText("" + (position + 1));
                    break;
            }

            viewHolder.mEncourage.setVisibility(View.GONE);
            viewHolder.mName.setText(studentInfo.mStudentName);
            ImageFetcher.getImageFetcher().loadImage(studentInfo.mHeadPhoto, viewHolder.mHead,
                    R.drawable.default_img, new RoundDisplayer());

            if (isLastRank) {
                viewHolder.mStudyCount.setText("" + studentInfo.controlRate);
                viewHolder.mStudyScore.setVisibility(View.GONE);
                viewHolder.mStudyCount.setPadding(UIUtils.dip2px(10), 0, 0, 0);
            } else {
                viewHolder.mStudyCount.setText("" + studentInfo.answerCount);
                viewHolder.mStudyScore.setText(studentInfo.rightRate + "%");
                viewHolder.mStudyScore.setVisibility(View.VISIBLE);
                viewHolder.mStudyCount.setPadding(0, 0, 0, 0);
            }

            if (position == getCount() - 1) {
                viewHolder.mDivider.setVisibility(View.GONE);
            } else {
                viewHolder.mDivider.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

    }

    /**
     * 查看更多
     * @param reportInfo
     */
    private void openMoreFragment(OnlineLoopHistoryInfo.OnlineReportInfo reportInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_BUNDLE_MATCH_REPORT, reportInfo);
        mBundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, matchID);
        LoopHistoryFragment fragment = LoopHistoryFragment.newFragment(getActivity(),
                LoopHistoryFragment.class, mBundle);
        showFragment(fragment);
    }

    class ViewHolder {
        public TextView mDate;
        public TextView mReportName;
        public TextView mCountView;
        public TextView mRateView;
        public TextView mScoreView;
        public TextView mScoreRule;
        public View mScoreLayout;
        public TextView mRateTitle;
        public TextView mCountTitle;
        public LinearLayout mMoreView;
        public TextView mHeaderStudent;
        public AccuracListView mListView;


        public ImageView mHead;
        public ImageView mRankImg;
        public TextView mRankIndex;
        public TextView mName;
        public TextView mEncourage;
        public TextView mStudyCount;
        public TextView mStudyScore;
        public View mDivider;
        public LinearLayout mUserView;
        public View mStudent;
        public View mEmpty;

        public TextView mUnStudyCount;
        public AccuracGridView mUnStudyGrid;
        public View mUnStudyView;
    }
}
