package com.buang.welewolf.modules.homework.competition;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.base.utils.StringUtils;
import com.buang.welewolf.widgets.headerviewpager.InnerScrollerContainer;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.knowbox.base.utils.UIUtils;
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineStudentInfo;
import com.buang.welewolf.base.bean.OnlineTestStudentListInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.updateclass.UpdateClassService;
import com.buang.welewolf.modules.classes.adapter.WordLearnStudentAdapter;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.widgets.ShareClassDialog;
import com.buang.welewolf.widgets.headerviewpager.InnerListView;
import com.buang.welewolf.widgets.headerviewpager.InnerScroller;
import com.buang.welewolf.widgets.headerviewpager.OuterScroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LiuYu on 17/4/12.
 */
public class SingleTestStudentFragment extends BaseUIFragment<UIFragmentHelper> implements InnerScrollerContainer {

    private final int ACTION_GET_LIST = 1;

    private InnerListView mListView;
    protected OuterScroller mOuterScroller;
    protected int mIndex;
    private TextView mSortView;

    private ClassInfoItem mClassInfoItem;
    private OnlineTestStudentListInfo studentListInfo;
    private LoopStudentAdapter mAdapter;
    private boolean isSortHightoLow = true;
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

        loadData(ACTION_GET_LIST, PAGE_FIRST);

        titleWidth = UIUtils.getWindowWidth(getActivity()) * 300 / 750;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.singletest_student_header_sort:
                    sortData();
                    break;
            }
        }
    };

    private void sortData() {
        if (studentListInfo == null || (studentListInfo.mJoinList == null && studentListInfo.mSupplementList == null )) {
            return;
        }
        isSortHightoLow = !isSortHightoLow;
        List<OnlineStudentInfo> studentInfos = new ArrayList<OnlineStudentInfo>();
        if (studentListInfo.mJoinList != null) {
            List<OnlineStudentInfo> joinInfos = new ArrayList<OnlineStudentInfo>();
            joinInfos.addAll(studentListInfo.mJoinList);
            if (!isSortHightoLow) {
                Collections.reverse(joinInfos);
            }
            studentInfos.addAll(joinInfos);
        }
        if (studentListInfo.mSupplementList != null) {
            List<OnlineStudentInfo> supplementInfos = new ArrayList<OnlineStudentInfo>();
            studentListInfo.mSupplementList.get(studentListInfo.mSupplementList.size() - 1).isShowTitle = false;
            studentListInfo.mSupplementList.get(0).isShowTitle = true;
            supplementInfos.addAll(studentListInfo.mSupplementList);
            if (!isSortHightoLow) {
                supplementInfos.get(0).isShowTitle = false;
                Collections.reverse(supplementInfos);
                supplementInfos.get(0).isShowTitle = true;
            }
            studentInfos.addAll(supplementInfos);
        }
        if (studentListInfo.mUnJoinList != null && studentListInfo.mUnJoinList.size() > 0) {
            OnlineStudentInfo studentInfo = new OnlineStudentInfo();
            studentInfo.isUnstudy = true;
            studentInfo.unStudyList = studentListInfo.mUnJoinList;
            studentInfos.add(studentInfo);
        }
        mAdapter.setItems(studentInfos);
        updateSortView();
    }

    private void updateStudentList() {
        if (studentListInfo != null) {
            if ((studentListInfo.mJoinList == null || studentListInfo.mJoinList.size() == 0) &&
                    (studentListInfo.mUnJoinList == null || studentListInfo.mUnJoinList.size() == 0) &&
                    (studentListInfo.mSupplementList == null || studentListInfo.mSupplementList.size() == 0)) {
                showStudentEmpty();
                return;
            }

            if ((studentListInfo.mJoinList != null && studentListInfo.mJoinList.size() > 0) ||
                    (studentListInfo.mSupplementList != null && studentListInfo.mSupplementList.size() > 0)) {
                View header = View.inflate(getActivity(), R.layout.layout_singletest_student_header, null);
                mSortView = (TextView) header.findViewById(R.id.singletest_student_header_sort);

                mSortView.setOnClickListener(mOnClickListener);
                mListView.addHeaderView(header);
                header.findViewById(R.id.singletest_student_header_student).setMinimumWidth(titleWidth);
                updateSortView();
            }

            mAdapter = new LoopStudentAdapter(getActivity());
            mListView.setAdapter(mAdapter);

            List<OnlineStudentInfo> studentInfos = new ArrayList<OnlineStudentInfo>();
            if (studentListInfo.mJoinList != null && studentListInfo.mJoinList.size() > 0) {
                studentInfos.addAll(studentListInfo.mJoinList);
            }
            if (studentListInfo.mSupplementList != null && studentListInfo.mSupplementList.size() > 0) {
                studentListInfo.mSupplementList.get(0).isShowTitle = true;
                studentInfos.addAll(studentListInfo.mSupplementList);
            }
            if (studentListInfo.mUnJoinList != null && studentListInfo.mUnJoinList.size() > 0) {
                OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                studentInfo.isUnstudy = true;
                studentInfo.unStudyList = studentListInfo.mUnJoinList;
                studentInfos.add(studentInfo);
            }
            if (mClassInfoItem == null) {
                UpdateClassService service = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
                mClassInfoItem = service.getClassInfoItem(studentListInfo.classId);
            }
            mAdapter.setItems(studentInfos);

            if (mOnDataLoadListener != null) {
                mOnDataLoadListener.onDataLoad(studentListInfo);
            }
        } else {
            showStudentEmpty();
        }
    }

    private void updateSortView() {
        if (null != mSortView) {
            if (isSortHightoLow) {
                mSortView.setText("分数从高到低");
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.icon_sort_high_to_low);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mSortView.setCompoundDrawables(drawable, null, null, null);
                mSortView.setCompoundDrawablePadding(UIUtils.dip2px(3));
            } else {
                mSortView.setText("分数从低到高");
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.icon_sort_low_to_high);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mSortView.setCompoundDrawables(drawable, null, null, null);
                mSortView.setCompoundDrawablePadding(UIUtils.dip2px(3));
            }
        }
    }

    private void showStudentEmpty() {
        if (mClassInfoItem == null) {
            UpdateClassService service = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
            mClassInfoItem = service.getClassInfoItem(studentListInfo.classId);
        }
        if (mAdapter == null) {
            mAdapter = new LoopStudentAdapter(getActivity());
            mListView.setAdapter(mAdapter);
        }
        mAdapter.setEmpty(true);
        mAdapter.notifyDataSetChanged();
        if (mOnDataLoadListener != null) {
            mOnDataLoadListener.onDataLoad(studentListInfo);
        }
    }

    private void openStudentInfo(OnlineStudentInfo studentInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_STUDENTINFO, studentInfo);
        mBundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, matchID);
        showFragment(newFragment(getActivity(), SingleTestStudentInfoFragment.class, mBundle));
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
        if (action == ACTION_GET_LIST) {
            String url = OnlineServices.getSingletestStudentListUrl(matchID);
            return new DataAcquirer<OnlineTestStudentListInfo>().acquire(url, new OnlineTestStudentListInfo(), -1);
        }
        return null;

    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GET_LIST) {
            studentListInfo = (OnlineTestStudentListInfo) result;
            updateStudentList();
        }
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        if (action == ACTION_GET_LIST) {
            showContent();
            showStudentEmpty();
            if (mOnDataLoadListener != null) {
                mOnDataLoadListener.onDataLoad(null);
            }
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
                convertView = View.inflate(getActivity(), R.layout.layout_singletest_student_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mRankIndex = (TextView) convertView.findViewById(R.id.student_ranking_index);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.student_name);
                viewHolder.mHead = (ImageView) convertView.findViewById(R.id.student_head_photo);
                viewHolder.mRankImg = (ImageView) convertView.findViewById(R.id.rank_img);
                viewHolder.mTimeUsed = (TextView) convertView.findViewById(R.id.student_time_used);
                viewHolder.mScore = (TextView) convertView.findViewById(R.id.student_score);
                viewHolder.mTimeoutLayout = (RelativeLayout) convertView.findViewById(R.id.student_outtime_layout);
                viewHolder.mTimeoutCount = (TextView) convertView.findViewById(R.id.student_outtime_count);
                viewHolder.mEmptyView = convertView.findViewById(R.id.student_empty);
                viewHolder.mRankView = convertView.findViewById(R.id.student_ranking);
                viewHolder.mEmptyAdd = (TextView) convertView.findViewById(R.id.student_empty_add);
                viewHolder.mEmptyDesc = (TextView) convertView.findViewById(R.id.student_empty_desc);
                viewHolder.mEmptyCode = (TextView) convertView.findViewById(R.id.student_empty_code);
                viewHolder.mEmptyImg = (ImageView) convertView.findViewById(R.id.student_empty_img);
                viewHolder.mUserView = (LinearLayout) convertView.findViewById(R.id.student_user_layout);

                viewHolder.mUnJoinCount = (TextView) convertView.findViewById(R.id.student_unjoin_count);
                viewHolder.mUnJoinGrid = (AccuracGridView) convertView.findViewById(R.id.student_unjoin_gridview);
                viewHolder.mUnJoinView = convertView.findViewById(R.id.student_unjoin);
                viewHolder.mUnJoinDesc = (TextView) convertView.findViewById(R.id.student_unjoin_desc);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (isEmpty()) {
                viewHolder.mEmptyView.setVisibility(View.VISIBLE);
                viewHolder.mRankView.setVisibility(View.GONE);
                viewHolder.mUnJoinView.setVisibility(View.GONE);
                if (mClassInfoItem.studentNum == 0) {
                    viewHolder.mEmptyCode.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyImg.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyCode.setText("学生可通过群号\"" + mClassInfoItem.classCode + "\"加入");
                    viewHolder.mEmptyDesc.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyAdd.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showShareDialog();
                        }
                    });
                } else {
                    viewHolder.mEmptyImg.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyCode.setVisibility(View.VISIBLE);
                    viewHolder.mEmptyCode.setText("获取数据失败");
                    viewHolder.mEmptyDesc.setVisibility(View.GONE);
                    viewHolder.mEmptyAdd.setVisibility(View.GONE);
                }
            } else {
                final OnlineStudentInfo studentInfo = getItem(position);
                if (studentInfo.isFooter) {
                    viewHolder.mEmptyView.setVisibility(View.VISIBLE);
                    viewHolder.mUnJoinView.setVisibility(View.GONE);
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
                    viewHolder.mUnJoinView.setVisibility(View.VISIBLE);
                    if (studentListInfo.isNoStart()) {
                        viewHolder.mUnJoinDesc.setText("待测验学生");
                    }else {
                        viewHolder.mUnJoinDesc.setText("未测验学生");
                    }
                    viewHolder.mUnJoinCount.setText("(" + studentInfo.unStudyList.size() + "人)");
                    WordLearnStudentAdapter adapter = new WordLearnStudentAdapter(getActivity());
                    adapter.setItems(studentInfo.unStudyList);
                    viewHolder.mUnJoinGrid.setAdapter(adapter);
                } else {
                    viewHolder.mEmptyView.setVisibility(View.GONE);
                    viewHolder.mRankView.setVisibility(View.VISIBLE);
                    viewHolder.mUnJoinView.setVisibility(View.GONE);
                    if (studentInfo.isShowTitle) {
                        viewHolder.mTimeoutLayout.setVisibility(View.VISIBLE);
                        viewHolder.mTimeoutCount.setText("(" + studentListInfo.mSupplementList.size() + "人)");
                    } else {
                        viewHolder.mTimeoutLayout.setVisibility(View.GONE);
                    }

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

                    viewHolder.mTimeUsed.setText(StringUtils.getSecondByMS(studentInfo.timeUsed));
                    if (studentInfo.isRevise == 0) {//未订正
                        viewHolder.mScore.setText(getSpannableString(studentInfo.score + " (未订正 " + studentInfo.errorCount + ")", R.color.color_button_red));
                    } else if (studentInfo.isRevise == 1) {//已订正
                        viewHolder.mScore.setText(getSpannableString(studentInfo.score + " (已订正)", R.color.color_text_83d368));
                    } else if (studentInfo.isRevise == 2) {//无订正
                        viewHolder.mScore.setText(studentInfo.score + "");
                    } else {
                        viewHolder.mScore.setText(studentInfo.score + "");
                    }

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.mUserView.getLayoutParams();
                    params.width = titleWidth;
                    viewHolder.mUserView.setLayoutParams(params);

                    viewHolder.mName.setText(studentInfo.mStudentName);
                    ImageFetcher.getImageFetcher().loadImage(studentInfo.mHeadPhoto, viewHolder.mHead,
                            R.drawable.default_img, new RoundDisplayer());

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

        public boolean isEmpty() {
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
            if (isEmpty()) {
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
        public TextView mTimeUsed;
        public TextView mScore;
        public View mRankView;
        public LinearLayout mUserView;
        public RelativeLayout mTimeoutLayout;
        public TextView mTimeoutCount;

        public View mEmptyView;
        public TextView mEmptyCode;
        public TextView mEmptyDesc;
        public TextView mEmptyAdd;
        public ImageView mEmptyImg;

        public TextView mUnJoinCount;
        public AccuracGridView mUnJoinGrid;
        public View mUnJoinView;
        public TextView mUnJoinDesc;
    }

    private SpannableString getSpannableString(String source, int colorId) {
        SpannableString sp = new SpannableString(source);
        sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), colorId)),
                source.indexOf("("), source.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(11, true), source.indexOf("("), source.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
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
        public void onDataLoad(OnlineTestStudentListInfo reportStudentListInfo);
    }
}
