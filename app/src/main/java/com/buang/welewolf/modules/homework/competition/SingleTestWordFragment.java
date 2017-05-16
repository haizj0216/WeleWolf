package com.buang.welewolf.modules.homework.competition;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buang.welewolf.widgets.headerviewpager.InnerScrollerContainer;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.utils.UIUtils;
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineLoopWordListInfo;
import com.buang.welewolf.base.bean.OnlineWordInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.widgets.headerviewpager.InnerListView;
import com.buang.welewolf.widgets.headerviewpager.InnerScroller;
import com.buang.welewolf.widgets.headerviewpager.OuterScroller;

import java.util.List;

/**
 * Created by LiuYu on 17/4/12.
 */
public class SingleTestWordFragment extends BaseUIFragment<UIFragmentHelper> implements InnerScrollerContainer {

    private InnerListView mListView;
    protected OuterScroller mOuterScroller;
    protected int mIndex;

    private String matchID;
    private SingleTestWordAdapter mAdaper;
    private OnlineLoopWordListInfo mWordListInfo;
    private Dialog mDialog;

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

        mAdaper = new SingleTestWordAdapter(getActivity());
        mListView.setAdapter(mAdaper);

        loadDefaultData(PAGE_MORE);
    }

    private void openWordInfoFragment(OnlineWordInfo wordInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_WORDINFO, wordInfo);
        mBundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, matchID);
        SingleTestWordInfoFragment fragment = newFragment(getActivity(), SingleTestWordInfoFragment.class, mBundle);
        showFragment(fragment);
    }

    private void updateData() {
        if (mWordListInfo.mTestWordInfos != null && mWordListInfo.mTestWordInfos.size() > 0) {
            mAdaper.setItems(mWordListInfo.mTestWordInfos);
        }else {
            mAdaper.setEmpty("该练习暂无单词");
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getSingletestWordListUrl(matchID);
        return new DataAcquirer<OnlineLoopWordListInfo>().acquire(url, new OnlineLoopWordListInfo(), -1);
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mWordListInfo = (OnlineLoopWordListInfo) result;
        updateData();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        showContent();
        mAdaper.setEmpty(ErrorManager.getErrorManager().getErrorHint(result.getRawResult(), result.getErrorDescription()));
    }

    class SingleTestWordAdapter extends SingleTypeAdapter<OnlineWordInfo> {
        private boolean isEmpty;
        private String emptyDesc;
        private final Drawable drawable;

        public SingleTestWordAdapter(Context context) {
            super(context);
            drawable = ContextCompat.getDrawable(mContext, R.drawable.icon_rule);
            drawable.setBounds(0, UIUtils.dip2px(1), drawable.getMinimumWidth(), drawable.getMinimumHeight() + UIUtils.dip2px(1));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.layout_singletest_word_item, null);
                viewHolder.mWordName = (TextView) convertView.findViewById(R.id.singletest_word_item_name);
                viewHolder.mWrongCount = (TextView) convertView.findViewById(R.id.singletest_word_item_errorcount);
                viewHolder.mWordTitle = (TextView) convertView.findViewById(R.id.singletest_word_title);
                viewHolder.mContentView = convertView.findViewById(R.id.item_content);
                viewHolder.mEmptyView = convertView.findViewById(R.id.student_empty);
                viewHolder.mEmptyDesc = (TextView) convertView.findViewById(R.id.student_empty_desc);
                viewHolder.mIconMore = convertView.findViewById(R.id.singletest_word_icon_more);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (isEmpty()) {
                viewHolder.mContentView.setVisibility(View.GONE);
                viewHolder.mEmptyView.setVisibility(View.VISIBLE);
                viewHolder.mEmptyDesc.setText(emptyDesc);
            }else {
                viewHolder.mContentView.setVisibility(View.VISIBLE);
                viewHolder.mEmptyView.setVisibility(View.GONE);
                final OnlineWordInfo wordInfo = getItem(position);
                viewHolder.mWordName.setText(wordInfo.content);
                if (wordInfo.errorStudentCount == 0) {
                    viewHolder.mWrongCount.setText("");
                    viewHolder.mIconMore.setVisibility(View.GONE);
                }else {
                    viewHolder.mWrongCount.setText(wordInfo.errorStudentCount + "人答错" + wordInfo.wrongCount + "次");
                    viewHolder.mIconMore.setVisibility(View.VISIBLE);
                }
                if (wordInfo.isLowRate) {
                    viewHolder.mWordTitle.setVisibility(View.VISIBLE);
                    viewHolder.mWordTitle.setCompoundDrawables(null, null, drawable, null);
                    viewHolder.mWordTitle.setCompoundDrawablePadding(UIUtils.dip2px(5));
                    viewHolder.mWordTitle.setText(getSpannableString("低频错词 (" + mWordListInfo.lowRateCount + "个)"));
                    viewHolder.mWordTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRuleDialog(false);
                        }
                    });
                }else if (wordInfo.isHighRate) {
                    viewHolder.mWordTitle.setVisibility(View.VISIBLE);
                    viewHolder.mWordTitle.setCompoundDrawables(null, null, drawable, null);
                    viewHolder.mWordTitle.setCompoundDrawablePadding(UIUtils.dip2px(5));
                    viewHolder.mWordTitle.setText(getSpannableString("高频错词 (" + mWordListInfo.highRateCount + "个)"));
                    viewHolder.mWordTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRuleDialog(true);
                        }
                    });
                }else if (wordInfo.isCorrect) {
                    viewHolder.mWordTitle.setVisibility(View.VISIBLE);
                    viewHolder.mWordTitle.setCompoundDrawables(null, null, null, null);
                    viewHolder.mWordTitle.setCompoundDrawablePadding(UIUtils.dip2px(0));
                    viewHolder.mWordTitle.setText(getSpannableString("未答错单词 (" + mWordListInfo.correctCount + "个)"));
                    viewHolder.mWordTitle.setOnClickListener(null);
                }else {
                    viewHolder.mWordTitle.setVisibility(View.GONE);
                }

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (wordInfo.errorStudentCount <= 0) return;
                        openWordInfoFragment(wordInfo);
                    }
                });
            }

            return convertView;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        public void setEmpty(String emptyDesc) {
            this.isEmpty = true;
            this.emptyDesc = emptyDesc;
            notifyDataSetChanged();
        }

        @Override
        public void setItems(List<OnlineWordInfo> items) {
            this.isEmpty = false;
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
        public TextView mWordName;
        public TextView mWrongCount;
        public TextView mWordTitle;
        public View mContentView;
        public View mEmptyView;
        public TextView mEmptyDesc;
        public View mIconMore;
    }

    private SpannableString getSpannableString(String source) {
        SpannableString sp = new SpannableString(source);
        sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.color_text_main)),
                source.indexOf("(") + 1, source.indexOf("个"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new StyleSpan(Typeface.BOLD), source.indexOf("(") + 1, source.indexOf("个"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    private void showRuleDialog(boolean isHighRate) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        String str;
        if (isHighRate) {
            str = "高频错词：班级内大于等于10%的同学答错的词";
        }else {
            str = "低频错词：班级内小于10%的同学答错的词";
        }

        mDialog = DialogUtils.getNewMessageDialog(getActivity(), R.drawable.icon_dialog_rule, "", 0, "知道了", "",str, Gravity.CENTER,
                new DialogUtils.OnDialogButtonClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
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
}
