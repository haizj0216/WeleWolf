package com.knowbox.teacher.modules.classes;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineSchoolTeacherInfo;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.widgets.RoundDisplayer;

/**
 * Created by weilei on 15/7/2.
 */
public class ClassTransferSelectFragment extends BaseUIFragment<UIFragmentHelper> {

    private View mPhoneSelectView;
    private View mMiddleSchoolView;
    private View mHighSchoolView;

    private ListView mMiddleListView;
    private ListView mHighListView;
    private TeacherSelectAdapter mMiddleAdapter;
    private TeacherSelectAdapter mHighAdapter;

    private OnlineSchoolTeacherInfo mOnlineSchoolTeacherInfo;
    private View mGradeSchoolView;
    private ListView mGradeListView;
    private TeacherSelectAdapter mGradeAdapter;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("选择教师");
        View view = View.inflate(getActivity(), R.layout.layout_class_transfer_select, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mPhoneSelectView = view.findViewById(R.id.class_transfer_phone);
        mMiddleSchoolView = view.findViewById(R.id.class_transfer_middleschool);
        mHighSchoolView = view.findViewById(R.id.class_transfer_highschool);
        mGradeSchoolView = view.findViewById(R.id.class_transfer_gradeschool);

        mGradeListView = (ListView) view.findViewById(R.id.class_transfer_gradeschool_list);
        mMiddleListView = (ListView) view.findViewById(R.id.class_transfer_middleschool_list);
        mHighListView = (ListView) view.findViewById(R.id.class_transfer_highschool_list);

        mMiddleAdapter = new TeacherSelectAdapter(getActivity());
        mHighAdapter = new TeacherSelectAdapter(getActivity());
        mGradeAdapter = new TeacherSelectAdapter(getActivity());
        mHighListView.setAdapter(mHighAdapter);
        mMiddleListView.setAdapter(mMiddleAdapter);
        mGradeListView.setAdapter(mGradeAdapter);

        mHighListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openClassTransferProcessFragment(mHighAdapter.getItem(position));
            }
        });

        mMiddleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openClassTransferProcessFragment(mMiddleAdapter.getItem(position));
            }
        });

        mGradeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openClassTransferProcessFragment(mGradeAdapter.getItem(position));
            }
        });

        mPhoneSelectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransferPhoneFragment();
            }
        });

        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDefaultData(1);
            }
        }, 200);

    }

    /**
     * 打开班级转移页
     *
     * @param info
     */
    private void openClassTransferProcessFragment(OnlineSchoolTeacherInfo.TeacherInfo info) {
        Bundle mBundle = new Bundle();
        info.mSchool = Utils.getLoginUserItem().schoolName;
        mBundle.putSerializable("teacherInfo", info);
        mBundle.putParcelable("class", getArguments().getParcelable("class"));
        ClassTransferProcessFragment fragment = (ClassTransferProcessFragment) Fragment.
                instantiate(getActivity(), ClassTransferProcessFragment.class.getName(), mBundle);
        showFragment(fragment);
    }

    private void updateAllAdapter() {
        if (mOnlineSchoolTeacherInfo != null) {
            if (mOnlineSchoolTeacherInfo.mHighTeachers != null && mOnlineSchoolTeacherInfo.mHighTeachers.size() > 0) {
                mHighAdapter.setItems(mOnlineSchoolTeacherInfo.mHighTeachers);
                mHighSchoolView.setVisibility(View.VISIBLE);
            } else {
                mHighSchoolView.setVisibility(View.GONE);
            }

            if (mOnlineSchoolTeacherInfo.mMiddleTeachers != null && mOnlineSchoolTeacherInfo.mMiddleTeachers.size() > 0) {
                mMiddleAdapter.setItems(mOnlineSchoolTeacherInfo.mMiddleTeachers);
                mMiddleSchoolView.setVisibility(View.VISIBLE);
            } else {
                mMiddleSchoolView.setVisibility(View.GONE);
            }

            if (mOnlineSchoolTeacherInfo.mGradeTeachers != null && mOnlineSchoolTeacherInfo.mGradeTeachers.size() > 0) {
                mGradeAdapter.setItems(mOnlineSchoolTeacherInfo.mGradeTeachers);
                mGradeSchoolView.setVisibility(View.VISIBLE);
            } else {
                mGradeSchoolView.setVisibility(View.GONE);
            }
        }
    }

    private void openTransferPhoneFragment() {
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("class", getArguments().getParcelable("class"));
        ClassTransferPhoneFragment fragment = (ClassTransferPhoneFragment)
                Fragment.instantiate(getActivity(), ClassTransferPhoneFragment.class.getName(), mBundle);
        showFragment(fragment);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getSchoolTeacherListUrl(Utils.getToken());
        OnlineSchoolTeacherInfo result = new DataAcquirer<OnlineSchoolTeacherInfo>().
                acquire(url, new OnlineSchoolTeacherInfo(), -1);
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result) {
        super.onGet(action, pageNo, result);
        mOnlineSchoolTeacherInfo = (OnlineSchoolTeacherInfo) result;
        updateAllAdapter();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result) {
        super.onFail(action, pageNo, result);
    }

    class TeacherSelectAdapter extends SingleTypeAdapter<OnlineSchoolTeacherInfo.TeacherInfo> {

        public TeacherSelectAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mViewHolder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_class_transfer_select_item, null);
                mViewHolder = new ViewHolder();
                mViewHolder.mHeaderView = (ImageView) convertView
                        .findViewById(R.id.class_transfer_teacher_item_icon);
                mViewHolder.mNameView = (TextView) convertView
                        .findViewById(R.id.class_transfer_teacher_item_name);
                mViewHolder.mPhoneView = (TextView) convertView
                        .findViewById(R.id.class_transfer_teacher_item_phone);
                mViewHolder.mSexView = (TextView) convertView
                        .findViewById(R.id.class_transfer_teacher_item_sex);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            OnlineSchoolTeacherInfo.TeacherInfo item = getItem(position);
            ImageFetcher.getImageFetcher().loadImage(item.mHeadPhoto,
                    mViewHolder.mHeaderView, R.drawable.bt_chat_teacher_default,
                    new RoundDisplayer());
            mViewHolder.mNameView.setText(item.mUserName);
            mViewHolder.mPhoneView.setText(item.mMobile);
            mViewHolder.mSexView.setText("1".equals(item.mSex) ? "男" : "女");
            return convertView;
        }
    }

    class ViewHolder {
        ImageView mHeaderView;
        TextView mNameView;
        TextView mPhoneView;
        TextView mSexView;
    }
}
