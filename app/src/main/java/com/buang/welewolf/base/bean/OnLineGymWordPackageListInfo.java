package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SummerRC on 16/10/21.
 * description: 词包首页对应的数据实体
 */
public class OnLineGymWordPackageListInfo extends BaseObject {
    public int remainTrainTime;         //今日剩余训练次数
    public int unlockedPackageCount;    //已解锁词包数
    public int controlledWordCount;     //已掌握单词数
    public int controlledWordPackageCount;     //已掌握单词数
    public boolean canTrain;            //是否有可训练的单词
    public int trainingWordCount;      //但掌握单词数量
    public GymWordPackageInfo errorPackage;

    public List<GymProductInfo> productList;        // 指购买训练次数这个产品，每个产品买到的训练次数不一样，目前默认只取第一个值
    public List<GymWordPackageInfo> packageList;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        if (isAvailable() && json.has("data")) {
            JSONObject object = json.optJSONObject("data");
            if (null != object) {
                canTrain = object.optBoolean("canTrain");
                remainTrainTime = object.optInt("remainTrainTime");
                unlockedPackageCount = object.optInt("unlockedPackageCount");
                controlledWordCount = object.optInt("controledWordCount");
                controlledWordPackageCount = object.optInt("controlleedPackageCount");
                trainingWordCount = object.optInt("trainingWordCount");

                JSONObject errorPackageObject = object.optJSONObject("errorPackage");
                if (errorPackageObject != null && errorPackageObject.has("wordPackageID")) {
                    errorPackage = new GymWordPackageInfo();
                    errorPackage.wordPackageID = errorPackageObject.optInt("wordPackageID");
                    errorPackage.wordPackageName = errorPackageObject.optString("wordPackageName");
                    errorPackage.imgUrl = errorPackageObject.optString("imgUrl");
                    errorPackage.totalWordCount = errorPackageObject.optInt("totalWordCount");
                    errorPackage.unlockedWordCount = errorPackageObject.optInt("unlockedWordCount");
                    errorPackage.allUnLocked = errorPackageObject.optInt("allUnLocked");
                    errorPackage.level = errorPackageObject.optInt("level");
                    errorPackage.redIcon = errorPackageObject.optInt("redIcon");
                    errorPackage.levelName = errorPackageObject.optString("levelName");
                }

                productList = new ArrayList<GymProductInfo>();
                JSONArray productArray = object.optJSONArray("productList");
                if (productArray != null && productArray.length() > 0) {
                    for (int i = 0; i < productArray.length(); i++) {
                        GymProductInfo gymProductInfo = new GymProductInfo();
                        JSONObject productItem = productArray.optJSONObject(i);
                        gymProductInfo.pid = productItem.optInt("pid");
                        gymProductInfo.consume_diamond_count = productItem.optInt("consumeDiamondCount");
                        gymProductInfo.incr_train_count = productItem.optInt("increTrainCount");
                        gymProductInfo.type = productItem.optString("type");
                        productList.add(gymProductInfo);
                    }
                }

                packageList = new ArrayList<GymWordPackageInfo>();
                JSONArray packageArray = object.optJSONArray("sysPackageList");
                if (packageArray != null && packageArray.length() > 0) {
                    for (int i = 0; i < packageArray.length(); i++) {
                        GymWordPackageInfo packageInfo = new GymWordPackageInfo();
                        JSONObject packageItem = packageArray.optJSONObject(i);
                        packageInfo.wordPackageID = packageItem.optInt("wordPackageID");
                        packageInfo.wordPackageName = packageItem.optString("wordPackageName");
                        packageInfo.imgUrl = packageItem.optString("imgUrl");
                        packageInfo.totalWordCount = packageItem.optInt("totalWordCount");
                        packageInfo.controledWordCount = packageItem.optInt("controlledWordCount");
                        packageInfo.unlockedWordCount = packageItem.optInt("unlockedWordCount");
                        packageInfo.allUnLocked = packageItem.optInt("allUnLocked");
                        packageInfo.level = packageItem.optInt("level");
                        packageInfo.redIcon = packageItem.optInt("redIcon");
                        packageInfo.levelName = packageItem.optString("levelName");
                        packageList.add(packageInfo);
                    }
                }
            }
        }
    }

    public class GymProductInfo {
        // "pid": 1,
        // "type": "lock_wp",
        // "consume_diamond_count": 5,
        // "incr_train_count": 5
        public int pid;
        public String type;
        public int consume_diamond_count;
        public int incr_train_count;
    }

    public class GymWordPackageInfo {
        public int wordPackageID;
        public String wordPackageName;
        public String imgUrl;
        public int totalWordCount;
        public int controledWordCount;
        public int unlockedWordCount;
        public int allUnLocked; //整个题包中所有单词是否全部解锁了
        public int level;       //0:未知；1：一级；2：二级；3：三级
        public int redIcon;
        public String levelName;
    }


}
