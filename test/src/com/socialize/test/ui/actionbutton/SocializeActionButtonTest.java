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
package com.socialize.test.ui.actionbutton;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.test.mock.MockContext;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.test.ui.SocializeUITestCase;
import com.socialize.ui.actionbutton.ActionButtonConfig;
import com.socialize.ui.actionbutton.ActionButtonLayoutView;
import com.socialize.ui.actionbutton.SocializeActionButton;

/**
 * @author Jason Polites
 */
@Deprecated
public class SocializeActionButtonTest extends SocializeUITestCase {
	
	public void testConstructorA() {
		new SocializeActionButton<SocializeAction>(getContext(), (AttributeSet) null) {
			@Override
			protected void doConfigBuild(Context context, AttributeSet attrs) {
				addResult(true);
			}
		};
		Boolean after = getNextResult();
		assertNotNull(after);
		assertTrue(after);
	}
	
	@UsesMocks ({MockContext.class, XmlResourceParser.class, PublicActionButtonConfig.class})
	public void testConfigBuild() {
		MockContext context = AndroidMock.createNiceMock(MockContext.class);
		XmlResourceParser attrs = AndroidMock.createNiceMock(XmlResourceParser.class);
		final PublicActionButtonConfig config = AndroidMock.createMock(PublicActionButtonConfig.class);
		
		config.build(context, attrs);
		
		AndroidMock.replay(attrs, config, context);
		
		PublicActionButton<SocializeAction> socializeActionButton = new PublicActionButton<SocializeAction>(getContext(), config) {
			@Override
			public ActionButtonConfig newActionButtonConfig() {
				return config;
			}

			@Override
			public void onAfterBuild(ActionButtonConfig config) {
				addResult(config);
			}
		};
		
		socializeActionButton.doConfigBuild(context, attrs);
		
		AndroidMock.verify(attrs, config);
		
		PublicActionButtonConfig after = getNextResult();
		assertNotNull(after);
		assertSame(config, after);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({IOCContainer.class, PublicActionButtonConfig.class, ActionButtonLayoutView.class, Entity.class})
	public void testGetView() {
		
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		PublicActionButtonConfig config = AndroidMock.createMock(PublicActionButtonConfig.class);
		ActionButtonLayoutView<SocializeAction> view = AndroidMock.createMock(ActionButtonLayoutView.class, getContext());
		final Entity entity = AndroidMock.createMock(Entity.class);
		
		final String key = "foo";
		final String name = "bar";
		
		PublicActionButton<SocializeAction> socializeActionButton = new PublicActionButton<SocializeAction>(getContext(), config) {
			@Override
			protected Entity createEntity(String key, String name) {
				return entity;
			}
		};
		
		AndroidMock.expect(container.getBean("actionButtonView", config, socializeActionButton)).andReturn(view);
		AndroidMock.expect(config.getEntityKey()).andReturn(key);
		AndroidMock.expect(config.getEntityName()).andReturn(name);
		
		view.setEntity(entity);
		
		AndroidMock.replay(container, config, view);
		
		socializeActionButton.setContainer(container);
		socializeActionButton.setConfig(config);
		
		View viewAfter = socializeActionButton.getView();
		
		AndroidMock.verify(container, config, view);
		
		assertNotNull(viewAfter);
		assertSame(view, viewAfter);
	}
}
