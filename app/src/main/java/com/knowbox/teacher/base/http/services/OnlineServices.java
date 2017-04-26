package com.knowbox.teacher.base.http.services;

import android.os.Build;
import android.text.TextUtils;

import com.hyena.framework.utils.VersionUtils;
import com.knowbox.teacher.App;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.Utils;
import com.umeng.analytics.AnalyticsConfig;

/**
 * 网络接口提供其
 *
 * @author yangzc
 */
public class OnlineServices {

    public final static String SOURCE = "androidTeacher";

    //是否开启debug模式
    private static boolean DEBUG = false;

    public static void enableDebug(boolean debug) {
        DEBUG = debug;
    }

    public static boolean getDebugMode() {
        return DEBUG;
    }

    /**
     * 获取新接口URL前缀
     * T
     *
     * @return
     * @author weilei
     */
    public static String getPhpUrlPrefix() {
        StringBuffer prefix = new StringBuffer();
        if (DEBUG) {
            int debugType = PreferencesController.getInt(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_API, 1);
            if(debugType == 0) {
                prefix.append("http://cow.test.knowbox.cn/");//单词部落联调环境
            }else if(debugType == 1) {
                prefix.append("http://beta.cow.api.knowbox.cn/"); //测试环境
            }else if(debugType == 2) {
                prefix.append("http://preview.cow.api.knowbox.cn/"); //预览环境
            }else if(debugType == 3) {
                prefix.append("http://cow.knowbox.cn/"); //正式环境
            }else if (debugType == 4) {
                String apiPrefix = PreferencesController.getStringValue(ConstantsUtils.PREF_KEY_CONFIG_DEBUG_APIPREFIX);
                if (TextUtils.isEmpty(apiPrefix)) {
                    apiPrefix = "http://cow.knowbox.cn/";
                }
                prefix.append(apiPrefix);
            }
        } else {
            prefix.append("http://cow.knowbox.cn/");
//            prefix.append("http://beta.cow.api.knowbox.cn/");
        }
        return prefix.toString();
    }

    private static StringBuffer getPhpCommonUrl(String commonUrl) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getPhpUrlPrefix());
        buffer.append(commonUrl);
        try {
            buffer.append("source=androidTeacher");
            buffer.append("&version=" + VersionUtils.getVersionCode(App.getAppContext()));
            String channel = AnalyticsConfig.getChannel(App.getAppContext());
            if ("${UMENG_CHANNEL_VALUE}".equals(channel)) {
                channel = "knowbox";
            }
            buffer.append("&channel=" + channel);
            if (!TextUtils.isEmpty(Utils.getToken())) {
                buffer.append("&token=" + Utils.getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 获得登录URL
     *
     * @return
     */
    public static String getLoginUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/passwd-login?");
        return buffer.toString();
    }

    /**
     * 获得短信验证登录URL
     *
     * @return
     */
    public static String getSmsLoginUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/code-login?");
        return buffer.toString();
    }

    /**
     * 注册时获取短信验证码
     *
     * @param mobile
     * @return
     */
    public static String getSendSmsCode4Regist(String mobile, String type) {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/get-login-code?");
        buffer.append("&mobile=" + mobile);
        buffer.append("&sms_type=" + type);
        return buffer.toString();
    }

    /**
     * 忘记密码时获取短信验证码
     *
     * @param mobile
     * @return
     */
    public static String getSendSmsCode4ForgetPass(String mobile, String type) {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/get-reset-passwd-code?");
        buffer.append("&mobile=" + mobile);
        buffer.append("&sms_type=" + type);
        return buffer.toString();
    }

    /**
     * 忘记密码时验证短信验证码
     *
     * @param mobile
     * @return
     */
    public static String getConfirmSmsCodeUrl(String mobile, String smsCode) {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/verify-reset-passwd-code?");
        buffer.append("&mobile=" + mobile);
        buffer.append("&code=" + smsCode);
        return buffer.toString();
    }

    /**
     * 根据省市区搜索学校
     *
     * @return
     */
    public static String getSearchSchoolByCityIdUrl(String cityId) {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/get-school?");
        buffer.append("&city_id=" + cityId);
        return buffer.toString();
    }

    /**
     * 根据省市区搜索学校
     *
     * @return
     */
    public static String getSearchSchoolByCityIdUrl(String token, String cityId) {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/get-school?");
        buffer.append("&city_id=" + cityId);
        return buffer.toString();
    }

    /**
     * 获得登出Url
     *
     * @return
     */
    public static String getLogOutUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/login-out?");
        return buffer.toString();
    }

    /**
     * 检查版本url
     * @return
     */
    public static String getCheckVersionUrl() {
        StringBuffer buffer = getPhpCommonUrl("common/app/check-version?");
        return buffer.toString();
    }

    /**
     * 获得删除班级接口
     *
     * @return
     */
    public static String getDeleteClassUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/class/delete-class?");
        return buffer.toString();
    }

    /**
     * 获得关闭班级URL
     *
     * @return
     */
    public static String getCloseClassUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/class/close-class?");
        return buffer.toString();
    }

    /**
     * 获得修改个人信息URL
     *
     * @return
     */
    public static String getUpdateUserInfo() {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/update-info?");
        return buffer.toString();
    }

    /**
     * 修改密码(设置页面)
     * token为空为修改密码，不为空为忘记密码时修改密码
     *
     * @return
     */
    public static String getUpdatePassword(String token) {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/modify-password?");
        if (!buffer.toString().contains("&token=")) {
            buffer.append("&token=" + token);
        }
        return buffer.toString();
    }

    /**
     * 获取联系人详细信息
     *
     * @param userId
     * @return
     */
    public static String getContactInfoUrl(String token, String userId) {
        StringBuffer buffer = getPhpCommonUrl("v1_class/teacher/get-student-info?");
        buffer.append("&token=" + token);
        buffer.append("&user_id=" + userId);
        return buffer.toString();
    }

    /**
     * 获取发送聊题url
     *
     * @param token
     * @param answerId
     * @return
     */
    public static String getMessageChatQuestionUrl(String token, String answerId) {
        StringBuffer buffer = getPhpCommonUrl("v1_answer/teacher/get-question-info?");
        buffer.append("&token=" + token);
        buffer.append("&answer_id=" + answerId);
        return buffer.toString();
    }

    /**
     * 获取个人信息
     *
     * @return
     */
    public static String getUserInfoUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/get-info?");
        return buffer.toString();
    }

    /** 题库API **/

    /**
     * 获取教材课本Url
     *
     * @return
     * @author weilei
     */
    public static String getTeachMaterialUrl() {
        return getPhpCommonUrl("user/user/get-teachmaterial-textbook?").toString();
    }

    /**
     * 语音上传token
     *
     * @return
     * @author weilei
     */
    public static String getVoiceUploadTokenUrl(int type) {
        StringBuffer buffer = getPhpCommonUrl("common/app/get-bucket-token?");
        buffer.append("&source_type=" + type);
        return buffer.toString();
    }

    /**
     * 获取同校教师
     *
     * @param token
     * @return
     */
    public static String getSchoolTeacherListUrl(String token) {
        StringBuffer buffer = getPhpCommonUrl("v1_class/teacher/get-school-teacher?");
        buffer.append("&token=" + token);
        return buffer.toString();
    }

    /**
     * 获取同校教师
     *
     * @param token
     * @return
     */
    public static String getTransferClassUrl(String token) {
        StringBuffer buffer = getPhpCommonUrl("v1_class/teacher/transfer-class?");
        buffer.append("&token=" + token);
        return buffer.toString();
    }

    /**
     * 添加班级
     *
     * @return
     */
    public static String getAddClassUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/class/create-class?");
        return buffer.toString();
    }

    /**
     * 验证手机号
     *
     * @param token
     * @param mobile
     * @param verifyExit
     * @return
     */
    public static String getVerifyMobileUrl(String token, String mobile, int verifyExit, String grade, String subject) {
        StringBuffer buffer = getPhpCommonUrl("v1_user/teacher/verify-mobile?");
        buffer.append("&token=" + token);
        buffer.append("&mobile=" + mobile);
        buffer.append("&verify_exist=" + verifyExit);
        buffer.append("&grade_part=" + grade);
        buffer.append("&subject=" + subject);
        return buffer.toString();
    }

    /**
     * 验证手机号（注册和忘记密码时使用）
     *
     * @param mobile
     * @return
     */
    public static String getVerifyMobileUrl(String mobile) {
        StringBuffer buffer = getPhpCommonUrl("user/teacher/verify-mobile?");
        buffer.append("&mobile=" + mobile);
        return buffer.toString();
    }

    /**
     * 登录通知
     *
     * @return
     */
    public static String getTeacherGlobalInfoUrl() {
        StringBuffer buffer = getPhpCommonUrl("common/activity/get-global-info?");
        return buffer.toString();
    }

    /**
     * 通知回调
     *
     * @return
     */
    public static String getGlobalCallBackUrl(String info_id) {
        StringBuffer buffer = getPhpCommonUrl("common/activity/callback-global-info?");
        buffer.append("&info_id=" + info_id);
        return buffer.toString();
    }

    /**
     * 发送邀请码
     *
     * @param token
     * @param mobile
     * @return
     */
    public static String getSendInvitationUrl(String token, String mobile) {
        StringBuffer buffer = getPhpCommonUrl("v1_user/teacher/send-invitation?");
        buffer.append("&token=" + token);
        buffer.append("&mobile=" + mobile);
        return buffer.toString();
    }

    /**
     * @return
     */
    public static String getCityPlist() {
        StringBuffer buffer = getPhpCommonUrl("user/user/verify-city-data?");
        return buffer.toString();
    }

    public static String getTeacherLogUrl() {
        StringBuffer buffer = getPhpCommonUrl("common/app/log-triger?");
        try {
            String token = Utils.getToken();
            if (!TextUtils.isEmpty(token)) {
                buffer.append("&token=" + token);
            }
            buffer.append("&mobileBrand=" + android.os.Build.BRAND);
            buffer.append("&mobileVersion=" + Build.VERSION.RELEASE);
            buffer.append("&mobileModel=" + Build.MODEL);
            buffer.append("&mobileSDK=" + Build.VERSION.SDK_INT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    /**
     * 活动相关Url
     *
     * @param token
     * @return
     */
    public static String getTeacherExtUrl(String token) {
        StringBuffer buffer = getPhpCommonUrl("common/activity/get-ext?");
        buffer.append("&token=" + token);
        return buffer.toString();
    }

    /**
     * 修改班级信息
     *
     * @return
     */
    public static String getUpdateClassInfoUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/class/update-class-info?");
        return buffer.toString();
    }

    /**
     * FAQ url
     *
     * @return
     */
    public static String getHelpUrl() {
        return getPhpUrlPrefix() + "wordsTribe-help/wordTribe-help.html";
    }


    /**
     * 获取模板更新地址
     * @return
     */
    public static String getOnlineTemplateUrl() {
        StringBuffer buffer = getPhpCommonUrl("common/app/get-resource-list?");
        return buffer.toString();
    }

    /**
     * 获取分享班级码
     * @param classCode
     * @return
     */
    public static String getInviteStudentShareUrl(String classCode) {
        StringBuffer buffer = getPhpCommonUrl("user/share/get-student-invite-content?");
        buffer.append("&class_code=" + classCode);
        return buffer.toString();
    }

    /**********单词部落********/

    public static String getCompetitionListUrl(int pageSize, int pageNo) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/list?");
        buffer.append("&size=" + pageSize);
        buffer.append("&page=" + pageNo);
        return buffer.toString();
    }

    /**
     * 获取锦标赛比赛详情
     * @param matchId
     * @return
     */
    public static String getCompetitionDetailsUrl(String matchId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/detail?");
        buffer.append("&match_id=" + matchId);
        return buffer.toString();
    }

    /**
     * 获取删除锦标赛url
     * @param matchId
     * @return
     */
    public static String getCompetitionDeleteUrl(String matchId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/delete?");
        buffer.append("&match_id=" + matchId);
        return buffer.toString();
    }

    /**
     * 获取删除多个锦标赛url
     * 1.1.0版本添加的功能
     * @return
     */
    public static String getCompetitionMultiDeleteUrl() {
        return getPhpCommonUrl("match/teacher-match/multi-delete?").toString();
    }

    /**
     * 获取锦标赛比赛单词
     * @param matchId
     * @return
     */
    public static String getCompetitionWordsUrl(String matchId, String classId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/words-status?");
        buffer.append("&class_id=" + classId);
        buffer.append("&match_id=" + matchId);
        return buffer.toString();
    }

    /**
     * 单元列表
     * @param classId
     * @return
     */
    public static String getClassUnitInfoUrl(String classId) {
        StringBuffer buffer = getPhpCommonUrl("unit/unit/class-unit-info?");
        buffer.append("&class_id=" + classId);
        return buffer.toString();
    }

    /**
     * 班级学生列表
     * @param classId
     * @return
     */
    public static String getClassStudentInfoUrl(String classId) {
        StringBuffer buffer = getPhpCommonUrl("word/classword/class-student-learn-info?");
        buffer.append("&class_id=" + classId);
        return buffer.toString();
    }

    /**
     * 鼓励金币
     * @return
     */
    public static String getGrantCoinUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/coin/encourage?");
        return buffer.toString();
    }

    /**
     * 单元单词列表
     * @param classId
     * @param bookId
     * @param sectionId
     * @return
     */
    public static String getUnitWordsUrl(String classId, String bookId, String sectionId) {
        StringBuffer buffer = getPhpCommonUrl("unit/unit/unit-words-learned-status?");
        buffer.append("&class_id=" + classId);
        buffer.append("&book_id=" + bookId);
        buffer.append("&section_id=" + sectionId);
        return buffer.toString();
    }

    /**
     * 单元学生列表
     * @param classId
     * @param bookId
     * @param sectionId
     * @return
     */
    public static String getUnitStudentUrl(String classId, String bookId, String sectionId) {
        StringBuffer buffer = getPhpCommonUrl("unit/unit/unit-students-learned-status?");
        buffer.append("&class_id=" + classId);
        buffer.append("&book_id=" + bookId);
        buffer.append("&section_id=" + sectionId);
        return buffer.toString();
    }

    /**
     * 单词学生列表
     * @param classId
     * @param word_id
     * @return
     */
    public static String getWordStudentListUrl(String classId, String word_id) {
        StringBuffer buffer = getPhpCommonUrl("word/classword/class-exercise-word-status?");
        buffer.append("&class_id=" + classId);
        buffer.append("&word_id=" + word_id);
        return buffer.toString();
    }

    /**
     * 学生单元列表
     * @param classId
     * @param bookId
     * @return
     */
    public static String getStudentUnitUrl(String classId, String bookId, String studentId, String editionId) {
        StringBuffer buffer = getPhpCommonUrl("word/classword/student-words-status?");
        buffer.append("&class_id=" + classId);
        buffer.append("&book_id=" + bookId);
        buffer.append("&student_id=" + studentId);
        buffer.append("&edition_id=" + editionId);
        return buffer.toString();
    }

    /**
     * 出题单元列表
     * @return
     */
    public static String getAssignUnitListUrl(String type) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/setting?");
        buffer.append("&type=" + type);
        return buffer.toString();
    }

    /**
     * 班级详情
     * @return
     */
    public static String getClassDetailUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/class/teacher-class-list?");
        return buffer.toString();
    }

    /**
     * 发布比赛
     * @return
     */
    public static String getLaunchMatchUrl() {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/create?");
        return buffer.toString();
    }

    /**
     * 官网地址
     * @return
     */
    public static String getWordWebUrl() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getPhpUrlPrefix());
        buffer.append("wordsTribe/knowboxword.html");
        return buffer.toString();
    }

    /**
     * 循环赛学生列表榜单
     * @param matchId
     * @param time
     * @param type
     * @return
     */
    public static String getReportStudentListUrl(String matchId, String time, int type) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/get-report-student-list?");
        buffer.append("&match_id=" + matchId);
        if (!TextUtils.isEmpty(time)) {
            buffer.append("&date=" + time);
        }
        buffer.append("&type=" + type);
        return buffer.toString();
    }

    /**
     * 单次测试赛学生列表榜单
     * @param matchId
     * @return
     */
    public static String getSingletestStudentListUrl(String matchId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-test/result?");
        buffer.append("&match_id=" + matchId);
        return buffer.toString();
    }

    /**
     * 循环赛单词列表榜单
     * @param matchId
     * @return
     */
    public static String getReportWordListUrl(String matchId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/get-report-word-list?");
        buffer.append("&match_id=" + matchId);
        return buffer.toString();
    }

    /**
     * 单次测验赛单词列表榜单
     * @param matchId
     * @return
     */
    public static String getSingletestWordListUrl(String matchId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-test/words-detail?");
        buffer.append("&match_id=" + matchId);
        return buffer.toString();
    }

    /**
     * 循环赛单词详情
     * @param matchId
     * @param time
     * @param type
     * @param studentId
     * @return
     */
    public static String getReportStudentDetailUrl(String matchId, String time, int type, String studentId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/get-report-student-detail?");
        buffer.append("&match_id=" + matchId);
        if (!TextUtils.isEmpty(time)) {
            buffer.append("&date=" + time);
        }
        buffer.append("&type=" + type);
        buffer.append("&student_id=" + studentId);
        return buffer.toString();
    }

    /**
     * 单次测验学生详情
     * @param matchId
     * @param studentId
     * @return
     */
    public static String getSingletestStudentDetailUrl(String matchId, String studentId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-test/student-detail?");
        buffer.append("&match_id=" + matchId);
        buffer.append("&student_id=" + studentId);
        return buffer.toString();
    }

    /**
     * 循环赛单词详情
     * @param matchId
     * @param wordId
     * @return
     */
    public static String getReportWordDetailUrl(String matchId, String wordId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/get-report-word-detail?");
        buffer.append("&match_id=" + matchId);
        buffer.append("&word_id=" + wordId);
        return buffer.toString();
    }

    /**
     * 单次测验单词详情
     * @param matchId
     * @param wordId
     * @return
     */
    public static String getSingletestWordDetailUrl(String matchId, String wordId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-test/error-detail?");
        buffer.append("&match_id=" + matchId);
        buffer.append("&word_id=" + wordId);
        return buffer.toString();
    }

    /**
     * 循环赛日报历史
     * @param matchId
     * @return
     */
    public static String getReportHistoryListUrl(String matchId) {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/get-report-history-list?");
        buffer.append("&match_id=" + matchId);
        return buffer.toString();
    }

    /**
     * 循环赛老师发送金币
     * @return
     */
    public static String getMatchDailyRewardtUrl() {
        StringBuffer buffer = getPhpCommonUrl("user/coin/match-daily-award?");
        return buffer.toString();
    }

    /**
     * 比赛时间估计
     * @return
     */
    public static String getEstimateTimeUrl() {
        StringBuffer buffer = getPhpCommonUrl("match/teacher-match/estimate-time?");
        return buffer.toString();
    }
}
