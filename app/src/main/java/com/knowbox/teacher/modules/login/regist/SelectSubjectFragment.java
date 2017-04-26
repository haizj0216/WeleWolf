package com.knowbox.teacher.modules.login.regist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.teacher.R;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 注册第四步选择科目页面
 * @author Fanjb
 * @date 2015-3-12
 */
public class SelectSubjectFragment extends BaseUIFragment<UIFragmentHelper> {
	private GridView mGridView;
	private SubjectGridAdapter mGridAdapter;
	
	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateImpl(savedInstanceState);
	}
	
	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreatedImpl(view, savedInstanceState);
		view.findViewById(R.id.select_subject_close).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mOnSubjectSelectListener != null) {
							mOnSubjectSelectListener.onSubjectSelected("","");
						}
					}
				});
		mGridView = (GridView) view.findViewById(R.id.select_subject_grid);
		mGridAdapter = new SubjectGridAdapter(getActivity());
		mGridView.setAdapter(mGridAdapter);
	}
	
	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.layout_regist_select_subject_activity, null);
		return view;
	}
	
	/**
	 * @name 科目网格列表适配器
	 */
	private class SubjectGridAdapter extends
			SingleTypeAdapter<BasicNameValuePair> {

		public SubjectGridAdapter(Context context) {
			super(context);
			List<BasicNameValuePair> items = new ArrayList<BasicNameValuePair>();
			items.add(new BasicNameValuePair("数学", "0"));
			items.add(new BasicNameValuePair("语文", "1"));
			items.add(new BasicNameValuePair("英语", "2"));
			items.add(new BasicNameValuePair("物理", "3"));
			items.add(new BasicNameValuePair("化学", "4"));
			items.add(new BasicNameValuePair("生物", "5"));
			items.add(new BasicNameValuePair("历史", "6"));
			items.add(new BasicNameValuePair("地理", "7"));
			items.add(new BasicNameValuePair("政治", "8"));
			setItems(items);
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final BasicNameValuePair item = getItem(position);
			convertView = View.inflate(mContext,
					R.layout.layout_regist_select_subject_grid_item, null);
			Button subjectBtn = (Button) convertView
					.findViewById(R.id.subject_icon);
			subjectBtn.setText(item.getName());
			subjectBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(ACTION_SELECT_SUBJECT);
//					intent.putExtra("subject_name", item.getName());
//					intent.putExtra("subject_id", item.getValue());
//					MsgCenter.sendLocalBroadcast(intent);
					if(mOnSubjectSelectListener != null) {
						mOnSubjectSelectListener.onSubjectSelected(item.getName(), item.getValue());
					}
				}
			});
			return convertView;
		}
	}
	
	public void closeFragment() {
		if(mOnSubjectSelectListener != null) {
			mOnSubjectSelectListener.onSubjectSelected("", "");
		}
	}
	
	@Override
	public void onDestroyImpl() {
		// TODO Auto-generated method stub
		super.onDestroyImpl();
		mGridView = null;
		mGridAdapter = null;
	}
	
	public void setOnSubjectSelectListener(OnSubjectSelectListener listener) {
		mOnSubjectSelectListener = listener;
	}
	
	private OnSubjectSelectListener mOnSubjectSelectListener;
	
	public interface OnSubjectSelectListener {
		public void onSubjectSelected(String name, String id);
	}

}
