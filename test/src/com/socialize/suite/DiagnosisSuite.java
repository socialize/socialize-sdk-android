package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());

		suite.addTestSuite(com.socialize.test.ui.actionbar.ActionBarCommentTest.class);	
		suite.addTestSuite(com.socialize.test.ui.actionbar.ActionBarLikeAutoTest.class);
		suite.addTestSuite(com.socialize.test.ui.actionbar.ActionBarLikeManualTest.class);
		suite.addTestSuite(com.socialize.test.ui.actionbar.ActionBarLikeStateTest.class);	
		suite.addTestSuite(com.socialize.test.ui.actionbar.ActionBarListenerTest.class);
		suite.addTestSuite(com.socialize.test.ui.actionbar.AuthRequestDialogFactoryTest.class);

		return suite;
	}
}
