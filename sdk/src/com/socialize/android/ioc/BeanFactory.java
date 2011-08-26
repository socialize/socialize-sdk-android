package com.socialize.android.ioc;

public class BeanFactory<T> implements IBeanFactory<T> {
	
	String beanName;
	Container container;

	public BeanFactory() {
		super();
	}
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getBean() {
		return (T) container.getBean(beanName);
	}
}
