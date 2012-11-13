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
package com.socialize.demo.implementations.user;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.socialize.UserUtils;
import com.socialize.demo.Debug;
import com.socialize.demo.R;
import com.socialize.error.SocializeException;


/**
 * @author Jason Polites
 *
 */
public class UserActivity extends ListActivity {
	final String[] values = new String[] { "Show User Settings (Profiled)", "Show User Profile", "Wipe Local User Session"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		setListAdapter(adapter);
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case 0:
			
			if(Debug.profileSettings) {
				android.os.Debug.startMethodTracing("settings");
			}
			
			UserUtils.showUserSettingsForResult(this, 0);
			break;

		case 1:
			try {
				UserUtils.showUserProfile(this, UserUtils.getCurrentUser(this));
			}
			catch (SocializeException e) {
				e.printStackTrace();
			}
			break;
			
		case 2:
			UserUtils.clearLocalSessionData(this);
			Toast.makeText(this, "Session cleared", Toast.LENGTH_SHORT).show();
			break;			
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(Debug.profileSettings) {
			Debug.profileSettings = false;
			android.os.Debug.stopMethodTracing();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
