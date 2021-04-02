package org.jftone.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {

	public static final String FMT_MONTH = "yyyy-MM";
	public static final String FMT_DATE = "yyyy-MM-dd";
	public static final String FMT_DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String FMT_MINUTE = "yyyy-MM-dd HH:mm";
	public static final String DIGI_YEAR = "yyyy";
	public static final String DIGI_MONTH = "yyyyMM";
	public static final String DIGI_DATE = "yyyyMMdd";
	public static final String DIGI_HOUR = "yyyyMMddHH";
	public static final String DIGI_MINUTE = "yyyyMMddHHmm";
	public static final String DIGI_DATETIME = "yyyyMMddHHmmss";
	public static final String DIGI_TIMESTAMP = "yyyyMMddHHmmssSSS";
	public static final String DIGI_SHORT_MONTH = "yyMM";
	public static final String DIGI_SHORT_DATE = "yyMMdd";

	/**
	 * 按照指定的格式将字符串转换为日期类型
	 * 
	 * @param dateStr
	 * @param format
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String dateStr, String format)
			throws ParseException {
		Date dateRs = null;
		if (dateStr != null && format != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			dateRs = sdf.parse(dateStr);
		}
		return dateRs;
	}

	/**
	 * 按照指定的格式将日期类型转换为字符串
	 * 
	 * @param date
	 * @param format
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String format(Date date, String format) {
		String str = null;
		if (date != null && format != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			str = sdf.format(date);
		}
		return str;
	}

	/**
	 * 默认日期转换类型
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDatetime(String dateStr) throws ParseException {
		return parse(dateStr, FMT_DATETIME);
	}

	public static Date parseDate(String dateStr) throws ParseException {
		return parse(dateStr, FMT_DATE);
	}

	/**
	 * 毫秒转时间
	 * 
	 * @param millis
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(long millis) throws ParseException {
		Calendar cal = getCalendar(null);
		cal.setTimeInMillis(millis);
		return cal.getTime();
	}

	/**
	 * 默认字符串转换类型
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDatetime(Date date) {
		return format(date, FMT_DATETIME);
	}

	public static String formatDate(Date date) {
		return format(date, FMT_DATE);
	}

	/**
	 * 计算两个时间差，返回长整型
	 * 
	 * @param maxDate
	 * @param minDate
	 * @return
	 */
	public static long intervalDate(Date maxDate, Date minDate) {
		return (maxDate.getTime() - minDate.getTime());
	}

	public static boolean great(Date maxDate, Date minDate) {
		Calendar calMax = getCalendar(null);
		Calendar calMin = getCalendar(null);
		calMax.setTime(maxDate);
		calMin.setTime(minDate);
		return calMax.after(calMin);
	}

	/**
	 * 获取给定月份数的间隔时间，从给定的日期开始推算 month是正整数，表示给定时间往后，为负整数，表示给定时间往前
	 * 
	 * @param inDate
	 * @param month
	 * @return
	 */
	public static Date intervalMonth(Date inDate, int month) {
		Calendar cal = getCalendar(inDate);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}

	/**
	 * 获取给定天数的间隔时间，从给定的日期开始推算 day是正整数，表示给定时间往后，为负整数，表示给定时间往前
	 * 
	 * @param inDate
	 * @param day
	 * @return
	 */
	public static Date intervalDay(Date inDate, int day) {
		Calendar cal = getCalendar(inDate);
		cal.add(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}

	/**
	 * 获取给定天数的间隔时间，从当前日期开始推算
	 * 
	 * @param day
	 * @return
	 */
	public static Date intervalDay(int day) {
		return intervalDay(null, day);
	}

	/**
	 * 获取给定Calendar形式的间隔时间，从给定的日期开始推算 counter是正整数，表示给定时间往后，为负整数，表示给定时间往前
	 * 
	 * @param inDate
	 * @param calFmt
	 * @param counter
	 * @return
	 */
	public static Date intervalDate(Date inDate, int calFmt, int count) {
		Calendar cal = getCalendar(inDate);
		cal.add(calFmt, count);
		return cal.getTime();
	}
	public static Date intervalDate(int calFmt, int count) {
		return intervalDate(null, calFmt, count);
	}
	
	/**
	 * 设置给定Calendar形式的时间
	 * @param inDate
	 * @param calFmt
	 * @param value
	 * @return
	 */
	public static Date setDate(Date inDate, int calFmt, int value) {
		Calendar cal = getCalendar(inDate);
		cal.set(calFmt, value);
		return cal.getTime();
	}
	public static Date setDate(int calFmt, int count) {
		return setDate(null, calFmt, count);
	}
	/**
	 * 获取当前日期时间字符串类型 形如：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getNowStr() {
		return formatDatetime(getNow());
	}

	public static String getNowStr(String format) {
		return format(getNow(), format);
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static Date getNow() {
		return getCalendar(null).getTime();
	}
	public static Calendar getCalendar(Date inDate) {
		Calendar cal = Calendar.getInstance();
		if(inDate != null){
			cal.setTime(inDate);
		}
		return cal;
	}
	
	/**
	 * 获取当前时间毫秒数
	 * 
	 * @return
	 */
	public static long getMillisecond() {
		return getMillisecond(null);
	}

	public static long getMillisecond(Date date) {
		Calendar cal = getCalendar(date);
		return cal.getTimeInMillis();
	}
	
	/**
	 * 获取给定时间秒，毫秒设置为0
	 * @param date
	 * @return
	 */
	public static Date getSecondDate(Date date) {
		Calendar cal = getCalendar(date);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	public static Date getSecondDate() {
		return getSecondDate(null);
	}
	
	/**
	 * 获取给定时间分，毫秒，秒设置为0
	 * @param date
	 * @return
	 */
	public static Date getMinuteDate(Date date) {
		Calendar cal = getCalendar(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	public static Date getMinuteDate() {
		return getMinuteDate(null);
	}
	
	/**
	 * 获取给定时间小时，毫秒，秒，分设置为0
	 * @param date
	 * @return
	 */
	public static Date getHourDate(Date date) {
		Calendar cal = getCalendar(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		return cal.getTime();
	}
	public static Date getHourDate() {
		return getHourDate(null);
	}
	
	/**
	 * 获取今天开始时间0点0分0秒时间
	 * @return
	 */
	public static Date getBeginTime() {
		Calendar cal = getCalendar(null);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 获取今天结束时间,23点59分59秒时间
	 * @return
	 */ 
	public static Date getLastTime() {
		Calendar cal = getCalendar(null);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	/**
	 * 获取当月第一天的0点0分0秒时间
	 * @return
	 */
	public static Date getMonthBeginTime() {
		Calendar cal = getCalendar(null);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 获取当月最后的最后一天23点59分59秒时间
	 * @return
	 */
	public static Date getMonthLastTime() {
		Calendar cal = getCalendar(null);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

}
