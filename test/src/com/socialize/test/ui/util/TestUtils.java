package com.socialize.test.ui.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.test.ui.ResultHolder;
import com.socialize.ui.dialog.DialogRegister;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.util.IOUtils;

public class TestUtils {
	
	static ResultHolder holder;
	static ActivityMonitor monitor;
	static Instrumentation instrumentation;
	static ActivityInstrumentationTestCase2<?> testCase;
	static Map<String, JSONObject> jsons = new HashMap<String, JSONObject>();
	
	public static final String DUMMY_FB_TOKEN = "AAAAAAITEghMBAIbDCXuiEjfIYD7tLNa7QRXKWXg6iaTjPgpo2n2HW8b3mSAQucVvD1TVkZAmE87FbLvSuvZBUffVeRMFETBiOqlxzgDdPiQAZC58tax";
	
	public static void incrementCount(String key) {
		holder.incrementCount(key);
		
	}
	public static int getCount(String key) {
		return holder.getCount(key);
	}
	
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
	
	public static void setUp(ActivityInstrumentationTestCase2<?> test)  {
		testCase = test;
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
	
	@SuppressWarnings("unchecked")
	public static <A extends Activity> A waitForActivity(long timeout) {
		if(monitor != null) {
			return (A) monitor.waitForActivityWithTimeout(timeout);
		}
		return null;
	}
	
	public static boolean waitForDialogToShow(Dialog dialog, int timeout) {
		long timeWaited = 0;
		while(!dialog.isShowing()) {
			sleep((timeout / 10));
			timeWaited += 10;
			
			if(timeWaited >= timeout) {
				break;
			}
		}
		
		return dialog.isShowing();
	}
	
	//	public static boolean waitForDialogToClose(Dialog dialog, int timeout) {
	//		long timeWaited = 0;
	//		while(dialog.isShowing()) {
	//			int sleep = timeout/ 10;
	//			sleep(sleep);
	//			timeWaited += sleep;
	//			
	//			if(timeWaited >= timeout) {
	//				break;
	//			}
	//		}
	//		
	//		return dialog.isShowing();
	//	}
	//	
	//	public static void waitForDialogToClose(Activity activity, long timeout) {
	//		getActiveDialog(activity);
	//	}
	//	
	//	public static Dialog getActiveDialog(Activity activity) {
	////		activity.getWindow().;
	//		
	//		try {
	//			getDialogs(activity);
	//		}
	//		catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		return null;
	//	}
	//	
	//	public static List<Dialog> getDialogs(Activity activity) throws Exception {
	//		List<Dialog> dialogs = new ArrayList<Dialog>();
	//		WindowManager mWindowManager = (WindowManager)activity.getSystemService("window");
	//		Field instanceField = mWindowManager.getClass().getDeclaredField(getWindowManagerString());
	//		
	//		instanceField.setAccessible(true);
	//		
	//		Object instance = instanceField.get(mWindowManager);
	//		
	//		Field viewsField = instance.getClass().getDeclaredField("mViews");
	//		viewsField.setAccessible(true);
	//		View[] views = (View[]) viewsField.get(instance);
	//		if(views != null) {
	//			for (View view : views) {
	//				Field mCallbackField = view.getClass().getDeclaredField("mCallback");
	//				if(mCallbackField != null) {
	//					Object cb = mCallbackField.get(view);
	//					if(cb instanceof Dialog) {
	//						dialogs.add((Dialog) cb);
	//					}
	//				}
	//			}
	//		}
	//		
	//		return dialogs;
	//	}
	//	
	//	public static View[] getWindowDecorViews(Activity activity)
	//	{
	//		Field viewsField;
	//		Field instanceField;
	//		try {
	//			viewsField = windowManager.getDeclaredField("mViews");
	//			instanceField = windowManager.getDeclaredField(getWindowManagerString());
	//			viewsField.setAccessible(true);
	//			instanceField.setAccessible(true);
	//			Object instance = instanceField.get(null);
	//			return (View[]) viewsField.get(instance);
	//		} catch (SecurityException e) {
	//			e.printStackTrace();
	//		} catch (NoSuchFieldException e) {
	//			e.printStackTrace();
	//		} catch (IllegalArgumentException e) {
	//			e.printStackTrace();
	//		} catch (IllegalAccessException e) {
	//			e.printStackTrace();
	//		}
	//		return null;
	//	}
	//	
	//	private static String getWindowManagerString(){
	//
	//		if(android.os.Build.VERSION.SDK_INT >= 13)
	//			return "sWindowManager";
	//
	//		else
	//			return  "mWindowManager";
	//	}
	
	
	public static boolean waitForText(Activity activity, String text, int minimumNumberOfMatches, long timeoutMS) {
		View view = findViewWithText(activity, View.class, text, timeoutMS);
		return view != null;
	}
	
	public static void destroyActivity() {
		if(monitor != null && monitor.getLastActivity() != null) {
			monitor.getLastActivity().finish();
		}
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
	
	public static <V extends View> V findViewInDialog(Context activity, Class<V> viewClass, long timeoutMs) {
		
		if(activity instanceof DialogRegister) {
			DialogRegister dr = (DialogRegister) activity;
			Collection<Dialog> dialogs = dr.getDialogs();
			
			if(dialogs != null) {
				for (Dialog dialog : dialogs) {
					V view = findView(dialog.getWindow().getDecorView(), viewClass, timeoutMs);
					if(view != null) {
						return view;
					}
				}
			}
		}
		return null;
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
	
	public static boolean findText(Activity activity, final String text, final long timeoutMs) {
		return findViewWithText(activity.getWindow().getDecorView(), View.class, text, timeoutMs) != null;
	}
	
	public static <V extends View> V findViewWithText(Activity activity, final Class<V> viewClass, final String text, final long timeoutMs) {
		return findViewWithText(activity.getWindow().getDecorView(), viewClass, text, timeoutMs);
	}
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findViewWithText(View parent, final Class<V> viewClass, final String text, final long timeoutMs) {
		if(parent instanceof ViewGroup) {
			return findViewWithText((ViewGroup) parent, viewClass, text, timeoutMs);
		}
		else {
			if(isViewWithText(parent, viewClass, text)) {
				return (V) parent;
			}
		}
		return null;
	}
	
	public static boolean clickOnButton(String text) {
		return clickOnButton(testCase.getActivity(), text, 10000);
	}
	
	public static boolean clickOnButton(Activity activity, String text, long timeout) {
		final Button btn = TestUtils.findViewWithText(activity.getWindow().getDecorView(), Button.class, text, timeout);
		
		final Set<Boolean> holder = new HashSet<Boolean>();
		final CountDownLatch latch = new CountDownLatch(1);
		
		if(btn != null) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					holder.add(btn.performClick());
					latch.countDown();
				}
			});
			
			try {
				latch.await(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException ignore) {}
			
			return (holder.size() > 0 && holder.iterator().next());
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findViewWithText(ViewGroup parent, final Class<V> viewClass, final String text, final long timeoutMs) {

		if(timeoutMs > 0) {
			long start = System.currentTimeMillis();
			long time = start;
			
			while(time - start < timeoutMs) {
				V view = (V) findView(parent, new ViewMatcher() {
						@Override
						public boolean matches(View view) {
							return isViewWithText(view, viewClass, text);
						}
					});
				
				if(view == null) {
					sleep((int) (timeoutMs / 10));
				}
				else {
					return view;
				}
				
				time = System.currentTimeMillis();
			}
		}
		else {
			return (V) findView(parent, new ViewMatcher() {
				@Override
				public boolean matches(View view) {
					return isViewWithText(view, viewClass, text);
				}
			});
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findViewAtIndex(ViewGroup view, final Class<V> viewClass, final int index, final long timeoutMs) {
		List<View> allViews = findViews(view, viewClass);
			
		if(allViews != null && allViews.size() > index) {
			return (V) allViews.get(index);
		}
		
		return null;
	}	
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findSingleViewWithText(View view, final Class<V> viewClass, final String text, long timeout) {
		if(view instanceof ViewGroup) {
			return findViewWithText((ViewGroup) view, viewClass, text, timeout);
		}
		else if(isViewWithText(view, viewClass, text)) {
			return (V) view;
		}
		return null;
	}
	
	public static CustomCheckbox findCheckboxWithImageName(final Activity activity, final String text, long timeout) {
		return findCheckboxWithImageName(activity.getWindow().getDecorView(), text, timeout);
	}
	
	public static CustomCheckbox findCheckboxWithImageName(final View parent, final String text, long timeoutMs) {
		if(parent instanceof ViewGroup) {
			
			if(timeoutMs > 0) {
				long time = System.currentTimeMillis();
				long current = time;
				
				CustomCheckbox chk = null;
				
				while(current - time < timeoutMs) {
					chk = (CustomCheckbox) findView((ViewGroup) parent, new ViewMatcher() {
						@Override
						public boolean matches(View view) {
							return isCheckboxWithImage(view, text);
						}
					});	
					
					if(chk == null) {
						sleep((int) (timeoutMs / 10));
					}
					else {
						return chk;
					}
					
					current = System.currentTimeMillis();
				}
			}
			else {
				return  (CustomCheckbox) findView((ViewGroup) parent, new ViewMatcher() {
					@Override
					public boolean matches(View view) {
						return isCheckboxWithImage(view, text);
					}
				});	
			}
		}
		else {
			if( isCheckboxWithImage(parent, text) ) {
				return (CustomCheckbox) parent;
			}
		}
		return null;
	}
	
	public static boolean isCheckboxWithImage(View view, String text) {
		if(view instanceof CustomCheckbox) {
			CustomCheckbox cbx = (CustomCheckbox) view;
			return cbx.getImageOn().contains(text) || cbx.getImageOff().contains(text);
		}
		return false;
	}
	
	public static boolean isViewWithText(View view, final Class<?> viewClass, final String text) {
		if(viewClass.isAssignableFrom(view.getClass())) {
			TextView textView = findView(view, TextView.class);
			if(textView != null) {
				return textView.getText().toString().contains(text);
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
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findViewById(Activity activity, final int id) {
		View view = activity.getWindow().getDecorView();
		
		if(view instanceof ViewGroup) {
			return findViewById((ViewGroup)view, id);
		}
		return (V) ((view.getId() == id) ? view : null);
	}
	
	public static <V extends View> V findViewById(ViewGroup view, final int id) {
		return findView(view, new ViewMatcher() {
			@Override
			public boolean matches(View view) {
				return view.getId() == id;
			}
		});
	}
	
	public static <V extends View> List<V> findViews(ViewGroup view,final Class<?> viewClass) {
		return findViews(view, new ViewMatcher() {
			@Override
			public boolean matches(View view) {
				return (viewClass.isAssignableFrom(view.getClass()));
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <V extends View> List<V> findViews(ViewGroup view, ViewMatcher matcher) {
		
		final List<V> items = new LinkedList<V>();
		
		int count = view.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = view.getChildAt(i);
			
			if(matcher.matches(child)) {
				items.add((V) child);
			}
			
			if(child instanceof ViewGroup && (child != view)) {
				List<V> toBeadded = findViews((ViewGroup)child, matcher);
				if(toBeadded != null) {
					items.addAll(toBeadded);
				}
			}
		}
		
		return items;
	}	
	
	@SuppressWarnings("unchecked")
	public static <V extends View> V findView(View view, final Class<V> viewClass) {
		if(view instanceof ViewGroup) {
			return findView((ViewGroup)view, viewClass);
		}
		else if(viewClass.isAssignableFrom(view.getClass())) {
			return (V) view;
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
	
	public static boolean findText(Activity view, String text) {
		View decorView = view.getWindow().getDecorView();
		if(decorView instanceof ViewGroup) {
			return findText((ViewGroup) decorView, text);
		}
		return false;
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
					if(((TextView)child).getText().toString().contains(text)) {
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
	
	public static String loadStream(InputStream in) throws IOException {
		IOUtils utils = new IOUtils();
		return utils.read(in);
	}
	
	public static void assertByteArrayEquals(byte[] expected, byte[] actual) {
		assertNotNull(expected);
		assertNotNull(actual);
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < actual.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}
	
	public static final JSONObject getJSON(Context context, String path)  {
		InputStream in = null;
		
		path = path.trim();
		
		if(!path.startsWith("existing-data")) {
			path  = "existing-data/" + path;
		}
		
		JSONObject json = jsons.get(path);

		if(json == null) {
			try {
				in = context.getAssets().open(path);

				if(in == null) {
					throw new IOException("No file with path [" +
							path +
					"] on device");
				}

				InputStreamReader reader = new InputStreamReader(in);
				BufferedReader breader = new BufferedReader(reader);

				StringBuilder builder = new StringBuilder();
				String line = breader.readLine();

				while(line != null) {
					builder.append(line);
					builder.append("\n");
					line = breader.readLine();
				}

				json = new JSONObject(builder.toString());
				
				jsons.put(path, json);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally {
				if(in != null) {
					try {
						in.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}		
		return json;
	}
	
}
