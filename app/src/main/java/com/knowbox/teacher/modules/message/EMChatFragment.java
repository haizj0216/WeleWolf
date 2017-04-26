/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.message;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.VoiceRecorder;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.app.widget.AccuracGridView;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineQuestionLink;
import com.knowbox.teacher.base.database.bean.AnswerItem;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.base.database.bean.HomeworkItem;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.ActionUtils;
import com.knowbox.teacher.base.utils.DirContext;
import com.knowbox.teacher.base.utils.FileUtils;
import com.knowbox.teacher.modules.homework.imagepicker.MultiImagesPickerFragment;
import com.knowbox.teacher.modules.message.adapter.EMChatListPiecesAdapter;
import com.knowbox.teacher.modules.message.adapter.ExpressionAdapter;
import com.knowbox.teacher.modules.message.adapter.ExpressionPagerAdapter;
import com.knowbox.teacher.modules.message.services.EMChatService;
import com.knowbox.teacher.modules.message.services.EMNewMessageListener;
import com.knowbox.teacher.modules.message.utils.Constant;
import com.knowbox.teacher.modules.message.utils.SendUtils;
import com.knowbox.teacher.modules.message.utils.SmileUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.DialogUtils.OnDialogButtonClickListener;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.PasteEditText;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EMChatFragment extends BaseUIFragment<UIFragmentHelper> {

	public static final int REQUEST_CODE_CAMERA = 18;// 相机
	public static final int REQUEST_CODE_LOCAL = 19;// 本地相册
	public static final int REQUEST_CODE_SELECT_FILE = 24;// 本地文件

	public static final String MSG_REFRESH_CHAT_LIST = "com.knowbox.student.message_refreshlist";

	private ImageView mEmoticonsNormal;
	private ImageView mEmoticonsChecked;
	private LinearLayout mBtnsContainer;
	private View mTakePictureBtn;
	private View mPictureBtn;
	private View mVideoBtn;
	private View mFileBtn;
	private View mVoiceCallBtn;
	private View mVideoCallBtn;

	private ViewGroup mMoreView;
	private Button mBtnMore;
	private LinearLayout mIconsContainer;

	private ViewPager mExpressionViewpager;
	private List<String> mReslist;

	private View mKeyboardModeBtn;
	private View mButtonSetModeVoice;
	private View mButtonPressToSpeak;
	private View mButtonSend;
	private PasteEditText mEditTextContent;
	private RelativeLayout mEdittextLayout;
	private File mCameraFile;

	private VoiceRecorder mVoiceRecorder;
	private PowerManager.WakeLock mWakeLock;
	private View mRecordingContainer;
	private int[] mMicImages;
	private ImageView mMicImage;
	private TextView mRecordingHint;
	private View mBarBottom;
	// private View mMenuSecondLine;
	// private View mConatinerFile;
	// private View mInputPanel;

	private ListView mChatList;
	private EMChatListPiecesAdapter mChatListAdapter;
	private EMChatService mEmChatService;

	private String mUserId = "";
	private ChatListItem mChatItem;
	private AnswerItem mAnswerItem;
	private HomeworkItem mHomeworkItem;

//	private LinearLayout mChatQuestionLayout;
//	private WebView mChatQuestionTxt;
//	private Button mChatQuestionSend;
//	private TextView mChatQuestionExpand;
//	private boolean isChatQuestionExpand = false;
	
	private Dialog mDialog;
	
	private EMConversation conversation;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private long mPressTime;

	private Handler mPicHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				List<String> paths = (List<String>) msg.obj;
				sendPictures(paths);
			}
		}
	};

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.layout_message_chat,
				null);
		return view;
	}

	@Override
	public void onPanelClosed(View pPanel) {
		super.onPanelClosed(pPanel);
		if (mChatListAdapter != null) {
			mChatListAdapter.releaseAdapter();
		}
	}

	private void sendPictures(List<String> imagePaths) {
		if (imagePaths.size() > 0) {
			String path = imagePaths.get(0);
			SendUtils.sendPicture(mUserId, mChatItem, path);
			imagePaths.remove(0);
			Message msg = new Message();
			msg.what = 1;
			msg.obj = imagePaths;
			mPicHandler.sendMessageDelayed(msg, 1000);
			refreshList(false);
		}
	}

	@Override
	public void finish() {
		if (hideKeyboard())
			return;
		super.finish();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mChatItem = (ChatListItem) getArguments().getSerializable("chatItem");
		mAnswerItem = (AnswerItem) getArguments().getSerializable("answerItem");
		mHomeworkItem = (HomeworkItem) getArguments().getSerializable("homeworkItem");
		if (mChatItem == null) {
			finish();
			return;
		}
		mUserId = mChatItem.mUserId;
		debug("select userId: " + mUserId);
		mEmChatService = (EMChatService) BaseApp.getAppContext()
				.getSystemService(EMChatService.SERVICE_NAME);
		mEmChatService.getObserver().addEMNewMessageListener(
				mNewMessageListener);
		mEmChatService.setCurrentUserId(mUserId);
		try {
			conversation = EMChatManager.getInstance().getConversation(mUserId);
		} catch (Exception e) {
			e.printStackTrace();
			finish();
			return;
		}
		mWakeLock = ((PowerManager) getActivity().getSystemService(
				Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "chat");
		updateTitle();
		mBarBottom = view.findViewById(R.id.bar_bottom);//最下面的一栏
		if (mChatItem.isPublic() || mChatItem.isNotice() || mChatItem.isSelfTrain()) {
			mBarBottom.setVisibility(View.GONE);
		} else {
			mBarBottom.setVisibility(View.VISIBLE);
		}
		mEditTextContent = (PasteEditText) view
				.findViewById(R.id.et_sendmessage);
		mEdittextLayout = (RelativeLayout) view
				.findViewById(R.id.edittext_layout);//edittext+选择头像
		mButtonSend = view.findViewById(R.id.btn_send);//发送
		mButtonSend.setOnClickListener(mOnClickListener);
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mEdittextLayout
							.setBackgroundResource(R.drawable.chat_input_bar_bg_active);
				} else {
					mEdittextLayout
							.setBackgroundResource(R.drawable.chat_input_bar_bg_normal);
				}
			}
		});
		mEdittextLayout
				.setBackgroundResource(R.drawable.chat_input_bar_bg_normal);
		mEditTextContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEdittextLayout
						.setBackgroundResource(R.drawable.chat_input_bar_bg_active);
				// mMoreView.setVisibility(View.GONE);
				showMore(false);
				mEmoticonsNormal.setVisibility(View.VISIBLE);//灰色表情可见
				mEmoticonsChecked.setVisibility(View.INVISIBLE);//绿色表情不可见
				mIconsContainer.setVisibility(View.GONE);//图标容器
				mBtnsContainer.setVisibility(View.GONE);//编辑按钮的容器
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					mBtnMore.setVisibility(View.GONE);
					mButtonSend.setVisibility(View.VISIBLE);
				} else {
					mBtnMore.setVisibility(View.VISIBLE);
					mButtonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mExpressionViewpager = (ViewPager) view.findViewById(R.id.vPager);
		// 表情list
		mReslist = getExpressionRes(35);
		// 初始化表情ViewPager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		mExpressionViewpager.setAdapter(new ExpressionPagerAdapter(views));

		mEmoticonsNormal = (ImageView) view
				.findViewById(R.id.iv_emoticons_normal);
		mEmoticonsChecked = (ImageView) view
				.findViewById(R.id.iv_emoticons_checked);
		mEmoticonsNormal.setVisibility(View.VISIBLE);
		mEmoticonsChecked.setVisibility(View.INVISIBLE);
		mEmoticonsNormal.setOnClickListener(mOnClickListener);
		mEmoticonsChecked.setOnClickListener(mOnClickListener);

		mBtnsContainer = (LinearLayout) view
				.findViewById(R.id.ll_btn_container);
		mIconsContainer = (LinearLayout) view
				.findViewById(R.id.ll_face_container);
		mMoreView = (ViewGroup) view.findViewById(R.id.more);

		// LayoutTransition transition = new LayoutTransition();
		// ValueAnimator animator = new ValueAnimator();
		// animator.setValues(PropertyValuesHolder.ofFloat("y", 0));
		// transition.setAnimator(LayoutTransition.APPEARING, animator);
		// mMoreView.setLayoutTransition(transition);

		// mInputPanel = view.findViewById(R.id.rl_bottom);
		// TODO
		mBtnMore = (Button) view.findViewById(R.id.btn_more);
		mBtnMore.setOnClickListener(mOnClickListener);
		loadmorePB = (ProgressBar) view.findViewById(R.id.pb_load_more);
		
		mTakePictureBtn = view.findViewById(R.id.btn_take_picture);//拍照
		mPictureBtn = view.findViewById(R.id.btn_picture);//图片
		mVideoBtn = view.findViewById(R.id.btn_video);//视频
		mFileBtn = view.findViewById(R.id.btn_file);
		mVoiceCallBtn = view.findViewById(R.id.btn_voice_call);
		mVideoCallBtn = view.findViewById(R.id.btn_video_call);

		mTakePictureBtn.setOnClickListener(mOnClickListener);
		mPictureBtn.setOnClickListener(mOnClickListener);
		mVideoBtn.setOnClickListener(mOnClickListener);
		mFileBtn.setOnClickListener(mOnClickListener);
		mVoiceCallBtn.setOnClickListener(mOnClickListener);
		mVideoCallBtn.setOnClickListener(mOnClickListener);

		mKeyboardModeBtn = view.findViewById(R.id.btn_set_mode_keyboard);//键盘
		mButtonSetModeVoice = view.findViewById(R.id.btn_set_mode_voice);//话筒
		mKeyboardModeBtn.setOnClickListener(mOnClickListener);
		mButtonSetModeVoice.setOnClickListener(mOnClickListener);

		mButtonPressToSpeak = view.findViewById(R.id.btn_press_to_speak);//按住说话
		mButtonPressToSpeak.setOnTouchListener(new PressToSpeakListener());

		mVoiceRecorder = new VoiceRecorder(mMICImageHandler);
		mRecordingContainer = view.findViewById(R.id.recording_container);
		mMicImage = (ImageView) view.findViewById(R.id.mic_image);
		mRecordingHint = (TextView) view.findViewById(R.id.recording_hint);

		mMicImages = new int[] {
				R.drawable.record_animate_01,
				R.drawable.record_animate_02,
				R.drawable.record_animate_03,
				R.drawable.record_animate_04,
				R.drawable.record_animate_05,
				R.drawable.record_animate_06,
				R.drawable.record_animate_07,
				R.drawable.record_animate_08,
				R.drawable.record_animate_09,
				R.drawable.record_animate_10,
				R.drawable.record_animate_11,
				R.drawable.record_animate_12,
				R.drawable.record_animate_13,
				R.drawable.record_animate_14,
				R.drawable.record_animate_15,
				R.drawable.record_animate_16,
				R.drawable.record_animate_17,
				R.drawable.record_animate_18,
				R.drawable.record_animate_19,
				R.drawable.record_animate_20 };

		mChatList = (ListView) view.findViewById(R.id.list);
		mChatListAdapter = new EMChatListPiecesAdapter(getActivity(),
				mChatItem, this);
		mChatList.setAdapter(mChatListAdapter);
		mChatList.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				// mMoreView.setVisibility(View.GONE);
				showMore(false);
				mEmoticonsNormal.setVisibility(View.VISIBLE);
				mEmoticonsChecked.setVisibility(View.INVISIBLE);
				mIconsContainer.setVisibility(View.GONE);
				mBtnsContainer.setVisibility(View.GONE);
				return false;
			}
		});
		mChatList.setOnScrollListener(new ListScrollListener());

		refreshList(true);
		IntentFilter filter = new IntentFilter();
		filter.addAction(MSG_REFRESH_CHAT_LIST);
		MsgCenter.registerLocalReceiver(mLocalReceiver, filter);

		// mMenuSecondLine = view.findViewById(R.id.container_menu_secondline);
		// mConatinerFile = view.findViewById(R.id.container_file);
		// mMenuSecondLine.setVisibility(mChatItem.isGroup() ? View.GONE:
		// View.VISIBLE);
		// mConatinerFile.setVisibility(mChatItem.isGroup() ? View.GONE:
		// View.VISIBLE);
//		initChatQuestion(view);
	}
	
	private void updateTitle() {
		if(mChatItem.isDisabled) {
			getUIFragmentHelper().getTitleBar().setTitle(R.drawable.bt_message_disabled, mChatItem.mUserName, null);
		} else {
			getUIFragmentHelper().getTitleBar().setTitle(-1,mChatItem.mUserName, null);//id<0时,免打扰图标gone掉
		}
	}

	/**
	 * 初始化聊题
	 * 
	 * @author weilei
	 *
//	 * @param view
	 */
//	private void initChatQuestion(View view) {
////		mChatQuestionLayout = (LinearLayout) view
////				.findViewById(R.id.chat_question_layout);
//		if (mAnswerItem != null) {
//			mChatQuestionExpand = (TextView) mChatQuestionLayout
//					.findViewById(R.id.chat_layout_expand);
//			mChatQuestionSend = (Button) mChatQuestionLayout
//					.findViewById(R.id.chat_question_send);
//			mChatQuestionTxt = (WebView) mChatQuestionLayout
//					.findViewById(R.id.chat_question_txt);
//			mChatQuestionLayout.setVisibility(View.VISIBLE);
//			// StringUtils.setTextHtml(mChatQuestionTxt, mAnswerItem.question);
//			StringUtils.setStemHtml(mChatQuestionTxt, mAnswerItem.question);
//			mChatQuestionSend.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					loadDefaultData(2);
//				}
//			});
//			mChatQuestionExpand.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (isChatQuestionExpand) {
//						mChatQuestionTxt.setVisibility(View.GONE);
//						mChatQuestionExpand
//								.setCompoundDrawablesWithIntrinsicBounds(
//										null,
//										null,
//										getResources()
//												.getDrawable(
//														R.drawable.bt_question_link_array_down),
//										null);
//					} else {
//						mChatQuestionExpand
//								.setCompoundDrawablesWithIntrinsicBounds(
//										null,
//										null,
//										getResources()
//												.getDrawable(
//														R.drawable.bt_question_link_array_up),
//										null);
//						mChatQuestionTxt.setVisibility(View.VISIBLE);
//					}
//					isChatQuestionExpand = !isChatQuestionExpand;
//				}
//			});
//
//		} else {
//			mChatQuestionLayout.setVisibility(View.GONE);
//		}
//	}

	@Override
	public void onPreAction(int action, int pageNo) {
		// TODO Auto-generated method stub
		super.onPreAction(action, pageNo);
		getUIFragmentHelper().getLoadingView().showLoading();
	}

	@Override
	public BaseObject onProcess(int action, int pageNo, Object... params) {
		// TODO Auto-generated method stub
		String url = OnlineServices.getMessageChatQuestionUrl(Utils.getToken(),
				mAnswerItem.answerId);
		final OnlineQuestionLink onlineAnswer = new DataAcquirer<OnlineQuestionLink>()
				.acquire(url, new OnlineQuestionLink(), 0);
		return onlineAnswer;
	}

	@Override
	public void onGet(int action, int pageNo, BaseObject result) {
		// TODO Auto-generated method stub
		super.onGet(action, pageNo, result);
		showContent();
		if (result.isAvailable()) {
			sendQuestionLink(Utils.getLoginUserItem(),
					(OnlineQuestionLink) result);
		}
	}

	@Override
	public void onFail(int action, int pageNo, BaseObject result) {
		// TODO Auto-generated method stub
		super.onFail(action, pageNo, result);
		showContent();
	}

	private void sendQuestionLink(UserItem user, OnlineQuestionLink onlineAnswer) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		String no = (mAnswerItem.mQuestionIndex + 1) + "/" +
				mAnswerItem.mQuestionCount;
		TextMessageBody body = new TextMessageBody("["+ mHomeworkItem.mHomeworkTitle +" "
				+ DateUtil.getMonthDayString(mHomeworkItem.mCreateTs)+"] "
				+ no +"题 "
				+ SubjectUtils.getNameByQuestionType(onlineAnswer.mQuestionType));
		message.addBody(body);
		// 设置用户名、头像
		if (user != null) {
			message.setAttribute("userName", user.userName);
			if(!TextUtils.isEmpty(user.headPhoto)){
				message.setAttribute("userPhoto", user.headPhoto);
			}
		}
		
		message.setAttribute("toUserName", mChatItem.mUserName);
		if(!TextUtils.isEmpty(mChatItem.mHeadPhoto)){
			message.setAttribute("toUserPhoto", mChatItem.mHeadPhoto);
		}
		message.setAttribute("type", Constant.MESSAGE_TYPE_QUESTION_LINK);
		message.setAttribute("answerID", onlineAnswer.mAnswerId);
		message.setAttribute("homeworkID", onlineAnswer.mHomeworkId);
		message.setAttribute("classID", onlineAnswer.mClassId);
		message.setAttribute("questionType", onlineAnswer.mQuestionType);
		message.setAttribute("subjectCode", onlineAnswer.mSubjectCode);
		message.setAttribute("questionNo", onlineAnswer.mOrderNum);
		message.setAttribute("startTime", String.valueOf(mHomeworkItem.mCreateTs));
		message.setAttribute("homeworkStartTime", String.valueOf(mHomeworkItem.mCreateTs));
		message.setAttribute("endTime", String.valueOf(mHomeworkItem.mDeadLineTs));
		message.setAttribute("questionID", mAnswerItem.mQuestionId);
		message.setAttribute("questionIndex", String.valueOf(mAnswerItem.mQuestionIndex));
		message.setAttribute("questionCount", String.valueOf(mAnswerItem.mQuestionCount));
		if (!TextUtils.isEmpty(mHomeworkItem.mHomeworkTitle))
			message.setAttribute("homeworkTitle", mHomeworkItem.mHomeworkTitle);
		// 设置要发给谁,用户username或者群聊groupid
		message.setReceipt(mUserId);
		// 把messgage加到conversation中
		conversation.addMessage(message);
		MsgCenter.sendLocalBroadcast(new Intent(
				EMChatFragment.MSG_REFRESH_CHAT_LIST));
	};

	private static final int MENU_CLEAR = 1;
	private static final int MENU_DISTURB = 2;

	@Override
	public List<MenuItem> getMenuItems() {
		List<MenuItem> items = new ArrayList<MenuItem>();
		String tet = mChatItem != null && mChatItem.isDisabled ? "关闭免打扰"
				: "开启免打扰";
		int id = mChatItem != null && mChatItem.isDisabled ? R.drawable.bt_menu_close_disabled
				: R.drawable.bt_menu_open_disabled;
		items.add(new MenuItem(MENU_DISTURB, id, tet, ""));
		items.add(new MenuItem(MENU_CLEAR, R.drawable.bt_menu_delete_info, "清除记录", ""));
		return items;
	}

	@Override
	public void onMenuItemClick(MenuItem item) {
		super.onMenuItemClick(item);
		if (item != null) {
			switch (item.type) {
			case MENU_CLEAR: {
				showDeleteDialog();
				break;
			}
			case MENU_DISTURB: {
				setDisabled();
			}
			default:
				break;
			}
		}
	}
	
	/**
	 * 显示删除对话框
	 * @author weilei
	 *
	 */
	private void showDeleteDialog() {
		if(mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		mDialog = DialogUtils.getMessageDialog(getActivity(), "清空消息", "确定", "取消",
				"确定要清空消息记录？", new OnDialogButtonClickListener() {
			
			@Override
			public void onItemClick(Dialog dialog, int btnId) {
				// TODO Auto-generated method stub
				if(btnId == BUTTON_CONFIRM) {
					EMChatManager.getInstance().clearConversation(mUserId);
					mEmChatService.getObserver().notifyClearMessage();
				}
				mDialog.dismiss();
			}
		});
		if(mDialog == null) {
			return;
		}
		mDialog.show();
	}

	/**
	 * 设置免打扰
	 * 
	 * @author weilei
	 *
	 */
	private void setDisabled() {
		EMChatOptions options = EMChatManager.getInstance()
				.getChatOptions();
		if (mChatItem.isGroup()) {
			List<String> mGroup = options.getGroupsOfNotificationDisabled();
			if (mGroup == null) {
				mGroup = new ArrayList<String>();
			}
			if (mChatItem.isDisabled) {
				mGroup.remove(mChatItem.mUserId);
			} else {
				mGroup.add(mChatItem.mUserId);
			}
			mChatItem.isDisabled = !mChatItem.isDisabled;
			options.setGroupsOfNotificationDisabled(mGroup);
		} else {
			List<String> mUser = options.getUsersOfNotificationDisabled();
			if (mUser == null) {
				mUser = new ArrayList<String>();
			}
			if (mChatItem.isDisabled) {
				mUser.remove(mChatItem.mUserId);
			} else {
				mUser.add(mChatItem.mUserId);
			}
			mChatItem.isDisabled = !mChatItem.isDisabled;
			options.setUsersOfNotificationDisabled(mUser);
		}
		updateTitle();
	}

	private BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (MSG_REFRESH_CHAT_LIST.equals(action)) {
				refreshList(false);
			}
		}

	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			hideKeyboard();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroyViewImpl() {
		super.onDestroyViewImpl();
		MsgCenter.unRegisterLocalReceiver(mLocalReceiver);
		mEmChatService.setCurrentUserId(null);
		mEmChatService.getObserver().removeEMNewMessageListener(
				mNewMessageListener);
	}

	private Handler mMICImageHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			// 切换msg切换图片
			mMicImage.setImageResource(mMicImages[msg.what]);
		}
	};

	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (mChatListAdapter != null) {
				mChatListAdapter.refreshList();
			}
		}
	}

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListener implements OnTouchListener {

		@SuppressLint({ "Wakelock", "ClickableViewAccessibility" })
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!FileUtils.isSdcardAvailable()) {
					ToastUtils.showShortToast(getActivity(), "发送语音需要sdcard支持！");
					return false;
				}
				if (System.currentTimeMillis() - mPressTime < 2000) {
					ToastUtils.showShortToast(getActivity(), "操作过于频繁");
					return false;
				}
				try {
					v.setPressed(true);
					mWakeLock.acquire();
					// TODO
					/*
					 * if (VoicePlayClickListener.isPlaying)
					 * VoicePlayClickListener
					 * .currentPlayListener.stopPlayVoice();
					 */
					mRecordingContainer.setVisibility(View.VISIBLE);
					mRecordingHint
							.setText(getString(R.string.move_up_to_cancel));
					mRecordingHint.setBackgroundColor(Color.TRANSPARENT);
					mVoiceRecorder.startRecording(null, mUserId,
							BaseApp.getAppContext());
				} catch (Exception e) {
					v.setPressed(false);
					if (mWakeLock.isHeld())
						mWakeLock.release();
					if (mVoiceRecorder != null)
						mVoiceRecorder.discardRecording();
					mRecordingContainer.setVisibility(View.INVISIBLE);
					ToastUtils.showShortToast(getActivity(), R.string.recoding_fail);
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					mRecordingHint
							.setText(getString(R.string.release_to_cancel));
					mRecordingHint
							.setBackgroundResource(R.drawable.chat_recording_text_hint_bg);
				} else {
					mRecordingHint
							.setText(getString(R.string.move_up_to_cancel));
					mRecordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				v.setPressed(false);
				mPressTime = System.currentTimeMillis();
				mRecordingContainer.setVisibility(View.INVISIBLE);
				if (mWakeLock.isHeld())
					mWakeLock.release();
				if (event.getY() < 0) {
					mVoiceRecorder.discardRecording();
				} else {
					try {
						final UserItem userItem = Utils.getLoginUserItem();
						if (userItem == null)
							return true;
						UiThreadHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								int length = mVoiceRecorder.stopRecoding();
								if (length > 0) {
									sendVoice(mVoiceRecorder.getVoiceFilePath(),
											mVoiceRecorder
													.getVoiceFileName(userItem.userId),
											Integer.toString(length), false);
								} else if (length == EMError.INVALID_FILE) {
									ToastUtils.showShortToast(getActivity(), "无录音权限");
								} else {
									ToastUtils.showShortToast(getActivity(), "录音时间太短");
								}
							}
						}, 300);
					} catch (Exception e) {
						ToastUtils.showShortToast(getActivity(), "发送失败，请检测服务器是否连接");
					}

				}
				return true;
			default:
				mRecordingContainer.setVisibility(View.INVISIBLE);
				if (mVoiceRecorder != null)
					mVoiceRecorder.discardRecording();
				return false;
			}
		}
	}

	private void sendVoice(String filePath, String fileName, String length,
			boolean isResend) {
		if (!(new File(filePath).exists()) || mChatItem == null) {
			return;
		}
		try {
			SendUtils.sendVoice(mUserId, mChatItem, filePath, length);
			refreshList(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showMore(boolean visible) {
		if (visible) {
			ObjectAnimator animator = ObjectAnimator.ofFloat(mMoreView,
					"alpha", 0.5f, 1);
			animator.setDuration(100);
			animator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
					mMoreView.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}
			});
			animator.start();
		} else {
			ObjectAnimator animator = ObjectAnimator.ofFloat(mMoreView,
					"alpha", 1, 0.5f);
			animator.setDuration(100);
			animator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mMoreView.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					mMoreView.setVisibility(View.GONE);
				}
			});
			animator.start();
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			UserItem userItem = Utils.getLoginUserItem();
			if (userItem == null)
				return;
			int id = v.getId();
			switch (id) {
			case R.id.iv_emoticons_normal:// 点击显示表情框
			{
				if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
					ActionUtils.notifyVirtualTip();
					return;
				}
				mEmoticonsNormal.setVisibility(View.INVISIBLE);
				mEmoticonsChecked.setVisibility(View.VISIBLE);

				mBtnsContainer.setVisibility(View.GONE);
				mIconsContainer.setVisibility(View.VISIBLE);
				// mMoreView.setVisibility(View.VISIBLE);
				showMore(true);
				hideKeyboard();
				break;
			}
			case R.id.iv_emoticons_checked:// 点击隐藏表情框
			{
				mEmoticonsNormal.setVisibility(View.VISIBLE);
				mEmoticonsChecked.setVisibility(View.INVISIBLE);

				mBtnsContainer.setVisibility(View.VISIBLE);
				mIconsContainer.setVisibility(View.GONE);
				// mMoreView.setVisibility(View.GONE);
				showMore(false);
				break;
			}
			case R.id.btn_more: {
				if (mMoreView.getVisibility() == View.GONE) {
					hideKeyboard();
					// mMoreView.setVisibility(View.VISIBLE);
					showMore(true);
					mBtnsContainer.setVisibility(View.VISIBLE);
					mIconsContainer.setVisibility(View.GONE);
				} else {
					if (mIconsContainer.getVisibility() == View.VISIBLE) {
						mIconsContainer.setVisibility(View.GONE);
						mBtnsContainer.setVisibility(View.VISIBLE);
						mEmoticonsNormal.setVisibility(View.VISIBLE);
						mEmoticonsChecked.setVisibility(View.INVISIBLE);
					} else {
						// mMoreView.setVisibility(View.GONE);
						showMore(false);
					}

				}
				break;
			}
			case R.id.btn_take_picture: {
				if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
					ActionUtils.notifyVirtualTip();
					return;
				}
				if (!FileUtils.isSdcardAvailable()) {
					ToastUtils.showShortToast(getActivity(), "SD卡不存在，不能拍照");
					return;
				}
				mCameraFile = new File(DirContext.getImageCacheDir(),
						userItem.userId + "_" + System.currentTimeMillis()
								+ ".jpg");
				mCameraFile.getParentFile().mkdirs();

				startActivityForResult(new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile)),
						REQUEST_CODE_CAMERA);
				break;
			}
			case R.id.btn_picture: {
				if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
					ActionUtils.notifyVirtualTip();
					return;
				}
//				Intent intent;
//				if (Build.VERSION.SDK_INT < 19) {
//					intent = new Intent(Intent.ACTION_GET_CONTENT);
//					intent.setType("image/*");
//				} else {
//					intent = new Intent(
//							Intent.ACTION_PICK,
//							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//				}
//				startActivityForResult(intent, REQUEST_CODE_LOCAL);

				final Bundle imageBundle = new Bundle();
				MultiImagesPickerFragment fragment = (MultiImagesPickerFragment) Fragment.instantiate(getActivity(),
						MultiImagesPickerFragment.class.getName(), imageBundle);
				fragment.setSelectListener(new MultiImagesPickerFragment.OnImageSelectedListener() {
					@Override
					public void onImageSelect(List<String> imagePaths) {
						if (imagePaths != null && imagePaths.size() > 0) {
							sendPictures(imagePaths);
						}
					}
				});
				showFragment(fragment);
				break;
			}
			case R.id.btn_file: {
				Intent intent = null;
				if (Build.VERSION.SDK_INT < 19) {
					intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("*/*");
					intent.addCategory(Intent.CATEGORY_OPENABLE);
				} else {
					intent = new Intent(
							Intent.ACTION_PICK,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				}
				startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
				break;
			}// 以下优先级较低，后续处理
			case R.id.btn_video: {
				if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
					ActionUtils.notifyVirtualTip();
					return;
				}
				if (!FileUtils.isSdcardAvailable()) {
					ToastUtils.showShortToast(getActivity(), "SD卡不存在，不能拍照");
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("chatItem", mChatItem);
				ImageGridFragment fragment = (ImageGridFragment) Fragment
						.instantiate(getActivity(),
								ImageGridFragment.class.getName(), bundle);
				showFragment(fragment);
				// Intent intent = new Intent(getActivity(),
				// ImageGridActivity.class);
				// startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
				break;
			}
			case R.id.btn_voice_call: {
				if (!EMChatManager.getInstance().isConnected())
				ToastUtils.showShortToast(getActivity(),"尚未连接至服务器，请稍后重试");
				else
					startActivity(new Intent(getActivity(),
							VoiceCallActivity.class).putExtra("username",
							userItem.userName).putExtra("isComingCall", false));
				break;
			}
			case R.id.btn_video_call: {
				if (!EMChatManager.getInstance().isConnected())
				ToastUtils.showShortToast(getActivity(),"尚未连接至服务器，请稍后重试");
				else
					startActivity(new Intent(getActivity(),
							VideoCallActivity.class).putExtra("username",
							userItem.userName).putExtra("isComingCall", false));
				break;
			}
			case R.id.btn_set_mode_voice: {
				if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
					ActionUtils.notifyVirtualTip();
					return;
				}
				hideKeyboard();
				mEdittextLayout.setVisibility(View.GONE);
				// mMoreView.setVisibility(View.GONE);
				showMore(false);
				mButtonSetModeVoice.setVisibility(View.GONE);
				mKeyboardModeBtn.setVisibility(View.VISIBLE);
				mButtonSend.setVisibility(View.GONE);
				mBtnMore.setVisibility(View.VISIBLE);
				mButtonPressToSpeak.setVisibility(View.VISIBLE);
				mEmoticonsNormal.setVisibility(View.VISIBLE);
				mEmoticonsChecked.setVisibility(View.INVISIBLE);
				mBtnsContainer.setVisibility(View.VISIBLE);
				mIconsContainer.setVisibility(View.GONE);
				break;
			}
			case R.id.btn_set_mode_keyboard: {
				mEdittextLayout.setVisibility(View.VISIBLE);
				// mMoreView.setVisibility(View.GONE);
				showMore(false);
				mKeyboardModeBtn.setVisibility(View.GONE);
				mButtonSetModeVoice.setVisibility(View.VISIBLE);
				mEditTextContent.requestFocus();
				mButtonPressToSpeak.setVisibility(View.GONE);
				if (TextUtils.isEmpty(mEditTextContent.getText())) {
					mBtnMore.setVisibility(View.VISIBLE);
					mButtonSend.setVisibility(View.GONE);
				} else {
					mBtnMore.setVisibility(View.GONE);
					mButtonSend.setVisibility(View.VISIBLE);
				}
				break;
			}
			case R.id.btn_send: {
				if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
					ActionUtils.notifyVirtualTip();
					return;
				}
				if (mChatItem == null)
					return;

				String s = mEditTextContent.getText().toString();
				SendUtils.sendText(mUserId, mChatItem, s);
				refreshList(true);
				break;
			}
			default:
				break;
			}
		}
	};

	/**
	 * 刷新列表
	 * 
	 * @param isClearTxt
	 */
	private void refreshList(boolean isClearTxt) {
		mChatListAdapter.refreshList();
		int itemCount = mChatListAdapter.getCount();
		if (itemCount > 0) {
			mChatList.setSelection(itemCount - 1);
		}
		if (isClearTxt)
			mEditTextContent.setText("");
//		for (int i = 0; i < conversation.getAllMessages().size(); i++) {
//			debug(conversation.getAllMessages().get(i).getBody().toString());
//		}
	}

	private EMNewMessageListener mNewMessageListener = new EMNewMessageListener() {
		@Override
		public void onNewMessage(EMMessage message) {
			if (message != null) {
				// 如果是群聊消息，获取到group id
				String fromUserId = message.getFrom();
				if (message.getChatType() == ChatType.GroupChat) {
					fromUserId = message.getTo();
				}
				if (!fromUserId.equals(mUserId)) {
					return;
				}
			}

			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					refreshList(false);
				}
			});
		}

		@Override
		public void onClearMessage() {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					refreshList(false);
				}
			});
		}

		@Override
		public void onApnsMessage(EMMessage message) {

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		debug("onActivityResult, requestCode : " + requestCode
				+ ", resultCode: " + resultCode);
		if (mChatItem == null)
			return;

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_CAMERA: {
				if (mCameraFile != null && mCameraFile.exists()) {
					SendUtils.sendPicture(mUserId, mChatItem,
							mCameraFile.getAbsolutePath());
					refreshList(false);
				}
				break;
			}
			case REQUEST_CODE_LOCAL: {
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						try {
							SendUtils.sendPicByUri(mUserId, mChatItem,
									selectedImage);
							refreshList(false);
						} catch (Exception e) {
						}
					}
				}
				break;
			}
			case REQUEST_CODE_SELECT_FILE: {
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						SendUtils.sendFile(mUserId, mChatItem, uri);
						refreshList(false);
					}
				}
				break;
			}

			default:
				break;
			}
		}
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;
			reslist.add(filename);
		}
		return reslist;
	}

	private View getGridChildView(int i) {
		View view = View.inflate(getActivity(),
				R.layout.layout_message_expression_gridview, null);
		AccuracGridView gv = (AccuracGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = mReslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(mReslist.subList(20, mReslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(
				getActivity(), 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (mKeyboardModeBtn.getVisibility() != View.VISIBLE) {
						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class<?> clz = Class
									.forName("com.knowbox.teacher.modules.message.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(
									getActivity(), (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {
								int selectionStart = mEditTextContent
										.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText()
											.toString();
									String tempStr = body.substring(0,
											selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i,
												selectionStart);
										if (SmileUtils.containsKey(cs
												.toString()))
											mEditTextContent.getEditableText()
													.delete(i, selectionStart);
										else
											mEditTextContent.getEditableText()
													.delete(selectionStart - 1,
															selectionStart);
									} else {
										mEditTextContent.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
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
	
	/**
	 * listview滑动监听listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData && conversation.getAllMessages().size() != 0) {
					isloading = true;
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多					
					List<EMMessage> messages;
					EMMessage firstMsg = conversation.getAllMessages().get(0);
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (mChatItem.isGroup())
							messages = conversation.loadMoreGroupMsgFromDB(
									firstMsg.getMsgId(), pagesize);
						else
							messages = conversation.loadMoreMsgFromDB(
									firstMsg.getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					if (messages.size() != 0) {
						// 刷新ui
						if (messages.size() > 0) {
							refreshListSeekTo(messages.size() - 1);
						}
						
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}
	}
	
	private void refreshListSeekTo(int position){
		mChatListAdapter.refreshList();
		mChatList.setSelection(position);
	}
}
