package com.knowbox.teacher.modules.utils;

import com.hyena.framework.utils.BaseApp;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

public class UmengConstant {
	/**
	 * key名称
	 */
	public static final String RESULT = "result";
	public static final String REQ_TIME = "reqTime";
//	public static final String RESULT_CODE = "result_code";
//	public static final String KEY_REGIST_STEP = "regist_step";
//	public static final String REGIST_STEP_TYPE = "regist_step_type";
	public static final String LOGIN_USER_INFO_SUBJECT = "login_user_subject";
	public static final String LOGIN_USER_INFO_SCHOOL = "login_user_school";
//	public static final String SUBMIT_HOMEWORK_DATE = "submit_homework_date";
//	public static final String SUBMIT_HOMEWORK_DEVICE = "submit_homework_device";
	public static final String KEY_MESSAGE = "message_phone";
	
//	public static final String KEY_QUESTION_TYPE_CHOICE = "question_type_choice";// 单选
//	public static final String KEY_QUESTION_TYPE_MULTI_CHOICE = "question_type_multichoice";// 多选
//	public static final String KEY_QUESTION_TYPE_SUBJECTIVE = "question_type_subjective"; // 解答
//	public static final String KEY_QUESTION_TYPE_FILL_IN = "question_type_fillin";// 填空
//	public static final String KEY_QUESTION_TYPE_COMPREHENSION = "question_type_comprehension";// 阅读
//	public static final String KEY_QUESTION_TYPE_COMPOSITION = "question_type_composition"; // 作文
//
//	public static final String KEY_QUESTION_TYPE_CLOZE = "question_type_cloze"; // 完形
//	public static final String KEY_QUESTION_TYPE_TRANSLATE = "question_type_translate"; // 翻译
//	public static final String KEY_QUESTION_TYPE_RATIONAL = "question_type_rational"; // 语法
//	public static final String KEY_QUESTION_TYPE_ANSWERFILLIN = "question_type_answerfillin"; // 拍照填空
//	public static final String KEY_QUESTION_TYPE = "question_type"; // 拍照填空
	public static final String KEY_AUTH_CLICK = "auth_click";
	public static final String KEY_AUTH_UPLOAD_SUCCESS = "auth_submit_hz";
	public static final String KEY_AUTH_CAMERA = "auth_camera";
	public static final String KEY_AUTH_CAMERA_RESULT = "auth_camera_result";
//	public static final String KEY_CORRECT_UPLOAD_PICTURE = "correct_upload_picture";

	/**
	 * key = RESULT的取值
	 */
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
//	public static final String TIMEOUT = "timeout";

	/**
	 * 事件名称
	 */
	public static final String LOGIN = "EVENT_PHONE_LOGIN";
	public static final String REGIST = "EVENT_PHONE_REGIST";
	public static final String REGIST_GET_SCHOOL_LIST = "EVENT_PHONE_REGIST_GET_SCHOOL";
//	public static final String PAGE_ACCESS_COUNT = "EVENT_PHONE_PAGE_ACCESS_COUNT";
//	public static final String UPLOAD_CORRECT = "EVENT_PHONE_UPLOAD_CORRECT";
//	public static final String SUBMIT_HOMEWORK = "EVENT_PHONE_SUBMIT_HOMEWORK";
//	public static final String REGIST_STEP = "EVENT_PHONE_REGIST_STEP";
	public static final String LOGIN_USERINFO = "EVENT_PHONE_REGIST_USER";
	public static final String EVENT_PHONE_SMS = "EVENT_PHONE_SMS";
//	public static final String EVENT_PHOTOQUESTION = "EVENT_PHONE_PHOTO_QUESTION";
//	public static final String EVENT_PHOTOQUESTION_UPLOAD = "EVENT_PHONE_PHOTO_QUESTION_UPLOAD";
	public static final String EVENT_CLASS_ADD_BACK = "b_class_add_back";//,创建班级群返回
//	public static final String EVENT_CLASS_ADDSTU_LIST = "b_class_addstu_list";//,学生列表中添加
//	public static final String EVENT_CLASS_ADDSTU_SHARE = "b_class_addstu_share";//,分享班级
	public static final String EVENT_CLASS_FINISH = "b_class_finish";//,创建班级群完成
//	public static final String EVENT_CLASS_MANAGE = "b_class_manage";//,班级管理
//	public static final String EVENT_CLASS_ORDER = "b_class_order";//,排序
//	public static final String EVENT_CLASS_ORDER_LASTRIGHT = "b_class_order_lastright";//,最近正确率排序
//	public static final String EVENT_CLASS_ORDER_LASTSUBMIT = "b_class_order_lastSubmit";//,最近提交时间排序
//	public static final String EVENT_CLASS_ORDER_MONTHRIGHT = "b_class_order_monthright";//,月平均正确率排序
//	public static final String EVENT_EVENTS = "b_events";//,活动
//	public static final String EVENT_FORGETPW_SMS = "b_forgetpw_sms";//,忘记密码获取验证码
	public static final String EVENT_FORGETPWD = "b_forgetpwd";//,忘记密码
//	public static final String EVENT_HOMEWORK_COPY = "b_homework_copy";//,复制作业
//	public static final String EVENT_HOMEWORK_DELETE = "b_homework_delete";//,删除作业
//	public static final String EVENT_HOMEWORK_DIPLAYALL = "b_homework_diplayall";//,显示全部
//	public static final String EVENT_HOMEWORK_DISPLAYCORRECT = "b_homework_diplaycorrect";//,显示批改
//	public static final String EVENT_HOMEWORK_MODIFY = "b_homework_modify";//,修改作业截止时间
//	public static final String EVENT_INVITE = "b_invite";//,邀请
//	public static final String EVENT_AWARD = "b_award";//奖励
	public static final String EVENT_ME_BIRTH = "b_me_birth";//,生日
	public static final String EVENT_ME_FACE = "b_me_face";//,头像
	public static final String EVENT_ME_NAME = "b_me_name";//,姓名
	public static final String EVENT_ME_SCHOOL = "b_me_school";//,学校
	public static final String EVENT_ME_SEX = "b_me_sex";//,性别
	public static final String EVENT_MESSAGE_NOTE_OFF = "b_message_note_off";//,关闭提醒
	public static final String EVENT_MESSAGE_NOTE_ON = "b_message_note_on";//,开启提醒
	public static final String EVENT_MODIFYPWD = "b_modifypwd";//,修改密码
//	public static final String EVENT_QUESTIONBANK_BACK = "b_questionbank_back";//,题库“返回”
//	public static final String EVENT_SERVICE = "b_service";//,客服小助手
	public static final String EVENT_HELP = "b_help";//,帮助
	public static final String EVENT_SETTINGS= "b_settings";
	public static final String EVENT_SHARE_COPYLINKER = "b_share_copylinker";//,复制链接分享
	public static final String EVENT_SHARE_QQ = "b_share_qq";//,qq好友分享
	public static final String EVENT_SHARE_QQ_ZONE = "b_share_qq_zone";//,qq空间分享
	public static final String EVENT_SHARE_WX = "b_share_wx";//,微信分享
	public static final String EVENT_SHARE_WX_CIRCLE = "b_share_wx_circle";//,分享到微信朋友圈

	//作业发布成功页各分享渠道的点击
//	public static final String EVENT_SHARE_MAKEOUT_WORK_TEACHER = "b_share_makeout_homework_teacher";
//	public static final String EVENT_SHARE_MAKEOUT_WORK_QQ = "b_share_makeout_homework_qq";
//	public static final String EVENT_SHARE_MAKEOUT_WORK_QQ_ZONE = "b_share_makeout_homework_qq_zone";
//	public static final String EVENT_SHARE_MAKEOUT_WORK_WX = "b_share_makeout_homework_wx";
//	public static final String EVENT_SHARE_MAKEOUT_WORK_WX_CIRCLE = "b_share_makeout_homework_wx_circle";

	//作业概览页分享按钮，并且各个渠道的点击
//	public static final String EVENT_SHARE_IN_OVERVIEW = "b_share_in_homework_overview";
//	public static final String EVENT_SHARE_OVERVIEW_TEACHER = "b_share_overview_teacher";
//	public static final String EVENT_SHARE_OVERVIEW_QQ = "b_share_overview_qq";
//	public static final String EVENT_SHARE_OVERVIEW_QQ_ZONE = "b_share_overview_qq_zone";
//	public static final String EVENT_SHARE_OVERVIEW_WX = "b_share_overview_wx";
//	public static final String EVENT_SHARE_OVERVIEW_WX_CIRCLE = "b_share_overview_wx_circle";

	//题库“我的”条目中详情页，分享按钮点击，各渠道的点击
//	public static final String EVENT_SHARE_IN_PERSONAL = "b_share_in_assign_personal";
//	public static final String EVENT_SHARE_PERSONAL_TEACHER = "b_share_personal_teacher";
//	public static final String EVENT_SHARE_PERSONAL_QQ = "b_share_personal_qq";
//	public static final String EVENT_SHARE_PERSONAL_QQ_ZNOE = "b_share_personal_qq_zone";
//	public static final String EVENT_SHARE_PERSONAL_WX = "b_share_personal_wx";
//	public static final String EVENT_SHARE_PERSONAL_WX_CIRCLE = "b_share_personal_wx_circle";

	//同校分享，每次分享的老师数
//	public static final String EVENT_SHARE_COUNT_TEACHER = "b_share_count_sameschool_teacher";

	//消息中分享作业消息点击，消息条目点击
//	public static final String EVENT_SHARE_GROUP_MASSAGE = "b_share_massage_group";//分享题组消息
//	public static final String EVENT_SHARE_MASSAGE = "b_share_massage";//消息条目

	//题组详情页，收藏，布置成作业的点击
//	public static final String EVENT_SHARE_QUESTION_COLLECT = "b_collect_share_questions";//收藏
//	public static final String EVENT_SHARE_QUESTION_MAKEOUT = "b_makeout_share_questions";//布置成作业

	//站外打开盒子
	public static final String EVENT_OPENAPP_FROM_OUTSIDE = "open_app_from_outside";

	/****************************2.7.0新加打点******************************/
//	public static final String EVENT_OPEN_BANK_PAPER = "b_open_test_paper";//题库首页-试卷
//	public static final String EVENT_OPEN_BANK_ASSISTANT = "b_open_assistant";//题库首页-教辅
//	public static final String EVENT_OPEN_BANK_MODIFY_MATERIAL = "b_modify_teachingmaterial";//题库首页-修改教材
//	public static final String EVENT_OPEN_BANK_SYNC = "b_open_chapter_sync";//题库首页-同步
//	public static final String EVENT_OPEN_BANK_KNOWLEDGE = "b_open_knowledge_point";//题库首页-知识点
//	public static final String EVENT_OPEN_BANK_BANNER = "b_open_banner";//题库首页-banner
//
//
//	public static final String EVENT_MY_COLLECT = "b_my_collect";//我的-我的收藏
//	public static final String EVENT_PHOTO_QUESTION_GROUP = "b_my_photo_questiongroup";//我的-拍照题组
//	public static final String EVENT_CREATE_GROUP = "b_create_question_group";//我的-新建组
//	public static final String EVENT_QUESTION_GROUP = "b_my_question_group";//我的-题组
//	public static final String EVENT_DELETE_GROUP = "b_delete_question_group";//题组条目删除
//
//	public static final String EVENT_COLLECT_TYPE = "b_collect_filter_by_type";//收藏题组-题型
//	public static final String EVETN_COLLECT_DIFFICULTY = "b_collect_filter_by_difficulty";//收藏题组-难度
//	public static final String EVENT_COLLECT_ISASSIGN = "b_collect_filter_by_isassign";//收藏题组-类型
//	public static final String EVENT_COLLECT_ADD_BASKET = "b_collect_add_basket";//收藏题组-加入出题框
//	public static final String EVENT_COLLECT_BATCH_MANAGE = "b_collect_batch_management";//收藏题组-批量管理
//
//	public static final String EVENT_PHOTO_TYPE = "b_photo_filter_by_type";//拍照题组-题型
//	public static final String EVETN_PHOTO_DIFFICULTY = "b_photo_filter_by_difficulty";//拍照题组-难度
//	public static final String EVENT_PHOTO_ISASSIGN = "b_photo_filter_by_isassign";//拍照题组-类型
//	public static final String EVENT_PHOTO_ADD_BASKET = "b_photo_add_basket";//拍照题组-加入出题框
//	public static final String EVENT_PHOTO_BATCH_MANAGE = "b_photo_batch_management";//拍照题组-批量管理
//
//	public static final String EVENT_MYGROUP_TYPE = "b_mygroup_filter_by_type";//我的题组-题型
//	public static final String EVETN_MYGROUP_DIFFICULTY = "b_mygroup_filter_by_difficulty";//我的题组-难度
//	public static final String EVENT_MYGROUP_ISASSIGN = "b_mygroup_filter_by_isassign";//我的题组-类型
//	public static final String EVENT_MYGROUP_ADD_BASKET = "b_mygroup_add_basket";//我的题组-加入出题框
//	public static final String EVENT_MYGROUP_BATCH_MANAGE = "b_mygroup_batch_management";//我的题组-批量管理
//	public static final String EVENT_MYGROUP_SELECT_ALL = "b_mygroup_select_all";//我的题组-全选
//	public static final String EVENT_MYGROUP_SHARE = "b_mygroup_share";//我的题组-分享
//	public static final String EVENT_MYGROUP_RENAME = "b_mygroup_rename";//我的题组-重命名
//	public static final String EVENT_MYGROUP_DELETE = "b_mygroup_delete";//我的题组-删除
//
//	public static final String EVENT_BATCH_MANAGE_TRANSFER = "b_batch_manage_transfer";//批量管理-转移到
//	public static final String EVENT_BATCH_MANAGE_COPY = "b_batch_manage_copy";//批量管理-复制到
//	public static final String EVENT_BATCH_MANAGE_DELETE = "b_batch_manage_delete";//批量管理-删除
//	public static final String EVENT_TRANSFER_TO_CREATEGROUP = "b_transfer_to_creategroup";//转移到-新建组
//	public static final String EVENT_TRANSFER_TO_GROUP = "b_transfer_to_group";//转移到-题组
//	public static final String EVENT_COPY_TO_CREATEGROUP = "b_copy_to_creategroup";//复制到-新建组
//	public static final String EVENT_COPY_TO_GROUP = "b_copy_to_group";//复制到-题组
//
//	public static final String EVENT_TOPIC_SHARE = "b_topic_share";//专题-分享
//	public static final String EVENT_TOPIC_SHARE_TO_WX = "b_topic_share_to_wx";//专题-分享到微信
//	public static final String EVENT_TOPIC_SHARE_TO_WX_CIRCLE = "b_topic_share_to_wx_circle";//专题-分享到微信朋友圈
//	public static final String EVENT_TOPIC_SHARE_TO_QQ = "b_topic_share_to_qq";//专题-分享到qq好友
//	public static final String EVENT_TOPIC_SHARE_TO_ZONE = "b_topic_share_to_qq_zone";//专题-分享到qq空间
//	public static final String EVENT_TOPIC_SAVE = "b_topic_save_as_questiongroup";//专题-保存为题组
//	public static final String EVENT_TOPIC_MAKE = "b_topic_make_out_homework";//专题-布置成作业
//	public static final String EVENT_TOPIC_OTHER = "b_topic_others";//专题-其他专题
//	public static final String EVENT_TOPIC_ALL = "b_topic_all";//专题-全部
//
//	public static final String EVENT_SYNC_BOOK_LEVELONE = "b_sync_book_level1";//同步-一级
//	public static final String EVENT_SYNC_BOOK_LEVELTWO = "b_sync_book_level2";//同步-二级
//	public static final String EVENT_SYNC_BOOK_LEVELTHREE = "b_sync_book_level3";//同步-三级
//	public static final String EVENT_SYNC_BOOK_LEVELFOUR = "b_sync_book_level4";//同步-四级
//
//	public static final String EVENT_KNOWLEDGE_LEVELONE = "b_knowledge_level1";//知识点-一级
//	public static final String EVENT_KNOWLEDGE_LEVELTWO = "b_knowledge_level2";//知识点-二级
//	public static final String EVENT_KNOWLEDGE_LEVELTHREE = "b_knowledge_level3";//知识点-三级
//	public static final String EVENT_KNOWLEDGE_LEVELFOUR = "b_knowledge_level4";//知识点-四级
//
//	public static final String EVENT_QUESTIONTYPE_LEVELONE = "b_questiontype_level1";//题型-一级
//	public static final String EVENT_QUESTIONTYPE_LEVELTWO = "b_questiontype_level2";//题型-二级
//	public static final String EVENT_QUESTIONTYPE_LEVELTHREE = "b_questiontype_level3";//题型-三级
//	public static final String EVENT_QUESTIONTYPE_LEVELFOUR = "b_questiontype_level4";//题型-四级
//
//	public static final String EVENT_PAPER_OUTSTANDING = "b_paper_oustanding";//试卷-推荐试卷
//	public static final String EVENT_PAPER_FILTER_BY_AREA = "b_paper_filter_by_area";//试卷-地区
//	public static final String EVENT_PAPER_FILTER_BY_TYPE = "b_paper_filter_by_type";//试卷-类型
//	public static final String EVENT_PAPER_FILTER_BY_TIME = "b_paper_filter_by_time";//试卷-年份
//	public static final String EVENT_PAPER_LIST = "b_paper_list";//试卷-试卷列表
//
//	public static final String EVENT_QUESTION_LIST_FILTER_TYPE = "b_question_list_filter_by_type";//题目列表-题型
//	public static final String EVENT_QUESTION_LIST_FILTER_DIFFICULTY = "b_question_list_filter_by_diff";//题目列表-难度
//	public static final String EVENT_QUESTION_LIST_FILTER_ISASSIGN = "b_question_list_filter_by_isassign";//题目列表-类型
//	public static final String EVENT_QUESTION_LIST = "b_question_list";//题目列表-题目
//	public static final String EVENT_QUESTION_ADD_TO_BASKET = "b_question_add_to_basket";//题目列表-加入出题框
//
//	public static final String EVENT_QUESTION_INFO_ADD_TO_BASKET = "b_question_info_addto_basket";//题目详情-加入出题框
//	public static final String EVENT_QUESTION_INFO_COLLECT = "b_question_info_collect";//题目详情-收藏
//	public static final String EVENT_QUESTION_INFO_REPORT_ERROR = "b_question_info_report_error";//题目详情-报错
//	public static final String EVENT_QUESTION_INFO_COLLECT_CREATEGROUP = "b_question_info_collect_creategroup";//收藏-新建组
//	public static final String EVENT_QUESTION_INFO_COLLECT_GROUP = "b_question_info_collect_group";//收藏-题组
//
//	public static final String EVENT_BASKET_SAVE_AS_QUESTIONGROUP = "b_basket_save_as_questiongroup";//出题框-保存
//	public static final String EVENT_BASKET_MAKE_AS_HOMEWORK = "b_basket_make_out_as_homework";//出题框-布置
//	public static final String EVENT_BASKET_EDIT = "b_basket_edit";//出题框-编辑
//	public static final String EVENT_BASKET_DELETE = "b_basket_delete";//出题框-删除
//
//	public static final String EVENT_HOMEWORK_ALL = "b_homework_all";//作业-全部
//	public static final String EVENT_HOMEWORK_WAIT_CORRECT = "b_homework_wait_correct";//作业-待批
//	public static final String EVETN_HOMEWORK_CLASS_GROUP = "b_homework_class_group";//作业-班群
//
//	/***************************3.0.0加入埋点********************************/
//
//	public static final String EVENT_HOMEWORK_BATCH_MANAGE = "b_homework_preview_batch_manage";//已选题目页批量管理
//	public static final String EVENT_BATCH_MANAGE_MOVE_UP = "b_homework_batch_manage_moveup";//已选题目页批量管理上移
//	public static final String EVENT_BATCH_MANAGE_MOVE_DOWN = "b_homework_batch_manage_movedown";//已选题目页批量管理下移
//
//	/**********************3.1.0添加埋点*************************/
//
//	public static final String EVENT_RECOMMEND_TAB = "t_recommend_tab";//题库-推荐的tab
//	public static final String EVENT_RECOMMEND_FAMOUS_TEACHER = "b_recommend_famous_teacher";//名师推荐
//	public static final String EVENT_RECOMMEND_OTHER_TEACHER = "b_recommend_other_teacher";//其他老师
//	public static final String EVENT_RECOMMEND_HOT_KNOWLEDGE = "b_recommend_hot_knowledge";//热点知识点
//	public static final String EVENT_RECOMMEND_HIGH_LIGHT = "b_recommend_high_light";//高亮考点
//	public static final String EVENT_RECOMMEND_CARE_SELECT = "b_recommend_care_select";//精选专题
//	public static final String EVENT_RECOMMEND_FAMOUS_SCHOOL = "b_recommend_famous_school";//名校试卷
//	public static final String EVENT_RECOMMEND_FINE_PAPER = "b_recommend_fine_paper";//精品试卷
//	public static final String EVENT_MINE_COLLECT_GROUPS = "b_mine_collect_groups";//收藏题组
//	public static final String EVENT_MINE_COLLECT_QUESTIONS = "b_mine_collect_questions";//收藏的散题
//
//	public static final String EVENT_MORE_FAMOUS_TEACHER = "b_more_famous_teacher";//更多名师推荐
//	public static final String EVENT_MORE_OTHER_TEACHER = "b_more_other_teacher";//更多其他老师
//	public static final String EVENT_MORE_HOT_KNOWLEDGE = "b_more_hot_knowledge";//更多热点知识点
//	public static final String EVENT_MORE_HIGH_LIGHT = "b_more_high_light";//更多高亮考点
//	public static final String EVENT_MORE_CARE_SELECT = "b_more_care_select";//更多精选专题
//	public static final String EVENT_MORE_FAMOUS_SCHOOL = "b_more_famous_school";//更多名校试卷
//	public static final String EVENT_MORE_FINE_PAPER = "b_more_fine_paper";//更多精品试卷
//
//	public static final String EVENT_LOOK_SELFTRAIN_RANK = "action_look_selftrain_rank";//老师查看学生自主练习排行榜
//	public static final String EVENT_THUMBS_UP = "b_rank_thumbs_up_student";//老师给学生点赞


	/**********************3.2.0添加埋点***********************/

//	public static final String EVENT_NEWUSER_TO_SUBJECT_PAGER = "v_new_user_into_subjectselectpage";//新用户进入科目选择页
//	public static final String EVENT_OLD_USER_LOGIN = "b_old_user_login_btn";//已有账号登录
//	public static final String EVENT_FIRST_SELECT_TEXTBOOK = "b_first_select_textbook_into_mainpage";//选择年级（选择教材，即完成初次启动并进入首页数）

	public static final String EVENT_VERIFYCODE_LOGIN_BTN_CLICK = "b_verifycode_login";//短信验证登录，登录button点击
	public static final String EVENT_ACCOUNT_PWD_LOGIN_BTN_CLICK = "b_account_pwd_login";//账号密码登录，登录button点击

//	public static final String EVENT_VIRTUAL_HOMEWORK_CORRECT = "b_virtual_homework_test_correct";//体验批改作业点击
//	public static final String EVENT_VIRTUAL_HOMEWORK_COMPLETE = "b_virtual_homework_test_complete";//完成虚拟体验

//	public static final String EVENT_GOTO_QUESTIONBANK_AT_LISTBUTTOM = "b_goto_questionbank";//作业list底部去题库布置作业的button点击
//	public static final String EVENT_GOTO_QUESTIONBANK_AT_BUTTOM_BAR = "t_switchto_questionbank";//底部bar切到题库页
//	public static final String EVENT_MAKE_HOMEWORK_BTN = "b_make_homework";//布置作业点击
//	public static final String EVENT_PUBLISH_HOMEWORK_BTN = "b_publish_homework";//发布作业点击
//	public static final String EVENT_ONE_KEY_MAKE = "b_onekey_make";//一键布置点击

//	public static final String EVENT_MAIN_HOMEWORK_CLASSGROUP = "b_main_homework_classgroup";//首页班群点击
//	public static final String EVENT_CLASSGROUP_SETTING = "b_classgroup_setting";//班群页设置点击
	public static final String EVENT_RENAME_CLASSGROUP = "b_class_rename";//修改班群名称点击

//	public static final String EVENT_ADD_STUDENT_IN_SUBMIT_DETAIL = "b_add_student_in_submit_detail";//提交详情，添加学生点击
//	public static final String EVENT_INVITE_STUDENT_IN_CLASSEMPTY = "b_invite_student_in_classempty";//班群空页面，邀请学生btn
//	public static final String EVENT_INVITE_MORE_STUDENT = "b_invite_more_student";//邀请添加更多的学生
//	public static final String EVENT_INVITE_STUDENT_IN_CLASS = "b_invite_student_in_class";//班群邀请学生“+”邀请学生
//	public static final String EVENT_ADD_STUDENT_IN_CLASS = "b_add_student_in_class";//班群邀请学生“+”添加学生

	public static final String EVENT_ACTIVITY_DIALOG_CLOSE = "b_activity_dialog_close";//首页活动弹窗关闭
	public static final String EVENT_ACTIVITY_FRAGMENT_OPEN = "b_activity_fragment_open";//首页活动弹窗跳转打开

	/**********************3.3.0添加埋点*********************/

//	public static final String EVENT_MAIN_ALL_HOMEWORK = "b_main_all_homework";//首页全部作业点击
//	public static final String EVENT_MAIN_HOMEWORK_ITEM = "b_main_homework_item";//首页作业点击
//	public static final String EVENT_MAIN_VIRTUAL_HOMEWORK = "b_main_virtual_homework";//首页虚拟作业点击
//	public static final String EVENT_MAIN_TO_CLASS_INFO = "b_main_to_class_info";//首页点击进入班群详情页
//	public static final String EVENT_MAIN_CREATE_CLASSGROUP = "b_main_create_classgroup";//首页创建班级点击
//	public static final String EVENT_MAIN_MAKEOUT_HOMEWORK = "b_main_makeout_homework";//首页布置作业点击
//	public static final String EVENT_MAIN_CLICK_NEW_TASK = "b_main_click_new_task";//首页新手任务点击
//	public static final String EVENT_MAIN_CLOSE_NEW_TASK = "b_main_close_new_task";//首页关闭新手任务点击
//
//	public static final String EVENT_CLASS_DATAINFO = "b_class_datainfo";//班群页数据描述点击
//	public static final String EVENT_CLASS_KNOWLEDGE_CHART = "b_class_knowledge_chart";//班群页学情分析点击
//	public static final String EVENT_CLASS_ALL_HOMEWORK = "b_class_all-homework";//班群页全部作业点击
//	public static final String EVENT_CLASS_GROUP_CHAT = "b_class_group_chat";//班群页群聊点击
//	public static final String EVENT_CLASS_GIVE_GOLD_COIN = "b_class_give_gold_coin";//班群页送金币点击
//	public static final String EVENT_CLASS_GIVE_PRAISE = "b_class_give_praise";//班群页点赞点击
//	public static final String EVENT_CLASS_INVITE_STUDENT = "b_class_invite_student";//b_class_invite_student
//	public static final String EVENT_CLASS_ADD_STUDENT = "b_class_add_student";//班群页添加学生点击
//	public static final String EVENT_CLASS_SELF_TRAINING_TAB = "b_class_self_training_tab";//班群页自主练习排名tab点击
//	public static final String EVENT_CLASS_SETTING = "b_class_setting";//班群页右上角管理界面
//
//	public static final String EVENT_QUESTIONS_SELECT_TEXTBOOK = "b_questions_select_textbook";//题库顶部修改教材点击
//	public static final String EVENT_QUESTIONS_MAKEOUT_BOX = "b_questions_makeout_box";//题库出题框点击
//	public static final String EVENT_QUESTIONS_RECOMMEND_SWITCH = "b_questions_recommend_switch";//题库点击切换到同步教学
//	public static final String EVENT_QUESTIONS_PAPER_SWITCH = "b_questions_paperknowledge_switch";//题库点击切换到试卷知识点
//	public static final String EVENT_QUESTIONS_COLLECT_SWITCH = "b_questions_collect_switch";//题库点击切换到我的收藏
//	public static final String EVENT_QUESTIONLIST_PAGKAGE = "b_questionlist_package";//题目列表页顶部题包点击
//	public static final String EVENT_QUESTIONLIST_ALL = "b_questionlist_all_question";//题目列表页全部题目点击
//	public static final String EVENT_QUESTIONPACKAGE_SELECTALL = "b_questionpackage_selectall";//题包详情页全部选入点击
//
//	public static final String EVENT_PAPER_RECOMMEND_CLASS_PART = "b_paper_recommend_classpart";//同步检测试卷年级筛选
//
//	public static final String EVENT_PAPER_RECOMMEND_TYPE = "b_paper_recommend_type";//同步检测试卷类型筛选
//	public static final String EVENT_PAPER_TYPE = "b_paper_type";//真题模拟试卷类型筛选
//
//	public static final String EVENT_PAPER_RECOMMEND_YEAR = "b_paper_recommend_year";//同步检测试卷年份筛选
//	public static final String EVENT_PAPER_YEAR = "b_paper_year";//真题模拟试卷年份筛选
//
//	public static final String EVENT_PAPER_RECOMMEND_AREA = "b_paper_recommend_area";//同步检测试卷地区筛选
//	public static final String EVENT_PAPER_AREA = "b_paper_area";//真题模拟试卷地区筛选
//
//	public static final String EVENT_PAPER_FAMOUS_SCHOOL = "b_paper_famous_school";//试卷仅名校筛选
//
//	public static final String EVENT_INTO_STUDENT_INFO = "b_studentlist_goto_info";//学生列表到个人主页的点击
//
//	/***********************3.4.0埋点************************/
//
//	public static final String EVENT_SWITCH_CHINESEBASIC_TAB = "b_switch_chinesebasic_tab";//切换到语基tab
//	public static final String EVENT_PLUS_CHINESEBASIC_QUESTION = "b_plus_chinesebasic_question";//语基列表加题目
//	public static final String EVENT_MINUS_CHINESEBASIC_QUESTION = "b_minus_chinesebasic_question";//语基列表减题目
//	public static final String EVENT_PREVIEW_REPLACE_QUETION = "b_preview_replace_question";//出题框换一换的点击
//
//	/**********************3.4.2埋点*************************/
//
//	public static final String EVENT_CLASS_RANK_BY_CORRECT = "b_class_homework_rank_by_correctrate";//作业排名按正确率
//	public static final String EVENT_CLASS_RANK_BY_SUBMIT = "b_class_homework_rank_by_submitrate";//作业排名按提交率
//	public static final String EVENT_CLASS_RANK_BY_YESTODAT = "b_class_homework_rank_by_yestoday";//作业排名查看昨天
//	public static final String EVENT_CLASS_RANK_BY_THISWEEK = "b_class_homework_rank_by_thisweek";//作业排名查看本周
//	public static final String EVENT_CLASS_RANK_BY_LASTWEEK = "b_class_homework_rank_by_lastweek";//作业排名查看上周
//	public static final String EVENT_CLASS_RANK_BY_MONTHBEFORE = "b_class_homework_rank_by_monthbefore";//作业排名查看前一个月
//	public static final String EVENT_CLASS_RANK_BY_NEARLYTHREEMONTH = "b_class_homework_rank_by_nearlythreemonth";//作业排名查看近三个月
//
//	public static final String EVENT_HOMEWORK_RENAME = "b_homework_rename";//修改作业名
//
//	public static final String EVENT_EXPORT_PDF_TO_EMAIL = "b_export_pdf_to_email";//导出到邮箱
//	public static final String EVENT_EXPORT_PDF_TO_EMAIL_CONFIRMCEND = "b_export_pdf_to_email_confirmsend";//导出到邮箱确认发送
//	public static final String EVENT_EXPORT_PDF_TO_OTHER_CHANNEL = "b_export_pdf_to_other_channel";//导出到其他方式
//
//	public static final String EVENT_SELECTED_QUESTIONS_EDIT = "b_selected_questions_edit";//已选题目编辑
//
//	/************************3.4.3***************************/
//
//	public static final String EVENT_CLASS_WRONG_BOOK = "b_class_wrong_book";//打开错题本
//	public static final String EVENT_WRONGBOOK_OPEN_REVISE = "b_wrongbook_open_revise";//错题本打开待订正错题学生列表
//	public static final String EVENT_WRONGBOOK_RATE_ALL = "b_wrongbook_rate_all";//错题本按错误率筛选-全部
//	public static final String EVENT_WRONGBOOK_RATE_HIGHER70 = "b_wrongbook_rate_higher70";//错题本按错误率筛选>=70%
//	public static final String EVENT_WRONGBOOK_RATE_HIGHER50 = "b_wrongbook_rate_higher50";//错题本按错误率筛选>=50%
//	public static final String EVENT_WRONGBOOK_RATE_HIGHER20 = "b_wrongbook_rate_higher20";//错题本按错误率筛选>=20%
//	public static final String EVENT_WRONGBOOK_TIME_ALL = "b_wrongbook_time_all";//错题本按时间筛选-全部
//	public static final String EVENT_WRONGBOOK_TIME_LASTTIME = "b_wrongbook_time_lasttime";//错题本按时间筛选-上次
//	public static final String EVENT_WRONGBOOK_TIME_LASTWEEKL = "b_wrongbook_time_lastweek";//错题本按时间筛选-上周
//	public static final String EVENT_WRONGBOOK_TIME_THISMONTH = "b_wrongbook_time_thismonth";//错题本按时间筛选-本月
//	public static final String EVENT_WRONGBOOK_TIME_THISSEMESTER = "b_wrongbook_time_thissemester";//错题本按时间筛选-本学期
//	public static final String EVENT_REVISE_WRONGBOOK_REMIND = "b_revise_wrongbook_remind";//提示单个学生订正错题
//	public static final String EVENT_REVISE_WRONGBOOK_REMINDALL = "b_revise_wrongbook_remindall";//提示全部学生订正错题
//	public static final String EVENT_HOMEWORK_OPEN_REVISE = "b_homework_open_revise";//作业概览打开待订正作业学生列表
//	public static final String EVENT_REVISE_HOMEWORK_REMIND = "b_revise_homework_remind";//提示单个学生订正作业
//	public static final String EVENT_REVISE_HOMEWORK_REMINDALL = "b_revise_homework_remindall";//提示单个学生订正作业
//
//	public static final String EVENT_HOMEWORK_COLLECT = "b_homework_collect";//收藏作业到作业集
//	public static final String EVENT_HOMEWORK_QUESTION_COLLECT = "b_homework_question_collect";//收藏作业单题到题组
//	public static final String EVENT_CLASS_ENGLISHWORD_TAB = "b_class_englishword_tab";//单词部落table点击
//	public static final String EVENT_CLASS_ENGLISHWORD_GIVE_DIAMOND = "b_class_englishword_give_diamond";//发钻石
//
//	/************************3.5.0 **************************/
//
//	public static final String EVENT_EARN_GOLD_ENTRY = "b_earn_gold_entry";//赚金币入口
//	public static final String EVENT_EARN_GOLD_INVITE_ACTIVITY = "b_earn_gold_invite_activity";//赚金币列表邀请任务banner
//	public static final String EVENT_EARN_GOLD_REDPKG_ACTIVITY = "b_earn_gold_redpkg_activity";//赚金币列表红包活动banner
//	public static final String EVENT_EARN_GOLD_SHOPPINGMALL = "b_earn_gold_shopping_mall";//赚金币列表金币商城入口
//
//	public static final String EVENT_MAIN_REDPKG_CARD = "b_main_redpkg_card";//首页点击红包
//	public static final String EVENT_REDPKG_ACTIVITY_POP_REDPKG = "b_redpkg_activity_pop_redpkg";//红包活动页面弹出红包
//	public static final String EVENT_REDPKG_ACTIVITY_OPEN_REDPKG = "b_redpkg_activity_open_redpkg";//红包活动页面打开红包
//	public static final String EVENT_REDPKG_ACTIVITY_INVITE = "b_redpkg_activity_invite";//红包活动页面邀请更多老师

	/***************************单词部落1.0.0*****************************/

	public static final String EVENT_HOME_STUDENT_TAB = "b_home_student_tab";//学生tab点击
	public static final String EVENT_HOME_STUDENT_ITEM = "b_home_student_item";//学生条目点击
	public static final String EVENT_HOME_UNIT_CARD = "b_home_unit_card";//单元卡片点击
	public static final String EVENT_HOME_UNIT_WORD_CARD = "b_home_unit_word_card";//单元-单词卡片点击
	public static final String EVENT_HOME_UNIT_STUDENT_TAB = "b_home_unit_student_tab";//单元-学生列表tab点击
	public static final String EVENT_HOME_SWITCH_CLASS = "b_home_switch_class";//切换班级点击
	public static final String EVENT_HOME_CREATE_CLASS = "b_home_create_class";//建班点击
	public static final String EVENT_HOME_LOOK_CLASSINFO = "b_home_look_classinfo";//修改查看班级资料点击

	public static final String EVENT_COMPETITION_LOOK_DETAILS = "b_competition_look_details";//锦标赛查看详情点击
	public static final String EVENT_COMPETITION_LOOK_WORDS = "b_competition_look_words";//锦标赛查看单词点击
	public static final String EVENT_COMPETITION_LOOK_WORD_DETAILS = "b_competition_look_word_details";//锦标赛查看单词详情点击
	public static final String EVENT_COMPETITION_LOOK_STUDENT_RANK = "b_competition_look_student_rank";//锦标赛查看完整排行点击

//	public static final String EVENT_SIGNUP1_BACK = "b_signup1_back";//,基本信息“返回”
//	public static final String EVENT_SIGNUP1_NEXT = "b_signup1_next";//,基本信息“下一步”
//	public static final String EVENT_SIGNUP2_BACK = "b_signup2_back";//,账号密码“返回”
//	public static final String EVENT_SIGNUP2_NEXT = "b_signup2_next";//,账号密码“下一步”
//	public static final String EVENT_SIGNUP3_BACK = "b_signup3_back";//,验证手机“返回”
//	public static final String EVENT_SIGNUP3_FINISH = "b_signup3_finish";//,验证手机“注册”
//	public static final String EVENT_SIGNUP_SMS = "b_signup_sms";//,注册获取验证码
//	public static final String EVENT_GROUP_CHAT = "b_group_chat";//,群聊
//	public static final String EVENT_STUDENT_CHAT = "b_student_chat";//,单聊
	public static final String EVENT_LOGINOUT = "b_loginout";//,登出
//	public static final String EVENT_QINIU_UPLOAD_FILE = "r_qiniu_upload_file";//,七牛云上传文件
//	public static final String EVENT_CLASS = "t_class";//,班群
//	public static final String EVENT_HOMEWORK = "t_homework";//,作业
//	public static final String EVENT_ME = "t_me";//,我
//	public static final String EVENT_MESSAGE = "t_message";//,消息
//	public static final String EVENT_REGISTER_INTRODUCE = "t_regist_introduce";//介绍页注册
//	public static final String EVENT_REGISTER_LOGIN = "b_regist_login";//登陆页注册
	public static final String EVENT_AUTH_UPLOAD_FILE = "r_auth_upload_file";//认证上传图片

//	public static final String PARAM_KEY_RESULT = "result";
//	public static final String PARAM_VALUE_COUNT = "teacher_count";
	public static final void reportUmengEvent(String eventId, Object eventValue) {
		if (eventValue != null) {
			if (eventValue instanceof String) {
				MobclickAgent.onEvent(BaseApp.getAppContext(), eventId, (String) eventValue);
			} else if (eventValue instanceof Map) {
				MobclickAgent.onEvent(BaseApp.getAppContext(), eventId, (Map) eventValue);
			}
		} else {
			MobclickAgent.onEvent(BaseApp.getAppContext(), eventId);
		}
//		if (LogUtil.isDebug())
//			ToastUtils.showLongToast(BaseApp.getAppContext(), eventId);
	}

}
