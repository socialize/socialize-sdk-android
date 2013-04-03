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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.socialize.LikeUtils;
import com.socialize.demo.R;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbutton.LikeButtonListener;


/**
 * @author Jason Polites
 *
 */
public class LikeButtonSnippets extends Activity {

// begin-snippet-0
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
		
	// Define/obtain your entity
	Entity entity = Entity.newInstance("http://getsocialize.com", "Socialize");
	
	// Get a reference to the button you want to transform
	// This can be any type of CompoundButton (CheckBox, RadioButton, Switch, ToggleButton)
	
	CheckBox btnCustomCheckBoxLike = (CheckBox) findViewById(R.id.btnCustomCheckBoxLike);
	
	// Make the button a socialize like button!
	LikeUtils.makeLikeButton(this, btnCustomCheckBoxLike, entity, new LikeButtonListener() {
	
		@Override
		public void onClick(CompoundButton button) {
			// You can use this callback to set any loading text or display a progress dialog
			button.setText("--");
		}
	
		@Override
		public void onCheckedChanged(CompoundButton button, boolean isChecked) {
			// The like was posted successfully, change the button to reflect the change
			if(isChecked) {
				button.setText("Unlike");
			}
			else {
				button.setText("Like");
			}
		}
	
		@Override
		public void onError(CompoundButton button, Exception error) {
			// An error occurred posting the like, we need to return the button to its original state
			Log.e("Socialize", "Error on like button", error);
			if(button.isChecked()) {
				button.setText("Unlike");
			}
			else {
				button.setText("Like");
			}
		}
	});		
}
//end-snippet-0
}
