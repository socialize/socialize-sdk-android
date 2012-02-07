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
package com.socialize.test.ui.actionbutton;

import android.content.Context;
import android.util.AttributeSet;

import com.socialize.entity.SocializeAction;
import com.socialize.ui.actionbutton.ActionButtonConfig;
import com.socialize.ui.actionbutton.SocializeActionButton;

/**
 * @author Jason Polites
 *
 */
public class PublicActionButton<A extends SocializeAction> extends SocializeActionButton<A> {

	public PublicActionButton(Context context, ActionButtonConfig config) {
		super(context, config);
	}

	public PublicActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PublicActionButton(Context context) {
		super(context);
	}

	@Override
	public ActionButtonConfig newActionButtonConfig() {
		return super.newActionButtonConfig();
	}

	@Override
	public void doConfigBuild(Context context, AttributeSet attrs) {
		super.doConfigBuild(context, attrs);
	}

	@Override
	public void onAfterBuild(ActionButtonConfig config) {
		super.onAfterBuild(config);
	}

	@Override
	public ActionButtonConfig getDefault() {
		return super.getDefault();
	}

	@Override
	public void setConfig(ActionButtonConfig config) {
		super.setConfig(config);
	}
}
