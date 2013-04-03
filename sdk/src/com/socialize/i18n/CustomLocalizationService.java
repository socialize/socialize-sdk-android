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
package com.socialize.i18n;

import android.content.Context;
import android.content.res.Resources;
import com.socialize.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;


/**
 * @author Jason Polites
 *
 */
public class CustomLocalizationService implements LocalizationService {

	private static final Map<String, String> strings = new TreeMap<String, String>();
	
	private String packageName;
	private LocalizationService defaultLocalizationService;
	private Resources resources;
	
	public void init(Context context) {
		packageName = context.getPackageName();
		resources = context.getResources();
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.i18n.LocalizationService#getString(java.lang.String)
	 */
	@Override
	public String getString(String key) {
		if(key == null) return "";
		String str = strings.get(key);
		if(StringUtils.isEmpty(str)) {
			int resId = resources.getIdentifier(key, "string", packageName);
			
			if(resId > 0) {
				try {
					str = resources.getString(resId);
				}
				catch (Exception e) {
					// Shouldn't happen
					e.printStackTrace();
				}
			}
			
			if(str == null) {
				// Get default
				str = defaultLocalizationService.getString(key);
			}
			
			if(str != null) {
				strings.put(key, str);
			}
			else {
				str = key;
			}
		}
		
		return str;
	}
	
	public void setDefaultLocalizationService(LocalizationService defaultLocalizationService) {
		this.defaultLocalizationService = defaultLocalizationService;
	}
	
}
