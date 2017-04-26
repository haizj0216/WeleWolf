package com.knowbox.teacher.base.bean;


import com.hyena.framework.datacache.BaseObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 16/8/10.
 *
 */
public class OnlineTikuCategoryInfo extends BaseObject implements Serializable{

    private static final long serialVersionUID = -8331219080190286418L;
    public int mCategoryType;
    public String mCategoryTitle;
    public String mIcon;
    public String mIconSelect;

    public List<CategoryItem> mCategoryItems;
    public List<CategoryGradeItem> mCategoryGrades;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (null != data) {
            mCategoryType = data.optInt("type");
            mCategoryTitle = data.optString("title");
            mIcon = data.optString("icon");
            mIconSelect = data.optString("iconSelect");

            if (data.has("categoryList")) {
                mCategoryItems = new ArrayList<CategoryItem>();
                JSONArray categoryArray = data.optJSONArray("categoryList");
                for (int i = 0; i < categoryArray.length(); i++) {
                    JSONObject object = categoryArray.optJSONObject(i);
                    CategoryItem item = new CategoryItem();
                    item.categoryID = object.optString("categoryID");
                    item.categoryName = object.optString("categoryName");
                    item.subject = object.optInt("subject");
                    item.gradePart = object.optInt("gradePart");
                    item.questionCount = object.optInt("questionCount");
                    item.uesdQuestionCount = object.optInt("usedTime");
                    item.correctRate = object.optInt("rightRate");
                    item.courseSectionID = object.optString("courseSectionID");
                    item.sectionName = object.optString("sectionName");
                    mCategoryItems.add(item);
                }
            }

            if (data.has("gradeList")) {
                mCategoryGrades = new ArrayList<CategoryGradeItem>();
                JSONArray gradeArray = data.optJSONArray("gradeList");
                for (int i = 0; i < gradeArray.length(); i++) {
                    JSONObject object = gradeArray.optJSONObject(i);
                    CategoryGradeItem item = new CategoryGradeItem();
                    item.mGradeId = object.optString("id");
                    item.mGradeName = object.optString("text");
                    mCategoryGrades.add(item);
                }
            }
        }
    }

    public class CategoryItem {
        public String categoryID;
        public String categoryName;
        public int uesdQuestionCount;
        public int correctRate;
        public int subject;
        public int gradePart;
        public int questionCount;
        public int selectCount;
        public String courseSectionID;
        public String sectionName;
    }

    public class CategoryGradeItem {
        public String mGradeId;
        public String mGradeName;
        public boolean isSelect;
    }

}
