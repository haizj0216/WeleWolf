/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.buang.welewolf.modules.login.searchschool;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.buang.welewolf.base.bean.OnlineSearchSchoolInfo;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.hyena.framework.app.activity.BaseActivity;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @name 注册时搜索学校
 * @author Fanjb
 * @date 2015-3-11
 */
public class SearchSchoolActivity extends BaseActivity {

	public static final String ACTION_SELECT_SCHOOL = "com.buang.welewolf.select_school";

	private EditText mSearchEdt;
	private ListView mResultList;
	private ResultAdapter mResultAdapter;
	private SearchResultTask mCurrentTask;

	/**
	 * item点击事件：结束本次选择并回调结果
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			NameValuePair item = mResultAdapter.getItem(position);
			Intent data = new Intent(ACTION_SELECT_SCHOOL);
			data.putExtra("school_name", item.getName());
			data.putExtra("school_id", item.getValue());
			MsgCenter.sendLocalBroadcast(data);
			UIUtils.hideInputMethod(SearchSchoolActivity.this);
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(com.buang.welewolf.R.layout.layout_regist_search_school);
		mSearchEdt = (EditText) findViewById(com.buang.welewolf.R.id.searchschool_edit);
		mResultList = (ListView) findViewById(com.buang.welewolf.R.id.searchschool_resultlist);
		mResultList.setOnItemClickListener(mItemClickListener);
		mSearchEdt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				return false;
			}
		});

		mSearchEdt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String searchTxt = s.toString();
				if (TextUtils.isEmpty(searchTxt)) {
					clearResult();
					return;
				}

				if (mCurrentTask != null) {
					mCurrentTask.cancel(true);
				}
				mCurrentTask = new SearchResultTask();
				mCurrentTask.execute(searchTxt);
			}
		});

		findViewById(com.buang.welewolf.R.id.searchschool_root).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						UIUtils.hideInputMethod(SearchSchoolActivity.this);
						finish();
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSearchEdt = null;
		mResultAdapter = null;
		mCurrentTask = null;
	}

	private void clearResult() {
		if (mResultAdapter != null) {
			mResultAdapter.setItems(null);
		}
	}

	/**
	 * @name 搜索学校任务
	 */
	private class SearchResultTask extends
			AsyncTask<String, Void, List<NameValuePair>> {
		Map<String, String> umengCount = new HashMap<String, String>();
		long reqTime = 0;

		@Override
		protected List<NameValuePair> doInBackground(String... params) {
			reqTime = System.currentTimeMillis();

//			String url = OnlineServices.getSearchSchoolUrl(params[0]);
			String url = "";
			OnlineSearchSchoolInfo result = new DataAcquirer<OnlineSearchSchoolInfo>()
					.acquire(url, new OnlineSearchSchoolInfo());
			reqTime = System.currentTimeMillis() - reqTime;
			umengCount.put(UmengConstant.REQ_TIME, "" + (reqTime / 1000.0));
			if (result.isAvailable()) {
				umengCount.put(UmengConstant.RESULT, UmengConstant.SUCCESS);
				MobclickAgent.onEvent(SearchSchoolActivity.this,
						UmengConstant.REGIST_GET_SCHOOL_LIST, umengCount);
				// return result.mSchools;
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("(statusCode = ").append(result.getStatusCode());
				if (!TextUtils.isEmpty(result.getRawResult())) {
					sb.append(" , rawCode = ").append(result.getRawResult())
							.append(")");
				} else {
					sb.append(")");
				}
				umengCount.put(UmengConstant.RESULT,
						UmengConstant.FAIL + sb.toString());
				MobclickAgent.onEvent(SearchSchoolActivity.this,
						UmengConstant.REGIST_GET_SCHOOL_LIST, umengCount);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<NameValuePair> result) {
			super.onPostExecute(result);
			if (result != null && !result.isEmpty()) {
				if (mResultAdapter == null) {
					mResultAdapter = new ResultAdapter(
							SearchSchoolActivity.this);
					mResultList.setAdapter(mResultAdapter);
				}
				mResultAdapter.setItems(result);
			} else {
				if (mResultAdapter != null)
					mResultAdapter.setItems(null);
			}
		}
	}

	private class ResultAdapter extends SingleTypeAdapter<NameValuePair> {

		public ResultAdapter(Context context) {
			super(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(SearchSchoolActivity.this,
						com.buang.welewolf.R.layout.layout_regist_search_list_item, null);
				holder = new ViewHolder();
				holder.mResultTxt = (TextView) convertView
						.findViewById(com.buang.welewolf.R.id.search_list_item_result_txt);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			NameValuePair item = getItem(position);
			holder.mResultTxt.setText(item.getName());
			return convertView;
		}

		private class ViewHolder {
			public TextView mResultTxt;
		}
	}

	@Override
	public void overridePendingTransition(int enterAnim, int exitAnim) {
		super.overridePendingTransition(0, 0);
	}
}
