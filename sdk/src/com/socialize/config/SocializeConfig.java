package com.socialize.config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

public class SocializeConfig {
	
	private Properties properties;
	
	private String propertiesFileName = "socialize.properties";
	private final String defaultPropertiesFileName = propertiesFileName;
	
	public SocializeConfig() {
		super();
	}

	public SocializeConfig(String propertiesFileName) {
		super();
		this.propertiesFileName = propertiesFileName;
	}
	
	public void init(Context context) {
		InputStream in = null;
		try {
			try {
				
				// Check assets first for override
				try {
					in = context.getAssets().open(propertiesFileName);
				}
				catch (FileNotFoundException ignore) {
					// Ignore this, just means no override.
				}
				
				if(in == null) {
					in = Thread.currentThread().getContextClassLoader().getResourceAsStream(defaultPropertiesFileName); // Don't use prop file name
				}
				
				if(in != null) {
					properties = new Properties();
					properties.load(in);
				}
				else {
					throw new FileNotFoundException("Could not locate [" +
							defaultPropertiesFileName +
							"] in the classpath");
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

	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	public String getDefaultPropertiesFileName() {
		return defaultPropertiesFileName;
	}
	
}
