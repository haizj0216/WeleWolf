// @author Bhavya Mehta
package com.buang.welewolf.widgets.pinned_listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buang.welewolf.R;
import com.buang.welewolf.base.bean.PinyinIndexModel;

import java.util.ArrayList;
import java.util.List;

// Customized adaptor to populate data in PinnedHeaderListView
public class PinnedHeaderAdapter extends BaseAdapter implements
		OnScrollListener, IPinnedHeader {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SECTION = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SECTION + 1;

	LayoutInflater mLayoutInflater;
	int mCurrentSectionPosition = 0, mNextSectionPostion = 0;

	// array list to store section positions
	ArrayList<Integer> mListSectionPos;

	// array list to store list view data
	List<PinyinIndexModel> mListItems;

	// context object
	Context mContext;

	public PinnedHeaderAdapter(Context context,
			List<PinyinIndexModel> listItems, ArrayList<Integer> listSectionPos) {
		this.mContext = context;
		this.mListItems = listItems;
		this.mListSectionPos = listSectionPos;

		mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return !mListSectionPos.contains(position);
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return mListSectionPos.contains(position) ? TYPE_SECTION : TYPE_ITEM;
	}

	@Override
	public Object getItem(int position) {
		return mListItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mListItems.get(position).hashCode();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			int type = getItemViewType(position);
			final float density = mContext.getResources().getDisplayMetrics().density;
			switch (type) {
			case TYPE_ITEM:
				convertView = mLayoutInflater.inflate(
						R.layout.dialog_choose_school_row_view, null);
				AbsListView.LayoutParams rowParams = new AbsListView.LayoutParams(
						AbsListView.LayoutParams.MATCH_PARENT,
						(int) (55 * density));
				convertView.setLayoutParams(rowParams);
				break;
			case TYPE_SECTION:
				convertView = mLayoutInflater.inflate(
						R.layout.dialog_choose_school_section_row_view, null);
				AbsListView.LayoutParams sectionParams = new AbsListView.LayoutParams(
						AbsListView.LayoutParams.MATCH_PARENT,
						(int) (23 * density));
				convertView.setLayoutParams(sectionParams);
				break;
			}
			holder.textView = (TextView) convertView
					.findViewById(R.id.row_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textView.setText(mListItems.get(position).getName());
		return convertView;
	}

	@Override
	public int getPinnedHeaderState(int position) {
		// hide pinned header when items count is zero OR position is less than
		// zero OR
		// there is already a header in list view
		if (getCount() == 0 || position < 0
				|| mListSectionPos.indexOf(position) != -1) {
			return PINNED_HEADER_GONE;
		}

		// the header should get pushed up if the top item shown
		// is the last item in a section for a particular letter.
		mCurrentSectionPosition = getCurrentSectionPosition(position);
		mNextSectionPostion = getNextSectionPosition(mCurrentSectionPosition);
		if (mNextSectionPostion != -1 && position == mNextSectionPostion - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}

		return PINNED_HEADER_VISIBLE;
	}

	public int getCurrentSectionPosition(int position) {
		PinyinIndexModel section = new PinyinIndexModel("", mListItems.get(
				position).getFirstLetter());
		return mListItems.indexOf(section);
	}

	public int getNextSectionPosition(int currentSectionPosition) {
		int index = mListSectionPos.indexOf(currentSectionPosition);
		if ((index + 1) < mListSectionPos.size()) {
			return mListSectionPos.get(index + 1);
		}
		return mListSectionPos.get(index);
	}

	@Override
	public void configurePinnedHeader(View v, int position) {
		// set text in pinned header
		TextView header = (TextView) v;
		mCurrentSectionPosition = getCurrentSectionPosition(position);
		header.setText(mListItems.get(mCurrentSectionPosition).getFirstLetter());
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	public static class ViewHolder {
		public TextView textView;
	}
}
