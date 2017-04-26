/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.message.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.knowbox.teacher.modules.message.adapter.EMChatListPiecesAdapter;
import com.knowbox.teacher.modules.utils.DialogUtils;

import java.util.List;

@SuppressWarnings("deprecation")
public class EMChatMenuLongClickListener implements OnLongClickListener {

	public static final int TYPE_COPY = 0;
	public static final int TYPE_DELETE = 1;
	public static final int TYPE_FORWARD = 2;
	private List<MenuItem> mItemDatas;
	private Dialog mMenuDialog = null;
	
	private Context mContext;
	private EMChatListPiecesAdapter mChatListAdapter;
	private EMMessage mMessage;
	
	public EMChatMenuLongClickListener(Context context, EMChatListPiecesAdapter adapter,
									   EMMessage message, List<MenuItem> items) {
		this.mContext = context;
		this.mChatListAdapter= adapter;
		this.mMessage = message;
		this.mItemDatas = items;
	}
	
	@Override
	public boolean onLongClick(View v) {
		mMenuDialog = DialogUtils.getListDialog(mContext, "选项", mItemDatas, new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MenuItem item = mItemDatas.get(position);
				switch (item.type) {
				case TYPE_COPY:
				{
					ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
					clipboard.setText(((TextMessageBody) mMessage.getBody()).getMessage());
					break;
				}
				case TYPE_DELETE:
				{
					mChatListAdapter.getEmConversation().removeMessage(mMessage.getMsgId());
					mChatListAdapter.refreshList();
					break;
				}
				default:
					break;
				}
				if(mMenuDialog != null && mMenuDialog.isShowing()){
					mMenuDialog.dismiss();
				}
			}
		});
		mMenuDialog.show();
		return true;
	}

}
