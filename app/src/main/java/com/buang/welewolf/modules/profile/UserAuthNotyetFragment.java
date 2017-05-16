package com.buang.welewolf.modules.profile;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineLoginInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.FileUtils;
import com.buang.welewolf.base.utils.StringUtils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.service.upload.UploadListener;
import com.knowbox.base.service.upload.UploadService;
import com.knowbox.base.service.upload.UploadTask;
import com.knowbox.base.utils.UIUtils;
import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.OnlineTeacherExtInfo;
import com.buang.welewolf.base.database.tables.UserTable;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.base.utils.ImageUtil;
import com.buang.welewolf.modules.main.MainProfileFragment;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.webactivity.NewTaskActivityFragment;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author LiuYu
 * @name 用户教师资格证认证页面
 * @date 2015-9-5
 */
public class UserAuthNotyetFragment extends BaseUIFragment<UIFragmentHelper> implements View.OnClickListener {

    /**
     * 为上传的图片定义的一个action常量
     */
    private static final int ACTION_UPLOAD_DOCUMENT_PICTURE = 1;
    private static final int ACTION_GETUSERINFO_NEARLY = 2;

    //用户（教师）的信息条目
    private UserItem mUserItem;
    private int mAuthStatus = 0;

    //证件审核状态
    private File userDocumentFile;
//    private UploadDataService mRecorderService;
    private byte[] imageDatas;
    private String mCertificateUrl;
    private String mcertTime;

    private View mAuthCertView;
    private View mAuthPhotoView;
    private View mAuthingView;
    private View mAuthSucView;
//    private EditText mInvitationEdit;
//    private TextView mAuthCerInvitor;
//    private TextView mAuthSucInvitor;
//    private TextView mAuthingInvitor;
    private TextView mAuthTimeTxt;
    private TextView mAuthFailView;
    private TextView mAuthSucTime;
    private ImageView mAuthImage;
    private WebView mRuleView;
    private Button mUploadBtn;
    private View mEmptyView;
    private Dialog mDialog;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mUserItem = Utils.getLoginUserItem();
//        mRecorderService = (UploadDataService) BaseApp.getAppContext()
//                .getSystemService(UploadDataService.SERVICE_NAME);
        userDocumentFile = new File(DirContext.getImageCacheDir(), "authentication_image"
                + Utils.getLoginUserItem().userId + ".jpg");
        if (userDocumentFile != null && userDocumentFile.exists()) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    userDocumentFile.delete();
                }
            }.start();
        }
        mcertTime = mUserItem.mCertTime;
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_profile_authentication, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getTitleBar().setTitle("教师认证");
        initView(view);
        setMyOnClickListener();

        loadData(ACTION_GETUSERINFO_NEARLY, PAGE_FIRST);
    }

    /**
     * 初始化布局
     */
    private void initView(View view) {
        mAuthPhotoView = view.findViewById(R.id.profile_certification_image_layout);
        mAuthSucView = view.findViewById(R.id.profile_certification_success_layout);
        mAuthCertView = view.findViewById(R.id.profile_certification_cert_layout);
        mAuthingView = view.findViewById(R.id.profile_certification_certing_layout);
        mUploadBtn = (Button) view.findViewById(R.id.btn_upload_teacherinfo);

//        mInvitationEdit = (EditText) view.findViewById(R.id.profile_invitationcode_edit);
//        mAuthCerInvitor = (TextView) view.findViewById(R.id.profile_invitationcode_text);
        mAuthFailView = (TextView) view.findViewById(R.id.profile_certification_fail_reason);
        mAuthImage = (ImageView) view.findViewById(R.id.profile_certification_image);
        mAuthSucTime = (TextView) view.findViewById(R.id.profile_certification_sucess_time);
        mAuthTimeTxt = (TextView) view.findViewById(R.id.profile_certification_certing_time);
//        mAuthingInvitor = (TextView) view.findViewById(R.id.profile_certification_certing_invitor);
//        mAuthSucInvitor = (TextView) view.findViewById(R.id.profile_certification_sucess_invitor);
        mRuleView = (WebView) view.findViewById(R.id.profile_certification_rule);
        mEmptyView = view.findViewById(R.id.profile_certification_empty);
        if (null == mUserItem) {
            return;
        }
        String status = mUserItem.authenticationStatus;
        mAuthStatus = Integer.parseInt(status);
        updateAuthStatus(mAuthStatus);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    /**
     * 更新认证状态
     *
     * @param statusint
     */
    private void updateAuthStatus(int statusint) {
        switch (statusint) {
            case UserItem.AUTHENTICATION_NOT_YET: {
                //还没有认证，显示未认证的界面
                mAuthSucView.setVisibility(View.GONE);
                mAuthCertView.setVisibility(View.VISIBLE);
                mAuthingView.setVisibility(View.GONE);
                mAuthFailView.setVisibility(View.GONE);
                break;
            }

            case UserItem.AUTHENTICATION_SUCCESS: {
                //认证成功，显示认证成功的页面
                mAuthSucView.setVisibility(View.VISIBLE);
                mAuthCertView.setVisibility(View.GONE);
                mAuthingView.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(mcertTime))
                    mAuthSucTime.setText(getCertificateTime(mcertTime) + "通过认证");
                else {
                    mAuthSucTime.setVisibility(View.GONE);
                }
                break;
            }

            case UserItem.AUTHENTICATION_UNDER_WAY: {
                //正在认证
                mAuthSucView.setVisibility(View.GONE);
                mAuthCertView.setVisibility(View.GONE);
                mAuthingView.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mcertTime))
                    mAuthTimeTxt.setText(getCertificateTime(mcertTime) + "提交，预计3个工作日内审核完成。");
                else {
                    mAuthTimeTxt.setVisibility(View.GONE);
                }
                break;
            }
            case UserItem.AUTHENTICATION_FAIL: {
                //已经认证，失败了。显示认证失败界面
                mAuthCertView.setVisibility(View.VISIBLE);
                mAuthingView.setVisibility(View.GONE);
                mAuthSucView.setVisibility(View.GONE);
                if (TextUtils.isEmpty(mUserItem.authError)) {
                    mAuthFailView.setText("证件审核失败，请重新上传。");
                } else
                    mAuthFailView.setText("证件审核失败，请重新上传。失败原因：" + mUserItem.authError);
                mAuthFailView.setVisibility(View.VISIBLE);
                break;
            }
            default:
                break;
        }
    }

    private void showCancelDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getMessageDialog(getActivity(), "提示", "退出", "取消",
                "您已经拍摄了审核照片，确定要退出吗？", new DialogUtils.OnDialogButtonClickListener() {
                    @Override
                    public void onItemClick(Dialog dialog, int btnId) {
                        if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                            mDialog.dismiss();
                            finish();
                        } else {
                            mDialog.dismiss();
                        }
                    }
                });
        mDialog.show();
    }

    /**
     * 设置这个Fragment中需要的监听器
     */
    private void setMyOnClickListener() {
        mUploadBtn.setOnClickListener(this);
        mAuthPhotoView.setOnClickListener(this);
        mAuthImage.setOnClickListener(this);
    }

    /**
     * 上传拍摄的资格证图片(附带数据)
     */
    private void upLoadUserData() {
        //校验数据的合法性
        final HashMap<String,String> umengMap = new HashMap<String, String>();
        umengMap.put(UmengConstant.KEY_AUTH_CLICK, "1");
        if (imageDatas == null) {
            if (userDocumentFile != null && userDocumentFile.exists()) {
                imageDatas = ImageUtil.getBitmapBytes(userDocumentFile, 300,
                        800, 800);
            } else {
                umengMap.put(UmengConstant.RESULT, UmengConstant.FAIL + ":图片文件不存在");
                MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
                ToastUtils.showShortToast(getActivity(), "请拍摄证件照");
            }
        }
        if (imageDatas != null && imageDatas.length != 0) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    // 上传网络
                    uploadBmpToNet(imageDatas);
                }
            }.start();
        } else {
            umengMap.put(UmengConstant.RESULT, UmengConstant.FAIL + ":图片文件处理异常");
            MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
        }
    }

    /**
     * 将图片上传到网络
     */
    private void uploadBmpToNet(byte[] imageData) {
        final HashMap<String,String> umengMap = new HashMap<String, String>();

        if (imageData == null) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShortToast(getActivity(), "上传头像失败");
                }
            });
            umengMap.put(UmengConstant.RESULT, UmengConstant.FAIL + ":图片文件处理异常");
            MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
            return;
        }
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
            public void onUploadComplete(UploadTask uploadTask, String url) {
                showContent();
                if (!TextUtils.isEmpty(url)) {
                    String mInvitationCode = "";
//                    if (mInvitationEdit != null)
//                        mInvitationCode = mInvitationEdit.getText().toString().trim();//邀请码
                    loadData(ACTION_UPLOAD_DOCUMENT_PICTURE, 2, url, mInvitationCode);
                    mUserItem.authImage = url;
                    umengMap.put(UmengConstant.RESULT, UmengConstant.SUCCESS);
                    MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
                } else {
                    umengMap.put(UmengConstant.RESULT, UmengConstant.FAIL + ":图片文件地址为空");
                    MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
                }
            }

            @Override
            public void onUploadError(UploadTask uploadTask, int i, String s, String extend) {
                showContent();
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(getActivity(), "上传头像失败");
                    }
                });
                umengMap.put(UmengConstant.RESULT, UmengConstant.FAIL + ":" + s);
                MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
            }

			@Override
			public void onRetry(UploadTask uploadTask, int errorCode,
					String error, String extend) {
			}
        });
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        super.onPreAction(action, pageNo);
        if (action == ACTION_UPLOAD_DOCUMENT_PICTURE) {
            getLoadingView().showLoading();
        } else {
            getUIFragmentHelper().getLoadingView().showHomeworkLoading();
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        if (action == ACTION_UPLOAD_DOCUMENT_PICTURE) {
            String url = OnlineServices.getUpdateUserInfo();
            JSONObject object = new JSONObject();
            try {
                object.put("certificate_img", (String) params[0]);
//                object.put("invite_code", (String) params[1]);//邀请码
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String data = object.toString();
            OnlineLoginInfo info = new DataAcquirer<OnlineLoginInfo>()
                    .post(url, data, new OnlineLoginInfo());
            final HashMap<String,String> umengMap = new HashMap<String, String>();
            if (info.isAvailable()) {
                umengMap.put(UmengConstant.KEY_AUTH_UPLOAD_SUCCESS, UmengConstant.SUCCESS);
                MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
            } else {
                umengMap.put(UmengConstant.KEY_AUTH_UPLOAD_SUCCESS, UmengConstant.FAIL + ":" + info.getRawResult());
                MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
            }
            return info;
        } else if (action == ACTION_GETUSERINFO_NEARLY) {
            String url = OnlineServices.getTeacherExtUrl(Utils.getToken());
            OnlineTeacherExtInfo result = new DataAcquirer<OnlineTeacherExtInfo>().acquire(url
                    , new OnlineTeacherExtInfo(), -1);
            return result;
        }
        return null;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result) {
        super.onGet(action, pageNo, result);
        showContent();
        if (action == ACTION_UPLOAD_DOCUMENT_PICTURE) {
            if (result.isAvailable()) {
                OnlineLoginInfo info = (OnlineLoginInfo) result;
                mUserItem.authImage = info.mUserItem.authImage;
                mUserItem.authenticationStatus = info.mUserItem.authenticationStatus;
                mUserItem.mCertTime = info.mUserItem.mCertTime;
                //更新数据库
                updateUserDB();
                UserAuthUploadSuccessFragment fragment = (UserAuthUploadSuccessFragment) Fragment
                        .instantiate(getActivity(), UserAuthUploadSuccessFragment.class.getName(), null);
                showFragment(fragment);
                //重新请求数据
                finish();
            }
        } else if (action == ACTION_GETUSERINFO_NEARLY) {
            if (result.isAvailable()) {
                updateExtInfo((OnlineTeacherExtInfo) result);
            }
        }
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result) {
        super.onFail(action, pageNo, result);
        if (action == ACTION_UPLOAD_DOCUMENT_PICTURE) {
            showContent();
        } else if (action == ACTION_GETUSERINFO_NEARLY) {
            getUIFragmentHelper().getEmptyView().showEmpty();
        }
    }

    private void showQualificationDialog(OnlineTeacherExtInfo result) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = DialogUtils.getWebViewDialog(getActivity(), null, result.mAuthRule, "我知道了", null, new DialogUtils.OnDialogButtonClickListener(){

            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                    mDialog.dismiss();
                }
            }
        });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        mDialog.show();
    }

    private void updateExtInfo(OnlineTeacherExtInfo result) {
        if (result != null) {
            if (!result.hasQualification) {
                mEmptyView.setVisibility(View.VISIBLE);;
                showQualificationDialog(result);
                return;
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            if (!mUserItem.authenticationStatus.equals(result.mAuthStatus)) {
                mAuthStatus = result.mAuthStatus;
                mUserItem.authenticationStatus = String.valueOf(mAuthStatus);
                mUserItem.mCertTime = result.mAuthTime;
                mUserItem.authError = result.mAuthError;
                mcertTime = mUserItem.mCertTime;
                updateUserDB();
                MsgCenter.sendLocalBroadcast(new Intent(
                        MainProfileFragment.ACTION_USERINFO_CHANGE));
                MsgCenter.sendLocalBroadcast(new Intent(
                        UserInfoEditFragment.ACTION_INFOEDIT_CHANGE));
                updateAuthStatus(mAuthStatus);
            }

//            String mInviteUserName = result.mAuthInviteOriginUserName;
            if (mAuthStatus == UserItem.AUTHENTICATION_NOT_YET) {
                mCertificateUrl = result.mAuthUrl;
                doNewTaskActivity();
            }
//            if (!TextUtils.isEmpty(mInviteUserName)) {
//                if (mAuthStatus == UserItem.AUTHENTICATION_FAIL
//                        || mAuthStatus == UserItem.AUTHENTICATION_NOT_YET) {
//                    mAuthCerInvitor.setVisibility(View.VISIBLE);
//                    mInvitationEdit.setVisibility(View.GONE);
//                    mAuthCerInvitor.setText("已接受" + mInviteUserName + "老师的邀请");
//                } else if (mAuthStatus == UserItem.AUTHENTICATION_SUCCESS) {
//                    mAuthSucInvitor.setText("已接受" + mInviteUserName + "老师的邀请");
//                    mAuthSucInvitor.setVisibility(View.VISIBLE);
//                } else if (mAuthStatus == UserItem.AUTHENTICATION_UNDER_WAY) {
//                    mAuthingInvitor.setText("已接受" + mInviteUserName + "老师的邀请");
//                    mAuthingInvitor.setVisibility(View.VISIBLE);
//                }
//            } else {
//                if (mAuthStatus == UserItem.AUTHENTICATION_FAIL
//                        || mAuthStatus == UserItem.AUTHENTICATION_NOT_YET) {
//                    mAuthCerInvitor.setVisibility(View.GONE);
//                    mInvitationEdit.setVisibility(View.VISIBLE);
//                } else if (mAuthStatus == UserItem.AUTHENTICATION_SUCCESS) {
//                    mAuthSucInvitor.setVisibility(View.GONE);
//                } else if (mAuthStatus == UserItem.AUTHENTICATION_UNDER_WAY) {
//                    mAuthingInvitor.setVisibility(View.GONE);
//                }
//            }
            if (mAuthStatus == UserItem.AUTHENTICATION_FAIL
                    || mAuthStatus == UserItem.AUTHENTICATION_NOT_YET) {
                if (!TextUtils.isEmpty(result.mAuthRule)) {
                    StringUtils.setStemHtml(mRuleView, result.mAuthRule);
                    mRuleView.setVisibility(View.VISIBLE);
                } else {
                    mRuleView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_certification_image_layout:
                hideKeyboard();
                shootingUserDocument();
                break;

            case R.id.btn_upload_teacherinfo:
                //点击确认提交
                hideKeyboard();
                upLoadUserData();
                break;

            case R.id.profile_certification_image:
                //图片预览
                hideKeyboard();
                previewAuthImage();
                break;
            case R.id.title_bar_back:
                if (mAuthImage != null && mAuthImage.getVisibility() == View.VISIBLE) {
                    showCancelDialog();
                } else {
                    finish();
                }
                break;
            default:
                break;

        }
    }

    public void doNewTaskActivity() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        if (!TextUtils.isEmpty(mCertificateUrl)) {
            Bundle mBundle = new Bundle();
            mBundle.putString("url", mCertificateUrl);
            NewTaskActivityFragment fragment = NewTaskActivityFragment.newFragment
                    (getActivity(), NewTaskActivityFragment.class, mBundle, AnimType.ANIM_NONE);
            showFragment(fragment);
        }
    }

    /**
     * 打开图片预览
     */
    private void previewAuthImage() {
        UIUtils.hideInputMethod(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString("filename", userDocumentFile.getAbsolutePath());
        DocumentImgPreviewFragment fragment = (DocumentImgPreviewFragment) Fragment.instantiate(getActivity(),
                DocumentImgPreviewFragment.class.getName(), bundle);
        fragment.setOnImageDeleteListener(new DocumentImgPreviewFragment.OnImageDeleteListener() {
            @Override
            public void onImageDelete(final String filename) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        FileUtils.deleteFile(filename);
                        updataMiniPicture();
                    }
                }.start();
            }
        });
        showFragment(fragment);
    }

    /**
     * 拍摄用户的证件照
     */
    private void shootingUserDocument() {
        UIUtils.hideInputMethod(getActivity());
        AuthShootingDocumentFragment fragment = (AuthShootingDocumentFragment) Fragment.instantiate(getActivity(),
                AuthShootingDocumentFragment.class.getName());
        fragment.setOnCaptureFinshListener(new AuthShootingDocumentFragment.OnCaptureFinshListener() {
            @Override
            public void onCaptureFinish(File localImageFile) {
                File mDocumentFile = localImageFile;
                if (mDocumentFile != null && mDocumentFile.exists()) {
                    imageDatas = ImageUtil.getBitmapBytes(mDocumentFile,
                            300, 800, 800);
                    final Bitmap bitmap = compresseBitmap(imageDatas);
                    if (bitmap != null) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                // 写到本地
                                writeBmpToNative(bitmap);
                                updataMiniPicture();
                            }
                        }.start();
                    }
                }
            }
        });
        showFragment(fragment);
    }

    /**
     * 更新迷你图
     */
    private void updataMiniPicture() {
        UIUtils.hideInputMethod(getActivity());
        if (userDocumentFile != null && userDocumentFile.exists()) {
            if (imageDatas == null) {
                imageDatas = ImageUtil.getBitmapBytes(userDocumentFile,
                        300, 800, 800);
            }
            final Bitmap bitmap = compresseBitmap(imageDatas);
            if (bitmap != null) {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAuthImage.setImageBitmap(bitmap);
                        mAuthImage.setVisibility(View.VISIBLE);
                        mUploadBtn.setEnabled(true);
                    }
                });
            } else {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAuthImage.setImageBitmap(null);
                        mAuthImage.setVisibility(View.GONE);
                        mUploadBtn.setEnabled(false);
                    }
                });
            }
        } else {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAuthImage.setVisibility(View.GONE);
                    mAuthImage.setImageBitmap(null);
                    mUploadBtn.setEnabled(false);
                }
            });
        }
    }

    /**
     * 将图片写到本地
     *
     * @param bitmap
     */
    private void writeBmpToNative(Bitmap bitmap) {
        userDocumentFile.delete();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(userDocumentFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 压缩bitmap
     *
     * @return
     */
    private Bitmap compresseBitmap(byte[] imageData) {
        if (imageData != null && imageData.length != 0) {
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } else {
            return null;
        }
    }

    private void updateUserDB() {
        DataBaseManager.getDataBaseManager().getTable(
                UserTable.class).updateCurrentUserInfo(mUserItem);
    }

    /**
     * 获取认证日期
     *
     * @param mLongStr
     * @return
     */
    private String getCertificateTime(String mLongStr) {
        SimpleDateFormat mDateFmt = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        Date certificateTime = new Date(Long.parseLong(mLongStr) * 1000);
        return mDateFmt.format(certificateTime);
    }

    private boolean hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                return manager.hideSoftInputFromWindow(getActivity()
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return false;
    }

    @Override
    public void finish() {
        if (hideKeyboard())
            return;
        super.finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mAuthImage != null && mAuthImage.getVisibility() == View.VISIBLE) {
                showCancelDialog();
                return true;
            }
            hideKeyboard();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        if (mRuleView != null) {
            mRuleView.destroy();
        }
    }
}
