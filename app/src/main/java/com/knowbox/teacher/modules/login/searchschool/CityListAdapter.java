package com.knowbox.teacher.modules.login.searchschool;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.CityModel;

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
					R.layout.dialog_choose_city_item, null);
			holder = new Holder();
			holder.txt = (TextView) convertView
					.findViewById(R.id.choose_city_item_txt);
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
