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
package com.socialize.test.ui.actionbar;

import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.test.mock.MockActionBarSliderView;
import com.socialize.test.mock.MockActionBarView;
import com.socialize.test.ui.SocializeUIActivityTest;
import com.socialize.ui.slider.ActionBarSliderItem;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public class SliderActionBarListenerTest extends SocializeUIActivityTest {

	@UsesMocks ({
		ActionBarSliderItem.class,
		IBeanFactory.class,
		MockActionBarView.class,
		MockActionBarSliderView.class
	})
	public void testSliderActionBarListenerShare() {
		
//		SliderActionBarListener listener = new SliderActionBarListener();
//		
//		IBeanFactory<ActionBarSliderItem> shareSliderItemFactory = AndroidMock.createMock(IBeanFactory.class);
//		ActionBarSliderItem item = AndroidMock.createMock(ActionBarSliderItem.class);
//		MockActionBarView actionBarView = AndroidMock.createMock(MockActionBarView.class, getContext());
//		MockActionBarSliderView actionBarSliderView = AndroidMock.createMock(MockActionBarSliderView.class, getContext());
//		
//		AndroidMock.expect(shareSliderItemFactory.getBean(actionBarView, listener)).andReturn(item);
//		AndroidMock.expect(actionBarView.getSlider()).andReturn(actionBarSliderView);
//		
//		actionBarSliderView.showSliderItem(item);
//		
//		AndroidMock.replay(shareSliderItemFactory);
//		AndroidMock.replay(actionBarView);
//		AndroidMock.replay(actionBarSliderView);
//	
//		listener.setShareSliderItemFactory(shareSliderItemFactory);
//		
//		listener.onClick(actionBarView, ActionBarEvent.SHARE);
//		
//		AndroidMock.verify(shareSliderItemFactory);
//		AndroidMock.verify(actionBarView);
//		AndroidMock.verify(actionBarSliderView);
	}
	
}
