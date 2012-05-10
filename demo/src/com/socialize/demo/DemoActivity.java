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
package com.socialize.demo;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.SocializeObject;
import com.socialize.ui.dialog.DialogRegister;
import com.socialize.ui.dialog.SafeProgressDialog;


/**
 * @author Jason Polites
 *
 */
public abstract class DemoActivity extends Activity implements DialogRegister {
	
	Set<Dialog> dialogs = new HashSet<Dialog>();
	
	protected TextView resultText;
	protected TextView entryText;
	protected Dialog progress;
	protected Entity entity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_activity);
		
		entity = Entity.newInstance("http://getsocialize.com", "Socialize");
		resultText = (TextView) findViewById(R.id.txtResult);
		entryText = (TextView) findViewById(R.id.txtEntry);
		entryText.setMovementMethod(new ScrollingMovementMethod());
		
		if(isTextEntryRequired()) {
			entryText.setVisibility(View.VISIBLE);
		}
		else {
			entryText.setText("");
			entryText.setVisibility(View.GONE);
		}
		
		Button button = (Button) findViewById(R.id.btnDemo);
		button.setText(getButtonText());
		
		final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(entryText.getWindowToken(), 0);				
				progress = SafeProgressDialog.show(DemoActivity.this);
				executeDemo(entryText.getText().toString());
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.dialog.DialogRegister#register(android.app.Dialog)
	 */
	@Override
	public void register(Dialog dialog) {
		dialogs.add(dialog);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.dialog.DialogRegister#getDialogs()
	 */
	@Override
	public Collection<Dialog> getDialogs() {
		return dialogs;
	}

	public abstract void executeDemo(String text);
	
	public abstract String getButtonText();
	
	public abstract boolean isTextEntryRequired();
	
	protected void handleError(Exception error) {
		if(progress != null) {
			progress.dismiss();
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(out);
		error.printStackTrace(writer);
		writer.flush();
		String trace = new String(out.toByteArray());
		resultText.setText(trace);
		
		DemoUtils.showErrorDialog(this, error);
	}
	
	protected <A extends SocializeObject> void handleBasicSocializeResult(ListResult<A> results) {
		List<A> items = results.getItems();
		Collection<String> strResults = new ArrayList<String>();
		for (A socializeAction : items) {
			strResults.add(socializeAction.toString());
		}
		
		handleResults(strResults);
	}
	
	protected <A extends SocializeAction> void handleSocializeResult(ListResult<A> results) {
		List<A> items = results.getItems();
		Collection<String> strResults = new ArrayList<String>();
		for (A socializeAction : items) {
			strResults.add(socializeAction.getDisplayText());
		}
		
		handleResults(strResults);
	}
	
	protected void handleBasicSocializeResult(SocializeObject action) {
		handleResult(action.toString());
	}
	
	protected void handleSocializeResult(SocializeAction action) {
		handleResult(action.getDisplayText());
	}
	
	protected void handleResult(String action) {
		resultText.setText(action);
		handleAfterResult();
	}
	
	protected void handleResults(Collection<String> results) {
		StringBuilder builder = new StringBuilder();
		for (String string : results) {
			builder.append(string);
			builder.append("\n");
		}
		resultText.setText(builder.toString());
		handleAfterResult();
	}
	
	protected void handleAfterResult() {
		entryText.setText("");
		if(progress != null) {
			progress.dismiss();
		}
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
}
