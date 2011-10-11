package com.socialize.ui.integrationtest.comment;

import com.socialize.ui.integrationtest.SocializeUIRobotiumTest;

public class CommentListTest extends SocializeUIRobotiumTest {

	public void testListComments() throws InterruptedException {
		robotium.waitForActivity("CommentActivity");
		
		synchronized (this) {
			wait(10000);
		}
		
	}
	
}
