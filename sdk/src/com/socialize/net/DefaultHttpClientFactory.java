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
package com.socialize.net;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
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

import java.security.KeyStore;

/**
 * Produces HttpClients with appropriate config.
 * @author Jason Polites
 *
 */
public class DefaultHttpClientFactory implements HttpClientFactory {

	private HttpParams params;

	private ClientConnectionManager connectionManager;

	private SocializeLogger logger;
	
	private IBeanFactory<DefaultHttpClient> apacheHttpClientFactory;
	
	private DefaultHttpClient client; // This should be thread safe
	
	private IdleConnectionMonitorThread monitor;
	
	private boolean destroyed = false;
	
	/* (non-Javadoc)
	 * @see com.socialize.net.HttpClientFactory#init()
	 */
	@Override
	public void init(SocializeConfig config) throws SocializeException  {
		
		try {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Initializing " + getClass().getSimpleName());
			}
			
	        params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	        
	        HttpConnectionParams.setConnectionTimeout(params, config.getIntProperty(SocializeConfig.HTTP_CONNECTION_TIMEOUT, 10000));
	        HttpConnectionParams.setSoTimeout(params, config.getIntProperty(SocializeConfig.HTTP_SOCKET_TIMEOUT, 10000));
	        
	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

	        connectionManager = new ThreadSafeClientConnManager(params, registry);
	        
	        monitor = new IdleConnectionMonitorThread(connectionManager);
	        monitor.setDaemon(true);
	        monitor.start();
	        
	        if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Initialized " + getClass().getSimpleName());
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
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Destroying " + getClass().getSimpleName());
		}
		if(monitor != null) {
			monitor.shutdown();
		}
		
		if(connectionManager != null) {
			connectionManager.shutdown();
		}
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Destroyed " + getClass().getSimpleName());
		}
		
		destroyed = true;
	}

	/* (non-Javadoc)
	 * @see com.socialize.net.HttpClientFactory#getClient()
	 */
	@Override
	public synchronized HttpClient getClient() {
		if(client == null) {
			if(apacheHttpClientFactory != null) {
				client = apacheHttpClientFactory.getBean(connectionManager, params);
			}
			else {
				client = new DefaultHttpClient(connectionManager, params);
			}
		}
		else {
			monitor.trigger();
		}
		return client;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}
	
	public void setApacheHttpClientFactory(IBeanFactory<DefaultHttpClient> apacheHttpClientFactory) {
		this.apacheHttpClientFactory = apacheHttpClientFactory;
	}
	
}
