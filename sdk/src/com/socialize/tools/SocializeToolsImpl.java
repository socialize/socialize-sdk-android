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
import com.socialize.log.SDCardExternalLogger;
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
public class SocializeToolsImpl implements SocializeToolsProxy {

	private ImageLoader imageLoader;
	private GeoUtils geoUtils;
	private DisplayUtils displayUtils;
	private KeyboardUtils keyboardUtils;
	private IOUtils ioUtils;
	private AppUtils appUtils;
	
	/* (non-Javadoc)
	 * @see com.socialize.tools.SocializeToolsProxy#getImageLoader(android.content.Context)
	 */
	@Override
	public ImageLoader getImageLoader(Context context) {
		return imageLoader;
	}
	
	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	@Override
	public GeoUtils getGeoUtils(Context context) {
		return geoUtils;
	}

	
	public void setGeoUtils(GeoUtils geoUtils) {
		this.geoUtils = geoUtils;
	}

	@Override
	public DisplayUtils getDisplayUtils(Context context) {
		return displayUtils;
	}

	
	public void setDisplayUtils(DisplayUtils displayUtils) {
		this.displayUtils = displayUtils;
	}

	@Override
	public KeyboardUtils getKeyboardUtils(Context context) {
		return keyboardUtils;
	}

	
	public void setKeyboardUtils(KeyboardUtils keyboardUtils) {
		this.keyboardUtils = keyboardUtils;
	}

	@Override
	public IOUtils getIOUtils(Context context) {
		return ioUtils;
	}

	
	public void setIoUtils(IOUtils ioUtils) {
		this.ioUtils = ioUtils;
	}

	@Override
	public AppUtils getAppUtils(Context context) {
		return appUtils;
	}

	
	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	
	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	@Override
	public Set<Uri> getExternalLogFilePaths(Context context) {
		return SDCardExternalLogger.getExternalLogFilePaths(context);
	}

	@Override
	public void deleteExternalLogFiles(Context context) {
		SDCardExternalLogger.clearExternalLogFiles(context);
	}
	
}
