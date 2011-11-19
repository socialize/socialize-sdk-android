package com.socialize;

public class SocializeBeanAccess {
	public <T extends Object> T getBean(String beanName) {
		return getBean(Socialize.getSocialize(), beanName);
	}
	
	public <T extends Object> T getBean(SocializeService socialize, String beanName) {
		if(socialize instanceof SocializeServiceImpl) {
			SocializeServiceImpl impl = (SocializeServiceImpl) socialize;
			return impl.getContainer().getBean(beanName);
		}
		return null;
	}
}
