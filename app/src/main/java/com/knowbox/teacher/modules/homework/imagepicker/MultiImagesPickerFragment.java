package com.knowbox.teacher.modules.homework.imagepicker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.utils.DirContext;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MultiImagesPickerFragment extends BaseUIFragment<UIFragmentHelper> implements LoaderManager.LoaderCallbacks<ArrayList<ImageBean>>, ImageGridFragment.ViewImageListener {

    private final int IMAGE_LIMIT = 9;
    private DisplayImageOptions mOptions = null;
    private ArrayList<ImageBean> mImages = null;
    private RelativeLayout mBtnLayout = null;
    private LinearLayout mNumberLayout = null;
    private TextView mPreviewTextView = null;
    private ImageView mConfirmButton = null;
    private int mSelectedCount = 0;
    private int mCountLimit;
    private OnImageSelectedListener mListener;
    private LinkedList<String> mSelectedImagePaths = new LinkedList<String>();

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.multi_images_picker_main, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getTitleBar().setTitle("请选择图片");
        mCountLimit = getArguments() == null ? IMAGE_LIMIT : getArguments().getInt("limit", IMAGE_LIMIT);
        mBtnLayout = (RelativeLayout) view.findViewById(R.id.images_picker_bottom_container);
        mNumberLayout = (LinearLayout) view.findViewById(R.id.images_picker_picture_count);
        mNumberLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedCount <= 0) {
                    return;
                }
                openImagePager(false, 0);
            }
        });

        mPreviewTextView = (TextView) view.findViewById(R.id.images_picker_preview_image);
        mConfirmButton = (ImageView) view.findViewById(R.id.images_picker_confirm);
        mConfirmButton.setEnabled(false);
        mConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onImageSelect(mSelectedImagePaths);
                    finish();
                }
            }
        });

        initImageLoader();

        ImageGridFragment fragment = (ImageGridFragment) Fragment.instantiate(getActivity(), ImageGridFragment.class.getName());
        fragment.setImageLoader(mOptions);
        fragment.setViewImageListener(this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.images_picker_content, fragment).commit();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        getLoaderManager().destroyLoader(0);
    }

    public void setSelectListener(OnImageSelectedListener listener) {
        this.mListener = listener;
    }

    @Override
    public Loader<ArrayList<ImageBean>> onCreateLoader(int arg0, Bundle arg1) {
        return new ImagesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ImageBean>> arg0, ArrayList<ImageBean> arg1) {
        this.mImages = arg1;
        swapDatas(arg1);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ImageBean>> arg0) {
        swapDatas(null);
    }

    private void swapDatas(ArrayList<ImageBean> arg1) {
        if (arg1 == null || arg1.isEmpty()) {
            getUIFragmentHelper().getEmptyView().showEmpty("暂无图片");
            return;
        }
        Fragment fragment = getFragmentManager().findFragmentById(R.id.images_picker_content);
        if (fragment instanceof ImageGridFragment) {
            ((ImageGridFragment) fragment).swapDatas(arg1);
        }
    }

    @Override
    public void viewImage(int position) {
        openImagePager(true, position);
    }

    private void openImagePager(boolean all, int position) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("all", all);
        if (all) {
            bundle.putParcelableArrayList("datas", mImages);
            bundle.putInt("position", position);
        } else {
            bundle.putParcelableArrayList("datas", getSelectedImages());
        }
        ImagePagerFragment fragment = (ImagePagerFragment) Fragment.instantiate(getActivity(), ImagePagerFragment.class.getName(), bundle);
        fragment.setImageLoader(mOptions);
        fragment.setChoseImageListener(this);
        showFragment(fragment);
    }

    private ArrayList<ImageBean> getSelectedImages() {
        ArrayList<ImageBean> selectedImages = new ArrayList<ImageBean>();
        for (ImageBean image : mImages) {
            if (image.isSelected()) {
                selectedImages.add(image);
            }
        }
        return selectedImages;
    }

    private ArrayList<String> getSelectedImagePaths() {
        ArrayList<String> selectedImages = new ArrayList<String>();
        for (ImageBean image : mImages) {
            if (image.isSelected()) {
                selectedImages.add(image.getPath());
            }
        }
        return selectedImages;
    }

    @Override
    public boolean onSelected(ImageBean image) {
        if (mSelectedCount >= mCountLimit) {
            ToastUtils.showShortToast(getActivity(), R.string.multi_images_picker_arrive_limit_count);
            return false;
        }
        image.setSelected(true);
        mSelectedCount++;
        refreshPreviewTextView();
        mSelectedImagePaths.add(image.getPath());
        return true;
    }

    @Override
    public boolean onCancelSelect(ImageBean image) {
        image.setSelected(false);
        mSelectedCount--;
        refreshPreviewTextView();
        mSelectedImagePaths.remove(image.getPath());
        return true;
    }

    private void refreshPreviewTextView() {
        if (mSelectedCount <= 0) {
            mBtnLayout.setVisibility(View.GONE);
            mPreviewTextView.setText("");
            mConfirmButton.setEnabled(false);
        } else {
            mBtnLayout.setVisibility(View.VISIBLE);
            mPreviewTextView.setText(mSelectedCount + "");
            mConfirmButton.setEnabled(true);
        }
    }

    private void initImageLoader() {
        if (mOptions == null) {
            DisplayImageOptions.Builder displayBuilder = new DisplayImageOptions.Builder();
            displayBuilder.cacheInMemory(true);
            displayBuilder.cacheOnDisk(true);
            displayBuilder.showImageOnLoading(R.drawable.default_photo);
            displayBuilder.showImageForEmptyUri(R.drawable.default_photo);
            displayBuilder.considerExifParams(true);
            displayBuilder.bitmapConfig(Bitmap.Config.RGB_565);
            displayBuilder.imageScaleType(ImageScaleType.EXACTLY);
            displayBuilder.displayer(new FadeInBitmapDisplayer(300));
            mOptions = displayBuilder.build();
        }

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration.Builder loaderBuilder = new ImageLoaderConfiguration.Builder(getActivity());
            loaderBuilder.memoryCacheSize(getMemoryCacheSize());

            try {
                loaderBuilder.diskCache(new LruDiskCache(DirContext.getImageCacheDir(), DefaultConfigurationFactory.createFileNameGenerator(), 500 * 1024 * 1024));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageLoader.getInstance().init(loaderBuilder.build());
        }

    }

    private int getMemoryCacheSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        return screenWidth * screenHeight * 4 * 3;
    }

    public interface OnImageSelectedListener {
        public void onImageSelect(List<String> imagePaths);
    }

}