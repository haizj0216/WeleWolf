package com.knowbox.teacher.modules.classes;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineAddClassInfo;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.services.updateclass.UpdateClassService;
import com.knowbox.teacher.base.utils.ActionUtils;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.CleanableEditText;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fanjb
 * @name 添加班级页面
 * @date 2015-3-27
 */
public class AddGradeClassFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_ADD_CLASS = 1;
    private static final int ACTION_CHANGE_CLASS_NAME = 2;

    public static final int TYPE_ADD_CLASS = 1;
    public static final int TYPE_CHANGE_CLASS = 2;

    private TextView mGradeEdit;
    private CleanableEditText mClassEdit;
    private String mGradeInfo;
    private String mClassInfo;
    private String mClassId;
    private PopupWindow mPopWindow;
    private int type;
    private String mTitle;
    private String mClassName;
    private UpdateClassService mUpdateClassService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mTitle = getArguments().getString("title");
        type = getArguments().getInt("type");
        mClassId = getArguments().getString("classId");
        mClassName = getArguments().getString("classname");
        mUpdateClassService = (UpdateClassService) getActivity().getSystemService(UpdateClassService.SERVICE_NAME);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        if(!TextUtils.isEmpty(mTitle)){
            getTitleBar().setTitle(mTitle);
        }else {
            getTitleBar().setTitle("创建班群");
        }
        getUIFragmentHelper().getTitleBar().setUMengKey(UmengConstant.EVENT_CLASS_ADD_BACK);
        View view = View.inflate(getActivity(), R.layout.layout_myclass_add, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        String rightMoreText = "完成";
        if(!TextUtils.isEmpty(mTitle)){
            rightMoreText = "保存";
        }
        getUIFragmentHelper().getTitleBar().setRightMoreTxt(rightMoreText, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    UIUtils.hideInputMethod(getActivity());
                    if (type == TYPE_ADD_CLASS) {
                        if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
                            ActionUtils.notifyVirtualTip();
                            return;
                        }
                        MobclickAgent.onEvent(BaseApp.getAppContext(),
                                UmengConstant.EVENT_CLASS_FINISH);
                        loadData(ACTION_ADD_CLASS, PAGE_MORE, mGradeInfo, mClassEdit.getText());
                    } else if (type == TYPE_CHANGE_CLASS) {
                        loadData(ACTION_CHANGE_CLASS_NAME, PAGE_MORE, mGradeInfo, mClassEdit.getText(), mClassId);
                    }

                }
            }
        });
        mGradeEdit = (TextView) view.findViewById(R.id.class_add_grade);
        mGradeEdit.setOnClickListener(mOnClickListener);

        mClassEdit = (CleanableEditText) view.findViewById(R.id.class_add_class_name);
        if(!TextUtils.isEmpty(mClassName)){
            mClassEdit.getEditText().setText(mClassName);
        }else{
            mClassEdit.setHint(getString(R.string.class_add_class_name));
        }
        mClassEdit.setHintTextColor(getActivity().getResources().getColor(R.color.color_text_third));
        mClassEdit.getEditText().setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
        mClassEdit.addFilter(new CleanableEditText.UserNameLoginFilter());
        mClassEdit.addTextChangedListener(mTextWatcher);

        mClassEdit.setFocusable(true);
        mClassEdit.requestFocus();

        if (!TextUtils.isEmpty(mGradeEdit.getText().toString())
                && !TextUtils.isEmpty(mClassEdit.getText())) {
            changeState(true);
        } else {
            changeState(false);
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

//		mClassEdit.setOnClickListener(mOnClickListener);
        //changeState(false);
        String gradePart = Utils.getLoginUserItem().gradePart;
        if (ConstantsUtils.GRADE_PART_HIGH.equals(gradePart)) {
            mGradeInfo = ConstantsUtils.GRADE_FIRST_HIGH;
            mGradeEdit.setText("高一");
        } else if (ConstantsUtils.GRADE_PART_MIDDLE.equals(gradePart)) {
            mGradeInfo = ConstantsUtils.GRADE_FIRST_MIDDLE;
            mGradeEdit.setText("六年级");
        } else if (ConstantsUtils.GRADE_PART_GRADE.equals(gradePart)) {
            mGradeInfo = ConstantsUtils.GRADE_FIRST_GRADE;
            mGradeEdit.setText("小学三年级");
        }
        String grade = getArguments().getString("grade");
        if (!TextUtils.isEmpty(grade)) {
            mGradeInfo = grade;
            mGradeEdit.setText(SubjectUtils.getGradeName(grade));
        }

        if (type == TYPE_ADD_CLASS) {
            view.findViewById(R.id.create_class_desc).setVisibility(View.VISIBLE);
        }
    }

    private void showSelectGradePop() {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();
        }
        final List<MenuItem> lists = new ArrayList<MenuItem>();
        String gradePart = Utils.getLoginUserItem().gradePart;
        if (ConstantsUtils.GRADE_PART_HIGH.equals(gradePart)) {
            lists.add(new MenuItem(0, "高一", ConstantsUtils.GRADE_FIRST_HIGH));
            lists.add(new MenuItem(1, "高二", ConstantsUtils.GRADE_SECOND_HIGH));
            lists.add(new MenuItem(2, "高三", ConstantsUtils.GRADE_THIRD_HIGH));
        } else if (ConstantsUtils.GRADE_PART_MIDDLE.equals(gradePart)) {
            lists.add(new MenuItem(0, "六年级", ConstantsUtils.GRADE_FIRST_MIDDLE));
            lists.add(new MenuItem(1, "七年级", ConstantsUtils.GRADE_SECOND_MIDDLE));
            lists.add(new MenuItem(2, "八年级", ConstantsUtils.GRADE_THIRD_MIDDLE));
            lists.add(new MenuItem(3, "九年级", ConstantsUtils.GRADE_FOUR_MIDDLE));
        } else if (ConstantsUtils.GRADE_PART_GRADE.equals(gradePart)) {
            lists.add(new MenuItem(0, "小学一年级", ConstantsUtils.GRADE_FIRST_GRADE));
            lists.add(new MenuItem(1, "小学二年级", ConstantsUtils.GRADE_SECOND_GRADE));
            lists.add(new MenuItem(2, "小学三年级", ConstantsUtils.GRADE_THIRD_GRADE));
            lists.add(new MenuItem(3, "小学四年级", ConstantsUtils.GRADE_FOUR_GRADE));
            lists.add(new MenuItem(4, "小学五年级", ConstantsUtils.GRADE_FIVE_GRADE));
            lists.add(new MenuItem(5, "小学六年级", ConstantsUtils.GRADE_SIX_GRADE));
        }
        int index = -1;
        String grade = mGradeInfo;
        for (int i = 0; i < lists.size(); i++) {
            MenuItem item = lists.get(i);
            if (item.desc.equals(grade)) {
                index = i;
                break;
            }
        }
        List<String> mLists = new ArrayList<String>();
        for (int i = 0; i < lists.size(); i++) {
            mLists.add(lists.get(i).title);
        }
        mPopWindow = DialogUtils.getAssignFilterPop(getActivity(), index, mLists, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem item = lists.get(position);
                mGradeInfo = item.desc;
                mGradeEdit.setText(item.title);
                mPopWindow.dismiss();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        mPopWindow.showAsDropDown(mGradeEdit);
    }

    /**
     * 年级、班级点击事件
     */
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mGradeEdit) {
                showSelectGradePop();
            }/* else if (v == mClassEdit) {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                SelectClassFragment fragment = (SelectClassFragment) Fragment.instantiate(
                        getActivity(), SelectClassFragment.class.getName());
                fragment.setOnClasSelectListenr(new OnClassSelectListenr() {

                    @Override
                    public void onConfirm(String classInfo) {
                        mClassInfo = classInfo;
                        if (!TextUtils.isEmpty(mClassInfo)) {
                            mClassEdit.setText(mClassInfo);
                        }
                    }
                });
                showFragment(fragment);
            }*/
        }
    };

    public boolean isValid() {
        if (TextUtils.isEmpty(mGradeEdit.getText().toString())) {
//			UiThreadHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					Toast.makeText(AddGradeClassActivity.this, "请选择您的年级",
//							Toast.LENGTH_SHORT).show();
//				}
//			});
            return false;
        }

        if (TextUtils.isEmpty(mClassEdit.getText())) {
//			UiThreadHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					UiHelper.notify2shake(mClassEdit);
//					Toast.makeText(AddGradeClassActivity.this, "请选择您的班级",
//							Toast.LENGTH_SHORT).show();
//				}
//			});
            return false;
        }
        if(mClassEdit.getText().length()<2
                || mClassEdit.getText().length()>20){
    		ToastUtils.showShortToast(getActivity(), "请输入2-20个字符的班群名称");
    		return false;
    	}

        if (type == TYPE_CHANGE_CLASS && mClassName.equals(mClassEdit.getText()) && mGradeInfo.equals(getArguments().getString("grade"))) {
            finish();
            return false;
        }

        return true;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String gradeInfo = mGradeEdit.getText().toString();
            String classInfo = mClassEdit.getText();
            if (!TextUtils.isEmpty(gradeInfo) && !TextUtils.isEmpty(classInfo)) {
                changeState(true);
            } else {
                changeState(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void changeState(boolean state) {
        TextView confirmTxt = getUIFragmentHelper().getTitleBar().getMoreTextView();
        if (confirmTxt != null) {
            confirmTxt.setEnabled(state);
        }else {
            return;
        }
        if(state){
            confirmTxt.setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
        }else {
            confirmTxt.setTextColor(getActivity().getResources().getColor(R.color.color_text_button_pressed));
        }
    }

    private OnAddClassSucessListenr mConfirmClickListenr;

    public void setOnConfirmClickListenr(OnAddClassSucessListenr listenr) {
        this.mConfirmClickListenr = listenr;
    }

    public interface OnAddClassSucessListenr {
        public void onAddClassSucess();
        public void onChangeClassName(String grade, String name);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        if (getActivity() != null) {
            UIUtils.hideInputMethod(getActivity());
        }
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
        getLoadingView().showLoading();
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_ADD_CLASS) {
            String url = OnlineServices.getAddClassUrl();
            JSONObject object = new JSONObject();
            try {
                object.put("grade", (String) params[0]);
                object.put("class_name", (String) params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data = object.toString();
            if (TextUtils.isEmpty(data))
                return null;
            OnlineAddClassInfo onlineInfo = new DataAcquirer<OnlineAddClassInfo>()
                    .post(url, data, new OnlineAddClassInfo());

            return onlineInfo;
        } else if (action == ACTION_CHANGE_CLASS_NAME) {
            String url = OnlineServices.getUpdateClassInfoUrl();
            JSONObject object = new JSONObject();
            try {
                object.put("grade", (String) params[0]);
                object.put("class_name", (String) params[1]);
                object.put("class_id", (String) params[2]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data = object.toString();
            if (TextUtils.isEmpty(data))
                return null;
            BaseObject onlineInfo = new DataAcquirer<BaseObject>()
                    .post(url, data, new BaseObject());

            return onlineInfo;
        }
        return null;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result) {
        super.onGet(action, pageNo, result);
        if (action == ACTION_ADD_CLASS) {
            if (result != null && result.isAvailable()) {
                ToastUtils.showShortToast(getActivity(), "创建班群成功");
                mUpdateClassService.addClassItem(((OnlineAddClassInfo) result).info);
                finish();
            }
        } else if (action == ACTION_CHANGE_CLASS_NAME) {
            if (result != null && result.isAvailable()) {
                if (mConfirmClickListenr != null) {
                    mConfirmClickListenr.onChangeClassName(mGradeInfo, mClassEdit.getText());
                }
                finish();
            }
        }

    }

}