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

import com.socialize.android.ioc.AndroidIOC;
import com.socialize.api.DefaultSocializeRequestFactory;
import com.socialize.api.DefaultSocializeResponseFactory;
import com.socialize.api.DefaultSocializeSessionFactory;
import com.socialize.api.PreferenceSessionPersister;
import com.socialize.api.action.activity.SocializeActivitySystem;
import com.socialize.api.action.comment.SocializeCommentSystem;
import com.socialize.api.action.entity.SocializeEntitySystem;
import com.socialize.api.action.like.SocializeLikeSystem;
import com.socialize.api.action.share.SocializeShareSystem;
import com.socialize.api.action.user.SocializeUserSystem;
import com.socialize.api.action.view.SocializeViewSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.CommentFactory;
import com.socialize.entity.UserFactory;
import com.socialize.ioc.SocializeIOC;
import com.socialize.log.SocializeLogger;
import com.socialize.net.DefaultHttpClientFactory;
import com.socialize.oauth.CommonsHttpOAuthConsumerFactory;
import com.socialize.oauth.DefaultOauthRequestSigner;
import com.socialize.oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import com.socialize.provider.DefaultSocializeProvider;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.DeviceUtils;
import com.socialize.util.IOUtils;
import com.socialize.util.JSONParser;
import com.socialize.util.ResourceLocator;

/**
 * Integration tests to verify that the IOC config for socialize is correct.
 * 
 * @author Jason Polites
 * 
 */
public class SocializeIOCTest extends SocializeActivityTest {
	

	public void testSocializeBeans() throws Exception {

		// TODO: Add more tests to verify the structure, not just the beans
		
		SocializeIOC ioc = new SocializeIOC();
		ResourceLocator locator = new ResourceLocator();
		locator.setLogger(new SocializeLogger());
		locator.setClassLoaderProvider(new ClassLoaderProvider());
		ioc.init(TestUtils.getActivity(this), locator);

		// Now make sure all our beans are there
		checkBeanType(ioc, "deviceUtils", DeviceUtils.class);
		checkBeanType(ioc, "resourceLocator", ResourceLocator.class);
		checkBeanType(ioc, "ioUtils", IOUtils.class);
		checkBeanType(ioc, "jsonParser", JSONParser.class);
		checkBeanType(ioc, "config", SocializeConfig.class);
		checkBeanType(ioc, "logger", SocializeLogger.class);
		checkBeanType(ioc, "userFactory", UserFactory.class);
		checkBeanType(ioc, "clientFactory", DefaultHttpClientFactory.class);
		checkBeanType(ioc, "sessionFactory", DefaultSocializeSessionFactory.class);
		checkBeanType(ioc, "oauthConsumerFactory", CommonsHttpOAuthConsumerFactory.class);
		checkBeanType(ioc, "oauthSigningStrategy", AuthorizationHeaderSigningStrategy.class);
		checkBeanType(ioc, "oauthSigner", DefaultOauthRequestSigner.class);
		checkBeanType(ioc, "commentFactory", CommentFactory.class);
		checkBeanType(ioc, "commentRequestFactory", DefaultSocializeRequestFactory.class);
		checkBeanType(ioc, "commentProvider", DefaultSocializeProvider.class);
		checkBeanType(ioc, "commentSystem", SocializeCommentSystem.class);
		checkBeanType(ioc, "shareSystem", SocializeShareSystem.class);
		checkBeanType(ioc, "likeSystem", SocializeLikeSystem.class);
		checkBeanType(ioc, "viewSystem", SocializeViewSystem.class);
		checkBeanType(ioc, "userSystem", SocializeUserSystem.class);
		checkBeanType(ioc, "entitySystem", SocializeEntitySystem.class);
		checkBeanType(ioc, "activitySystem", SocializeActivitySystem.class);
		checkBeanType(ioc, "commentSystem", SocializeCommentSystem.class);
		checkBeanType(ioc, "responseFactory", DefaultSocializeResponseFactory.class);
		checkBeanType(ioc, "sessionPersister",PreferenceSessionPersister.class);
		
		// Check each bean
		assertConfig((SocializeConfig) ioc.getBean("config"));
		
		// Check specific beans
		checkCommentProvider(ioc);
		
		ioc.destroy();
	}
	
	private void assertConfig(SocializeConfig config) {
		assertNotNull(config.getProperties());
	}
	
	private void checkBeanType(AndroidIOC ioc, String name, Class<?> clazz) {
		Object bean = ioc.getBean(name);
		assertNotNull(bean);
		assertTrue(clazz.isAssignableFrom(bean.getClass()));
	}
	
	private void checkCommentProvider(SocializeIOC ioc) {
		Object bean = ioc.getBean("commentProvider");
		checkDefaultSocializeProvider(bean);
	}
	
	private void checkDefaultSocializeProvider(Object bean) {
		assertNotNull(bean);
		assertTrue((bean instanceof DefaultSocializeProvider));
		DefaultSocializeProvider<?> provider = (DefaultSocializeProvider<?> ) bean;
		assertNotNull(provider.getSessionPersister());
	}
}
