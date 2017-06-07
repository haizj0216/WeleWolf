package com.buang.welewolf.base.services.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import com.hyena.framework.utils.UiThreadHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by weilei on 17/6/7.
 */

public class ShareSDKService implements ShareService {

    @Override
    public void initConfig(final Activity activity) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                ShareSDK.initSDK(activity);
            }
        });
    }

    @Override
    public void shareToWX(final Activity activity, ShareContent content, final ShareListener listener) {
        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        if(!platform.isClientValid()){
            Toast.makeText(activity, "您还没有安装微信，暂时无法分享", Toast.LENGTH_SHORT).show();
            return;
        }
        share(platform, getShareData(content), new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (listener != null) {
                    listener.onComplete(platform, i, hashMap);
                }
                Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (listener != null) {
                    listener.onError(platform, i, throwable);
                }
                Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (listener != null) {
                    listener.onCancel(platform, i);
                }
                Toast.makeText(activity, "分享取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void shareToWXCircle(final Activity activity, ShareContent content, final ShareListener listener) {
        Platform platform = ShareSDK.getPlatform(WechatMoments.NAME);
        if(!platform.isClientValid()){
            Toast.makeText(activity, "您还没有安装微信，暂时无法分享", Toast.LENGTH_SHORT).show();
            return;
        }
        share(platform, getShareData(content), new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (listener != null) {
                    listener.onComplete(platform, i, hashMap);
                }
                Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (listener != null) {
                    listener.onError(platform, i, throwable);
                }
                Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (listener != null) {
                    listener.onCancel(platform, i);
                }
                Toast.makeText(activity, "分享取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void shareToQQ(final Activity activity, ShareContent content, final ShareListener listener) {
        Platform platform = ShareSDK.getPlatform(QQ.NAME);
        if(!platform.isClientValid()){
            Toast.makeText(activity, "您还没有安装QQ，暂时无法分享", Toast.LENGTH_SHORT).show();
            return;
        }
        share(platform, getShareData(content), new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (listener != null) {
                    listener.onComplete(platform, i, hashMap);
                }
                Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (listener != null) {
                    listener.onError(platform, i, throwable);
                }
                Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (listener != null) {
                    listener.onCancel(platform, i);
                }
                Toast.makeText(activity, "分享取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void shareToQQZone(final Activity activity, ShareContent content, final ShareListener listener) {
        Platform platform = ShareSDK.getPlatform(QZone.NAME);
//        if(!platform.isClientValid()){
//            Toast.makeText(activity, "您还没有安装QQ，暂时无法分享", Toast.LENGTH_SHORT).show();
//            return;
//        }
        share(platform, getShareData(content), new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (listener != null) {
                    listener.onComplete(platform, i, hashMap);
                }
                Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (listener != null) {
                    listener.onError(platform, i, throwable);
                }
                Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (listener != null) {
                    listener.onCancel(platform, i);
                }
                Toast.makeText(activity, "分享取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HashMap<String, Object> getShareData(ShareContent content){
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("title", content.mShareTitle);
        data.put("titleUrl", content.mShareTitleUrl);
        data.put("text", content.mShareContent);
        data.put("imageUrl", content.mUrlImage);
        data.put("siteUrl", content.mSiteUrl);
        data.put("site", content.mSiteName);
        data.put("description", content.mDescription);
        data.put("url", content.mShareUrl);
        return data;
    }

    /**
     * 分享具体实现
     * @param plat
     * @param data
     * @param listener
     * @return
     */
    public boolean share(Platform plat, HashMap<String, Object> data, PlatformActionListener listener) {
        if (plat == null || data == null) {
            return false;
        }
        try {
            String imagePath = (String) data.get("imagePath");
            Bitmap viewToShare = (Bitmap) data.get("viewToShare");
            if (TextUtils.isEmpty(imagePath) && viewToShare != null && !viewToShare.isRecycled()) {
                String path = com.mob.tools.utils.R.getCachePath(plat.getContext(), "screenshot");
                File ss = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                FileOutputStream fos = new FileOutputStream(ss);
                viewToShare.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                data.put("imagePath", ss.getAbsolutePath());
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }

        data.put("shareType", getShareType(data));
        Platform.ShareParams sp = new Platform.ShareParams(data);
        plat.setPlatformActionListener(listener);
        plat.share(sp);
        return true;
    }

    /**
     * 获得分享类型
     * @param data
     * @return
     */
    private int getShareType(HashMap<String, Object> data){
        int shareType = Platform.SHARE_TEXT;
        String imagePath = String.valueOf(data.get("imagePath"));
        if (imagePath != null && (new File(imagePath)).exists()) {
            shareType = Platform.SHARE_IMAGE;
            if (imagePath.endsWith(".gif")) {
                shareType = Platform.SHARE_EMOJI;
            } else if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString())) {
                shareType = Platform.SHARE_WEBPAGE;
                if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString())) {
                    shareType = Platform.SHARE_MUSIC;
                }
            }
        } else {
            Bitmap viewToShare = (Bitmap) data.get("viewToShare");
            if (viewToShare != null && !viewToShare.isRecycled()) {
                shareType = Platform.SHARE_IMAGE;
                if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString())) {
                    shareType = Platform.SHARE_WEBPAGE;
                    if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString())) {
                        shareType = Platform.SHARE_MUSIC;
                    }
                }
            } else {
                Object imageUrl = data.get("imageUrl");
                if (imageUrl != null && !TextUtils.isEmpty(String.valueOf(imageUrl))) {
                    shareType = Platform.SHARE_IMAGE;
                    if (String.valueOf(imageUrl).endsWith(".gif")) {
                        shareType = Platform.SHARE_EMOJI;
                    } else if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString())) {
                        shareType = Platform.SHARE_WEBPAGE;
                        if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString())) {
                            shareType = Platform.SHARE_MUSIC;
                        }
                    }
                }
            }
        }
        return shareType;
    }
}
