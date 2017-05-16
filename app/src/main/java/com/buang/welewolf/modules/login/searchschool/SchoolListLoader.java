package com.buang.welewolf.modules.login.searchschool;

import android.os.AsyncTask;

import com.buang.welewolf.base.bean.OnlineSearchSchoolInfo;
import com.buang.welewolf.base.bean.PinyinIndexModel;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.hyena.framework.datacache.DataAcquirer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SchoolListLoader extends AsyncTask<String, Void, Void> {

	private ArrayList<PinyinIndexModel> mListItems = new ArrayList<PinyinIndexModel>();
	private ArrayList<Integer> mListSectionPos = new ArrayList<Integer>();
	private SchoolLoadListener mLoadListener;
	private boolean isFailed;

	public SchoolListLoader(SchoolLoadListener loadListener) {
		mLoadListener = loadListener;
	}

	@Override
	protected void onPreExecute() {
		mLoadListener.onPreLoad();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(String... params) {
		mListItems.clear();
		mListSectionPos.clear();
		isFailed = false;
		// load school list
		String url = OnlineServices.getSearchSchoolByCityIdUrl(params[0]);
		OnlineSearchSchoolInfo result = new DataAcquirer<OnlineSearchSchoolInfo>()
				.acquire(url, new OnlineSearchSchoolInfo(), -1);
		if (result != null && result.isAvailable()) {
			List<PinyinIndexModel> items = result.mSchools;
			if (items.size() > 0) {
				Collections.sort(items, new SortIgnoreCase());

				String prev_section = "";
				for (PinyinIndexModel current_item : items) {
					String current_section = current_item.getFirstLetter();
					if (!prev_section.equals(current_section)) {
						PinyinIndexModel section = new PinyinIndexModel("",
								current_section);
						section.setFirstLetter(current_section);
						mListItems.add(section);
						mListItems.add(current_item);
						mListSectionPos.add(mListItems.indexOf(section));
						prev_section = current_section;
					} else {
						mListItems.add(current_item);
					}
				}
			}
		}else {
			isFailed = true;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (!isCancelled()) {
			mLoadListener.onPostLoad(mListItems, mListSectionPos, isFailed);
		}
		super.onPostExecute(result);
	}

	class SortIgnoreCase implements Comparator<PinyinIndexModel> {
		public int compare(PinyinIndexModel s1, PinyinIndexModel s2) {
			return s1.getPinyin().compareToIgnoreCase(s2.getPinyin());
		}
	}
}
