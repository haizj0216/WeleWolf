package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlinePaperInfo extends BaseObject {

	public List<PaperItem> mPapers = new ArrayList<OnlinePaperInfo.PaperItem>();

	public String mTotalCount;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		try {
			if (isAvailable()) {
				JSONObject obj = json.optJSONObject("data");
				if (null != obj) {
					mTotalCount = obj.optString("totalCount");
					JSONArray arrays = obj.optJSONArray("list");
					if (arrays != null) {
						for (int i = 0; i < arrays.length(); i++) {
							JSONObject paper = arrays.optJSONObject(i);
							PaperItem item = new PaperItem();
							item.mPaperId = paper.optString("paperId");
							item.mPaperName = paper.optString("name");
							item.mCount = paper.optInt("count");
							item.mType = paper.optString("type");

							if (paper.has("tab")) {
								JSONArray tabs = paper.optJSONArray("tab");
								item.mTabs = new ArrayList<String>();
								for (int j = 0; j < tabs.length(); j++) {
									item.mTabs.add(tabs.optString(j));
								}
							}

							mPapers.add(item);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class PaperItem {
		public String mPaperId;
		public String mPaperName;
		public int mCount;
		public String mType;
		public List<String> mTabs;
	}

}
