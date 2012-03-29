package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;
import com.socialize.test.blackbox.AuthProviderDataTest;
import com.socialize.test.blackbox.CommentFactoryBlackboxTest;
import com.socialize.test.blackbox.DeviceUtilsBlackboxTest;
import com.socialize.test.blackbox.HttpUtilsTest;
import com.socialize.test.blackbox.OAuthConsumerTest;
import com.socialize.test.blackbox.SocializeApiErrorTest;
import com.socialize.test.blackbox.SocializeBlackboxTest;
import com.socialize.test.blackbox.SocializeConfigTest;
import com.socialize.test.blackbox.SocializeIOCTest;
import com.socialize.test.blackbox.UrlBuilderTest;
import com.socialize.test.integration.DefaultHttpClientFactoryTest;
import com.socialize.test.integration.SocializeLocationManagerTest;
import com.socialize.test.integration.ViewTest;
import com.socialize.test.integration.runonprod.AuthenticateTest;
import com.socialize.test.integration.runonprod.CommentTest;
import com.socialize.test.integration.runonprod.EntityTest;
import com.socialize.test.integration.runonprod.LikeTest;
import com.socialize.test.integration.runonprod.UserActivityTest;
import com.socialize.test.ui.ActivityIOCProviderTest;
import com.socialize.test.ui.AuthenticatedViewListenerTest;
import com.socialize.test.ui.AuthenticatedViewTest;
import com.socialize.test.ui.BaseViewTest;
import com.socialize.test.ui.CommentAdapterTest;
import com.socialize.test.ui.DateUtilsTest;
import com.socialize.test.ui.DialogErrorHandlerTest;
import com.socialize.test.ui.ProgressDialogFactoryTest;
import com.socialize.test.ui.SocializeActivityTestCase;
import com.socialize.test.ui.SocializeButtonTest;
import com.socialize.test.ui.SocializeLaunchActivityTest;
import com.socialize.test.ui.SocializeUIInstanceTests2;
import com.socialize.test.ui.SocializeViewTest;
import com.socialize.test.ui.actionbar.SliderActionBarListenerTest;
import com.socialize.test.ui.actionbutton.SocializeActionButtonTest;
import com.socialize.test.ui.auth.AuthPanelViewTest;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());


		suite.addTestSuite(AuthProviderDataTest.class);
		suite.addTestSuite(CommentFactoryBlackboxTest.class);
		suite.addTestSuite(DeviceUtilsBlackboxTest.class);
		suite.addTestSuite(HttpUtilsTest.class);
		suite.addTestSuite(OAuthConsumerTest.class);
		suite.addTestSuite(SocializeApiErrorTest.class);
		suite.addTestSuite(SocializeBlackboxTest.class);
		suite.addTestSuite(SocializeConfigTest.class);
		suite.addTestSuite(SocializeIOCTest.class);
		suite.addTestSuite(UrlBuilderTest.class);
		suite.addTestSuite(CommentTest.class);
		suite.addTestSuite(DefaultHttpClientFactoryTest.class);
		suite.addTestSuite(EntityTest.class);
		suite.addTestSuite(LikeTest.class);
		suite.addTestSuite(SocializeLocationManagerTest.class);
		suite.addTestSuite(ViewTest.class);
		suite.addTestSuite(AuthenticateTest.class);
		suite.addTestSuite(CommentTest.class);
		suite.addTestSuite(EntityTest.class);
		suite.addTestSuite(LikeTest.class);
		suite.addTestSuite(UserActivityTest.class);
		suite.addTestSuite(ActivityIOCProviderTest.class);
		suite.addTestSuite(AuthenticatedViewListenerTest.class);
		suite.addTestSuite(AuthenticatedViewTest.class);
		suite.addTestSuite(BaseViewTest.class);
		suite.addTestSuite(CommentAdapterTest.class);
		suite.addTestSuite(DateUtilsTest.class);
		suite.addTestSuite(DialogErrorHandlerTest.class);
		suite.addTestSuite(ProgressDialogFactoryTest.class);
		suite.addTestSuite(SocializeActivityTestCase.class);
		suite.addTestSuite(SocializeButtonTest.class);
		suite.addTestSuite(SocializeLaunchActivityTest.class);
		suite.addTestSuite(SocializeUIInstanceTests2.class);
		suite.addTestSuite(SocializeViewTest.class);
		suite.addTestSuite(SliderActionBarListenerTest.class);
		suite.addTestSuite(SocializeActionButtonTest.class);
		suite.addTestSuite(AuthPanelViewTest.class);



		return suite;
	}
}
