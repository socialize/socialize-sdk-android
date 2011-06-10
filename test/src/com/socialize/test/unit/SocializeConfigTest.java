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
package com.socialize.test.unit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.ClassLoaderProvider;

public class SocializeConfigTest extends SocializeActivityTest {

	/**
	 * tests that the config object loads correctly based on the props file.
	 * @throws IOException 
	 */
	public void testConfigLoad() throws IOException {
		
		// Load the file manually...
		Properties props = new Properties();
		SocializeConfig config = new SocializeConfig();
		
		InputStream in = null;
		
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(config.getDefaultPropertiesFileName()); 
			
			props.load(in);
			
			config.init(getActivity());
			
			Properties confProps = config.getProperties();
			
			Set<Object> keySet = props.keySet();
			
			for (Object key : keySet) {
				Object propsValue = props.get(key);
				Object confValue = confProps.get(key);
				
				Assert.assertNotNull(confValue);
				Assert.assertEquals(propsValue, confValue);
			}
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
	}
	
	/**
	 * tests that the config loads correctly when no override props is specified.
	 * @throws IOException
	 */
	public void testConfigLoadWithoutOverride() throws IOException {
		SocializeConfig config = new SocializeConfig("does.not.exist");
		config.init(getActivity());
		Assert.assertNotNull(config.getProperties());
		Assert.assertNull(config.getProperties().getProperty("test_value"));
	}
	
	public void testConfigLoadWithOverride() throws IOException {
		SocializeConfig config = new SocializeConfig("socialize.sample.properties");
		config.init(getActivity());
		Assert.assertNotNull(config.getProperties());
		Assert.assertNotNull(config.getProperties().getProperty("test_value"));
		
		Assert.assertEquals("sample", config.getProperties().getProperty("test_value"));
	}
	
	/**
	 * tests that config load failes when no props file found
	 */
	@UsesMocks({ClassLoaderProvider.class, ClassLoader.class})
	public void testConfigLoadFail() { 
		
		final String noFile = "does.not.exist";
		ClassLoaderProvider mockProvider = AndroidMock.createMock(ClassLoaderProvider.class);
		ClassLoader mockLoader = AndroidMock.createMock(ClassLoader.class);
		
		AndroidMock.expect(mockProvider.getClassloader()).andReturn(mockLoader);
		AndroidMock.expect(mockLoader.getResourceAsStream(SocializeConfig.DEFAULT_PROPERTIES)).andReturn(null);
		
		AndroidMock.replay(mockProvider);
		AndroidMock.replay(mockLoader);
		
		SocializeConfig config = new SocializeConfig(noFile);
		
		config.init(getActivity(), mockProvider);
		
		AndroidMock.verify(mockProvider);
		AndroidMock.verify(mockLoader);
		
		assertNotNull(config.getProperties());
		assertEquals(0, config.getProperties().size());
		
	}
	
}
