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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public class DefaultProfileImageContextMenu implements ProfileImageContextMenu {

	private Activity context;
	private DeviceUtils deviceUtils;
	
	public DefaultProfileImageContextMenu(Activity context) {
		super();
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.profile.ProfileImageContextMenu#show()
	 */
	@Override
	public void show() {
		
		if(deviceUtils != null && deviceUtils.hasCamera()) {
			final String items[] = {"From Gallery", "From Camera"};
			
			AlertDialog.Builder ab=new AlertDialog.Builder(context);
			ab.setTitle("New Profile Picture");
			ab.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface d, int choice) {
					if (choice == 0) {
						launchGallery();
					}
					else if (choice == 1) {
						launchCamera();
					}
				}
			});
			
			ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			
			ab.show();
		}
		else {
			launchGallery();
		}
	}
	
	protected void launchGallery() {
//		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		}

		context.startActivityForResult(Intent.createChooser(intent, "Select Picture"), ProfileActivity.GALLERY_PIC_REQUEST);
	}
	
	protected void launchCamera() {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		context.startActivityForResult(cameraIntent, ProfileActivity.CAMERA_PIC_REQUEST);
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
}
