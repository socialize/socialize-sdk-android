package com.socialize.test.ui.dialog;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.mock.MockAlertDialog;
import com.socialize.test.mock.MockDialogBuilder;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.error.DialogErrorHandler;
import com.socialize.util.Drawables;

public class DialogErrorHandlerTest extends SocializeActivityTest {

	@UsesMocks ({
		MockDialogBuilder.class, 
		MockAlertDialog.class,
		Drawables.class,
		Drawable.class,
		SocializeConfig.class
		})
	public void testHandleErrorWithDebug() {
		
		final String message = "An unexpected error occurred.  Please try again";
		final Exception error = new Exception(message);
		
		final MockDialogBuilder builder = AndroidMock.createMock(MockDialogBuilder.class, TestUtils.getActivity(this));
		MockAlertDialog dialog = AndroidMock.createMock(MockAlertDialog.class, TestUtils.getActivity(this));
		Drawables drawables = AndroidMock.createMock(Drawables.class);
		Drawable drawable = AndroidMock.createMock(Drawable.class);
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		AndroidMock.expect(builder.setTitle("Error")).andReturn(builder);
		AndroidMock.expect(builder.setMessage(message)).andReturn(builder);
		AndroidMock.expect(builder.setCancelable(false)).andReturn(builder);
		AndroidMock.expect(builder.setPositiveButton(AndroidMock.eq("OK"), (OnClickListener) AndroidMock.anyObject())).andReturn(builder);
		AndroidMock.expect(builder.create()).andReturn(dialog);
		AndroidMock.expect(drawables.getDrawable("socialize_icon_white.png")).andReturn(drawable);
		AndroidMock.expect(builder.setIcon(drawable)).andReturn(builder);
//		AndroidMock.expect(config.getBooleanProperty(SocializeConfig.SOCIALIZE_DEBUG_MODE, false)).andReturn(true);
		
		dialog.show();
		
		DialogErrorHandler handler = new DialogErrorHandler() {
			@Override
			protected Builder makeBuilder(Context context) {
				return builder;
			}
		};
		
		AndroidMock.replay(builder);
		AndroidMock.replay(dialog);
		AndroidMock.replay(drawables);
		AndroidMock.replay(config);
		
		handler.setDrawables(drawables);
		handler.handleError(TestUtils.getActivity(this), error);
		
		AndroidMock.verify(builder);
		AndroidMock.verify(dialog);
		AndroidMock.verify(drawables);
		AndroidMock.verify(config);
	}
}
