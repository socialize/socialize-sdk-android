package com.socialize.test.ui.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.test.ui.ResultHolder;

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
		
		Socialize.getSocialize().destroy(true);
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
		
		Socialize.getSocialize().destroy(true);
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
	public static <V extends View> V findViewWithText(ViewGroup parent, final Class<V> viewClass, final String text) {
		return (V)  findView(parent, new ViewMatcher() {
			@Override
			public boolean matches(View view) {
				return isViewWithText(view, viewClass, text);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findSingleViewWithText(View view, final Class<V> viewClass, final String text) {
		if(view instanceof ViewGroup) {
			return findViewWithText((ViewGroup) view, viewClass, text);
		}
		else if(isViewWithText(view, viewClass, text)) {
			return (V) view;
		}
		return null;
	}
	
	public static boolean isViewWithText(View view, final Class<?> viewClass, final String text) {
		if(viewClass.isAssignableFrom(view.getClass())) {
			TextView textView = findView(view, TextView.class);
			if(textView != null) {
				return textView.getText().toString().equals(text);
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findView(ViewGroup view, ViewMatcher matcher) {
		int count = view.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = view.getChildAt(i);
			
			if(matcher.matches(child)) {
				return (V) child;
			}
			
			if(child instanceof ViewGroup && (child != view)) {
				View found = findView((ViewGroup)child, matcher);
				if(found != null) {
					return (V) found;
				}
			}
		}
		return null;
	}
	
	public static <V extends View> V findView(View view, final Class<V> viewClass) {
		if(view instanceof ViewGroup) {
			return findView((ViewGroup)view, viewClass);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findView(ViewGroup view, final Class<V> viewClass) {
		return (V)  findView(view, new ViewMatcher() {
			@Override
			public boolean matches(View view) {
				return (viewClass.isAssignableFrom(view.getClass()));
			}
		});
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
		setupSocializeOverrides(mockFacebook, mockSocialize, (String[])null);
	}
	
	public static void setupSocializeOverrides(boolean mockFacebook, boolean mockSocialize, String...others) {
		
		List<String> configs = new ArrayList<String>();
		
		if(mockFacebook) {
			configs.add("socialize_ui_mock_beans.xml");
		}
		
		if(mockSocialize) {
			configs.add("socialize_ui_mock_socialize_beans.xml");
		}
		
		if(others != null) {
			for (int i = 0; i < others.length; i++) {
				configs.add(others[i]);
			}
		}
		
		if(!configs.isEmpty()) {
			SocializeAccess.setBeanOverrides(configs.toArray(new String[configs.size()]));
		}
		else {
			SocializeAccess.setBeanOverrides((String[]) null);
		}
	}
	
}
