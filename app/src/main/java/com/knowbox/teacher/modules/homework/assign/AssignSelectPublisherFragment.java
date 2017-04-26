/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.knowbox.teacher.modules.homework.assign;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.App;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineTeachingMaterialInfo;
import com.knowbox.teacher.base.bean.OnlineTeachingMaterialInfo.ChooseItem;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择教材出版社
 *
 * @author LiuYu
 */
public class AssignSelectPublisherFragment extends BaseUIFragment<UIFragmentHelper> {

    public static boolean noCache = false;

    private OnlineTeachingMaterialInfo mTearchingInfo;

    private ListView mPublisherListView;
    private AssignPublisherAdapter mAdapter;
    private List<ChooseItem> mPublisherItems;
    private boolean isForceSetting;
    private boolean isNewUser;
    private String mPublishId;
    private boolean isCanBack;
    private String clazzName;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        isForceSetting = getArguments().getBoolean(ConstantsUtils.FORCE_SETTING, false);
        if (isForceSetting) {
            setSlideable(false);
        } else {
            setSlideable(true);
        }
        isNewUser = getArguments().getBoolean(ConstantsUtils.IS_NEWUSER, false);
        clazzName = getArguments().getString(ConstantsUtils.CLAZZ_NAME);
        mPublisherItems = new ArrayList<ChooseItem>();
        mPublishId = Utils.getLoginUserItem().mEditionId;
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("选择教材");
        View view = View.inflate(getActivity(), R.layout.layout_assign_publisher_setting,
                null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        if (isForceSetting) {
            getUIFragmentHelper().getTitleBar().setBackBtnVisible(false);
        }
        mPublisherListView = (ListView) view.findViewById(R.id.assign_setting_listview);
        mAdapter = new AssignPublisherAdapter(getActivity());
        mPublisherListView.setAdapter(mAdapter);
        mPublisherListView.setOnItemClickListener(mOnItemClickListener);
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDefaultData(PAGE_FIRST);
            }
        }, 200);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle bundle = new Bundle();
            bundle.putString("publisherid", mPublisherItems.get(position).getValue());
            bundle.putString("publishername", mPublisherItems.get(position).getName());
            bundle.putSerializable("teachingInfo", mTearchingInfo);
            bundle.putString(ConstantsUtils.CLAZZ_NAME, clazzName);
            AssignSelectRequiredBookFragment fragment = (AssignSelectRequiredBookFragment) Fragment
                    .instantiate(getActivity(),
                            AssignSelectRequiredBookFragment.class.getName(), bundle);
            showFragment(fragment);
        }
    };

    /**
     * 更新出版社信息
     */
    private void updatePublisherItem() {
        if (mTearchingInfo == null || mTearchingInfo.mChooseItemPairs == null)
            return;
        mPublisherItems = mTearchingInfo.mChooseItemPairs;
        mAdapter.setItems(mPublisherItems);
    }


    @Override
    public void onPreAction(int action, int pageNo) {
        getUIFragmentHelper().getLoadingView().showHomeworkLoading();
    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        if (noCache) {
            return null;
        }
        return new UrlModelPair(OnlineServices.getTeachMaterialUrl(), new OnlineTeachingMaterialInfo());
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        String url = OnlineServices.getTeachMaterialUrl();
        OnlineTeachingMaterialInfo result = new DataAcquirer<OnlineTeachingMaterialInfo>()
                .acquire(url, new OnlineTeachingMaterialInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        showContent();
        if (((OnlineTeachingMaterialInfo) result).mChooseItemPairs != null
                && ((OnlineTeachingMaterialInfo) result).mChooseItemPairs.size() > 0) {
            mTearchingInfo = (OnlineTeachingMaterialInfo) result;
            updatePublisherItem();
            noCache = false;
        } else {
            UserItem mUserItem = Utils.getLoginUserItem();
            String classPart;
            if (null != mUserItem) {
                classPart = SubjectUtils.getGrade(mUserItem.gradePart) + "英语";
            }else {
                classPart = "当前科目";
            }
            getUIFragmentHelper().getEmptyView().showEmpty(R.drawable.icon_empty_nodata, classPart + "暂时没有教材\n您可以选择其他科目", null);
            isCanBack = !isNewUser;
            getUIFragmentHelper().getTitleBar().setBackBtnVisible(true);
        }
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        if (!noCache && ((OnlineTeachingMaterialInfo) result).mChooseItemPairs != null
                && ((OnlineTeachingMaterialInfo) result).mChooseItemPairs.size() > 0) {
            mTearchingInfo = (OnlineTeachingMaterialInfo) result;
            updatePublisherItem();
        }
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        super.onFail(action, pageNo, result, params);
        if (isForceSetting) {
            isCanBack = !isNewUser;
            if (isCanBack) {
                getUIFragmentHelper().getTitleBar().setBackBtnVisible(true);
            }
        }
    }

    //教辅出版社的适配器
    private class AssignPublisherAdapter extends SingleTypeAdapter<ChooseItem> {

        public AssignPublisherAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(),
                        R.layout.layout_assign_teach_publisher_item, null);
                holder.mPublisherName = (TextView) convertView
                        .findViewById(R.id.assign_publisher_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mPublisherName.setText(getItem(position).getName());
            if (!TextUtils.isEmpty(mPublishId) && mPublishId.equals(getItem(position).getValue())) {
                holder.mPublisherName.setTextColor(getActivity().getResources().getColor(R.color.color_main_app));
            }else {
                holder.mPublisherName.setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mPublisherName;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isCanBack) {
            return super.onKeyDown(keyCode, event);
        } else {
            if (isForceSetting && null != getActivity()) {
                ((App) BaseApp.getAppContext()).exit();
                getActivity().finish();
            }
            return isForceSetting || super.onKeyDown(keyCode, event);
        }
    }
}
