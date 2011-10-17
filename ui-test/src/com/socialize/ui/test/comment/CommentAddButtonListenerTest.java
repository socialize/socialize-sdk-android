package com.socialize.ui.test.comment;

import android.widget.EditText;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.config.SocializeConfig;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.comment.CommentAddButtonListener;
import com.socialize.ui.comment.CommentButtonCallback;
import com.socialize.ui.comment.CommentEditField;
import com.socialize.ui.comment.CommentReAuthListener;
import com.socialize.ui.test.SocializeUITestCase;
import com.socialize.ui.util.KeyboardUtils;

public class CommentAddButtonListenerTest extends SocializeUITestCase {

	
	@UsesMocks ({
		CommentEditField.class, 
		KeyboardUtils.class, 
		CommentButtonCallback.class, 
		SocializeUI.class, 
		SocializeService.class})
	public void testOnClickUnAuthenticated() {
	
		final String text = "foobar"; 
		final String consumerKey = "foo_key";
		final String consumerSecret = "bar_secret";
		
		final SocializeUI socializeUI = AndroidMock.createMock(SocializeUI.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		
		CommentEditField field = AndroidMock.createMock(CommentEditField.class, getContext());
		KeyboardUtils keyboardUtils = AndroidMock.createMock(KeyboardUtils.class, getContext());
		CommentButtonCallback callback = AndroidMock.createMock(CommentButtonCallback.class);
		
		EditText mockET = new EditText(getContext());
		
		AndroidMock.expect(field.getText()).andReturn(text);
		AndroidMock.expect(socialize.isAuthenticated()).andReturn(false);
		AndroidMock.expect(field.getEditText()).andReturn(mockET);
		
		keyboardUtils.hideKeyboard(mockET);
		
		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(consumerKey);
		AndroidMock.expect(socializeUI.getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).andReturn(consumerSecret);
		
		socialize.authenticate(AndroidMock.eq(consumerKey), AndroidMock.eq(consumerSecret), (CommentReAuthListener) AndroidMock.anyObject());
		
		AndroidMock.replay(field);
		AndroidMock.replay(socialize);
		AndroidMock.replay(socializeUI);
		AndroidMock.replay(keyboardUtils);
		
		CommentAddButtonListener listener = new CommentAddButtonListener(getContext(), field, callback, keyboardUtils) {
			@Override
			protected SocializeUI getSocializeUI() {
				return socializeUI;
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};
		
		listener.onClick(null);
		
		AndroidMock.verify(field);
		AndroidMock.verify(socialize);
		AndroidMock.verify(socializeUI);
		AndroidMock.verify(keyboardUtils);
	}
	
}
