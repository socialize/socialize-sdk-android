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

import android.content.Context;
import android.graphics.Bitmap;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.util.BitmapUtils;

/**
 * @author Jason Polites
 *
 */
public class DefaultProfileSaver implements ProfileSaver {
	
	private BitmapUtils bitmapUtils;

	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.profile.ProfileSaver#save(android.content.Context, java.lang.String, android.graphics.Bitmap, com.socialize.listener.user.UserSaveListener)
	 */
	@Override
	public void save(Context context, String name, Bitmap image, UserSaveListener listener) {
		
		String encodedImage = null;
		
		if(image != null && !image.isRecycled()) {
			encodedImage = bitmapUtils.encode(image);
		}
		
		String firstName = name;
		String lastName = null;
		
		// Split the name.
		String[] names = name.split("\\s+");
		
		if(names.length > 1) {
			firstName = names[0];
			lastName = names[1];
		}
		
		getSocialize().saveCurrentUserProfile(firstName, lastName, encodedImage,listener);
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setBitmapUtils(BitmapUtils bitmapUtils) {
		this.bitmapUtils = bitmapUtils;
	}
	
	
}
