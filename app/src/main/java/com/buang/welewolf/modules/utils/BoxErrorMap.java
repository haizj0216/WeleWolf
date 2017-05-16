/**
 * Copyright (C) 2015 The AndroidPhoneStudent Project
 */
package com.buang.welewolf.modules.utils;

import android.text.TextUtils;

import com.buang.welewolf.base.services.update.UpdateService;
import com.hyena.framework.error.ErrorMap;
import com.hyena.framework.utils.BaseApp;
import com.buang.welewolf.modules.login.services.LoginService;

import java.util.HashMap;

public class BoxErrorMap implements ErrorMap {

    private HashMap<String, String> mErrorMap = null;
	
    public BoxErrorMap() {
    	init();
	}
    
	@Override
	public String getErrorHint(String errorCode, String descript) {
        String hint = descript;

        if ("20009".equals(errorCode)) {
            UpdateService mUpdateService = (UpdateService) BaseApp.getAppContext()
                    .getSystemService(UpdateService.SERVICE_NAME);
            mUpdateService.checkVersion(true);
        } else if ("20014".equals(errorCode)) {
            hint = descript;
        } else if ("20001".equals(errorCode) || "20013".equals(errorCode)
                || "100001".equals(errorCode) || "30003".equals(errorCode)) {
            LoginService mLoginService = (LoginService) BaseApp.getAppContext().getSystemService(LoginService.SERVICE_NAME);
            if (mLoginService.isLogin()) {
                mLoginService.logout(null);
                ToastUtils.showShortToast(BaseApp.getAppContext(), "用户数据异常，请重新登录!");
            }
        }

        if (TextUtils.isEmpty(hint)) {
            return "网络连接异常，请稍候再试!";
        }
        return hint;
	}

	private void init() {
        mErrorMap = new HashMap<String, String>();

        mErrorMap.put("100001", "用户验证失败");
        mErrorMap.put("100002", "请升级到最新版本");
        mErrorMap.put("170001", "请求过于频繁");
        mErrorMap.put("170002", "用户已存在");
        mErrorMap.put("170003", "用户不存在");
        mErrorMap.put("170004", "验证码错误");
        mErrorMap.put("170005", "密码错误");
        mErrorMap.put("170006", "短信发送失败");
        mErrorMap.put("170007", "验证码已过期");
        mErrorMap.put("170101", "超过老师最大班级数量");
        mErrorMap.put("170102", "创建班级失败");
        mErrorMap.put("170103", "没有权限查看该班级");
        mErrorMap.put("170104", "没有权限查看该学生");
        mErrorMap.put("170105", "班级信息更新失败");
        mErrorMap.put("170106", "班级信息不存在");
        mErrorMap.put("170107", "班级不允许学生加入");
        mErrorMap.put("170108", "超过班级学生最大数量");
        mErrorMap.put("170109", "班级解散失败");
        mErrorMap.put("170110", "班级已经解散");
        mErrorMap.put("170111", "已经加入该班群");
        mErrorMap.put("170112", "加入班级失败");
        mErrorMap.put("170113", "未加入该班级");
        mErrorMap.put("170114", "退出班级失败");
        mErrorMap.put("170115", "超过加入班级最大数量");
        mErrorMap.put("170116", "用户信息不符");
        mErrorMap.put("170117", "班级学生为空");
        mErrorMap.put("170118", "班群不允许解散");
        mErrorMap.put("170119", "学生不在该班级");
        mErrorMap.put("170201", "学校不存在");
        mErrorMap.put("170301", "金币不足");
        mErrorMap.put("170302", "已经领过金币");
        mErrorMap.put("170303", "金币接受者不存在");
        mErrorMap.put("180001", "比赛不存在");
        mErrorMap.put("180002", "服务端数据异常");
        mErrorMap.put("180007", "您已经布置了一场比赛");
        mErrorMap.put("200001", "用户异常");
        mErrorMap.put("200002", "帐号异常");
        mErrorMap.put("200004", "已截止");
        mErrorMap.put("200005", "操作过频繁，请稍后再试");
        mErrorMap.put("200006", "手机号码格式有误");
        mErrorMap.put("200008", "新手任务未完成");
        mErrorMap.put("200009", "正在审核中");
        mErrorMap.put("200010", "用户非作业盒子老用户");
        mErrorMap.put("200011", "请勿重复领取");

        /**
         * 作业盒子errorcode
         */

        mErrorMap.put("10001", "服务器错误");
        mErrorMap.put("10002", "服务器错误");
        mErrorMap.put("10003", "推送错误");
        mErrorMap.put("10004", "提交失败");

        mErrorMap.put("20000", "参数错误");
        mErrorMap.put("20001", "Token失效");
        mErrorMap.put("20002", "请求动作不存在");
        mErrorMap.put("20013", "学生不存在");
        mErrorMap.put("20015", "该学生未提交本次布置的作业");
        mErrorMap.put("20201", "账号密码错误");
        mErrorMap.put("20202", "账号已存在");
        mErrorMap.put("20203", "deviceToken不存在");
        mErrorMap.put("20204", "账号不存在，请核实后再试");
        mErrorMap.put("20205", "密码错误,请核实后再试");
        mErrorMap.put("20206", "原密码错误");
        mErrorMap.put("20301", "重复加入班群");
        mErrorMap.put("20302", "班群已关闭");
        mErrorMap.put("20303", "班群已经不存在");

        mErrorMap.put("20004", "对象不存在");
        mErrorMap.put("20304", "做题已经被收藏");
        mErrorMap.put("20305", "做题不存在");
        mErrorMap.put("20306", "做题已经被不是事");
        mErrorMap.put("20601", "作业已经被老师撤销");
        mErrorMap.put("20701", "意见提交失败");
        mErrorMap.put("20005", "作业还没提交，请稍后再试");//作业不存在
        mErrorMap.put("20402", "已经yo过了");

        mErrorMap.put("20801", "配对失败");
        mErrorMap.put("20901", "学生不存在");
        mErrorMap.put("21001", "班群已经关闭");
        mErrorMap.put("21002", "班群已经开启");
        mErrorMap.put("21003", "该年级不存在该学科");
        mErrorMap.put("20006", "题目不存在");
        mErrorMap.put("20007", "班群不存在");
        mErrorMap.put("20502", "邀请码不存在");
        mErrorMap.put("20503", "学校不存在或关闭");
        mErrorMap.put("20008", "题数为0");
        mErrorMap.put("20503", "学校不存在或关闭");
        mErrorMap.put("20504", "邀请码已被验证");
        mErrorMap.put("20307", "最后一个班群");
        mErrorMap.put("20010", "题目数不对");
        mErrorMap.put("20016", "没有找到要转移的老师");
        mErrorMap.put("20017", "不能跨科目转移班群");
        mErrorMap.put("20018", "转移班群失败");

        mErrorMap.put("20009", "强制升级");
        mErrorMap.put("20003", "推送失败");
        mErrorMap.put("21101", "标签为空");
        mErrorMap.put("21102", "标签不存在");
        mErrorMap.put("20011", "答案不存在，请稍后再试");
        mErrorMap.put("20012", "提交文件不存在");
        mErrorMap.put("20013", "学生不存在");
        mErrorMap.put("21201", "已经赞了");
        mErrorMap.put("21202", "已经推荐");
        mErrorMap.put("21203", "作业过期");
        mErrorMap.put("20014", "自定义错误");
        mErrorMap.put("20501", "老师账号存在");
        mErrorMap.put("21204", "不能收藏");

        mErrorMap.put("21205", "已经收藏过了");
        mErrorMap.put("21206", "该题没有收藏");
        mErrorMap.put("21207", "截止时间早于布置时间");
        mErrorMap.put("21208", "刚刚催过作业了");
        mErrorMap.put("21004", "不能创建该学段的班群");
        mErrorMap.put("20505", "账号不存在");
        mErrorMap.put("20506", "验证码错误");
        mErrorMap.put("20507", "验证码过期");
        mErrorMap.put("20508", "验证码错误");


        mErrorMap.put("30001", "参数错误");
        mErrorMap.put("30002", "上传参数错误");
        mErrorMap.put("30003", "账号数据异常");//token格式错误
        mErrorMap.put("30004", "老师信息不存在");
        mErrorMap.put("30005", "密码检效失败");
        mErrorMap.put("30006", "非授权老师");
        mErrorMap.put("30007", "老师无作业权限");
        mErrorMap.put("30008", "老师无班群权限");
        mErrorMap.put("30013", "获取分享链接失败");
        mErrorMap.put("31002", "获取默认分组失败");
        mErrorMap.put("31003", "分组不存在");
        mErrorMap.put("31004", "存在同名分组");
        mErrorMap.put("31012", "题目已保存在该组");
        mErrorMap.put("31006", "未找到作业");
        mErrorMap.put("30020", "查看更多题目请登录并完善个人信息");
        mErrorMap.put("37006", "没有金币了");
        mErrorMap.put("37007", "还没有完成新手任务");
        mErrorMap.put("37009", "暂时没有钻石了");
        mErrorMap.put("37010", "已经给TA发过钻石了");

        mErrorMap.put("40033", "邮件发送失败");

        mErrorMap.put("31013", "暂时没有发现错题");



    }

}
