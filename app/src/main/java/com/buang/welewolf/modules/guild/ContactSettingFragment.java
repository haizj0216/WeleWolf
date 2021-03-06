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

import com.buang.welewolf.R;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.upload.UploadListener;
import com.buang.welewolf.base.services.upload.UploadService;
import com.buang.welewolf.base.services.upload.UploadTask;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.base.utils.ImageUtil;
import com.buang.welewolf.modules.profile.ModifyNameFragment;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/6/11.
 */

public class ContactSettingFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int REQCODE_CAMERA = 0xA0;
    private static final int REQCODE_PICKER = 0xA1;
    private static final int REQCODE_FROM_CROP = 0xA2;
    public static final String ACTION_INFOEDIT_CHANGE = "com.knowbox.infoedit.changed";
    private static final int ACTION_UPDATE_USERINFO = 1;

    private static final int UPDATE_SEX = 0;
    private static final int UPDATE_BIRTHDAY = 1;
    private static final int UPDATE_HEADPHOTO = 2;
    private File headImageFile;
    private Dialog mDialog;

    private UserItem contactInfo;
    private ImageView mHeadView;
    private TextView mNameView;
    private TextView mSexView;
    private TextView mSignView;
    private TextView mIDView;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_contact_setting, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("编辑资料");
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        headImageFile = new File(DirContext.getImageCacheDir(), "user.jpg");

        mHeadView = (ImageView) view.findViewById(R.id.ivHead);
        mNameView = (TextView) view.findViewById(R.id.tvName);
        mSexView = (TextView) view.findViewById(R.id.tvSex);
        mSignView = (TextView) view.findViewById(R.id.tvSign);
        mIDView = (TextView) view.findViewById(R.id.tvID);

        view.findViewById(R.id.rvHead).setOnClickListener(onClickListener);
        view.findViewById(R.id.lvName).setOnClickListener(onClickListener);
        view.findViewById(R.id.lvSex).setOnClickListener(onClickListener);
        view.findViewById(R.id.lvSign).setOnClickListener(onClickListener);
        setContactView();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.rvHead:
                    updateUserHeadImg();
                    break;
                case R.id.lvName:
                    openModifyNameFragment(ModifyNameFragment.TYPE_USER_NAME, mNameView.getText().toString());
                    break;
                case R.id.lvSex:
                    showSex();
                    break;
                case R.id.lvSign:
                    openModifyNameFragment(ModifyNameFragment.TYPE_USER_SIGN, mSignView.getText().toString());
                    break;
            }
        }
    };

    private void openModifyNameFragment(final int type, String name) {
        Bundle mBundle = new Bundle();
        mBundle.putInt("type", type);
        mBundle.putString("name", name);
        ModifyNameFragment fragment = ModifyNameFragment.newFragment(getActivity(), ModifyNameFragment.class, mBundle);
        fragment.setOnModifyNameListener(new ModifyNameFragment.OnModifyNameListener() {
            @Override
            public void onModifyName(String name) {
                if (type == ModifyNameFragment.TYPE_USER_NAME) {
                    loadData("userName", name);
                } else {
                    loadData("sign", name);
                }
            }
        });
        showFragment(fragment);
    }

    private void setContactView() {
        contactInfo = Utils.getLoginUserItem();
        ImageFetcher.getImageFetcher().loadImage(contactInfo.headPhoto, mHeadView, R.drawable.bt_message_default_head, new RoundDisplayer());
        mNameView.setText(contactInfo.userName);
        mSexView.setText(contactInfo.sex.equals("1") ? "男" : "女");
        mSignView.setText(contactInfo.sign);
    }

    private void loadData(String param, String content) {
        loadDefaultData(PAGE_MORE, param, content);
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getUserEditUrl((String) params[0], (String) params[1]);
        BaseObject result = new DataAcquirer<>().get(url, new BaseObject());
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);

    }

    private void showSex() {
        final List<MenuItem> items = new ArrayList<MenuItem>();
        items.add(new MenuItem(2, "男", ""));
        items.add(new MenuItem(1, "女", ""));
        mDialog = DialogUtils.getListDialog(getActivity(), "修改性别", items,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String sex = "0";
                        if (position == 0) {
                            sex = "0";
                        } else if (position == 1) {
                            sex = "1";
                        }
                        loadData("sex", sex);
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }
                });
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
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
                loadData("headPhoto", s);
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
