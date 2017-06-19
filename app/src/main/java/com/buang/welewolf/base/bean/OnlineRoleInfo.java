package com.buang.welewolf.base.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by weilei on 17/6/19.
 */

public class OnlineRoleInfo implements Serializable {

    public String userName;
    public String userID;
    public String userPhoto;
    public boolean isOwner;
    public int userIndex;
    public int roleType;
    public boolean isAnonymous;
    public boolean isLock;
    public boolean isEmpty;
    public boolean isReady;
    public boolean isRaise;
    public boolean isDeath;
    public boolean isMyVoice;
    public boolean isMySelf;
    public boolean isVillage;


//    player = {
//        userName = "法官"; // 用户昵称
//        userID = "10000";   // 用户ID
//        userPhoto = "http://7xkdpi.com2.z0.glb.qiniucdn.com/28229_20160627180262_9580";//用户头像
//        isOwner = "0";      // 是否是房主
//        userIndex = "0";    // 座位编号  0 是法官   1-12 是玩家  -1  是观众
//        roleType = 2;       // 游戏角色  -1 未开始  即未分配   其他对应枚举
//    }
//    isAnonymous = "0";  //端上可以根据房间属性  判断
//    isLock = "1";       // 座位是否关闭
//    isEmpty = "0";      // 这个字段 也可以让端上根据player跟islock 字段  判断
//    isReady = "0";      // 准备状态  0  未准备  1 准备
//    isRaise = "0";      // 举手
//    isDeath = "0";      //死亡  0 未死亡 1 死亡
//    isMyVoice = "0";    //排麦  0 别人麦   1自己的麦
//    isMySelf = "1";     //我自己  这个也可以端上自己根据userID 判定
//    isVillage = "0";    //是否是村长  0 不是 1 是

    public void parseInfo(JSONObject object) {
        JSONObject player = object.optJSONObject("player");
        if (player != null) {
            userName = player.optString("userName");
            userID = player.optString("userID");
            userPhoto = player.optString("userPhoto");
            isOwner = player.optInt("isOwner") == 1;
            userIndex = player.optInt("userIndex");
            roleType = player.optInt("roleType");
        }
        isAnonymous = object.optInt("isAnonymous") == 1;
        isLock = object.optInt("isLock") == 1;
        isEmpty = object.optInt("isEmpty") == 1;
        isReady = object.optInt("isReady") == 1;
        isRaise = object.optInt("isRaise") == 1;
        isDeath = object.optInt("isDeath") == 1;
        isMyVoice = object.optInt("isMyVoice") == 1;
        isMySelf = object.optInt("isMySelf") == 1;
        isVillage = object.optInt("isVillage") == 1;
    }
}
