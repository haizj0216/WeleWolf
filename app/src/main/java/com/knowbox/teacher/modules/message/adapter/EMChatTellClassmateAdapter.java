package com.knowbox.teacher.modules.message.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;
import com.hyena.framework.app.adapter.SingleViewAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.base.utils.RoundDisplayer;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.modules.message.utils.MessagePushUtils;
import com.knowbox.teacher.modules.utils.DateUtil;

import java.util.Date;

/**
 * Created by yangxh on 2016/6/13.
 */
public class EMChatTellClassmateAdapter extends SingleViewAdapter<EMMessage> {

	private EMConversation mEmConversation;
	private BaseUIFragment mFragment;
	private ChatListItem mChatItem;
	private int mIndex;
	public EMChatTellClassmateAdapter(Context context, BaseAdapter parent, int type,
									  ChatListItem chatItem, BaseUIFragment fragment,
									  EMMessage message, int index) {
		super(context, parent, type, message);
		this.mFragment = fragment;
		this.mChatItem=chatItem;
		mIndex = index;
		mEmConversation = EMChatManager.getInstance().getConversation(chatItem.mUserId);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			convertView = View.inflate(mContext, R.layout.layout_chatlist_item_tell_classmate, null);
			holder = new ViewHolder();
			holder.mMessageTitle = (TextView) convertView.findViewById(R.id.chatlist_item_content);
            holder.mHeadIcon=(ImageView) convertView.findViewById(R.id.iv_userhead);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final EMMessage item = getItem(position);
		String title = item.getStringAttribute("txt", "");
		String photo=item.getStringAttribute("userPhoto","");
		holder.mMessageTitle.setText(title);
		holder.mHeadIcon.setImageURI(Uri.parse(photo));
		ImageFetcher.getImageFetcher().loadImage(mChatItem.mHeadPhoto,
				holder.mHeadIcon, R.drawable.profile_icon_default, new RoundDisplayer());


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
				showClassMateRank(item);
			}
		});
		return convertView;
	}

	private void showClassMateRank(EMMessage item) {
		MessagePushUtils pushUtils=new MessagePushUtils(mFragment);
		pushUtils.handleMessage(item);
	}

	private class ViewHolder {
		public TextView mMessageTitle;
		public ImageView mHeadIcon;

	}
	
}
