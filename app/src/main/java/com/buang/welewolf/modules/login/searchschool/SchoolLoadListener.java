package com.buang.welewolf.modules.login.searchschool;

import com.buang.welewolf.base.bean.PinyinIndexModel;

import java.util.ArrayList;
import java.util.List;

public interface SchoolLoadListener {
	public void onPreLoad();

	public void onPostLoad(List<PinyinIndexModel> data,
			ArrayList<Integer> sectionPos, boolean isfailed);
}
