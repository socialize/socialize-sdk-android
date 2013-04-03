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

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.ui.dialog.DialogRegister;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Jason Polites
 */
public abstract class SocializeActivity extends Activity implements DialogRegister {

	protected IOCContainer container;
	private Set<Dialog> dialogs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialogs = new LinkedHashSet<Dialog>();
		initSocialize();
		container = ActivityIOCProvider.getInstance().getContainer();
		onPostSocializeInit(container);
	}
	
	protected <E extends Object> E getBean(String name) {
		return container.getBean(name);
	}
	
	public void setContainer(IOCContainer container) {
		this.container = container;
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
	
	protected void initSocialize() {
		getSocialize().init(this);
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	protected void onPostSocializeInit(IOCContainer container) {}
}
