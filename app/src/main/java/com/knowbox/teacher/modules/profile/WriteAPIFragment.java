package com.knowbox.teacher.modules.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.ToastUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.widgets.CleanableEditText;

public class WriteAPIFragment extends BaseUIFragment<UIFragmentHelper> {
    CleanableEditText mEditView;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_create_school, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("填写你的APIPrefix");
        TextView mDesc = (TextView) view.findViewById(R.id.desc);
        mDesc.setText("找不到想要的API？输入你自己的进行创建吧");
        TextView city = (TextView) view
                .findViewById(R.id.dialog_choose_school_not_found_city_name);
        city.setVisibility(View.GONE);

        mEditView = (CleanableEditText) view
                .findViewById(R.id.dialog_choose_school_not_found_create);
        mEditView.getEditText().setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
        mEditView.setHintTextColor(getActivity().getResources().getColor(R.color.color_text_third));
        mEditView.setText("http://test.api.knowbox.cn/");

        getUIFragmentHelper().getTitleBar().setRightMoreTxt("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEditView.getText())) {
                    if (null != mAPIwriteListener) {
                        mAPIwriteListener.apiWrited(mEditView.getText());
                        finish();
                    }
                } else {
                    ToastUtils.showShortToast(getActivity(), "你还没有输入");
                }

            }
        });

    }

    private APIwriteListener mAPIwriteListener;

    public interface APIwriteListener {
        public void apiWrited(String api);
    }

    public void setOnAPIWriteListener(APIwriteListener listener) {
        this.mAPIwriteListener = listener;
    }

}
