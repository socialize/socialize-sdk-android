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
package com.socialize;

import com.socialize.config.SocializeConfig;
import com.socialize.listener.SocializeInitListener;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeSystem {
	private String[] beanOverrides;
	private SocializeInitListener initListener;
	
	static final String[] CORE_CONFIG = {
		SocializeConfig.SOCIALIZE_CORE_BEANS_PATH,
		SocializeConfig.SOCIALIZE_UI_BEANS_PATH
	};
	
	public String[] getBeanConfig() {
		String[] config = null;
		
		if(!StringUtils.isEmpty(beanOverrides)) {
			config = new String[beanOverrides.length + CORE_CONFIG.length];
			
			for (int i = 0; i < CORE_CONFIG.length; i++) {
				config[i] = CORE_CONFIG[i];
			}
			
			for (int i = 0; i < beanOverrides.length; i++) {
				config[i+CORE_CONFIG.length] = beanOverrides[i];
			}
		}
		else {
			config = CORE_CONFIG;
		}
		
		return config;
	}
	
	public void destroy() {
		initListener = null;
		beanOverrides = null;
	}
	
	public SocializeInitListener getSystemInitListener() {
		return initListener;
	}

	/**
	 * EXPERT ONLY (Not documented)
	 * @param initListener
	 */
	void setSystemInitListener(SocializeInitListener initListener) {
		this.initListener = initListener;
	}

	/**
	 * EXPERT ONLY (Not documented)
	 * @param beanOverride
	 */
	void setBeanOverrides(String...beanOverrides) {
		this.beanOverrides = beanOverrides;
	}
}
