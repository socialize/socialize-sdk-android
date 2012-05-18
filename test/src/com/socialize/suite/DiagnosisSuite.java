package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());

	    suite.addTestSuite(com.socialize.test.ui.integrationtest.actionbar.ActionBarCommentTest.class);
	    suite.addTestSuite(com.socialize.test.ui.integrationtest.actionbar.ActionBarLikeAutoTest.class);
	    suite.addTestSuite(com.socialize.test.ui.integrationtest.actionbar.ActionBarLikeManualTest.class);
	    suite.addTestSuite(com.socialize.test.ui.integrationtest.actionbar.ActionBarLikeStateTest.class);
	    suite.addTestSuite(com.socialize.test.ui.integrationtest.actionbar.ActionBarListenerTest.class);
	    suite.addTestSuite(com.socialize.test.ui.integrationtest.actionbar.AuthRequestDialogFactoryTest.class);
	    suite.addTestSuite(com.socialize.test.ui.integrationtest.actionbar.ShareDialogViewTest.class);
	    suite.addTestSuite(com.socialize.test.ui.integrationtest.actionbutton.LikeButtonManualTest.class);		
		
		
	    suite.addTestSuite(com.socialize.test.ui.integrationtest.comment.CommentUITest.class);
	    suite.addTestSuite(com.socialize.test.ui.network.SocialNetworkSignOutClickListenerTest.class);
	    suite.addTestSuite(com.socialize.test.ui.profile.ProfileActivityLoadTest.class);
	    suite.addTestSuite(com.socialize.test.ui.share.AbstractShareHandlerTest.class);
	    suite.addTestSuite(com.socialize.test.ui.share.EmailShareHandlerTest.class);
	    suite.addTestSuite(com.socialize.test.ui.share.SharePanelViewTest.class);		

		return suite;
	}
}
