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
package com.socialize.ui;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.DialogRegister;

/**
 * @author Jason Polites
 *
 */
public abstract class SocializeUIActivity extends Activity implements DialogRegister {
	
	public static final int PROFILE_UPDATE = 1347;
	
	private Set<Dialog> dialogs;
	
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			dialogs = new LinkedHashSet<Dialog>();
			onCreateSafe(savedInstanceState);
		}
		catch (Throwable e) {
			SocializeLogger.e("", e);
			finish();
		}
	}
	
	protected void onNewIntent(Intent intent) {
		try {
			super.onNewIntent(intent);
			onNewIntentSafe(intent);
		}
		catch (Throwable e) {
			SocializeLogger.e("", e);
			finish();
		}
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
				dialog.dismiss();
			}
			dialogs.clear();
		}
		super.onDestroy();
	}

	protected void onNewIntentSafe(Intent intent) {}
	
	protected abstract void onCreateSafe(Bundle savedInstanceState);
}
