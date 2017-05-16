package com.buang.welewolf.modules.homework.competition;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.ToastUtils;
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineStudentInfo;
import com.buang.welewolf.base.bean.OnlineTestStudentDetailInfo;
import com.buang.welewolf.base.bean.OnlineWordInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.SubjectUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.widgets.RoundDisplayer;

/**
 * Created by liuYu on 17/4/14.
 */
public class SingleTestStudentInfoFragment extends BaseUIFragment<UIFragmentHelper> {

    private String matchID;
    private OnlineStudentInfo mStudentInfo;
    private OnlineTestStudentDetailInfo mStudentDetailInfo;

    private ImageView mHeadPhoto;
    private TextView mName;
    private TextView mScore;
    private StudentDetailAdapter adapter;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        matchID = getArguments().getString(ConstantsUtils.KEY_BUNDLE_MATCHID);
        mStudentInfo = (OnlineStudentInfo) getArguments().getSerializable(ConstantsUtils.KEY_STUDENTINFO);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_singletest_student_info, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("学生测验详情");
        mHeadPhoto = (ImageView) view.findViewById(R.id.student_head_photo);
        mName = (TextView) view.findViewById(R.id.student_name);
        mScore = (TextView) view.findViewById(R.id.student_score);
        ListView mListView = (ListView) view.findViewById(R.id.student_words_listview);
        adapter = new StudentDetailAdapter(getActivity());
        mListView.setAdapter(adapter);
        loadDefaultData(PAGE_FIRST);
    }

    private void updateData() {
        ImageFetcher.getImageFetcher()
                .loadImage(mStudentInfo.mHeadPhoto, mHeadPhoto, R.drawable.default_img, new RoundDisplayer());
        mName.setText(mStudentInfo.mStudentName);
        mScore.setText(mStudentInfo.score);
        if (mStudentDetailInfo.mWords != null) {
            adapter.setItems(mStudentDetailInfo.mWords);
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getSingletestStudentDetailUrl(matchID, mStudentInfo.mStudentId);
        return new DataAcquirer<OnlineTestStudentDetailInfo>().acquire(url, new OnlineTestStudentDetailInfo(), -1);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mStudentDetailInfo = (OnlineTestStudentDetailInfo) result;
        updateData();
    }

    private SpannableString getSpannableString(String source, int colorid) {
        SpannableString sp = new SpannableString(source);
        sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), colorid)),
                source.indexOf("("), source.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.NORMAL), source.indexOf("("), source.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new AbsoluteSizeSpan(11, true), source.indexOf("("), source.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    class StudentDetailAdapter extends SingleTypeAdapter<OnlineWordInfo> {

        public StudentDetailAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_singletest_student_word_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mWordContent = (TextView) convertView.findViewById(R.id.word_content);
                viewHolder.mWordIndex = (TextView) convertView.findViewById(R.id.index);
                viewHolder.mWordDimIcon = (ImageView) convertView.findViewById(R.id.dim_icon);
                viewHolder.mDivider = convertView.findViewById(R.id.grid_divider);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final OnlineWordInfo wordInfo = getItem(position);

            viewHolder.mWordIndex.setText("" + (position + 1));
            viewHolder.mWordDimIcon.setImageResource(SubjectUtils.getKnowledgeDimIcon(wordInfo.knowledgeDim));
            if (wordInfo.isRight) {
                viewHolder.mWordContent.setTextColor(getResources().getColor(R.color.color_text_main));
            } else {
                viewHolder.mWordContent.setTextColor(getResources().getColor(R.color.color_button_red));
            }
            String contentStr = wordInfo.content;
            if (!wordInfo.isRight && wordInfo.isRevise == 0) {//未订正
                viewHolder.mWordContent.setText(getSpannableString(contentStr + " (未订正)", R.color.color_button_red));
            } else if (!wordInfo.isRight && wordInfo.isRevise == 1) {//已订正
                viewHolder.mWordContent.setText(getSpannableString(contentStr + " (已订正)", R.color.color_text_83d368));
            } else {
                viewHolder.mWordContent.setText(contentStr);
            }
            viewHolder.mWordContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShortToast(mContext, wordInfo.content);
                }
            });

            if (position <= 0) {
                viewHolder.mDivider.setVisibility(View.GONE);
            } else {
                viewHolder.mDivider.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mWordContent;
        public TextView mWordIndex;
        public ImageView mWordDimIcon;
        public View mDivider;
    }
}
