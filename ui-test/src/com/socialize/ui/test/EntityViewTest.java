package com.socialize.ui.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.EntityView;

public class EntityViewTest extends SocializeUIActivityTest {

	@UsesMocks ({Activity.class, Intent.class})
	public void testGetViewWithBundle() {
		
		final Activity activity = AndroidMock.createNiceMock(Activity.class);
		Intent intent = AndroidMock.createNiceMock(Intent.class);
		
		// We can't mock bundles, so use a real one
		final String key = "foobar";
		Bundle data = new Bundle();
		data.putString(SocializeUI.ENTITY_KEY, key);
		
		AndroidMock.expect(activity.getIntent()).andReturn(intent);
		AndroidMock.expect(intent.getExtras()).andReturn(data);
		
		EntityView view = new EntityView(getActivity()) {
			
			@Override
			protected Context getViewContext() {
				return activity;
			}

			@Override
			protected View getView(Bundle bundle, Object... entityKeys) {
				addResult(bundle);
				addResult(entityKeys[0]);
				return null;
			}

			@Override
			protected String[] getEntityKeys() {
				return new String[]{SocializeUI.ENTITY_KEY};
			}

			@Override
			public View getLoadingView() {
				return null;
			}

		};
		
		AndroidMock.replay(activity);
		AndroidMock.replay(intent);
		
		view.getView();
		
		Bundle bundle = getNextResult();
		String entityKey = getNextResult();
		
		assertNotNull(entityKey);
		assertNotNull(bundle);
		
		assertEquals(key, entityKey);
		assertSame(data, bundle);
		
		AndroidMock.verify(activity);
		AndroidMock.verify(intent);
	}
	
	@UsesMocks ({Activity.class, Intent.class})
	public void testGetViewWithoutBundle() {
		final Activity activity = AndroidMock.createNiceMock(Activity.class);
		Intent intent = AndroidMock.createNiceMock(Intent.class);
		
		AndroidMock.expect(activity.getIntent()).andReturn(intent);
		AndroidMock.expect(intent.getExtras()).andReturn(null);
		
		EntityView view = new EntityView(getActivity()) {
			
			@Override
			protected Context getViewContext() {
				return activity;
			}
			
			@Override
			protected View getView(Bundle bundle, Object... entityKeys) {
				return null;
			}

			@Override
			protected String[] getEntityKeys() {
				return new String[]{SocializeUI.ENTITY_KEY};
			}
			
			@Override
			public View getLoadingView() {
				return null;
			}
		};
		
		AndroidMock.replay(activity);
		AndroidMock.replay(intent);
		
		view.getView();
		
		Boolean result = getNextResult();
		
		assertNotNull(result);
		
		AndroidMock.verify(activity);
		AndroidMock.verify(intent);
	}
	
//	public void testGetErrorView() {
//		PublicEntityView view = new PublicEntityView(getContext()) {
//			@Override
//			protected View getView(Bundle bundle, Object... entityKeys) {
//				return null;
//			}
//
//			@Override
//			protected String[] getEntityKeys() {
//				return new String[]{SocializeUI.ENTITY_KEY};
//			}
//	
//		};
//		
//		View errorView = view.getErrorView(getContext());
//		
//		assertNotNull(errorView);
//		assertTrue((errorView instanceof TextView));
//		TextView txt = (TextView) errorView;
//		assertEquals("Socialize Error! No entity url specified", txt.getText().toString());
//	}
	
	abstract class PublicEntityView extends EntityView {

		public PublicEntityView(Context context) {
			super(context);
		}

		@Override
		protected View getView(Bundle bundle, Object... entityKeys) {
			return null;
		}

		@Override
		protected String[] getEntityKeys() {
			return null;
		}

		@Override
		public View getLoadingView() {
			return null;
		}
	}
	
}
