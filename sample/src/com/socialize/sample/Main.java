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
package com.socialize.sample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.socialize.Socialize;

public class Main extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        Button btn = (Button) findViewById(R.id.btnSample);
        Button btnMock = (Button) findViewById(R.id.btnSampleWithMocks);
        
        btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSocialize(false);
			}
		});
        
        btnMock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSocialize(true);
			}
		});
    }

	public void onBackPressed() {
		Log.e("Main", "Back button pushed");
		super.onBackPressed();
	}
	
	public void startSocialize(final boolean isMock) {
		
		final ProgressDialog progress = ProgressDialog.show(Main.this, "Initializing", "Please wait...");
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				if(isMock) {
					Socialize.init(Main.this, "socialize_beans.xml", "socialize_mock_beans.xml");
				}
				else {
					Socialize.init(Main.this);
				}
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progress.dismiss();
				Intent i = new Intent(Main.this, AuthenticateActivity.class);
				startActivity(i);
			}
		}.execute((Void)null);
	}
    
}
