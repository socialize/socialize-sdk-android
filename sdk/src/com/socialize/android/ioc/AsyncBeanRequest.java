package com.socialize.android.ioc;

public class AsyncBeanRequest<T> {

	private String name;
	private Object[] args;
	private BeanCreationListener<T> beanCreationListener;
	private T bean;
	private Exception error;

	public BeanCreationListener<T> getBeanCreationListener() {
		return beanCreationListener;
	}

	public void setBeanCreationListener(BeanCreationListener<T> beanCreationListener) {
		this.beanCreationListener = beanCreationListener;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public T getBean() {
		return bean;
	}

	public void setBean(T bean) {
		this.bean = bean;
	}

	public Exception getError() {
		return error;
	}

	public void setError(Exception error) {
		this.error = error;
	}
	
	
}
