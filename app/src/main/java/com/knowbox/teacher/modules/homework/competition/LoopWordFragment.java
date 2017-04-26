package com.knowbox.teacher.modules.homework.competition;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineLoopWordListInfo;
import com.knowbox.teacher.base.bean.OnlineStudentInfo;
import com.knowbox.teacher.base.bean.OnlineWordInfo;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.widgets.headerviewpager.InnerListView;
import com.knowbox.teacher.widgets.headerviewpager.InnerScroller;
import com.knowbox.teacher.widgets.headerviewpager.InnerScrollerContainer;
import com.knowbox.teacher.widgets.headerviewpager.OuterScroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by weilei on 17/4/6.
 */
public class LoopWordFragment extends BaseUIFragment<UIFragmentHelper> implements InnerScrollerContainer {

    private InnerListView mListView;
    protected OuterScroller mOuterScroller;
    protected int mIndex;

    private String matchID = "";
    private LoopWordAdapter mAdaper;
    private OnlineLoopWordListInfo mOnlineLoopWordListInfo;

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

        mAdaper = new LoopWordAdapter(getActivity());

        View header = new View(getContext());
        header.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(15)));
        header.setBackgroundColor(getResources().getColor(R.color.color_main_background));
        mListView.addHeaderView(header);
        mListView.setAdapter(mAdaper);

        loadDefaultData(PAGE_MORE, matchID);
    }

    private void openWordInfoFragment(OnlineWordInfo wordInfo) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_WORDINFO, wordInfo);
        mBundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, matchID);
        LoopWordInfoFragment fragment = LoopWordInfoFragment.newFragment(getActivity(), LoopWordInfoFragment.class, mBundle);
        showFragment(fragment);
    }

    private void updateData() {
        if (mOnlineLoopWordListInfo.mWordInfos != null && mOnlineLoopWordListInfo.mWordInfos.size() > 0) {
            mAdaper.setItems(mOnlineLoopWordListInfo.mWordInfos);
        } else {
            mAdaper.setEmpty(true);
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getReportWordListUrl((String) params[0]);
        OnlineLoopWordListInfo result = new DataAcquirer<OnlineLoopWordListInfo>().acquire(url, new OnlineLoopWordListInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mOnlineLoopWordListInfo = (OnlineLoopWordListInfo) result;
        updateData();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
    }

    class LoopWordAdapter extends SingleTypeAdapter<OnlineWordInfo> {

        private boolean isEmpty;
        public LoopWordAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.layout_loop_word_item, null);
                viewHolder.mWordName = (TextView) convertView.findViewById(R.id.loop_word_item_name);
                viewHolder.mWrongCount = (TextView) convertView.findViewById(R.id.loop_word_item_count);
                viewHolder.mDataView = convertView.findViewById(R.id.loop_word_item_data);
                viewHolder.mEmptyView = convertView.findViewById(R.id.loop_word_item_empty);
                viewHolder.mMoreView = (ImageView) convertView.findViewById(R.id.loop_word_item_more);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (getEmpty()) {
                viewHolder.mDataView.setVisibility(View.GONE);
                viewHolder.mEmptyView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mDataView.setVisibility(View.VISIBLE);
                viewHolder.mEmptyView.setVisibility(View.GONE);
                final OnlineWordInfo wordInfo = getItem(position);
                viewHolder.mWordName.setText(wordInfo.content);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.mWrongCount.getLayoutParams();

                if (wordInfo.wrongCount <= 0) {
                    viewHolder.mMoreView.setVisibility(View.GONE);
                    params.rightMargin = UIUtils.dip2px(15);
                    viewHolder.mWrongCount.setText("未答错");
                } else {
                    viewHolder.mMoreView.setVisibility(View.VISIBLE);
                    params.rightMargin = UIUtils.dip2px(32);
                    viewHolder.mWrongCount.setText("答错" + wordInfo.wrongCount + "次");
                }

                viewHolder.mWrongCount.setLayoutParams(params);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (wordInfo.wrongCount <= 0){
                            return;
                        }
                        openWordInfoFragment(wordInfo);
                    }
                });
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
        public void setItems(List<OnlineWordInfo> items) {
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
        public TextView mWordName;
        public TextView mWrongCount;
        public ImageView mMoreView;
        public View mDataView;
        public View mEmptyView;
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
