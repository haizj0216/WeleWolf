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
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.modules.message.utils.MessagePushUtils;
import com.knowbox.teacher.modules.utils.DateUtil;

import java.util.Date;

/**
 * Created by LiuYu on 2016/5/21.
 */
public class EMChatSelfTrainAdapter extends SingleViewAdapter<EMMessage> {

	private EMConversation mEmConversation;
	private BaseUIFragment mFragment;
	private int mIndex;
	public EMChatSelfTrainAdapter(Context context, BaseAdapter parent, int type,
								  ChatListItem chatItem, BaseUIFragment fragment,
								  EMMessage message, int index) {
		super(context, parent, type, message);
		this.mFragment = fragment;
		mIndex = index;
		mEmConversation = EMChatManager.getInstance().getConversation(chatItem.mUserId);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			convertView = View.inflate(mContext, R.layout.layout_chatlist_item_self_train, null);
			holder = new ViewHolder();
			holder.mMessageTitle = (TextView) convertView.findViewById(R.id.message_title);
			holder.mMessageContent = (TextView) convertView.findViewById(R.id.massage_content);
			holder.mRankDate = (TextView) convertView.findViewById(R.id.rank_date);
			holder.mDescInfo = (TextView) convertView.findViewById(R.id.desc_info);
			holder.mContainerView = convertView.findViewById(R.id.chatlist_item_container);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final EMMessage item = getItem(position);
		String title = item.getStringAttribute("title", "");
		holder.mMessageTitle.setText(title);
		String content = item.getStringAttribute("txt", "");
		holder.mMessageContent.setText(content);
		String desc = item.getStringAttribute("txt2","");
		holder.mDescInfo.setText(desc);
		String rankDate = item.getStringAttribute("rankDate", "0");
		holder.mRankDate.setText(rankDate.substring(4,6)+"-"+rankDate.substring(6,8));

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
				new MessagePushUtils(mFragment).handleMessage(item);
			}
		});
		return convertView;
	}

//	private void showSelfTrainingRank(EMMessage item) {
//		Bundle mBundle = new Bundle();
//		mBundle.putString("class_ID", item.getStringAttribute("classID", ""));
//		mBundle.putString("rank_Date", item.getStringAttribute("rankDate", ""));
//		SelfTrainingRankFragment fragment = SelfTrainingRankFragment.newFragment(mFragment.getActivity()
//				, SelfTrainingRankFragment.class, mBundle);
//		mFragment.showFragment(fragment);
//	}

	private class ViewHolder {
		public TextView mMessageTitle;
		public TextView mMessageContent;
		public TextView mRankDate;
		public TextView mDescInfo;
		public View mContainerView;
	}
	
}
