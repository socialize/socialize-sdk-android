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
package com.socialize.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.socialize.util.AppUtils;
import com.socialize.util.EntityLoaderUtils;

/**
 * @author Jason Polites
 *
 */
public abstract class ActivityLauncher extends BaseLauncher {
	
	private AppUtils appUtils;
	private EntityLoaderUtils entityLoaderUtils;
	
	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#launch(android.app.Activity, android.os.Bundle)
	 */
	@Override
	public boolean launch(Activity context, Bundle data) {
		
		if(entityLoaderUtils != null) {
			entityLoaderUtils.initEntityLoader();
		}
			
		Class<?>[] activityClasses = getActivityClasses();
		Class<?> activityClass = null;
		
		for (Class<?> cls : activityClasses) {
			if(appUtils.isActivityAvailable(context, cls)) {
				activityClass = cls;
				break;
			}
		}
		
		if(activityClass != null) {
			Intent intent = newIntent(context, activityClass);
			intent.putExtras(data);
			handleIntent(context, intent, data);
			context.startActivity(intent);
			return true;
		}
		
		return false;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}
	
	public void setEntityLoaderUtils(EntityLoaderUtils entityLoaderUtils) {
		this.entityLoaderUtils = entityLoaderUtils;
	}

	// So we can mock
	protected Intent newIntent(Activity context, Class<?> activityClass) {
		return new Intent(context, activityClass);
	}

	protected void handleIntent(Activity context, Intent intent, Bundle data) {}
	
	public abstract Class<?>[] getActivityClasses();
	
}
