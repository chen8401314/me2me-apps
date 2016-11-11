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
	
	public static Date addDay(Date date, int dayNum){
		if(null == date){
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, dayNum);
		return cal.getTime();
	}
}
