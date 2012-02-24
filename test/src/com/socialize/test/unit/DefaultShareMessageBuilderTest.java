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
package com.socialize.test.unit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.api.DefaultShareMessageBuilder;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.AppUtils;

/**
 * @author Jason Polites
 * 
 */
@UsesMocks({ SocializeConfig.class, Entity.class })
public class DefaultShareMessageBuilderTest extends SocializeUnitTest {

	@UsesMocks ({AppUtils.class})
	public void test_buildShareLink() {
		
		AppUtils appUtils = AndroidMock.createMock(AppUtils.class);
		Entity entity = AndroidMock.createMock(Entity.class);
		
		AndroidMock.expect(appUtils.getEntityUrl(entity)).andReturn("foobar");

		AndroidMock.replay(appUtils);

		DefaultShareMessageBuilder builder = new DefaultShareMessageBuilder();

		builder.setAppUtils(appUtils);
		
		String link = builder.buildShareLink(entity);

		AndroidMock.verify(appUtils);

		assertEquals("foobar", link);
	}

	@UsesMocks({ User.class, SocializeSession.class, SocializeService.class })
	public void test_buildShareSubject() {
		Entity entity = AndroidMock.createMock(Entity.class);
		User user = AndroidMock.createMock(User.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		AndroidMock.expect(entity.getKey()).andReturn("foo");
		AndroidMock.expect(entity.getName()).andReturn("bar");
		AndroidMock.expect(socialize.getSession()).andReturn(session);
		AndroidMock.expect(session.getUser()).andReturn(user);
		AndroidMock.expect(user.getDisplayName()).andReturn("display_name");

		AndroidMock.replay(entity, socialize, session, user);

		DefaultShareMessageBuilder builder = new DefaultShareMessageBuilder() {
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};

		String link = builder.buildShareSubject(entity);

		AndroidMock.verify(entity, socialize, session, user);

		assertEquals("display_name shared bar", link);
	}

	public void test_buildShareMessage() {
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		AndroidMock.expect(config.isBrandingEnabled()).andReturn(true);
		AndroidMock.replay(config);
		doTestBuildShareMessage(config, "foobar_comment<br/><br/>foobar_entity_link<br/><br/>Shared from <a href=\"foo_url\">bar_name</a> using <a href=\"http://www.getsocialize.com\">Socialize for Android</a>.");
		AndroidMock.verify(config);
	}

	public void test_buildShareMessageNoBranding() {
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		AndroidMock.expect(config.isBrandingEnabled()).andReturn(false);
		AndroidMock.replay(config);
		doTestBuildShareMessage(config, "foobar_comment<br/><br/>foobar_entity_link<br/><br/>Shared from <a href=\"foo_url\">bar_name</a>");
		AndroidMock.verify(config);
	}

	@UsesMocks({ AppUtils.class })
	private void doTestBuildShareMessage(SocializeConfig config, String expected) {

		AppUtils appUtils = AndroidMock.createMock(AppUtils.class);

		final String entityLink = "foobar_entity_link";
		final String comment = "foobar_comment";
		final String url = "foo_url";
		final String name = "bar_name";

		AndroidMock.expect(appUtils.getAppUrl()).andReturn(url);
		AndroidMock.expect(appUtils.getAppName()).andReturn(name);

		AndroidMock.replay(appUtils);

		DefaultShareMessageBuilder builder = new DefaultShareMessageBuilder() {
			@Override
			public String getEntityLink(Entity entity, boolean html) {
				return entityLink;
			}
		};

		builder.setAppUtils(appUtils);
		builder.setConfig(config);

		String actual = builder.buildShareMessage(null, comment, true, true);

		AndroidMock.verify(appUtils);

		assertEquals(expected, actual);

	}

}
