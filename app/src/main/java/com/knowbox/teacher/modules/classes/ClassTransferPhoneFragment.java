package com.knowbox.teacher.modules.classes;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineSchoolTeacherInfo;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.ActionUtils;
import com.knowbox.teacher.base.utils.StringUtils;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.CleanableEditText;

/**
 * Created by weilei on 15/7/2.
 */
public class ClassTransferPhoneFragment extends BaseUIFragment {

    private static int ACTION_VERIFY_MOBILE = 1;
    private static int ACTION_INVITATION = 2;

    private CleanableEditText mPhoneEdit;
    private Button mNextButton;

    private OnlineSchoolTeacherInfo.TeacherInfo mTeacherInfo;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        setSlideable(true);
        super.onCreateImpl(savedInstanceState);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("手机邀请转移");
        View view = View.inflate(getActivity(), R.layout.layout_class_transfer_phone, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mPhoneEdit = (CleanableEditText) view.findViewById(R.id.class_transfer_phone_edit);
        mPhoneEdit.getEditText().setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
        mPhoneEdit.setInputType(
                InputType.TYPE_CLASS_PHONE | InputType.TYPE_TEXT_VARIATION_PHONETIC);
        mPhoneEdit.setDigist("1234567890");
        mNextButton = (Button) view.findViewById(R.id.class_transfer_phone_next);

        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 11 && StringUtils.isMobilePhoneNumber(s.toString()) &&
                        !s.toString().equals(Utils.getLoginUserItem().loginName)) {
                    mNextButton.setEnabled(true);
                } else {
                    mNextButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
                    ActionUtils.notifyVirtualTip();
                    return;
                }
                loadData(ACTION_VERIFY_MOBILE, 2, mPhoneEdit.getText());
            }
        });
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        UIUtils.hideInputMethod(getActivity());
    }

    private void openTransferProcessFragment(OnlineSchoolTeacherInfo.TeacherInfo info) {
        UIUtils.hideInputMethod(getActivity());
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("teacherInfo", info);
        mBundle.putParcelable("class", getArguments().getParcelable("class"));
        ClassTransferProcessFragment fragment = (ClassTransferProcessFragment) Fragment
                .instantiate(getActivity(), ClassTransferProcessFragment.class.getName(), mBundle);
        showFragment(fragment);
    }

    private void showInvitionDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getMessageDialog(getActivity(), "", "邀请", "取消",
                "", new DialogUtils.OnDialogButtonClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                            loadData(ACTION_INVITATION, 1);
                        }
                        mDialog.dismiss();
                    }
                });
        mDialog.dismiss();
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_VERIFY_MOBILE) {
            String url = OnlineServices.getVerifyMobileUrl(Utils.getToken(), (String) params[0], 1,
                    Utils.getLoginUserItem().gradePart, Utils.getLoginUserItem().subjectCode);
            OnlineSchoolTeacherInfo result = new DataAcquirer<OnlineSchoolTeacherInfo>().
                    acquire(url, new OnlineSchoolTeacherInfo(), -1);
            return result;
        } else if (action == ACTION_INVITATION) {
            String url = OnlineServices.getSendInvitationUrl(Utils.getToken(), (String) params[0]);
            BaseObject result = new DataAcquirer<BaseObject>().acquire(url,
                    new BaseObject(), -1);
            return result;
        }
        return null;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result) {
        super.onGet(action, pageNo, result);
        showContent();
        if (action == ACTION_VERIFY_MOBILE) {
            mTeacherInfo = ((OnlineSchoolTeacherInfo) result).mTeacherInfo;
            openTransferProcessFragment(mTeacherInfo);
        } else {
            ToastUtils.showShortToast(BaseApp.getAppContext(), "邀请成功");
        }
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result) {
        super.onFail(action, pageNo, result);
        showContent();
        if (action == ACTION_VERIFY_MOBILE && result.getRawResult().equals("20505")) {
            showInvitionDialog();
        }
    }
}
