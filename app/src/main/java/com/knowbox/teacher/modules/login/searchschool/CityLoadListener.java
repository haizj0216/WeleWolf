package com.knowbox.teacher.modules.login.searchschool;

import com.knowbox.teacher.base.bean.CityModel;

public interface CityLoadListener {
	public void onPreLoad();

	public void onPostLoad(CityModel cities);
}
