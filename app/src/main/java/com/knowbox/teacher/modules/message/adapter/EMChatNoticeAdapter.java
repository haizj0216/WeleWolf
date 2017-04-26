package com.knowbox.teacher.modules.message.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.knowbox.teacher.R;
import com.knowbox.teacher.modules.message.utils.MessagePushUtils;
import com.knowbox.teacher.modules.utils.DateUtil;

import java.util.Date;

/**
 * 聊题 adapter
 *
 * @author weilei
 */
public class EMChatNoticeAdapter extends SingleViewAdapter<EMMessage> {

    public static final String NATIVE_AUTH_URI = "native://authentication";
    private Context mContext;
    private EMConversation mEmConversation;
    private BaseUIFragment mFragment;
    private int mIndex;

    public EMChatNoticeAdapter(Context context, BaseAdapter parent,
                               int type, String userId, BaseUIFragment fragment,
                               EMMessage message, int index) {
        super(context, parent, type, message);
        mContext = context;
        mFragment = fragment;
        mIndex = index;
        mEmConversation = EMChatManager.getInstance().getConversation(userId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.layout_message_notice_item, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.timestamp);
            holder.img = (ImageView) convertView
                    .findViewById(R.id.iv_notice_image);
            holder.title = (TextView) convertView
                    .findViewById(R.id.tv_notice_title);
            holder.content = (TextView) convertView
                    .findViewById(R.id.tv_notice_content);
            holder.divider = convertView
                    .findViewById(R.id.view_notice_divider);
            holder.detail = convertView
                    .findViewById(R.id.layout_notice_content_bottom);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final EMMessage message = getItem(position);
        if (mIndex == 0) {
            holder.time.setText(DateUtil.getMessageTimeString(new Date(message
                    .getMsgTime())));
            holder.time.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (DateUtils.isCloseEnough(message.getMsgTime(), mEmConversation
                    .getMessage(mIndex - 1).getMsgTime())) {
                holder.time.setVisibility(View.GONE);
            } else {
                holder.time.setText(DateUtil.getMessageTimeString(new Date(message
                        .getMsgTime())));
                holder.time.setVisibility(View.VISIBLE);
            }
        }
        String img = message.getStringAttribute("img", null);
        if (TextUtils.isEmpty(img)) {
            holder.img.setVisibility(View.GONE);
        } else {
            holder.img.setVisibility(View.VISIBLE);
            ImageFetcher.getImageFetcher().loadImage(img, holder.img, R.drawable.default_image);
        }
        String title = message.getStringAttribute("title", null);
        String content = message.getStringAttribute("txt", null);
        if (TextUtils.isEmpty(title)) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(title);
        }
        if (TextUtils.isEmpty(content)) {
            holder.content.setVisibility(View.GONE);
        } else {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(content);
        }
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new MessagePushUtils(mFragment).handleMessage(message);
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        TextView time;
        ImageView img;
        TextView title;
        TextView content;
        View divider;
        View detail;
    }

}
