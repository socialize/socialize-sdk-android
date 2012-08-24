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
package com.socialize.test.blackbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import junit.framework.Assert;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.ResourceLocator;

public class SocializeConfigTest extends SocializeActivityTest {

	SocializeConfig config;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		config = new SocializeConfig();
		
		ResourceLocator locator = new ResourceLocator();
		
		ClassLoaderProvider provider = new ClassLoaderProvider();
		locator.setClassLoaderProvider(provider);
		
		config.setResourceLocator(locator);
		
	}

	/**
	 * tests that the config object loads correctly based on the props file.
	 * @throws IOException 
	 */
	public void testConfigLoad() throws IOException {
		
		Properties props = new Properties();

		InputStream in = null;
		
		try {
			// Load the file manually...
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(SocializeConfig.DEFAULT_PROPERTIES_PATH); 
			
			props.load(in);
			
			config.init(TestUtils.getActivity(this), false); // Don't override
			
			Properties confProps = config.getProperties();
			
			Set<Object> keySet = props.keySet();
			Set<Object> confKeySet = confProps.keySet();
			
			assertEquals(keySet.size(), confKeySet.size());
			
			for (Object key : keySet) {
				
				assertTrue(confKeySet.contains(key));
				
				Object propsValue = props.get(key);
				Object confValue = confProps.get(key);
				
				Assert.assertNotNull(confValue);
				Assert.assertNotNull(propsValue);
			}
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
	}
	
	public void testConfigLoadWithOverride() throws IOException {
		config.init(TestUtils.getActivity(this));
		Assert.assertNotNull(config.getProperties());
		Assert.assertNotNull(config.getProperties().getProperty("test_value"));
		Assert.assertEquals("sample", config.getProperties().getProperty("test_value"));
	}
	
	/**
	 * tests that config load fails when no props file found
	 * @throws IOException 
	 */
	@UsesMocks({ResourceLocator.class})
	public void testConfigLoadFail() throws IOException { 
		
		final String noFile = "does.not.exist";
		ResourceLocator mockProvider = AndroidMock.createMock(ResourceLocator.class);
		
		AndroidMock.expect(mockProvider.locate(TestUtils.getActivity(this), noFile)).andReturn(null);
		AndroidMock.expect(mockProvider.locateInClassPath(TestUtils.getActivity(this), SocializeConfig.DEFAULT_PROPERTIES_PATH)).andReturn(null);
		
		AndroidMock.replay(mockProvider);
		
		SocializeConfig config = new SocializeConfig(noFile);
		config.setResourceLocator(mockProvider);
		config.init(TestUtils.getActivity(this));
		
		AndroidMock.verify(mockProvider);
		
		assertNotNull(config.getProperties());
		assertEquals(0, config.getProperties().size());
	}
	
	@UsesMocks({InputStream.class, Properties.class})
	public void testConfigMerge() {
		
		final String noFile = "does.not.exist";
		
		SocializeConfig config = new SocializeConfig(noFile);
		config.setProperty("foo", "bar");
		config.setProperty("remove", "me");
		
		assertEquals("bar", config.getProperty("foo"));
		
		Properties merged = new Properties();
		merged.put("foo", "bar_changed");
		merged.put("new", "value");
		
		Set<String> toBeRemoved = new HashSet<String>();
		
		toBeRemoved.add("remove");
		
		config.merge(merged, toBeRemoved);
		
		assertNotNull(config.getProperty("new"));
		assertEquals("value", config.getProperty("new"));
		assertEquals("bar_changed", config.getProperty("foo"));
		assertNull(config.getProperty("remove"));
		assertTrue(toBeRemoved.isEmpty());
	}
	
	@UsesMocks({ResourceLocator.class, InputStream.class, Properties.class})
	public void testConfigMergesOverride() throws IOException { 
		
		final String noFile = "does.not.exist";
		
		InputStream primary = AndroidMock.createNiceMock(InputStream.class);
		InputStream secondary = AndroidMock.createNiceMock(InputStream.class);
		
		ResourceLocator mockProvider = AndroidMock.createMock(ResourceLocator.class);
		
		AndroidMock.expect(mockProvider.locate(TestUtils.getActivity(this), noFile)).andReturn(primary);
		AndroidMock.expect(mockProvider.locateInClassPath(TestUtils.getActivity(this),SocializeConfig.DEFAULT_PROPERTIES_PATH)).andReturn(secondary);
		
		AndroidMock.replay(primary);
		AndroidMock.replay(secondary);
		AndroidMock.replay(mockProvider);
		
		SocializeConfig config = new SocializeConfig(noFile) {
			@Override
			public void merge(Properties other, Set<String> removed) {
				addResult(true);
			}
		};
		
		config.setResourceLocator(mockProvider);
		config.init(TestUtils.getActivity(this));
		
		AndroidMock.verify(mockProvider);
		
		assertNotNull(config.getProperties());
		assertEquals(0, config.getProperties().size());
		
		Boolean nextResult = getNextResult();
		assertNotNull(nextResult);
		assertTrue(nextResult);
	}
	
	public void test_setFacebookSingleSignOnEnabled() {
		SocializeConfig config = new SocializeConfig() {
			@Override
			public void setProperty(String key, String value) {
				addResult(0, key);
				addResult(1, value);
			}
		};
		
		config.setFacebookSingleSignOnEnabled(true);
		
		String value = getResult(1);
		String key = getResult(0);
		
		assertEquals(SocializeConfig.FACEBOOK_SSO_ENABLED, key);
		assertEquals("true", value);
	}
	
	public void testConfigValueTrimOnInit() {
		config.init(TestUtils.getActivity(this));
		Assert.assertNotNull(config.getProperties());
		Assert.assertNotNull(config.getProperties().getProperty("untrimmed"));
		Assert.assertEquals("value", config.getProperties().getProperty("untrimmed"));
	}
	
	public void testConfigValueTrimOnSet() {
		String value = "untrimmed ";
		SocializeConfig config = new SocializeConfig();
		config.setProperty("key", value);
		
		
		Assert.assertNotNull(config.getProperty("key"));
		Assert.assertEquals("untrimmed", config.getProperty("key"));
	}	
	
//	public void testBeanOverride() {
//		SocializeAccess.setBeanOverrides("foobar");
//		String[] config = Socialize.getSocialize().getSystem().getBeanConfig();
//		assertNotNull(config);
//		assertEquals(3, config.length);
//		assertEquals("socialize_beans.xml", config[0]);
//		assertEquals("socialize_ui_beans.xml", config[1]);
//		assertEquals("foobar", config[2]);		
//	}
}
