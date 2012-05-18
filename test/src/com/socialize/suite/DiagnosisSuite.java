package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());

		suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsIntegrationTestA.class);
		suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsIntegrationTestB.class);
		suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsNotificationTest.class);	

		suite.addTestSuite(com.socialize.test.integration.services.ActionUtilsTest.class);
		suite.addTestSuite(com.socialize.test.integration.services.CommentUtilsTest.class);
		suite.addTestSuite(com.socialize.test.integration.services.ConcurrentTest.class);	    

		return suite;
	}
}
