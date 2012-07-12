package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());

		suite.addTestSuite(com.socialize.test.ui.actionbar.ActionBarShareTestListener.class);
		suite.addTestSuite(com.socialize.test.ui.actionbar.ActionBarShareTest.class);


		return suite;
	}
}
