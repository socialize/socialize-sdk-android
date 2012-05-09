package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {

		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());
		
	     suite.addTestSuite(com.socialize.test.blackbox.AuthProviderDataTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.AuthProviderTypeTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.CommentFactoryBlackboxTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.DeviceUtilsBlackboxTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.HttpUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.OAuthConsumerTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.SocializeApiErrorTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.SocializeBlackboxTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.SocializeConfigTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.SocializeIOCTest.class);
	     suite.addTestSuite(com.socialize.test.blackbox.UrlBuilderTest.class);
	     suite.addTestSuite(com.socialize.test.integration.AuthenticateSDKTest.class);
	     suite.addTestSuite(com.socialize.test.integration.CommentSDKTest.class);
	     suite.addTestSuite(com.socialize.test.integration.DefaultHttpClientFactoryTest.class);
	     suite.addTestSuite(com.socialize.test.integration.EntitySDKTest.class);
	     suite.addTestSuite(com.socialize.test.integration.LikeSDKTest.class);
	     suite.addTestSuite(com.socialize.test.integration.SocializeLocationManagerTest.class);
	     suite.addTestSuite(com.socialize.test.integration.UserActivitySDKTest.class);
	     suite.addTestSuite(com.socialize.test.integration.ViewSDKTest.class);
	     suite.addTestSuite(com.socialize.test.integration.notification.DeveloperNotificationTest.class);
	     suite.addTestSuite(com.socialize.test.integration.notification.DirectEntityIntegrationTest.class);
	     suite.addTestSuite(com.socialize.test.integration.notification.DirectEntityNotificationTest.class);
	     suite.addTestSuite(com.socialize.test.integration.notification.DirectUrlIntegrationTest.class);
	     suite.addTestSuite(com.socialize.test.integration.notification.DirectUrlNotificationTest.class);
	     suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsIntegrationTestA.class);
	     suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsIntegrationTestB.class);
	     suite.addTestSuite(com.socialize.test.integration.notification.NewCommentsNotificationTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.ActionUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.CommentUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.ConcurrentTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.EntityUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.FacebookUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.LikeUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.LocationUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.ShareUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.TwitterUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.UserUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.integration.services.ViewUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.ui.ActivityIOCProviderTest.class);
	     suite.addTestSuite(com.socialize.test.ui.AlertDialogFactoryTest.class);
	     suite.addTestSuite(com.socialize.test.ui.AuthenticatedViewListenerTest.class);
	     suite.addTestSuite(com.socialize.test.ui.AuthenticatedViewTest.class);
	     suite.addTestSuite(com.socialize.test.ui.BaseViewTest.class);
	     suite.addTestSuite(com.socialize.test.ui.CommentAdapterTest.class);
	     suite.addTestSuite(com.socialize.test.ui.DateUtilsTest.class);
	     suite.addTestSuite(com.socialize.test.ui.DialogErrorHandlerTest.class);
	     suite.addTestSuite(com.socialize.test.ui.ProgressDialogFactoryTest.class);
	     suite.addTestSuite(com.socialize.test.ui.SocializeActivityTestCase.class);
	     suite.addTestSuite(com.socialize.test.ui.SocializeButtonTest.class);
	     suite.addTestSuite(com.socialize.test.ui.SocializeLaunchActivityTest.class);
	     suite.addTestSuite(com.socialize.test.ui.SocializeUIInstanceTests2.class);
	     suite.addTestSuite(com.socialize.test.ui.SocializeViewTest.class);
	     suite.addTestSuite(com.socialize.test.ui.actionbar.SliderActionBarListenerTest.class);
	     suite.addTestSuite(com.socialize.test.ui.actionbutton.SocializeActionButtonTest.class);
	     suite.addTestSuite(com.socialize.test.ui.auth.AuthPanelViewTest.class);
	     suite.addTestSuite(com.socialize.test.ui.comment.CommentActivityLoadTest.class);
	     suite.addTestSuite(com.socialize.test.ui.comment.CommentEditFieldFactoryTest.class);
	     suite.addTestSuite(com.socialize.test.ui.comment.CommentListItemTest.class);
	     suite.addTestSuite(com.socialize.test.ui.comment.CommentListViewTest.class);
	     suite.addTestSuite(com.socialize.test.ui.comment.CommentReAuthListenerTest.class);
	     suite.addTestSuite(com.socialize.test.ui.comment.ListItemLoadingViewTest.class);
	     suite.addTestSuite(com.socialize.test.ui.facebook.FacebookButtonTest.class);
	     suite.addTestSuite(com.socialize.test.ui.facebook.FacebookSharerTest.class);
	     suite.addTestSuite(com.socialize.test.ui.facebook.FacebookWallPosterTest.class);
	     suite.addTestSuite(com.socialize.test.ui.image.ImageLoadAsyncTaskTest.class);
	     suite.addTestSuite(com.socialize.test.ui.image.ImageLoadRequestTest.class);
	     suite.addTestSuite(com.socialize.test.ui.image.ImageLoaderTest.class);
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
		
		
		
		suite.addTestSuite(com.socialize.test.ui.twitter.TwitterAuthProviderInfoTest.class);
		suite.addTestSuite(com.socialize.test.ui.twitter.TwitterAuthProviderTest.class);
		suite.addTestSuite(com.socialize.test.ui.twitter.TwitterAuthUtilsTest.class);
		suite.addTestSuite(com.socialize.test.ui.twitter.TwitterAuthViewTest.class);
		suite.addTestSuite(com.socialize.test.ui.twitter.TwitterAuthWebViewTest.class);
		suite.addTestSuite(com.socialize.test.ui.twitter.TwitterOAuthProviderTest.class);
		suite.addTestSuite(com.socialize.test.ui.twitter.TwitterWebViewClientTest.class);
		suite.addTestSuite(com.socialize.test.unit.AppUtilsTest.class);
		suite.addTestSuite(com.socialize.test.unit.ApplicationFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.AuthProvidersTest.class);
		suite.addTestSuite(com.socialize.test.unit.BitmapUtilsTest.class);
		suite.addTestSuite(com.socialize.test.unit.CommentFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.DefaultLocationProviderTest.class);
		suite.addTestSuite(com.socialize.test.unit.DefaultShareMessageBuilderTest.class);
		suite.addTestSuite(com.socialize.test.unit.DefaultSocializeProviderTest.class);
		suite.addTestSuite(com.socialize.test.unit.DeviceUtilsTest.class);
		suite.addTestSuite(com.socialize.test.unit.DrawablesTest.class);
		suite.addTestSuite(com.socialize.test.unit.EntityAccessorTests.class);
		suite.addTestSuite(com.socialize.test.unit.EntityFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.EntityLoaderUtilsTest.class);
		suite.addTestSuite(com.socialize.test.unit.GSONUtilsTest.class);
		suite.addTestSuite(com.socialize.test.unit.GenericActionFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.IOUtilsTest.class);
		suite.addTestSuite(com.socialize.test.unit.ImageUrlLoaderTest.class);
		suite.addTestSuite(com.socialize.test.unit.ImageUtilsTest.class);
		suite.addTestSuite(com.socialize.test.unit.InitializationAsserterTest.class);
		suite.addTestSuite(com.socialize.test.unit.OAuthRequestSignerTest.class);
		suite.addTestSuite(com.socialize.test.unit.PreferenceSessionPersisterTest.class);
		suite.addTestSuite(com.socialize.test.unit.PropagationFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.PropagationInfoResponseFactoryTest.class);		
		
		suite.addTestSuite(com.socialize.test.unit.ShareFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.ShareOptionsTest.class);
		suite.addTestSuite(com.socialize.test.unit.ShareTypeTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeActionFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeApiAsyncTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeApiTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeErrorTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeListenerTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeLocationListenerTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeLoggerTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeRequestFactoryTest.class);
		suite.addTestSuite(com.socialize.test.unit.SocializeServiceTest.class);	

		return suite;
	}
}
