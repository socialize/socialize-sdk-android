package com.socialize.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

public class SocializeConfig {
	
	private Properties properties;

	public void init(Context context) {
		InputStream in = null;
		try {
			try {
				in = context.getAssets().open("socialize.properties");
				
				if(in != null) {
					properties = new Properties();
					properties.load(in);
				}
			}
			catch (FileNotFoundException ignore) {
				// TODO: log
			}
			finally {
				if(in != null) {
					in.close();
				}
			}
			
			if(properties == null) {
				properties = new Properties();
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Properties getProperties() {
		return properties;
	}
}
