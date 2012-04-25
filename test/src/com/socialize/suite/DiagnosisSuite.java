package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());


		//		suite.addTestSuite(AuthProviderDataTest.class);
		//		suite.addTestSuite(CommentFactoryBlackboxTest.class);
		//		suite.addTestSuite(DeviceUtilsBlackboxTest.class);
		//		suite.addTestSuite(HttpUtilsTest.class);
		//		suite.addTestSuite(OAuthConsumerTest.class);
		//		suite.addTestSuite(SocializeApiErrorTest.class);
		//		suite.addTestSuite(SocializeBlackboxTest.class);
		//		suite.addTestSuite(SocializeConfigTest.class);
		//		suite.addTestSuite(SocializeIOCTest.class);
		//		suite.addTestSuite(UrlBuilderTest.class);
		//		suite.addTestSuite(CommentTest.class);
		//		suite.addTestSuite(DefaultHttpClientFactoryTest.class);
		//		suite.addTestSuite(EntityTest.class);
		//		suite.addTestSuite(LikeTest.class);
		//		suite.addTestSuite(SocializeLocationManagerTest.class);
		//		suite.addTestSuite(ViewTest.class);
		//		suite.addTestSuite(AuthenticateTest.class);
		//		suite.addTestSuite(CommentTest.class);
		//		suite.addTestSuite(EntityTest.class);
		//		suite.addTestSuite(LikeTest.class);
		//		suite.addTestSuite(UserActivityTest.class);
		//		suite.addTestSuite(ActivityIOCProviderTest.class);
		//		suite.addTestSuite(AuthenticatedViewListenerTest.class);
		//		suite.addTestSuite(AuthenticatedViewTest.class);
		//		suite.addTestSuite(BaseViewTest.class);
		//		suite.addTestSuite(CommentAdapterTest.class);
		//		suite.addTestSuite(DateUtilsTest.class);
		//		suite.addTestSuite(DialogErrorHandlerTest.class);
		//		suite.addTestSuite(ProgressDialogFactoryTest.class);
		//		suite.addTestSuite(SocializeActivityTestCase.class);
		//		suite.addTestSuite(SocializeButtonTest.class);
		//		suite.addTestSuite(SocializeLaunchActivityTest.class);
		//		suite.addTestSuite(SocializeUIInstanceTests2.class);
		//		suite.addTestSuite(SocializeViewTest.class);
		//		suite.addTestSuite(SliderActionBarListenerTest.class);
		//		suite.addTestSuite(SocializeActionButtonTest.class);
		//		suite.addTestSuite(AuthPanelViewTest.class);

		//		suite.addTestSuite(DirectUrlIntegrationTest.class);
		//		suite.addTestSuite(DirectUrlNotificationTest.class);

		suite.addTestSuite(com.socialize.test.integration.notification.DeveloperNotificationTest.class);
		suite.addTestSuite(com.socialize.test.integration.notification.DirectEntityIntegrationTest.class);
		suite.addTestSuite(com.socialize.test.integration.notification.DirectEntityNotificationTest.class);
		suite.addTestSuite(com.socialize.test.integration.notification.DirectUrlIntegrationTest.class);
		suite.addTestSuite(com.socialize.test.integration.notification.DirectUrlNotificationTest.class);
		suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsIntegrationTestA.class);
		suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsIntegrationTestB.class);
		suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsNotificationTest.class);
		suite.addTestSuite(com.socialize.test.integration.services.LikeUtilsTest.class);
		suite.addTestSuite(com.socialize.test.integration.services.UserUtilsTest.class);
		suite.addTestSuite(com.socialize.test.ui.ActivityIOCProviderTest.class);		

		return suite;
	}
}
