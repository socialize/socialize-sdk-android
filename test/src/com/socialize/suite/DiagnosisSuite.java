package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());

		
		
		suite.addTestSuite(com.socialize.test.ui.integrationtest.comment.CommentUITest.class);
//		suite.addTestSuite(com.socialize.test.ui.network.SocialNetworkSignOutClickListenerTest.class);
//		suite.addTestSuite(com.socialize.test.ui.profile.ProfileActivityLoadTest.class);
//		suite.addTestSuite(com.socialize.test.ui.share.AbstractShareHandlerTest.class);
//		suite.addTestSuite(com.socialize.test.ui.share.EmailShareHandlerTest.class);
		suite.addTestSuite(com.socialize.test.ui.share.SharePanelViewTest.class);

		return suite;
	}
}
