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
package com.socialize.test.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.socialize.sample.Main;

/**
 * @author Jason Polites
 */
public abstract class SocializeRobotiumTest extends ActivityInstrumentationTestCase2<Main> {

	public static final int DEFAULT_TIMEOUT_SECONDS = 100;
	public static final String DEFAULT_ENTITY_URL = "http://socialize.integration.tests.com?somekey=somevalue&anotherkey=anothervalue";
	public static final String DEFAULT_GET_ENTITY = "http://entity1.com";
		
	protected Solo robotium;
	protected InputMethodManager imm = null;

	private Map<String, JSONObject> jsons = null;

	public SocializeRobotiumTest() {
		super("com.socialize.sample", Main.class);
	}

	public void setUp() throws Exception {
		jsons = new HashMap<String, JSONObject>();
		robotium = new Solo(getInstrumentation(), getActivity());
		imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		robotium.clickOnButton("Launch Sample");
		robotium.waitForActivity("AuthenticateActivity");
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			robotium.finish();
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();

		super.tearDown();
	}

	/**
	 * Returns the user ID from the call to authenticate.
	 * @return
	 * @throws Throwable 
	 */
	protected int authenticateSocialize() {

		hideKeyboard();

		robotium.clickOnButton("Auth Socialize");

		waitForSuccess();

		// Get the user Id
		TextView txt = (TextView) robotium.getView(com.socialize.sample.R.id.txtAuthUserID);

		int userId = Integer.parseInt(txt.getText().toString());

		// go to API
		robotium.clickOnButton("Access API");
		robotium.waitForActivity("ApiActivity", DEFAULT_TIMEOUT_SECONDS);

		return userId;
	}

	protected void clearCache() {
		hideKeyboard();
		robotium.clickOnButton("Clear Cache");
		waitForSuccess();
	}

	protected final void sleep(int milliseconds) {
		synchronized (this) {
			try {
				wait(milliseconds);
			}
			catch (InterruptedException ignore) {}
		}
	}

	protected final void hideKeyboard() {
		// Hide keyboard for all
		ArrayList<EditText> currentEditTexts = robotium.getCurrentEditTexts();

		if(currentEditTexts != null) {
			for (EditText editText : currentEditTexts) {
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			}
		}
	}

	protected final void waitForSuccess() {
		// It seems robotium sometimes finds success before the 
		// progress dialog is closed, resulting in an abnormal abort
		// which causes a test failure.
		// Add a sleep to hack it
		sleep(4000);
		assertTrue(robotium.waitForText("SUCCESS", 1, DEFAULT_TIMEOUT_SECONDS));
	}

	/**
	 * Returns a json object based on the given path (local to device).  For example "existing-data/myfile.json"
	 * @param name
	 * @return
	 * @throws IOException 
	 * @throws JSONException 
	 */
	protected final JSONObject getJSON(String path)  {
		InputStream in = null;
		
		path = path.trim();
		
		if(!path.startsWith("existing-data")) {
			path  = "existing-data/" + path;
		}
		
		JSONObject json = jsons.get(path);

		if(json == null) {
			try {
				in = getActivity().getAssets().open(path);

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
