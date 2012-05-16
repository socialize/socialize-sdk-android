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
import com.socialize.ActionBarUtils;
import com.socialize.demo.implementations.comment.CommentActivity;
import com.socialize.demo.implementations.entity.EntityActivity;
import com.socialize.demo.implementations.like.LikeActivity;
import com.socialize.demo.implementations.share.ShareActivity;
import com.socialize.demo.implementations.view.ViewActivity;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbar.ActionBarOptions;

/**
 * @author Jason Polites
 * 
 */
public class DemoList extends ListActivity {

	final String[] values = new String[] { "Action Bar", "Entities", "Comments", "Sharing", "Likes", "Views", "Actions (User Activity)", "Subscriptions", "Location"};
	final Class<?>[] activities = new Class<?>[] { null, EntityActivity.class, CommentActivity.class, ShareActivity.class, LikeActivity.class, ViewActivity.class, null, null, null, null};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Your entity key.  May be passed as a Bundle parameter to your activity
		final String entityKey = "http://getsocialize.com";
		
		// Create an entity object, including a name.
		final Entity entity = Entity.newInstance(entityKey, "Socialize");
		
		// Disable scroll view because we are on a list view which already scrolls
		ActionBarOptions options = new ActionBarOptions();
		options.setAddScrollView(false);
		
		// No need for a listener, so just pass null.
		View view = ActionBarUtils.showActionBar(this, R.layout.demo_list, entity, options, null);
		
		setContentView(view);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Class<?> activityClass = activities[position];
		if(activityClass != null) {
			Intent intent = new Intent(this, activityClass);
			startActivity(intent);
		}
	}
}
