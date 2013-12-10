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
import com.socialize.SocializeService;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.view.BaseView;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 *
 */
public class BaseViewTest extends SocializeActivityTest {

	public void test_showError() {
		SocializeErrorHandler handler = Mockito.mock(SocializeErrorHandler.class);
		Exception error = Mockito.mock(Exception.class);
		
		BaseView view = new BaseView(TestUtils.getActivity(this)) {};
		view.setErrorHandler(handler);
		
		view.showError(TestUtils.getActivity(this), error);

		Mockito.verify(handler).handleError(TestUtils.getActivity(this), error);;
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
	
	public void test_assignId() {
		
		LinearLayout parent = Mockito.mock(LinearLayout.class);
		
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
	
	public void test_getNextViewId() {
		LinearLayout group = Mockito.mock(LinearLayout.class);
		View child0 = Mockito.mock(View.class);
		View child1 = Mockito.mock(View.class);
		
		Mockito.when(group.getChildCount()).thenReturn(2);
		Mockito.when(group.getChildAt(0)).thenReturn(child0);
		Mockito.when(group.getChildAt(1)).thenReturn(child1);
		
		Mockito.when(child0.getId()).thenReturn(10);
		Mockito.when(child1.getId()).thenReturn(5);
		
        PublicBaseView view = new PublicBaseView(TestUtils.getActivity(this));
		
		int id = view.getNextViewId(group);
		
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
