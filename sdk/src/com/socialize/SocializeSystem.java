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
package com.socialize;

import android.content.Context;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.SocializeInitListener;
import com.socialize.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jason Polites
 *
 */
public class SocializeSystem {
	private String[] beanOverrides;
	private SocializeInitListener initListener;
	private boolean configChanged = false;
	private String[] config = null;
	private Properties beanOverrideProps = null;
	
	static final SocializeSystem instance = new SocializeSystem();
	
	public static SocializeSystem getInstance() {
		return instance;
	}
	
	static final String[] CORE_CONFIG = {
		SocializeConfig.SOCIALIZE_CORE_BEANS_PATH,
		SocializeConfig.SOCIALIZE_UI_BEANS_PATH
	};
	
	public String[] getBeanConfig(final Context context) {
		if(config == null || configChanged) {

			// Try to load the overrides from disk
			if(beanOverrideProps == null) {
				final CountDownLatch latch = new CountDownLatch(1);

				new Thread(){
					@Override
					public void run() {
						beanOverrideProps = new Properties();
						try {
							beanOverrideProps.load(context.getAssets().open("socialize.properties"));
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						finally {
							latch.countDown();
						}
					}
				}.start();

				try {
					latch.await();
				} catch (InterruptedException ignore) {}
			}

			if(beanOverrideProps != null) {
				String overrides = beanOverrideProps.getProperty("bean.overrides");

				if(!StringUtils.isEmpty(overrides)) {
					String[] arrOverrides = overrides.split("\\s*,\\s*");

					if(!StringUtils.isEmpty(arrOverrides)) {
						if(StringUtils.isEmpty(beanOverrides)) {
							beanOverrides = arrOverrides;
						}
						else {
							String[] newOverrides = new String[beanOverrides.length + arrOverrides.length];
							System.arraycopy(beanOverrides, 0, newOverrides, 0, beanOverrides.length);
							System.arraycopy(arrOverrides, 0, newOverrides, beanOverrides.length, arrOverrides.length);
							beanOverrides = newOverrides;
						}
					}
				}
			}

			if(!StringUtils.isEmpty(beanOverrides)) {
				config = new String[beanOverrides.length + CORE_CONFIG.length];

				System.arraycopy(CORE_CONFIG, 0, config, 0, CORE_CONFIG.length);
				System.arraycopy(beanOverrides, 0, config, CORE_CONFIG.length, beanOverrides.length);
			}
			else {
				config = CORE_CONFIG;
			}
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
	 * @param beanOverrides
	 */
	void setBeanOverrides(String...beanOverrides) {
		this.beanOverrides = beanOverrides;
		this.configChanged = true;
	}
}
