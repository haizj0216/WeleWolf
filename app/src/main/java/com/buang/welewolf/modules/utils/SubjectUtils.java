package com.buang.welewolf.modules.utils;

import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SubjectUtils {

    public static final int SUBJECT_CODE_MATH = 0;
    public static final int SUBJECT_CODE_CHINESE = 1;
    public static final int SUBJECT_CODE_ENGLISH = 2;
    public static final int SUBJECT_CODE_PHYSICAL = 3;
    public static final int SUBJECT_CODE_CHEMICAL = 4;
    public static final int SUBJECT_CODE_BIOLOGY = 5;
    public static final int SUBJECT_CODE_HISTORY = 6;
    public static final int SUBJECT_CODE_GEOGRAPHY = 7;
    public static final int SUBJECT_CODE_POLITIAL = 8;
    public static final int SUBJECT_CODE_INFORMATION = 9;

    public static final int QUESTION_TYPE_CHOICE = 0;// 单选
    public static final int QUESTION_TYPE_MULTI_CHOICE = 1;// 多选
    public static final int QUESTION_TYPE_SUBJECTIVE = 2; // 解答
    public static final int QUESTION_TYPE_FILL_IN = 3;// 填空
    public static final int QUESTION_TYPE_COMPREHENSION = 6;// 阅读
    public static final int QUESTION_TYPE_COMPOSITION = 8; // 作文

    public static final int QUESTION_TYPE_CLOZE = 5; // 完形
    public static final int QUESTION_TYPE_TRANSLATE = 4; // 翻译
    public static final int QUESTION_TYPE_RATIONAL = 7; // 语法
    public static final int QUESTION_TYPE_ANSWERFILLIN = 9; // 拍照填空
    public static final int QUESTION_TYPE_SHORT_DIALOGUE = 11;//端对话
    public static final int QUESTION_TYPE_LONG_DIALGUE = 12;//长对话
    public static final int QUESTION_TYPE_MONOLOGUE = 13;//独白

    public static final int QUESTION_DIM_YINGHAN = 1;//英汉互译
    public static final int QUESTION_DIM_TINGLI = 2;//听力训练
    public static final int QUESTION_DIM_TIANKONG = 4;//选词填空
    public static final int QUESTION_DIM_WAKOONG = 3;//单词挖空
    public static final int QUESTION_DIM_QUANPIN = 5;//单词全拼

    public static final int KNOWLEDGE_DIM_YI = 1;//义
    public static final int KNOWLEDGE_DIM_YIN =2;//音
    public static final int KNOWLEDGE_DIM_XING = 3;//形
    public static final int KNOWLEDGE_DIM_JV = 4;//句


    public static int getQuestionTypeIcon(int type) {
        int id = 0;
        switch (type) {
            case QUESTION_DIM_YINGHAN:
                id = com.buang.welewolf.R.drawable.icon_question_type_yi;
                break;
            case QUESTION_DIM_TINGLI:
                id = com.buang.welewolf.R.drawable.icon_question_type_yin;
                break;
            case QUESTION_DIM_TIANKONG:
                id = com.buang.welewolf.R.drawable.icon_question_type_ju;
                break;
            case QUESTION_DIM_WAKOONG:
                id = com.buang.welewolf.R.drawable.icon_question_type_xing;
                break;
            case QUESTION_DIM_QUANPIN:
                id = com.buang.welewolf.R.drawable.icon_question_type_xing;
                break;
        }
        return id;
    }

    public static String getQuestionDim(int type) {
        String name = "";
        switch (type) {
            case QUESTION_DIM_YINGHAN:
                name = "英汉互译";
                break;
            case QUESTION_DIM_TINGLI:
                name = "听力训练";
                break;
            case QUESTION_DIM_TIANKONG:
                name = "选词填空";
                break;
            case QUESTION_DIM_WAKOONG:
                name = "单词挖空";
                break;
            case QUESTION_DIM_QUANPIN:
                name = "单词全拼";
                break;
        }
        return name;
    }

    public static int getKnowledgeDimIcon(int knowledgeDim) {
        int id = 0;
        switch (knowledgeDim) {
            case KNOWLEDGE_DIM_YI:
                id = com.buang.welewolf.R.drawable.icon_question_type_yi;
                break;
            case KNOWLEDGE_DIM_YIN:
                id = com.buang.welewolf.R.drawable.icon_question_type_yin;
                break;
            case KNOWLEDGE_DIM_JV:
                id = com.buang.welewolf.R.drawable.icon_question_type_ju;
                break;
            case KNOWLEDGE_DIM_XING:
                id = com.buang.welewolf.R.drawable.icon_question_type_xing;
                break;
        }
        return id;
    }

    public static String getNameByCode(int code) {
        String subjectName = "未知科目";
        switch (code) {
            case SUBJECT_CODE_MATH:
                subjectName = "数学";
                break;
            case SUBJECT_CODE_CHINESE:
                subjectName = "语文";
                break;
            case SUBJECT_CODE_ENGLISH:
                subjectName = "英语";
                break;
            case SUBJECT_CODE_PHYSICAL:
                subjectName = "物理";
                break;
            case SUBJECT_CODE_CHEMICAL:
                subjectName = "化学";
                break;
            case SUBJECT_CODE_BIOLOGY:
                subjectName = "生物";
                break;
            case SUBJECT_CODE_HISTORY:
                subjectName = "历史";
                break;
            case SUBJECT_CODE_GEOGRAPHY:
                subjectName = "地理";
                break;
            case SUBJECT_CODE_POLITIAL:
                subjectName = "政治";
                break;
            case SUBJECT_CODE_INFORMATION:
                subjectName = "信息技术";
                break;
            default:
                break;
        }
        return subjectName;
    }

    public static int[] getSortQuestionType() {
        return new int[]{
                SubjectUtils.QUESTION_TYPE_SHORT_DIALOGUE,
                SubjectUtils.QUESTION_TYPE_LONG_DIALGUE,
                SubjectUtils.QUESTION_TYPE_MONOLOGUE,
                SubjectUtils.QUESTION_TYPE_CHOICE, // 单选
                SubjectUtils.QUESTION_TYPE_MULTI_CHOICE, // 多选
                SubjectUtils.QUESTION_TYPE_FILL_IN, // 填空
                SubjectUtils.QUESTION_TYPE_TRANSLATE,
                SubjectUtils.QUESTION_TYPE_CLOZE, // 完形
                SubjectUtils.QUESTION_TYPE_COMPREHENSION, // 阅读
                SubjectUtils.QUESTION_TYPE_SUBJECTIVE, // 解答
                SubjectUtils.QUESTION_TYPE_RATIONAL, // 语法
                SubjectUtils.QUESTION_TYPE_COMPOSITION,// 作文
                SubjectUtils.QUESTION_TYPE_ANSWERFILLIN // 拍照填空
        };
    }

    public static boolean isMultiQuestionType(int questionType) {
        return questionType == QUESTION_TYPE_LONG_DIALGUE ||questionType == QUESTION_TYPE_CLOZE
                || questionType == QUESTION_TYPE_MONOLOGUE || questionType == QUESTION_TYPE_COMPREHENSION;
    }

    public static String getNameByQuestionType(int questionType) {
        String name = "未 知";
        switch (questionType) {
            case QUESTION_TYPE_CHOICE:
                name = "单 选";
                break;
            case QUESTION_TYPE_MULTI_CHOICE:
                name = "多 选";
                break;
            case QUESTION_TYPE_SUBJECTIVE:
                name = "解 答";
                break;
            case QUESTION_TYPE_FILL_IN:
                name = "填 写";
                break;
            case QUESTION_TYPE_TRANSLATE:
                name = "翻 译";
                break;
            case QUESTION_TYPE_CLOZE:
                name = "完 型";
                break;
            case QUESTION_TYPE_COMPREHENSION:
                name = "阅 读";
                break;
            case QUESTION_TYPE_RATIONAL:
                name = "语 法";
                break;
            case QUESTION_TYPE_COMPOSITION:
                name = "作 文";
                break;
            case QUESTION_TYPE_ANSWERFILLIN:
                name = "填 空";
                break;
            case QUESTION_TYPE_SHORT_DIALOGUE:
                name = "短对话";
                break;
            case QUESTION_TYPE_LONG_DIALGUE:
                name = "长对话";
                break;
            case QUESTION_TYPE_MONOLOGUE:
                name = "独 白";
                break;
            default:
                break;
        }
        return name;
    }

    /**
     * 获取题目类型（无空格）
     *
     * @param questionType
     * @return
     */
    public static String getQuestionType(int questionType) {
        String name = getNameByQuestionType(questionType);
        if (!TextUtils.isEmpty(name)) {
            name = name.replace(" ", "");
        }
        return name;
    }

    public static String GRADE_LIST[] = {"FirstGrade", "SecondGrade", "ThirdGrade", "FourthGrade", "FifthGrade", "SixthGrade",
            "FirstMiddle", "SecondMiddle", "ThirdMiddle", "FourthMiddle",
            "FirstHigh", "SecondHigh", "ThirdHigh"};

    public static List<NameValuePair> getGradePairs() {
        List<NameValuePair> items = new ArrayList<NameValuePair>();
        items.add(new BasicNameValuePair("小学一年级", ConstantsUtils.GRADE_FIRST_GRADE));
        items.add(new BasicNameValuePair("小学二年级", ConstantsUtils.GRADE_SECOND_GRADE));
        items.add(new BasicNameValuePair("小学三年级", ConstantsUtils.GRADE_THIRD_GRADE));
        items.add(new BasicNameValuePair("小学四年级", ConstantsUtils.GRADE_FOUR_GRADE));
        items.add(new BasicNameValuePair("小学五年级", ConstantsUtils.GRADE_FIVE_GRADE));
        items.add(new BasicNameValuePair("小学六年级", ConstantsUtils.GRADE_SIX_GRADE));

        items.add(new BasicNameValuePair("六年级", ConstantsUtils.GRADE_FIRST_MIDDLE));
        items.add(new BasicNameValuePair("七年级", ConstantsUtils.GRADE_SECOND_MIDDLE));
        items.add(new BasicNameValuePair("八年级", ConstantsUtils.GRADE_THIRD_MIDDLE));
        items.add(new BasicNameValuePair("九年级", ConstantsUtils.GRADE_FOUR_MIDDLE));

        items.add(new BasicNameValuePair("高一", ConstantsUtils.GRADE_FIRST_HIGH));
        items.add(new BasicNameValuePair("高二", ConstantsUtils.GRADE_SECOND_HIGH));
        items.add(new BasicNameValuePair("高三", ConstantsUtils.GRADE_THIRD_HIGH));
        return items;
    }

    public static String getGradeName(String grade) {
        List<NameValuePair> values = getGradePairs();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getValue().equals(grade)) {
                return values.get(i).getName();
            }
        }
        return "";
    }

    /**
     * 获取学段
     *
     * @param gradePart
     * @return
     * @author weilei
     */
    public static String getGrade(String gradePart) {
        if ("30".equals(gradePart)) {
            return "高中";
        } else if ("20".equals(gradePart)) {
            return "初中";
        } else if ("10".equals(gradePart)) {
            return "小学";
        }
        return "";
    }
}
