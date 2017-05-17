package com.buang.welewolf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineVersion;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.database.tables.QuestionTable;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.services.update.CheckVersionListener;
import com.buang.welewolf.base.services.update.UpdateService;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.homework.assign.AssignSelectPublisherFragment;
import com.buang.welewolf.modules.login.IntroduceFragment;
import com.buang.welewolf.modules.login.LoginFragment;
import com.buang.welewolf.modules.login.SplashFragment;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.login.services.UpdateJiaocaiListener;
import com.buang.welewolf.modules.login.services.UserStateChangeListener;
import com.buang.welewolf.modules.message.services.EMChatService;
import com.buang.welewolf.modules.profile.MainSelectSubjectFragment;
import com.buang.welewolf.modules.services.RongIMService;
import com.buang.welewolf.modules.utils.BoxViewBuilder;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.PackageUpdateTask;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.utils.VirtualClassUtils;
import com.hyena.framework.app.activity.NavigateActivity;
import com.hyena.framework.app.fragment.BaseFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.UIViewFactory;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.servcie.debug.DebugModeListener;
import com.hyena.framework.servcie.debug.DebugService;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.buang.welewolf.modules.main.MainFragment;
import com.buang.welewolf.modules.message.services.EMChatLoginListener;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.youme.voiceengine.VoiceEngineService;
import com.youme.voiceengine.mgr.YouMeManager;

import java.io.File;

import io.rong.imlib.RongIMClient;

/**
 * 主场景
 * 
 * @author yangzc
 */
@SuppressLint("NewApi")
public class MainActivity extends NavigateActivity {

	private static final String TAG = "MainActivity";
	private static final int SCENE_LOGIN = 0x00000011;//登陆
	private static final int SCENE_MAIN = 0x00000012;// 主场景
	private static final int SCENE_SPLASH = 0x00000013;// 闪屏页面
	private static final int SCENE_INTRODUCE = 0x00000014;// 引导页面
	private static final int SCENE_SUBJECT = 0x00000015;//学科选择
	private static final int SCENE_BOOK = 0x00000016;//教材

	private int mCurrentScene = -1;

	private View mDebugPanel;
	private TextView mDebugTxt;
	// 登录服务
	private LoginService mLoginService;
	// 升级服务
	private UpdateService mUpdateService;
	// Debug服务
	private DebugService mDebugService;
	// 消息服务
	private EMChatService mEMChatService;

	private ProgressDialog mProgressDialog;

	private static final int MESSAGE_DIALOG_SHOW = 1;
	private static final int MESSAGE_DIALOG_UPDATE = 2;
	private static final int MESSAGE_DIALOG_DISMISS = 3;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == MESSAGE_DIALOG_SHOW) {
				showDialog();
			} else if(msg.what == MESSAGE_DIALOG_UPDATE) {
				int progress = (Integer) msg.obj;
				updateDialog(progress);
			} else if(msg.what == MESSAGE_DIALOG_DISMISS) {
				hideDialog();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 9) {
//			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//					.detectAll().penaltyLog().build());
//		}
		YouMeManager.Init(this);
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this,VoiceEngineService.class);
		startService(intent);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//wewview开启调试模式
			if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
				WebView.setWebContentsDebuggingEnabled(true);
			}
		}
		//umeng channel
		VirtualClassUtils.getInstance(this).initPrefs();
		UIViewFactory.getViewFactory().registViewBuilder(new BoxViewBuilder());
		showScene(SCENE_SPLASH);
		if(!App.mIsEmChatConntcted){
			showConflictDialog();
		}
		RongIMService rongIMService = (RongIMService) getSystemService(RongIMService.SERVICE_NAME);
		rongIMService.connect();
	}

	private void initImpl(){
		new TeacherLogTask().execute();
		// 获得Debug服务
		mDebugService = (DebugService) getSystemService(DebugService.SERVICE_NAME);
		File debugKey = new File(DirContext.getRootDir(), ".debugkey");
		if (debugKey.exists())
			mDebugService.enableDebug(true);

		mDebugService.getObserver().addDebugModeListener(mDebugModeListener);
		// 获得登录服务
		mLoginService = (LoginService) getSystemService(LoginService.SERVICE_NAME);
		mLoginService.getServiceObvserver().addUserStateChangeListener(
				mStateChangeListener);
		mLoginService.getServiceObvserver().addUpdatejiaocaiListener(mUpdateJiaocailistener);
		// 升级服务
		mUpdateService = (UpdateService) getSystemService(UpdateService.SERVICE_NAME);
		mUpdateService.getObserver().addVersionChangeListener(
				mVersionChangeListener);
		mUpdateService.checkVersion(true);

		mEMChatService = (EMChatService) getSystemService(EMChatService.SERVICE_NAME);
		mEMChatService.getObserver().addEMChatLoginListener(
				mEmChatLoginListener);
		mEMChatService.getObserver().addEMConnectionListener(
				mEmConnectionListener);
		mEMChatService.loginEMChat();
		mDebugPanel = findViewById(R.id.main_debug_panel);
		mDebugTxt = (TextView) findViewById(R.id.main_debug_txt);
		findViewById(R.id.main_debug_clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDebugService != null)
					mDebugService.clearMsg();
			}
		});
		mDebugTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogUtils.getDebugLogDialog(MainActivity.this, "Debug日志",
						mDebugTxt.getText().toString()).show();
			}
		});
		if (mDebugService.isDebug()) {
			mDebugModeListener.onDebugModeChange(true);
		}
		if (mLoginService != null && mLoginService.isLogin()) {
			((App)App.getAppContext()).onAppStarted();
		}

//		IntentFilter filter = new IntentFilter();
//		filter.addAction(ActionUtils.ACTION_TEACHINGMATERIAL_UPDATE);
//		MsgCenter.registerLocalReceiver(mReceiver, filter);

//		new TemplateUtils().loadOnlineTemplate();
		initPrefs();
	}

	private void initPrefs() {
	}

	/**
	 * 显示场景
	 * 
	 * @param scene
	 */
	private void showScene(int scene) {
		if (mCurrentScene == scene) return;
		switch (scene) {
			case SCENE_SPLASH:
				showSplashWindows();
				break;

			case SCENE_INTRODUCE:
				showIntroduceWindow();
				break;

			case SCENE_MAIN:
				showMainWindows();
				break;

			case SCENE_SUBJECT:
				showSubjectFragment();
				break;

			case SCENE_LOGIN:
				showLoginFragment();
				break;
			case SCENE_BOOK:
				showSelectBook();
				break;
			default:
				break;
		}
		this.mCurrentScene = scene;
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mConflictDialog != null && mConflictDialog.isShowing()){
			mConflictDialog.dismiss();
		}
		if (mVersionDialog != null && mVersionDialog.isShowing()) {
			mVersionDialog.dismiss();
		}
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		if (mDebugService != null) {
			mDebugService.getObserver().removeDebugModeListener(
					mDebugModeListener);
		}
		if (mLoginService != null) {
			mLoginService.getServiceObvserver().removeUserStateChangeListener(mStateChangeListener);
			mLoginService.getServiceObvserver().removeUpdateJiaocaiListener(mUpdateJiaocailistener);
		}
		if (mUpdateService != null) {
			mUpdateService.getObserver().removeVersionChangeListener(
					mVersionChangeListener);
		}
		if (mEMChatService != null) {
			mEMChatService.getObserver().removeEMChatLoginListener(
					mEmChatLoginListener);
			mEMChatService.getObserver().removeEMConnectionListener(
					mEmConnectionListener);
		}
//		MsgCenter.unRegisterLocalReceiver(mReceiver);
		clearPrefsOnExit(false);
	}

	/**
	 * 退出时，清空保存的数据
	 * @author weilei
	 *
	 */
	private void clearPrefsOnExit(boolean isLogOut) {
	}

//	BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(ActionUtils.ACTION_TEACHINGMATERIAL_UPDATE)) {
//				if (intent.getBooleanExtra(ConstantsUtils.IS_NEWUSER, false)) {
//					showScene(SCENE_MAIN);
//				}
//			}
//		}
//	};

	// 用户状态变化监听器
	private UserStateChangeListener mStateChangeListener = new UserStateChangeListener() {

		@Override
		public void onLogout(UserItem user) {
			if (mEMChatService != null)
				mEMChatService.logoutEMChat();
			UiThreadHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					QuestionTable table = DataBaseManager.getDataBaseManager().getTable(QuestionTable.class);
					table.clearQuestions();
					clearPrefsOnExit(true);
					//登出之后显示登录页
					showScene(SCENE_LOGIN);
				}
			}, 200);

			PreferencesController.setBoolean(ConstantsUtils.PREFS_ISNEW_TEACHER, false);
		}

		@Override
		public void onLogin(final UserItem user) {
			if (user == null) return;
			if (mEMChatService != null) mEMChatService.loginEMChat();
			LogUtil.d("MainActivity onLogin");
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					QuestionTable table = DataBaseManager.getDataBaseManager().getTable(QuestionTable.class);
					table.clearQuestions();

					if(user.gradePart.isEmpty()) {
						showScene(SCENE_SUBJECT);
					}else if (user.mBookId.equals("0")
							|| user.mEditionId.equals("0")
							|| user.mBookId.isEmpty()
							|| user.mEditionId.isEmpty()) {
						showScene(SCENE_BOOK);
					}else {
						showScene(SCENE_MAIN);
					}

					clearPrefsOnExit(false);
					if (mEMChatService != null) {
						mEMChatService.loginEMChat();
					}
				}
			});
			((App)App.getAppContext()).onAppStarted();
			PreferencesController.setBoolean(IntroduceFragment.BACK_FROM_LOGIN_REGIST, true);
		}
	};

	private CheckVersionListener mVersionChangeListener = new CheckVersionListener() {
		@Override
		public void onVersionChange(final boolean auto, OnlineVersion version) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					showNewVersionInfoDialog();
				}
			});
		}

		@Override
		public void onCheckFinish(boolean auto, int reason) {
		}
	};

	// Debug回调
	private DebugModeListener mDebugModeListener = new DebugModeListener() {

		@Override
		public void onShowDebugMsg(final String msg) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					mDebugTxt.setText(msg);
				}
			});
		}

		@Override
		public void onDebugModeChange(final boolean debugMode) {
			UiThreadHandler.post(new Runnable() {
				public void run() {
					mDebugPanel.setVisibility(debugMode ? View.VISIBLE
							: View.GONE);
				}
			});
		}
	};

	private EMConnectionListener mEmConnectionListener = new EMConnectionListener() {

		@Override
		public void onDisconnected(int error) {
			debug("emchat error: " + error);
			if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
				// 显示帐号在其他设备登陆dialog
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						showConflictDialog();
					}
				});
			}
		}

		@Override
		public void onConnected() {
			
		}
	};

	private Dialog mConflictDialog = null;

	private void showConflictDialog() {
		if(App.ALLOW_MULTI_NODE)
			return;
		
		if(mConflictDialog != null && mConflictDialog.isShowing()){
			mConflictDialog.dismiss();
		}
		mConflictDialog = DialogUtils.getMessageDialog(this, "提示", "确定", null,
				"该账号已在其他设备登录，如果不是您的操作，请重新登录修改密码。",
				new DialogUtils.OnDialogButtonClickListener() {
					@Override
					public void onItemClick(Dialog dialog, int btnId) {
						if(mLoginService != null)
							mLoginService.logout(null);
						if(dialog != null && dialog.isShowing()){
							dialog.dismiss();
						}
					}
				});

		mConflictDialog.setCancelable(false);
		mConflictDialog.getWindow().getDecorView().setOnTouchListener(null);
		mConflictDialog.show();
	}

	private static int mRetryTime = 0;
	private EMChatLoginListener mEmChatLoginListener = new EMChatLoginListener() {
		@Override
		public void onSuccess() {
			mRetryTime = 0;
		}

		@Override
		public void onError(int reason, final String message) {
			debug("onError");
			if (reason == EMError.SERVER_TIMEOUT
					|| reason == EMError.SERVER_BUSY
					|| reason == EMError.SERVER_GET_DNSLIST_FAILED
					|| reason == EMError.SERVER_NOT_REACHABLE
					|| reason == EMError.SERVER_UNKNOWN_ERROR) {
				if (mRetryTime < 3) {
					mEMChatService.loginEMChat();
					mRetryTime += 1;
				} else {
					mRetryTime = 0;
				}
			}
		}
	};

	private Dialog mVersionDialog;

	private void showNewVersionInfoDialog() {
		if (mVersionDialog != null && mVersionDialog.isShowing()) {
			mVersionDialog.dismiss();
		}
		if (mUpdateService.getLastVersion() != null) {
			mVersionDialog = DialogUtils.getMessageDialog(this, "更新", "下载安装",
					"取消", mUpdateService.getLastVersion().newFeature,
					new DialogUtils.OnDialogButtonClickListener() {
						@Override
						public void onItemClick(Dialog dialog, int btnId) {
							if (btnId == BUTTON_CONFIRM) {
								if (mUpdateService == null) {
									return;
								}
								PackageUpdateTask task = new PackageUpdateTask();
								task.setOnUpdateProgressListener(mOnUpdateProgressListener);
								File target = new File(android.os.Environment
										.getExternalStorageDirectory(),
										mUpdateService.getLastVersion().version
												+ ".apk");
								task.execute(
										mUpdateService.getLastVersion().downloadUrl,
										target.getAbsolutePath());
								
								if (OnlineVersion.FLAG_UPDATE_FORCE != mUpdateService
										.getLastVersion().type
										&& dialog.isShowing()) {
									dialog.dismiss();
								} else {
									finish();
								}
								ToastUtils.showShortToast(MainActivity.this,"正在下载新版本，请稍候！");
							} else {
								if (OnlineVersion.FLAG_UPDATE_FORCE == mUpdateService
										.getLastVersion().type) {
									finish();
								}
								if (dialog.isShowing()) {
									dialog.dismiss();
								}
							}
						}
					});
			if (OnlineVersion.FLAG_UPDATE_FORCE == mUpdateService
					.getLastVersion().type) {
				mVersionDialog.getWindow().getDecorView()
						.setOnTouchListener(null);
				mVersionDialog.setCancelable(false);
			}
			mVersionDialog.show();
		}
	}

	// --------------------

	/**
	 * 显示主框架窗口
	 */
	private void showMainWindows() {
		final MainFragment fragment = (MainFragment) Fragment.instantiate(this,
				MainFragment.class.getName(), null);
		fragment.setOnMainFragmentLoadListener(new MainFragment.OnMainFragmentLoadListener() {
			@Override
			public void onMainLoad() {
				LogUtil.d("Tag", "onMainLoad.................");
				removeAllFragment();
				Intent intent = getIntent();
				if (intent != null) {
					fragment.onNewIntent(intent);
				}
			}
		});
		fragment.setAnimationType(BaseUIFragment.AnimType.ANIM_NONE);
		showFragment(fragment);
	}

	/**
	 * 显示闪屏窗口
	 */
	private void showSplashWindows() {
		SplashFragment fragment = (SplashFragment) Fragment.instantiate(this,
				SplashFragment.class.getName(), null);
		fragment.setSplashActionListener(new SplashFragment.SplashActionListener() {
			@Override
			public void onShowMainWindows() {//处于登录转态
				if (TextUtils.isEmpty(Utils.getLoginUserItem().gradePart)) {
					showScene(SCENE_SUBJECT);
				} else if (TextUtils.isEmpty(Utils.getLoginUserItem().mEditionId) ||
						TextUtils.isEmpty(Utils.getLoginUserItem().mBookId)) {
					showScene(SCENE_BOOK);
				}else {
					showScene(SCENE_MAIN);
				}
				initImpl();
			}

			@Override
			public void onShowIntroduceWindow() {
				showScene(SCENE_INTRODUCE);
				initImpl();
			}
		});
		fragment.setAnimationType(BaseUIFragment.AnimType.ANIM_NONE);
		showFragment(fragment);
	}

	/**
	 * 显示引导页面
	 */
	private void showIntroduceWindow() {
		IntroduceFragment fragment = (IntroduceFragment) Fragment.instantiate(
				this, IntroduceFragment.class.getName(), null);
		fragment.setAnimationType(BaseUIFragment.AnimType.ANIM_NONE);
		showFragment(fragment);
		removeAllFragment();
	}

	/**
	 * 学科选择页
	 */
	private void showSubjectFragment() {
		LogUtil.d("showSubjectFragment");
		Bundle mBundle = new Bundle();
		mBundle.putInt(ConstantsUtils.SUBJECT_FROM, MainSelectSubjectFragment.FROM_MAINACTIVITY);
		MainSelectSubjectFragment fragment = MainSelectSubjectFragment.newFragment(this,
				MainSelectSubjectFragment.class, mBundle, BaseUIFragment.AnimType.ANIM_NONE);
		fragment.setOnSubjectSelectListener(new MainSelectSubjectFragment.OnSubjectSelectListener() {
			@Override
			public void onSubjectSelect() {
				showScene(SCENE_BOOK);
//				showScene(SCENE_MAIN);
			}
		});
		showFragment(fragment);
		removeAllFragment();
	}

	/**
	 * 选择学段之后教材选择
	 */
	private void showSelectBook() {
		LogUtil.d("showSelectBook");
		Bundle bundle = new Bundle();
		bundle.putBoolean(ConstantsUtils.FORCE_SETTING, true);
		bundle.putBoolean(ConstantsUtils.IS_NEWUSER, true);//没有设置教材的用户再此定义为新用户
		bundle.putString(ConstantsUtils.CLAZZ_NAME, MainActivity.class.getName());
		AssignSelectPublisherFragment fragment = AssignSelectPublisherFragment
				.newFragment(this, AssignSelectPublisherFragment.class, bundle, BaseUIFragment.AnimType.ANIM_NONE);
		showFragment(fragment);
		removeAllFragment();
	}

	/**
	 * 退出应用时显示登录
	 */
	private void showLoginFragment() {
		LogUtil.d("showLoginFragment");
		LoginFragment fragment = LoginFragment.newFragment(this, LoginFragment.class, null, BaseUIFragment.AnimType.ANIM_NONE);
		showFragment(fragment);
		removeAllFragment();
	}

	private void showDialog() {
		if(mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle("下载更新");
		mProgressDialog.setMax(100);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.bg_update_progressdialog));
		mProgressDialog.show();
	}
	
	private void updateDialog(int progress) {
		if(mProgressDialog != null) {
			mProgressDialog.setProgress(progress);
		}
	}
	
	private void hideDialog() {
		if(mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}
	
	PackageUpdateTask.OnUpdateProgressListener mOnUpdateProgressListener = new PackageUpdateTask.OnUpdateProgressListener() {
		
		@Override
		public void onUpdateStart() {
			mHandler.sendEmptyMessageDelayed(MESSAGE_DIALOG_SHOW, 100);
		}
		
		@Override
		public void onUpdateProgress(int progress) {
			Message msg = new Message();
			msg.what = MESSAGE_DIALOG_UPDATE;
			msg.obj = progress;
			mHandler.sendMessage(msg);
		}
		
		@Override
		public void onUpdateFail() {
			mHandler.sendEmptyMessageDelayed(MESSAGE_DIALOG_DISMISS, 1000);
		}
		
		@Override
		public void onUpdateCommplete() {
			mHandler.sendEmptyMessage(MESSAGE_DIALOG_DISMISS);
		}
	};

	private UpdateJiaocaiListener mUpdateJiaocailistener = new UpdateJiaocaiListener() {
		@Override
		public void onUpdateSuccess(UserItem userItem, String clazzName) {
			if (clazzName != null && clazzName.equals(MainActivity.class.getName())) {
				showScene(SCENE_MAIN);
			}
		}

		@Override
		public void onUpdateFailed(String error, String clazzName) {
		}
	};

	private class TeacherLogTask extends AsyncTask<Object, Void, BaseObject> {

		public TeacherLogTask() {
			super();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected BaseObject doInBackground(Object... params) {
			String url = OnlineServices.getTeacherLogUrl();
			BaseObject object = new DataAcquirer<BaseObject>().acquire(url,
					new BaseObject(), -1);
			return object;
		}

		@Override
		protected void onPostExecute(BaseObject result) {
			super.onPostExecute(result);
		}
	}

	@Override
	public UIFragmentHelper getUIFragmentHelper(BaseFragment fragment) {
		if(fragment instanceof BaseUIFragment) {
            return new UIFragmentHelper((BaseUIFragment<?>) fragment);
        }
		return null;
	}

	//----------------

	@Override
	public void onPreCreate() {}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return super.dispatchTouchEvent(event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (mLoginService != null && mLoginService.isLogin()) {
			if (intent != null) {
				BaseFragment fragment = getCurrentFragment();
				if (fragment instanceof MainFragment) {
					fragment.onNewIntent(intent);
				}
			}
		}
	}
}
