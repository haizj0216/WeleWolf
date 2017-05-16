package com.buang.welewolf.modules.homework.assign;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineAssignUnitInfo;
import com.buang.welewolf.base.bean.OnlineCompQuestionPreviewInfo;
import com.buang.welewolf.base.bean.OnlineCompetitionListInfo;
import com.buang.welewolf.base.bean.OnlineWordInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.updateclass.UpdateClassService;
import com.buang.welewolf.modules.login.services.UpdateJiaocaiListener;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.widgets.flowlayout.FlowLayout;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.base.bean.OnlineUnitInfo;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.SubjectUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/17.
 */
public class AssignUnitFragment extends BaseUIFragment<UIFragmentHelper> {

    private GridView mUnitFlow;
    private TextView mBookView;
    private TextView mWeakBox;
    private TextView mCountView;
    private TextView mMakeTxt;
    private TextView mAllSelectView;
    private TextView mTypeCountView;
    private TextView mTypeTitle;
    private LinearLayout mListView;
    private AccuracGridView mTypeGridView;
    //    private AssignWordAdapter mAdapter;
    private OnlineAssignUnitInfo mOnlineAssignUnitInfo;
    private List<OnlineUnitInfo> mSelectUnits = new ArrayList<OnlineUnitInfo>();
    private List<OnlineWordInfo> mSelectWords = new ArrayList<OnlineWordInfo>();
    private List<OnlineCompQuestionPreviewInfo.QuestionTypeItem> mSelectTypes = new ArrayList<OnlineCompQuestionPreviewInfo.QuestionTypeItem>();

    private LoginService mLoginService;
    private boolean isSelectWeek;
    private boolean isAllSelect = true;
    private String matchType;//比赛类型

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mLoginService = (LoginService) getSystemService(LoginService.SERVICE_NAME);
        mLoginService.getServiceObvserver().addUpdatejiaocaiListener(updateJiaocaiListener);
        if (null != getArguments()) {
            matchType = getArguments().getString(ConstantsUtils.KEY_ASSIGN_TYPE);
        }
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_assign_unit, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setRightMoreTxt("修改教材", mOnClickListener);
        mListView = (LinearLayout) view.findViewById(com.buang.welewolf.R.id.assign_unit_word_list);
//        mAdapter = new AssignWordAdapter(getActivity());
//        mListView.setAdapter(mAdapter);

        mTypeGridView = (AccuracGridView) view.findViewById(com.buang.welewolf.R.id.assign_type_gridview);

        mBookView = (TextView) view.findViewById(com.buang.welewolf.R.id.assign_unit_book);
        mCountView = (TextView) view.findViewById(com.buang.welewolf.R.id.assign_unit_count);
        mMakeTxt = (TextView) view.findViewById(com.buang.welewolf.R.id.assign_unit_make_txt);
        mUnitFlow = (AccuracGridView) view.findViewById(com.buang.welewolf.R.id.assign_unit_grdiview);
        mWeakBox = (TextView) view.findViewById(com.buang.welewolf.R.id.assign_unit_weeknewss);
        mAllSelectView = (TextView) view.findViewById(com.buang.welewolf.R.id.assign_unit_all_select);
        mTypeCountView = (TextView) view.findViewById(com.buang.welewolf.R.id.assign_type_count);
        mTypeTitle = (TextView) view.findViewById(com.buang.welewolf.R.id.assign_type_title);
        mWeakBox.setOnClickListener(mOnClickListener);
        mBookView.setText("-  " + Utils.getLoginUserItem().mEditionName + Utils.getLoginUserItem().mBookName + "  -");
        view.findViewById(com.buang.welewolf.R.id.assign_unit_preview).setOnClickListener(mOnClickListener);
        view.findViewById(com.buang.welewolf.R.id.assign_unit_make).setOnClickListener(mOnClickListener);
        view.findViewById(com.buang.welewolf.R.id.assign_unit_all_select).setOnClickListener(mOnClickListener);

        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDefaultData(PAGE_FIRST);
            }
        }, 200);

        if (matchType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
            mTypeTitle.setText("比赛题型");
            getUIFragmentHelper().getTitleBar().setTitle("比赛范围");
        } else {
            mTypeTitle.setText("测验题型");
            getUIFragmentHelper().getTitleBar().setTitle("测验范围");
        }
    }

    UpdateJiaocaiListener updateJiaocaiListener = new UpdateJiaocaiListener() {
        @Override
        public void onUpdateSuccess(final UserItem userItem, String clazzName) {
            if (clazzName != null && clazzName.equals(AssignUnitFragment.class.getName())) {
                getActivity().onBackPressed();
                getActivity().onBackPressed();
            }
            mSelectUnits.clear();
            mSelectWords.clear();
            mSelectTypes.clear();
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListView.removeAllViews();
                    updateWordList(null, false);
                    if (Utils.getLoginUserItem() == null) {
                        mBookView.setText("-  " + userItem.mEditionName + userItem.mBookName + "  -");
                    } else {
                        mBookView.setText("-  " + Utils.getLoginUserItem().mEditionName + Utils.getLoginUserItem().mBookName + "  -");
                    }
                    loadDefaultData(PAGE_FIRST);
                }
            });

        }

        @Override
        public void onUpdateFailed(String error, String clazzName) {

        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case com.buang.welewolf.R.id.title_bar_rightView:
                    openBookSelectFragment();
                    break;
                case com.buang.welewolf.R.id.assign_unit_preview:
                    showFragment(AssignUnitPreviewFragment.newFragment(getActivity(), AssignUnitPreviewFragment.class, null, AnimType.BOTTOM_TO_TOP));
                    break;
                case com.buang.welewolf.R.id.assign_unit_make:
                    if (mSelectWords.size() < mOnlineAssignUnitInfo.minWordCount) {
                        ToastUtils.showShortToast(getActivity(), "最少选择" + mOnlineAssignUnitInfo.minWordCount + "个单词");
                        return;
                    }
                    if (mSelectWords.size() > mOnlineAssignUnitInfo.maxWordCount) {
                        ToastUtils.showShortToast(getActivity(), "最多选择" + mOnlineAssignUnitInfo.maxWordCount + "个单词");
                        return;
                    }
                    ArrayList<OnlineUnitInfo> lists = (ArrayList<OnlineUnitInfo>) mSelectUnits;
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(ConstantsUtils.KEY_ASSIGN_UNITLIST, lists);
                    mBundle.putSerializable(ConstantsUtils.KEY_ASSIGN_WORDLIST, (ArrayList<OnlineWordInfo>) mSelectWords);
                    mBundle.putSerializable(ConstantsUtils.KEY_ASSIGN_DIMLIST, (ArrayList<OnlineCompQuestionPreviewInfo.QuestionTypeItem>) mSelectTypes);
                    mBundle.putInt(ConstantsUtils.KEY_BUNDLE_MINDAY, mOnlineAssignUnitInfo.minMatchDays);
                    mBundle.putInt(ConstantsUtils.KEY_BUNDLE_START_CLOCK, mOnlineAssignUnitInfo.startClock);
                    mBundle.putInt(ConstantsUtils.KEY_BUNDLE_END_CLOCK, mOnlineAssignUnitInfo.endClock);
                    mBundle.putString(ConstantsUtils.KEY_ASSIGN_TYPE, matchType);
                    AssignSubmitFragment fragment = AssignSubmitFragment.newFragment(getActivity(),
                            AssignSubmitFragment.class, mBundle);
                    showFragment(fragment);
                    break;
                case com.buang.welewolf.R.id.assign_unit_weeknewss:
                    if (mOnlineAssignUnitInfo != null && mOnlineAssignUnitInfo.mWeekUnit != null &&
                            mOnlineAssignUnitInfo.mWeekUnit.mWordInfos != null && mOnlineAssignUnitInfo.mWeekUnit.mWordInfos.size() > 0) {
                        isSelectWeek = !isSelectWeek;
                        updateSelectWeek();
                    } else {
                        ToastUtils.showShortToast(getActivity(), "需积累学生单词掌握情况智能推荐");
                    }
                    break;
                case com.buang.welewolf.R.id.assign_unit_all_select:
                    updateAllSelect();
                    break;
            }
        }
    };

    /**
     * 全选处理
     */
    private void updateAllSelect() {
        isAllSelect = !isAllSelect;
        mAllSelectView.setVisibility(View.VISIBLE);
        if (isAllSelect) {
            mSelectWords.clear();
            List<OnlineUnitInfo> mUnitInfos = mSelectUnits;
            if (mUnitInfos.size() > 0) {
                for (int i = 0; i < mUnitInfos.size(); i++) {
                    mSelectWords.addAll(mUnitInfos.get(i).mWordInfos);
                }
            }
            mAllSelectView.setText("清空勾选");
        } else {
            mSelectWords.clear();
            mAllSelectView.setText("全选");
        }
        updateWordCount();
        updateAllSelectView();
    }

    /**
     * 选择教材
     */
    private void openBookSelectFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtils.CLAZZ_NAME, AssignUnitFragment.class.getName());
        AssignSelectPublisherFragment fragment = AssignSelectPublisherFragment.newFragment(getActivity(),
                AssignSelectPublisherFragment.class, bundle);
        showFragment(fragment);
    }

    /**
     * 刷新数据
     *
     * @param result
     */
    private void updateData(final OnlineAssignUnitInfo result) {
        mOnlineAssignUnitInfo = result;

        final AssignUnitAdapter adapter = new AssignUnitAdapter(getActivity());
        adapter.setItems(result.mUnitInfos);
        mUnitFlow.setAdapter(adapter);
        int width = UIUtils.getWindowWidth(getActivity());
        int margin = (width - UIUtils.dip2px(50) * 4) / 10;
        mUnitFlow.setPadding(margin, 0, margin, 0);
        mUnitFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnlineUnitInfo unitInfo = adapter.getItem(position);
                if (mSelectUnits.contains(unitInfo)) {
                    updateWordList(unitInfo, false);
                } else {
                    updateWordList(unitInfo, true);
                }
                adapter.notifyDataSetChanged();
            }
        });

        if (result.mWeekUnit != null && !result.mWeekUnit.mWordInfos.isEmpty()) {
            isSelectWeek = true;
            mWeakBox.setText("同时选入薄弱单词 (" + result.mWeekUnit.mWordInfos.size() + "个)");
        } else {
            mWeakBox.setText("暂无薄弱单词");
        }
        initTypes();
        if (mOnlineAssignUnitInfo.mTypeItems != null && mOnlineAssignUnitInfo.mTypeItems.size() > 0) {
            if (matchType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
                mSelectTypes.addAll(mOnlineAssignUnitInfo.mTypeItems);
            } else {
                mSelectTypes.add(mOnlineAssignUnitInfo.mTypeItems.get(0));
            }
            mTypeCountView.setText("(已选" + mSelectTypes.size() + "种)");
            final AssignTypeAdapter assignTypeAdapter = new AssignTypeAdapter(getActivity());
            assignTypeAdapter.setItems(mOnlineAssignUnitInfo.mTypeItems);
            mTypeGridView.setAdapter(assignTypeAdapter);
            mTypeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    OnlineCompQuestionPreviewInfo.QuestionTypeItem typeItem = assignTypeAdapter.getItem(position);
                    if (mSelectTypes.contains(typeItem)) {
                        if (mSelectTypes.size() == 1) {
                            ToastUtils.showShortToast(getActivity(), "至少选择一种题型");
                            return;
                        }
                        mSelectTypes.remove(typeItem);
                    } else {
                        mSelectTypes.add(typeItem);
                    }
                    updateWordCount();
                    assignTypeAdapter.notifyDataSetChanged();
                    mTypeCountView.setText("(已选" + mSelectTypes.size() + "种)");
                }
            });
        }
        updateWordList(null, false);

        UpdateClassService updateClassService = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
        updateClassService.addAllClassItem(result.mClassInfos);
    }

    private void initTypes() {
        mOnlineAssignUnitInfo.mTypeItems = new ArrayList<OnlineCompQuestionPreviewInfo.QuestionTypeItem>();

        int types[] = new int[]{SubjectUtils.QUESTION_DIM_QUANPIN, SubjectUtils.QUESTION_DIM_WAKOONG, SubjectUtils.QUESTION_DIM_TIANKONG,
                SubjectUtils.QUESTION_DIM_YINGHAN, SubjectUtils.QUESTION_DIM_TINGLI};
        for (int i = 0; i < types.length; i++) {
            OnlineCompQuestionPreviewInfo.QuestionTypeItem typeItem = new OnlineCompQuestionPreviewInfo.QuestionTypeItem();
            typeItem.type = types[i];
            typeItem.typeName = SubjectUtils.getQuestionDim(typeItem.type);
            mOnlineAssignUnitInfo.mTypeItems.add(typeItem);
        }
    }

    /**
     * 更新单词列表
     * @param newUnit
     * @param isAdd
     */
    private void updateWordList(OnlineUnitInfo newUnit, boolean isAdd) {
        if (newUnit != null) {
            if (isAdd) {
                mSelectUnits.add(0, newUnit);
                mSelectWords.addAll(newUnit.mWordInfos);
                updateWordView(newUnit, 0);
            } else {
                int index = mSelectUnits.indexOf(newUnit);
                mListView.removeViewAt(index);
                mSelectUnits.remove(newUnit);
                mSelectWords.removeAll(newUnit.mWordInfos);
            }
        }
        if (mSelectUnits.size() == 0) {
            mAllSelectView.setVisibility(View.GONE);
            isAllSelect = true;
            mAllSelectView.setText("全选");
        } else {
            if (mSelectUnits.size() == 1 && isAdd) {
                mAllSelectView.setText("清空勾选");
            }
            mAllSelectView.setVisibility(View.VISIBLE);
        }
        updateWordCount();
    }

    /**
     * 薄弱单词选择
     */
    private void updateSelectWeek() {
        if (isSelectWeek) {
            mSelectUnits.add(mSelectUnits.size(), mOnlineAssignUnitInfo.mWeekUnit);
            mSelectWords.addAll(mOnlineAssignUnitInfo.mWeekUnit.mWordInfos);
            updateWordView(mOnlineAssignUnitInfo.mWeekUnit, mListView.getChildCount());
        } else {
            mSelectUnits.remove(mOnlineAssignUnitInfo.mWeekUnit);
            mSelectWords.removeAll(mOnlineAssignUnitInfo.mWeekUnit.mWordInfos);
            mListView.removeViewAt(mListView.getChildCount() - 1);
        }

        if (mSelectUnits.size() == 0) {
            mAllSelectView.setVisibility(View.GONE);
            isAllSelect = true;
            mAllSelectView.setText("清空勾选");
        } else {
            mAllSelectView.setVisibility(View.VISIBLE);
        }
        updateWordCount();
    }

    /**
     * 全部选择view刷新
     */
    private void updateAllSelectView() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View view = mListView.getChildAt(i);
            OnlineUnitInfo unitInfo = mSelectUnits.get(i);
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            FlowLayout flawLayout = viewHolder.mFlowLayout;
            for (int j = 0; j < flawLayout.getChildCount(); j++) {
                View tagView = flawLayout.getChildAt(j);
                final TextView textView = (TextView) tagView.findViewById(com.buang.welewolf.R.id.assign_word_item_text);
                final ImageView select = (ImageView) tagView.findViewById(com.buang.welewolf.R.id.assign_word_item_select);
                textView.setSelected(isAllSelect);
                select.setVisibility(isAllSelect ? View.VISIBLE : View.INVISIBLE);

            }

            if (isAllSelect) {
                viewHolder.mUnitName.setText(unitInfo.mUnitName + " (已选" + unitInfo.mWordInfos.size() + "词)");
                unitInfo.mSelectCount = unitInfo.mWordInfos.size();
            } else {
                viewHolder.mUnitName.setText(unitInfo.mUnitName + " (已选0词)");
                mSelectUnits.get(i).mSelectCount = 0;
            }
        }

    }

    /**
     * 更新单词列表View
     * @param unitInfo
     * @param index
     */
    private void updateWordView(final OnlineUnitInfo unitInfo, int index) {
        final ViewHolder viewHolder;
        View convertView = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_assign_word_item, null);
        viewHolder = new ViewHolder();
        viewHolder.mUnitName = (TextView) convertView.findViewById(com.buang.welewolf.R.id.assign_unit_item_count);
        viewHolder.mDesc = (TextView) convertView.findViewById(com.buang.welewolf.R.id.assign_unit_item_desc);
        viewHolder.mFlowLayout = (FlowLayout) convertView.findViewById(com.buang.welewolf.R.id.assign_unit_item_flowlayout);
        viewHolder.mUnitName.setText(unitInfo.mUnitName + " (已选" + unitInfo.mWordInfos.size() + "词)");
        if (unitInfo.isWeak) {
            viewHolder.mDesc.setVisibility(View.VISIBLE);
            viewHolder.mDesc.setText("根据班级单词练习情况推荐薄弱单词");
        } else {
            viewHolder.mDesc.setVisibility(View.GONE);
        }

        convertView.setTag(viewHolder);

        unitInfo.mSelectCount = unitInfo.mWordInfos.size();
        viewHolder.mFlowLayout.removeAllViews();
        for (int i = 0; i < unitInfo.mWordInfos.size(); i++) {
            final OnlineWordInfo o = unitInfo.mWordInfos.get(i);
            View view = mInflater.inflate(com.buang.welewolf.R.layout.layout_assign_unit_word_item, viewHolder.mFlowLayout, false);
            final TextView textView = (TextView) view.findViewById(com.buang.welewolf.R.id.assign_word_item_text);
            textView.setText(o.content);
            final ImageView select = (ImageView) view.findViewById(com.buang.welewolf.R.id.assign_word_item_select);
            if (mSelectWords.contains(o)) {
                textView.setSelected(true);
                select.setVisibility(View.VISIBLE);
            } else {
                textView.setSelected(false);
                select.setVisibility(View.INVISIBLE);
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectWords.contains(o)) {
                        mSelectWords.remove(o);
                        textView.setSelected(false);
                        select.setVisibility(View.INVISIBLE);
                        unitInfo.mSelectCount --;
                    } else {
                        mSelectWords.add(o);
                        textView.setSelected(true);
                        select.setVisibility(View.VISIBLE);
                        unitInfo.mSelectCount ++;
                    }
                    viewHolder.mUnitName.setText(unitInfo.mUnitName + " (已选" + unitInfo.mSelectCount + "词)");
                    updateWordCount();
                }
            });
            viewHolder.mFlowLayout.addView(view);
        }

        mListView.addView(convertView, index);
    }

    /**
     * 更新底部
     */
    private void updateWordCount() {
        if (matchType.equals(OnlineCompetitionListInfo.CompetitionItem.TYPE_LOOP_MATCH)) {
            mMakeTxt.setText("确认发布" + mSelectWords.size() + "词");
            mCountView.setText("比赛单词 (共选" + mSelectWords.size() + "词)");
        } else {
            mCountView.setText("测验单词 (共选" + mSelectWords.size() + "词)");
            mMakeTxt.setText("确认发布" + mSelectWords.size() + "词(约" + (mSelectWords.size() * mSelectTypes.size()) + "题)");
        }
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getAssignUnitListUrl(matchType);
        OnlineAssignUnitInfo result = new DataAcquirer<OnlineAssignUnitInfo>().acquire(url, new OnlineAssignUnitInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        updateData((OnlineAssignUnitInfo) result);
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
    }

    class AssignUnitAdapter extends SingleTypeAdapter<OnlineUnitInfo> {

        public AssignUnitAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_assign_unit_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mIndex = (TextView) convertView.findViewById(com.buang.welewolf.R.id.assign_unit_item_index);
                viewHolder.mUnitName = (TextView) convertView.findViewById(com.buang.welewolf.R.id.assign_unit_item_name);
                viewHolder.mUnitBg = convertView.findViewById(com.buang.welewolf.R.id.assign_unit_item_select_bg);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OnlineUnitInfo unitInfo = getItem(position);
            viewHolder.mUnitName.setText(unitInfo.unitAbbr);
            viewHolder.mIndex.setText("" + unitInfo.unitNum);
            if (mSelectUnits.contains(unitInfo)) {
                viewHolder.mIndex.setTextColor(getResources().getColor(com.buang.welewolf.R.color.white));
                viewHolder.mUnitName.setTextColor(getResources().getColor(com.buang.welewolf.R.color.white));
                viewHolder.mUnitBg.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mIndex.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_main));
                viewHolder.mUnitName.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_text_main));
                viewHolder.mUnitBg.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    class AssignTypeAdapter extends SingleTypeAdapter<OnlineCompQuestionPreviewInfo.QuestionTypeItem> {

        public AssignTypeAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_assign_type_item, null);
                viewHolder = new ViewHolder();
                viewHolder.mUnitName = (TextView) convertView.findViewById(com.buang.welewolf.R.id.assign_type_item_text);
                viewHolder.mTypeIcon = (ImageView) convertView.findViewById(com.buang.welewolf.R.id.assign_type_icon);
                viewHolder.mTypeSelect = (ImageView) convertView.findViewById(com.buang.welewolf.R.id.assign_word_item_select);
                viewHolder.mUnitBg = convertView.findViewById(com.buang.welewolf.R.id.assign_type_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OnlineCompQuestionPreviewInfo.QuestionTypeItem item = getItem(position);
            viewHolder.mUnitName.setText(item.typeName);
            viewHolder.mTypeIcon.setImageResource(SubjectUtils.getQuestionTypeIcon(item.type));
            if (mSelectTypes.contains(item)) {
                viewHolder.mTypeSelect.setVisibility(View.VISIBLE);
                viewHolder.mUnitBg.setSelected(true);
            } else {
                viewHolder.mTypeSelect.setVisibility(View.GONE);
                viewHolder.mUnitBg.setSelected(false);
            }

            return convertView;
        }
    }

    class AssignWordAdapter extends SingleTypeAdapter<OnlineUnitInfo> {
        LayoutInflater mInflater;

        public AssignWordAdapter(Context context) {
            super(context);
            mInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            final ViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = View.inflate(getActivity(), R.layout.layout_assign_word_item, null);
//                viewHolder = new ViewHolder();
//                viewHolder.mUnitName = (TextView) convertView.findViewById(R.id.assign_unit_item_count);
//                viewHolder.mDesc = (TextView) convertView.findViewById(R.id.assign_unit_item_desc);
//                viewHolder.mFlowLayout = (TagFlowLayout) convertView.findViewById(R.id.assign_unit_item_flowlayout);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            OnlineUnitInfo unitInfo = getItem(position);
//            viewHolder.mUnitName.setText(unitInfo.mUnitName + " (已选" + unitInfo.mWordInfos.size() + "词)");
//            if (unitInfo.isWeak) {
//                viewHolder.mDesc.setVisibility(View.VISIBLE);
//                viewHolder.mDesc.setText("根据班级单词练习情况推荐薄弱单词");
//            } else {
//                viewHolder.mDesc.setVisibility(View.GONE);
//            }
//
//            viewHolder.mFlowLayout.removeAllViews();
//            viewHolder.mFlowLayout.setAdapter(new TagAdapter<OnlineWordInfo>(unitInfo.mWordInfos) {
//                @Override
//                public View getView(final FlowLayout parent, int position, final OnlineWordInfo o) {
//                    View view = mInflater.inflate(R.layout.layout_assign_unit_word_item, viewHolder.mFlowLayout, false);
//                    final TextView textView = (TextView) view.findViewById(R.id.assign_word_item_text);
//                    textView.setText(o.content);
//                    final ImageView select = (ImageView) view.findViewById(R.id.assign_word_item_select);
//                    if (mSelectWords.contains(o)) {
//                        textView.setSelected(true);
//                        select.setVisibility(View.VISIBLE);
//                    } else {
//                        textView.setSelected(false);
//                        select.setVisibility(View.INVISIBLE);
//                    }
//                    textView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (mSelectWords.contains(o)) {
//                                mSelectWords.remove(o);
//                                textView.setSelected(false);
//                                select.setVisibility(View.INVISIBLE);
//                            } else {
//                                mSelectWords.add(o);
//                                textView.setSelected(true);
//                                select.setVisibility(View.VISIBLE);
//                            }
//                            updateWordCount();
//                        }
//                    });
//                    return view;
//                }
//            });

            return convertView;
        }
    }

    class ViewHolder {
        public TextView mUnitName;
        public TextView mDesc;
        public FlowLayout mFlowLayout;
        public TextView mIndex;
        public View mUnitBg;

        public ImageView mTypeIcon;
        public ImageView mTypeSelect;
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (mLoginService != null) {
            mLoginService.getServiceObvserver().removeUpdateJiaocaiListener(updateJiaocaiListener);
        }
    }
}
