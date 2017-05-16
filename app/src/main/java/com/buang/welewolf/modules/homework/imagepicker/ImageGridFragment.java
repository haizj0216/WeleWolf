package com.buang.welewolf.modules.homework.imagepicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.buang.welewolf.widgets.HeaderGridView;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.utils.MsgCenter;
import com.knowbox.base.utils.UIUtils;
import com.buang.welewolf.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;

public class ImageGridFragment extends BaseSubFragment implements OnItemClickListener {

    private HeaderGridView imageGridView = null;
    private ImageGridAdapter mAdapter = null;
    private DisplayImageOptions options = null;
    private ArrayList<ImageBean> mImages = null;
    private ViewImageListener mViewImageListener = null;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ImagePagerFragment.ACTION_CLOSED_FOR_CHANGE.equals(intent.getAction())) {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void setImageLoader(DisplayImageOptions options) {
        this.options = options;
    }

    public void swapDatas(ArrayList<ImageBean> images) {
        if (this.mImages != null) {
            this.mImages.clear();
            this.mImages = null;
        }
        this.mImages = images;
        if (mAdapter != null) {
            mAdapter.swapDatas(mImages);
        }
    }

    @Override
    public View onCreateViewImpl(ViewGroup container, Bundle savedInstanceState) {
        MsgCenter.registerLocalReceiver(mReceiver, new IntentFilter(ImagePagerFragment.ACTION_CLOSED_FOR_CHANGE));
        return View.inflate(getActivity(), R.layout.multi_images_picker_grid_layout, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);

        imageGridView = (HeaderGridView) view.findViewById(R.id.gridGallery);
        int numColumns = (getResources().getDisplayMetrics().widthPixels - UIUtils.dip2px(getActivity(), 6)) / UIUtils.dip2px(getActivity(), 116);
        imageGridView.setNumColumns(numColumns);
        imageGridView.setVerticalScrollBarEnabled(false);

        View footerView = new View(getActivity());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, UIUtils.dip2px(getActivity(), 82));
        footerView.setLayoutParams(lp);
        imageGridView.addFooterView(footerView, null, false);

        mAdapter = new ImageGridAdapter(getActivity(), options);
        mAdapter.setChoseImageListener(mViewImageListener);
        mAdapter.swapDatas(mImages);
        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(), true, true);
        imageGridView.setOnScrollListener(listener);
        imageGridView.setOnItemClickListener(this);
        imageGridView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        MsgCenter.unRegisterLocalReceiver(mReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mViewImageListener != null) {
            mViewImageListener.viewImage(position);
        }
    }

    public void setViewImageListener(ViewImageListener listener) {
        this.mViewImageListener = listener;
    }

    public interface ViewImageListener extends ChoseImageListener {
        public void viewImage(int position);
    }
}
