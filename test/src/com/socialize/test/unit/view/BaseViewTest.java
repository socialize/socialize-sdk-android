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
package com.socialize.test.unit.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class BaseViewTest extends SocializeActivityTest {

	@UsesMocks  ({SocializeErrorHandler.class, Exception.class})
	public void test_showError() {
		SocializeErrorHandler handler = AndroidMock.createMock(SocializeErrorHandler.class);
		Exception error = AndroidMock.createMock(Exception.class);
		
		handler.handleError(TestUtils.getActivity(this), error);
		
		AndroidMock.replay(handler);
		
		BaseView view = new BaseView(TestUtils.getActivity(this)) {};
		view.setErrorHandler(handler);
		
		view.showError(TestUtils.getActivity(this), error);
		
		AndroidMock.verify(handler);
	}
	
	public void test_getActivity() {
		Activity activity = TestUtils.getActivity(this);
		PublicBaseView view = new PublicBaseView(activity);
		assertSame(activity, view.getActivity());
	}
	
	public void test_onWindowVisibilityChangedVisibleFirstLoad() {
		
		final String success = "onViewLoad";
		
		PublicBaseView view = new PublicBaseView(TestUtils.getActivity(this)) {
			@Override
			public boolean isInEditMode() {
				return false;
			}

			@Override
			public boolean checkLoaded() {
				return false;
			}

			@Override
			public void onViewLoad() {
				addResult(success);
			}
		};
		
		view.onWindowVisibilityChanged(View.VISIBLE);
		
		String val = getNextResult();
		
		assertNotNull(val);
		assertEquals(success, val);
	}
	
	public void test_onWindowVisibilityChangedVisibleSecondLoad() {
		
		final String success = "onViewUpdate";
		
		PublicBaseView view = new PublicBaseView(TestUtils.getActivity(this)) {
			@Override
			public boolean isInEditMode() {
				return false;
			}

			@Override
			public boolean checkLoaded() {
				return true;
			}

			@Override
			public void onViewUpdate() {
				addResult(success);
			}
		};
		
		view.onWindowVisibilityChanged(View.VISIBLE);
		
		String val = getNextResult();
		
		assertNotNull(val);
		assertEquals(success, val);
	}	
//	
//	public void test_onWindowVisibilityChangedInvisible() {
//		
//		final String success = "decrementLoaded";
//		
//		PublicBaseView view = new PublicBaseView(TestUtils.getActivity(this)) {
//			@Override
//			public boolean isInEditMode() {
//				return false;
//			}
//
//			@Override
//			public boolean checkLoaded() {
//				return true;
//			}
//
//			@Override
//			public void decrementLoaded() {
//				addResult(success);
//			}
//		};
//		
//		view.onWindowVisibilityChanged(View.INVISIBLE);
//		
//		String val = getNextResult();
//		
//		assertNotNull(val);
//		assertEquals(success, val);
//	}	
//	
//	public void test_onWindowVisibilityChangedGone() {
//		
//		final String success = "decrementLoaded";
//		
//		PublicBaseView view = new PublicBaseView(TestUtils.getActivity(this)) {
//			@Override
//			public boolean isInEditMode() {
//				return false;
//			}
//
//			@Override
//			public boolean checkLoaded() {
//				return true;
//			}
//
//			@Override
//			public void decrementLoaded() {
//				addResult(success);
//			}
//		};
//		
//		view.onWindowVisibilityChanged(View.GONE);
//		
//		String val = getNextResult();
//		
//		assertNotNull(val);
//		assertEquals(success, val);
//	}		
	
	public void test_checkLoaded() {
		PublicBaseView view = new PublicBaseView(TestUtils.getActivity(this));
		
		assertFalse(view.checkLoaded());
		assertTrue(view.checkLoaded());
	}
	
	@UsesMocks(LinearLayout.class)
	public void test_assignId() {
		
		LinearLayout parent = AndroidMock.createMock(LinearLayout.class, TestUtils.getActivity(this));
		
		PublicBaseView view = new PublicBaseView(TestUtils.getActivity(this)) {
			@Override
			public int getNextViewId(View parent) {
				addResult(parent);
				return 0;
			}
		};
		
		view.assignId(parent);
		
		View val = getNextResult();
		
		assertNotNull(val);
		assertSame(parent, val);
	}
	
	@UsesMocks({LinearLayout.class, View.class})
	public void test_getNextViewId() {
		LinearLayout group = AndroidMock.createMock(LinearLayout.class, TestUtils.getActivity(this));
		View child0 = AndroidMock.createMock(View.class, TestUtils.getActivity(this));
		View child1 = AndroidMock.createMock(View.class, TestUtils.getActivity(this));
		
		AndroidMock.expect(group.getChildCount()).andReturn(2);
		AndroidMock.expect(group.getChildAt(0)).andReturn(child0);
		AndroidMock.expect(group.getChildAt(1)).andReturn(child1);
		
		AndroidMock.expect(child0.getId()).andReturn(10);
		AndroidMock.expect(child1.getId()).andReturn(5);
		
		AndroidMock.replay(group);
		AndroidMock.replay(child0);
		AndroidMock.replay(child1);
		
		PublicBaseView view = new PublicBaseView(TestUtils.getActivity(this));
		
		int id = view.getNextViewId(group);
		
		AndroidMock.verify(group);
		AndroidMock.verify(child0);
		AndroidMock.verify(child1);
		
		assertEquals(11, id);
	}
	
	private class PublicBaseView extends BaseView {

		public PublicBaseView(Context context) {
			super(context);
		}

		@Override
		public SocializeService getSocialize() {
			return super.getSocialize();
		}

		@Override
		public Activity getActivity() {
			return super.getActivity();
		}

		@Override
		public void onWindowVisibilityChanged(int visibility) {
			super.onWindowVisibilityChanged(visibility);
		}

		@Override
		public View getEditModeView() {
			return super.getEditModeView();
		}

		@Override
		public void onViewUpdate() {
			super.onViewUpdate();
		}

		@Override
		public void onViewLoad() {
			super.onViewLoad();
		}

		@Override
		public int getNextViewId(View parent) {
			return super.getNextViewId(parent);
		}

		@Override
		public boolean checkLoaded() {
			return super.checkLoaded();
		}

		@Override
		public void incrementLoaded() {
			super.incrementLoaded();
		}

//		@Override
//		public void decrementLoaded() {
//			super.decrementLoaded();
//		}
	}
}
