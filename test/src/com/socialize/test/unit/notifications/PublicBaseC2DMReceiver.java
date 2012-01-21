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
package com.socialize.test.unit.notifications;

import android.content.Context;
import android.content.Intent;

import com.socialize.notifications.BaseC2DMReceiver;

/**
 * @author Jason Polites
 *
 */
public abstract class PublicBaseC2DMReceiver extends BaseC2DMReceiver {

	public PublicBaseC2DMReceiver() {
		super("TESTING ONLY");
	}

	@Override
	public void onRegistrationResponse(Context context, Intent intent) {
		super.onRegistrationResponse(context, intent);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

	}

	@Override
	protected void onError(Context context, String errorId) {

	}

	@Override
	protected void onRegistrered(Context context, String registrationId) {
		
	}

	@Override
	protected void onUnregistered(Context context) {
		
	}
}
