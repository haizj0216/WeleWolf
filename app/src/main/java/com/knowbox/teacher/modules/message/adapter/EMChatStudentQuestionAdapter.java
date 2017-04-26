package com.knowbox.teacher.modules.message.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;
import com.hyena.framework.app.adapter.SingleViewAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.message.utils.MessagePushUtils;
import com.knowbox.teacher.modules.utils.DateUtil;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Date;

/**
 * Created by weilei on 16/1/19.
 */
public class EMChatStudentQuestionAdapter extends SingleViewAdapter<EMMessage> {

    private Context mContext;
    private ChatListItem mChatItem;
    private EMConversation mEmConversation;
    private LoginService mLoginService;
    private BaseUIFragment mFragment;
    private int mIndex;

    public EMChatStudentQuestionAdapter(Context context, BaseAdapter parent,
                                        int type, ChatListItem chatItem, BaseUIFragment fragment,
                                        EMMessage message, int index) {
        super(context, parent, type, message);
        mContext = context;
        this.mChatItem = chatItem;
        mFragment = fragment;
        this.mIndex = index;
        mEmConversation = EMChatManager.getInstance().getConversation(
                chatItem.mUserId);
        mLoginService = (LoginService) BaseApp.getAppContext()
                .getSystemService(LoginService.SERVICE_NAME);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EMMessage message = getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext,
                    R.layout.layout_row_receive_question_link, null);
            holder = new ViewHolder();
            holder.head_iv = (ImageView) convertView
                    .findViewById(R.id.iv_userhead);
            holder.tv_userId = (TextView) convertView
                    .findViewById(R.id.tv_userid);

            holder.msg_chatcontent_container = (LinearLayout) convertView
                    .findViewById(R.id.msg_chatcontent_container);
            holder.tv_chatcontent_title = (TextView) convertView
                    .findViewById(R.id.tv_chatcontent_title);
            holder.tv_chatcontent_time = (TextView) convertView
                    .findViewById(R.id.tv_chatcontent_left_time);
            holder.tv_chatcontent_questionNo = (TextView) convertView
                    .findViewById(R.id.tv_chatcontent_questionNo);
            holder.tv_chatcontent_questionType = (TextView) convertView
                    .findViewById(R.id.tv_chatcontent_questionType);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageFetcher.getImageFetcher().loadImage(message.getStringAttribute("userPhoto", ""), holder.head_iv, R.drawable.default_img,
                new RoundedBitmapDisplayer(84));
        holder.tv_chatcontent_questionType.setText(SubjectUtils.getNameByQuestionType(
                Integer.parseInt(message.getStringAttribute("questionType", "0"))));
        holder.tv_chatcontent_title.setText(message.getStringAttribute("homeworkTitle", "暂无作业标题"));
        String no = (Integer.parseInt(message.getStringAttribute("questionIndex", "0")) + 1) + "/" +
                (Integer.parseInt(message.getStringAttribute("questionCount", "0")) + 1);
        holder.tv_chatcontent_questionNo.setText(no);
        try {
            holder.tv_chatcontent_time.setText(DateUtil.getMonthDayString(message.getIntAttribute("homeworkStartTime", 0)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        if (mIndex == 0) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message
                    .getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (DateUtils.isCloseEnough(message.getMsgTime(), mEmConversation
                    .getMessage(mIndex - 1).getMsgTime())) {
                timestamp.setVisibility(View.GONE);
            } else {
                timestamp.setText(DateUtils.getTimestampString(new Date(message
                        .getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessagePushUtils messagePushUtils = new MessagePushUtils(mFragment);
                messagePushUtils.handleMessage(message);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        TextView tv;
        ImageView head_iv;
        TextView tv_userId;
        // 显示已读回执状态
        LinearLayout msg_chatcontent_container;
        TextView tv_chatcontent_title;
        TextView tv_chatcontent_time;
        TextView tv_chatcontent_questionNo;
        TextView tv_chatcontent_questionType;
    }
}
