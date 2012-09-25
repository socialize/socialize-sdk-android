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
package com.socialize.launcher;

import android.app.Activity;
import android.os.Bundle;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.entity.EntitySystem;
import com.socialize.entity.Entity;
import com.socialize.log.SocializeLogger;
import com.socialize.notifications.NotificationAuthenticator;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.util.EntityLoaderUtils;

/**
 * @author Jason Polites
 *
 */
public class EntityLauncher extends BaseLauncher {
	
	private EntityLoaderUtils entityLoaderUtils;
	private SocializeLogger logger;
	private EntitySystem entitySystem;
	private NotificationAuthenticator notificationAuthenticator;
	
	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#launch(android.app.Activity, android.os.Bundle)
	 */
	@Override
	public boolean launch(final Activity context, Bundle data) {
		
		if(entityLoaderUtils != null) {
			final SocializeEntityLoader entityLoader = entityLoaderUtils.initEntityLoader();
			
			if(entityLoader != null) {
				
				Object idObj = data.get(Socialize.ENTITY_ID);
				
				if(idObj != null) {
					long id = Long.parseLong(idObj.toString());
					try {
						SocializeSession session = notificationAuthenticator.authenticate(context);
						Entity entity = entitySystem.getEntitySynchronous(session, id);
						return loadEntity(context, entityLoader, entity);
					}
					catch (Exception e) {
						handleError("Failed to load entity", e);
					}
				}
				else {
					handleWarn("No entity id found.  Entity based notification cannot be handled");
				}
			}
			else {
				handleWarn("No entity loader found.  Entity based notification cannot be handled");
			}
		}
		
		return false;
	}
	
	protected boolean loadEntity(Activity context, SocializeEntityLoader entityLoader, Entity entity) {
		if(entity != null) {
			if(entityLoader.canLoad(context, entity)) {
				
				if(logger != null && logger.isInfoEnabled()) {
					logger.info("Calling entity loader for entity with key [" +
							entity.getKey() +
							"]");
				}
				
				entityLoader.loadEntity(context, entity);
				
				return true;
			}
			else {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Entity loaded indicates that entity with key [" +
							entity.getKey() +
							"] cannot be loaded");
				}
			}
		}
		else {
			handleWarn("No entity object found under key [" +
					Socialize.ENTITY_OBJECT +
					"] in EntityLaucher");
			
		}
		
		return false;
	}
	
	protected void handleWarn(String msg) {
		if(logger != null) {
			logger.warn(msg);
		}
		else {
			System.err.println(msg);
		}
	}
	
	protected void handleError(String msg, Exception e) {
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			SocializeLogger.e(e.getMessage(), e);
		}
	}	
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	public void setEntityLoaderUtils(EntityLoaderUtils entityLoaderUtils) {
		this.entityLoaderUtils = entityLoaderUtils;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setEntitySystem(EntitySystem entitySystem) {
		this.entitySystem = entitySystem;
	}

	public void setNotificationAuthenticator(NotificationAuthenticator notificationAuthenticator) {
		this.notificationAuthenticator = notificationAuthenticator;
	}
}
