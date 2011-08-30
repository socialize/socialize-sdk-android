package com.socialize.ui.test;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ui.error.DialogErrorHandler;
import com.socialize.ui.sample.mock.MockAlertDialog;
import com.socialize.ui.sample.mock.MockDialogBuilder;

public class DialogErrorHandlerTest extends SocializeUIActivityTest {

	@UsesMocks ({MockDialogBuilder.class, MockAlertDialog.class})
	public void testHandleError() {
		
		final String message = "foobar";
		
		final MockDialogBuilder builder = AndroidMock.createMock(MockDialogBuilder.class, getActivity());
		MockAlertDialog dialog = AndroidMock.createMock(MockAlertDialog.class, getActivity());
		
		AndroidMock.expect(builder.setTitle("Error")).andReturn(builder);
		AndroidMock.expect(builder.setMessage(message)).andReturn(builder);
		AndroidMock.expect(builder.setCancelable(false)).andReturn(builder);
		AndroidMock.expect(builder.setPositiveButton(AndroidMock.eq("OK"), (OnClickListener) AndroidMock.anyObject())).andReturn(builder);
		AndroidMock.expect(builder.create()).andReturn(dialog);
		dialog.show();
		
		DialogErrorHandler handler = new DialogErrorHandler() {
			@Override
			protected Builder makeBuilder(Context context) {
				return builder;
			}
		};
		
		AndroidMock.replay(builder);
		AndroidMock.replay(dialog);
		
		handler.handleError(getActivity(), message);
		
		AndroidMock.verify(builder);
		AndroidMock.verify(dialog);
	}
}
