package com.buang.welewolf.modules.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineCompetitionListInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.modules.homework.competition.CompetitionTypeFragment;
import com.buang.welewolf.modules.homework.competition.SingleTestDetailFragment;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.widgets.GradientDrawableBuilder;
import com.buang.welewolf.widgets.pulltorefresh.PullToRefreshBase;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UIUtils;
import com.buang.welewolf.modules.homework.competition.LoopDetailFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.widgets.LoadMoreListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 15/12/28.
 */
public class MainBankFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int PAGESIZE = 10;
    private int pageNum = 1;
    private static final int ADAPTER_MODE_NOMAL = 0;
    private static final int ADAPTER_MODE_DELETE = 1;
    private int currentMode = ADAPTER_MODE_NOMAL;

    private static final int ACTION_POST_DELETE = 1;

    private LoadMoreListView mListView;
    private CompetitionAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private boolean mIsEnd = false;
    private View mEmptyView;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_WITH_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_main_competition, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("比赛");
        getUIFragmentHelper().getTitleBar().setBackBtnVisible(false);
        setTitleRightText();
        mListView = (LoadMoreListView) view.findViewById(com.buang.welewolf.R.id.competition_list);
        adapter = new CompetitionAdapter(getActivity());
        mListView.setAdapter(adapter);

        mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadDefaultData(PAGE_MORE);
            }
        });

        mEmptyView = view.findViewById(com.buang.welewolf.R.id.empty_layout);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(com.buang.welewolf.R.id.competition_list_refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(com.buang.welewolf.R.color.color_text_primary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                loadDefaultData(PAGE_FIRST);
            }
        });

        loadDefaultData(PAGE_FIRST);
        view.findViewById(com.buang.welewolf.R.id.competition_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(CompetitionTypeFragment.newFragment(getActivity(), CompetitionTypeFragment.class, null));
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActionUtils.ACTION_MATCH_CHANGED);
        MsgCenter.registerLocalReceiver(mReceiver, intentFilter);
    }

    private void setTitleRightText() {
        getUIFragmentHelper().getTitleBar().setRightMoreTxtColor(ContextCompat.getColor(getActivity(), com.buang.welewolf.R.color.color_text_main));
        getUIFragmentHelper().getTitleBar().setRightMoreTxt("删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != adapter && adapter.getCount() > 0) {
                    if (currentMode == ADAPTER_MODE_NOMAL) {
                        currentMode = ADAPTER_MODE_DELETE;
                        adapter.notifyDataSetChanged();
                        getUIFragmentHelper().getTitleBar().setRightMoreTxt("取消");
                        getUIFragmentHelper().getTitleBar().setRightMoreTxtColor(ContextCompat.getColor(getActivity(), com.buang.welewolf.R.color.color_main_app));
                    } else if (currentMode == ADAPTER_MODE_DELETE) {
                        if (hasSelectId()) {
                            showDeleteDialog();
                            return;
                        }

                        currentMode = ADAPTER_MODE_NOMAL;
                        adapter.notifyDataSetChanged();
                        getUIFragmentHelper().getTitleBar().setRightMoreTxt("删除");
                        getUIFragmentHelper().getTitleBar().setRightMoreTxtColor(ContextCompat.getColor(getActivity(), com.buang.welewolf.R.color.color_text_main));
                    }
                }
            }
        });
    }

    private void showDeleteDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        mDialog = DialogUtils.getNewMessageDialog(getActivity(), com.buang.welewolf.R.drawable.icon_competition_create,"", 0, "删除", "取消",
                "删除比赛同时会删除所有比赛数据\n您确定吗?", Gravity.CENTER, new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                    //删除所选比赛
                    if (hasSelectId()) {
                        loadData(ACTION_POST_DELETE, PAGE_MORE);
                    }
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void loadDefaultData(int pageNo, Object... params) {
        if (pageNo == PAGE_MORE && mIsEnd) {
            return;
        }
        mIsEnd = false;
        super.loadDefaultData(pageNo, params);
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        if (action == ACTION_DEFAULT) {
            if (pageNo == PAGE_MORE) {
                mListView.setMakeWorkViewVisible(false);
            }
        }else {
            super.onPreAction(action, pageNo);
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        if (action == ACTION_DEFAULT) {
            String url = OnlineServices.getCompetitionListUrl(PAGESIZE, pageNum);
            return new DataAcquirer<OnlineCompetitionListInfo>().acquire(url, new OnlineCompetitionListInfo(), -1);
        }else if (action == ACTION_POST_DELETE){
            String url = OnlineServices.getCompetitionMultiDeleteUrl();
            return new DataAcquirer<BaseObject>().post(url, getDeleteIdStr(), new BaseObject());
        }
        return null;
    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        if (action == ACTION_DEFAULT) {
            String url = OnlineServices.getCompetitionListUrl(PAGESIZE, pageNum);
            return new UrlModelPair(url, new OnlineCompetitionListInfo());
        }
        return null;
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        if (action == ACTION_DEFAULT) {
            if (adapter.getCount() > 0) {
                return;
            }
            OnlineCompetitionListInfo mCompetitonListInfo = (OnlineCompetitionListInfo) result;
            if (mCompetitonListInfo != null && mCompetitonListInfo.mCompetitions.size() > 0) {
                mEmptyView.setVisibility(View.GONE);
                adapter.setItems(mCompetitonListInfo.mCompetitions);
            }
        }
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_DEFAULT) {
            refreshLayout.setRefreshing(false);
            mListView.setFootVisible(true);
            OnlineCompetitionListInfo mCompetitonListInfo = (OnlineCompetitionListInfo) result;
            if (mCompetitonListInfo != null && mCompetitonListInfo.mCompetitions.size() > 0) {
                mEmptyView.setVisibility(View.GONE);
                if (pageNo == PAGE_MORE) {//加载更多
                    if (adapter.getCount() > 0) {
                        adapter.addItems(mCompetitonListInfo.mCompetitions);
                    }else {
                        adapter.setItems(mCompetitonListInfo.mCompetitions);
                    }
                }else {//刷新
                    currentMode = ADAPTER_MODE_NOMAL;
                    adapter.setItems(mCompetitonListInfo.mCompetitions);
                    setTitleRightText();
                }

                if (mCompetitonListInfo.mCompetitions.size() < PAGESIZE) {
                    mIsEnd = true;
                    mListView.setMakeWorkViewVisible(true);
                }else {
                    mListView.setMakeWorkViewVisible(false);
                }

                ++pageNum;//页码自增

            }else {
                if (pageNo == PAGE_MORE) {
                    if (adapter.getCount() > 0) {
                        showContent();
                        mIsEnd = true;
                        mListView.setMakeWorkViewVisible(true);
                        mEmptyView.setVisibility(View.GONE);
                    }else {
                        adapter.setItems(null);
                        setEmptyView("暂无锦标赛记录");
                    }
                }else {
                    adapter.setItems(null);
                    setEmptyView("暂无锦标赛记录");
                }
            }
        }else if (action == ACTION_POST_DELETE) {
            clearAllSelect();
        }

    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        if (action == ACTION_DEFAULT) {
            showContent();
            refreshLayout.setRefreshing(false);
            if (adapter.getCount() > 0) {
                showContent();
            }else {
                //需要细分
                setEmptyView(ErrorManager.getErrorManager().getErrorHint(result.getRawResult(), "暂无锦标赛记录"));
            }
        }else {
            super.onFail(action, pageNo, result, params);
        }
    }

    private void setEmptyView(String desc) {
        getUIFragmentHelper().getTitleBar().setRightMoreTxt(" ", null);
        mEmptyView.setVisibility(View.VISIBLE);
        mListView.setFootVisible(false);
        ((TextView)mEmptyView.findViewById(com.buang.welewolf.R.id.empty_text)).setText(desc);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (ActionUtils.ACTION_MATCH_CHANGED.equals(action)) {
                pageNum = 1;
                loadDefaultData(PAGE_FIRST);
            }
        }
    };

    private boolean hasSelectId() {
        List<OnlineCompetitionListInfo.CompetitionItem> items = adapter.getItems();
        boolean has = false;
        for (OnlineCompetitionListInfo.CompetitionItem item : items) {
            if (item.isSelected) {
                has = true;
                break;
            }
        }
        return has;
    }

    private void clearAllSelect() {
        List<OnlineCompetitionListInfo.CompetitionItem> items = adapter.getItems();
        if (items != null && !items.isEmpty()) {
            List<OnlineCompetitionListInfo.CompetitionItem> unSelectItems = new ArrayList<OnlineCompetitionListInfo.CompetitionItem>();
            for (OnlineCompetitionListInfo.CompetitionItem item : items) {
                if (!item.isSelected) {
                    unSelectItems.add(item);
                }
            }
            currentMode = ADAPTER_MODE_NOMAL;
            adapter.setItems(unSelectItems);
            titleRightTextChanged();
            if (unSelectItems.size() <= 0) {
                setEmptyView("暂无锦标赛记录");
            }
        }
    }

    private ArrayList<String> getSelectedIDs() {
        List<OnlineCompetitionListInfo.CompetitionItem> items = adapter.getItems();
        ArrayList<String> competitionItems;
        if (items != null && !items.isEmpty()) {
            competitionItems = new ArrayList<String>();
            for (OnlineCompetitionListInfo.CompetitionItem item : items) {
                if (item.isSelected) {
                    competitionItems.add(item.matchID);
                }
            }
            return competitionItems;
        }
        return null;
    }


    private String getDeleteIdStr() {
        ArrayList<String> ids = getSelectedIDs();
        if (null != ids && !ids.isEmpty()) {
            JSONObject data = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                for (String str : ids) {
                    array.put(str);
                }
                data.put("match_ids", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data.toString();
        }
        return "";
    }

    private void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public class CompetitionAdapter extends SingleTypeAdapter<OnlineCompetitionListInfo.CompetitionItem> {
        GradientDrawable redDeawable = new GradientDrawableBuilder().createCommonDrawable(getActivity())
                .buildColor(getResources().getColor(com.buang.welewolf.R.color.color_button_red))
                .buildCornerRadius(UIUtils.dip2px(13)).create();

        GradientDrawable strokeDeawable = new GradientDrawableBuilder().createCommonDrawable(getActivity())
                .buildColor(getResources().getColor(com.buang.welewolf.R.color.white))
                .buildStroke(1, getResources().getColor(com.buang.welewolf.R.color.color_divider))
                .buildCornerRadius(UIUtils.dip2px(13)).create();

        private int displayWidth;
        private RelativeLayout.LayoutParams layoutParams;


        public CompetitionAdapter(Context context) {
            super(context);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            displayWidth = displayMetrics.widthPixels;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(com.buang.welewolf.R.layout.layout_competitions_item, parent, false);
                holder.mItemView = (RelativeLayout) convertView.findViewById(com.buang.welewolf.R.id.item_layout);
                holder.mRootView = (RelativeLayout) convertView.findViewById(com.buang.welewolf.R.id.root_layout_view);
                layoutParams = (RelativeLayout.LayoutParams) convertView.findViewById(com.buang.welewolf.R.id.item_layout).getLayoutParams();
                layoutParams.width = displayWidth;
                convertView.findViewById(com.buang.welewolf.R.id.root_layout_view).getLayoutParams().width = displayWidth + UIUtils.dip2px(45);
                holder.mTitle = (TextView) convertView.findViewById(com.buang.welewolf.R.id.title);
                holder.mDate = (TextView) convertView.findViewById(com.buang.welewolf.R.id.time);
                holder.mGrade = (TextView) convertView.findViewById(com.buang.welewolf.R.id.grade);
                holder.mState = (TextView) convertView.findViewById(com.buang.welewolf.R.id.state);
                holder.mJoinCount = (TextView) convertView.findViewById(com.buang.welewolf.R.id.join_count);
                holder.mStudentCount = (TextView) convertView.findViewById(com.buang.welewolf.R.id.student_count);
                holder.mSelectBtn = (ImageView) convertView.findViewById(com.buang.welewolf.R.id.select_btn);
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (layoutParams != null) {
                if (currentMode == ADAPTER_MODE_NOMAL) {
                    layoutParams.setMargins(0, 0, 0, 0);
                }else if (currentMode == ADAPTER_MODE_DELETE) {
                    layoutParams.setMargins(UIUtils.dip2px(45), 0, 0, 0);
                }
                holder.mItemView.setLayoutParams(layoutParams);
            }

            final OnlineCompetitionListInfo.CompetitionItem item = getItem(position);
            holder.mTitle.setText(item.name);

            if (OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH.equals(item.type)) {
                Drawable drawable = ContextCompat.getDrawable(mContext, com.buang.welewolf.R.drawable.icon_competition_loop_match);
                drawable.setBounds(0, UIUtils.dip2px(1), drawable.getMinimumWidth(), drawable.getMinimumHeight() + UIUtils.dip2px(1));
                holder.mTitle.setCompoundDrawables(drawable, null, null, null);
                holder.mTitle.setCompoundDrawablePadding(UIUtils.dip2px(5));
                holder.mDate.setText(item.matchTimeDesc);
            }else if (OnlineCompetitionListInfo.CompetitionItem.TYPE_SINGLE_TEST.equals(item.type)) {
                Drawable drawable = ContextCompat.getDrawable(mContext, com.buang.welewolf.R.drawable.icon_competition_single_test);
                drawable.setBounds(0, UIUtils.dip2px(1), drawable.getMinimumWidth(), drawable.getMinimumHeight() + UIUtils.dip2px(1));
                holder.mTitle.setCompoundDrawables(drawable, null, null, null);
                holder.mTitle.setCompoundDrawablePadding(UIUtils.dip2px(5));
                holder.mDate.setText(item.singletestTime);
            }else {
                holder.mTitle.setCompoundDrawablePadding(0);
                holder.mTitle.setCompoundDrawables(null, null, null, null);
                holder.mDate.setText(item.matchTimeDesc);
            }

            holder.mGrade.setText(item.className);
            holder.mJoinCount.setText(item.joinUser);
            holder.mStudentCount.setText("/" + item.totalUser);
            if (!item.totalUser.equals("0") && item.totalUser.equals(item.joinUser)) {
                holder.mJoinCount.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_main_app));
                holder.mStudentCount.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_main_app));
            } else {
                holder.mJoinCount.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_main));
                holder.mStudentCount.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_primary));
            }

            if (item.status == ConstantsUtils.MATCH_STATUS_NOTSTART) {
                holder.mItemView.setBackgroundColor(getResources().getColor(com.buang.welewolf.R.color.white));
                holder.mState.setText("未开赛");
                holder.mState.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_primary));
                setBackgroundDrawable(holder.mState, strokeDeawable);
            }else if (item.status == ConstantsUtils.MATCH_STATUS_FINISHED) {
                holder.mItemView.setBackgroundColor(getResources().getColor(com.buang.welewolf.R.color.color_main_background));
                holder.mState.setText("已结束");
                holder.mState.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_primary));
                holder.mState.setBackgroundResource(0);
            }else {
                holder.mItemView.setBackgroundColor(getResources().getColor(com.buang.welewolf.R.color.white));
                holder.mState.setText("进行中");
                holder.mState.setTextColor(getResources().getColor(com.buang.welewolf.R.color.white));
                setBackgroundDrawable(holder.mState, redDeawable);
            }

            holder.mSelectBtn.setSelected(item.isSelected);

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentMode == ADAPTER_MODE_NOMAL) {
                        UmengConstant.reportUmengEvent(UmengConstant.EVENT_COMPETITION_LOOK_DETAILS, null);
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantsUtils.KEY_BUNDLE_MATCHID, item.matchID);
                        bundle.putString(ConstantsUtils.KEY_BUNDLE_TTTLE, item.name);
                        bundle.putString(ConstantsUtils.KEY_BUNDLE_TIME, item.matchTimeDesc);
                        bundle.putString(ConstantsUtils.KEY_BUNDLE_CLASSNAME, item.className);
                        bundle.putString(ConstantsUtils.KEY_BUNDLE_STUDENTCOUNT, item.totalUser);
                        bundle.putString(ConstantsUtils.KEY_BUNDLE_CLASSID, item.classId);
                        bundle.putInt(ConstantsUtils.KEY_BUNDLE_STATUS, item.status);
                        bundle.putSerializable(ConstantsUtils.KEY_BUNDLE_CHAM_ITEM, item);
                        if (OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH.equals(item.type)) {
                            showFragment(LoopDetailFragment.newFragment(getActivity(), LoopDetailFragment.class, bundle));
                        }else if (OnlineCompetitionListInfo.CompetitionItem.TYPE_SINGLE_TEST.equals(item.type)) {
                            showFragment(SingleTestDetailFragment.newFragment(getActivity(), SingleTestDetailFragment.class, bundle));
                        }
                    }else if (currentMode == ADAPTER_MODE_DELETE){
                        item.isSelected = !item.isSelected;
                        adapter.notifyDataSetChanged();
                        titleRightTextChanged();
                    }
                }
            });
            holder.mItemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            holder.mSelectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.isSelected = !item.isSelected;
                    adapter.notifyDataSetChanged();
                    titleRightTextChanged();
                }
            });
            return convertView;
        }

        class ViewHolder {
            public RelativeLayout mItemView;
            public TextView mTitle;
            public TextView mDate;
            public TextView mGrade;
            public TextView mState;
            public TextView mJoinCount;
            public ImageView mSelectBtn;
            public TextView mStudentCount;
            public RelativeLayout mRootView;

        }
    }

    private void titleRightTextChanged() {
        if (currentMode == ADAPTER_MODE_DELETE) {
            getUIFragmentHelper().getTitleBar().setRightMoreTxtColor(ContextCompat.getColor(getActivity(), com.buang.welewolf.R.color.color_main_app));
            if (getSelectedIDs() != null && getSelectedIDs().size() > 0) {
                getUIFragmentHelper().getTitleBar().setRightMoreTxt(getSpannableString("确认删除(" + getSelectedIDs().size() + ")"));
            }else {
                getUIFragmentHelper().getTitleBar().setRightMoreTxt("取消");
            }
        }else if (currentMode == ADAPTER_MODE_NOMAL) {
            getUIFragmentHelper().getTitleBar().setRightMoreTxtColor(ContextCompat.getColor(getActivity(), com.buang.welewolf.R.color.color_text_main));
            getUIFragmentHelper().getTitleBar().setRightMoreTxt("删除");
        }
    }

    private SpannableString getSpannableString(String source) {
        SpannableString sp = new SpannableString(source);
        sp.setSpan(new AbsoluteSizeSpan(11, true), source.indexOf("("), source.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (mReceiver != null) {
            MsgCenter.unRegisterLocalReceiver(mReceiver);
        }
    }
}
