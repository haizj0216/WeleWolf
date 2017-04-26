package com.knowbox.teacher.modules.message.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.services.updateclass.UpdateClassService;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.profile.SettingsFragment;
import com.knowbox.teacher.modules.utils.Utils;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MessageNotifier {

	private final static String TAG = "MessageNotifier";
	Ringtone ringtone = null;

	protected final static String[] msg_eng = { "sent a message",
			"sent a picture", "sent a voice", "sent location message",
			"sent a video", "sent a file", "%1 contacts sent %2 messages" };
	protected final static String[] msg_ch = { "消息", "图片", "语音",
			"位置信息", "视频", "文件", "%1个联系人发来%2条消息" };

	protected static int notifyID = 0525; // start notification id
	protected static int foregroundNotifyID = 0555;

	protected NotificationManager notificationManager = null;

	protected HashSet<String> fromUsers = new HashSet<String>();
	protected int notificationNum = 0;

	protected Context appContext;
	protected String packageName;
	protected String[] msgs;
	protected long lastNotifiyTime;
	protected AudioManager audioManager;
	protected Vibrator vibrator;
	protected HXNotificationInfoProvider notificationInfoProvider;
	private UpdateClassService mUpdateClassService;

	public MessageNotifier() {
	}

	/**
	 * 开发者可以重载此函数 this function can be override
	 * 
	 * @param context
	 * @return
	 */
	public void init(Context context) {
		appContext = context;
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		packageName = appContext.getApplicationInfo().packageName;
		if (Locale.getDefault().getLanguage().equals("zh")) {
			msgs = msg_ch;
		} else {
			msgs = msg_eng;
		}

		audioManager = (AudioManager) appContext
				.getSystemService(Context.AUDIO_SERVICE);
		vibrator = (Vibrator) appContext
				.getSystemService(Context.VIBRATOR_SERVICE);

		mUpdateClassService = (UpdateClassService) appContext.getSystemService(UpdateClassService.SERVICE_NAME);

	}

	/**
	 * 开发者可以重载此函数 this function can be override
	 */
	public void reset() {
		resetNotificationCount();
		cancelNotificaton();
	}

	void resetNotificationCount() {
		notificationNum = 0;
		fromUsers.clear();
	}

	void cancelNotificaton() {
		if (notificationManager != null)
			notificationManager.cancel(notifyID);
	}

	/**
	 * 处理新收到的消息，然后发送通知
	 * 
	 * 开发者可以重载此函数 this function can be override
	 * 
	 * @param message
	 */
	public synchronized void onNewMsg(EMMessage message) {
		if (EMChatManager.getInstance().isSlientMessage(message)) {
			return;
		}

		// 判断app是否在后台
		if (!EasyUtils.isAppRunningForeground(appContext)) {
			sendNotification(message, false);
		} else {
			sendNotification(message, true);
		}

		viberateAndPlayTone(message);
	}

	public synchronized void onNewMesg(List<EMMessage> messages) {
		if (EMChatManager.getInstance().isSlientMessage(
				messages.get(messages.size() - 1))) {
			return;
		}
		// 判断app是否在后台
		if (!EasyUtils.isAppRunningForeground(appContext)) {
			EMLog.d(TAG, "app is running in backgroud");
			sendNotification(messages, false);
		} else {
			sendNotification(messages, true);
		}
		viberateAndPlayTone(messages.get(messages.size() - 1));
	}

	/**
	 * 发送通知栏提示 This can be override by subclass to provide customer
	 * implementation
	 * 
	 * @param messages
	 * @param isForeground
	 */
	protected void sendNotification(List<EMMessage> messages,
			boolean isForeground) {
		for (EMMessage message : messages) {
			if (!isForeground) {
				notificationNum++;
				fromUsers.add(message.getFrom());
			}
		}
		sendNotification(messages.get(messages.size() - 1), isForeground, false);
	}

	protected void sendNotification(EMMessage message, boolean isForeground) {
		sendNotification(message, isForeground, true);
	}

	/**
	 * 发送通知栏提示 This can be override by subclass to provide customer
	 * implementation
	 * 
	 * @param message
	 */
	protected void sendNotification(EMMessage message, boolean isForeground,
			boolean numIncrease) {
		if (message.getFrom().equals(Utils.getLoginUserItem().userId)) {
			return;
		}
		String username = message.getStringAttribute("userName", "");
		EMMessage.ChatType chatType = message.getChatType();
		try {
			String notifyText = username + ":";
			int count = EMChatManager.getInstance().getConversation(message.getFrom()).getUnreadMsgCount();
			if (count > 1) {
				notifyText = "[" + count + "条]" + username + ":";
			}

			switch (message.getType()) {
			case TXT:
				TextMessageBody textMessageBody = (TextMessageBody) message.getBody();
				String customType = message.getStringAttribute("type", "");//自定义类型
				if (!TextUtils.isEmpty(message.getStringAttribute("apns", ""))) {
					//"em_apns_ext" -> "{"em_push_title":"自定义信息"}"
					String ext = message.getStringAttribute("em_apns_ext");
					if (!TextUtils.isEmpty(ext)) {
						JSONObject object = new JSONObject(ext);
						notifyText = object.optString("em_push_title");
					}
				} else if (!TextUtils.isEmpty(customType)) {
					notifyText = textMessageBody.getMessage();
				} else {
					notifyText += textMessageBody.getMessage();
				}

				break;
			case IMAGE:
				notifyText += msgs[1];
				break;
			case VOICE:
				notifyText += msgs[2];
				break;
			case LOCATION:
				notifyText += msgs[3];
				break;
			case VIDEO:
				notifyText += msgs[4];
				break;
			case FILE:
				notifyText += msgs[5];
				break;
			default:
				break;
			}

			PackageManager packageManager = appContext.getPackageManager();
			String appname = (String) packageManager
					.getApplicationLabel(appContext.getApplicationInfo());

			// notification titile
			String contentTitle = appname;
			if (notificationInfoProvider != null) {
				String customNotifyText = notificationInfoProvider
						.getDisplayedText(message);
				String customCotentTitle = notificationInfoProvider
						.getTitle(message);
				if (customNotifyText != null) {
					// 设置自定义的状态栏提示内容
					notifyText = customNotifyText;
				}

				if (customCotentTitle != null) {
					// 设置自定义的通知栏标题
					contentTitle = customCotentTitle;
				}
			}

			int notifyID = 0;
			if (chatType == EMMessage.ChatType.Chat) {
				notifyID = Integer.parseInt(message.getFrom());
			} else if (chatType == EMMessage.ChatType.GroupChat) {
				ClassInfoItem item = mUpdateClassService.getClassInfoByGroupId(message.getTo());
				contentTitle = item.className;
				notifyID = Integer.parseInt(item.classId + "000");
			}

			// create and send notificaiton
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					appContext)
					.setSmallIcon(appContext.getApplicationInfo().icon)
					.setWhen(System.currentTimeMillis()).setAutoCancel(true);

			Intent msgIntent = appContext.getPackageManager()
					.getLaunchIntentForPackage(packageName);
			if (notificationInfoProvider != null) {
				// 设置自定义的notification点击跳转intent
				msgIntent = notificationInfoProvider.getLaunchIntent(message);
			}
			msgIntent.putExtra("message", message);
			PendingIntent pendingIntent = PendingIntent.getActivity(appContext,
					notifyID, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			if (numIncrease) {
				// prepare latest event info section
				if (!isForeground) {
					notificationNum++;
					fromUsers.add(message.getFrom());
				}
			}

			mBuilder.setContentTitle(contentTitle);
//			if(!isForeground)
				mBuilder.setTicker(notifyText);
			mBuilder.setContentText(notifyText);
			mBuilder.setContentIntent(pendingIntent);
			// mBuilder.setNumber(notificationNum);
			Notification notification = mBuilder.build();

//			if (isForeground) {
//				notificationManager.notify(notifyID, notification);
//				notificationManager.cancel(notifyID);
//			} else {
				notificationManager.notify(notifyID, notification);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 手机震动和声音提示
	 */
	@SuppressLint("DefaultLocale")
	public void viberateAndPlayTone(EMMessage message) {
		if (message != null) {
			List<String> slientUsers = EMChatManager.getInstance()
					.getChatOptions().getUsersOfNotificationDisabled();
			List<String> slientGroups = EMChatManager.getInstance()
					.getChatOptions().getGroupsOfNotificationDisabled();
			if (EMChatManager.getInstance().isSlientMessage(message)
					|| !PreferencesController.getBoolean(
							SettingsFragment.ISNOTIFY, true)
					|| (slientUsers != null && slientUsers.contains(message
							.getFrom()) && message.getChatType() != EMMessage.ChatType.GroupChat)
					|| (slientGroups != null && slientGroups.contains(message
							.getTo()))) {
				return;
			}
		}

		if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
			// received new messages within 2 seconds, skip play ringtone
			return;
		}

		try {
			lastNotifiyTime = System.currentTimeMillis();

			// 判断是否处于静音模式
			if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
				EMLog.e(TAG, "in slient mode now");
				return;
			}

			long[] pattern = new long[] { 0, 180, 80, 120 };
			vibrator.vibrate(pattern, -1);

			if (ringtone == null) {
				Uri notificationUri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

				ringtone = RingtoneManager.getRingtone(appContext,
						notificationUri);
				if (ringtone == null) {
					EMLog.d(TAG,
							"cant find ringtone at:"
									+ notificationUri.getPath());
					return;
				}
			}

			if (!ringtone.isPlaying()) {
				String vendor = Build.MANUFACTURER;
				ringtone.play();
				if (vendor != null && vendor.toLowerCase().contains("samsung")) {
					Thread ctlThread = new Thread() {
						public void run() {
							try {
								Thread.sleep(3000);
								if (ringtone.isPlaying()) {
									ringtone.stop();
								}
							} catch (Exception e) {
							}
						}
					};
					ctlThread.run();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置NotificationInfoProvider
	 * 
	 * @param provider
	 */
	public void setNotificationInfoProvider(HXNotificationInfoProvider provider) {
		notificationInfoProvider = provider;
	}

	public interface HXNotificationInfoProvider {
		/**
		 * 设置发送notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消息)
		 * 
		 * @param message
		 *            接收到的消息
		 * @return null为使用默认
		 */
		String getDisplayedText(EMMessage message);

		/**
		 * 设置notification持续显示的新消息提示(比如2个联系人发来了5条消息)
		 * 
		 * @param message
		 *            接收到的消息
		 * @param fromUsersNum
		 *            发送人的数量
		 * @param messageNum
		 *            消息数量
		 * @return null为使用默认
		 */
		String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

		/**
		 * 设置notification标题
		 * 
		 * @param message
		 * @return null为使用默认
		 */
		String getTitle(EMMessage message);

		/**
		 * 设置小图标
		 * 
		 * @param message
		 * @return 0使用默认图标
		 */
		int getSmallIcon(EMMessage message);

		/**
		 * 设置notification点击时的跳转intent
		 * 
		 * @param message
		 *            显示在notification上最近的一条消息
		 * @return null为使用默认
		 */
		Intent getLaunchIntent(EMMessage message);
	}

}
