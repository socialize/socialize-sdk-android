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
package com.socialize.ui.profile;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.socialize.ui.SocializeUI;
import com.socialize.ui.SocializeUIActivity;

/**
 * @author Jason Polites
 */
public class ProfileActivity extends SocializeUIActivity {

	public static final int CAMERA_PIC_REQUEST = 1337;
	public static final int GALLERY_PIC_REQUEST = 1338;

	private ProfileView view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();

		if (extras == null || !extras.containsKey(SocializeUI.USER_ID)) {
			Toast.makeText(this, "No user id provided", Toast.LENGTH_SHORT).show();
			finish();
		}
		else {
			view = new ProfileView(this);
			setContentView(view);
		}
	}

	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//		// TODO: May need to inpect the view if we ever want more than one
//		// context menu.
//		menu.setHeaderTitle("Choose an image from...");
//		menu.add(0, 0, 0, "Gallery");
//		menu.add(0, 1, 1, "Camera");
//	}
//
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		if (item.getItemId() == 0) {
//			Intent intent = new Intent();
//			intent.setType("image/*");
//			intent.setAction(Intent.ACTION_GET_CONTENT);
//			startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PIC_REQUEST);
//		}
//		else if (item.getItemId() == 1) {
//			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//		}
//
//		return true;
//	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			if (data != null) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				if (thumbnail != null) {
					view.onImageChange(thumbnail);
				}
			}
		}
		else if (requestCode == GALLERY_PIC_REQUEST) {
			if (data != null) {
				Uri selectedImageUri = data.getData();
				
				String[] projection = { MediaStore.Images.Media.DATA };
				
				Cursor cursor = managedQuery(selectedImageUri, projection, null, null, null);
				
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				
				cursor.moveToFirst();
				
				String path = cursor.getString(column_index);
				
				cursor.close();
				
				BitmapFactory.Options bfo = new BitmapFactory.Options();
				bfo.inDither = true;
				bfo.inSampleSize = 2;
				
				Bitmap bm = BitmapFactory.decodeFile(path, bfo);
				if (bm != null) {
					view.onImageChange(bm);
				}
			}
		}
	}

}
