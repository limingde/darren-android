package com.dd.datastatistics.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

import com.dd.datastatistics.constant.DataStaMeilaConfig;

public class DataStaCalendarUtils {
	static final String TAG = "CalendarUtils";
	
	public static final int NOT_PLAN = 0;
	public static final int MISS = 1;
	public static final int PLAN = 2;
	public static final int PERFECTION = 3;

	public static final int TODAY = 1;
	public static final int BEFORE = 2;
	public static final int AFTER = 3;

	private final static String WEEK_NUMBER[] = { "鏃", "涓", "浜", "涓", "鍥", "浜",
			"鍏"};
	

	public static String LeftPadTowZero(int str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);

	}

	private static SimpleDateFormat yearMonthFormat = new SimpleDateFormat(
			"yyyy/MM");
	private static SimpleDateFormat monthDayFormat = new SimpleDateFormat(
			"MM鏈坉d鏃");
	private static SimpleDateFormat chDateFormat = new SimpleDateFormat(
			"MM鏈坉d鏃� HH:mm");
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static SimpleDateFormat longDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
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
		return simpleDateFormat.format(calendar.getTime());
	}
	public static String getDay(Date date) {
		return simpleDateFormat.format(date.getTime());
	}

	public static String getTime(long seconds) {
//		return longDateFormat.format(parseDate(seconds));
		return longDateFormat.format(seconds*1000);
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
		Date date = parseDate(DataStaMeilaConfig.currentTimeSec());
		return minuteFormat.format(date);
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
		return "鏄熸湡" + WEEK_NUMBER[calendar.get(Calendar.DAY_OF_WEEK) - 1] + "";
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
}
