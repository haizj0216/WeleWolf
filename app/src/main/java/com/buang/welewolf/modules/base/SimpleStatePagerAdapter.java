package com.buang.welewolf.modules.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class SimpleStatePagerAdapter<T extends Fragment> extends
		FragmentStatePagerAdapter {
	private List<T> mItems;

	public SimpleStatePagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	public void setItems(List<T> items) {
		this.mItems = items;
	}

	@Override
	public T getItem(int position) {
		if(mItems == null)
			return null;
		return mItems.get(position);
	}

	@Override
	public int getCount() {
		if (mItems == null)
			return 0;
		return mItems.size();
	}

}
