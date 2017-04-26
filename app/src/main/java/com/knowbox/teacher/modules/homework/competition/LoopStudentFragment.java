package com.knowbox.teacher.modules.homework.competition;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.knowbox.base.service.share.ShareContent;
import com.knowbox.base.service.share.ShareService;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineGrantCoinInfo;
import com.knowbox.teacher.base.bean.OnlineReportStudentListInfo;
import com.knowbox.teacher.base.bean.OnlineShareHomeworkInfo;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.services.updateclass.UpdateClassService;
import com.knowbox.teacher.base.utils.ActionUtils;
import com.knowbox.teacher.modules.classes.adapter.WordLearnStudentAdapter;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.ShareClassDialog;
import com.knowbox.teacher.widgets.headerviewpager.InnerListView;
import com.knowbox.teacher.widgets.headerviewpager.InnerScroller;
import com.knowbox.teacher.widgets.headerviewpager.InnerScrollerContainer;
import com.knowbox.teacher.widgets.headerviewpager.OuterScroller;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by weilei on 17/4/6.
 */
public class LoopStudentFragment extends BaseUIFragment<UIFragmentHelper> implements InnerScrollerContainer {

    private final int ACTION_GET_LIST = 1;
    private final int ACTION_GRANT = 2;

    private InnerListView mListView;
    protected OuterScroller mOuterScroller;
    protected int mIndex;
    private TextView mSortView;
    private TextView mHeaderTitle;
    private TextView mHeaderRate;
    private TextView mHeaderCount;

    private OnlineGrantCoinInfo mOnlineGrantCoinInfo;
    private ClassInfoItem mClassInfoItem;
    private OnlineReportStudentListInfo reportStudentListInfo;
    private LoopStudentAdapter mAdapter;
    private boolean isSortHigh = true;
    private String matchID;
    private int titleWidth;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
        matchID = getArguments().getString(ConstantsUtils.KEY_BUNDLE_MATCHID);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_class_item_list, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mListView = (InnerListView) view.findViewById(R.id.listView);
        mListView.register2Outer(mOuterScroller, mIndex);
        mListView.setDividerHeight(0);

        loadData(ACTION_GET_LIST, PAGE_FIRST, matchID);

        titleWidth = UIUtils.getWindowWidth(getActivity()) * 260 / 750;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.loop_student_header_sort:
                    sortData();
                    break;
            }
        }
    };

    private void sortData() {
        isSortHigh = !isSortHigh;
        List<OnlineStudentInfo> studentInfos = new ArrayList<OnlineStudentInfo>();
        studentInfos.addAll(reportStudentListInfo.mStudyStudentList);
        if (!isSortHigh) {
            Collections.reverse(studentInfos);
        }
        if (reportStudentListInfo.mUnStudyStudentList != null && reportStudentListInfo.mUnStudyStudentList.size() > 0) {
            OnlineStudentInfo studentInfo = new OnlineStudentInfo();
            studentInfo.isUnstudy = true;
            studentInfo.unStudyList = reportStudentListInfo.mUnStudyStudentList;
            studentInfos.add(studentInfo);
        }
        mAdapter.setItems(studentInfos);
        updateSortView();
    }

    private void updateStudentList() {
        if (reportStudentListInfo != null) {
            if (mOnDataLoadListener != null) {
                mOnDataLoadListener.onDataLoad(reportStudentListInfo);
            }
            if ((reportStudentListInfo.mStudyStudentList == null || reportStudentListInfo.mStudyStudentList.size() == 0) &&
                    (reportStudentListInfo.mUnStudyStudentList == null || reportStudentListInfo.mUnStudyStudentList.size() == 0)) {
                showStudentEmpty();
                return;
            }

            List<OnlineStudentInfo> studentInfos = new ArrayList<OnlineStudentInfo>();
            if (reportStudentListInfo.mStudyStudentList != null && reportStudentListInfo.mStudyStudentList.size() > 0) {
                View header = View.inflate(getActivity(), R.layout.layout_loop_student_header, null);
                mSortView = (TextView) header.findViewById(R.id.loop_student_header_sort);
                mHeaderTitle = (TextView) header.findViewById(R.id.loop_student_header_title);
                mHeaderRate = (TextView) header.findViewById(R.id.loop_student_header_rate);
                mHeaderCount = (TextView) header.findViewById(R.id.loop_student_header_count);
                mSortView.setOnClickListener(mOnClickListener);
                mListView.addHeaderView(header);
                header.findViewById(R.id.loop_student_header_student).setMinimumWidth(titleWidth);
                studentInfos.addAll(reportStudentListInfo.mStudyStudentList);

                if (reportStudentListInfo.isLastRank()) {
                    mHeaderCount.setText("练熟程度");
                    mHeaderRate.setVisibility(View.GONE);
                    mHeaderTitle.setText("练熟程度排行榜");
                }

                updateSortView();
            }

            mAdapter = new LoopStudentAdapter(getActivity());
            mListView.setAdapter(mAdapter);

            if (reportStudentListInfo.mUnStudyStudentList != null && reportStudentListInfo.mUnStudyStudentList.size() > 0) {
                OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                studentInfo.isUnstudy = true;
                studentInfo.unStudyList = reportStudentListInfo.mUnStudyStudentList;
                studentInfos.add(studentInfo);
            }
            mAdapter.setItems(studentInfos);


            if (mClassInfoItem == null) {
                UpdateClassService service = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
                mClassInfoItem = service.getClassInfoItem(reportStudentListInfo.classId);
            }

        } else {
            showStudentEmpty();
        }
    }

    private void updateSortView() {
        if (mSortView == null) {
            return;
        }
        if (isSortHigh) {
            mSortView.setText("从高到低");
        } else {
            mSortView.setText("从低到高");
        }
        if (isSortHigh) {
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.icon_sort_high_to_low);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mSortView.setCompoundDrawables(drawable, null, null, null);
            mSortView.setCompoundDrawablePadding(UIUtils.dip2px(3));
        } else {
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.icon_sort_low_to_high);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mSortView.setCompoundDrawables(drawable, null, null, null);
            mSortView.setCompoundDrawablePadding(UIUtils.dip2px(3));
        }
    }

    private void grantStudent(OnlineStudentInfo studentInfo) {
        loadData(ACTION_GRANT, PAGE_MORE, studentInfo);
    }

    private void showStudentEmpty() {
        if (mClassInfoItem == null && reportStudentListInfo != null) {
            UpdateClassService service = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
            mClassInfoItem = service.getClassInfoItem(reportStudentListInfo.classId);
        }
        if (mAdapter == null) {
            mAdapter = new LoopStudentAdapter(getActivity());
            mListView.setAdapter(mAdapter);
        }
        mAdapter.setEmpty(true);
    }

    private void openStudentInfo(OnlineStudentInfo studentInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_STUDENTINFO, studentInfo);
        mBundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, matchID);
        mBundle.putSerializable(ConstantsUtils.KEY_BUNDLE_MATCH_DETAIL, reportStudentListInfo);
        LoopStudentInfoFragment fragment = LoopStudentInfoFragment.newFragment(getActivity(),
                LoopStudentInfoFragment.class, mBundle);
        showFragment(fragment);
    }

    private void showShareDialog() {
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(ConstantsUtils.KEY_CLASSINFOITEM, mClassInfoItem);
        ShareClassDialog fragment = ShareClassDialog.newFragment(getActivity(),
                ShareClassDialog.class, mBundle, AnimType.ANIM_NONE);
        showFragment(fragment);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        if (action == ACTION_GRANT) {
            String url = OnlineServices.getMatchDailyRewardtUrl();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("class_id", mClassInfoItem.classId);
                jsonObject.put("student_id", ((OnlineStudentInfo) params[0]).mStudentId);
                jsonObject.put("teacher_id", Utils.getLoginUserItem().userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data = jsonObject.toString();
            OnlineGrantCoinInfo result = new DataAcquirer<OnlineGrantCoinInfo>().post(url, data, new OnlineGrantCoinInfo());
            return result;
        } else if (action == ACTION_GET_LIST) {
            String url = OnlineServices.getReportStudentListUrl((String) params[0], "", -1);
            OnlineReportStudentListInfo result = new DataAcquirer<OnlineReportStudentListInfo>().acquire(url, new OnlineReportStudentListInfo(), -1);
            return result;
        }
        return null;

    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GRANT) {
            mOnlineGrantCoinInfo = (OnlineGrantCoinInfo) result;
            ((OnlineStudentInfo) params[0]).isGrant = true;
            mAdapter.notifyDataSetChanged();
            ToastUtils.showShortToast(getActivity(), "鼓励成功");
        } else {
            reportStudentListInfo = (OnlineReportStudentListInfo) result;
            updateStudentList();
        }
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        if (action == ACTION_GET_LIST) {
            if (mOnDataLoadListener != null) {
                mOnDataLoadListener.onDataLoad(null);
            }
            showContent();
            showStudentEmpty();
        } else {
            super.onFail(action, pageNo, result, params);
        }

    }

    class LoopStudentAdapter extends SingleTypeAdapter<OnlineStudentInfo> {
        private boolean isEmpty;

        public LoopStudentAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_loop_student_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mEncourage = (TextView) convertView.findViewById(R.id.student_encourage);
                viewHolder.mRankIndex = (TextView) convertView.findViewById(R.id.student_ranking_index);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.student_name);
                viewHolder.mHead = (ImageView) convertView.findViewById(R.id.student_head_photo);
                viewHolder.mRankImg = (ImageView) convertView.findViewById(R.id.rank_img);
                viewHolder.mStudyCount = (TextView) convertView.findViewById(R.id.student_study_count);
                viewHolder.mStudyScore = (TextView) convertView.findViewById(R.id.student_study_rate);
                viewHolder.mEmptyView = convertView.findViewById(R.id.student_empty);
                viewHolder.mRankView = convertView.findViewById(R.id.student_ranking);
                viewHolder.mEmptyAdd = (TextView) convertView.findViewById(R.id.student_empty_add);
                viewHolder.mEmptyDesc = (TextView) convertView.findViewById(R.id.student_empty_desc);
                viewHolder.mEmptyCode = (TextView) convertView.findViewById(R.id.student_empty_code);
                viewHolder.mEmptyImg = (ImageView) convertView.findViewById(R.id.student_empty_img);
                viewHolder.mUserView = (LinearLayout) convertView.findViewById(R.id.student_user_layout);

                viewHolder.mUnStudyCount = (TextView) convertView.findViewById(R.id.student_unstudy_count);
                viewHolder.mUnStudyGrid = (AccuracGridView) convertView.findViewById(R.id.student_unstudy_gridview);
                viewHolder.mUnStudyView = convertView.findViewById(R.id.student_unstudy);
                viewHolder.mUnstudyTitle = (TextView) convertView.findViewById(R.id.student_unstudy_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (getEmpty()) {
                viewHolder.mEmptyView.setVisibility(View.VISIBLE);
                viewHolder.mRankView.setVisibility(View.GONE);
                viewHolder.mUnStudyView.setVisibility(View.GONE);
                if (reportStudentListInfo != null && mClassInfoItem != null && mClassInfoItem.studentNum == 0) {
                    viewHolder.mEmptyCode.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyImg.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyCode.setText("学生在学生端输入班级部落号\"" + reportStudentListInfo.classCode + "\"加入");
                    viewHolder.mEmptyDesc.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyAdd.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showShareDialog();
                        }
                    });
                } else {
                    viewHolder.mEmptyCode.setText("获取数据失败");
                    viewHolder.mEmptyDesc.setVisibility(View.GONE);
                    viewHolder.mEmptyAdd.setVisibility(View.GONE);
                }
            } else {
                final OnlineStudentInfo studentInfo = getItem(position);
                if (studentInfo.isFooter) {
                    viewHolder.mEmptyView.setVisibility(View.VISIBLE);
                    viewHolder.mUnStudyView.setVisibility(View.GONE);
                    viewHolder.mRankView.setVisibility(View.GONE);
                    viewHolder.mEmptyImg.setVisibility(View.GONE);
                    viewHolder.mEmptyDesc.setVisibility(View.GONE);
                    viewHolder.mEmptyCode.setVisibility(View.GONE);
                    viewHolder.mEmptyAdd.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showShareDialog();
                        }
                    });
                } else if (studentInfo.isUnstudy) {
                    viewHolder.mEmptyView.setVisibility(View.GONE);
                    viewHolder.mRankView.setVisibility(View.GONE);
                    viewHolder.mUnStudyView.setVisibility(View.VISIBLE);
                    viewHolder.mUnStudyCount.setText("(" + studentInfo.unStudyList.size() + "人)");

                    if (reportStudentListInfo.isUnBegining()) {
                        viewHolder.mUnstudyTitle.setText("待参赛学生");
                    } else {
                        viewHolder.mUnstudyTitle.setText("未参赛学生");
                    }

                    WordLearnStudentAdapter adapter = new WordLearnStudentAdapter(getActivity());
                    adapter.setItems(studentInfo.unStudyList);
                    viewHolder.mUnStudyGrid.setAdapter(adapter);
                } else {
                    viewHolder.mEmptyView.setVisibility(View.GONE);
                    viewHolder.mRankView.setVisibility(View.VISIBLE);
                    viewHolder.mUnStudyView.setVisibility(View.GONE);
                    int rankIndex = studentInfo.rankIndex;
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
                            viewHolder.mRankIndex.setText("" + rankIndex);
                            break;
                    }

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.mUserView.getLayoutParams();
                    params.width = titleWidth;
                    viewHolder.mUserView.setLayoutParams(params);

                    viewHolder.mEncourage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            grantStudent(studentInfo);
                        }
                    });

                    viewHolder.mEncourage.setEnabled(!studentInfo.isGrant);
                    if (studentInfo.isGrant) {
                        viewHolder.mEncourage.setText("已鼓励");
                    } else {
                        viewHolder.mEncourage.setText("鼓励TA");
                    }

                    viewHolder.mName.setText(studentInfo.mStudentName);
                    ImageFetcher.getImageFetcher().loadImage(studentInfo.mHeadPhoto, viewHolder.mHead,
                            R.drawable.default_img, new RoundDisplayer());

                    if (reportStudentListInfo.isLastRank()) {
                        viewHolder.mStudyCount.setText("" + studentInfo.controlRate);
                        viewHolder.mStudyScore.setVisibility(View.GONE);
                    } else {
                        viewHolder.mStudyCount.setText("" + studentInfo.answerCount);
                        viewHolder.mStudyScore.setText(studentInfo.rightRate + "%");
                        viewHolder.mStudyScore.setVisibility(View.VISIBLE);
                    }
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openStudentInfo(studentInfo);
                        }
                    });
                }
            }
            return convertView;
        }

        public boolean getEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
            notifyDataSetChanged();
        }

        @Override
        public void setItems(List<OnlineStudentInfo> items) {
            isEmpty = false;
            super.setItems(items);
        }

        @Override
        public int getCount() {
            if (getEmpty()) {
                return 1;
            }
            return super.getCount();
        }
    }

    class ViewHolder {
        public ImageView mHead;
        public ImageView mRankImg;
        public TextView mRankIndex;
        public TextView mName;
        public TextView mEncourage;
        public TextView mStudyCount;
        public TextView mStudyScore;
        public View mRankView;
        public LinearLayout mUserView;

        public View mEmptyView;
        public TextView mEmptyCode;
        public TextView mEmptyDesc;
        public TextView mEmptyAdd;
        public ImageView mEmptyImg;

        public TextView mUnStudyCount;
        public TextView mUnstudyTitle;
        public AccuracGridView mUnStudyGrid;
        public View mUnStudyView;
    }

    @Override
    public void setOuterScroller(OuterScroller outerScroller, int myPosition) {
        if (outerScroller == mOuterScroller && myPosition == mIndex) {
            return;
        }
        mOuterScroller = outerScroller;
        mIndex = myPosition;

        if (getInnerScroller() != null) {
            getInnerScroller().register2Outer(mOuterScroller, mIndex);
        }
    }

    @Override
    public InnerScroller getInnerScroller() {
        return mListView;
    }

    private OnDataLoadListener mOnDataLoadListener;

    public void setOnDataLoadListener(OnDataLoadListener listener) {
        mOnDataLoadListener = listener;
    }

    public interface OnDataLoadListener {
        public void onDataLoad(OnlineReportStudentListInfo reportStudentListInfo);
    }
}
