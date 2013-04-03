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
package com.socialize.tools;

import android.content.Context;
import android.net.Uri;
import com.socialize.annotations.Synchronous;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.util.GeoUtils;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.util.AppUtils;
import com.socialize.util.DisplayUtils;
import com.socialize.util.IOUtils;

import java.util.Set;


/**
 * @author Jason Polites
 *
 */
public interface SocializeToolsProxy {
	@Synchronous
	public ImageLoader getImageLoader(Context context);
	
	@Synchronous
	public GeoUtils getGeoUtils(Context context);
	
	@Synchronous
	public DisplayUtils getDisplayUtils(Context context);	
	
	@Synchronous
	public KeyboardUtils getKeyboardUtils(Context context);		
	
	@Synchronous
	public IOUtils getIOUtils(Context context);	
	
	@Synchronous
	public AppUtils getAppUtils(Context context);

	@Synchronous
	public Set<Uri> getExternalLogFilePaths(Context context);

	@Synchronous
	public void deleteExternalLogFiles(Context context);
}
