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
package com.socialize.demo;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import android.widget.Toast;
import com.socialize.api.action.ActionType;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.SocializeObject;
import com.socialize.ui.dialog.SafeProgressDialog;


/**
 * @author Jason Polites
 *
 */
public abstract class SDKDemoActivity extends DemoActivity {
	
	protected TextView resultText;
	protected TextView entryText;
	protected Dialog progress;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_activity);
		
		resultText = (TextView) findViewById(R.id.txtResult);
		resultText.setMovementMethod(new ScrollingMovementMethod());
		
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
				progress = SafeProgressDialog.show(SDKDemoActivity.this);
				executeDemo(entryText.getText().toString());
			}
		});
	}

	public abstract void executeDemo(String text);
	
	public abstract String getButtonText();
	
	public abstract boolean isTextEntryRequired();
	
	protected void handleCancel() {
		if(progress != null) {
			progress.dismiss();
		}
		Toast.makeText(this, "Operation Canceled", Toast.LENGTH_SHORT);
	}
	
	protected void handleError(Activity context, Exception error) {
		if(progress != null) {
			progress.dismiss();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(out);
		error.printStackTrace(writer);
		writer.flush();
		String trace = new String(out.toByteArray());
		resultText.setText(trace);
		
		super.handleError(context, error);
		
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
		handleSocializeResult(items);
	}
	
	protected <A extends SocializeAction> void handleSocializeResult(List<A> items) {
		Collection<String> strResults = new ArrayList<String>();
		for (A socializeAction : items) {
			if(socializeAction.getActionType().equals(ActionType.LIKE)) {
				strResults.add(socializeAction.getActionType().name() + " On entity: " + socializeAction.getEntityDisplayName() + " by " + socializeAction.getUser().getDisplayName());
			}
			else {
				strResults.add(socializeAction.getActionType().name() + ": " + socializeAction.getDisplayText() + " by " + socializeAction.getUser().getDisplayName());
			}
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
	
}
