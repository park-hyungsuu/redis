package com.hyungsuu.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;

public class TimeUtil {

	public static long getCurrentTimewithMillis() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}

	public static String getDateFormatyyyyMMddHHmmssSSSS() {
		return new SimpleDateFormat("yyyMMddHHmmssSSSS").format(new Date());
	}
	
	public static String getRandomNum(int count) {
		
		if (count < 0) count = 6;
		
		return RandomStringUtils.randomNumeric(count);
	}
	
	public static String getDateAndRandom() {
		
		return getDateFormatyyyyMMddHHmmssSSSS() + getRandomNum(6);
	}
	
	
	
//	String generatedString = RandomStringUtils.randomAlphanumeric(10);
	public static void main(String[] args) {
		String str = getDateAndRandom();
//		String str = RandomStringUtils.randomNumeric(10);
//    	String str = getDateFormatyyyyMMddHHmmssSSSS();
        System.out.println(str);
    }
}

