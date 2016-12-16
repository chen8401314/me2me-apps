package com.me2me.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {

	public static String date2string(Date date, String pattern){
		if(null == date || StringUtils.isBlank(pattern)){
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	public static Date string2date(String dateStr, String pattern) throws ParseException{
		if(StringUtils.isBlank(dateStr) || StringUtils.isBlank(pattern)){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try{
			return sdf.parse(dateStr);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isSameDay(Date date1, Date date2){
		if(null == date1 || null == date2){
			return false;
		}
		String datestr1 = date2string(date1, "yyyyMMdd");
		String datestr2 = date2string(date2, "yyyyMMdd");
		if(datestr1.equals(datestr2)){
			return true;
		}
		return false;
	}
	
	public static Date addDay(Date date, int dayNum){
		if(null == date){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, dayNum);
		return cal.getTime();
	}
	
	public static long getDaysBetween2Date(Date date1, Date date2){
		if(null == date1 || null == date2){
			return -1;
		}
		try{
			date1 = string2date(date2string(date1, "yyyy-MM-dd"), "yyyy-MM-dd");
			date2 = string2date(date2string(date2, "yyyy-MM-dd"), "yyyy-MM-dd");
			long day = (date1.getTime() - date2.getTime())/24*60*60*1000l;
			return Math.abs(day);
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	public static long getHoursBetween2Date(Date date1, Date date2){
		if(null == date1 || null == date2){
			return -1;
		}
		try{
			date1 = string2date(date2string(date1, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
			date2 = string2date(date2string(date2, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
			long hour = (date1.getTime() - date2.getTime())/60*60*1000l;
			return Math.abs(hour);
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
	}
}
