package com.buang.welewolf.modules.guild;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.buang.welewolf.base.services.upload.UploadListener;
import com.buang.welewolf.base.services.upload.UploadService;
import com.buang.welewolf.base.services.upload.UploadTask;
import com.buang.welewolf.base.utils.ImageUtil;
import com.buang.welewolf.modules.profile.ModifyNameFragment;
import com.buang.welewolf.modules.services.GuildService;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.WolfUtils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UiThreadHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/5/27.
 */

public class GuildSettingFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int REQCODE_CAMERA = 0xA0;
    private static final int REQCODE_PICKER = 0xA1;
    private static final int REQCODE_FROM_CROP = 0xA2;

    private static final int ACTION_EXIT = 1;
    private static final int ACTION_DELETE = 2;

    private ImageView mHeadView;
    private TextView mNameView;
    private TextView mIDView;
    private TextView mSignView;
    private ToggleButton mToggle;
    private TextView mExitView;
    private OnlineGuildInfo guildInfo;
    private GuildService mGuildService;

    private File headImageFile;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        mGuildService = (GuildService) getSystemService(GuildService.SERVICE_NAME);
        guildInfo = mGuildService.getGuildInfo();
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_guild_setting, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("公会设置");
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));

        mNameView = (TextView) view.findViewById(R.id.tvName);
        mHeadView = (ImageView) view.findViewById(R.id.ivHead);
        mIDView = (TextView) view.findViewById(R.id.tvID);
        mSignView = (TextView) view.findViewById(R.id.tvSign);
        mToggle = (ToggleButton) view.findViewById(R.id.tvToggle);
        mExitView = (TextView) view.findViewById(R.id.tvExit);

        if (guildInfo.job == WolfUtils.JOB_HUIZHANG) {
            mExitView.setText("解散公会");
            view.findViewById(R.id.lvGuildSetting).setVisibility(View.VISIBLE);
        } else {
            mExitView.setText("退出公会");
            view.findViewById(R.id.lvGuildSetting).setVisibility(View.GONE);
        }
        updateGuildData();
        view.findViewById(R.id.lvName).setOnClickListener(onClickListener);
        view.findViewById(R.id.lvSign).setOnClickListener(onClickListener);
        view.findViewById(R.id.rvHead).setOnClickListener(onClickListener);
        view.findViewById(R.id.tvExit).setOnClickListener(onClickListener);
    }

    private void updateGuildData() {
        mNameView.setText(guildInfo.guildName);
        ImageFetcher.getImageFetcher().loadImage(guildInfo.mHeadPhoto, mHeadView, R.drawable.bt_message_default_head, new RoundDisplayer());
        mIDView.setText(guildInfo.guildID);
        mSignView.setText(guildInfo.sign);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.lvName:
                    openModifyNameFragment(ModifyNameFragment.TYPE_GUILD_NAME, guildInfo.guildName);
                    break;
                case R.id.lvSign:
                    openModifyNameFragment(ModifyNameFragment.TYPE_GUILD_SIGN, guildInfo.sign);
                    break;
                case R.id.rvHead:
                    updateUserHeadImg();
                    break;
                case R.id.tvExit:
                    showExitDialog();
                    break;
            }
        }
    };

    private void showExitDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        String title = "退出公会";
        String message = "确认退出公会？";
        if (guildInfo.job == WolfUtils.JOB_HUIZHANG) {
            title = "解散公会";
            message = "确定要解散公会吗？";
        }
        mDialog = DialogUtils.getMessageDialog(getActivity(), title, "确定", "缺陷", message, new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                    if (guildInfo.job == WolfUtils.JOB_HUIZHANG) {
                        loadData(ACTION_DELETE, PAGE_MORE);
                    } else {
                        loadData(ACTION_EXIT, PAGE_MORE);
                    }
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void openModifyNameFragment(int type, String name) {
        Bundle mBundle = new Bundle();
        mBundle.putInt("type", type);
        mBundle.putString("name", name);
        ModifyNameFragment fragment = ModifyNameFragment.newFragment(getActivity(), ModifyNameFragment.class, mBundle);
        fragment.setOnModifyNameListener(new ModifyNameFragment.OnModifyNameListener() {
            @Override
            public void onModifyName(String name) {

            }
        });
        showFragment(fragment);
    }

    /**
     * 修改用户头像
     */
    private void updateUserHeadImg() {
        List<MenuItem> items = new ArrayList<MenuItem>();
        items.add(new MenuItem(0, "拍照", ""));
        items.add(new MenuItem(1, "相册", ""));
        mDialog = DialogUtils.getListDialog(getActivity(), "更换头像", items,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        // 调用系统相机拍照
                        if (position == 0) {
                            Intent intent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(headImageFile));
                            intent.putExtra("return-data", false);
                            startActivityForResult(intent, REQCODE_CAMERA);
//							setupCameraIntentHelper();
//							if (mCameraIntentHelper != null) {
//								mCameraIntentHelper.startCameraIntent();
//							}
                        }

                        // 调用图片浏览器
                        else if (position == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setDataAndType(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    "image/*");
                            startActivityForResult(intent, REQCODE_PICKER);
                        }
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }
                });
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拍照或从相册选取图片结束后默认进入裁剪照片
//		mCameraIntentHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQCODE_CAMERA && resultCode == -1) {
            crop(Uri.fromFile(headImageFile));
        } else if (requestCode == REQCODE_PICKER && resultCode == -1) {
            crop(data.getData());
        }
        // 裁剪结束后先写入到本地，在读取本地图片显示
        else if (requestCode == REQCODE_FROM_CROP && resultCode == -1) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                final Bitmap bitmap = extras.getParcelable("data");
                if (bitmap != null) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            writeBmpNative(bitmap);
                            uploadBmpNet();
                        }
                    }.start();
                }
            }
        }
    }

    /**
     * 将图片写入到本地保存
     *
     * @param bitmap
     */
    private void writeBmpNative(Bitmap bitmap) {
        headImageFile.delete();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(headImageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 将图片上传到网络
     */
    private void uploadBmpNet() {
        final byte[] imageData = ImageUtil.getBitmapBytes(
                headImageFile, 300, 800, 600);

        UploadService uploadService = (UploadService) getActivity().getSystemService(UploadService.SERVICE_NAME_QINIU);
        uploadService.upload(new UploadTask(UploadTask.TYPE_PICTURE, imageData), new UploadListener() {
            @Override
            public void onUploadStarted(UploadTask uploadTask) {
                getLoadingView().showLoading();
            }

            @Override
            public void onUploadProgress(UploadTask uploadTask, double v) {

            }

            @Override
            public void onUploadComplete(UploadTask uploadTask, String s) {
                showContent();
            }

            @Override
            public void onUploadError(UploadTask uploadTask, int i, String s, String token) {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showContent();
                        ToastUtils.showShortToast(getActivity(), "上传头像失败");
                    }
                });
            }

            @Override
            public void onRetry(UploadTask uploadTask, int errorCode,
                                String error, String extend) {
            }
        });
    }

    /**
     * 调用系统裁剪照片
     *
     * @param uri
     */
    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQCODE_FROM_CROP);
    }
}
