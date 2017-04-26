package com.knowbox.teacher.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 17/4/12.
 */
public class OnlineTestStudentListInfo extends BaseObject implements Serializable {

    private static final long serialVersionUID = 2543720628511523678L;
    public String classId;
    public String className;
    public String classImage;
    public String joinerCount;
    public String avgScore;
    public String highErrorWordCount;
    public int status = -1;//0-未开始，1-进行中，2-测验结束

    public List<OnlineStudentInfo> mJoinList;
    public List<OnlineStudentInfo> mSupplementList;
    public List<OnlineStudentInfo> mUnJoinList;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            classId = data.optString("classID");
            className = data.optString("className");
            classImage = data.optString("image");
            joinerCount = data.optString("joiner");
            avgScore = data.optString("avgScore");
            status = data.optInt("status");
            highErrorWordCount = data.optString("highErrorWordCount");

            if (data.has("joinList")) {
                JSONArray joinListArray = data.optJSONArray("joinList");
                if (null != joinListArray && joinListArray.length() > 0) {
                    mJoinList = new ArrayList<OnlineStudentInfo>();
                    for (int i = 0; i < joinListArray.length(); i++) {
                        OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                        studentInfo.parseInfo(joinListArray.optJSONObject(i));
                        studentInfo.rankIndex = i + 1;
                        mJoinList.add(studentInfo);
                    }
                }
            }

            if (data.has("supplementList")) {
                JSONArray supplementListArray = data.optJSONArray("supplementList");
                if (null != supplementListArray && supplementListArray.length() > 0) {
                    mSupplementList = new ArrayList<OnlineStudentInfo>();
                    for (int i = 0; i < supplementListArray.length(); i++) {
                        OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                        studentInfo.parseInfo(supplementListArray.optJSONObject(i));
                        if (null == mJoinList) {
                            studentInfo.rankIndex = i + 1;
                        }else {
                            studentInfo.rankIndex = mJoinList.size() + i  + 1;
                        }
                        mSupplementList.add(studentInfo);
                    }
                }
            }

            if (data.has("unJoinList")) {
                JSONArray unJoinListArray = data.optJSONArray("unJoinList");
                if (null != unJoinListArray && unJoinListArray.length() > 0) {
                    mUnJoinList = new ArrayList<OnlineStudentInfo>();
                    for (int i = 0; i < unJoinListArray.length(); i++) {
                        OnlineStudentInfo studentInfo = new OnlineStudentInfo();
                        studentInfo.parseInfo(unJoinListArray.optJSONObject(i));
                        mUnJoinList.add(studentInfo);
                    }
                }
            }

        }
    }

    public boolean isNoStart() {
        return status == 0;
    }

}
