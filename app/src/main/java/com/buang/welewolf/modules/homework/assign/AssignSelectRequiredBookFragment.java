package com.buang.welewolf.modules.homework.assign;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineTeachingMaterialInfo;
import com.buang.welewolf.base.utils.PreferencesController;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.buang.welewolf.R;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;

import java.util.List;

/**
 * 必修一
 * Created by LiuYu on 2015/12/31.
 */
public class AssignSelectRequiredBookFragment extends BaseUIFragment<UIFragmentHelper> {

    private OnlineTeachingMaterialInfo mTearchingInfo;
    private AssignRequireBookAdapter mAdapter;
    private List<OnlineTeachingMaterialInfo.ChooseItem> mMaterials;//出版社下面的教材的集合
    private String mPublisherId;
    private String mPublisherName;
    private String mJiaocaiId;
    private LoginService mLoginService;
    private String clazzName;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPublisherId = bundle.getString("publisherid");
            mPublisherName = bundle.getString("publishername");
            clazzName = bundle.getString(ConstantsUtils.CLAZZ_NAME);
            mTearchingInfo = (OnlineTeachingMaterialInfo) bundle.getSerializable("teachingInfo");
        }
        mJiaocaiId = Utils.getLoginUserItem().mBookId;
        mLoginService = (LoginService) getActivity().getSystemService(LoginService.SERVICE_NAME);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_assign_show_requirebook, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getTitleBar().setTitle(mPublisherName);
        ListView mListView = (ListView) view.findViewById(R.id.assign_requirebooks_listview);
        mAdapter = new AssignRequireBookAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PreferencesController.setStringValue(ConstantsUtils.BUNK_PUBLISHER_NAME, mPublisherName);
                PreferencesController.setStringValue(ConstantsUtils.BUNK_PUBLISHER_VALUE, mPublisherId);
                PreferencesController.setStringValue(ConstantsUtils.BUNK_REQUIREBOOK_NAME, mMaterials.get(position).getName());
                PreferencesController.setStringValue(ConstantsUtils.BUNK_REQUIREBOOK_VALUE, mMaterials.get(position).getValue());
                mLoginService.updateJiaoCai(clazzName);
            }
        });
        updateRequireBooksItem();
//        loadDefaultData(PAGE_FIRST);
    }

    /*@Override
    public void onPreAction(int action, int pageNo) {
        if (mTearchingInfo == null)
            getUIFragmentHelper().getLoadingView().showHomeworkLoading();
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
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        return new UrlModelPair(OnlineServices.getTeachMaterialUrl(),
                new OnlineTeachingMaterialInfo());
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        if (((OnlineTeachingMaterialInfo) result).mChooseItemPairs != null
                && ((OnlineTeachingMaterialInfo) result).mChooseItemPairs.size() > 0) {
            mTearchingInfo = (OnlineTeachingMaterialInfo) result;
            updateRequireBooksItem();
        }
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (((OnlineTeachingMaterialInfo) result).mChooseItemPairs != null
                && ((OnlineTeachingMaterialInfo) result).mChooseItemPairs.size() > 0) {
            mTearchingInfo = (OnlineTeachingMaterialInfo) result;
            updateRequireBooksItem();
        } else {
            getUIFragmentHelper().getEmptyView().showEmpty(R.drawable.icon_empty_nodata, "暂无教辅", null);
        }
    }*/

    private class AssignRequireBookAdapter extends SingleTypeAdapter<OnlineTeachingMaterialInfo.ChooseItem> {

        public AssignRequireBookAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.layout_assign_books_item, null);
                holder.mRequireBookName = (TextView) convertView.findViewById(R.id.assign_book_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            OnlineTeachingMaterialInfo.ChooseItem item = getItem(position);
            holder.mRequireBookName.setText(item.getName());
            if (!TextUtils.isEmpty(mJiaocaiId) && mJiaocaiId.equals(getItem(position).getValue())) {
                holder.mRequireBookName.setTextColor(getActivity().getResources().getColor(R.color.color_main_app));
            }else {
                holder.mRequireBookName.setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mRequireBookName;
    }

    /**
     * 更新教辅信息
     */
    private void updateRequireBooksItem() {
        if (mTearchingInfo == null || mTearchingInfo.mChooseItemPairs == null)
            return;
        List<OnlineTeachingMaterialInfo.ChooseItem> mPublisherItems = mTearchingInfo.mChooseItemPairs;
        for (OnlineTeachingMaterialInfo.ChooseItem materialItem : mPublisherItems) {
            if (!TextUtils.isEmpty(mPublisherId) && materialItem.getValue().equals(mPublisherId)) {
                mMaterials = materialItem.mSubList;
                if (mMaterials.size() > 0) {
                    mAdapter.setItems(mMaterials);
                } else {
                    getUIFragmentHelper().getEmptyView().showEmpty(R.drawable.icon_empty_nodata, "暂无教材", null);
                }
            }
        }
    }


}
