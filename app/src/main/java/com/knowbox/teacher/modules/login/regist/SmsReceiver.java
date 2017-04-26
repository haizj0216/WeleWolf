package com.knowbox.teacher.modules.login.regist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.widget.EditText;

import com.hyena.framework.utils.BaseApp;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @name 拦截并提取短信验证码
 * @author Fanjb
 * @date 2015年4月17日
 */
public class SmsReceiver extends BroadcastReceiver {

	public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private EditText mSmsCodeEdit;

	public void setEditText(EditText editText) {
		this.mSmsCodeEdit = editText;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null && intent.getAction().equals(ACTION_SMS_RECEIVED)) {
			try {
				SmsMessage[] messages = getMessagesFromIntent(intent);

				for (int i = messages.length - 1; i >= 0 ; i--) {
					SmsMessage message = messages[i];
					if (message != null) {
						String messageBody = message.getDisplayMessageBody();
						if (!TextUtils.isEmpty(messageBody)
								&& (messageBody.contains("知识印象")
								|| messageBody.contains("单词部落"))) {
							String number = getNumbers(messageBody);
							if (mSmsCodeEdit != null && !TextUtils.isEmpty(number)) {
								mSmsCodeEdit.setText("" + number);
							}
							Map<String, String> umengCount = new HashMap<String, String>();
							umengCount.put(UmengConstant.KEY_MESSAGE, "receiver");
							MobclickAgent.onEvent(BaseApp.getAppContext(),
									UmengConstant.EVENT_PHONE_SMS, umengCount);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取短信内容
	 * 
	 * @param intent
	 * @return
	 */
	private SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
		byte[][] pduObjs = new byte[messages.length][];
		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}
		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return msgs;
	}

	/**
	 * 提取数字
	 * 
	 * @param content
	 * @return
	 */
	private String getNumbers(String content) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			return matcher.group(0);
		}
		return "";
	}
}
