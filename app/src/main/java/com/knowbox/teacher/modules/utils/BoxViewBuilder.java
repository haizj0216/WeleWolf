/**
 * Copyright (C) 2015 The AndroidPhoneStudent Project
 */
package com.knowbox.teacher.modules.utils;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.ViewBuilder;
import com.hyena.framework.app.widget.EmptyView;
import com.hyena.framework.app.widget.LoadingView;
import com.hyena.framework.app.widget.TitleBar;
import com.knowbox.teacher.widgets.BoxEmptyView;
import com.knowbox.teacher.widgets.BoxLoadingView;
import com.knowbox.teacher.widgets.BoxTitleBar;

public class BoxViewBuilder implements ViewBuilder {

	@Override
    public TitleBar buildTitleBar(BaseUIFragment<?> fragment) {
        BoxTitleBar titleBar = new BoxTitleBar(fragment.getActivity());
        titleBar.setBaseUIFragment(fragment);
        return titleBar;
    }

    @Override
    public EmptyView buildEmptyView(BaseUIFragment<?> fragment) {
        BoxEmptyView emptyView = new BoxEmptyView(fragment.getActivity());
        emptyView.setBaseUIFragment(fragment);
        return emptyView;
    }

    @Override
    public LoadingView buildLoadingView(BaseUIFragment<?> fragment) {
        BoxLoadingView loadingView = new BoxLoadingView(fragment.getActivity());
        loadingView.setBaseUIFragment(fragment);
        return loadingView;
    }

}
