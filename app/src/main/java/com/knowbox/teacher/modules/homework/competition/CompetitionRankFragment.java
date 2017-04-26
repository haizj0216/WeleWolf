package com.knowbox.teacher.modules.homework.competition;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UIUtils;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineCompetitionDetailsInfo;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

/**
 * Created by LiuYu on 2017/2/18.
 */
public class CompetitionRankFragment extends BaseUIFragment<UIFragmentHelper> {

    private ListView mListView;
    private StudentRankAdapter mAdapter;
    private OnlineCompetitionDetailsInfo detailsInfo;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        detailsInfo = (OnlineCompetitionDetailsInfo) getArguments().getSerializable("detailsInfo");
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(),R.layout.layout_competition_rank, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("答题排行榜");

        mListView = (ListView) view.findViewById(R.id.competition_studentrank_list);
        mAdapter = new StudentRankAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        if (detailsInfo != null && detailsInfo.mJoinList.size() > 0) {
            mAdapter.setItems(detailsInfo.mJoinList);
        }
    }

    class StudentRankAdapter extends SingleTypeAdapter<OnlineStudentInfo> {
        private RelativeLayout.LayoutParams layoutParams;

        public StudentRankAdapter(Context context) {
            super(context);
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(106));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.layout_competition_student_item, null);
                viewHolder.mItemRoot = (RelativeLayout) convertView.findViewById(R.id.ranking_item);
                viewHolder.mRankImg = (ImageView) convertView.findViewById(R.id.rank_img);
                viewHolder.mRankText = (TextView) convertView.findViewById(R.id.rank_text);
                viewHolder.mHeadPhoto = (ImageView) convertView.findViewById(R.id.student_head_photo);
                viewHolder.mStudentName = (TextView) convertView.findViewById(R.id.student_name);
                viewHolder.mScore = (TextView) convertView.findViewById(R.id.score);
                viewHolder.mMasterWordCount = (TextView) convertView.findViewById(R.id.master_word_count);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            OnlineStudentInfo studentInfo = getItem(position);
            viewHolder.mItemRoot.setLayoutParams(layoutParams);
            if (position == 0) {
                viewHolder.mRankImg.setVisibility(View.VISIBLE);
                viewHolder.mRankText.setVisibility(View.GONE);
                viewHolder.mRankImg.setImageResource(R.drawable.icon_rank_first);
            }else if (position == 1) {
                viewHolder.mRankImg.setVisibility(View.VISIBLE);
                viewHolder.mRankText.setVisibility(View.GONE);
                viewHolder.mRankImg.setImageResource(R.drawable.icon_rank_second);
            }else if (position == 2) {
                viewHolder.mRankImg.setVisibility(View.VISIBLE);
                viewHolder.mRankText.setVisibility(View.GONE);
                viewHolder.mRankImg.setImageResource(R.drawable.icon_rank_third);
            }else {
                viewHolder.mRankImg.setVisibility(View.GONE);
                viewHolder.mRankText.setVisibility(View.VISIBLE);
                viewHolder.mRankText.setText(position + 1 + "");
            }

            ImageFetcher.getImageFetcher()
                    .loadImage(studentInfo.mHeadPhoto, viewHolder.mHeadPhoto, R.drawable.default_img, new RoundDisplayer());
            viewHolder.mStudentName.setText(studentInfo.mStudentName);
            viewHolder.mScore.setText(studentInfo.score);
            viewHolder.mMasterWordCount.setText(studentInfo.learnedCount + "");

            return convertView;
        }
    }

    class ViewHolder {
        public RelativeLayout mItemRoot;
        public TextView mRankText;
        public ImageView mRankImg;
        public TextView mStudentName;
        public ImageView mHeadPhoto;
        public TextView mScore;
        public TextView mMasterWordCount;
    }
}
