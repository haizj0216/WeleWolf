/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.message.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;
import com.hyena.framework.app.adapter.SingleViewAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.modules.message.utils.MessagePushUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.UmengConstant;

import java.util.Date;

/**
 *分享作业列表适配器
 * @author LiuYu
 */
public class EMChatShareHomeworkAdapter extends SingleViewAdapter<EMMessage> {

	private ChatListItem mChatListItem;
	private EMConversation mEmConversation;
	private BaseUIFragment mFragment;
	private int mIndex;
	public EMChatShareHomeworkAdapter(Context context, BaseAdapter parent, int type,
									  ChatListItem chatItem, BaseUIFragment fragment,
									  EMMessage message, int index) {
		super(context, parent, type, message);
		this.mChatListItem = chatItem;
		this.mFragment = fragment;
		mIndex = index;
		mEmConversation = EMChatManager.getInstance().getConversation(chatItem.mUserId);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {
			convertView = View.inflate(mContext, R.layout.layout_chatlist_item_share, null);
			holder = new ViewHolder();
			holder.mBackground = convertView.findViewById(R.id.chatlist_item_bg);
			holder.mShareTitle = (TextView) convertView.findViewById(R.id.share_homework_title);
			holder.mFromTeacher = (TextView) convertView.findViewById(R.id.share_homework_teacher_from);
			holder.mSubject = (TextView) convertView.findViewById(R.id.share_homework_subject);
			holder.mContainerView = convertView.findViewById(R.id.chatlist_item_container);
			holder.mFromTeacher.setMaxWidth(UIUtils.dip2px(140));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final EMMessage item = getItem(position);
		String teacherName = item.getStringAttribute("teacherName", "");
		holder.mFromTeacher.setText(teacherName);
		String homeworkTitle = item.getStringAttribute("homeworkTitle", "");
		holder.mShareTitle.setText(homeworkTitle);
		int subjectCode = -1;
		try {
			subjectCode = Integer.parseInt(item.getStringAttribute("subject", "-1"));
			holder.mSubject.setText(SubjectUtils.getNameByCode(subjectCode));
		} catch (Exception e) {
			holder.mSubject.setText("未知科目");
		}
		setItemBackGroud(subjectCode, holder.mBackground);
		holder.mContainerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showShareHomeworkDetail(item);
			}
		});

		//消息时间
		TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
		if (mIndex == 0) {
			timestamp.setText(DateUtil.getMessageTimeString(new Date(item.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			if (mEmConversation != null && 
					DateUtils.isCloseEnough(item.getMsgTime(),
							mEmConversation.getMessage(mIndex - 1).getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(DateUtil.getMessageTimeString(new Date(item.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				UmengConstant.reportUmengEvent(UmengConstant.EVENT_SHARE_MASSAGE, null);
				showShareHomeworkDetail(item);
			}
		});
		return convertView;
	}

	/**
	 * 显示分享作业的详情
	 */
	private void showShareHomeworkDetail(EMMessage item) {
		MessagePushUtils pushUtils = new MessagePushUtils(mFragment);
		pushUtils.handleMessage(item);
	}

	private void setItemBackGroud(int subjectCode, View view) {
		if (subjectCode == -1)
			return;
		switch (subjectCode) {
			case SubjectUtils.SUBJECT_CODE_MATH:
				//subjectName = "数学";
				view.setBackgroundResource(R.drawable.message_share_math);
				break;
			case SubjectUtils.SUBJECT_CODE_CHINESE:
				//subjectName = "语文";
				view.setBackgroundResource(R.drawable.message_share_chinese);
				break;
			case SubjectUtils.SUBJECT_CODE_ENGLISH:
				//subjectName = "英语";
				view.setBackgroundResource(R.drawable.message_share_english);
				break;
			case SubjectUtils.SUBJECT_CODE_PHYSICAL:
				//subjectName = "物理";
				view.setBackgroundResource(R.drawable.message_share_physical);
				break;
			case SubjectUtils.SUBJECT_CODE_CHEMICAL:
				//subjectName = "化学";
				view.setBackgroundResource(R.drawable.message_share_chemical);
				break;
			case SubjectUtils.SUBJECT_CODE_BIOLOGY:
				//subjectName = "生物";
				view.setBackgroundResource(R.drawable.message_share_biology);
				break;
			case SubjectUtils.SUBJECT_CODE_HISTORY:
				//subjectName = "历史";
				view.setBackgroundResource(R.drawable.message_share_history);
				break;
			case SubjectUtils.SUBJECT_CODE_GEOGRAPHY:
				//subjectName = "地理";
				view.setBackgroundResource(R.drawable.message_share_geography);
				break;
			case SubjectUtils.SUBJECT_CODE_POLITIAL:
				//subjectName = "政治";
				view.setBackgroundResource(R.drawable.message_share_polital);
				break;
			case SubjectUtils.SUBJECT_CODE_INFORMATION:
				//subjectName = "信息技术"
				view.setBackgroundResource(R.drawable.message_share_infomation);
				break;
			default:
				break;
		}
	}
	
	private class ViewHolder {
		public View mBackground;
		public TextView mShareTitle;
		public TextView mFromTeacher;
		public TextView mSubject;
		public View mContainerView;
	}
	
}
