package com.knowbox.teacher.modules.debugs;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.servcie.debug.DebugService;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.App;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.DebugUtils;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @name 开发者模式
 * @author Fanjb
 * @date 2015年4月14日
 */
public class DebugFragment extends BaseUIFragment<UIFragmentHelper> {

	private ToggleButton mDebugSwitchBtn;
	private ToggleButton mAllowMultiNodeLogin;
	private DebugService mDebugService;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
		mDebugService = (DebugService) getActivity().getSystemService(
				DebugService.SERVICE_NAME);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		getTitleBar().setTitle("开发者模式");
		return View.inflate(getActivity(), R.layout.layout_debug, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);

		// 打开或关闭DebugCansole
		mDebugSwitchBtn = ((ToggleButton) view
				.findViewById(R.id.debug_cansole_switch));
		mDebugSwitchBtn.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mDebugSwitchBtn.setChecked(mDebugService.isDebug());

		view.findViewById(R.id.ping_layout)
				.setOnClickListener(mOnClickListener);

		// 默认不允许多端登录
		mAllowMultiNodeLogin = ((ToggleButton) view
				.findViewById(R.id.allow_multi_node_login_switch));
		mAllowMultiNodeLogin
				.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mAllowMultiNodeLogin.setChecked(App.ALLOW_MULTI_NODE);

		view.findViewById(R.id.copy_db_to_external_layout).setOnClickListener(
				mOnClickListener);
		view.findViewById(R.id.ping_layout)
				.setOnClickListener(mOnClickListener);
		view.findViewById(R.id.clear_default_db_layout).setOnClickListener(
				mOnClickListener);
		view.findViewById(R.id.app_detail_layout).setOnClickListener(
				mOnClickListener);
	}

	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (buttonView == mDebugSwitchBtn) {
				mDebugService.enableDebug(isChecked);
			} else if (buttonView == mAllowMultiNodeLogin) {
				App.ALLOW_MULTI_NODE = isChecked;
			}
		}
	};

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ping_layout:
				new Thread(){
					public void run() {
						ping();
					}
				}.start();
				break;
			case R.id.copy_db_to_external_layout:
				DebugUtils.copyDB2Sdcard(getActivity());
				ToastUtils
						.showLongToast(getActivity(), "数据库文件已成功拷贝到SDCard根目录下");
				break;
			case R.id.clear_default_db_layout:
				DataBaseManager.getDataBaseManager().clearDataBase();
				ToastUtils.showShortToast(getActivity(), "数据库已清空");
				break;
			case R.id.app_detail_layout:
				showAppDetail();
				break;
			default:
				break;
			}
		}
	};

	private void ping() {
		final StringBuffer sb = new StringBuffer();
		String hostStr = Uri.parse(OnlineServices.getPhpUrlPrefix()).getHost();
		int sendCount = 0;
		int unReceivedCount = 0;
		long alltime = 0;
		try {
			InetAddress ia = InetAddress.getByName(hostStr);
			sb.append("PING...目标地址\n" + ia.toString() + "\n");
			for (sendCount = 0; sendCount < 4; sendCount++) {
				long startTimeMills = System.currentTimeMillis();
				Socket socket = new Socket(ia, 80);
				long time = (System.currentTimeMillis() - startTimeMills);
				sb.append("第" + (sendCount + 1) + "次回复：\n");
				if ((time / 1000) > 30) {
					sb.append("status=Fail,time=" + time
							+ "ms,package=32bytes\n");
					unReceivedCount++;
				}
				socket.close();
				sb.append("status=Success,time=" + time
						+ "ms,package=32bytes\n");
				alltime += time;
			}
		} catch (UnknownHostException e) {
			sb.append("PING...目标地址\n" + hostStr + "\n可能网络连接失败\n" + e);
		} catch (IOException e) {
			e.printStackTrace();
			sb.append("PING...目标地址\n" + hostStr + "\n可能网络连接失败\n" + e);
		}
		float loss = ((float) unReceivedCount / sendCount) * 100;
		sb.append("\n统计信息:\n")
				.append("数据包: 已发送" + sendCount + "次，已接收"
						+ (sendCount - unReceivedCount) + "次，丢包" + loss + "%\n");
		sb.append("平均往返时间:" + alltime / sendCount + "ms");
		UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				DialogUtils.getDebugLogDialog(getActivity(), "Ping 服务", sb.toString()).show();
				mDebugService.showDebugMsg(sb.toString());	
			}
		});

	}

	/**
	 * 查看应用详细信息
	 */
	private void showAppDetail() {
		String packageName = getActivity().getPackageName();
		Uri packageURI = Uri.parse("package:" + packageName);
		ComponentName component = new ComponentName("com.android.settings",
				"com.android.settings.applications.InstalledAppDetails");
		Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		i.setComponent(component);
		i.setData(packageURI);
		startActivity(i);

	}
}
