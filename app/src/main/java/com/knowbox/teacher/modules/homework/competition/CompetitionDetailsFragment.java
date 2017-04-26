package com.knowbox.teacher.modules.homework.competition;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UIUtils;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineCompetitionDetailsInfo;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;

import java.util.List;

/**
 * Created by LiuYu on 2017/2/17.
 */
public class CompetitionDetailsFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_GET_DETAILS = 1;
    private static final int ACTION_DELETE_MATCH = 2;

    private LinearLayout mMasterStudents;
    private AccuracGridView mGridView;
    private LinearLayout.LayoutParams layoutParams;
    private StudentAdapter mAdapter;
    private LinearLayout mMoreStudentLayout;
    private LinearLayout mNoJoinStudentEmpty;
    private String matchId;
    private String title;
    private String timeDesc;
    private TextView learnedWordCount;
    private TextView totalWordCount;
    private String className;
    private String studentCount;
    private OnlineCompetitionDetailsInfo detailsInfo;
    private int matchStatus;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        if (getArguments() != null) {
            matchId = getArguments().getString(ConstantsUtils.KEY_BUNDLE_MATCHID);
            title = getArguments().getString(ConstantsUtils.KEY_BUNDLE_TTTLE);
            timeDesc = getArguments().getString(ConstantsUtils.KEY_BUNDLE_TIME);
            className = getArguments().getString(ConstantsUtils.KEY_BUNDLE_CLASSNAME);
            studentCount = getArguments().getString(ConstantsUtils.KEY_BUNDLE_STUDENTCOUNT);
            matchStatus = getArguments().getInt(ConstantsUtils.KEY_BUNDLE_STATUS);
        }
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(),R.layout.layout_comptition_details, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {

        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle(title);
        if (matchStatus == ConstantsUtils.MATCH_STATUS_NOTSTART) {
            getUIFragmentHelper().getTitleBar().setRightMoreTxt("删除比赛", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete match
                    showDeleteCompetitionDialog();
                }
            });
        }
        ((TextView)view.findViewById(R.id.competition_time)).setText(timeDesc + "\n" + className);


        learnedWordCount = (TextView) view.findViewById(R.id.competition_word_master_count);
        totalWordCount = (TextView) view.findViewById(R.id.competition_word_count);

        mMasterStudents = (LinearLayout) view.findViewById(R.id.student_masterlist);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(106));

        mMoreStudentLayout = (LinearLayout) view.findViewById(R.id.competition_student_more);
        mNoJoinStudentEmpty = (LinearLayout) view.findViewById(R.id.nojion_student_empty);
        mMoreStudentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == detailsInfo || detailsInfo.mJoinList.size() <= 3) return;
                UmengConstant.reportUmengEvent(UmengConstant.EVENT_COMPETITION_LOOK_STUDENT_RANK, null);
                Bundle bundle = new Bundle();
                bundle.putSerializable("detailsInfo", detailsInfo);
                showFragment(CompetitionRankFragment.newFragment(getActivity(), CompetitionRankFragment.class, bundle));
            }
        });

        view.findViewById(R.id.word_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengConstant.reportUmengEvent(UmengConstant.EVENT_COMPETITION_LOOK_WORDS, null);
                Bundle bundle = getArguments();
                showFragment(CompetitionWordsFragment.newFragment(getActivity(), CompetitionWordsFragment.class, bundle));
            }
        });

        mGridView = (AccuracGridView) view.findViewById(R.id.student_nojoin_list);
        mAdapter = new StudentAdapter(getActivity());
        mGridView.setAdapter(mAdapter);

        if (!TextUtils.isEmpty(matchId)) {
            loadData(ACTION_GET_DETAILS, PAGE_FIRST);
        }

    }

    private void showDeleteCompetitionDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getNewMessageDialog(getActivity(),
                R.drawable.icon_dialog_message, "", "删除比赛后将不可撤销，\n是否确定删除",
                "取消", "确定", new DialogUtils.OnDialogButtonClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                            deleteCompetition();
                        }
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
    }

    private void deleteCompetition() {
        loadData(ACTION_DELETE_MATCH, PAGE_MORE);
    }

    private void setParticipantList(List<OnlineStudentInfo> studentInfos) {
        if (studentInfos.size() <= 3) {
            mMoreStudentLayout.setVisibility(View.GONE);
        }else {
            mMoreStudentLayout.setVisibility(View.VISIBLE);
        }
        mMasterStudents.removeAllViews();
        mMasterStudents.setBackgroundColor(getResources().getColor(R.color.color_main_background));
        mMasterStudents.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mMasterStudents.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < studentInfos.size(); i++) {
            if (i >= 3) {
                break;
            }
            View itemView = View.inflate(getActivity(), R.layout.layout_competition_student_item, null);
            ImageView rankImg = (ImageView) itemView.findViewById(R.id.rank_img);
            ImageView headPhoto = (ImageView) itemView.findViewById(R.id.student_head_photo);
            TextView studentName = (TextView) itemView.findViewById(R.id.student_name);
            TextView score = (TextView) itemView.findViewById(R.id.score);
            TextView masterWordCount = (TextView) itemView.findViewById(R.id.master_word_count);

            OnlineStudentInfo itemInfo = studentInfos.get(i);
            if (i == 0) {
                rankImg.setImageResource(R.drawable.icon_rank_first);
            }else if (i == 1) {
                rankImg.setImageResource(R.drawable.icon_rank_second);
            }else if (i == 2) {
                rankImg.setImageResource(R.drawable.icon_rank_third);
            }

            ImageFetcher.getImageFetcher().loadImage(itemInfo.mHeadPhoto, headPhoto, R.drawable.default_img, new RoundDisplayer());
            studentName.setText(itemInfo.mStudentName);
            score.setText(itemInfo.score);
            masterWordCount.setText(itemInfo.learnedCount + "");


            if ((studentInfos.size() == 1 && i == 0) || studentInfos.size() == 2 && i == 1 ||
                    (studentInfos.size() >= 3 && i >= 2)) {
                itemView.findViewById(R.id.divider_line_buttom).setVisibility(View.GONE);
            }
            mMasterStudents.addView(itemView, layoutParams);
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        if (action == ACTION_GET_DETAILS) {
            String url = OnlineServices.getCompetitionDetailsUrl(matchId);
            return new DataAcquirer<OnlineCompetitionDetailsInfo>()
                    .acquire(url, new OnlineCompetitionDetailsInfo(), -1);
        }else if (action == ACTION_DELETE_MATCH){
            String url = OnlineServices.getCompetitionDeleteUrl(matchId);
            return new DataAcquirer<BaseObject>().get(url, new BaseObject());
        }
        return null;
    }

    private void updateDetailsInfo(OnlineCompetitionDetailsInfo result) {
        detailsInfo = result;
        if (result == null ) return;
        if (!TextUtils.isEmpty(result.wordLearnedCount) && !TextUtils.isEmpty(result.wordCount)) {
            learnedWordCount.setText(result.wordLearnedCount);
            totalWordCount.setText("/" + result.wordCount);
        }

        if (studentCount.equals("0")){
            mNoJoinStudentEmpty.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
            mMoreStudentLayout.setVisibility(View.GONE);
            getUIFragmentHelper().getEmptyView().setEmptyMargin(204);
            getUIFragmentHelper().getEmptyView().setEmptyBg(getResources().getColor(R.color.white));
            getUIFragmentHelper().getEmptyView().showEmpty(R.drawable.icon_empty_nodata, "该班级暂无学生");
            return;
        }

        if (result.mJoinList != null && !result.mJoinList.isEmpty()) {
            setParticipantList(result.mJoinList);
        }else {
            setParticipantEmpty();
        }

        if (result.mUnJoinList != null && !result.mUnJoinList.isEmpty()) {
            mAdapter.setItems(result.mUnJoinList);
            mNoJoinStudentEmpty.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
        }else {
            mNoJoinStudentEmpty.setVisibility(View.VISIBLE);
            TextView textView = (TextView) mNoJoinStudentEmpty.findViewById(R.id.unjoin_empty_text);
            if (!TextUtils.isEmpty(studentCount) && studentCount.equals("0")) {
                textView.setText("部落中暂时没有斗士加入");
            }else {
                textView.setText("全员已参赛");
            }
            mGridView.setVisibility(View.GONE);
        }
    }

    private void setParticipantEmpty() {
        mMoreStudentLayout.setVisibility(View.GONE);
        mMasterStudents.removeAllViews();
        mMasterStudents.setBackgroundColor(getResources().getColor(R.color.white));
        mMasterStudents.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(138)));
        mMasterStudents.setOrientation(LinearLayout.VERTICAL);
        mMasterStudents.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.icon_empty_nodata);

        TextView textView = new TextView(getActivity());
        textView.setText("暂无学生参赛");
        textView.setTextColor(getResources().getColor(R.color.color_text_primary));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setPadding(0, UIUtils.dip2px(5), 0, 0);

        mMasterStudents.addView(imageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mMasterStudents.addView(textView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GET_DETAILS) {
            updateDetailsInfo((OnlineCompetitionDetailsInfo) result);
        }else if (action == ACTION_DELETE_MATCH){
            if (null != deleteMatchListener) {
                deleteMatchListener.deleteSuccess(matchId);
                finish();
            }
        }
    }


    class StudentAdapter extends SingleTypeAdapter<OnlineStudentInfo> {

        public StudentAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.layout_student_gridview_item, null);

                viewHolder.mHeadPhoto = (ImageView) convertView.findViewById(R.id.student_head_photo);
                viewHolder.mStudentName = (TextView) convertView.findViewById(R.id.student_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OnlineStudentInfo studentInfo = getItem(position);
            viewHolder.mStudentName.setText(studentInfo.mStudentName);
            ImageFetcher.getImageFetcher()
                    .loadImage(studentInfo.mHeadPhoto, viewHolder.mHeadPhoto, R.drawable.default_img, new RoundDisplayer());
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mStudentName;
        public ImageView mHeadPhoto;
    }

    public interface OnDeleteMatchListener {
        public abstract void deleteSuccess(String matchId);
    }

    private OnDeleteMatchListener deleteMatchListener;

    public void setOnDeleteMatchLisenter(OnDeleteMatchListener lisenter) {
        this.deleteMatchListener = lisenter;
    }
}
