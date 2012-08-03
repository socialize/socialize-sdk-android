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
import com.socialize.SocializeAccess;
import com.socialize.sample.ui.SampleActivity2;
import com.socialize.sample.util.StictModeUtils;
import com.socialize.ui.dialog.SafeProgressDialog;

public class Main extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        StictModeUtils.enableDefaults();
        
        setContentView(R.layout.main);
        
        Button btn = (Button) findViewById(R.id.btnSample);
        Button btnUI2 = (Button) findViewById(R.id.btnSampleSocializeUI2);
        
        btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSocialize(false);
			}
		});
        
        btnUI2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Main.this, SampleActivity2.class);
				startActivity(i);
			}
		});          
    }

	public void onBackPressed() {
		Log.e("Main", "Back button pushed");
		super.onBackPressed();
	}
	
	public void startSocialize(final boolean isMock) {
		
		final ProgressDialog progress = SafeProgressDialog.show(Main.this, "Initializing", "Please wait...");
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				if(isMock) {
					SocializeAccess.init(Main.this, "socialize_core_beans.xml", "socialize_mock_beans.xml");
				}
				else {
					Socialize.init(Main.this);
				}
				
				return null;
			}

			@SuppressWarnings("deprecation")
			@Override
			protected void onPostExecute(Void result) {
				progress.dismiss();
				Intent i = new Intent(Main.this, AuthenticateActivity.class);
				startActivity(i);
			}
		}.execute((Void)null);
	}
}
