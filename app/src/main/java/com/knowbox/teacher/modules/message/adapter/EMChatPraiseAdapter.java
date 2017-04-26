package com.knowbox.teacher.modules.message.adapter;

import android.content.Context;
import android.text.TextUtils;
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

import java.util.Date;

/**
 * Created by LiuYu on 16/5/21.
 */
public class EMChatPraiseAdapter extends SingleViewAdapter<EMMessage> {
    private Context mContext;
    private EMConversation mEmConversation;
    private int mIndex;
    private BaseUIFragment mFragment;

    public EMChatPraiseAdapter(Context context, BaseAdapter parent,
                               int type, ChatListItem chatItem, BaseUIFragment fragment,
                               EMMessage message, int index) {
        super(context, parent, type, message);
        mContext = context;
        mIndex = index;
        this.mFragment = fragment;
        mEmConversation = EMChatManager.getInstance().getConversation(chatItem.mUserId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.layout_message_praise_student, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.timestamp);
            holder.title = (TextView) convertView
                    .findViewById(R.id.tv_praise_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            final EMMessage message = getItem(position);

            String title = message.getStringAttribute("txt", "");
            if (TextUtils.isEmpty(title)) {
                title = message.getStringAttribute("msg","");
            }
            holder.title.setText(title);
//            String rankDate = message.getStringAttribute("rankDate", "0");
//            holder.mRankDate.setText(DateUtil.getSelfTrainRankTime(Long.parseLong(rankDate)));
            if (mIndex == 0) {
                holder.time.setText(DateUtils.getTimestampString(new Date(message
                        .getMsgTime())));
                holder.time.setVisibility(View.VISIBLE);
            } else {
                // 两条消息时间离得如果稍长，显示时间
                if (DateUtils.isCloseEnough(message.getMsgTime(), mEmConversation
                        .getMessage(mIndex - 1).getMsgTime())) {
                    holder.time.setVisibility(View.GONE);
                } else {
                    holder.time.setText(DateUtils.getTimestampString(new Date(message
                            .getMsgTime())));
                    holder.time.setVisibility(View.VISIBLE);
                }
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MessagePushUtils(mFragment).handleMessage(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView time;
        TextView title;

    }
}
