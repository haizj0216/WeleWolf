package com.buang.welewolf.base.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by weilei on 17/6/1.
 */

public class ContactInfo implements Serializable {

    public String name;
    public String head;
    public String sign;
    public int sex;
    public int level;
    public int exp;
    public RecordInfo recordInfo;
    public int popularity;
    public List<GiftInfo> mGifts;

    public class RecordInfo {
        public int win;
        public int lost;
        public int total;
        public float rate;
    }
}
