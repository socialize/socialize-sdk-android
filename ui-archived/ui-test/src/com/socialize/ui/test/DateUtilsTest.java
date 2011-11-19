package com.socialize.ui.test;

import com.socialize.ui.util.DateUtils;


public class DateUtilsTest extends SocializeUITestCase {

	// Just integration tests
	public void testTimeUtils() {
		
		/**
		 * a. < 1 minute
		 * b. < 1 hour
		 * c. < 1 day
		 * d. < 1 month
		 * e. > 1 month
		 */
		
		long second = 1000;
		long minute = second * 60;
		long hour = minute * 60;
		long day = hour * 24;
		long month = day * 30;
		
		long a = 30 * second;
		long b = 20 * minute;
		long c = 8 * hour;
		long d = 15 * day;
		long e = 2 * month;
		long f = 400 * day;
		
		DateUtils timeUtils = new DateUtils();
		
		String aValue = timeUtils.getTimeString(a);
		String bValue = timeUtils.getTimeString(b);
		String cValue = timeUtils.getTimeString(c);
		String dValue = timeUtils.getTimeString(d);
		String eValue = timeUtils.getTimeString(e);
		String fValue = timeUtils.getTimeString(f);
		
		System.out.println(aValue);
		System.out.println(bValue);
		System.out.println(cValue);
		System.out.println(dValue);
		System.out.println(eValue);
		System.out.println(fValue);
		
		assertEquals("Just now", aValue);
		assertEquals("20 minutes ago", bValue);
		assertEquals("8 hours ago", cValue);
		assertEquals("15 days ago", dValue);
		assertEquals("2 months ago", eValue);
		assertEquals("Over a year ago", fValue);
		
		
	}
	
}
