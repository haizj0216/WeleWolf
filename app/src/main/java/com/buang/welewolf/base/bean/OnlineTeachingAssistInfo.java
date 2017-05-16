package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;
import com.buang.welewolf.base.bean.OnlineTeachingMaterialInfo.ChooseItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineTeachingAssistInfo extends BaseObject {

	public List<ChooseItem> mMaterials = new ArrayList<ChooseItem>();

	@Override
	public void parse(JSONObject json) {
		// TODO Auto-generated method stub
		super.parse(json);
		try {
			JSONArray array = json.optJSONArray("data");
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				ChooseItem mMaterial = new ChooseItem(
						object.optString("teachingName"),
						object.optString("teachingID"));
				ChooseItem mAssist = new ChooseItem(
						object.optString("teachingAssistName"),
						object.optString("teachingAssistID"));
				mMaterial.mType = ChooseItem.ITEM_TYPE_TEACH;
				mAssist.mType = ChooseItem.ITEM_TYPE_ASSIST;
				mAssist.isSync = object.optInt("isSync") == 1;
				mAssist.isArea = object.optInt("isArea");
				mAssist.mParent = new ChooseItem(
						object.optString("textBookName"),
						object.optString("textBookID"));
//				if (!mMaterials.contains(mMaterial)) {
//					mMaterial.mSubList.add(mAssist);
//					mMaterials.add(mMaterial);
//				} else {
//					ChooseItem material = mMaterials.get(mMaterials
//							.indexOf(mMaterial));
//					if (material != null)
//						material.mSubList.add(mAssist);
//				}
				mMaterials.add(mAssist);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
