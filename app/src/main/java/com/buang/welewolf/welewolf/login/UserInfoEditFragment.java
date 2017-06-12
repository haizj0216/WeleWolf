package com.buang.welewolf.welewolf.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineLoginInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.database.tables.UserTable;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.upload.UploadListener;
import com.buang.welewolf.base.services.upload.UploadService;
import com.buang.welewolf.base.services.upload.UploadTask;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.base.utils.FileUtils;
import com.buang.welewolf.base.utils.ImageUtil;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.ImageUtils;
import com.hyena.framework.utils.RoundDisplayer;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/5/16.
 */

public class UserInfoEditFragment extends BaseUIFragment<UIFragmentHelper> {
    private static final int REQCODE_CAMERA = 0xA0;
    private static final int REQCODE_PICKER = 0xA1;
    private static final int REQCODE_FROM_CROP = 0xA2;

    private Dialog mDialog;
    private ImageView mPhoto;
    private View mPhotoView;
    private RadioGroup mSex;
    private EditText mName;
    private Button mButton;

    private File headImageFile;
    private String imageUrl;
    private int sex;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        headImageFile = new File(DirContext.getImageCacheDir(), "/user.jpg");
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_welewolf_user_edit, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("完善资料");
        getUIFragmentHelper().getTitleBar().setBackBtnVisible(false);
        getUIFragmentHelper().setTintBar(getResources().getColor(R.color.color_title_bar));
        mPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        mPhotoView = view.findViewById(R.id.ivPhotoView);
        mSex = (RadioGroup) view.findViewById(R.id.ivSex);
        mName = (EditText) view.findViewById(R.id.ivName);
        mButton = (Button) view.findViewById(R.id.ivConfirm);

        mPhotoView.setOnClickListener(onClickListener);
        mButton.setOnClickListener(onClickListener);
        mSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.ivMale) {
                    sex = 0;
                } else if (checkedId == R.id.ivFemale) {
                    sex = 1;
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.ivPhotoView:
                    updateUserHeadImg();
                    break;
                case R.id.ivConfirm:
                    try {
                        loadData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void loadData() throws JSONException {
        UIUtils.hideInputMethod(getActivity());
        if (TextUtils.isEmpty(imageUrl)) {
            ToastUtils.showShortToast(getActivity(), "请上传头像");
            return;
        }
        if (mName.getText().length() > 20 || mName.getText().length() < 2) {
            ToastUtils.showShortToast(getActivity(), "请输入正确的名称");
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userName", mName.getText().toString());
        jsonObject.put("sex", sex);
        jsonObject.put("headPhoto", imageUrl);
        loadDefaultData(PAGE_MORE, jsonObject.toString());
    }


    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getFormatUserInfoUrl();
        OnlineLoginInfo result = new DataAcquirer<OnlineLoginInfo>().post(url, (String) params[0], new OnlineLoginInfo());
        return result;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        UserItem user = ((OnlineLoginInfo) result).mUserItem;
        DataBaseManager.getDataBaseManager().getTable(UserTable.class).updateCurrentUserInfo(user);
        finish();
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
                            try {
                                Intent intent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(headImageFile));
                                intent.putExtra("return-data", false);
                                startActivityForResult(intent, REQCODE_CAMERA);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtils.showShortToast(getActivity(), "打开相机失败");
                            }

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
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            uploadImage(bitmap);
                        }
                    });
                }
            }
        }
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

    private void uploadImage(Bitmap bitmap) {
        if (bitmap != null) {
            FileUtils.saveBitmap(bitmap, headImageFile);
            // 上传七牛云
            final byte[] imageData = ImageUtil.getBitmapBytes(headImageFile, 100, 200, 200);
            if (getActivity() == null) {//崩溃处理
                return;
            }
            UploadService uploadService = (UploadService) getActivity().getSystemService(UploadService.SERVICE_NAME_QINIU);
            uploadService.upload(new UploadTask(UploadTask.TYPE_PICTURE, imageData), new UploadListener() {
                @Override
                public void onUploadStarted(UploadTask uploadTask) {
                }

                @Override
                public void onUploadProgress(UploadTask uploadTask, double v) {

                }

                @Override
                public void onUploadComplete(UploadTask uploadTask, final String s) {
                    imageUrl = s;
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ImageFetcher.getImageFetcher().loadImage(s, mPhoto, R.drawable.bt_message_default_head, new RoundDisplayer());
                        }
                    });
                }

                @Override
                public void onUploadError(UploadTask uploadTask, int i, String s, String s1) {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "上传头像失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onRetry(UploadTask uploadTask, int i, String s, String s1) {

                }

            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
