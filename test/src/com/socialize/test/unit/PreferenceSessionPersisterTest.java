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
import com.socialize.api.PreferenceSessionPersister;
import com.socialize.api.SocializeSessionFactory;
import com.socialize.entity.UserFactory;
import com.socialize.test.SocializeActivityTest;
import com.socialize.testapp.mock.MockEditor;
import org.json.JSONException;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 *
 */
public class PreferenceSessionPersisterTest extends SocializeActivityTest {

	MockContext context;
	SharedPreferences prefs;
	MockEditor editor;
	UserFactory userFactory;
	SocializeSessionFactory socializeSessionFactory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		context = Mockito.mock(MockContext.class);
		prefs = Mockito.mock(SharedPreferences.class);
		editor = Mockito.mock(MockEditor.class);
		userFactory = Mockito.mock(UserFactory.class);
		socializeSessionFactory = Mockito.mock(SocializeSessionFactory.class);
	}
	
	public void testPreferencePersistDelete() throws JSONException {

		Mockito.when(context.getSharedPreferences("SocializeSession", Context.MODE_PRIVATE)).thenReturn(prefs);
		Mockito.when(prefs.edit()).thenReturn(editor);
		Mockito.when(editor.clear()).thenReturn(editor);
		Mockito.when(editor.commit()).thenReturn(true);
		
		PreferenceSessionPersister persister = new PreferenceSessionPersister(userFactory, socializeSessionFactory);
		
		persister.delete(context);

        Mockito.verify(context).getSharedPreferences("SocializeSession", Context.MODE_PRIVATE);
        Mockito.verify(prefs).edit();
        Mockito.verify(editor).clear();
        Mockito.verify(editor).commit();
	}
}
