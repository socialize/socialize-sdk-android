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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.socialize.Socialize;
import com.socialize.demo.implementations.action.ActionActivity;
import com.socialize.demo.implementations.actionbar.ActionBarActivity;
import com.socialize.demo.implementations.auth.AuthButtonsActivity;
import com.socialize.demo.implementations.comment.CommentActivity;
import com.socialize.demo.implementations.entity.EntityActivity;
import com.socialize.demo.implementations.facebook.FacebookActivity;
import com.socialize.demo.implementations.like.LikeActivity;
import com.socialize.demo.implementations.location.LocationActivity;
import com.socialize.demo.implementations.share.ShareActivity;
import com.socialize.demo.implementations.subscribe.SubscriptionActivity;
import com.socialize.demo.implementations.tools.ToolsActivity;
import com.socialize.demo.implementations.twitter.TwitterActivity;
import com.socialize.demo.implementations.user.UserActivity;
import com.socialize.demo.implementations.view.ViewActivity;

/**
 * @author Jason Polites
 */
public class DemoList extends ListActivity {
	
	final String[] values = new String[] { "Config", "Linking Twitter & Facebook", "Facebook Direct", "Twitter Direct",  "Action Bar (Profiled)", "Sharing", "Comments", "Likes", "Views", "Entities", "User Profile (Profiled)", "Actions (User Activity)", "Subscriptions", "Location", "Init", "Tools"};
	final Class<?>[] activities = new Class<?>[] { 
			AuthButtonsActivity.class, 
			FacebookActivity.class,
			TwitterActivity.class,
			ActionBarActivity.class, 
			ShareActivity.class, 
			CommentActivity.class, 
			LikeActivity.class, 
			ViewActivity.class, 
			EntityActivity.class,
			UserActivity.class,
			ActionActivity.class, 
			SubscriptionActivity.class, 
			LocationActivity.class,
			InitActivity.class,
			ToolsActivity.class};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Socialize.onCreate(this, savedInstanceState);
		setContentView(R.layout.demo_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(position > 0) {
			Class<?> activityClass = activities[position-1];
			if(activityClass != null) {
				Intent intent = new Intent(this, activityClass);
				startActivity(intent);
			}			
		}
		else {
			showConfigDialog();
		}
	}
	
	protected void showConfigDialog() {
		ConfigDialog.showConfigDialog(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Socialize.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Socialize.onResume(this);
	}

	@Override
	protected void onDestroy() {
		Socialize.onDestroy(this);
		super.onDestroy();
	}
}
