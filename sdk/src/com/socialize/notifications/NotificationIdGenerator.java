package com.socialize.notifications;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationIdGenerator {

	private AtomicInteger counter;

	public NotificationIdGenerator() {
		super();
		counter = new AtomicInteger(100);
	}
	
	public int getNextId() {
		return counter.getAndIncrement();
	}
}
