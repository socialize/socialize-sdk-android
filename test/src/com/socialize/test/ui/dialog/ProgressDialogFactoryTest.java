package com.socialize.test.ui.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.dialog.ProgressDialogFactory;

public class ProgressDialogFactoryTest extends SocializeActivityTest {

	@UsesMocks ({ProgressDialog.class, Activity.class})
	public void testShow() {
		
		final String title = "foo";
		final String message = "bar";
		
		final ProgressDialog dialog = AndroidMock.createMock(ProgressDialog.class, TestUtils.getActivity(this));
		
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.show();
		
		AndroidMock.replay(dialog);
		
		ProgressDialogFactory factory = new ProgressDialogFactory()  {
			@Override
			protected ProgressDialog makeDialog(Context context) {
				return dialog;
			}
		};
		
		factory.show(TestUtils.getActivity(this), title, message);
		
		AndroidMock.verify(dialog);
	}
	
}
