package com.knowbox.teacher.modules.homework.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.MsgCenter;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerFragment extends BaseUIFragment<UIFragmentHelper> implements OnPageChangeListener {

    public static final String ACTION_CLOSED_FOR_CHANGE = "com.knowbox.teacher.ACTION_CLOSED_FOR_CHANGE";
    private ArrayList<ImageBean> mImages = null;
    private int mPosition;
    private ImagePagerAdapter mAdapter = null;
    private DisplayImageOptions options = null;
    private ViewPager mImagePager = null;
    private ChoseImageListener mChoseImageListener = null;
    private ImageView mRightView;
    private boolean all = false;

    public void setImageLoader(DisplayImageOptions options) {
        this.options = options;
    }

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        mImagePager = new ViewPager(getActivity());
        mImages = getArguments().getParcelableArrayList("datas");
        all = getArguments().getBoolean("all");
        mPosition = getArguments().getInt("position");
        mAdapter = new ImagePagerAdapter(mImages);
        mImagePager.setAdapter(mAdapter);
        mImagePager.setCurrentItem(mPosition, true);
        mImagePager.setOnPageChangeListener(this);
        return mImagePager;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mRightView = (ImageView) view.findViewById(R.id.title_bar_menu);
        final int padding = UIUtils.dip2px(getActivity(), 14);
        mRightView.setPadding(padding, padding, padding, padding);
        mRightView.setBackgroundResource(0);
        mRightView.setVisibility(View.VISIBLE);
        refreshSelectBtn(mPosition);
        mRightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageChosen();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        MsgCenter.sendLocalBroadcast(new Intent(ACTION_CLOSED_FOR_CHANGE));
    }

    private void onImageChosen() {
        ImageBean image = mImages.get(mImagePager.getCurrentItem());
        if (image.isSelected()) {
            if (mChoseImageListener.onCancelSelect(image)) {
                mRightView.setImageResource(R.drawable.multi_images_picker_title_check_off);

                if (!all) {
                    mImages.remove(image);
                    if (mImages.size() > 0) {
                        mImagePager.removeAllViews();
                        mAdapter.setData(mImages);
                    } else {
                        finish();
                    }
                }
            }
        } else {
            if (mChoseImageListener.onSelected(image)) {
                mRightView.setImageResource(R.drawable.multi_images_picker_title_check_on);
            }
        }
    }

    public void swapDatas(ArrayList<ImageBean> images) {
        if (mImages != null) {
            mImages.clear();
            mImages = null;
        }
        mImages = images;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (arg0 != ViewPager.SCROLL_STATE_IDLE) {
            ImageLoader.getInstance().pause();
        } else {
            ImageLoader.getInstance().resume();
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(final int position) {
        refreshSelectBtn(position);
    }

    private void refreshSelectBtn(int position) {
        ImageBean image = mImages.get(position);
        if (image.isSelected()) {
            mRightView.setImageResource(R.drawable.multi_images_picker_title_check_on);
        } else {
            mRightView.setImageResource(R.drawable.multi_images_picker_title_check_off);
        }
        getTitleBar().setTitle((position + 1) + " / " + mImages.size());
    }

    public void setChoseImageListener(ChoseImageListener listener) {
        this.mChoseImageListener = listener;
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<ImageBean> mImageItems;

        public ImagePagerAdapter(List<ImageBean> data) {
            mImageItems = new ArrayList<ImageBean>();
            mImageItems.addAll(data);
        }

        public void setData(List<ImageBean> data) {
            mImageItems.clear();
            mImageItems.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ImageView itemView = (ImageView) object;
            ((ViewGroup) container).removeView(itemView);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ImageBean image = mImageItems.get(position);
            ImageView itemView = new ImageView(getActivity());
            itemView.setScaleType(ScaleType.CENTER);
            itemView.setImageResource(R.drawable.image_previewer_default);
            ImageLoader.getInstance().displayImage("file://" + image.getPath(), itemView, options);
            ((ViewGroup) container).addView(itemView);
            return itemView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            refreshSelectBtn(mImagePager.getCurrentItem());
        }

        @Override
        public int getCount() {
            if (mImages == null) {
                return 0;
            } else {
                return mImageItems.size();
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }
}
