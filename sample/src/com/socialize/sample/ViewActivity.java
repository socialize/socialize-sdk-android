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
package com.socialize.sample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.ViewUtils;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.sample.util.ErrorHandler;
import com.socialize.ui.SocializeActivity;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.util.StringUtils;

public class ViewActivity extends SocializeActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view);
		
		Socialize.init(this);
		
		final EditText txtKey = (EditText) findViewById(R.id.txtKey);
		final TextView txtViewCreateResult = (TextView) findViewById(R.id.txtViewCreateResult);
		
		final Button btnViewCreate = (Button) findViewById(R.id.btnViewCreate);
		
		if(Socialize.getSocialize().isAuthenticated()) {
			
			btnViewCreate.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					clearViewData();
					
					final ProgressDialog progress = SafeProgressDialog.show(v.getContext(), "Posting View", "Please wait...");
					
					txtViewCreateResult.setText("");
					btnViewCreate.setEnabled(false);
					
					String key = txtKey.getText().toString();
					
					Entity entity = Entity.newInstance(key, "test entity");
					
					if(!StringUtils.isEmpty(key)) {
						
						ViewUtils.view(ViewActivity.this, entity, new ViewAddListener() {
							
							@Override
							public void onError(SocializeException error) {
								txtViewCreateResult.setText("FAIL: " + ErrorHandler.handleApiError(ViewActivity.this, error));
								btnViewCreate.setEnabled(true);
								progress.dismiss();
							}
							
							@Override
							public void onCreate(com.socialize.entity.View view) {
								btnViewCreate.setEnabled(true);
								txtViewCreateResult.setText("SUCCESS");
								
								populateViewData(view);
								
								progress.dismiss();
							}
						});
					}
					else {
						txtViewCreateResult.setText("FAIL: No Key");
						btnViewCreate.setEnabled(true);
						progress.dismiss();
					}
				}
			});
		}
		else {
			// Not authorized, you would normally do a re-auth here
			txtViewCreateResult.setText("AUTH FAIL");
		}
	}

	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
	
	private void populateViewData(com.socialize.entity.View view) {
		final TextView txtViewIdCreated = (TextView) findViewById(R.id.txtViewIdCreated);
		final TextView txtViewDateCreated = (TextView) findViewById(R.id.txtViewDateCreated);
		
		final TextView txtEntityIdCreated = (TextView) findViewById(R.id.txtEntityIdCreated);
		final TextView txtEntityKeyCreated = (TextView) findViewById(R.id.txtEntityKeyCreated);
		
		if(view.getId() != null) {
			txtViewIdCreated.setText(String.valueOf(view.getId()));
		}
		
		if(view.getDate() != null) {
			txtViewDateCreated.setText(String.valueOf(view.getDate()));
		}
		
		if(view.getEntity() != null) {
			txtEntityIdCreated.setText(String.valueOf(view.getEntity().getId()));
			txtEntityKeyCreated.setText(view.getEntity().getKey());
		}
		else if(view.getEntityKey() != null) {
			txtEntityKeyCreated.setText(view.getEntityKey());
		}
	}
	
	private void clearViewData() {
		final TextView txtViewIdCreated = (TextView) findViewById(R.id.txtViewIdCreated);
		final TextView txtViewDateCreated = (TextView) findViewById(R.id.txtViewDateCreated);
		
		final TextView txtEntityIdCreated = (TextView) findViewById(R.id.txtEntityIdCreated);
		final TextView txtEntityKeyCreated = (TextView) findViewById(R.id.txtEntityKeyCreated);
		
		txtViewIdCreated.setText("");
		txtViewDateCreated.setText("");
		
		txtEntityIdCreated.setText("");
		txtEntityKeyCreated.setText("");
	}
}
