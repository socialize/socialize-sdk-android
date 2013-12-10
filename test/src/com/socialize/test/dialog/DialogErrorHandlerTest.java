package com.socialize.test.dialog;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import com.socialize.config.SocializeConfig;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.testapp.mock.MockAlertDialog;
import com.socialize.testapp.mock.MockDialogBuilder;
import com.socialize.ui.error.DialogErrorHandler;
import com.socialize.util.Drawables;
import org.mockito.Mockito;

public class DialogErrorHandlerTest extends SocializeActivityTest {

	public void testHandleErrorWithDebug() {
		
		final String message = "An unexpected error occurred.  Please try again";
		final Exception error = new Exception(message);
        final MockDialogBuilder builder = Mockito.mock(MockDialogBuilder.class);

        MockAlertDialog dialog = Mockito.mock(MockAlertDialog.class);
		Drawables drawables = Mockito.mock(Drawables.class);
		Drawable drawable = Mockito.mock(Drawable.class);

		Mockito.when(builder.setTitle("Error")).thenReturn(builder);
		Mockito.when(builder.setMessage(message)).thenReturn(builder);
		Mockito.when(builder.setCancelable(false)).thenReturn(builder);
		Mockito.when(builder.setPositiveButton(Mockito.eq("OK"), (OnClickListener) Mockito.anyObject())).thenReturn(builder);
		Mockito.when(builder.create()).thenReturn(dialog);
		Mockito.when(drawables.getDrawable("socialize_icon_white.png")).thenReturn(drawable);
		Mockito.when(builder.setIcon(drawable)).thenReturn(builder);

		DialogErrorHandler handler = new DialogErrorHandler() {
			@Override
			protected Builder makeBuilder(Context context) {
				return builder;
			}
		};
		
		handler.setDrawables(drawables);
		handler.handleError(TestUtils.getActivity(this), error);
		
		Mockito.verify(dialog).show();
	}
}
