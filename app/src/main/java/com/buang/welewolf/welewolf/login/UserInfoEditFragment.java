package com.buang.welewolf.welewolf.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.buang.welewolf.R;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.utils.UiThreadHandler;

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
    public static final String ACTION_INFOEDIT_CHANGE = "com.knowbox.infoedit.changed";
    private static final int ACTION_UPDATE_USERINFO = 1;

    private Dialog mDialog;
    private ImageView mPhoto;
    private View mPhotoView;
    private RadioGroup mSex;
    private EditText mName;
    private Button mButton;

    private File headImageFile;


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
        mPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        mPhotoView = view.findViewById(R.id.ivPhotoView);
        mSex = (RadioGroup) view.findViewById(R.id.ivSex);
        mName = (EditText) view.findViewById(R.id.ivName);
        mButton = (Button) view.findViewById(R.id.ivConfirm);

        mPhotoView.setOnClickListener(onClickListener);
        mButton.setOnClickListener(onClickListener);
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
                    ToastUtils.showShortToast(getActivity(), "恭喜进入狼人杀");
                    removeAllFragment();
                    break;
            }
        }
    };

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
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPhoto.setImageBitmap(bitmap);
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
}
