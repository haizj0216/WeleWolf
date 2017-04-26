/**
 * Copyright (C) 2015 The AndroidPhoneStudent Project
 */
package com.knowbox.teacher.modules.utils;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.knowbox.teacher.widgets.BoxEmptyView;
import com.knowbox.teacher.widgets.BoxLoadingView;
import com.knowbox.teacher.widgets.BoxTitleBar;

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
}
