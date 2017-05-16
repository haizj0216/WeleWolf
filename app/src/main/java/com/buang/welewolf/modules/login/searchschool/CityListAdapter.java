package com.buang.welewolf.modules.login.searchschool;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buang.welewolf.base.bean.CityModel;
import com.hyena.framework.app.adapter.SingleTypeAdapter;

/**
 * 选择城市列表适配器
 * 
 * @author Jet.Z
 *
 */
public class CityListAdapter extends SingleTypeAdapter<CityModel> {

	public CityListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {

		Holder holder = null;
		if (null == convertView) {
			convertView = View.inflate(mContext,
					com.buang.welewolf.R.layout.dialog_choose_city_item, null);
			holder = new Holder();
			holder.txt = (TextView) convertView
					.findViewById(com.buang.welewolf.R.id.choose_city_item_txt);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		CityModel item = getItem(pos);
		holder.txt.setText(item.getName());
		return convertView;
	}

	class Holder {
		public TextView txt;
	}
}
