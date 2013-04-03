/*
 * Copyright (c) 2012 Socialize Inc.
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

import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Jason Polites
 */
public class DateUtils {
	
	public static final long minute = 60;
	public static final long hour = minute*60;
	public static final long day = hour*24;
	public static final long month = day*30;
	public static final long year = day*365;
	
	private LocalizationService localizationService;
	
	private final DateFormat SIMPLE_DATE = new SimpleDateFormat("h:mm a 'on' dd MMM yyyy");
	
	public String getTimeString(long diffMilliseconds) {
		
		StringBuilder builder = new StringBuilder();
		
		long diff = diffMilliseconds / 1000;
		
		String unit = null;
		
		long value = 0;
		
		if(diff >= minute && diff < year) {
			
			// is minutes
			if(diff > hour) {
				if(diff > day) {
					
					if(diff > month) {
						value = diff / month;
						
						if(value > 1) {
							unit = localizationService.getString(I18NConstants.DATE_MONTH_PLURAL);
						}
						else {
							unit = localizationService.getString(I18NConstants.DATE_MONTH_SINGLE);
						}
						
					}
					else {
						value = diff / day;
						
						if(value > 1) {
							unit = localizationService.getString(I18NConstants.DATE_DAY_PLURAL);
						}
						else {
							unit = localizationService.getString(I18NConstants.DATE_DAY_SINGLE);
						}						
					}
				}
				else {
					// Calc hours
					value = diff / hour;
					
					if(value > 1) {
						unit = localizationService.getString(I18NConstants.DATE_HOUR_PLURAL);
					}
					else {
						unit = localizationService.getString(I18NConstants.DATE_HOUR_SINGLE);
					}							
				}
			}
			else {
				// Calc minutes
				value = diff / minute;
				
				if(value > 1) {
					unit = localizationService.getString(I18NConstants.DATE_MINUTE_PLURAL);
				}
				else {
					unit = localizationService.getString(I18NConstants.DATE_MINUTE_SINGLE);
				}						
			}
			
			builder.append(value);
			builder.append(" ");
			builder.append(unit);
		}
		else if(diff < minute) {
			builder.append(localizationService.getString(I18NConstants.DATE_JUST_NOW));
		}
		else {
			builder.append(localizationService.getString(I18NConstants.DATE_OVER_A_YEAR_AGO));
		}
		
		return builder.toString();
	}
	
	/**
	 * Returns a simple one-line date string used for display.
	 * @param date
	 * @return
	 */
	public synchronized String getSimpleDateString(Date date) {
		return SIMPLE_DATE.format(date);
	}
	
	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}
}
