/**
 * Copyright (C) 2015 The Android_WorkBox Project
 */
package com.knowbox.teacher.modules.utils;

import android.text.format.DateFormat;

import com.easemob.util.DateUtils;
import com.easemob.util.TimeInfo;
import com.hyena.framework.utils.BaseApp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {

	private static final String[] dayofWeek = new String[]{"周日","周一","周二","周三","周四","周五","周六"};
//	private static final String[] monthOfYear = new String[]{"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"};
	private static SimpleDateFormat mDayFmt = new SimpleDateFormat("dd日", Locale.getDefault());
	private static SimpleDateFormat mDateFmt = new SimpleDateFormat("MM-dd", Locale.getDefault());
	private static SimpleDateFormat mHourFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
	private static final SimpleDateFormat mYMDFmt = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
	
	public static String getTimeString(long time, long nowTime){
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(new Date(nowTime * 1000l));
		
		Date date = new Date(time * 1000l);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		StringBuffer buffer = new StringBuffer();
		if(nowTime > 0 && isToday(nowCalendar, calendar) && !isMonth(nowCalendar, calendar)){
			buffer.append("今天 ");
			buffer.append(mHourFmt.format(date));
		}else if(nowTime > 0 && isNextDay(nowCalendar, calendar) && !isMonth(nowCalendar, calendar)){
			buffer.append("明天 ");
			buffer.append(mHourFmt.format(date));
		}else if(nowTime > 0 && isLastDay(nowCalendar, calendar) && !isMonth(nowCalendar, calendar)){
			buffer.append("昨天 ");
			buffer.append(mHourFmt.format(date));
		}else{
			buffer.append(mDateFmt.format(date));
			buffer.append(" ");
			try {
				buffer.append(dayofWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
			} catch (Exception e) {
			}
			buffer.append(" ");
			buffer.append(mHourFmt.format(date));
		}
		return buffer.toString();
	}

	public static String getDefaultYMDDate(long date) {
		return mYMDFmt.format(date * 1000L);
	}

	public static String getDefaultTimeString(long nowTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		return sdf.format(nowTime * 1000l);
	}

	public static String getTime(long date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		return sdf.format(date * 1000L);
	}

	public static String getMonthDayString(long time) {
		Date date = new Date(time * 1000l);
		return mDateFmt.format(date);
	}

	public static String getTimeWithoutMonthString(long time, long nowTime){
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(new Date(nowTime * 1000l));
		
		Date date = new Date(time * 1000l);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		StringBuffer buffer = new StringBuffer();
		if(nowTime > 0 && isToday(nowCalendar, calendar)){
			buffer.append("今天");
			SimpleDateFormat mDayFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
			buffer.append(mDayFmt.format(date));
//			buffer.append(mHourFmt.format(date));
		}else if(nowTime > 0 && isNextDay(nowCalendar, calendar)){
			buffer.append("明天");
			SimpleDateFormat mDayFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
			buffer.append(mDayFmt.format(date));
			//			buffer.append(mHourFmt.format(date));
		}else if(nowTime > 0 && isNext2Day(nowCalendar, calendar)){
			buffer.append("后天");
			SimpleDateFormat mDayFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
			buffer.append(mDayFmt.format(date));
			//			buffer.append(mHourFmt.format(date));
		}else if(nowTime > 0 && isLastDay(nowCalendar, calendar)){
			buffer.append("昨天");
			SimpleDateFormat mDayFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
			buffer.append(mDayFmt.format(date));
			//			buffer.append(mHourFmt.format(date));
		}else if(nowTime >0 && isLast2Day(nowCalendar, calendar)){
			buffer.append("前天");
			SimpleDateFormat mDayFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
			buffer.append(mDayFmt.format(date));
		}else if (nowTime > 0 && isSameYear(nowCalendar, calendar)){
			SimpleDateFormat mDayFmt = new SimpleDateFormat("MM月dd日HH:mm", Locale.getDefault());
			buffer.append(mDayFmt.format(date));
		} else {
			SimpleDateFormat mDayFmt = new SimpleDateFormat("yyyy年MM月dd日HH:mm", Locale.getDefault());
			buffer.append(mDayFmt.format(date));
		}
//		SimpleDateFormat mDayFmt = new SimpleDateFormat("HH-mm", Locale.getDefault());
//		buffer.append(mDayFmt.format(date)+"发布");
		return buffer.toString();
	}
	
	public static String getTime(long time){
		return mHourFmt.format(new Date(time * 1000l));
	}

	public static String getMessageTimeString(Date date) {
		String var1 = null;
		long var2 = date.getTime();
		if(isSameDay(var2)) {
			Calendar var4 = GregorianCalendar.getInstance();
			var4.setTime(date);
			DateFormat format = new DateFormat();
			if (format.is24HourFormat(BaseApp.getAppContext())) {
				var1 = "HH:mm";
			} else {
				int var5 = var4.get(11);
				if(var5 > 17) {
					var1 = "晚上 hh:mm";
				} else if(var5 >= 0 && var5 <= 6) {
					var1 = "凌晨 hh:mm";
				} else if(var5 > 11 && var5 <= 17) {
					var1 = "下午 hh:mm";
				} else {
					var1 = "上午 hh:mm";
				}
			}

		} else if(isYesterday(var2)) {
			var1 = "昨天 HH:mm";
		} else {
			var1 = "M月d日 HH:mm";
		}

		return (new SimpleDateFormat(var1, Locale.CHINA)).format(date);
	}

	private static boolean isSameDay(long var0) {
		TimeInfo var2 = DateUtils.getTodayStartAndEndTime();
		return var0 > var2.getStartTime() && var0 < var2.getEndTime();
	}

	public static boolean isYesterday(long var0) {
		TimeInfo var2 = DateUtils.getYesterdayStartAndEndTime();
		return var0 > var2.getStartTime() && var0 < var2.getEndTime();
	}

	/**
	 *
	 * @param time
	 * @return
     */
	public static String getDayString(long time) {
		Calendar now = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time * 1000);

		SimpleDateFormat sdf;
		if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR)) {
			sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
		} else {
			sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		}
		return sdf.format(new Date(time * 1000));
	}

	public static String getYearMonthDayString(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd", Locale.getDefault());
		return sdf.format(new Date(time * 1000));
	}

	
	/**
	 * 获得月份
	 * @param time
	 * @return
	 */
	public static String getMonth(long time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM月", Locale.getDefault());
			return sdf.format(new Date(time * 1000l));
//			Calendar nowCalendar = Calendar.getInstance();
//			nowCalendar.setTime(new Date(time * 1000l));
//			int month = nowCalendar.get(Calendar.MONTH);
//			return monthOfYear[month];
		} catch (Exception e) {
		}
		return "未知";
	}

	/**
	 * 获得月份
	 * @param time
	 * @return
	 */
	public static String getYearAndMonth(long time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
			return sdf.format(new Date(time * 1000l));
		} catch (Exception e) {
		}
		return "未知";
	}

	/**
	 * 获取星期
	 * @param time
	 * @return
	 */
	public static String getWeek(long time) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(time * 1000l));
		return dayofWeek[c.get(Calendar.DAY_OF_WEEK) - 1];
	}

	/**
	 * 获取日
	 * @param time
	 * @return
	 */
	public static String getDay(long time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
			return sdf.format(new Date(time * 1000l));
//			Calendar nowCalendar = Calendar.getInstance();
//			nowCalendar.setTime(new Date(time * 1000l));
//			int month = nowCalendar.get(Calendar.MONTH);
//			return monthOfYear[month];
		} catch (Exception e) {
		}
		return "未知";
	}

	public static String getWithoutYearTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
		return sdf.format(new Date(time * 1000l));
	}

	public static String getMonthAndDay(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
		return sdf.format(new Date(time * 1000l));
	}

	public static boolean isNextDay(Calendar now, Calendar time){
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(now.getTime());
		nowCalendar.add(Calendar.DAY_OF_MONTH, 1);
		return nowCalendar.get(Calendar.DAY_OF_MONTH) == time.get(Calendar.DAY_OF_MONTH) && isMonth(now, time);
	}

	public static boolean isNext2Day(Calendar now, Calendar time){
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(now.getTime());
		nowCalendar.add(Calendar.DAY_OF_MONTH, 2);
		return nowCalendar.get(Calendar.DAY_OF_MONTH) == time.get(Calendar.DAY_OF_MONTH) && isMonth(now, time);
	}

	public static boolean isLast2Day(Calendar now, Calendar time){
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(now.getTime());
		nowCalendar.add(Calendar.DAY_OF_MONTH, -2);
		return nowCalendar.get(Calendar.DAY_OF_MONTH) == time.get(Calendar.DAY_OF_MONTH) && isMonth(now, time);
	}

	public static boolean isLastDay(Calendar now, Calendar time){
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(now.getTime());
		nowCalendar.add(Calendar.DAY_OF_MONTH, -1);
		return nowCalendar.get(Calendar.DAY_OF_MONTH) == time.get(Calendar.DAY_OF_MONTH) && isMonth(now, time);
	}

	public static boolean isToday(Calendar now, Calendar time){
		return now.get(Calendar.DAY_OF_MONTH) == time.get(Calendar.DAY_OF_MONTH) && isMonth(now, time) ;
	}

	public static boolean isMonth(Calendar now, Calendar time) {
		return now.get(Calendar.MONTH) == time.get(Calendar.MONTH) && isSameYear(now, time);
	}

	public static boolean isSameYear(Calendar now, Calendar time) {
		return now.get(Calendar.YEAR) == time.get(Calendar.YEAR);
	}

	public static String parseDate2String(Date date, String format,
										  Locale locale) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
		return sdf.format(date);
	}

	public static String getTimeString2(long time) {
		Date date = new Date(time * 1000L);
		return parseDate2String(date, "HH:mm", Locale.getDefault());
	}

	public static String timeParse(long duration) {
		String time = "" ;

		long minute = duration / 60000 ;
		long seconds = duration % 60000 ;

		long second = Math.round((float)seconds/1000) ;

		if( minute < 10 ){
			time += "0" ;
		}
		time += minute+":" ;

		if( second < 10 ){
			time += "0" ;
		}
		time += second ;

		return time ;
	}

	public static String getSelfTrainRankTime(long time) {
		Date date = new Date(time * 1000L);
		return parseDate2String(date, "MM-dd", Locale.getDefault());
	}

	public static String getMonthDay(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.getDefault());
		return sdf.format(calendar.getTime());
	}

	public static String getMonthDay(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.getDefault());
		return sdf.format(new Date(time * 1000L));
	}

	public static String getHourMintute(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
		return sdf.format(calendar.getTime());
	}

	public static String getHourMintute(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
		return sdf.format(new Date(time * 1000L));
	}

	/**
	 *
	 * @param start
	 * @param end
     * @return
     */
	public static boolean compMonthDay(Calendar start, Calendar end) {
		if (start.get(Calendar.YEAR) > end.get(Calendar.YEAR)) {
			return false;
		}
		if (start.get(Calendar.MONTH) > end.get(Calendar.MONTH)) {
			return false;
		}
		if (start.get(Calendar.DAY_OF_MONTH) >= end.get(Calendar.DAY_OF_MONTH)) {
			return false;
		}
		return true;
	}

	public static boolean compHourMin(Calendar start, Calendar end) {
		if (start.get(Calendar.HOUR_OF_DAY) > end.get(Calendar.HOUR_OF_DAY)) {
			return false;
		}
		if (start.get(Calendar.MINUTE) > end.get(Calendar.MINUTE)) {
			return false;
		}
		return true;
	}
}
