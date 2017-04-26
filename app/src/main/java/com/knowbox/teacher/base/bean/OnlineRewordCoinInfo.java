package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

public class OnlineRewordCoinInfo extends BaseObject {

	public String mCoinRemainedCount;
	public String mStudentId;
	public String mGainedCoinCount;
	public OnlineActivityDetailInfo.ActivityTaskInfo taskInfo;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		try {
			if (isAvailable()) {
				JSONObject obj = json.optJSONObject("data");
				if (null != obj) {
					mCoinRemainedCount = obj.optString("coinRemainCount");
					mStudentId = obj.optString("studentID");
					mGainedCoinCount = obj.optString("gainedCoinCount");
					if (obj.has("tipsList")) {
						try {
							JSONObject tipsList = obj.optJSONObject("tipsList");
							if (null != tipsList) {
								JSONObject redPkg = tipsList.optJSONObject("redpkg");
								if (null != redPkg) {
									taskInfo = new OnlineActivityDetailInfo.ActivityTaskInfo(redPkg);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
