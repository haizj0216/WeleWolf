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
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.hyena.framework.app.adapter.SingleViewAdapter;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.knowbox.teacher.R;

import java.util.Date;

/**
 * Created by weilei on 15/10/14.
 */
public class EMChatTipAdapter extends SingleViewAdapter<EMMessage> {
    private Context mContext;
    private EMConversation mEmConversation;
    private int mIndex;

    public EMChatTipAdapter(Context context, BaseAdapter parent,
                            int type, String userId, BaseSubFragment fragment,
                            EMMessage message, int index) {
        super(context, parent, type, message);
        mContext = context;
        mIndex = index;
        mEmConversation = EMChatManager.getInstance().getConversation(userId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.layout_message_row_received_createclass, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.timestamp);
            holder.title = (TextView) convertView
                    .findViewById(R.id.tv_create_chatcontent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            final EMMessage message = getItem(position);
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
            TextMessageBody txtBody = (TextMessageBody) message.getBody();
            String title = txtBody.getMessage();
            if (TextUtils.isEmpty(title)) {
                holder.title.setVisibility(View.GONE);
            } else {
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(title);
            }
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
