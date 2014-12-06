package com.dd.whateat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.dd.whateat.bean.DdConst;

import android.text.TextUtils;

public class CalendarUtils {
	static final String TAG = "CalendarUtils";
	
	public static final int NOT_PLAN = 0;
	public static final int MISS = 1;
	public static final int PLAN = 2;
	public static final int PERFECTION = 3;

	public static final int TODAY = 1;
	public static final int BEFORE = 2;
	public static final int AFTER = 3;

	private final static String WEEK_NUMBER[] = { "日", "一", "二", "三", "四", "五",
			"六" };
	

	public static String LeftPadTowZero(int str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);

	}

	private static SimpleDateFormat yearMonthFormat = new SimpleDateFormat(
			"yyyy/MM");
	private static SimpleDateFormat monthDayFormat = new SimpleDateFormat(
			"MM月dd日");
	private static SimpleDateFormat chDateFormat = new SimpleDateFormat(
			"MM月dd日  HH:mm");
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static SimpleDateFormat yearMonthDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd");
	private static SimpleDateFormat longDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static SimpleDateFormat longDateFormatForOrder = new SimpleDateFormat(
			"yyyy-MM-dd  HH:mm:ss");
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss");
	private static SimpleDateFormat minuteFormat = new SimpleDateFormat(
			"HH:mm");
	public static boolean compare(Date compareDate, Date currentDate) {
		return simpleDateFormat.format(compareDate).compareTo(
				simpleDateFormat.format(currentDate)) >= 0;
	}

	public static Boolean equalsDate(Date date1, Date date2) {
		return simpleDateFormat.format(date1).compareTo(
				simpleDateFormat.format(date2)) == 0;
	}

	public static Boolean compareMoreDate(Date date1, Date date2) {
		return simpleDateFormat.format(date1).compareTo(
				simpleDateFormat.format(date2)) > 0;
	}

	public static Boolean compareLessDate(Date date1, Date date2) {
		return simpleDateFormat.format(date1).compareTo(
				simpleDateFormat.format(date2)) < 0;
	}

	public static String getDay(Calendar calendar) {
		try {
			return simpleDateFormat.format(calendar.getTime());
		} catch (Exception e) {
			DdLog.e(TAG, e.getMessage());
		}
		
		return "";
	}
	public static String getDayKindTwo(Calendar calendar) {
		try {
			return yearMonthDateFormat.format(calendar.getTime());
		} catch (Exception e) {
			DdLog.e(TAG, e.getMessage());
		}
		
		return "";
	}
	
	public static String getDay(Date date) {
		return simpleDateFormat.format(date.getTime());
	}

	public static String getTime(long seconds) {
//		return longDateFormat.format(parseDate(seconds));
		return longDateFormat.format(seconds*1000);
	}
	
	public static String getTimeForOrder(long seconds) {
		try {
			Date date = parseDate(seconds);
			return longDateFormatForOrder.format(date);
		} catch (Exception e) {
			DdLog.e(TAG, e.getMessage());
		}
		return null;
	}
	public static String getTime(Calendar calendar) {
		return longDateFormat.format(calendar.getTime());
	}
	public static String getTime(Date date) {
		return longDateFormat.format(date.getTime());
	}
	public static String getJustTime(Calendar calendar) {
		return timeFormat.format(calendar.getTime());
	}
	public static String getSimple(long seconds) {
		Date date = parseDate(seconds);
		return simpleDateFormat.format(date);
	}
	public static String getJustHourMinute(Date date) {
		return minuteFormat.format(date);
	}
	public static String getJustHourMinuteNow() {
		Date date = parseDate(DdConst.currentTimeSec());
		return minuteFormat.format(date);
	}
	public static String getHourMinuteKindTwo(Calendar calendar) {
		try {
			return minuteFormat.format(calendar.getTime());
		} catch (Exception e) {
			DdLog.e(TAG, e.getMessage());
		}
		
		return "";
	}
	public static String getDateHourMinute(long seconds){
		Date date = parseDate(seconds);
		return getTime(date);
	}
	public static String getMonthDay(long seconds) {
		Date date = parseDate(seconds);
		return monthDayFormat.format(date);
	}
	public static String getYearMonth(long seconds) {
		Date date = parseDate(seconds);
		return yearMonthFormat.format(date);
	}
	public static String getYearMonthDate(long seconds) {
		Date date = parseDate(seconds);
		return yearMonthDateFormat.format(date);
	}
	
	public static String getWeekHourMinute(long seconds){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(seconds*1000);
		return getWeek(calendar) + " " + getJustHourMinute(parseDate(seconds));
	}
	public static String getWeek(long seconds){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(seconds*1000);
		return getWeek(calendar);
	}
	public static String getWeek(Calendar calendar) {
		return "星期" + WEEK_NUMBER[calendar.get(Calendar.DAY_OF_WEEK) - 1] + "";
	}
	public static String getDayOfMonth(String date) {
		return String.format("%te", parseDate(date));
	}
	public static String getEnMonthName(String date) {
		return String.format(Locale.US, "%tb", parseDate(date));
	}
	public static String getChTime(long timeSec) {
		return chDateFormat.format(parseDate(timeSec));
	}
	public static String getYear(String date) {
		return String.format("%tY", parseDate(date));
	}
	public static String getDayOfMonth(long seconds) {
		return String.format("%te", parseDate(seconds));
	}
	public static String getEnMonthName(long seconds) {
		return String.format(Locale.US, "%tb", parseDate(seconds));
	}
	public static String getYear(long seconds) {
		return String.format("%tY", parseDate(seconds));
	}
	
	public static String getMonth(long seconds) {
		return String.format("%tM", parseDate(seconds));
	}

	public static Date parseDate(long seconds) {
		Date date = new Date(seconds*1000);
		return date;
	}
	public static Date parseDate(String strDate) {
		Date date = null;
		try {
			date = simpleDateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}
	
	public static Calendar parseCalendar(String strDate) {
		Calendar c = null;
		Date date = null;
		try {
			date = simpleDateFormat.parse(strDate);
			if (date != null) {
				c = Calendar.getInstance();
				c.setTime(date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return c;
	}
	
	public static Calendar parseHourMinCalendar(String strDate) {
		Calendar c = null;
		Date date = null;
		try {
			date = minuteFormat.parse(strDate);
			if (date != null) {
				c = Calendar.getInstance();
				c.setTime(date);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return c;
	}
	
	public static Date parseLongDate(String strDate) {
		Date date = null;
		try {
			date = longDateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	public static Integer[] getDateList(String birthday) {
		if (!TextUtils.isEmpty(birthday)) {
			String[] list = birthday.split("-");
			if (list.length > 0) {
				Integer[] intList = new Integer[list.length];
				for (int i=0; i<list.length; i++) {
					intList[i] = Integer.parseInt(list[i]);
				}
				
				return intList;
			}
		}
		return null;
	}
	
	static public long getTodayFirstSecond(long nowSec){
		Date d = new Date(nowSec*1000);
		String s = (1900+d.getYear())+"-"+(d.getMonth()+1)+"-"+d.getDate();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d2 = df.parse(s);
//			Date d2 = df.parse("2013-11-15");
//			System.out.println("d2: "+d2.toLocaleString());
//			System.out.println("sys: "+System.currentTimeMillis());
//			System.out.println("d2 : "+d2.getTime());
			return d2.getTime()/1000;
		} catch (ParseException e) {
		}
		return 0;
	}
	
	/**
	 * 计算相隔两个日期的间隔天数
	 * 
	 * 0：日期相等，大于0，startDate未来日子，小于0，startDate过去日子
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDistance(Calendar startDate, Calendar endDate) throws Exception {
		if(startDate == null || endDate == null){
			throw new NullPointerException("date is null");
		}
		
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);

		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.MILLISECOND, 0);

		return (int) ((startDate.getTime().getTime() - endDate.getTime().getTime()) / (1000 * 60 * 60 * 24));  
	}
	
	public static boolean isToday(long seconds){
		Date today = new Date();
		Date d = CalendarUtils.parseDate(seconds);
		if(d.getYear() == today.getYear() && d.getMonth() == today.getMonth() && d.getDate() == today.getDate()){
			return true;
		}
		return false;
	}
	public static boolean isYesterday(long seconds){
		return isToday(seconds + 24*60*60);
	}
	public static boolean isThisWeek(long seconds){
		int day = parseDate(seconds).getDay();
		for(int i=0; i<=day; ++i){
			if(isToday(seconds - 24*60*60*i)){
				return true;
			}
		}
		return false;
	}
	
	public static String getHuatiTimeString(final long time){
		long nowtime = DdConst.currentTimeSec();
		long delta = nowtime - time;
		
		long minSeconds = 60;
		long hourSeconds = 60*minSeconds;
		long daySeconds = 24*hourSeconds;
		long monSeconds = 30*daySeconds;
		long yearSeconds = 12*monSeconds;
		
		long min = delta/minSeconds;
		long hour = delta/hourSeconds;
		long day = delta/daySeconds;
		long mon = delta/monSeconds;
		long year = delta/yearSeconds;
		
		DdLog.d(TAG, "getTimeString, nowtime: "+nowtime+", time: "+time+", year:"+year);
		
		if(year>0){
			return year+"年前"; 
		}
		if(mon>0){
			return mon+"个月前";
		}
		if(day>0){
			return day+"天前";
		}
		if(hour>0){
			return hour+"小时前";
		}
		if(min>0){
			return min+"分钟前";
		}
		return "刚刚";
	}
}
