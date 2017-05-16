package com.buang.welewolf.modules.login.searchschool;

import com.buang.welewolf.base.bean.CityModel;

public interface CityLoadListener {
	public void onPreLoad();

	public void onPostLoad(CityModel cities);
}
