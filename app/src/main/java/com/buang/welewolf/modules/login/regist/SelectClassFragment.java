package com.buang.welewolf.modules.login.regist;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.DialogUtils.OnFillDialogBtnClickListener;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 注册时选择班级页面
 * @author Fanjb
 * @date 2015-3-26
 */
public class SelectClassFragment extends BaseUIFragment<UIFragmentHelper> {

	private View mCreateClass;
	private ListView mClassListView;
	private ClassListAdapter mClassListAdapter;
	private Dialog mCreateClassDialog;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		getTitleBar().setTitle("选择班群");
		View view = View.inflate(getActivity(),
				R.layout.layout_regist_select_class, null);
		return view;
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		getActivity()
				.getWindow()
				.setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		mCreateClass = view.findViewById(R.id.create_class_btn);
		mCreateClass.setOnClickListener(mOnClickListener);
		mClassListView = (ListView) view.findViewById(R.id.class_list_view);
		mClassListAdapter = new ClassListAdapter(getActivity());
		mClassListView.setAdapter(mClassListAdapter);
		mClassListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				sendBroadcast(mClassListAdapter.getItem(position).getName());
			}
		});
		mCreateClassDialog = DialogUtils.getFillBlackDialog(getActivity(),
				"创建自定义班群", "确定", "取消", "", -1, new OnFillDialogBtnClickListener() {
					@Override
					public void onItemClick(Dialog dialog, boolean isConfirm,
							String result) {
						if (isConfirm) {
							sendBroadcast(result);
						}
						if (mCreateClassDialog.isShowing()) {
							mCreateClassDialog.dismiss();
						}
					}
				});
	}
	
	@Override
	public void onPanelClosed(View pPanel) {
		// TODO Auto-generated method stub
		super.onPanelClosed(pPanel);
		if(getArguments() != null && !TextUtils.isEmpty(getArguments().getString("from"))) {
			if(mOnClassSelectListenr != null) {
				mOnClassSelectListenr.onConfirm("");
			}
		}
	}
	
	/**
	 * 设置信息并退出
	 * 
	 * @param classInfo
	 */
	private void sendBroadcast(String classInfo) {
//		Intent data = new Intent(ACTION_SELECT_CLASS);
//		data.putExtra("class_info", classInfo);
//		MsgCenter.sendLocalBroadcast(data);
		if(mOnClassSelectListenr != null) {
			mOnClassSelectListenr.onConfirm(classInfo);
		}
		finish();
	}
	
	public void closeFragment() {
		if(mOnClassSelectListenr != null) {
			mOnClassSelectListenr.onConfirm("");
		}
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mCreateClass) {
				if (!mCreateClassDialog.isShowing()) {
					mCreateClassDialog.show();
				}
			}
		}
	};

	class ClassListAdapter extends SingleTypeAdapter<BasicNameValuePair> {

		public ClassListAdapter(Context context) {
			super(context);
			List<BasicNameValuePair> items = new ArrayList<BasicNameValuePair>();
			for (int i = 1; i <= 20; i++) {
				items.add(new BasicNameValuePair(i + "班", i + "班"));
			}
			setItems(items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mContext,
						R.layout.layout_regist_select_class_item, null);
				holder.mText = (TextView) convertView
						.findViewById(R.id.class_name_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mText.setText(getItem(position).getName() + "");
			return convertView;
		}

		class ViewHolder {
			private TextView mText;
		}
	}
	
	private OnClassSelectListenr mOnClassSelectListenr;
	public void setOnClasSelectListenr(OnClassSelectListenr listenr){
		this.mOnClassSelectListenr = listenr;
	}
	public static interface OnClassSelectListenr {
		public void onConfirm(String classInfo);
	}
}
