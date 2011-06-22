package com.socialize.sample;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.socialize.util.IOUtils;

/**
 * Used to render HTML files generated from 500 errors produced by the server.
 * @author jasonpolites
 *
 */
public class Error500Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WebView webview = new WebView(this);
		setContentView(webview);
		
		InputStream in = null;
		try {
			in = this.openFileInput("error.html");
			
			if(in != null) {
				IOUtils utils = new IOUtils();
				String read = utils.read(in);
				webview.loadData(read, "text/html", "utf-8");
			}
			else {
				webview.loadData("No Error Data!", "text/html", "utf-8");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
