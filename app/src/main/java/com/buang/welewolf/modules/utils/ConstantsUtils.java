package com.buang.welewolf.modules.utils;

import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.VersionUtils;

/**
 * Created by weilei on 16/5/16.
 */
public class ConstantsUtils {

    public static final String BUNK_PUBLISHER_NAME = "publisherName";
    public static final String BUNK_PUBLISHER_VALUE = "publisherValue";
    public static final String BUNK_REQUIREBOOK_NAME = "requirebookName";
    public static final String BUNK_REQUIREBOOK_VALUE = "requirebookValue";

    public static final String PREFS_ISNEW_TEACHER = "prefs_isnew_teacher";
    public static final String PREFS_NEW_CLASS_TIP = "prefs_new_class_tip";
    public static final String PREFS_VIRTUAL_HOMEWORK = "prefs_virtual_homework";
    public static final String PREFS_LOGIN_SUBJECT = "prefs_login_subject";
    public static final String PREFS_LOGIN_GRADEPART = "prefs_login_gradepart";
    public static final String PREFS_LOGIN_ISRECOMMEND = "prefs_login_isrecommend";
    public static final String PREFS_DOWNLOAD_TIP = "prefs_download_tip";
    public static final String PREFS_SECTION_EXPAND_ID = "prefs_section_expand_id";
    public static final String PREFS_SUB_SECTION_EXPAND_ID = "prefs_sub_section_expand_id";
    public static final String PREFS_NEW_TASK_CLOSE_COUNT = "prefs_task_close_count";
    public static final String PREFS_PDF_EMAIL = "prefs_pdf_email";
    public static final String PREFS_HOMEWORK_PREVIEW_GUIDE = "prefs_homework_preview_guide";

    public static final String SHRAE_IMAGE_URL = "http://7xjnvd.com2.z0.glb.qiniucdn.com/new_teacher.png";
    public static final String HZ_APP_DOWNLOAD_URL = "http://app.knowbox.cn/hz/";

    public static final String FORCE_SETTING = "is_force_setting";
    public static final String IS_NEWUSER = "is_new_user";
    public static final String CLAZZ_NAME = "com_knowbox_wordteacher_clazzname";

    public static final String IS_VIRTUAL = "is_virtual_visiter";
    public static final String SUBJECT_FROM = "subject_from";

    public static final String PREF_KEY_CONFIG_DEBUG_API = "knowbox_teacher_debug_api";
    public static final String PREF_KEY_CONFIG_DEBUG_APIPREFIX = "knowbox_teacher_debug_apiprefix";

    public static final String GREETING_IMAGEURL = "greeting_image_url";
    public static final String GREETING_JUMPURL = "greeting_image_jumpurl";
    public static final String GREETING_BOUTH_TIME = "greeting_image_bouth_time";
    public static final String GREETING_IMG_FIELPATH = "greeting_filepath";
    public static final String NEWTASK_URL = "new_task_url";
    public static final String MAKEOUT_SUCCESS_DIALOG = "makeout_homework_success_dialog";
    public static final String TOTAL_QUESTION_NUM = "total_question_number";
    public static final String MAX_QUESTION_NUM_PREHOMEWORK = "max_question_num_perhomework";
    public static final String TIKU_BASIC_CATEGORY_TYPE = "tiku_basic_category_type";
    public static final String KEY_APP_OPENED_COUNT = "key_app_opened_count";
    public static final String KEY_REDPKG_TASK_FINISH_ACTIVITYID = "key_redpkg_task_finish_activityid";

    //预习题包章节id
    public static final String TIKU_PREVIEW_SECTIONID = "tiku_preview_package_section_id";

    /**
     * 错题本题目筛选维度
     */

    public final static String WRONG_BOOK_FILTER_TIME = "wrong_book_filter_by_time";
    public final static String WRONG_BOOK_FILTER_RATE = "wrong_book_filter_by_rate";

    //题库基础练习和题包预习科目类型
    public static final int TIKU_CATEGORY_TYPE_NO_SAVE = -1000;
    public static final int TIKU_CATEGORY_TYPE_HAVENOT = 0;
    public static final int TIKU_CATEGORY_TYPE_CHINESE = 1;
    public static final int TIKU_CATEGORY_TYPE_MATH = -1;
    public static final int TIKU_CATEGORY_TYPE_ENGLISH = 2;


    //题组类型
    public final static int BASIC_TYPE_ASSIST = 1; // 教辅
    public final static int BASIC_TYPE_KNOWLEDGE = 2; // 知识点
    public final static int BASIC_TYPE_PAPER = 3; // 试卷
    public final static int BASIC_TYPE_PERSONALGROUP = 4; // 个人群组
    public final static int BASIC_TYPE_PHOTO = 5; // 拍照
    public final static int BASIC_TYPE_SUPERSEARCH = 6; // 超级搜索
    public final static int BASIC_TYPE_SHARE = 7; // 分享
    public final static int BASIC_TYPE_SYN = 8; // 同步
    public final static int BASIC_TYPE_TOPIC = 9; // 专题
    public final static int BASIC_TYPE_ISSUE = 10; //
    public final static int BASIC_TYPE_HOMEWORK = 11; // 作业
    public final static int SEARCH_TYPE_QUESTION = 21; // 搜索题目
    public final static int SEARCH_TYPE_KNOWLEDGE = 22; // 搜索知识点
    public final static int SEARCH_TYPE_ISSUE = 23; //
    public final static int SEARCH_TYPE_PAPER = 24; // 搜索试卷
    public final static int GROUP_TYPE_PGC = 31; // 推荐作业
    public final static int GROUP_TYPE_KNOWLEDGE = 32; // 题包知识点
    public final static int GROUP_TYPE_ISSUE = 33; // 热点
    public final static int GROUP_TYPE_SCHOOLPAPER = 34; // 名校试卷
    public final static int GROUP_TYPE_GOVPAPER = 35; // 精品试卷
    public final static int SYNC_SECTION = 36;//推荐列表同步章节
    public final static int TEACHING_ASSIST = 37;//推荐中教辅列表
    public final static int KNOWLEDGE_QUESTIONTYPE = 38;//综合复习中的知识点和题型
    public final static int GROUP_TYPE_MATH_PREVIEW = 40;//数学预习题包
    public final static int GROUP_TYPE_ENGLISH_LISTEN = 42;//英语听力训练
    public final static int GROUP_TYPE_HOMEWORK = 50;//收藏的作业
    public final static int BASIC_TYPE_ERRORBOOK = 51;//来源于错题本

    /**
     * 题目来源
     */
    public final static int FROM_TYPE_BASIC = 1;//基础分类
    public final static int FROM_TYPE_SHARE = 2;//分享
    public final static int FROM_TYPE_RECOMMEND = 3;//推荐
    public final static int FROM_TYPE_CHINESEBASIC = 4;//语基
    public final static int FROM_TYPE_HOMEWORK = 5;//作业收藏
    public final static int FROM_TYPE_ERRORBOOK = 6;//错题本

    public final static int PAPER_TYPE_GROUP_SYNC = 1;
    public final static int PAPER_TYPE_GROUP_ALL = 2;

    /**单词部落**/

    public final static String WEB_USER_AGENT = "AppOS/android AppFrom/twordclan AppVersion/"+ VersionUtils.getVersionCode(BaseApp.getAppContext());

    public final static String KEY_CLASSINFOITEM = "classItem";
    public final static String KEY_UNITINFO = "unitInfo";
    public final static String KEY_WORDINFO = "wordInfo";
    public final static String KEY_STUDENTINFO = "studentInfo";
    public final static String KEY_ASSIGN_UNITLIST = "assignUnits";
    public final static String KEY_ASSIGN_WORDLIST = "assign_words";
    public final static String KEY_ASSIGN_DIMLIST = "assign_dims";
    public static final String KEY_ASSIGN_TYPE = "assign_matches_type";
    public final static String KEY_SUBMIT_START_DATE = "submit_start_date";
    public final static String KEY_SUBMIT_END_DATE = "submit_end_date";
    public final static String KEY_SUBMIT_JUMPURL = "submit_jump_url";
    public final static String KEY_SUBMIT_COMP = "submit_competition";
    public final static String KEY_SUBMIT_GAME_TYPE = "submit_game_type";
    public final static String KEY_SUBMIT_CLASS_NAMES = "submit_class_names";
    public final static String KEY_BUNDLE_MATCHID = "bundle_match_id";
    public final static String KEY_BUNDLE_TTTLE = "bundle_match_title";
    public final static String KEY_BUNDLE_TIME = "bundle_match_time";
    public final static String KEY_BUNDLE_CLASSNAME = "bundle_match_classname";
    public final static String KEY_BUNDLE_STUDENTCOUNT = "bundle_match_studentcount";
    public final static String KEY_BUNDLE_CLASSID = "bundle_match_classid";
    public final static String KEY_BUNDLE_STATUS = "bundle_match_status";
    public final static String KEY_BUNDLE_CHAM_ITEM = "bundle_match_item";
    public final static String KEY_BUNDLE_QUESTION_LIST = "bundle_question_list";
    public final static String KEY_BUNDLE_CONTACT_INFO = "bundle_contact_info";

    public final static int MATCH_STATUS_NOTSTART = 1;
    public final static int MATCH_STATUS_FINISHED = 5;
    public final static String KEY_BUNDLE_START_CLOCK = "bundle_start_clock";
    public final static String KEY_BUNDLE_END_CLOCK = "bundle_end_clock";
    public final static String KEY_BUNDLE_MINDAY = "bundle_min_day";
    public final static String KEY_BUNDLE_RELEASE_GAME_TYPE = "bundle_release_game_type";
    public final static String KEY_BUNDLE_MATCH_TYPE = "bundle_match_type";
    public final static String KEY_BUNDLE_MATCH_DETAIL = "bundle_match_detail";
    public final static String KEY_BUNDLE_MATCH_REPORT = "bundle_match_report";

    public final static int LEARN_STATUS_LEARNED = 1;//已熟背
    public final static int LEARN_STATUS_LEARNING = 2;//背诵中
    public final static int LEARN_STATUS_UNLEARN = 3;//未背诵

    public final static String PREFS_SELECT_CLASS = "pref_select_class";
    public final static String PREFS_GRANT_COIN = "pref_grant_coin";
    public final static String PREFS_SUBMIT_START = "submit_start";
    public final static String PREFS_SUBMIT_END = "submit_end";

    public static final int TYPE_DATA_PICKER_START = 1;
    public static final int TYPE_DATA_PICKER_END = 2;
    public static final int TYPE_TIME_PICKER_START = 3;
    public static final int TYPE_TIME_PICKER_END = 4;
    public static final int TYPE_DATE_SINGLE_TEST_GAME_PICK = 5;

    public static final String GRADE_PART_GRADE = "10";
    public static final String GRADE_PART_MIDDLE = "20";
    public static final String GRADE_PART_HIGH = "30";
    public static final String GRADE_FIRST_GRADE = "1";
    public static final String GRADE_SECOND_GRADE = "2";
    public static final String GRADE_THIRD_GRADE = "3";
    public static final String GRADE_FOUR_GRADE = "4";
    public static final String GRADE_FIVE_GRADE = "5";
    public static final String GRADE_SIX_GRADE = "6";
    public static final String GRADE_FIRST_MIDDLE = "11";
    public static final String GRADE_SECOND_MIDDLE = "12";
    public static final String GRADE_THIRD_MIDDLE = "13";
    public static final String GRADE_FOUR_MIDDLE = "14";
    public static final String GRADE_FIRST_HIGH = "21";
    public static final String GRADE_SECOND_HIGH = "22";
    public static final String GRADE_THIRD_HIGH = "23";

}
