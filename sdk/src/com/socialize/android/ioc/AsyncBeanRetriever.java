package com.socialize.android.ioc;

import android.os.AsyncTask;

public class AsyncBeanRetriever<T> extends AsyncTask<AsyncBeanRequest<T>, Void, AsyncBeanRequest<T>> {
	
	private Container container;

	public AsyncBeanRetriever(Container container) {
		super();
		this.container = container;
	}

	@Override
	protected AsyncBeanRequest<T> doInBackground(AsyncBeanRequest<T>... requests) {
		
		AsyncBeanRequest<T> request = requests[0];
		
		try {
			T bean = container.getBean(request.getName(), request.getArgs());
			request.setBean(bean);
		} 
		catch (Exception e) {
			request.setError(e);
		}
		return request;
	}

	@Override
	protected void onPostExecute(AsyncBeanRequest<T> result) {
		BeanCreationListener<T> beanCreationListener = result.getBeanCreationListener();
		if(beanCreationListener != null) {
			if(result.getError() != null) {
				beanCreationListener.onError(result.getName(), result.getError());
			}
			else if(result.getBean() != null) {
				beanCreationListener.onCreate(result.getBean());
			}
			else {
				beanCreationListener.onError(result.getName(), new Exception("Bean creation returned null"));
			}
		}
	}
}
