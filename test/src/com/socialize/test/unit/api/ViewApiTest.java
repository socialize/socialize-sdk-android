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
package com.socialize.test.unit.api;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.view.SocializeViewSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.View;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.view.ViewListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;
import org.mockito.Mockito;

import java.util.List;

/**
 * @author Jason Polites
 */
public class ViewApiTest extends SocializeUnitTest {

	SocializeProvider<View> provider;
	SocializeSession session;
	ViewListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
        provider = Mockito.mock(SocializeProvider.class);
		session = Mockito.mock(SocializeSession.class);
		listener = Mockito.mock(ViewListener.class);
	}

	/**
	 * More specific test to ensure the view is actually set.
	 */
	public void testAddView() {
		final String key = "foo";
		
		SocializeViewSystem api = new SocializeViewSystem(provider){
			@Override
			public void postAsync(SocializeSession session, String endpoint, List<View> objects, SocializeActionListener listener) {
				addResult(objects);
			}
		};
		
		api.addView(session, Entity.newInstance(key, null), null, listener);
		
		List<View> views = getNextResult();
		
		assertNotNull(views);
		assertEquals(1, views.size());
		
		View result = views.get(0);
		
		assertNotNull(result);
		assertNotNull(result.getEntityKey());
		assertEquals(key, result.getEntityKey());
	}
	
}
