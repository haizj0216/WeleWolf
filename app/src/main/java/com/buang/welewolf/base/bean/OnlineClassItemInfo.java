package com.buang.welewolf.base.bean;

import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.buang.welewolf.base.database.bean.HomeworkItem;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 16/7/7.
 */
public class OnlineClassItemInfo {

    public List<ClassInfoItem> mClassInfoItems = new ArrayList<ClassInfoItem>();

    public List<NameValuePair> mStatisticData = new ArrayList<NameValuePair>();

    public List<HomeworkItem> mHomeworkItems = new ArrayList<HomeworkItem>();
}
