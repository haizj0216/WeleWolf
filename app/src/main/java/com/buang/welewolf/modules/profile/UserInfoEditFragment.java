package com.buang.welewolf.modules.profile;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineLoginInfo;
import com.buang.welewolf.modules.login.services.UpdateSchoolListener;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.service.upload.UploadListener;
import com.knowbox.base.service.upload.UploadService;
import com.knowbox.base.service.upload.UploadTask;
import com.knowbox.base.utils.ImageFetcher;
import com.buang.welewolf.R;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.database.tables.UserTable;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.base.utils.ImageUtil;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.login.services.UpdateJiaocaiListener;
import com.buang.welewolf.modules.main.MainProfileFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DateUtil;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.DialogUtils.OnDataTimePickerSelectListener;
import com.buang.welewolf.modules.utils.SubjectUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.widgets.RoundDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @name 用户信息编辑页面
 */
public class UserInfoEditFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int REQCODE_CAMERA = 0xA0;
    private static final int REQCODE_PICKER = 0xA1;
    private static final int REQCODE_FROM_CROP = 0xA2;
    public static final String ACTION_INFOEDIT_CHANGE = "com.knowbox.infoedit.changed";
    private static final int ACTION_UPDATE_USERINFO = 1;

    private static final int UPDATE_SEX = 0;
    private static final int UPDATE_BIRTHDAY = 1;
    private static final int UPDATE_HEADPHOTO = 2;
    private File headImageFile;
    private UserItem mUserItem;
    private String mSchoolName;

    private ImageView mHeadImageView;
    private TextView mNameTextView;
    private TextView mSexText;
    private TextView mBirthdayText;
    private TextView mSchoolText;
    private TextView mSubjetText;
    private TextView mAuthenticationText;

    private Dialog mDialog;

    private int status;
    private String mGradePart;
//    private int mSubjectCode;
    private LoginService loginService;
    //	private String mLevel;
//	private String mTotalExp;
//	private String mLevelExp;
//	private TextView mLevelText;
//	private View mLevelLayout;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
        loginService = (LoginService) BaseApp.getAppContext()
                .getSystemService(LoginService.SERVICE_NAME);
        loginService.getServiceObvserver().addUpdateSchoolListener(updateSchoolListener);
        loginService.getServiceObvserver().addUpdatejiaocaiListener(mUpdateJiaocailistener);
        getActivity()
                .getWindow()
                .setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
                                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
                                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        headImageFile = new File(DirContext.getImageCacheDir(), "/user.jpg");
//		if (getArguments() != null) {
//			mLevel = getArguments().getString("level");
//			mTotalExp = getArguments().getString("totalExp");
//			mLevelExp = getArguments().getString("levelExp");
//		}

    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_profile_edit, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        getUIFragmentHelper().getTitleBar().setTitle("我的信息");
        //注册一个广播接收者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_INFOEDIT_CHANGE);
//        intentFilter.addAction(ActionUtils.ACTION_SCHOOL_CHANGED);
        intentFilter.addAction(ActionUtils.ACTION_SUBJECTINFO_CHANGE);
//        intentFilter.addAction(ActionUtils.ACTION_TEACHINGMATERIAL_UPDATE);
        MsgCenter.registerLocalReceiver(mReceiver, intentFilter);

        mUserItem = Utils.getLoginUserItem();

//        mSubjectCode = Integer.parseInt(mUserItem.subjectCode);
        mGradePart = mUserItem.gradePart;
        // 头像
        mHeadImageView = (ImageView) view.findViewById(R.id.profile_user_icon);
        ImageFetcher.getImageFetcher().loadImage(mUserItem.headPhoto,
                mHeadImageView, R.drawable.profile_icon_default, new RoundDisplayer());
        view.findViewById(R.id.profile_user_icon_layout).setOnClickListener(mOnClickListener);

        // 姓名
        mNameTextView = (TextView) view.findViewById(R.id.profile_user_name);
        mNameTextView.setText(mUserItem.userName);
        view.findViewById(R.id.profile_user_name_layout).setOnClickListener(mOnClickListener);

        // 性别
        mSexText = (TextView) view.findViewById(R.id.profile_user_gender);
        setSex();
        view.findViewById(R.id.profile_user_gender_layout).setOnClickListener(mOnClickListener);

        // 生日
        mBirthdayText = (TextView) view.findViewById(R.id.profile_user_birthday);
        mBirthdayText.setText(DateUtil.getDefaultYMDDate(Long.parseLong(mUserItem.birthday)));
        view.findViewById(R.id.profile_user_birthday_layout).setOnClickListener(mOnClickListener);

        // 学校名称
        mSchoolText = (TextView) view.findViewById(R.id.profile_user_school);
        if (TextUtils.isEmpty(mUserItem.schoolName)) {
            mSchoolText.setText("尚未选择学校，点击修改");
        }else {
            mSchoolText.setText(mUserItem.schoolName);
        }
        view.findViewById(R.id.profile_user_school_layout).setOnClickListener(mOnClickListener);

        // 科目
        mSubjetText = (TextView) view.findViewById(R.id.profile_user_subject);
        mSubjetText.setText(SubjectUtils.getGrade(mUserItem.gradePart) + "英语"/* + SubjectUtils.getNameByCode(
                Integer.parseInt(mUserItem.subjectCode)*/);
        view.findViewById(R.id.profile_user_subject_layout).setOnClickListener(mOnClickListener);

        // 认证信息
        mAuthenticationText = (TextView) view.findViewById(R.id.profile_user_status_authentication);

        if (mUserItem != null) {
            String statustr = mUserItem.authenticationStatus;
            if (TextUtils.isEmpty(statustr)) {
                statustr = "0";
            }
            status = Integer.parseInt(statustr);
        }
        if (status == UserItem.AUTHENTICATION_NOT_YET) {
            mAuthenticationText.setText("仅限英语学科教师认证");
        } else if (status == UserItem.AUTHENTICATION_SUCCESS) {
            mAuthenticationText.setText("已认证");
        } else if (status == UserItem.AUTHENTICATION_FAIL) {
            mAuthenticationText.setText("重新认证");
        } else if (status == UserItem.AUTHENTICATION_UNDER_WAY) {
            mAuthenticationText.setText("正在审核");
        }
//        view.findViewById(R.id.profile_user_authentication_layout).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.profile_user_authentication_layout).setVisibility(View.GONE);

        //等级信息
//		mLevelText = (TextView) view.findViewById(R.id.profile_user_level);
//		mLevelLayout = view.findViewById(R.id.profile_user_level_layout);
//		if (!TextUtils.isEmpty(mLevel)) {
//			mLevelLayout.setVisibility(View.VISIBLE);
//			mLevelText.setText("Lv" + mLevel);
//		}else {
//			mLevelLayout.setVisibility(View.GONE);
//		}
//		mLevelLayout.setOnClickListener(mOnClickListener);

//		if (TextUtils.isEmpty(mLevel)){
//			loadData(ACTION_GETEDITINFO_NEARLY, PAGE_MORE);
//		}
    }

    /**
     * 各个设置条目点击事件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
				ActionUtils.notifyVirtualTip();
				return;
			}*/
            switch (v.getId()) {
                case R.id.profile_user_icon_layout: {//修改头像
                    updateUserHeadImg();
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_ME_FACE, null);
                    break;
                }
                case R.id.profile_user_name_layout: {//修改姓名
                    Bundle mBundle = new Bundle();
                    mBundle.putString("name", mUserItem.userName);
                    ProfileUserNameFragment fragment = (ProfileUserNameFragment) Fragment.instantiate(
                            getActivity(), ProfileUserNameFragment.class.getName(), mBundle);
                    fragment.setOnUserNameEditListener(new ProfileUserNameFragment.OnUserNameEditListener() {
                        @Override
                        public void onUserNameEdit(String newName) {
                            mNameTextView.setText(newName);
                            mUserItem.userName = newName;
                            updateDatabase();
                            MsgCenter.sendLocalBroadcast(new Intent(MainProfileFragment.ACTION_USERINFO_CHANGE));
                        }
                    });
                    showFragment(fragment);

                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_ME_NAME, null);
                    break;
                }
                case R.id.profile_user_gender_layout: {//修改性别
                    final List<MenuItem> items = new ArrayList<MenuItem>();
                    items.add(new MenuItem(2, "男", ""));
                    items.add(new MenuItem(1, "女", ""));
                    mDialog = DialogUtils.getListDialog(getActivity(), "修改性别", items,
                            new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    if (position == 0) {
                                        loadUpdateData("sex", "M", UPDATE_SEX);
                                    }else if (position == 1) {
                                        loadUpdateData("sex", "F", UPDATE_SEX);
                                    }
                                    if (mDialog.isShowing()) {
                                        mDialog.dismiss();
                                    }
                                }
                            });
                    if (!mDialog.isShowing()) {
                        mDialog.show();
                    }

                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_ME_SEX, null);
                    break;
                }
                case R.id.profile_user_birthday_layout: {//修改生日
                    mDialog = DialogUtils.getBirthdayPickerDialog(getActivity(), Long.parseLong(mUserItem.birthday),
                            new OnDataTimePickerSelectListener() {
                                @Override
                                public void onSelectDateTime(long time) {
                                    if (time > System.currentTimeMillis() / 1000) {
                                        ToastUtils.showShortToast(BaseApp.getAppContext(), "日期选择错误");
                                    } else {
                                        loadUpdateData("birthday", String.valueOf(time), UPDATE_BIRTHDAY);
                                    }

                                    if (mDialog.isShowing()) {
                                        mDialog.dismiss();
                                    }
                                }
                            });
                    if (!mDialog.isShowing()) {
                        mDialog.show();
                    }

                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_ME_BIRTH, null);
                    break;
                }
                case R.id.profile_user_school_layout: {//修改学校
                    Bundle bundle = new Bundle();
                    bundle.putString("from_clazzName", UserInfoEditFragment.class.getName());
                    CitySelectFragment fragment = CitySelectFragment.newFragment
                            (getActivity(), CitySelectFragment.class, bundle);
                    showFragment(fragment);
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_ME_SCHOOL, null);
                    break;
                }
                case R.id.profile_user_authentication_layout: {//用户认证
//                    UserAuthNotyetFragment fragment = (UserAuthNotyetFragment) Fragment
//                            .instantiate(getActivity(),
//                                    UserAuthNotyetFragment.class.getName(), null);
//                    showFragment(fragment);
                    break;
                }
                case R.id.profile_user_subject_layout: {
                    Bundle mBundle = new Bundle();
                    mBundle.putInt(ConstantsUtils.SUBJECT_FROM, MainSelectSubjectFragment.FROM_USERINFO);
                    MainSelectSubjectFragment fragment = (MainSelectSubjectFragment) Fragment
                            .instantiate(getActivity(), MainSelectSubjectFragment.class.getName(), mBundle);
                    showFragment(fragment);
                    break;
                }
//				case R.id.profile_user_level_layout:
//					showLevelFragment();
//					break;
            }
        }
    };

    /**
     * 提交要修改的数据
     *
     * @param key        要修改的key
     * @param value      新的属性数据
     * @param actionCode 修改属性的唯一码，必传
     */
    private void loadUpdateData(String key, String value, int actionCode) {
        loadData(ACTION_UPDATE_USERINFO, PAGE_MORE, key, value, actionCode);
    }

    @Override
    public void onPreAction(int action, int pageNo) {
        getLoadingView().showLoading("正在上传数据");
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        if (action == ACTION_UPDATE_USERINFO) {
            JSONObject data = new JSONObject();
            try {
                data.put((String) params[0], (String) params[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new DataAcquirer<OnlineLoginInfo>()
                    .post(OnlineServices.getUpdateUserInfo(), data.toString(), new OnlineLoginInfo());
        }
        return null;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_UPDATE_USERINFO && params.length == 3) {
            int actionCode = (Integer) params[2];
            updateUserInfos((OnlineLoginInfo) result, actionCode);
        }
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        getLoadingView().setVisibility(View.GONE);
        if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
            ToastUtils.showShortToast(getActivity(), "暂无网络请稍后再试!");
        } else if (result != null && !TextUtils.isEmpty(result.getRawResult())) {
            String error = "修改失败";
            if (TextUtils.isEmpty(result.getErrorDescription())) {
                if (params.length == 3) {
                    if ((Integer) params[2] == UPDATE_SEX) {
                        error = "修改性别失败";
                    } else if ((Integer) params[2] == UPDATE_BIRTHDAY) {
                        error = "修改生日失败";
                    } else if ((Integer) params[2] == UPDATE_HEADPHOTO) {
                        error = "修改头像失败";
                    }
                }
            } else {
                error = result.getErrorDescription();
            }
            ToastUtils.showShortToast(getActivity(),
                    ErrorManager.getErrorManager().getErrorHint(result.getRawResult(), error));
        }
    }

    private void updateUserInfos(OnlineLoginInfo info, int actionCode) {
        if (mUserItem == null || info == null) {
            return;
        }
        switch (actionCode) {
            case UPDATE_SEX:
                if (TextUtils.isEmpty(info.mUserItem.sex)) return;
                mUserItem.sex = info.mUserItem.sex;
                setSex();
                break;

            case UPDATE_BIRTHDAY:
                if (TextUtils.isEmpty(info.mUserItem.birthday)) return;
                mUserItem.birthday = info.mUserItem.birthday;
                mBirthdayText.setText(DateUtil.getDefaultYMDDate(Long.parseLong(mUserItem.birthday)));
                break;

            case UPDATE_HEADPHOTO:
                if (TextUtils.isEmpty(info.mUserItem.headPhoto)) return;
                mUserItem.headPhoto = info.mUserItem.headPhoto;
                ImageFetcher.getImageFetcher().loadImage(mUserItem.headPhoto,
                        mHeadImageView, R.drawable.profile_icon_default, new RoundDisplayer());
                ToastUtils.showShortToast(getActivity(), "头像上传成功");
                break;

            default:
                break;
        }
        updateDatabase();
        MsgCenter.sendLocalBroadcast(new Intent(
                MainProfileFragment.ACTION_USERINFO_CHANGE));
    }

    /**
     * 刷新数据库用户信息
     */
    private void updateDatabase() {
        UserTable table = DataBaseManager.getDataBaseManager().getTable(
                UserTable.class);
        table.updateByCase(mUserItem, UserTable.USERID + " = ?",
                new String[]{mUserItem.userId});

    }

    private void setSex() {
        if (mUserItem.sex != null && mSexText != null) {
            if (mUserItem.sex.equals("F")) {
                mSexText.setText("女");
            } else if (mUserItem.sex.equals("M")) {
                mSexText.setText("男");
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            // 重新加载用户认证信息
            if (UserInfoEditFragment.ACTION_INFOEDIT_CHANGE.equals(action)) {
                mUserItem = Utils.getLoginUserItem();
                if (null == mUserItem) return;

                int authStatus = Integer.parseInt(mUserItem.authenticationStatus);
                if (authStatus == UserItem.AUTHENTICATION_SUCCESS) {
                    mAuthenticationText.setText("已认证");
                } else if (authStatus == UserItem.AUTHENTICATION_FAIL) {
                    mAuthenticationText.setText("重新认证");
                } else if (authStatus == UserItem.AUTHENTICATION_UNDER_WAY) {
                    mAuthenticationText.setText("正在审核");
                }
            }else if (ActionUtils.ACTION_SUBJECTINFO_CHANGE.equals(action)) {
                mGradePart = intent.getStringExtra("gradepart");
//                mSubjectCode = Integer.parseInt(intent.getStringExtra("subjectcode"));
                if (!TextUtils.isEmpty(mGradePart)/* && mSubjectCode >= 0*/) {
                    mSubjetText.setText(SubjectUtils.getGrade(mGradePart)
                            + "英语"/* + SubjectUtils.getNameByCode(mSubjectCode)*/);
                }
            }/* else if (ActionUtils.ACTION_TEACHINGMATERIAL_UPDATE.equals(action)) {
                if (!intent.getBooleanExtra(ConstantsUtils.IS_NEWUSER, false)) {
                    getActivity().onBackPressed();
                    getActivity().onBackPressed();
                    getActivity().onBackPressed();
//                    removeAllFragment();
                }
            }*/
        }
    };

    private UpdateSchoolListener updateSchoolListener = new UpdateSchoolListener() {
        @Override
        public void onUpdateSuccess(final UserItem userItem, boolean isUserDefined) {
            mUserItem = Utils.getLoginUserItem();
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mUserItem == null) {
                        mSchoolText.setText(userItem.schoolName);
                    }else {
                        mSchoolText.setText(mUserItem.schoolName);
                    }
                }
            });

        }

        @Override
        public void onUpdateFailed(String error, boolean isUserDefined) {
        }
    };

    private UpdateJiaocaiListener mUpdateJiaocailistener = new UpdateJiaocaiListener() {
        @Override
        public void onUpdateSuccess(UserItem userItem, String clazzName) {
            if (clazzName != null && clazzName.equals(MainSelectSubjectFragment.class.getName())) {
                getActivity().onBackPressed();
                getActivity().onBackPressed();
                getActivity().onBackPressed();
            }
        }

        @Override
        public void onUpdateFailed(String error, String clazzName) {
        }
    };

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        MsgCenter.unRegisterLocalReceiver(mReceiver);
        if (null != loginService) {
            loginService.getServiceObvserver().removeUpdateSchoolListener(updateSchoolListener);
            loginService.getServiceObvserver().removeUpdateJiaocaiListener(mUpdateJiaocailistener);
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
                new OnItemClickListener() {
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
            bitmap.compress(CompressFormat.JPEG, 100, os);
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
                loadUpdateData("head_photo", s, UPDATE_HEADPHOTO);

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
