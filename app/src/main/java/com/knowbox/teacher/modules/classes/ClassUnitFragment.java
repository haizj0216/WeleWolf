package com.knowbox.teacher.modules.classes;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineUnitInfo;
import com.knowbox.teacher.base.bean.OnlineUnitListInfo;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.services.updateclass.ClassGroupsChangeListener;
import com.knowbox.teacher.base.services.updateclass.UpdateClassService;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.login.services.UpdateJiaocaiListener;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.widgets.headerviewpager.InnerListView;
import com.knowbox.teacher.widgets.headerviewpager.InnerScroller;
import com.knowbox.teacher.widgets.headerviewpager.InnerScrollerContainer;
import com.knowbox.teacher.widgets.headerviewpager.OuterScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/2/13.
 */
public class ClassUnitFragment extends BaseUIFragment<UIFragmentHelper> implements InnerScrollerContainer {

    private InnerListView mListView;
    protected OuterScroller mOuterScroller;
    protected int mIndex;
    private ClassUnitAdapter mAdapter;
    private View mHeaderView;

    private ClassInfoItem mClassInfoItem;
    private UpdateClassService updateClassService;
    private OnlineUnitListInfo mOnlineUnitListInfo;
    private LoginService mLoginService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
        updateClassService = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
        updateClassService.getObserver().addClassGroupChangeListener(classGroupsChangeListener);
        mLoginService = (LoginService) getSystemService(LoginService.SERVICE_NAME);
        mLoginService.getServiceObvserver().addUpdatejiaocaiListener(updateJiaocaiListener);
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
        mAdapter = new ClassUnitAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        updateTeaching();
    }

    private void updateTeaching() {
        UserItem userItem = Utils.getLoginUserItem();
        if (!TextUtils.isEmpty(userItem.mBookName) && !TextUtils.isEmpty(userItem.mEditionName)) {
            if (mHeaderView == null) {
                mHeaderView = View.inflate(getActivity(), R.layout.layout_class_unit_header, null);
                mListView.addHeaderView(mHeaderView);
            }
            ((TextView) mHeaderView.findViewById(R.id.unit_header_jiaocai)).setText("-  " +
                    userItem.mBookName + userItem.mEditionName + "  -");
        } else {
            showUnitEmpty();
        }
    }

    /*private void initData() {
        List<List<OnlineUnitInfo>> mUnitInfos = new ArrayList<List<OnlineUnitInfo>>();
        int count = 13;
        int line = 3;
        int row = count / line == 0 ? count / line : count / line + 1;
        if (count < line) {
            row = 1;
        }
        for (int j = 0; j < row; j++) {
            List<OnlineUnitInfo> unitInfos = new ArrayList<OnlineUnitInfo>();
            for (int i = 0; i < line; i++) {
                if (i + j * line < count) {
                    OnlineUnitInfo unitInfo = new OnlineUnitInfo();
                    unitInfo.mUnitName = "Unit";
                    unitInfo.wordsCount = 50;
                    unitInfo.learnedCount = 10 + i + j * line;
                    unitInfo.mIndex = i + j * line;
                    unitInfos.add(unitInfo);
                } else {
                    break;
                }
            }
            mUnitInfos.add(unitInfos);
        }
        mAdapter.setUnitInfos(mUnitInfos);
    }
*/
    /**
     * 显示空页面
     */
    private void showUnitEmpty() {
        mAdapter.setEmpty(true);
    }

    /**
     * 更新单元列表
     */
    private void updateUnitInfo() {
        if (mOnlineUnitListInfo.mUnitList.size() == 0) {
            mAdapter.setEmpty(true);
            return;
        }
        List<List<OnlineUnitInfo>> mUnitInfos = new ArrayList<List<OnlineUnitInfo>>();
        List<OnlineUnitInfo> mAllUnits = mOnlineUnitListInfo.mUnitList;
        int count = mAllUnits.size();
        int line = 3;
        int row = count / line == 0 ? count / line : count / line + 1;
        if (count < line) {
            row = 1;
        }
        for (int j = 0; j < row; j++) {
            List<OnlineUnitInfo> unitInfos = new ArrayList<OnlineUnitInfo>();
            for (int i = 0; i < line; i++) {
                if (i + j * line < count) {
                    unitInfos.add(mAllUnits.get(i + j * line));
                } else {
                    break;
                }
            }
            mUnitInfos.add(unitInfos);
        }
        mAdapter.setUnitInfos(mUnitInfos);
    }

    private void onClassChanged(ClassInfoItem classInfoItem) {
        if (classInfoItem == mClassInfoItem) {
            return;
        }
        if (classInfoItem == null) {
            return;
        }
        mClassInfoItem = classInfoItem;
        loadDefaultData(PAGE_FIRST);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getClassUnitInfoUrl(mClassInfoItem.classId);
        OnlineUnitListInfo result = new DataAcquirer<OnlineUnitListInfo>().acquire(url, new OnlineUnitListInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        mOnlineUnitListInfo = (OnlineUnitListInfo) result;
        updateTeaching();
        updateUnitInfo();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        showContent();
        showUnitEmpty();
    }

    private ClassGroupsChangeListener classGroupsChangeListener = new ClassGroupsChangeListener() {
        @Override
        public void refreshClassGroupList() {
        }

        @Override
        public void createClassGroupSuccess(ClassInfoItem classInfoItem) {
        }

        @Override
        public void deleteClassGroupSuccess(ClassInfoItem classInfoItem) {
        }

        @Override
        public void updateClassGroupSuccess(ClassInfoItem classInfoItem) {
        }

        @Override
        public void onClassSelectChanged(ClassInfoItem classInfoItem) {
            onClassChanged(classInfoItem);
        }
    };

    UpdateJiaocaiListener updateJiaocaiListener = new UpdateJiaocaiListener() {
        @Override
        public void onUpdateSuccess(final UserItem userItem, String clazzName) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadDefaultData(PAGE_FIRST);
                }
            });

        }

        @Override
        public void onUpdateFailed(String error, String clazzName) {

        }
    };

    class ClassUnitAdapter extends BaseAdapter {

        private List<List<OnlineUnitInfo>> mUnitInfos = new ArrayList<List<OnlineUnitInfo>>();
        private boolean isEmpty = false;

        public ClassUnitAdapter(Context context) {
        }

        @Override
        public int getCount() {
            return getEmpty() ? 1 : mUnitInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mUnitInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_class_unit_layout_ltem, null);
                viewHolder = new ViewHolder();
                viewHolder.mUnitLayout = (LinearLayout) convertView.findViewById(R.id.unit_item_layout);
                viewHolder.mEmptyView = (LinearLayout) convertView.findViewById(R.id.unit_empty);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (getEmpty()) {
                viewHolder.mEmptyView.setVisibility(View.VISIBLE);
                viewHolder.mUnitLayout.setVisibility(View.GONE);
            } else {
                viewHolder.mUnitLayout.setVisibility(View.VISIBLE);
                viewHolder.mEmptyView.setVisibility(View.GONE);
                viewHolder.mUnitLayout.removeAllViews();

                List<OnlineUnitInfo> unitInfos = mUnitInfos.get(position);
                for (int i = 0; i < unitInfos.size(); i++) {
                    View view = View.inflate(getActivity(), R.layout.layout_class_unit_item, null);
                    TextView mUnitTotal = (TextView) view.findViewById(R.id.class_unit_item_total);
                    TextView mUnitGrasp = (TextView) view.findViewById(R.id.class_unit_item_grasp);
                    TextView mUnitName = (TextView) view.findViewById(R.id.class_unit_item_name);
                    TextView mUnitIndex = (TextView) view.findViewById(R.id.class_unit_item_index);

                    final OnlineUnitInfo unitInfo = unitInfos.get(i);
                    mUnitName.setText(unitInfo.unitAbbr);
                    mUnitTotal.setText("/" + mClassInfoItem.studentNum);
                    mUnitGrasp.setText("" + unitInfo.studentsLearnedCount);
                    mUnitIndex.setText(unitInfo.unitNum);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(UIUtils.dip2px(112), UIUtils.dip2px(154));
                    params.bottomMargin = UIUtils.dip2px(10);
                    int margin = (UIUtils.getWindowWidth(getActivity()) - UIUtils.dip2px(112) * 3) / 8;
                    params.leftMargin = margin;
                    params.rightMargin = margin;
                    viewHolder.mUnitLayout.addView(view, params);

                    RelativeLayout.LayoutParams layoutLp = (RelativeLayout.LayoutParams) viewHolder.mUnitLayout.getLayoutParams();
                    layoutLp.leftMargin = margin;
                    layoutLp.rightMargin = margin;
                    viewHolder.mUnitLayout.setLayoutParams(layoutLp);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openUnitInfoFragment(unitInfo);
                        }
                    });
                }
            }

            return convertView;
        }

        public List<List<OnlineUnitInfo>> getUnitInfos() {
            return mUnitInfos;
        }

        public void setUnitInfos(List<List<OnlineUnitInfo>> unitInfos) {
            isEmpty = false;
            this.mUnitInfos = unitInfos;
            notifyDataSetChanged();
        }

        public boolean getEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
            notifyDataSetChanged();
        }
    }

    private void openUnitInfoFragment(OnlineUnitInfo unitInfo) {
        UmengConstant.reportUmengEvent(UmengConstant.EVENT_HOME_UNIT_CARD, null);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ConstantsUtils.KEY_UNITINFO, unitInfo);
        mBundle.putParcelable(ConstantsUtils.KEY_CLASSINFOITEM, mClassInfoItem);
        ClassUnitInfoFragment classUnitInfoFragment = ClassUnitInfoFragment.newFragment(getActivity()
                , ClassUnitInfoFragment.class, mBundle);
        showFragment(classUnitInfoFragment);

    }

    class ViewHolder {
        public LinearLayout mUnitLayout;
        public LinearLayout mEmptyView;
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

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (updateClassService != null) {
            updateClassService.getObserver().removeClassGroupChangeListener(classGroupsChangeListener);
        }
        if (mLoginService != null) {
            mLoginService.getServiceObvserver().removeUpdateJiaocaiListener(updateJiaocaiListener);
        }
    }
}
