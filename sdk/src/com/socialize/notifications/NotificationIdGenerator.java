package com.socialize.notifications;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @deprecated Just wrong.  We need to store the ID on disk, or generate it using a random int.
 * @author Jason Polites
 *
 */
@Deprecated
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
