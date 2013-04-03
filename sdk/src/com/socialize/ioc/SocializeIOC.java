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
package com.socialize.ioc;

import android.content.Context;
import com.socialize.android.ioc.AndroidIOC;
import com.socialize.android.ioc.BeanMappingSource;
import com.socialize.config.SocializeConfig;
import com.socialize.util.ResourceLocator;

import java.io.InputStream;

/**
 * @author Jason Polites
 */
public class SocializeIOC extends AndroidIOC {

	public void init(Context context, ResourceLocator resourceLocator, String...configPaths) throws Exception {
		InputStream[] streams = null;
		try {
			streams = new InputStream[configPaths.length];
			
			for (int i = 0; i < configPaths.length; i++) {
				streams[i] = resourceLocator.locate(context, configPaths[i]);
			}
			
			final InputStream[] fstreams = streams;
			
			final StringBuilder name = new StringBuilder();
			
			for (String path : configPaths) {
				name.append(path);
			}
			
			BeanMappingSource source = new BeanMappingSource() {
				@Override
				public InputStream[] getSources() {
					return fstreams;
				}
				@Override
				public String getName() {
					return name.toString();
				}
			};

			super.init(context, source);
		}
		finally {
			if(streams != null) {
				for (InputStream in : streams) {
					if(in != null) {
						in.close();
					}
				}
			}
		}
	}
	
	public void init(Context context, ResourceLocator resourceLocator) throws Exception {
		init(context, resourceLocator, SocializeConfig.SOCIALIZE_CORE_BEANS_PATH);
	}
}
