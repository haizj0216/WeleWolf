/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.modules.login;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.update.UpdateService;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.base.utils.HttpHelper;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.DataAcquirer;

import java.io.File;

/**
 * @author Fanjb
 * @name 闪屏页面
 * @date 2015-3-13
 */
public class SplashFragment extends BaseUIFragment {

    private LoginService mLoginService;
    public static boolean fileIsExists = false;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_splash, null);
        return view;
    }


    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
//        ImageView logo = (ImageView) view.findViewById(com.buang.welewolf.R.id.splash_img_logo);
////        ImageView version = (ImageView) view.findViewById(R.id.splash_img_version);
//        ImageView greetImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.splash_img_greetimg);
//        try {
//            String fileName = PreferencesController.getStringValue(ConstantsUtils.GREETING_IMG_FIELPATH);
//            if (!TextUtils.isEmpty(fileName)) {
//                File file = new File(fileName);
//                fileIsExists = file.exists();
//                if (fileIsExists) {
//                    greetImg.setVisibility(View.VISIBLE);
//                    logo.setVisibility(View.GONE);
//                    String path = file.getAbsolutePath();
//                    Bitmap bitmap = createSplashBitmap(path);
//                    greetImg.setImageBitmap(bitmap);
//                }else {
//                    greetImg.setVisibility(View.GONE);
//                }
//            }else {
//                greetImg.setVisibility(View.GONE);
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }

        SplashTask mSplashTask = new SplashTask();
        mSplashTask.execute();
    }

    public static Bitmap createSplashBitmap(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        Bitmap bitmap;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int outHeight = options.outHeight;
            if (outHeight > 2048) {
                options.inSampleSize = 2;
            }else {
                options.inSampleSize = 1;
            }
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeFile(path, options);
            return bitmap;
        } catch (OutOfMemoryError e) {
            LogUtil.e("OutOfMemoryError", "OOM in BitmapUtil.createBitmap : "
                    + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class SplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (getActivity() == null || getActivity().isFinishing()) {
                return null;
            }
            mLoginService = (LoginService) getActivity().getSystemService(
                    LoginService.SERVICE_NAME);
            UpdateService mUpdateService = (UpdateService) getActivity().getSystemService(
                    UpdateService.SERVICE_NAME);
            if (mUpdateService != null) {
                mUpdateService.init();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mLoginService == null || getActivity() == null
                    || getActivity().isFinishing())
                return;

            // 检查是否已经登录
            if (mLoginService.isLogin()) {
                if (mActionListener != null) {
                    mActionListener.onShowMainWindows();
                }
            }else {
                if (mActionListener != null) {
                    mActionListener.onShowIntroduceWindow();
                }
            }
        }
    }


    private SplashActionListener mActionListener;

    public void setSplashActionListener(SplashActionListener listener) {
        this.mActionListener = listener;
    }

    public static interface SplashActionListener {
        public void onShowIntroduceWindow();

        public void onShowMainWindows();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
