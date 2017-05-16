package com.buang.welewolf.modules.classes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineWordInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.widgets.WaveView;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.base.bean.OnlineUnitInfo;
import com.buang.welewolf.base.bean.OnlineUnitWordListInfo;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.UmengConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/14.
 */
public class UnitWordListFragment extends BaseUIFragment<UIFragmentHelper> {

    private AccuracGridView mHeaderGrid;
    private WordListAdapter mAdapter;
    private OnlineUnitInfo mUnitInfo;
    private ClassInfoItem mClassInfoItem;
    private WaveView mWaveView;
    private TextView mLearnedCount;
    private TextView mWordCount;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
        mUnitInfo = (OnlineUnitInfo) getArguments().getSerializable(ConstantsUtils.KEY_UNITINFO);
        mClassInfoItem = getArguments().getParcelable(ConstantsUtils.KEY_CLASSINFOITEM);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_unit_word_list, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getLoadingView().showLoading();
        mHeaderGrid = (AccuracGridView) view.findViewById(com.buang.welewolf.R.id.unit_word_grid);
        mAdapter = new WordListAdapter(getActivity());

        mWaveView = (WaveView) view.findViewById(com.buang.welewolf.R.id.circle_progress);
        TextView ruleView = (TextView) view.findViewById(com.buang.welewolf.R.id.unit_header_rule);
        mWordCount = (TextView) view.findViewById(com.buang.welewolf.R.id.unit_header_total);
        mLearnedCount = (TextView) view.findViewById(com.buang.welewolf.R.id.unit_header_learned);
        ruleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRuleDialog();
            }
        });
        mWordCount.setText("/" + mUnitInfo.wordsCount);
        mLearnedCount.setText("" + mUnitInfo.learnedCount);

        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDefaultData(PAGE_FIRST, mClassInfoItem.classId, mUnitInfo.mUnitID, Utils.getLoginUserItem().mBookId);
            }
        }, 200);
        mHeaderGrid.setOnItemClickListener(onItemClickListener);
        mHeaderGrid.setAdapter(mAdapter);
    }

    private void initData() {
        showContent();
        List<OnlineWordInfo> wordInfos = new ArrayList<OnlineWordInfo>();
        for (int i = 0; i < 11; i++) {
            OnlineWordInfo wordInfo = new OnlineWordInfo();
            wordInfo.content = "Word" + i;
            wordInfo.studentCount = 20;
            wordInfo.learnedCount = 10 + i;
            wordInfos.add(wordInfo);
        }
        mAdapter.setItems(wordInfos);
        int progress = mUnitInfo.learnedCount * 100 / mUnitInfo.wordsCount;
        mWaveView.setProgress(progress);
    }

    private void showRuleDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        String msg = "答题正确，增加熟练度\n" +
                "答题错误，减少熟练度\n" +
                "达到所需熟练度，为已练熟单词\n" +
                "\n此为[该单元所有单词已练熟]的学生数量";
        mDialog = DialogUtils.getNewMessageDialog(getActivity(), com.buang.welewolf.R.drawable.icon_dialog_rule, "" ,msg, "", "确定", new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_UNIT_WORD_CARD, null);
            OnlineWordInfo wordInfo = mAdapter.getItem(position);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(ConstantsUtils.KEY_WORDINFO, wordInfo);
            mBundle.putParcelable(ConstantsUtils.KEY_CLASSINFOITEM, mClassInfoItem);
            WordLearnInfoFragment fragment = newFragment(getActivity(), WordLearnInfoFragment.class
                    , mBundle);
            showFragment(fragment);
        }
    };

    private void updateWordList(OnlineUnitWordListInfo result) {
        mWordCount.setText("/" + result.wordsCount);
        mLearnedCount.setText("" + result.wordslearnedCount);
        int progress = 0;
        if (result.wordsCount != 0) {
            progress = result.wordslearnedCount * 100 / result.wordsCount;
        }
        mWaveView.setProgress(progress);
        mAdapter.setItems(result.mWordInfos);
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        String url = OnlineServices.getUnitWordsUrl((String) params[0], (String) params[2], (String) params[1]);
        return new UrlModelPair(url, new OnlineUnitWordListInfo());
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getUnitWordsUrl((String) params[0], (String) params[2], (String) params[1]);
        OnlineUnitWordListInfo result = new DataAcquirer<OnlineUnitWordListInfo>().acquire(url, new OnlineUnitWordListInfo(), -1);
        return result;
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        updateWordList((OnlineUnitWordListInfo) result);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        updateWordList((OnlineUnitWordListInfo) result);
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
    }

    class WordListAdapter extends SingleTypeAdapter<OnlineWordInfo> {

        public WordListAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_unit_word_item, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);

                viewHolder.mWordName = (TextView) convertView.findViewById(com.buang.welewolf.R.id.unit_word_item_name);
                viewHolder.mWordDesc = (TextView) convertView.findViewById(com.buang.welewolf.R.id.unit_word_item_desc);
                viewHolder.mWordLayout = (LinearLayout) convertView.findViewById(com.buang.welewolf.R.id.unit_word_item_layout);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OnlineWordInfo wordInfo = getItem(position);
            viewHolder.mWordName.setText(wordInfo.content);

            if (wordInfo.learnedCount == wordInfo.studentCount && wordInfo.studentCount != 0) {
                viewHolder.mWordLayout.setBackgroundResource(com.buang.welewolf.R.drawable.icon_word_learn_bg);
                viewHolder.mWordDesc.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_main_app));
                viewHolder.mWordDesc.setText("全员已练熟");
                viewHolder.mWordDesc.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                viewHolder.mWordDesc.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                if (wordInfo.studentCount == 0) {
                    viewHolder.mWordDesc.setText("人数 " + wordInfo.learnedCount + "/" + wordInfo.studentCount);
                } else {
                    String text = "已练熟人数 " + wordInfo.learnedCount + "/" + wordInfo.studentCount;
                    SpannableString sp = new SpannableString(text);
                    StyleSpan span = new StyleSpan(Typeface.BOLD);
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(com.buang.welewolf.R.color.color_text_main));
                    sp.setSpan(span, 5, text.indexOf("/"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(colorSpan, 5, text.indexOf("/"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.mWordDesc.setText(sp);
                }
                viewHolder.mWordLayout.setBackgroundResource(com.buang.welewolf.R.drawable.icon_word_bg);
                viewHolder.mWordDesc.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_primary));

            }
            return convertView;
        }
    }

    class ViewHolder {
        public LinearLayout mWordLayout;
        public TextView mWordName;
        public TextView mWordDesc;
    }
}
