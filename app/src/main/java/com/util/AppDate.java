package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期时间类
 * 
 * @author adam
 * 
 */

/**
 * 日期时间类
 * 
 * @author adam
 * 
 */
public class AppDate {

	private GregorianCalendar gc = new GregorianCalendar();
	private boolean mInvalid = false;

	public AppDate() {
	}

	public AppDate(Date date) {

		setTime(date);
	}

	public AppDate(String datetime) {
		setDateTime(datetime);
	}

	public AppDate(long milliseconds) {
		setTimeInMillis(milliseconds);
	}

	public long getTimeInMillis() {
		return mInvalid ? 0l : gc.getTimeInMillis();
	}

	public void setTime(Date date) {
		if (date == null) {
			mInvalid = true;
		} else {
			gc.setTime(date);
		}
	}

	public void setDateTime(String value) {
		if (value == null) {
			mInvalid = true;
			return;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			setTime(df.parse(value));
		} catch (ParseException e) {
			AppLog.error(e);
		}
	}

	public void setDate(String value) {
		if (value == null)
			return;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			setTime(df.parse(value));
		} catch (ParseException e) {
			AppLog.error(e);
		}
	}

	public void setTimeInMillis(long milliseconds) {
		gc.setTimeInMillis(milliseconds);
	}

	/**
	 * 年份
	 * 
	 * @return
	 */
	public void setYear(int value) {
		gc.set(Calendar.YEAR, value);
	}

	/**
	 * 月份(正确)<br />
	 * 注意已-1
	 * 
	 * @return
	 */
	public void setMonth(int value) {
		gc.set(Calendar.MONTH, value - 1);
	}

	/**
	 * 日(月中的)
	 * 
	 * @return
	 */
	public void setDay(int value) {
		gc.set(Calendar.DAY_OF_MONTH, value);
	}

	/**
	 * 小时(24小时)
	 * 
	 * @return
	 */
	public void setHour(int value) {
		gc.set(Calendar.HOUR_OF_DAY, value);
	}

	/**
	 * 分钟
	 * 
	 * @return
	 */
	public void setMinute(int value) {
		gc.set(Calendar.MINUTE, value);
	}

	/**
	 * 秒
	 * 
	 * @return
	 */
	public void setSecond(int value) {
		gc.set(Calendar.SECOND, value);
	}

	/**
	 * 毫秒
	 * 
	 * @return
	 */
	public void setMilliSecond(int value) {
		gc.set(Calendar.MILLISECOND, value);
	}

	/**
	 * 年份
	 * 
	 * @return
	 */
	public int getYear() {
		return gc.get(Calendar.YEAR);
	}

	/**
	 * 月份(正确)<br />
	 * 注意已+1
	 * 
	 * @return
	 */
	public int getMonth() {
		return gc.get(Calendar.MONTH) + 1;
	}

	/**
	 * 日(月中的)
	 * 
	 * @return
	 */
	public int getDay() {
		return gc.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 小时(24小时)
	 * 
	 * @return
	 */
	public int getHour() {
		return gc.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 分钟
	 * 
	 * @return
	 */
	public int getMinute() {
		return gc.get(Calendar.MINUTE);
	}

	/**
	 * 秒
	 * 
	 * @return
	 */
	public int getSecond() {
		return gc.get(Calendar.SECOND);
	}

	/**
	 * 毫秒
	 * 
	 * @return
	 */
	public int getMilliSecond() {
		return gc.get(Calendar.MILLISECOND);
	}

	/**
	 * 改函数未经过任何处理,使用时小心月份的问题<br />
	 * Adds the specified amount to a {@code Calendar} field.
	 * 
	 * @param field
	 *            the {@code Calendar} field to modify.
	 * @param value
	 *            the amount to add to the field.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified field is DST_OFFSET or ZONE_OFFSET.
	 */
	public void add(int field, int value) {
		gc.add(field, value);
	}

	/**
	 * 日期
	 * 
	 * @return yyyy-MM-dd
	 */
	public String getDate() {
		return mInvalid ? null : String.format("%04d-%02d-%02d", getYear(), getMonth(), getDay());
	}

	/**
	 * 时间
	 * 
	 * @return HH:mm:ss
	 */
	public String getTime() {
		return mInvalid ? null : String.format("%02d:%02d:%02d", getHour(), getMinute(), getSecond());
	}

	/**
	 * 日期时间
	 * 
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public String getDateTime() {
		// SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// return df.format(gc.getTime());

		return mInvalid ? null
				: String.format("%04d-%02d-%02d %02d:%02d:%02d", getYear(), getMonth(), getDay(), getHour(),
						getMinute(), getSecond());
	}

	/**
	 * 字符转日期，固定格式yyyy-MM-dd
	 * 
	 * @param sDate
	 * @return
	 */

	static public Date getDateFormat(String sDate) {
		Date dt = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			dt = dateFormat.parse(sDate);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return dt;
	}

	/**
	 * 日期转字符，固定格式yyyy-MM-dd
	 * 
	 * @param sDate
	 * @return
	 */
	static public String getDateFormat(Date sDate) {
		String date = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = dateFormat.format(sDate);
		} catch (Exception ex) {
			date = "";
		}

		return date;
	}

	/**
	 * 日期转字符，固定格式yyyy-MM-dd
	 * 
	 * @param sDate
	 * @return
	 */
	static public String getDateFormat_str(String sDate) {
		String date = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = dateFormat.format(dateFormat.parse(sDate));
		} catch (Exception ex) {
			date = "";
		}
		return date;
	}

	/**
	 * 字符转日期，带格式
	 * 
	 * @param sDate
	 * @param sFormat
	 * @return
	 */
	static public Date getDateFormat(String sDate, String sFormat) {
		Date dt = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(sFormat);
		try {
			dt = dateFormat.parse(sDate);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return dt;
	}

	/**
	 * 日期比较,不比较时间 0 相等,-1 date1 < date2,1 date1 > date2,99 无法比较
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDate(String date1, String date2) {
		int ret = 99;
		
		date1 = date1 + " 00:00:00";
		date2 = date2 + " 00:00:00";
		
		Date dt1 = new Date();
		Date dt2 = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			dt1 = dateFormat.parse(date1);
			dt2 = dateFormat.parse(date2);

			int val = dt1.compareTo(dt2);
			if (val == 0) {
				ret = 0;
			} else if (val < 0) {
				ret = -1;
			} else {
				ret = 1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 * 
	 * @author dick
	 * @version 创建时间：2013-1-21 下午2:01:42
	 */
	public static Date currentTime() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 日期加天数 add date
	 * 
	 * @param date
	 * @param count
	 */
	static public Date addDate(Date date, int count) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, count);
		Date date1 = c.getTime();
		return date1;
	}

	static public String getDateEndFormat(Date sDate) {
		String date = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		try {
			date = dateFormat.format(sDate);
		} catch (Exception ex) {
			date = "";
		}
		return date;
	}
}
