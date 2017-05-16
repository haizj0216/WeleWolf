package com.buang.welewolf.modules.login.searchschool;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.buang.welewolf.base.bean.CityModel;
import com.buang.welewolf.base.bean.OnlineCityInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.PreferencesController;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.network.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CityListLoader {

	private static final String PREF_CITYPLIST_VERSION = "prefs_cityplist_version";
	private final int CITY_VERSION = 1;
	private String FILENAME;

	private Context mContext;
	private CityModel rootCity;
	private CityLoadListener mLoadListener;

	public CityListLoader(Context context) {
		mContext = context;
		FILENAME = context.getFilesDir() + "/city.json";
	}

	public void setLoadListener(CityLoadListener loadListener) {
		mLoadListener = loadListener;
	}

	private void loadCities() {
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		InputStreamReader inputStreamReader = null;
		InputStream in = null;
		BufferedReader bufferedReader = null;
		if (rootCity == null) {
			rootCity = new CityModel();
			try {
				if (new File(FILENAME).exists()) {
					in = new FileInputStream(FILENAME);
				} else {
					in = mContext.getAssets().open("city.json");
				}
				inputStreamReader = new InputStreamReader(in, "UTF-8");
				bufferedReader = new BufferedReader(
						inputStreamReader);
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}

				if (TextUtils.isEmpty(stringBuilder)) {
					in = mContext.getAssets().open("city.json");
					inputStreamReader = new InputStreamReader(in, "UTF-8");
					bufferedReader = new BufferedReader(
							inputStreamReader);
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line);
					}
				}

				bufferedReader.close();
				inputStreamReader.close();
				rootCity.setChildren(parseCityJson(rootCity,
						new JSONArray(stringBuilder.toString())));
			} catch (Exception e) {
				e.printStackTrace();
				try {
					if (TextUtils.isEmpty(stringBuilder)) {
						in = mContext.getAssets().open("city.json");
						inputStreamReader = new InputStreamReader(in, "UTF-8");
						bufferedReader = new BufferedReader(
								inputStreamReader);
						while ((line = bufferedReader.readLine()) != null) {
							stringBuilder.append(line);
						}
					}

					bufferedReader.close();
					inputStreamReader.close();
					rootCity.setChildren(parseCityJson(rootCity,
							new JSONArray(stringBuilder.toString())));
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		}
	}

	private List<CityModel> parseCityJson(CityModel parent,
										  JSONArray cityArray) throws JSONException {
		int length = cityArray.length();
		List<CityModel> list = new ArrayList<CityModel>(length);
		for (int i = 0; i < length; i++) {
			JSONObject json = cityArray.getJSONObject(i);
			String name = json.getString("v");
			CityModel city = new CityModel(json.getString("k"), name);
			city.setFullName(parent.getFullName() + name);
			city.setParent(parent);
			if (json.has("l")) {
				city.setChildren(parseCityJson(city,
						json.getJSONArray("l")));
			}
			list.add(city);
		}
		return list;
	}

	/**
	 * 加载在线城市列表
	 */
	public void loadOnlineCityPlist() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				if (mLoadListener != null) {
					mLoadListener.onPreLoad();
				}
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				String url = OnlineServices.getCityPlist();
				OnlineCityInfo info = new DataAcquirer<OnlineCityInfo>().get(url, new OnlineCityInfo());
				if (info != null && info.isAvailable()) {
					int version = PreferencesController.getInt(PREF_CITYPLIST_VERSION, CITY_VERSION);
					if (version < CITY_VERSION) {
						PreferencesController.setInt(PREF_CITYPLIST_VERSION, CITY_VERSION);
						version = CITY_VERSION;
					}
					if (info.mCityVersion > version) {
						if (HttpUtils.storeFile(info.mCityUrl, FILENAME)) {
							PreferencesController.setInt(PREF_CITYPLIST_VERSION, info.mCityVersion);
						}
					}
				}
				loadCities();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (mLoadListener != null) {
					mLoadListener.onPostLoad(rootCity);
				}
			}
		}.execute();
	}

}
