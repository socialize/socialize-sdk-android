package com.socialize.ui.test.util;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.socialize.ui.SocializeUIBeanOverrider;
import com.socialize.ui.test.ResultHolder;

public class TestUtils {
	
	static ResultHolder holder;
	static ActivityMonitor monitor;
	static Instrumentation instrumentation;
	
	public static void addResult(Object obj) {
		holder.addResult(obj);
	}
	
	public static void addResult(int index, Object obj) {
		holder.addResult(index, obj);
	}
	
	public static <T extends Object> T getResult(int index) {
		return holder.getResult(index);
	}
	
	public static <T extends Object> T getNextResult() {
		return holder.getNextResult();
	}	
	
	public static void setUp(ActivityInstrumentationTestCase2<?> testCase)  {
		holder = new ResultHolder();
		holder.setUp();
		instrumentation = testCase.getInstrumentation();
	}
	
	public static void tearDown() {
		holder.clear();
		
		if(monitor != null) {
			Activity lastActivity = monitor.getLastActivity();
			if(lastActivity != null) {
				lastActivity.finish();
			}
			instrumentation.removeMonitor(monitor);
		}
		
		monitor = null;
	}
	
	public static void setUpActivityMonitor(Class<?> activityClass) {
		monitor = new ActivityMonitor(activityClass.getName(), null, false);
		instrumentation.addMonitor(monitor);
	}
	
	public static Activity waitForActivity(long timeout) {
		if(monitor != null) {
			return monitor.waitForActivityWithTimeout(timeout);
		}
		return null;
	}
	
	public static final void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException ignore) {}
	}
	
	public static boolean lookForText(Activity activity, String text, long timeoutMs) {
		return lookForText(activity.getWindow().getDecorView(), text, timeoutMs);
	}
	
	public static boolean lookForText(View view, String text, long timeoutMs) {
		if(view instanceof ViewGroup) {
			return lookForText(((ViewGroup)view), text, timeoutMs);
		}
		return false;
	}

	/**
	 * Looks for the given text in the given view hierarchy. Returns when found or after timeout.
	 * @param view
	 * @param text
	 * @param timeoutMs
	 */
	public static boolean lookForText(ViewGroup view, String text, long timeoutMs) {
		long startTime = System.currentTimeMillis();
		return lookForText(view, text, timeoutMs, startTime);
	}
	
	private static boolean lookForText(ViewGroup view, String text, long timeoutMs, long startTime) {
		if(findText(view, text)) {
			return true;
		}
		else {
			long endTime = System.currentTimeMillis();
			
			if(endTime - startTime > timeoutMs) {
				return false;
			}
			else {
				// Wait
				sleep((int) (timeoutMs / 10));
				return lookForText(view, text, timeoutMs, startTime);
			}
		}
	}
	
	public static <V extends View> V findView(Activity activity, Class<V> viewClass, long timeoutMs) {
		V view = findView(activity.getWindow().getDecorView(), viewClass, timeoutMs);
		return view;
	}
	
	public static <V extends View> V findView(View view, Class<V> viewClass, long timeoutMs) {
		if(view instanceof ViewGroup) {
			return findView(((ViewGroup)view), viewClass, timeoutMs);
		}
		return null;
	}	
	
	public static <V extends View> V findView(ViewGroup view, Class<V> viewClass, long timeoutMs) {
		long startTime = System.currentTimeMillis();
		return findView(view, viewClass, timeoutMs, startTime);
	}
	
	@SuppressWarnings("unchecked")
	private static <V extends View> V findView(ViewGroup view, Class<V> viewClass, long timeoutMs, long startTime) {
		if(viewClass.isAssignableFrom(view.getClass())) {
			return (V) view;
		}
		else {
			View found = findView(view, viewClass);
			
			if(found != null) {
				return (V) found;
			}
			
			long endTime = System.currentTimeMillis();
			
			if(endTime - startTime > timeoutMs) {
				return null;
			}
			else {
				// Wait
				sleep((int) (timeoutMs / 10));
				return findView(view, viewClass, timeoutMs, startTime);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findView(ViewGroup view, Class<V> viewClass) {
		int count = view.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = view.getChildAt(i);
			
			if(viewClass.isAssignableFrom(child.getClass())) {
				return (V) child;
			}
			
			if(child instanceof ViewGroup && (child != view)) {
				
				View found = findView((ViewGroup)child, viewClass);
				
				if(found != null) {
					return (V) found;
				}
			}
		}
		return null;
	}
	
	public static boolean findText(ViewGroup view, String text) {
		
		int count = view.getChildCount();
		
		for (int i = 0; i < count; i++) {
			View child = view.getChildAt(i);
			
			if(child instanceof ViewGroup && (child != view)) {
				if(findText(((ViewGroup)child), text)) {
					return true;
				}
			}
			else {
				if(child instanceof TextView) {
					if(((TextView)child).getText().toString().equals(text)) {
						return true;
					}
				}
			}
			
		}
		return false;
	}
	
	
	
	public static void setupSocializeOverrides(boolean mockFacebook, boolean mockSocialize) {
		
		SocializeUIBeanOverrider overrider = new SocializeUIBeanOverrider();
		
		if(mockFacebook) {
			if(mockSocialize) {
				overrider.setBeanOverrides("socialize_ui_mock_beans.xml", "socialize_ui_mock_socialize_beans.xml");
			}
			else {
				overrider.setBeanOverrides("socialize_ui_mock_beans.xml");
			}
		}
		else if(mockSocialize) {
			overrider.setBeanOverrides("socialize_ui_mock_socialize_beans.xml");
		}
		else {
			overrider.setBeanOverrides((String[]) null);
		}
	}
	
}
