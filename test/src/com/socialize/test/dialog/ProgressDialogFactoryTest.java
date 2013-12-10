package com.socialize.test.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.ui.dialog.ProgressDialogFactory;
import org.mockito.Mockito;

public class ProgressDialogFactoryTest extends SocializeActivityTest {

	public void testShow() {
		
		final String title = "foo";
		final String message = "bar";
		
        final ProgressDialog dialog = Mockito.mock(ProgressDialog.class);
		


		ProgressDialogFactory factory = new ProgressDialogFactory()  {
			@Override
			protected ProgressDialog makeDialog(Context context) {
				return dialog;
			}
		};
		
		factory.show(TestUtils.getActivity(this), title, message);

        Mockito.verify(dialog).setTitle(title);
        Mockito.verify(dialog).setMessage(message);
        Mockito.verify(dialog).show();
	}
	
}
