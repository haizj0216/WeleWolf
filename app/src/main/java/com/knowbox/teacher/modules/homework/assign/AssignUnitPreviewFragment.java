package com.knowbox.teacher.modules.homework.assign;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.teacher.R;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

/**
 * Created by LiuYu on 2017/2/20.
 */
public class AssignUnitPreviewFragment extends BaseUIFragment<UIFragmentHelper> {
    private LinearLayout rootView;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_assign_unit_preview, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("题型预览");
        getUIFragmentHelper().getTitleBar().setBackBtnVisible(false);
        getUIFragmentHelper().getTitleBar().setRightMoreTxt("关闭", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rootView = (LinearLayout) view.findViewById(R.id.preview_layout);
        //设置默认四种题型
        showTypePreView();
    }

    private void showTypePreView() {
        rootView.removeAllViews();
        int types[] = {1, 2, 3, 4, 5};
        for (int type : types) {
            View itemView = View.inflate(getActivity(), R.layout.layout_unit_preview_item, null);
            TextView questionTypeNo = (TextView) itemView.findViewById(R.id.question_type_no);
            TextView questionTypeName = (TextView) itemView.findViewById(R.id.question_type_name);
            ImageView questionTypePre = (ImageView) itemView.findViewById(R.id.question_type_preview);
            if (type == 1) {
                questionTypeNo.setText("题型1.");
                questionTypeName.setText("单词全拼");
                questionTypePre.setImageResource(R.drawable.icon_questiontype_card1);
            } else if (type == 2) {
                questionTypeNo.setText("题型2.");
                questionTypeName.setText("单词挖空");
                questionTypePre.setImageResource(R.drawable.icon_questiontype_card2);
            } else if (type == 3) {
                questionTypeNo.setText("题型3.");
                questionTypeName.setText("选词填空");
                questionTypePre.setImageResource(R.drawable.icon_questiontype_card3);
            } else if (type == 4) {
                questionTypeNo.setText("题型4.");
                questionTypeName.setText("英汉互译");
                questionTypePre.setImageResource(R.drawable.icon_questiontype_card4);
            } else if (type == 5) {
                questionTypeNo.setText("题型5.");
                questionTypeName.setText("听力训练");
                questionTypePre.setImageResource(R.drawable.icon_questiontype_card5);
            }
            rootView.addView(itemView);
        }
    }

}
