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
package com.socialize.demo.implementations.actionbar;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.socialize.Socialize;
import com.socialize.SocializeLifecycleListener;
import com.socialize.android.ioc.Logger;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;


/**
 * @author Jason Polites
 *
 */
public class ActionBarNetworkMonitor {

	private ActionBarView actionBar;
	private Timer timer;
	private long interval;
	private Activity context;
	
	public ActionBarNetworkMonitor(Activity context, ActionBarView actionBar, long interval) {
		super();
		this.actionBar = actionBar;
		this.timer = new Timer();
		this.context = context;
		this.interval = interval;
	}
	
	public void reloadOnNetworkAvailable(Activity context) {
		
		if(!isNetworkAvailable(context)) {
			this.actionBar.setOnActionBarEventListener(new OnActionBarEventListener() {

				@Override
				public void onUpdate(ActionBarView actionBar) {
					Logger.i("ActionBarWithMonitorActivity", "ActionBar loaded. Releasing lifecycle listener");
					Socialize.setSocializeLifecycleListener(null);
					timer.cancel();
				}
				
				@Override
				public void onLoad(ActionBarView actionBar) {
					Logger.i("ActionBarWithMonitorActivity", "ActionBar loaded. Releasing lifecycle listener");
					Socialize.setSocializeLifecycleListener(null);
					timer.cancel();
				}

				@Override
				public void onLoadFail(Exception error) {
					Logger.i("ActionBarWithMonitorActivity", "ActionBar load failed. Scheduling retry");
				}

				@Override
				public void onGetLike(ActionBarView actionBar, Like like) {}

				@Override
				public void onPostLike(ActionBarView actionBar, Like like) {}

				@Override
				public void onPostUnlike(ActionBarView actionBar) {}

				@Override
				public void onPostShare(ActionBarView actionBar, Share share) {}

				@Override
				public void onGetEntity(ActionBarView actionBar, Entity entity) {}

				@Override
				public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
					return false;
				}
			});
			
			Socialize.setSocializeLifecycleListener(new SocializeLifecycleListener() {

				@Override
				public void onCreate(Activity activity, Bundle savedInstanceState) {}

				@Override
				public void onDestroy(Activity activity) {
					Logger.i("ActionBarWithMonitorActivity", "onDestroy caught.  Cancelling scheduled reload");
					timer.cancel();
					Socialize.setSocializeLifecycleListener(null);
				}

				@Override
				public void onPause(Activity activity) {
					Logger.i("ActionBarWithMonitorActivity", "onPause caught.  Cancelling scheduled reload");
					timer.cancel();
				}

				@Override
				public void onResume(Activity activity) {}
			});
			
			timer.schedule(new ReloadTask(), interval, interval);
		}
	}

	private final class ReloadTask extends TimerTask {
		@Override
		public void run() {
			if(isNetworkAvailable(context)) {
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						actionBar.refresh();
					}
				});
			}
		}
	}
	
	private boolean isNetworkAvailable(Activity context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}	
}
