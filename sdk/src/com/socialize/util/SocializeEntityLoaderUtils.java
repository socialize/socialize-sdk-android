package com.socialize.util;

import com.socialize.Socialize;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeEntityLoader;

public class SocializeEntityLoaderUtils implements EntityLoaderUtils {
	
	private SocializeConfig config;
	private ObjectUtils objectUtils;
	private SocializeLogger logger;

	/* (non-Javadoc)
	 * @see com.socialize.util.EntityLoaderUtils#initEntityLoader()
	 */
	@Override
	public SocializeEntityLoader initEntityLoader() {
		SocializeEntityLoader entityLoader = Socialize.getSocialize().getEntityLoader();
		
		if(entityLoader == null) {

			String entityLoaderClassName = config.getProperty(SocializeConfig.SOCIALIZE_ENTITY_LOADER);
			
			if(!StringUtils.isEmpty(entityLoaderClassName)) {
				try {
					if(logger != null && logger.isDebugEnabled()) {
						logger.debug("Instantiating entity loader [" +
								entityLoader +
								"]");
					}
					
					Object loader = objectUtils.construct(entityLoaderClassName);
					
					if(loader instanceof SocializeEntityLoader) {
						entityLoader = (SocializeEntityLoader)loader;
						
						Socialize.getSocialize().setEntityLoader(entityLoader);
					}
					else {
						if(logger != null) {
							logger.error("Entity loader [" +
									entityLoader +
									"] is not an instance of [" +
									SocializeEntityLoader.class.getName() +
									"]");
						}
					}
				} 
				catch (SocializeException e) {
					if(logger != null) {
						logger.error("Failed to instantiate entity loader [" +
								entityLoader +
								"]", e);
					}
				}
			}
			else {
				if(logger != null) {
					logger.warn("No entity loader specified in socialize.properties");
				}	
			}		
		}
		
		return entityLoader;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setObjectUtils(ObjectUtils objectUtils) {
		this.objectUtils = objectUtils;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
}
