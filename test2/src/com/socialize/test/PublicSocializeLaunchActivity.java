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
package com.socialize.test;

import android.content.Intent;
import android.os.Bundle;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeLaunchActivity;

/**
 * @author Jason Polites
 *
 */
public class PublicSocializeLaunchActivity extends SocializeLaunchActivity {

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void superOnCreate(Bundle savedInstanceState) {
		super.superOnCreate(savedInstanceState);
	}

	@Override
	public IOCContainer getContainer() {
		return super.getContainer();
	}

	@Override
	public void initSocialize() {
		super.initSocialize();
	}

	@Override
	public SocializeService getSocialize() {
		return super.getSocialize();
	}

	@Override
	public SocializeAuthListener getAuthListener(IOCContainer container) {
		return super.getAuthListener(container);
	}
}
