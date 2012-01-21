package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.socialize.test.ui.auth.AuthConfirmDialogFactoryTest;
import com.socialize.test.ui.auth.AuthRequestDialogViewTest;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {
		
		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());
		
		suite.addTestSuite(AuthConfirmDialogFactoryTest.class);
		suite.addTestSuite(AuthRequestDialogViewTest.class);
		
		
//		suite.addTestSuite(AuthProviderDataTest.class);
//		suite.addTestSuite(AuthProviderTypeTest.class);
//		suite.addTestSuite(CommentFactoryBlackboxTest.class);
//		suite.addTestSuite(DeviceUtilsBlackboxTest.class);
//		suite.addTestSuite(HttpUtilsTest.class);
//		suite.addTestSuite(OAuthConsumerTest.class);
//		suite.addTestSuite(SocializeApiErrorTest.class);
//		suite.addTestSuite(SocializeBlackboxTest.class);
//		suite.addTestSuite(SocializeConfigTest.class);
		
		
//		suite.addTestSuite(SocializeIOCTest.class);
		
		
//		suite.addTestSuite(SocializeTest.class);
//		suite.addTestSuite(UrlBuilderTest.class);
		
//		suite.addTestSuite(SocializeCommentSystemTest.class);
//		suite.addTestSuite(SocializeEntitySystemTest.class);
//		suite.addTestSuite(SocializeLikeSystemTest.class);
//		suite.addTestSuite(SocializeViewSystemTest.class);
//		
//		suite.addTestSuite(ApplicationFactoryTest.class);
//		suite.addTestSuite(AuthProvidersTest.class);
//		suite.addTestSuite(BitmapUtilsTest.class);
//		suite.addTestSuite(CommentFactoryTest.class);
//		suite.addTestSuite(DefaultLocationProviderTest.class);
//		suite.addTestSuite(DefaultSocializeProviderTest.class);
//		suite.addTestSuite(DefaultSocializeSessionFactoryTest.class);
//		suite.addTestSuite(DeviceUtilsTest.class);
//		suite.addTestSuite(DrawablesTest.class);
//		suite.addTestSuite(EntityAccessorTests.class);
//		suite.addTestSuite(EntityFactoryTest.class);
//		suite.addTestSuite(FacebookActivityServiceTest.class);
//		suite.addTestSuite(FacebookActivityTest.class);
//		suite.addTestSuite(FacebookAuthProviderTest.class);
//		suite.addTestSuite(FacebookDialogListenerTest.class);
//		suite.addTestSuite(FacebookServiceTest.class);
//		suite.addTestSuite(FacebookSessionStoreTest.class);
//		suite.addTestSuite(FacebookUrlBuilderTest.class);
//		suite.addTestSuite(ImageUtilsTest.class);
//		suite.addTestSuite(IOUtilsTest.class);
//		suite.addTestSuite(OAuthRequestSignerTest.class);
//		suite.addTestSuite(PreferenceSessionPersisterTest.class);
//		suite.addTestSuite(SampleTest.class);
//		suite.addTestSuite(SocializeActionFactoryTest.class);
//		suite.addTestSuite(SocializeApiAsyncTest.class);
//		suite.addTestSuite(SocializeApiHostTest.class);
//		suite.addTestSuite(SocializeApiTest.class);
//		suite.addTestSuite(SocializeErrorTest.class);
//		suite.addTestSuite(SocializeFactoryTest.class);
//		suite.addTestSuite(SocializeListenerTest.class);
//		suite.addTestSuite(SocializeLocationListenerTest.class);
//		suite.addTestSuite(SocializeLoggerTest.class);
//		suite.addTestSuite(SocializeRequestFactoryTest.class);
//		suite.addTestSuite(SocializeServiceTest.class);
//		suite.addTestSuite(StatsFactoryTest.class);
//		suite.addTestSuite(StringUtilsTest.class);
//		suite.addTestSuite(TTLCacheTest.class);
//		suite.addTestSuite(UserAuthDataFactoryTest.class);
//		suite.addTestSuite(UserFactoryTest.class);
//		suite.addTestSuite(UserTest.class);
//		
		return suite;
	}
}
