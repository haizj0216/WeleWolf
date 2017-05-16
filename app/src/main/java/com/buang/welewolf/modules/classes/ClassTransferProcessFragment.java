package com.buang.welewolf.modules.classes;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineSchoolTeacherInfo;
import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.utils.VirtualClassUtils;
import com.buang.welewolf.widgets.RoundDisplayer;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.buang.welewolf.modules.utils.SubjectUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 班级转移页
 * <p/>
 * Created by weilei on 15/7/2.
 */
public class ClassTransferProcessFragment extends BaseUIFragment<UIFragmentHelper> {

    private OnlineSchoolTeacherInfo.TeacherInfo mTeacherInfo;
    private ClassInfoItem mClassItem;
    private TextView mButtonView;
    private ImageView mTransferStatus;
    private LinearLayout mCancelView;
    private TextView mCancelText;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mTeacherInfo = (OnlineSchoolTeacherInfo.TeacherInfo) getArguments()
                .getSerializable("teacherInfo");
        mClassItem = getArguments().getParcelable("class");
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("班群转移中");
        View view = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_class_transfer_process, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        if (mTeacherInfo == null || mClassItem == null) {
            finish();
            return;
        }
        initView(view);
    }

    private void initView(View view) {
        ImageView mUserView = (ImageView) view.findViewById(com.buang.welewolf.R.id.class_transfer_process_send_head);
        ImageFetcher.getImageFetcher().loadImage(Utils.getLoginUserItem().headPhoto,
                mUserView, com.buang.welewolf.R.drawable.bt_chat_teacher_default,
                new RoundDisplayer());

        ImageView mTeacherHeadView = (ImageView) view.findViewById(com.buang.welewolf.R.id.class_transfer_process_recerive_head);
        ImageFetcher.getImageFetcher().loadImage(mTeacherInfo.mHeadPhoto,
                mTeacherHeadView, com.buang.welewolf.R.drawable.bt_chat_teacher_default,
                new RoundDisplayer());

        ((TextView) view.findViewById(com.buang.welewolf.R.id.class_transfer_process_recerive_name)).setText(mTeacherInfo.mUserName);
        ((TextView) view.findViewById(com.buang.welewolf.R.id.class_transfer_process_recerive_phone)).setText(mTeacherInfo.mMobile);
        ((TextView) view.findViewById(com.buang.welewolf.R.id.class_transfer_process_recerive_school)).setText(mTeacherInfo.mSchool);
        ((TextView) view.findViewById(com.buang.welewolf.R.id.class_transfer_process_classname)).
                setText(SubjectUtils.getGradeName(mClassItem.grade) + mClassItem.className);
        mTransferStatus = (ImageView) view.findViewById(com.buang.welewolf.R.id.class_transfer_process_status);
        mButtonView = (TextView) view.findViewById(com.buang.welewolf.R.id.class_transfer_process_btn);
        mCancelText = (TextView) view.findViewById(com.buang.welewolf.R.id.cancel_text);
        mCancelView = (LinearLayout) view.findViewById(com.buang.welewolf.R.id.text_transfer_cancel);
        if (mClassItem.transferStatus == ClassInfoItem.STATUS_TRANSFER_SEND) {// 转移中
            mTransferStatus.setImageResource(com.buang.welewolf.R.drawable.bt_class_transfer_processing);
            mButtonView.setVisibility(View.GONE);
            mCancelView.setVisibility(View.VISIBLE);
        } else {
            mTransferStatus.setImageResource(com.buang.welewolf.R.drawable.bt_class_transfer_process);
            mButtonView.setVisibility(View.VISIBLE);
            mCancelView.setVisibility(View.GONE);
        }
        mButtonView.setOnClickListener(mOnClickListener);
        mCancelText.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
                ActionUtils.notifyVirtualTip();
                return;
            }

            showDialog();
//            if (ClassInfoItem.STATUS_TRANSFER_SEND == mClassItem.transferStatus) {//转移中
//
//                loadTransferClassData("0");
//            } else {
//                loadTransferClassData("1");
//            }
        }
    };

    private void showDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        String title;
        if (ClassInfoItem.STATUS_TRANSFER_SEND == mClassItem.transferStatus) {//转移中
            title = "确定要取消转移 " + mClassItem.className + " 吗?";
        } else {
            title = "确定要将 " + mClassItem.className + " 转移给 " + mTeacherInfo.mUserName + " 吗?";
        }
        mDialog = DialogUtils.getMessageDialog(getActivity(), "", "确定", "取消", title, new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                    if (ClassInfoItem.STATUS_TRANSFER_SEND == mClassItem.transferStatus) {//转移中
                        loadTransferClassData("0");
                    } else {
                        loadTransferClassData("1");
                    }
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void loadTransferClassData(String status) {
        loadDefaultData(PAGE_MORE, mTeacherInfo.mTeacherId, mClassItem.classId, status, "1");
    }

    /**
     * 更新操作结果
     */
    private void updateProcessResult() {
        if (ClassInfoItem.STATUS_TRANSFER_NONE == mClassItem.transferStatus) {// 转移中
            mTransferStatus.setImageResource(com.buang.welewolf.R.drawable.bt_class_transfer_processing);
            mButtonView.setVisibility(View.GONE);
            mCancelView.setVisibility(View.VISIBLE);
            mClassItem.transferStatus = ClassInfoItem.STATUS_TRANSFER_SEND;
            removeAllFragment();
        } else {
            mTransferStatus.setImageResource(com.buang.welewolf.R.drawable.bt_class_transfer_process);
            mButtonView.setVisibility(View.VISIBLE);
            mCancelView.setVisibility(View.GONE);
            mClassItem.transferStatus = ClassInfoItem.STATUS_TRANSFER_NONE;
            finish();
        }
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getTransferClassUrl(Utils.getToken());
        JSONObject object = new JSONObject();
        try {
            object.put("to_teacher_id", (String) params[0]);
            object.put("class_id", (String) params[1]);
            object.put("status", (String) params[2]);
            object.put("is_my_class", (String) params[3]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = object.toString();
        if (TextUtils.isEmpty(data))
            return null;
        BaseObject result = new DataAcquirer<BaseObject>().post(url,
                data, new BaseObject());
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result) {
        super.onGet(action, pageNo, result);
        if (null != mOnClassTransferLisenter) {
            mOnClassTransferLisenter.classTransfer();
        }
        ActionUtils.motifyClassTransferChanged();
        updateProcessResult();
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result) {
        super.onFail(action, pageNo, result);
    }

    public interface OnClassTransferLisenter {
        void classTransfer();
    }

    private OnClassTransferLisenter mOnClassTransferLisenter;

    public void setOnClassTransferLisenter(OnClassTransferLisenter classTransferLisenter) {
        this.mOnClassTransferLisenter = classTransferLisenter;
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
