package com.knowbox.teacher.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.AdapterView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.base.service.share.ShareContent;
import com.knowbox.base.service.share.ShareService;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineShareHomeworkInfo;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 17/4/14.
 */
public class ShareClassDialog extends BaseUIFragment<UIFragmentHelper> {

    private Dialog mDialog;

    private ClassInfoItem classInfoItem;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
        classInfoItem = getArguments().getParcelable(ConstantsUtils.KEY_CLASSINFOITEM);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        View view = new View(getActivity());
        view.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        loadDefaultData(PAGE_MORE);
    }

    private void showShareDialog(final ShareContent shareContent) {
        final List<MenuItem> items = new ArrayList<MenuItem>();
        MenuItem item = new MenuItem(0, R.drawable.icon_share_qq, "QQ好友", "QQ");
        items.add(item);
        item = new MenuItem(1, R.drawable.icon_share_qqzone, "QQ空间", "QQZone");
        items.add(item);
        item = new MenuItem(2, R.drawable.icon_share_weichat, "微信好友", "WX");
        items.add(item);
        item = new MenuItem(3, R.drawable.icon_share_pyq, "微信朋友圈", "WXCircle");
        items.add(item);

        if (mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
        }
        AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem item = items.get(position);
                doShare(shareContent, item.desc);
                mDialog.dismiss();

            }
        };

        mDialog = DialogUtils.getShareDialog(getActivity(), items, new DialogUtils.OnDialogButtonClickListener() {
            @Override
            public void onItemClick(Dialog dialog, int btnId) {
                switch (btnId) {
                    case DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM:
                        ClipboardManager cmb = (ClipboardManager) getActivity()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        String text = "同学们快来加入我的班群" + shareContent.mShareUrl + "(分享自@ 单词部落)";
                        cmb.setText(text);
                        MobclickAgent.onEvent(BaseApp.getAppContext(),
                                UmengConstant.EVENT_SHARE_COPYLINKER);
                        ToastUtils.showShortToast(getActivity(), "已复制到粘贴板");
                        break;
                    case DialogUtils.OnDialogButtonClickListener.BUTTON_CANCEL:
                        mDialog.dismiss();
                        break;
                }
            }
        }, mOnItemClickListener);
        mDialog.show();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }

    private void doShare(ShareContent content, String platform) {
        ShareService mShareService = (ShareService) getActivity().getSystemService(
                ShareService.SERVICE_NAME);
        if("QQ".equals(platform)){
            mShareService.shareToQQ(getActivity(), content, null);
            UmengConstant.reportUmengEvent(UmengConstant.EVENT_SHARE_QQ, null);
        }else if("QQZone".equals(platform)){
            mShareService.shareToQQZone(getActivity(), content, null);
            UmengConstant.reportUmengEvent(UmengConstant.EVENT_SHARE_QQ_ZONE, null);
        }else if("WX".equals(platform)){
            mShareService.shareToWX(getActivity(), content, null);
            UmengConstant.reportUmengEvent(UmengConstant.EVENT_SHARE_WX, null);
        }else if("WXCircle".equals(platform)){
            mShareService.shareToWXCircle(getActivity(), content, null);
            UmengConstant.reportUmengEvent(UmengConstant.EVENT_SHARE_WX_CIRCLE, null);
        }
    }

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        String url = OnlineServices.getInviteStudentShareUrl(classInfoItem.classCode);
        OnlineShareHomeworkInfo content = new DataAcquirer<OnlineShareHomeworkInfo>().acquire(
                url, new OnlineShareHomeworkInfo(), -1);
        return content;
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        ShareContent shareContent = ((OnlineShareHomeworkInfo) result).shareContent;
        showShareDialog(shareContent);
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        ToastUtils.showShortToast(getContext(), "分享失败");
        finish();
    }

}
