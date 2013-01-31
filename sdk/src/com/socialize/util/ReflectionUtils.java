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

import java.lang.reflect.Field;

/**
 * Gets the value of a reflected field.
 * @author Jason
 */
public final class ReflectionUtils {

	/**
	 * Gets the value of the given static field from the given class.
	 * @param fieldName
	 * @param clazz
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static final <E extends Object> E getStaticField(String fieldName, Class<?> clazz) throws Exception {
		
		try {
			Field field = clazz.getField(fieldName);
			
			if(field != null) {
				return (E) field.get(null);
			}

		} catch (Exception e) {
			throw e;
		}

		return null;
	}
	
	public static final boolean hasStaticField(String fieldName, Class<?> clazz) {
		Field[] fields = clazz.getFields();
		
		for (Field field : fields) {
			if(field.getName().equals(fieldName)) {
				return true;
			}
		}

		return false;
	}	
	
	/**
	 * Returns the name of the static field that corresponds to the given value
	 * @param value
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static final String getStaticFieldName(Object value, Class<?> clazz) throws Exception {
		Field[] fields = clazz.getFields();
		for (Field field : fields) {
			Object fVal = field.get(null);
			if(fVal != null && fVal.equals(value)) {
				return field.getName();
			}
		}
		return null;
	}
	
}
