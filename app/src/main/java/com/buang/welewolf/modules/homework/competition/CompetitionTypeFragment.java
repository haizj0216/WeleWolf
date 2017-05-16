package com.buang.welewolf.modules.homework.competition;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.base.bean.OnlineCompetitionListInfo.CompetitionItem;
import com.buang.welewolf.modules.homework.assign.AssignUnitFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

/**
 * Created by LiuYu on 2017/4/12.
 */
public class CompetitionTypeFragment extends BaseUIFragment<UIFragmentHelper> implements View.OnClickListener, View.OnLongClickListener {

    private boolean isClicked;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_competition_type_select, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("选择比赛类型");
        Rect outRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int layoutheight = (outRect.height() - UIUtils.dip2px(95)) / 2;
        View loopLayout = view.findViewById(com.buang.welewolf.R.id.ll_competition_loop_create);
        View testLayout = view.findViewById(com.buang.welewolf.R.id.ll_competition_singletest_create);
        loopLayout.getLayoutParams().height = layoutheight;
        testLayout.getLayoutParams().height = layoutheight;

        loopLayout.setOnClickListener(this);
        testLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case com.buang.welewolf.R.id.ll_competition_loop_create:
                if (isClicked) break;
                isClicked = true;
                showAssignMatchFragment(CompetitionItem.TYPE_LOOP_MATCH);
                break;
            case com.buang.welewolf.R.id.ll_competition_singletest_create:
                if (isClicked) break;
                isClicked = true;
                showAssignMatchFragment(CompetitionItem.TYPE_SINGLE_TEST);
                break;
            default:
                break;
        }
    }

    private void showAssignMatchFragment(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsUtils.KEY_ASSIGN_TYPE, type);
        showFragment(AssignUnitFragment.newFragment(getActivity(), AssignUnitFragment.class, bundle));
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isClicked = false;
            }
        }, 300);
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }
}
