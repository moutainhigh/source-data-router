package com.globalegrow.dyCubeBuildService.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * 类描述：时间操作定义类
 * 
 */
public class DateUtils extends PropertyEditorSupport {

	private static Logger log = LoggerFactory.getLogger(DateUtils.class);

    /**
     * 校验日期格式
     *
     * @param str
     *            日期字符串
     * @param dateFormat
     *            格式 例如:"yyyy-MM-dd"
     * @return
     */
    public static boolean isValidDate(String str, String dateFormat) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

	/**
	 * 当前日历，这里用中国时间表示
	 * 
	 * @return 以当地时区表示的系统当前日历
	 */
	public static Calendar getCalendar() {
		return Calendar.getInstance();
	}

	/**
	 * 默认方式表示的系统当前日期，具体格式：年-月-日
	 * 
	 * @return 默认日期按“年-月-日“格式显示
	 */
	public static String formatDate() {
		return formatDateSdf().format(getCalendar().getTime());
	}

	/**
	 * 默认方式表示的系统当前日期加n天，具体格式：年-月-日
	 *
	 * @return 默认日期按“年-月-日“格式显示
	 */
	public static String formatDate(int n) {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.DAY_OF_MONTH, n);
		return formatDateSdf().format(calendar.getTime());
	}

	/**
	 * 默认方式表示的系统当前日期，具体格式：年月日
	 * 
	 * @return 默认日期按“年月日“格式显示
	 */
	public static String formatDate2() {
		return formatDateSdf2().format(getCalendar().getTime());
	}
	
	public static int hasDaysFromNow(String endTime, int effectDays) {
		Date endDate = null;
		try {
			endDate = formatDateSdfHms().parse(endTime);
		} catch (ParseException e) {
			log.error("hasDaysFromNow.parse(" + endTime + ") error:" + e.getMessage());
		}
		Date nowDate = new Date();
		long endTimeToNow = endDate.getTime() - nowDate.getTime();
		if (endTimeToNow <= 0) {
			return 0;
		} else {
			int hasDaysFromNow = (int) ((endTimeToNow) / (24 * 60 * 60 * 1000)) + 1;
			if (hasDaysFromNow < effectDays) {
				return hasDaysFromNow;
			} else {
				return effectDays;
			}
		}
	}

	/**
	 * 指定日期的默认显示，具体格式：年-月-日
	 * 
	 * @param cal
	 *            指定的日期
	 * @return 指定日期按“年-月-日“格式显示
	 */
	public static String formatDate(Calendar cal) {
		return formatDateSdf().format(cal.getTime());
	}

	/**
	 * 指定日期的默认显示，具体格式：年-月-日
	 * 
	 * @param date
	 *            指定的日期
	 * @return 指定日期按“年-月-日“格式显示
	 */
	public static String formatDate(Date date) {
		return formatDateSdf().format(date);
	}

	/**
	 * 指定日期字符串转为Date格式
	 * 
	 * @param str
	 *            指定的日期
	 * @return Date
	 * @throws ParseException
	 */
	public static Date parseDate(String str)  {
		if (null == str || "".equals(str)) {
			return null;
		}
		try {
			return formatDateSdfHms().parse(str);
		} catch (ParseException e) {
			log.error("formatDateSdfHms().parse(" + str + ") error:" + e.getMessage());
		}
		return null;
	}

	/**
	 * 指定日期的默认显示，具体格式：年-月-日 时：分：秒
	 * 
	 * @param date
	 *            指定的日期
	 * @return 指定日期按“年-月-日 时：分：秒“格式显示
	 */
	public static String formatDateHms(Date date) {
		return formatDateSdfHms().format(date);
	}

	/**
	 * 获取当前是星期几
	 *
	 * @return 指定日期按“年-月-日“格式显示
	 */
	public static int getDayOfWeek() {
		return getCalendar().get(Calendar.DAY_OF_WEEK) - 1;
	}

	/**
	 * 判断是否同一天，忽略时分秒
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		int year1 = calendar1.get(Calendar.YEAR);
		int month1 = calendar1.get(Calendar.MONTH);
		int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
		int year2 = calendar2.get(Calendar.YEAR);
		int month2 = calendar2.get(Calendar.MONTH);
		int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
		if (year1 == year2 && month1 == month2 && day1 == day2) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 指定日期是否在某段时间内
	 * 
	 * @param fromTime
	 * @param toTime
	 * @param thisTime
	 * @return
	 */
	public static boolean isBetweenTime(String fromTime, String toTime, Date thisTime) {
		long fromDate = 0;
		;
		long toDate = 0;
		try {
			fromDate = formatDateSdfHms().parse(fromTime).getTime();
			toDate = formatDateSdfHms().parse(toTime).getTime();

		} catch (ParseException e) {
			log.error("isBetweenTime(String fromTime, String toTime, Date thisTime) error:"
					+ e.getMessage());
		}
		long thisDate = thisTime.getTime();
		if (thisDate > fromDate && thisDate < toDate) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 指定日期是否在某段时间内
	 * 
	 * @param fromTime
	 * @param toTime
	 * @param thisTime
	 * @return
	 */
	public static boolean isBetweenTimeByDate(Date fromTime, Date toTime, Date thisTime) {
		long fromDate = fromTime.getTime();
		long toDate = toTime.getTime();
		long thisDate = thisTime.getTime();
		if (thisDate > fromDate && thisDate < toDate) {
			return true;
		} else {
			return false;
		}
	}

	public static SimpleDateFormat formatDateSdf() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}
	
	public static SimpleDateFormat formatDateSdf2() {
		return new SimpleDateFormat("yyyyMMdd");
	}

	public static SimpleDateFormat formatDateSdfHms() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

}
