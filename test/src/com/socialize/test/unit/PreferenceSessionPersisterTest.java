/*
 * Copyright (c) 2011 Socialize Inc. 
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
package com.socialize.test.unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.mock.MockContext;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.PreferenceSessionPersister;
import com.socialize.api.SocializeSessionFactory;
import com.socialize.entity.UserFactory;
import com.socialize.test.SocializeActivityTest;
import com.socialize.testapp.mock.MockEditor;
import org.json.JSONException;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	MockContext.class, 
	SharedPreferences.class, 
	MockEditor.class,
	UserFactory.class,
		SocializeSessionFactory.class})
public class PreferenceSessionPersisterTest extends SocializeActivityTest {

	MockContext context;
	SharedPreferences prefs;
	MockEditor editor;
	UserFactory userFactory;
	SocializeSessionFactory socializeSessionFactory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		context = AndroidMock.createMock(MockContext.class);
		prefs = AndroidMock.createMock(SharedPreferences.class);
		editor = AndroidMock.createMock(MockEditor.class);
		userFactory = AndroidMock.createMock(UserFactory.class);
		socializeSessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
	}
	
	public void testPreferencePersistDelete() throws JSONException {

		AndroidMock.expect(context.getSharedPreferences("SocializeSession", Context.MODE_PRIVATE)).andReturn(prefs);
		AndroidMock.expect(prefs.edit()).andReturn(editor);
		AndroidMock.expect(editor.clear()).andReturn(editor);
		AndroidMock.expect(editor.commit()).andReturn(true);
		
		AndroidMock.replay(context);
		AndroidMock.replay(prefs);
		AndroidMock.replay(editor);
		
		PreferenceSessionPersister persister = new PreferenceSessionPersister(userFactory, socializeSessionFactory);
		
		persister.delete(context);
		
		AndroidMock.verify(context);
		AndroidMock.verify(prefs);
		AndroidMock.verify(editor);
	}
}
