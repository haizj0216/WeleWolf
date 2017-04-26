package com.knowbox.teacher.modules.message.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;
import com.hyena.framework.app.adapter.SingleViewAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.modules.message.EMChatFragment;
import com.knowbox.teacher.modules.message.utils.Constant;
import com.knowbox.teacher.modules.message.utils.MessagePushUtils;
import com.knowbox.teacher.modules.message.utils.SendUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.Utils;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Date;

/**
 * Created by weilei on 15/11/4.
 */
public class EmChatHomeworkAdapter extends SingleViewAdapter<EMMessage> {

    private Context mContext;
    private ChatListItem mChatItem;
    private EMConversation mEmConversation;
    private BaseUIFragment mFragment;
    private int mIndex;

    public EmChatHomeworkAdapter(Context context, BaseAdapter parent,
                                 int type, ChatListItem chatItem, BaseUIFragment fragment,
                                 EMMessage message, int index) {
        super(context, parent, type, message);
        mContext = context;
        this.mChatItem = chatItem;
        mIndex = index;
        mFragment = fragment;
        mEmConversation = EMChatManager.getInstance().getConversation(
                chatItem.mUserId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_message_homework_link, null);
            holder = new ViewHolder();
            holder.mHeadIcon = (ImageView) convertView.findViewById(R.id.iv_userhead);
            holder.mUserName = (TextView) convertView.findViewById(R.id.tv_userid);
            holder.mHomeworkTime = (TextView) convertView.findViewById(R.id.chatlist_item_homework_time);
            holder.mHomeworkTitle = (TextView) convertView.findViewById(R.id.chatlist_item_homework_title);
            holder.mContainerView = convertView.findViewById(R.id.chatlist_item_container);
            holder.mContentView = convertView.findViewById(R.id.chatlist_item_homework_layout);
            holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
            holder.staus_iv = (ImageView) convertView
                    .findViewById(R.id.msg_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final EMMessage item = getItem(position);
        if (item == null) {
            return convertView;
        }
        EMMessage.ChatType chatType = item.getChatType();

        String content = item.getStringAttribute("txt", "");
        if (!TextUtils.isEmpty(content)) {
            holder.mHomeworkTime.setText(content);
        }

        String title = item.getStringAttribute("homeworkTitle", "");
        if (!TextUtils.isEmpty(title)) {
            holder.mHomeworkTitle.setText(title);
        }

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
        if (item.direct == EMMessage.Direct.SEND) {
            // View statusView = convertView.findViewById(R.id.msg_status);
            // 重发按钮点击事件
            holder.staus_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.getMessageDialog(mContext,
                            mContext.getString(R.string.resend),
                            mContext.getString(R.string.confirm_resend), "确定",
                            "取消", new DialogUtils.OnDialogButtonClickListener() {

                                @Override
                                public void onItemClick(Dialog dialog, int btnId) {
                                    if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
                                        SendUtils.resendMessage(
                                                mChatItem.mUserId, item);
                                        MsgCenter
                                                .sendLocalBroadcast(new Intent(
                                                        EMChatFragment.MSG_REFRESH_CHAT_LIST));
                                    }
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
            ImageFetcher.getImageFetcher().loadImage(
                    Utils.getLoginUserItem() == null ? ""
                            : Utils.getLoginUserItem().headPhoto,
                    holder.mHeadIcon, R.drawable.default_img,
                    new RoundedBitmapDisplayer(84));
        } else {
            ImageFetcher.getImageFetcher().loadImage(
                    chatType != EMMessage.ChatType.GroupChat ? mChatItem.mHeadPhoto
                            : item.getStringAttribute("userPhoto", ""),
                    holder.mHeadIcon, R.drawable.default_img,
                    new RoundedBitmapDisplayer(84));
        }
        if (item.direct == EMMessage.Direct.SEND
                && chatType != EMMessage.ChatType.GroupChat) {
            holder.tv_ack = (TextView) convertView.findViewById(R.id.tv_ack);
            holder.tv_delivered = (TextView) convertView
                    .findViewById(R.id.tv_delivered);
            if (holder.tv_ack != null) {
                if (item.isAcked) {
                    if (holder.tv_delivered != null) {
                        holder.tv_delivered.setVisibility(View.INVISIBLE);
                    }
                    holder.tv_ack.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_ack.setVisibility(View.INVISIBLE);

                    // check and display msg delivered ack status
                    if (holder.tv_delivered != null) {
                        if (item.isDelivered) {
                            holder.tv_delivered.setVisibility(View.VISIBLE);
                        } else {
                            holder.tv_delivered.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        } else {
            // 如果是文本或者地图消息并且不是group messgae，显示的时候给对方发送已读回执
            if ((item.getType() == EMMessage.Type.TXT || item.getType() == EMMessage.Type.LOCATION)
                    && !item.isAcked && chatType != EMMessage.ChatType.GroupChat) {
                // 不是语音通话记录
                if (!item.getBooleanAttribute(
                        Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    try {
                        EMChatManager.getInstance().ackMessageRead(
                                item.getFrom(), item.getMsgId());
                        // 发送已读回执
                        item.isAcked = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (item.direct == EMMessage.Direct.SEND) {
            switch (item.status) {
                case SUCCESS: // 发送成功
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    // 发送消息
                    sendMsgInBackground(item, holder);
            }
        }

        holder.mContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MessagePushUtils(mFragment).handleMessage(item);
            }
        });
        return convertView;
    }

    /**
     * 发送消息
     *
     * @param message
     * @param holder
     */
    public void sendMsgInBackground(final EMMessage message,
                                    final ViewHolder holder) {
        holder.staus_iv.setVisibility(View.GONE);
        holder.pb.setVisibility(View.VISIBLE);
        // 调用sdk发送异步发送方法
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onSuccess() {
                updateSendedView(message, holder);
            }

            @Override
            public void onError(int code, String error) {
                updateSendedView(message, holder);
            }

            @Override
            public void onProgress(int progress, String status) {
            }

        });

    }

    /**
     * 更新ui上消息发送状态
     *
     * @param message
     * @param holder
     */
    private void updateSendedView(final EMMessage message,
                                  final ViewHolder holder) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                // send success
                if (message.status == EMMessage.Status.SUCCESS) {

                } else if (message.status == EMMessage.Status.FAIL) {
                    ToastUtils.showShortToast(mContext, mContext.getString(R.string.send_fail)
                            + mContext.getString(R.string.connect_failuer_toast));
                }

                notifyDataSetChanged();
            }
        });
    }

    private int getIntAttr(EMMessage item, String attr) {
        int value = 0;
        try {
            value = item.getIntAttribute(attr, 0);
        } catch (Exception e) {
            try {
                value = Integer.parseInt(item.getStringAttribute(attr, "0"));
            } catch (Exception e2) {
            }
        }
        return value;
    }

    private class ViewHolder {
        public ImageView mHeadIcon;
        public TextView mUserName;
        public TextView mHomeworkTime;
        public TextView mHomeworkTitle;
        public View mContainerView;
        public View mContentView;

        ProgressBar pb;
        ImageView staus_iv;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;
    }
}
