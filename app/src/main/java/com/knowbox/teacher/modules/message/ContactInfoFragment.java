/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.knowbox.teacher.modules.message;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineContactInfo;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.widgets.RoundDisplayer;

/**
 * @name 联系人基本信息场景
 * @author Fanjb
 * @date 2015-3-14
 */
public class ContactInfoFragment extends BaseUIFragment<UIFragmentHelper> {

	private ChatListItem mContactItem;
	private ImageView mUserHeadImg;
	private TextView mUserNameText;
	private ImageView mUserSexImg;
	private TextView mUserSchoolText;
	private ListView mClassListView;
	private ClassListAdapter mClassListAdapter;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
		mContactItem = (ChatListItem) getArguments()
				.getSerializable("userItem");
		if (mContactItem == null) {
			return;
		}
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		setMenuVisibility(false);
		getTitleBar().setTitle("详细资料");
		return View.inflate(getActivity(), R.layout.layout_contact_info, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);

		// 显示联系人基本信息：头像、姓名、性别、学校
		mUserHeadImg = (ImageView) view.findViewById(R.id.user_head_img);
		mUserNameText = (TextView) view.findViewById(R.id.user_name_text);
		mUserSexImg = (ImageView) view.findViewById(R.id.user_sex_icon);
		mUserSchoolText = (TextView) view.findViewById(R.id.user_school_text);
		mClassListView = (ListView) view.findViewById(R.id.class_list_view);
		mClassListAdapter = new ClassListAdapter(getActivity());
		mClassListView.setAdapter(mClassListAdapter);
		mClassListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
		loadData(0x11, 1, new OnlineContactInfo());
	}

	@Override
	public BaseObject onProcess(int action, int pageNo, Object... params) {
		String url = OnlineServices.getContactInfoUrl(Utils.getToken(), mContactItem.mUserId);
		return new DataAcquirer<OnlineContactInfo>().get(url, new OnlineContactInfo());
	}

	/**
	 * 数据读取成功，显示
	 * 
	 * @param action
	 * @param pageNo
	 * @param result
	 */
	@Override
	public void onGet(int action, int pageNo, BaseObject result) {
		super.onGet(action, pageNo, result);
		OnlineContactInfo info = (OnlineContactInfo) result;
		ImageFetcher.getImageFetcher().loadImage(info.headPhoto, mUserHeadImg,
				R.drawable.default_img, new RoundDisplayer());
		if (info.studentName != null) {
			mUserNameText.setText(info.studentName);
		}
		if (info.sex != null) {
			if (info.sex.equals("2")) {
				mUserSexImg.setImageResource(R.drawable.profile_icon_man);
			} else {
				mUserSexImg.setImageResource(R.drawable.profile_icon_woman);
			}
		}
		if (info.school != null) {
			mUserSchoolText.setText(info.school);
		}
		mClassListAdapter.setItems(info.classItems);
	}

	@Override
	public void onFail(int action, int pageNo, BaseObject result) {
		super.onFail(action, pageNo, result);
		showContent();
	}

	@Override
	public void onDestroyViewImpl() {
		super.onDestroyViewImpl();
		mUserHeadImg = null;
		mUserNameText = null;
		mUserSexImg = null;
		mUserSchoolText = null;
		mClassListView = null;
	}

	/**
	 * @name 学生已加入班级列表适配器
	 * @author Fanjb
	 * @date 2015-3-14
	 */
	class ClassListAdapter extends SingleTypeAdapter<ClassInfoItem> {

		public ClassListAdapter(Context context) {
			super(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.layout_myclass_item, null);
				holder = new Holder();
				holder.mClassName = (TextView) convertView
						.findViewById(R.id.profile_myclass_name);
				holder.mArrow = convertView.findViewById(R.id.icon);
				holder.mDividerLine = convertView
						.findViewById(R.id.item_divider_line);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.mClassName.setText(SubjectUtils.getGradeName(getItem(position).grade) + getItem(position).className);
			holder.mArrow.setVisibility(View.GONE);
			if (position == getItems().size() - 1) {
				holder.mDividerLine.setVisibility(View.GONE);
			} else {
				holder.mDividerLine.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		public class Holder {
			private TextView mClassName;
			private View mArrow;
			private View mDividerLine;
		}
	}
}
