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
package com.socialize.demo.snippets;

import com.socialize.demo.R;
//begin-snippet-0
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.socialize.ActionBarUtils;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbar.ActionBarOptions;

public class ActionBarCustom extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// Call Socialize in onCreate
		Socialize.onCreate(this, savedInstanceState);
		
		// Your entity key. May be passed as a Bundle parameter to your activity
		String entityKey = "http://www.getsocialize.com";
		
		// Create an entity object including a name
		// The Entity object is Serializable, so you could also store the whole object in the Intent
		Entity entity = Entity.newInstance(entityKey, "Socialize");
		
		
		// Create an options instance to specify your theme
		ActionBarOptions options = new ActionBarOptions();
		
		// Set the colors for the Action Bar
		options.setStrokeColor(Color.parseColor("#591100")); // The line between the buttons
		options.setAccentColor(Color.parseColor("#ffa229")); // The accent line below the buttons
		options.setFillColor(Color.parseColor("#831400")); // The main color of the buttons
		options.setBackgroundColor(Color.parseColor("#591100")); // The background color seen on the left
		options.setHighlightColor(Color.parseColor("#b05e08")); // The thin highlight line above the buttons
		options.setTextColor(Color.parseColor("#ffba00")); // The text color for all buttons
		
		// Optionally alter the images
		options.setLikeIconResourceId(R.drawable.autumn_like);
		options.setLikeIconActiveResourceId(R.drawable.autumn_like_hi);
		options.setCommentIconResourceId(R.drawable.autumn_comment);
		options.setShareIconResourceId(R.drawable.autumn_share);
		options.setViewIconResourceId(R.drawable.autumn_view);
		
		// Pass the options to the call to show
		View actionBar = ActionBarUtils.showActionBar(this, R.layout.autumn, entity, options);
		
		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBar);
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		
		// Call Socialize in onPause
		Socialize.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Call Socialize in onResume
		Socialize.onResume(this);
	}

	@Override
	protected void onDestroy() {
		// Call Socialize in onDestroy before the activity is destroyed
		Socialize.onDestroy(this);
		
		super.onDestroy();
	}	
}
//end-snippet-0


