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
package com.socialize.net;

import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;

/**
 * Produces HttpClients with appropriate config.
 * @author Jason Polites
 *
 */
public class DefaultHttpClientFactory implements HttpClientFactory {

	private HttpParams params;
	private ClientConnectionManager connectionManager;
	private SocializeLogger logger;
	
	private boolean destroyed = false;
	
	/* (non-Javadoc)
	 * @see com.socialize.net.HttpClientFactory#init()
	 */
	@Override
	public void init(SocializeConfig config) throws SocializeException  {
		
		try {
			if(logger != null && logger.isInfoEnabled()) {
				logger.info("Initializing " + getClass().getSimpleName());
			}
			
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new NaiveSSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	        
	        HttpConnectionParams.setConnectionTimeout(params, config.getIntProperty(SocializeConfig.HTTP_CONNECTION_TIMEOUT, 10000));
	        HttpConnectionParams.setSoTimeout(params, config.getIntProperty(SocializeConfig.HTTP_SOCKET_TIMEOUT, 10000));
	        
	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        connectionManager = new ThreadSafeClientConnManager(params, registry);
	        
	        if(logger != null && logger.isInfoEnabled()) {
				logger.info("Initialized " + getClass().getSimpleName());
			}
	        
	        destroyed = false;
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.net.HttpClientFactory#destroy()
	 */
	@Override
	public void destroy() {
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("Destroying " + getClass().getSimpleName());
		}
		if(connectionManager != null) {
			connectionManager.shutdown();
		}
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("Destroyed " + getClass().getSimpleName());
		}
		
		destroyed = true;
	}

	/* (non-Javadoc)
	 * @see com.socialize.net.HttpClientFactory#getClient()
	 */
	@Override
	public HttpClient getClient() {
	   return new DefaultHttpClient(connectionManager, params);
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}
}
