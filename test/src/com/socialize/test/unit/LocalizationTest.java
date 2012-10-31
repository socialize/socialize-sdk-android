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

import android.content.res.Resources;
import android.test.mock.MockContext;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.i18n.CustomLocalizationService;
import com.socialize.i18n.DefaultLocalizationService;
import com.socialize.i18n.I18NConstants;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.ResourceLocator;


/**
 * @author Jason Polites
 *
 */
public class LocalizationTest extends SocializeActivityTest {

	@UsesMocks ({Resources.class, MockContext.class})
	public void testCustomLocalizationServiceWithMocks() {
		
		String packageName = "foobar";
		String resKey = "foobar.key";
		String resVal = "foobar.val";
		int resId = 69;
		
		MockContext ctx = AndroidMock.createMock(MockContext.class);
		Resources resources = AndroidMock.createMock(Resources.class, getActivity().getResources().getAssets(), getActivity().getResources().getDisplayMetrics(), getActivity().getResources().getConfiguration());
		
		AndroidMock.expect(ctx.getResources()).andReturn(resources);
		AndroidMock.expect(ctx.getPackageName()).andReturn(packageName);
		
		AndroidMock.expect(resources.getIdentifier(resKey, "string", packageName)).andReturn(resId).once();
		AndroidMock.expect(resources.getString(resId)).andReturn(resVal).once();
		
		AndroidMock.replay(ctx, resources);
		
		CustomLocalizationService service = new CustomLocalizationService();
		
		service.init(ctx);
		
		String val = service.getString(resKey);
		String val2 = service.getString(resKey); // Uses cache
		
		AndroidMock.verify(ctx, resources);
		
		assertEquals(resVal, val);
		assertEquals(resVal, val2);
	}
	
	public void testCustomLocalizationServiceWithDefaultData() {
		
		ResourceLocator locator = new ResourceLocator();
		
		DefaultLocalizationService defaultService = new DefaultLocalizationService();
		defaultService.setResourceLocator(locator);
		defaultService.init(getContext());
		
		CustomLocalizationService service = new CustomLocalizationService();
		service.setDefaultLocalizationService(defaultService);
		
		service.init(getContext());
		String string = service.getString(I18NConstants.ACTIONBAR_COMMENT);
		assertEquals("Comment", string);
	}
	
	public void testCustomLocalizationServiceWithCustomData() {
		
		ResourceLocator locator = new ResourceLocator();
		
		DefaultLocalizationService defaultService = new DefaultLocalizationService();
		defaultService.setResourceLocator(locator);
		defaultService.init(getContext());
		
		CustomLocalizationService service = new CustomLocalizationService();
		service.setDefaultLocalizationService(defaultService);
		
		service.init(getContext());
		
		String string = service.getString(I18NConstants.COMMENT_HINT);
		assertEquals("Be the first to comment...", string);
	}	
	
}
