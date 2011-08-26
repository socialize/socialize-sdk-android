/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.ui.util;

/**
 * @author Jason Polites
 */
public class TimeUtils {

	public String getTimeString(long diffMilliseconds) {
		
		StringBuilder builder = new StringBuilder();
		
		long diff = diffMilliseconds / 1000;
		int minute = 60;
		int hour = minute*60;
		int day = hour*24;
		
		String unit = null;
		int value = 0;
		
		if(diff > minute) {
			
			// is minutes
			if(diff > hour) {
				if(diff > day) {
					unit = "day";
					value = (int) diff / day;
				}
				else {
					// Calc hours
					unit = "hour";
					value = (int) diff / hour;
				}
			}
			else {
				// Calc minutes
				unit = "minute";
				value = (int) diff / minute;
			}
			
			builder.append(value);
			builder.append(" ");
			builder.append(unit);
			
			if(value > 1) {
				builder.append("s");
			}
			
			builder.append(" ago");
		}
		else {
			builder.append("Just now");
		}
		
		return builder.toString();
	}
	
}
