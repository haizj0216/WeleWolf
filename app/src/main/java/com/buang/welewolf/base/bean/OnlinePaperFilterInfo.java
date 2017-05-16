package com.buang.welewolf.base.bean;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlinePaperFilterInfo extends BaseObject {

    public List<PaperFilterItem> mCitys = new ArrayList<OnlinePaperFilterInfo.PaperFilterItem>();
    public List<PaperFilterItem> mYears = new ArrayList<OnlinePaperFilterInfo.PaperFilterItem>();
    public List<PaperFilterItem> mPaperTypes = new ArrayList<OnlinePaperFilterInfo.PaperFilterItem>();
    public List<PaperFilterItem> mGrades = new ArrayList<OnlinePaperFilterInfo.PaperFilterItem>();

    public PaperFilterItem sProvinceItem = new PaperFilterItem();

    public static final int TYPE_CITY  = 3;
    public static final int TYPE_PAPERTYPE = 1;
    public static final int TYPE_YEAR = 2;
    public static final int TYPE_GRADE = 4;

    public OnlinePaperFilterInfo() {
        mCitys.add(new PaperFilterItem("全部", "0", TYPE_CITY));
        mYears.add(new PaperFilterItem("全部", "0", TYPE_YEAR));
        mPaperTypes.add(new PaperFilterItem("全部", "0", TYPE_PAPERTYPE));
        mGrades.add(new PaperFilterItem("全部", "0", TYPE_GRADE));
    }

    @Override
    public void parse(JSONObject json) {
        // TODO Auto-generated method stub
        super.parse(json);

        if (isAvailable()) {
            try {
                JSONObject data = json.optJSONObject("data");
                JSONArray citys = data.optJSONArray("city");
                if (citys != null) {
                    mCitys.addAll(parseCity(citys, null));
                }
                JSONArray years = data.optJSONArray("year");
                if (years != null) {
                    for (int i = 0; i < years.length(); i++) {
                        JSONObject year = years.optJSONObject(i);
                        PaperFilterItem item = new PaperFilterItem();
                        item.mFilterName = year.optString("year");
                        item.mFilterID = year.optString("year");
                        item.mFilterType = TYPE_YEAR;
                        item.mDisplayName = item.mFilterName;
                        mYears.add(item);
                    }
                }

                JSONArray types = data.optJSONArray("type");
                if (types != null) {
                    for (int i = 0; i < types.length(); i++) {
                        JSONObject type = types.optJSONObject(i);
                        PaperFilterItem item = new PaperFilterItem();
                        item.mFilterID = type.optString("paperTypeId");
                        item.mFilterName = type.optString("name");
                        item.mFilterType = TYPE_PAPERTYPE;
                        item.mDisplayName = item.mFilterName;
                        mPaperTypes.add(item);
                    }
                }
                JSONArray grades = data.optJSONArray("grade");
                if (grades != null) {
                    for (int i = 0; i < grades.length(); i++) {
                        JSONObject grade = grades.optJSONObject(i);
                        PaperFilterItem item = new PaperFilterItem();
                        item.mFilterID = grade.optString("gradeId");
                        item.mFilterName = grade.optString("name");
                        item.mFilterType = TYPE_GRADE;
                        item.mDisplayName = item.mFilterName;
                        mGrades.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private List<PaperFilterItem> parseCity(JSONArray citys, PaperFilterItem parent) {
        List<PaperFilterItem> lists = new ArrayList<PaperFilterItem>();
        for (int i = 0; i < citys.length(); i++) {
            JSONObject city = citys.optJSONObject(i);
            PaperFilterItem item = new PaperFilterItem();
            item.mFilterID = city.optString("cityId");
            item.mFilterName = city.optString("cityName");
            item.mDisplayName = item.mFilterName;
            item.level = city.optInt("cityLevel");
            item.isSchoolProvince = city.optInt("isSchoolProvince") == 1;
            if (item.isSchoolProvince) {
                sProvinceItem.mFilterID = item.mFilterID;
                sProvinceItem.mFilterName = item.mFilterName;
                sProvinceItem.level = item.level;
            }
            if (parent != null) {
                item.mParentItem = parent;
            }
            item.mFilterType = TYPE_CITY;
            if (city.has("subs")) {
                JSONArray subs = city.optJSONArray("subs");
                item.mSubCitys = new ArrayList<PaperFilterItem>();
                item.mSubCitys.addAll(parseCity(subs, parent));
            }
            lists.add(item);
        }
        return lists;
    }

    public static class PaperFilterItem {

        public PaperFilterItem() {

        }

        public PaperFilterItem(String name, String value, int type) {
            mFilterName = name;
            mFilterID = value;
            mFilterType = type;
            mDisplayName = name;
        }

        public int mFilterType;

        public String mFilterID;
        public String mFilterName;
        public String mDisplayName;
        public boolean isSchoolProvince;
        public int level = 1;
        public List<PaperFilterItem> mSubCitys;
        public PaperFilterItem mParentItem;
    }
}
