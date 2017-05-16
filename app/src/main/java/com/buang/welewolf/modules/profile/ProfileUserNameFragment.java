package com.buang.welewolf.modules.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.buang.welewolf.base.bean.OnlineLoginInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.widgets.CleanableEditText;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.base.utils.UIUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weilei on 15/8/7.
 */
public class ProfileUserNameFragment extends BaseUIFragment<UIFragmentHelper> {

    private CleanableEditText mEditText;
    private String mName;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("修改姓名");
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_user_name, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mEditText = (CleanableEditText) view.findViewById(com.buang.welewolf.R.id.profile_edit_username);
        mEditText.setHintTextColor(0xffc9c8ce);
        mEditText.getEditText().setTextColor(0xff5f5f5f);
        mEditText.getEditText().setText(getArguments().getString("name"));
        mEditText.setMaxLength(15);
        mEditText.addFilter(new CleanableEditText.UserNameLoginFilter());
        mEditText.setFocusable(true);
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        getUIFragmentHelper().getTitleBar().setRightMoreTxt("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserName(mEditText.getText());
            }
        });
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        UIUtils.hideInputMethod(getActivity());
    }

    private void updateUserName(String userName) {
        UIUtils.hideInputMethod(getActivity());
        userName = userName.replace(" ", "");
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShortToast(getActivity(), "姓名不能为空");
            return;
        }
        mName = userName;
        loadDefaultData(1, userName);
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
        getLoadingView().showLoading();
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getUpdateUserInfo();
        JSONObject object = new JSONObject();
        try {
            object.put("user_name", (String) params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = object.toString();
        OnlineLoginInfo info = new DataAcquirer<OnlineLoginInfo>()
                .post(url, data, new OnlineLoginInfo());
        return info;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result) {
        super.onGet(action, pageNo, result);
        showContent();
        if (mOnUserNameEditListener != null) {
            mOnUserNameEditListener.onUserNameEdit(mName);
        }
        finish();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result) {
        super.onFail(action, pageNo, result);
        showContent();
        ToastUtils.showShortToast(BaseApp.getAppContext(), "修改名称失败");
    }

    public interface OnUserNameEditListener {
        public void onUserNameEdit(String newName);
    }

    private OnUserNameEditListener mOnUserNameEditListener;
    public void setOnUserNameEditListener(OnUserNameEditListener mOnUserNameEditListener) {
        this.mOnUserNameEditListener = mOnUserNameEditListener;
    }
}
