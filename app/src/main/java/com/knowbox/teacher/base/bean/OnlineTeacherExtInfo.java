package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 15/8/13.
 */
public class OnlineTeacherExtInfo extends BaseObject {

    public List<TeacherExtInfo> mInfos = new ArrayList<TeacherExtInfo>();

    public int mAuthStatus;
    public String mAuthDesc;
    public String mAuthTime;
    public String mAuthError;
    public String mAuthInviteOriginUserName;//认证：发起邀请的人的用户名
    public String mAuthUrl;//中间页的url
    public boolean hasQualification;
    public String mAuthRule;

    //等级信息
    public String mCoin;
    public String mLevel;
    public String mTotalExp;
    public String mCurExp;
    public String mLevelExp;
    public String mStartExp;
    public String mCoinMallMsg;
    public String mCoinMallUrl;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        JSONArray arrays = data.optJSONArray("ext");
        for (int i = 0; i < arrays.length(); i++) {
            JSONObject object = arrays.optJSONObject(i);
            TeacherExtInfo info = new TeacherExtInfo();
            info.extID = object.optInt("extID");
            info.mTitle = object.optString("title");
            info.mDesc = object.optString("txt");
            info.mHasNew = object.optInt("hasNew");
            info.mUrl = object.optString("url");
            info.order = object.optInt("order");
            info.teacherName = object.optString("teacherName");
            info.inviteCode = object.optString("inviteCode");
            info.icon = object.optString("icon");
            mInfos.add(info);
        }
        JSONObject certificate = data.optJSONObject("certificate");
        if (certificate != null) {
            mAuthStatus = certificate.optInt("certificateStatus");
            mAuthDesc = certificate.optString("certificateDesc");
            mAuthTime = certificate.optString("certificateTime");
            mAuthError = certificate.optString("certificateError");
            mAuthUrl = certificate.optString("certificateUrl");
            mAuthInviteOriginUserName = certificate.optString("inviteOriginUserName");
            hasQualification = certificate.optInt("isClose") == 0;
            mAuthRule = certificate.optString("certificateTxt");
        }
        if (data.has("userExtInfo")) {
            JSONObject extInfo = data.optJSONObject("userExtInfo");
            if (null != extInfo) {
                mCoin = extInfo.optString("coin");
                mLevel = extInfo.optString("level");
                mTotalExp = extInfo.optString("totalExp");
                mCurExp = extInfo.optString("curExp");
                mLevelExp = extInfo.optString("levelExp");
                mStartExp = extInfo.optString("startExp");
                mCoinMallMsg = extInfo.optString("coinMallMsg");
                mCoinMallUrl = extInfo.optString("coinMallUrl");
            }
        }
    }

    public static class TeacherExtInfo {
        public int extID;
        public String mTitle = "";
        public String mDesc = "";
        public int mHasNew;
        public String mUrl;
        public String inviteCode;
        public String teacherName;
        public int order;
        public String icon;
    }

}
