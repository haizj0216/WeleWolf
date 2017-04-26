/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.knowbox.teacher.modules.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;
import com.knowbox.teacher.App;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineLoginInfo;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.database.tables.UserTable;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.ActionUtils;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.homework.assign.AssignSelectPublisherFragment;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.main.MainProfileFragment;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LiuYu on 2016/3/1.
 */
public class MainSelectSubjectFragment extends BaseUIFragment<UIFragmentHelper> {

    public static final int FROM_USERINFO = 2;
    public static final int FROM_MAINACTIVITY = 3;

    private static final int ACTION_UPDATE_INFO = 0;

    private UserItem mUserItem;

    private String mGradePartID = "20";
    private TextView mMiddleView;
    private TextView mHighView;
    private int mFrom;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        LoginService loginService = (LoginService) BaseApp.getAppContext().getSystemService(LoginService.SERVICE_NAME);
        mFrom = getArguments().getInt(ConstantsUtils.SUBJECT_FROM);
        if (mFrom != FROM_USERINFO) {
            setSlideable(false);
            if (loginService.isLogin()) {
                mUserItem = loginService.getLoginUser();
            }
        } else {
            setSlideable(true);
            mUserItem = loginService.getLoginUser();
        }

        if (mUserItem != null && !TextUtils.isEmpty(mUserItem.gradePart)) {
            mGradePartID = mUserItem.gradePart;
        }

    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_main_subject_part, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("学段科目");
        if (mFrom != FROM_USERINFO) getUIFragmentHelper().getTitleBar().setBackBtnVisible(false);

        view.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mGradePartID)) {
                    submitNewSubject(mGradePartID);
                } else {
                    ToastUtils.showShortToast(getActivity(), "请先选择学段科目！");
                }
            }
        });

        mMiddleView = (TextView) view.findViewById(R.id.subject_grade_middle);
        mHighView = (TextView) view.findViewById(R.id.subject_grade_high);
        mMiddleView.setOnClickListener(mOnClickListener);
        mHighView.setOnClickListener(mOnClickListener);

        if ("20".equals(mGradePartID)) {
            mMiddleView.setSelected(true);
        } else if ("30".equals(mGradePartID)) {
            mHighView.setSelected(true);
        }

//        mGridView = (GridView) view.findViewById(R.id.subject_gridview);
//        mAdapter = new MyGridAdapter(getActivity());
//        mGridView.setAdapter(mAdapter);
//        mAdapter.setItems(subjectItems);
    }

    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.subject_grade_middle:
                    mMiddleView.setSelected(true);
                    mHighView.setSelected(false);
                    mGradePartID = "20";
                    break;
                case R.id.subject_grade_high:
                    ToastUtils.showShortToast(getActivity(), "暂未开放高中学段");
//                    mMiddleView.setSelected(false);
//                    mHighView.setSelected(true);
//                    mGradePartID = "30";
                    break;
            }
        }
    };

    private void submitNewSubject(String gradePart) {
        if (null != mUserItem) {
            if (gradePart.equals(mUserItem.gradePart) && mFrom == FROM_USERINFO) {
                finish();
                return;
            }
            loadData(ACTION_UPDATE_INFO, PAGE_MORE, gradePart);
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_UPDATE_INFO) {
            String url = OnlineServices.getUpdateUserInfo();
            JSONObject object = new JSONObject();
            try {
                object.put("grade_part", (String) params[0]);
//                object.put("subject", SubjectUtils.SUBJECT_CODE_ENGLISH);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data = object.toString();
            if (TextUtils.isEmpty(data))
                return null;
            OnlineLoginInfo info = new DataAcquirer<OnlineLoginInfo>()
                    .post(url, data, new OnlineLoginInfo());
            return info;
        }
        return null;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_UPDATE_INFO) {
            mUserItem.gradePart = mGradePartID;
            mUserItem.mEditionId = "";
            mUserItem.mEditionName = "";
            mUserItem.mBookId = "";
            mUserItem.mBookName = "";
            try {
                if (Utils.getLoginUserItem() != null) {
                    Utils.getLoginUserItem().gradePart = mUserItem.gradePart;
                    Utils.getLoginUserItem().mEditionId = "";
                    Utils.getLoginUserItem().mBookId = "";
                    Utils.getLoginUserItem().mEditionName = "";
                    Utils.getLoginUserItem().mBookName = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateDatabase();
            updateBankInfo();
            MsgCenter.sendLocalBroadcast(new Intent(MainProfileFragment.ACTION_USERINFO_CHANGE));
            ActionUtils.notifySubjectPartChanged(mUserItem.gradePart);
            if (mFrom == FROM_USERINFO) {
                ToastUtils.showShortToast(getActivity(), "修改成功,选择教材");
                openSelectBook();
//                finish();
            }else {
                //选择教材课本
                if (mOnSubjectSelectListener != null) {
                    mOnSubjectSelectListener.onSubjectSelect();
                }
            }
        }
    }

    private void openSelectBook() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ConstantsUtils.FORCE_SETTING, true);
        bundle.putBoolean(ConstantsUtils.IS_NEWUSER, false);
        bundle.putString(ConstantsUtils.CLAZZ_NAME, MainSelectSubjectFragment.class.getName());
        AssignSelectPublisherFragment fragment =AssignSelectPublisherFragment
                .newFragment(getActivity(), AssignSelectPublisherFragment.class, bundle);
        showFragment(fragment);
    }

    /**
     * 刷新数据库用户信息
     */
    private void updateDatabase() {
        UserTable table = DataBaseManager.getDataBaseManager().getTable(
                UserTable.class);
        table.updateByCase(mUserItem, UserTable.USERID + " = ?",
                new String[]{mUserItem.userId});

    }

    private void updateBankInfo() {
        if (mFrom == FROM_USERINFO) {
            PreferencesController.setStringValue(ConstantsUtils.BUNK_PUBLISHER_NAME, "");
            PreferencesController.setStringValue(ConstantsUtils.BUNK_PUBLISHER_VALUE, "");
            PreferencesController.setStringValue(ConstantsUtils.BUNK_REQUIREBOOK_NAME, "");
            PreferencesController.setStringValue(ConstantsUtils.BUNK_REQUIREBOOK_VALUE, "");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mFrom != FROM_USERINFO) {
            if (getActivity() != null) {
                ((App) BaseApp.getAppContext()).exit();
                getActivity().finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface OnSubjectSelectListener {
        public void onSubjectSelect();
    }

    private OnSubjectSelectListener mOnSubjectSelectListener;

    public void setOnSubjectSelectListener(OnSubjectSelectListener listener) {
        mOnSubjectSelectListener = listener;
    }

  /*  private class MyGridAdapter extends SingleTypeAdapter<SubjectItem> {

        public MyGridAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.layout_subject_item, null);
                holder = new ViewHolder();
                holder.mSubejctName = (TextView) convertView.findViewById(R.id.subject_name);
                holder.mSubjectImg = (ImageView) convertView.findViewById(R.id.subject_img);
                holder.mSubjectSelect = (ImageView) convertView.findViewById(R.id.subject_selected_sign);
                holder.mSubjectView = convertView.findViewById(R.id.subject_item_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final SubjectItem subjectItem = getItem(position);

            holder.mSubejctName.setText(subjectItem.subjectName);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), subjectItem.subjectImg);
            holder.mSubjectImg.setImageBitmap(bm);

            if (mSubjectCode == subjectItem.subjectCode) {
                holder.mSubjectSelect.setVisibility(View.VISIBLE);
            } else {
                holder.mSubjectSelect.setVisibility(View.GONE);
            }

            holder.mSubjectView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSubjectCode = subjectItem.subjectCode;
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mSubejctName;
        public ImageView mSubjectImg;
        public ImageView mSubjectSelect;
        public View mSubjectView;
    }*/

}
