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
package com.socialize.ui.profile;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.entity.User;
import com.socialize.ui.SocializeUIActivity;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class ProfileActivity extends SocializeUIActivity {

	public static final int CAMERA_PIC_REQUEST = 1337;
	public static final int GALLERY_PIC_REQUEST = 1338;

	private ProfileView view;

	@Override
	protected void onCreateSafe(Bundle savedInstanceState) {
		
		Bundle extras = getIntent().getExtras();

		if (extras == null || !extras.containsKey(Socialize.USER_ID)) {
			Toast.makeText(this, "No user id provided", Toast.LENGTH_SHORT).show();
			finish();
		}
		else {
			// If WE are the user being viewed, assume a profile update
			String userId = extras.getString(Socialize.USER_ID);
			
			SocializeSession session = getSocialize().getSession();
			
			if(session != null) {
				User user = session.getUser();
				if(user != null) {
					if(!StringUtils.isEmpty(userId) && Integer.parseInt(userId) == user.getId().intValue()) {
						setResult(ProfileActivity.PROFILE_UPDATE);
					}
				}
			}
			
			view = new ProfileView(this);
			setContentView(view);
		}
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			if (data != null) {
				Bundle extras = data.getExtras();
				if(extras != null) {
					Bitmap thumbnail = (Bitmap) extras.get("data");
					if (thumbnail != null) {
						view.onImageChange(thumbnail, null);
					}
				}
			}
		}
		else if (requestCode == GALLERY_PIC_REQUEST) {
			if (data != null) {
				Uri selectedImageUri = data.getData();
				
				if(selectedImageUri != null) {

					String[] projection = { MediaStore.Images.Media.DATA };
					
					Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
					
					if(cursor != null) {
						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						
						cursor.moveToFirst();
						
						String path = cursor.getString(column_index);

						// Close should not be called as per http://developer.android.com/reference/android/app/Activity.html#managedQuery
//						cursor.close();

						if(path != null) {

							BitmapFactory.Options bfo = new BitmapFactory.Options();
							bfo.inDither = true;
							bfo.inSampleSize = 4;

							Bitmap bm = BitmapFactory.decodeFile(path, bfo);

							if (bm != null) {
								view.onImageChange(bm, path);
							}
						}
						else {
							Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}
	}
}
