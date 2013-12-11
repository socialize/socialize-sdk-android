package com.socialize.test.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.concurrent.AsyncTaskManager;
import com.socialize.ioc.SocializeIOC;
import com.socialize.networks.facebook.FacebookAccess;
import com.socialize.networks.twitter.TwitterAccess;
import com.socialize.ui.dialog.DialogRegister;
import com.socialize.util.IOUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestUtils {
	
	static ResultHolder holder;
    static Map<String, JSONObject> jsons = new HashMap<String, JSONObject>();

    static File cacheDir;

	private static String fb_token = null;
	private static String tw_token = null;
	private static String tw_secret = null;


    public static String getDummyTwitterToken(Context context) throws IOException {
		if(tw_token == null) {
			InputStream in = null;
			try {
				in = context.getAssets().open("socialize.properties");
                Properties props = new Properties();
				props.load(in);
				tw_token = props.getProperty("twitter.token");
			}
			finally {
				if(in != null) {
					in.close();
				}
			}
		}
		
		return tw_token;
	}
	
	public static String getDummyTwitterSecret(Context context) throws IOException {
		if(tw_secret == null) {
			InputStream in = null;
			try {
				in = context.getAssets().open("socialize.properties");
				Properties props = new Properties();
				props.load(in);
				tw_secret = props.getProperty("twitter.secret");
			}
			finally {
				if(in != null) {
					in.close();
				}
			}
		}
		
		return tw_secret;
	}
	
	public static String getDummyFBToken(Context context) throws IOException {
		if(fb_token == null) {
			InputStream in = null;
			try {
				in = context.getAssets().open("socialize.properties");
				Properties props = new Properties();
				props.load(in);
				fb_token = props.getProperty("facebook.token");
			}
			finally {
				if(in != null) {
					in.close();
				}
			}
		}
		
		return fb_token;
	}
	
	public static void addResult(Object obj) {
		holder.addResult(obj);
	}
	
	public static void addResult(int index, Object obj) {
		if(holder == null) {
			return;
		}
		holder.addResult(index, obj);
	}

	public static <T> T getResult(int index) {
		return holder.getResult(index);
	}
	
	public static <T> T getNextResult() {
		return holder.getNextResult();
	}	
	
	public static void clear() {
		holder.clear();
    }

	public static void setUp(ActivityInstrumentationTestCase2<?> test)  {

        setDexCache(test);
        
		AsyncTaskManager.setManaged(true);
		
		holder = new ResultHolder();
		holder.setUp();
		
        SharedPreferences prefs = test.getActivity().getSharedPreferences("SocializeSession", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }
	
	public static void tearDown() {
		Socialize.getSocialize().destroy(true);
		SocializeAccess.setAuthProviders(null);
		SocializeAccess.clearBeanOverrides();
		SocializeIOC.clearStubs();
		SocializeAccess.revertProxies();
		TwitterAccess.revertTwitterUtilsProxy();
		FacebookAccess.revertFacebookUtilsProxy();
		
		if(holder != null) {
			holder.clear();
			holder = null;
		}
	}

	public static Activity restart(ActivityInstrumentationTestCase2<?> test, Activity activity) {
        activity.finish();
        activity = test.getActivity();
		return activity;
	}

	public static ActivityMonitor setUpActivityMonitor(ActivityInstrumentationTestCase2<?> testCase, Class<?> activityClass) {
        ActivityMonitor monitor = new ActivityMonitor(activityClass.getName(), null, false);
        testCase.getInstrumentation().addMonitor(monitor);
		return monitor;
	}

	public static long sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException ignore) {}
		
		return milliseconds;
	}

	public static boolean lookForText(Activity activity, String text, long timeoutMs) {
		return lookForText(activity.getWindow().getDecorView(), text, timeoutMs);
	}
	
	public static boolean lookForText(View view, String text, long timeoutMs) {
        return view instanceof ViewGroup && lookForText(((ViewGroup) view), text, timeoutMs);
    }

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
        return findView(activity.getWindow().getDecorView(), viewClass, timeoutMs);
	}
	
	public static <V extends View> V findViewInDialog(Context activity, Class<V> viewClass, long timeoutMs) {
		
		if(activity instanceof DialogRegister) {
			DialogRegister dr = (DialogRegister) activity;
			Collection<Dialog> dialogs = dr.getDialogs();
			
			if(dialogs != null) {
				
				long start = System.currentTimeMillis();
				long consumed = 0;
				long waitForDialogsNum = timeoutMs / 10;
				
				while(dialogs.size() == 0) {
					sleep(waitForDialogsNum);
					consumed+=waitForDialogsNum;
					
					if(consumed >= timeoutMs) {
						return null;
					}
				}
				
				for (Dialog dialog : dialogs) {
					
					V view = findView(dialog.getWindow().getDecorView(), viewClass, timeoutMs-consumed);
					
					if(view != null) {
						return view;
					}
					
					consumed += (System.currentTimeMillis() - start);
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

	@SuppressWarnings("unchecked")
	public static <V extends View> V findViewWithText(ViewGroup parent, final Class<V> viewClass, final String text, final long timeoutMs) {

		if(timeoutMs > 0) {
			long start = System.currentTimeMillis();
			long time = start;
			
			while(time - start < timeoutMs) {
				V view = findView(parent, new ViewMatcher() {
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
			return findView(parent, new ViewMatcher() {
				@Override
				public boolean matches(View view) {
					return isViewWithText(view, viewClass, text);
				}
			});
		}
		
		return null;
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
		return findView(view, new ViewMatcher() {
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
	
	
	public static void setupSocializeProxies() {
		SocializeAccess.setBeanOverrides("socialize_proxy_beans.xml");
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
            Collections.addAll(configs, others);
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
	
	public static JSONObject getJSON(Context context, String path)  {
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
					} catch (IOException ignore) {
                        ignore.printStackTrace();
					}
				}
			}
		}		
		return json;
	}
	
	public static Activity getActivity(final ActivityInstrumentationTestCase2<?> test) {
        return test.getActivity();
	}
	
	public static boolean waitForIdle(final ActivityInstrumentationTestCase2<?> test, long timeout) throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		test.getInstrumentation().waitForIdle(new Runnable() {
			@Override
			public void run() {
				latch.countDown();
			}
		});
		
		return latch.await(timeout, TimeUnit.MILLISECONDS);
	}

	public static List<Object> getAllResults() {
		return holder.getAllResults();
	}

    public static void setStaticProperty(Class<?> container, String field, Object value) {
        try {
            Field declaredField = locateField(container, field);
            declaredField.setAccessible(true);
            declaredField.set(null, value);
        } catch (Exception e) {
            throw new RuntimeException("Cannot spy on field [" +
                    field +
                    "] in object [" +
                    container +
                    "]", e);
        }
    }

    public static void setProperty(Object container, String field, Object value) {
        try {
            Field declaredField = locateField(container.getClass(), field);
            declaredField.setAccessible(true);
            declaredField.set(container, value);
        } catch (Exception e) {
            throw new RuntimeException("Cannot spy on field [" +
                    field +
                    "] in object [" +
                    container +
                    "]", e);
        }
    }


    static Field locateField(Class<?> container, String field) throws NoSuchFieldException {

        try {
            return container.getDeclaredField(field);
        } catch (NoSuchFieldException e) {

            if (container.getSuperclass() != null) {
                return locateField(container.getSuperclass(), field);
            } else {
                throw e;
            }
        }
    }

    public static void setDexCache(ActivityInstrumentationTestCase2<?> test) {
        // Fix for bug https://code.google.com/p/dexmaker/issues/detail?id=2
        File cacheDir = getCacheDir(test.getInstrumentation().getTargetContext());
        ActivityInstrumentationTestCase2.assertNotNull(cacheDir);
        System.setProperty("dexmaker.dexcache", cacheDir.toString());
    }

    public static File getCacheDir(Context context) {
        if(cacheDir == null) {
            File dataDir = new File("/data/data/" + context.getPackageName());
            cacheDir = new File(dataDir, "cache");
            if (!cacheDir.exists() && !cacheDir.mkdir()) {
                Log.w("Socialize Test", "Failed to create cache dir in [" +
                        cacheDir +
                        "]");
                cacheDir = null;
            }
        }
        return cacheDir;
    }
}
