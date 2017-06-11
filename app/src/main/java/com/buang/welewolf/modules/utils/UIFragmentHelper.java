/**
 * Copyright (C) 2015 The AndroidPhoneStudent Project
 */
package com.buang.welewolf.modules.utils;

import com.buang.welewolf.R;
import com.buang.welewolf.widgets.BoxEmptyView;
import com.buang.welewolf.widgets.BoxLoadingView;
import com.buang.welewolf.widgets.BoxTitleBar;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;

public class UIFragmentHelper extends BaseUIFragmentHelper {

    public UIFragmentHelper(BaseUIFragment<?> fragment) {
        super(fragment);
    }

    public BoxTitleBar getTitleBar() {
        return (BoxTitleBar) getBaseUIFragment().getTitleBar();
    }

    public BoxEmptyView getEmptyView() {
        return (BoxEmptyView) getBaseUIFragment().getEmptyView();
    }

    public BoxLoadingView getLoadingView() {
        return (BoxLoadingView) getBaseUIFragment().getLoadingView();
    }

    public void setTintBar(int color) {
        getBaseUIFragment().setStatusTintBarEnable(true);
        getBaseUIFragment().setStatusTintBarColor(color);
    }
}
