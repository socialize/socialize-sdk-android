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
package com.socialize.sample.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.socialize.Socialize;
import com.socialize.ui.dialog.DialogRegister;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;


/**
 * @author Jason Polites
 *
 */
public class BaseActivity extends Activity implements DialogRegister {

	private Set<Dialog> dialogs = new HashSet<Dialog>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// Force async tasks static handler to be created on the main UI thread
		try {
			Class.forName("android.os.AsyncTask");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public void register(Dialog dialog) {
		dialogs.add(dialog);
	}

	@Override
	public Collection<Dialog> getDialogs() {
		return dialogs;
	}
	
	@Override
	protected void onDestroy() {
		if(dialogs != null) {
			for (Dialog dialog : dialogs) {
				try {
					dialog.dismiss();
				}
				catch (Throwable ignore) {}
			}
			dialogs.clear();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		Socialize.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		Socialize.onResume(this);
		super.onResume();
	}
}
