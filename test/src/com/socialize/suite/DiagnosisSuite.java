package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.socialize.test.ui.integrationtest.comment.CommentUITest;
import com.socialize.test.ui.profile.ProfileActivityLoadTest;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {
		
		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());
		
		
//		suite.addTestSuite(ActionBarCommentTest.class);
//		suite.addTestSuite(ActionBarLikeAutoTest.class);
//		suite.addTestSuite(ActionBarLikeManualTest.class);
//		suite.addTestSuite(ActionBarLikeStateTest.class);
//		suite.addTestSuite(ActionBarListenerTest.class);
//		suite.addTestSuite(ActionBarShareAutoTest.class);
//		suite.addTestSuite(LikeButtonManualTest.class);
		suite.addTestSuite(CommentUITest.class);
		suite.addTestSuite(ProfileActivityLoadTest.class);
		return suite;
	}
}
