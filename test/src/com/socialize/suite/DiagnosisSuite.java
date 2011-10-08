package com.socialize.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.socialize.test.blackbox.AuthProviderDataTest;
import com.socialize.test.blackbox.AuthProviderTypeTest;
import com.socialize.test.blackbox.CommentFactoryBlackboxTest;
import com.socialize.test.blackbox.DeviceUtilsBlackboxTest;
import com.socialize.test.blackbox.HttpUtilsTest;
import com.socialize.test.blackbox.OAuthConsumerTest;
import com.socialize.test.blackbox.SocializeApiErrorTest;
import com.socialize.test.blackbox.SocializeBlackboxTest;
import com.socialize.test.blackbox.SocializeConfigTest;
import com.socialize.test.blackbox.SocializeIOCTest;
import com.socialize.test.blackbox.SocializeTest;
import com.socialize.test.blackbox.UrlBuilderTest;
import com.socialize.test.unit.ApplicationFactoryTest;
import com.socialize.test.unit.AuthProvidersTest;
import com.socialize.test.unit.BitmapUtilsTest;
import com.socialize.test.unit.CommentFactoryTest;
import com.socialize.test.unit.DefaultLocationProviderTest;
import com.socialize.test.unit.DefaultSocializeProviderTest;
import com.socialize.test.unit.DefaultSocializeSessionFactoryTest;
import com.socialize.test.unit.DeviceUtilsTest;
import com.socialize.test.unit.DrawablesTest;
import com.socialize.test.unit.EntityAccessorTests;
import com.socialize.test.unit.EntityFactoryTest;
import com.socialize.test.unit.FacebookActivityServiceTest;
import com.socialize.test.unit.FacebookActivityTest;
import com.socialize.test.unit.FacebookAuthProviderTest;
import com.socialize.test.unit.FacebookDialogListenerTest;
import com.socialize.test.unit.FacebookServiceTest;
import com.socialize.test.unit.FacebookSessionStoreTest;
import com.socialize.test.unit.FacebookUrlBuilderTest;
import com.socialize.test.unit.IOUtilsTest;
import com.socialize.test.unit.ImageUtilsTest;
import com.socialize.test.unit.OAuthRequestSignerTest;
import com.socialize.test.unit.PreferenceSessionPersisterTest;
import com.socialize.test.unit.SampleTest;
import com.socialize.test.unit.SocializeActionFactoryTest;
import com.socialize.test.unit.SocializeApiAsyncTest;
import com.socialize.test.unit.SocializeApiHostTest;
import com.socialize.test.unit.SocializeApiTest;
import com.socialize.test.unit.SocializeErrorTest;
import com.socialize.test.unit.SocializeFactoryTest;
import com.socialize.test.unit.SocializeListenerTest;
import com.socialize.test.unit.SocializeLocationListenerTest;
import com.socialize.test.unit.SocializeLoggerTest;
import com.socialize.test.unit.SocializeRequestFactoryTest;
import com.socialize.test.unit.SocializeServiceTest;
import com.socialize.test.unit.StatsFactoryTest;
import com.socialize.test.unit.StringUtilsTest;
import com.socialize.test.unit.TTLCacheTest;
import com.socialize.test.unit.UserAuthDataFactoryTest;
import com.socialize.test.unit.UserFactoryTest;
import com.socialize.test.unit.UserTest;
import com.socialize.test.unit.api.CommentApiTest;
import com.socialize.test.unit.api.EntityApiTest;
import com.socialize.test.unit.api.LikeApiTest;
import com.socialize.test.unit.api.ViewApiTest;

public class DiagnosisSuite extends TestSuite {

	public static Test suite() {
		
		TestSuite suite = new TestSuite(DiagnosisSuite.class.getName());
		
//		suite.addTestSuite(AuthProviderDataTest.class);
//		suite.addTestSuite(AuthProviderTypeTest.class);
//		suite.addTestSuite(CommentFactoryBlackboxTest.class);
//		suite.addTestSuite(DeviceUtilsBlackboxTest.class);
//		suite.addTestSuite(HttpUtilsTest.class);
//		suite.addTestSuite(OAuthConsumerTest.class);
//		suite.addTestSuite(SocializeApiErrorTest.class);
//		suite.addTestSuite(SocializeBlackboxTest.class);
//		suite.addTestSuite(SocializeConfigTest.class);
		
		
		suite.addTestSuite(SocializeIOCTest.class);
		
		
//		suite.addTestSuite(SocializeTest.class);
//		suite.addTestSuite(UrlBuilderTest.class);
		
		
		
		
		
//		suite.addTestSuite(CommentApiTest.class);
//		suite.addTestSuite(EntityApiTest.class);
//		suite.addTestSuite(LikeApiTest.class);
//		suite.addTestSuite(ViewApiTest.class);
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
		suite.addTestSuite(SocializeApiAsyncTest.class);
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
