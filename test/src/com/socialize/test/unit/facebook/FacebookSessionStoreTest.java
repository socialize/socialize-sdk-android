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
package com.socialize.test.unit.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.facebook.Facebook;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.mock.MockEditor;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({Facebook.class, Activity.class, MockEditor.class, SharedPreferences.class, Drawables.class})
public class FacebookSessionStoreTest extends SocializeActivityTest {

	private Facebook facebook;
	private Activity context;
	private MockEditor editor;
	private SharedPreferences prefs;
	
	private final String appId = "foobar";
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		facebook = AndroidMock.createMock(Facebook.class, appId);
		prefs =  AndroidMock.createMock(SharedPreferences.class);
		context = AndroidMock.createMock(Activity.class);
		editor = AndroidMock.createMock(MockEditor.class);
	}

	public void testSave() {
		
		final String token = "foobar_token";
		final long expires = 69;
		
		AndroidMock.expect(context.getSharedPreferences(FacebookSessionStore.KEY, Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.edit()).andReturn(editor);
		AndroidMock.expect(facebook.getAccessToken()).andReturn(token);
		AndroidMock.expect(facebook.getAccessExpires()).andReturn(expires);
		AndroidMock.expect(editor.putString(FacebookSessionStore.TOKEN, token)).andReturn(editor);
		AndroidMock.expect(editor.putLong(FacebookSessionStore.EXPIRES, expires)).andReturn(editor);
		AndroidMock.expect(editor.commit()).andReturn(true);
		
		AndroidMock.replay(context);
		AndroidMock.replay(prefs);
		AndroidMock.replay(facebook);
		AndroidMock.replay(editor);
		
		FacebookSessionStore store = new FacebookSessionStore();
		
		assertTrue(store.save(facebook, context));
		
		AndroidMock.verify(context);
		AndroidMock.verify(prefs);
		AndroidMock.verify(facebook);
		AndroidMock.verify(editor);
	}
	
	public void testRestore() {
		final String token = "foobar_token";
		final long expires = 69;
		
		AndroidMock.expect(context.getSharedPreferences(FacebookSessionStore.KEY, Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.getString(FacebookSessionStore.TOKEN, null)).andReturn(token);
		AndroidMock.expect(prefs.getLong(FacebookSessionStore.EXPIRES, 0)).andReturn(expires);
		
		
		facebook.setAccessToken(token);
		facebook.setAccessExpires(expires);
		
		AndroidMock.expect(facebook.isSessionValid()).andReturn(true);
		
		AndroidMock.replay(context);
		AndroidMock.replay(prefs);
		AndroidMock.replay(facebook);
		
		FacebookSessionStore store = new FacebookSessionStore();
		
		assertTrue(store.restore(facebook, context));
		
		AndroidMock.verify(context);
		AndroidMock.verify(prefs);
		AndroidMock.verify(facebook);
	}
	
	public void testClear() {
		
		AndroidMock.expect(context.getSharedPreferences(FacebookSessionStore.KEY, Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.edit()).andReturn(editor);
		AndroidMock.expect(editor.clear()).andReturn(editor);
		AndroidMock.expect(editor.commit()).andReturn(true);
		
		AndroidMock.replay(context);
		AndroidMock.replay(prefs);
		AndroidMock.replay(editor);
		
		FacebookSessionStore store = new FacebookSessionStore();
		
		store.clear(context);
		
		AndroidMock.verify(context);
		AndroidMock.verify(prefs);
		AndroidMock.verify(editor);	
	}
}
