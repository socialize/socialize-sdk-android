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
package com.socialize.util;

import com.socialize.log.SocializeLogger;

import java.io.UnsupportedEncodingException;

/**
 * @author Jason Polites
 */
public class StringUtils {
	
	public static String ellipsis(String text, int max) {
		if(text != null && text.length() > max) {
			text = text.substring(0, max-3) + "...";
		}
		return text;
	}
	
	public static final boolean isEmpty(String[] str) {
		return str == null || str.length == 0;
	}
	
	public static final boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	public static final boolean equals(String str0, String str1) {
		if(str0 == str1) {
			return true;
		}
		else if(str0 == null || str1 == null) {
			return false;
		}
		else {
			return str0.equals(str1);
		}
	}
	
	public static String encodeUtf8(String text) {
		try {
			return new String(text.getBytes(), "utf-8");
		} 
		catch (UnsupportedEncodingException e) {
			SocializeLogger.e(e.getMessage(), e);
			return text;
		}
	}
	
	/**
	 * Replaces successive new line characters.
	 * @param src The source String.
	 * @param from Number of successive new lines (e.g. 3 would be \n\n\n)
	 * @param to The number of new lines to replace with.
	 * @return The cleaned string.
	 */
	public static String replaceNewLines(String src, int from, int to) {
		if(src != null && from > 0 && to < from) {
			
			String strFrom = "";
			String strTo = "";
			
			for (int i = 0; i < from; i++) {
				strFrom+="\n";
			}
			for (int i = 0; i < to; i++) {
				strTo+="\n";
			}
			
			while (src.contains(strFrom)) {
				src = src.replaceAll(strFrom, strTo);
			}
		}
		return src;
	}
}
