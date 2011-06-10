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
package com.socialize.test.integration;

import com.socialize.SocializeService;
import com.socialize.android.ioc.AndroidIOC;
import com.socialize.api.DefaultSocializeRequestFactory;
import com.socialize.api.DefaultSocializeResponseFactory;
import com.socialize.api.DefaultSocializeSessionFactory;
import com.socialize.api.entity.CommentApi;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.factory.CommentFactory;
import com.socialize.entity.factory.FactoryService;
import com.socialize.entity.factory.UserFactory;
import com.socialize.log.SocializeLogger;
import com.socialize.net.DefaultHttpClientFactory;
import com.socialize.oauth.CommonsHttpOAuthConsumerFactory;
import com.socialize.oauth.DefaultOauthRequestSigner;
import com.socialize.provider.DefaultSocializeProvider;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.DeviceUtils;
import com.socialize.util.IOUtils;
import com.socialize.util.JSONParser;

/**
 * Integration tests to verify that the IOC config for socialize is correct.
 * 
 * @author Jason Polites
 * 
 */
public class SocializeIOCTest extends SocializeActivityTest {

	public void testSocializeBeans() throws Exception {

		AndroidIOC ioc = new AndroidIOC();
		ioc.init(getActivity());

		// First just make sure all the beans are there...

		// Put a count assert to make sure this test fails when new beans are
		// added
		// so that developers are reminded to update this test.
		assertEquals(19, ioc.size());

		// Now make sure all our beans are there
		checkBeanType(ioc, "deviceUtils", DeviceUtils.class);
		checkBeanType(ioc, "ioUtils", IOUtils.class);
		checkBeanType(ioc, "jsonParser", JSONParser.class);
		checkBeanType(ioc, "config", SocializeConfig.class);
		checkBeanType(ioc, "logger", SocializeLogger.class);
		checkBeanType(ioc, "factoryService", FactoryService.class);
		checkBeanType(ioc, "userFactory", UserFactory.class);
		checkBeanType(ioc, "httpClientFactory", DefaultHttpClientFactory.class);
		checkBeanType(ioc, "sessionFactory", DefaultSocializeSessionFactory.class);
		checkBeanType(ioc, "oauthConsumerFactory", CommonsHttpOAuthConsumerFactory.class);
		checkBeanType(ioc, "oauthSigningStrategy", oauth.signpost.signature.AuthorizationHeaderSigningStrategy.class);
		checkBeanType(ioc, "oauthSigner", DefaultOauthRequestSigner.class);
		checkBeanType(ioc, "commentFactory", CommentFactory.class);
		checkBeanType(ioc, "commentRequestFactory", DefaultSocializeRequestFactory.class);
		checkBeanType(ioc, "commentProvider", DefaultSocializeProvider.class);
		checkBeanType(ioc, "commentApi", CommentApi.class);
		checkBeanType(ioc, "socializeService", SocializeService.class);
		checkBeanType(ioc, "responseFactory", DefaultSocializeResponseFactory.class);
		
		// Check each bean
		assertConfig((SocializeConfig) ioc.getBean("config"));
		assertFactoryService((FactoryService) ioc.getBean("factoryService"));
	}
	
	private void assertConfig(SocializeConfig config) {
		assertNotNull(config.getProperties());
	}
	
	private void assertFactoryService(FactoryService service) {
		assertNotNull(service.getFactories());
	}

	private void checkBeanType(AndroidIOC ioc, String name, Class<?> clazz) {
		Object bean = ioc.getBean(name);
		assertNotNull(bean);
		assertTrue(clazz.isAssignableFrom(bean.getClass()));
	}
}
