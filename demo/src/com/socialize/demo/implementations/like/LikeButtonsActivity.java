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
package com.socialize.demo.implementations.like;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.socialize.LikeUtils;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;
import com.socialize.entity.Like;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.IsLikedListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.ui.actionbutton.LikeButtonListener;
import com.socialize.ui.dialog.SafeProgressDialog;


/**
 * @author Jason Polites
 *
 */
public class LikeButtonsActivity extends DemoActivity {

	@Override
	protected void onCreate() {
		setContentView(R.layout.like_buttons_activity);
		
		final Button btnLike = (Button) findViewById(R.id.btnLike);
		
		final CheckBox btnCustomCheckBoxLike = (CheckBox) findViewById(R.id.btnCustomCheckBoxLike);
		
		final SafeProgressDialog progress = SafeProgressDialog.show(this);
		progress.setCancelable(false);
		
		makeLikeButton(btnCustomCheckBoxLike);
		
		// Check if we are liked
		LikeUtils.isLiked(this, entity.getKey(), new IsLikedListener() {
			@Override
			public void onNotLiked() {
				btnLike.setText("Like");
				progress.dismiss();
			}
			
			@Override
			public void onLiked(Like like) {
				btnLike.setText("Unlike");
				progress.dismiss();
			}
		});
		
		btnLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final SafeProgressDialog progress = SafeProgressDialog.show(LikeButtonsActivity.this);
				progress.setCancelable(false);
				
				if(btnLike.getText().equals("Like")) {
					LikeUtils.like(LikeButtonsActivity.this, entity, new LikeAddListener() {
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							handleError(LikeButtonsActivity.this, error);
						}
						
						@Override
						public void onCreate(Like result) {
							progress.dismiss();
							btnLike.setText("Unlike");
						}

						@Override
						public void onCancel() {
							progress.dismiss();
						}
					});
				}
				else {
					LikeUtils.unlike(LikeButtonsActivity.this, entity.getKey(), new LikeDeleteListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							handleError(LikeButtonsActivity.this, error);
						}
						
						@Override
						public void onDelete() {
							progress.dismiss();
							btnLike.setText("Like");
						}
					});
				}
			}
		});
		
		
	}
	
	protected void makeLikeButton(CompoundButton button) {

		LikeUtils.makeLikeButton(this, button, entity, new LikeButtonListener() {

			@Override
			public void onClick(CompoundButton button) {
				button.setText("--");
			}

			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				if(isChecked) {
					button.setText("Unlike");
				}
				else {
					button.setText("Like");
				}
			}

			@Override
			public void onError(CompoundButton button, Exception error) {
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
}
