package com.buang.welewolf.modules.game;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;

/**
 * Created by weilei on 17/7/6.
 */

public class GameHelpFrament extends BaseUIFragment<UIFragmentHelper> {

    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.dialog_welewolf_help, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        view.findViewById(R.id.rlNormalRule).setOnClickListener(clickListener);
        view.findViewById(R.id.rlAdvanceRule).setOnClickListener(clickListener);
        view.findViewById(R.id.rlRoleRule).setOnClickListener(clickListener);
        view.findViewById(R.id.blClose).setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.rlNormalRule:
                    showDialog(0);
                    break;
                case R.id.rlAdvanceRule:
                    showDialog(1);
                    break;
                case R.id.rlRoleRule:
                    showDialog(2);
                    break;
                case R.id.blClose:
                    finish();
                    break;
            }
        }
    };

    private void showDialog(int type) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (type == 0) {
            mDialog = DialogUtils.getNormalDialog(getActivity());
        } else if (type == 1) {
            mDialog = DialogUtils.getHelpAdvanceDialog(getActivity());
        } else if (type == 2) {
            mDialog = DialogUtils.getHelpRoleDialog(getActivity());
        }
        mDialog.show();
    }
}
