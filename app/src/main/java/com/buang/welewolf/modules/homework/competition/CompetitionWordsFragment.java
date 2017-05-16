package com.buang.welewolf.modules.homework.competition;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineCompetitionWordsInfo;
import com.buang.welewolf.base.bean.OnlineWordInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.classes.WordLearnInfoFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.UmengConstant;

/**
 * Created by LiuYu on 2017/2/20.
 */
public class CompetitionWordsFragment extends BaseUIFragment<UIFragmentHelper> {

    private AccuracGridView mUnmasterWordList;
    private AccuracGridView mMasterWordList;
    private TextView mUnmasterWords;
    private TextView mMasterWords;
    private WordListAdapter mUnmasterAdapter;
    private WordListAdapter mMasterAdapter;
    private String matchId;
    private String classID;
    private ClassInfoItem classInfoItem;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        if (getArguments() != null) {
            matchId = getArguments().getString(ConstantsUtils.KEY_BUNDLE_MATCHID);
            classID = getArguments().getString(ConstantsUtils.KEY_BUNDLE_CLASSID);
        }
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_comptition_words, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("比赛单词详情");
        mUnmasterWordList = (AccuracGridView) view.findViewById(R.id.unmaster_words);
        mMasterWordList = (AccuracGridView) view.findViewById(R.id.master_words);
        mUnmasterWords = (TextView) view.findViewById(R.id.unmaster_words_count);
        mMasterWords = (TextView) view.findViewById(R.id.master_words_count);
        mUnmasterAdapter = new WordListAdapter(getActivity());
        mMasterAdapter = new WordListAdapter(getActivity());
        mUnmasterWordList.setAdapter(mUnmasterAdapter);
        mMasterWordList.setAdapter(mMasterAdapter);

        if (!TextUtils.isEmpty(matchId) && !TextUtils.isEmpty(classID)) {
            loadDefaultData(PAGE_FIRST);
        }

    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        String url = OnlineServices.getCompetitionWordsUrl(matchId, classID);
        return new UrlModelPair(url, new OnlineCompetitionWordsInfo());
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getCompetitionWordsUrl(matchId, classID);
        return new DataAcquirer<OnlineCompetitionWordsInfo>()
                .acquire(url, new OnlineCompetitionWordsInfo(), -1);
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        updateDetailsInfo((OnlineCompetitionWordsInfo) result);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        updateDetailsInfo((OnlineCompetitionWordsInfo) result);
    }

    private void updateDetailsInfo(OnlineCompetitionWordsInfo result) {
        classInfoItem = result.classInfoItem;
        if (result.mMasterList != null && !result.mMasterList.isEmpty()) {
            mMasterWordList.setVisibility(View.VISIBLE);
            mMasterWords.setVisibility(View.VISIBLE);
            mMasterAdapter.setItems(result.mMasterList);
            mMasterWords.setText("-  已练熟单词 " + result.mMasterList.size() + "  -");
        }else {
            mMasterWordList.setVisibility(View.GONE);
            mMasterWords.setVisibility(View.GONE);
        }
        if (result.mUnMasterList != null && !result.mUnMasterList.isEmpty()) {
            mUnmasterWordList.setVisibility(View.VISIBLE);
            mUnmasterWords.setVisibility(View.VISIBLE);
            mUnmasterAdapter.setItems(result.mUnMasterList);
            mUnmasterWords.setText("-  练习中单词 " + result.mUnMasterList.size() + "  -");
        }else {
            mUnmasterWordList.setVisibility(View.GONE);
            mUnmasterWords.setVisibility(View.GONE);
        }

    }


    private void openWordInfo(OnlineWordInfo wordInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_WORDINFO, wordInfo);
        mBundle.putParcelable(ConstantsUtils.KEY_CLASSINFOITEM, classInfoItem);
        WordLearnInfoFragment fragment = WordLearnInfoFragment.newFragment(getActivity(),
                WordLearnInfoFragment.class, mBundle);
        showFragment(fragment);
    }

    class WordListAdapter extends SingleTypeAdapter<OnlineWordInfo> {

        public WordListAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.layout_unit_word_item, null);
                viewHolder.mWordName = (TextView) convertView.findViewById(R.id.unit_word_item_name);
                viewHolder.mWordDesc = (TextView) convertView.findViewById(R.id.unit_word_item_desc);
                viewHolder.mWordLayout = (LinearLayout) convertView.findViewById(R.id.unit_word_item_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final OnlineWordInfo wordInfo = getItem(position);
            viewHolder.mWordName.setText(wordInfo.content);

            if (wordInfo.isLearned()) {
                viewHolder.mWordLayout.setBackgroundResource(R.drawable.icon_word_learn_bg);
                viewHolder.mWordDesc.setTextColor(getResources().getColor(R.color.color_main_app));
                viewHolder.mWordDesc.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                viewHolder.mWordDesc.setText("全员已练熟");
            } else {
                viewHolder.mWordLayout.setBackgroundResource(R.drawable.icon_word_bg);
                viewHolder.mWordDesc.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                viewHolder.mWordDesc.setTextColor(getResources().getColor(R.color.color_text_primary));
                if (wordInfo.studentCount == 0) {
                    viewHolder.mWordDesc.setText("人数 " + wordInfo.learnedCount + "/" + wordInfo.studentCount);
                } else {
                    String text = "已练熟人数 " + wordInfo.learnedCount + "/" + wordInfo.studentCount;
                    SpannableString sp = new SpannableString(text);
                    StyleSpan span = new StyleSpan(Typeface.BOLD);
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.color_text_main));
                    sp.setSpan(span, 5, text.indexOf("/"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(colorSpan, 5, text.indexOf("/"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.mWordDesc.setText(sp);
                }

            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_COMPETITION_LOOK_WORD_DETAILS, null);
                    openWordInfo(wordInfo);
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        public LinearLayout mWordLayout;
        public TextView mWordName;
        public TextView mWordDesc;
    }

}
