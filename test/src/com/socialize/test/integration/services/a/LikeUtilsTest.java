/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.test.integration.services.a;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.LikeUtils;
import com.socialize.SocializeAccess;
import com.socialize.SocializeService;
import com.socialize.UserUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionOptions;
import com.socialize.api.action.ShareableActionOptions;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.api.action.like.LikeSystem;
import com.socialize.api.action.like.SocializeLikeSystem;
import com.socialize.api.action.like.SocializeLikeUtils;
import com.socialize.api.action.user.SocializeUserUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.IsLikedListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbutton.LikeButtonListener;
import com.socialize.ui.profile.UserSettings;
import com.socialize.ui.share.IShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;


/**
 * @author Jason Polites
 *
 */
public class LikeUtilsTest extends SocializeActivityTest {

	public void testAddLike() throws InterruptedException {
		final Entity entityKey = Entity.newInstance("testAddLike", "testAddLike");
		final CountDownLatch latch = new CountDownLatch(1);
		
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		LikeUtils.like(TestUtils.getActivity(this), entityKey, options, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				addResult(0, error);
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				addResult(0, like);
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Object result = getResult(0);
		
		assertNotNull(result);
		assertTrue("Result is not a like object", (result instanceof Like));
		
		Like like = (Like) result;
		assertTrue("Like ID is not greater than 0", like.getId() > 0);
		
		// Now use the normal UI to retrieve the like
		clearResults();
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		LikeUtils.getLike(TestUtils.getActivity(this), like.getId(), new LikeGetListener() {
			
			@Override
			public void onGet(Like entity) {
				addResult(0, entity);
				latch2.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
		});
		
		latch2.await(20, TimeUnit.SECONDS);
		
		Like after = getResult(0);
		
		assertNotNull(after);
		assertEquals(like.getId(), after.getId());
		assertEquals(entityKey.getKey(), after.getEntityKey());
	}
	
	public void testGetLikeExists() throws InterruptedException {
		
		final Entity entityKey = Entity.newInstance("testGetLikeExists", "testGetLikeExists");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		LikeUtils.like(TestUtils.getActivity(this), entityKey, options, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				LikeUtils.getLike(TestUtils.getActivity(LikeUtilsTest.this), entityKey.getKey(), new LikeGetListener() {
					
					@Override
					public void onGet(Like entity) {
						addResult(0, entity);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				});
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		Like like = getResult(0);
		assertNotNull(like);
		assertEquals(entityKey.getKey(), like.getEntityKey());
	}
	
	public void testGetLikeDoesNotExist() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final String entityKey = "testGetLikeDoesNotExist";
		
		LikeUtils.isLiked(TestUtils.getActivity(this), entityKey, new IsLikedListener() {
			
			@Override
			public void onLiked(Like like) {
				fail();
			}

			@Override
			public void onNotLiked() {
				addResult("success");
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		String success = getResult(0);
		assertNotNull(success);
		assertEquals("success", success);
	}
	
	public void testUnlike() throws InterruptedException {
		final Entity entityKey = Entity.newInstance("testUnlike", "testUnlike");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		LikeUtils.like(TestUtils.getActivity(this), entityKey, options, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				
				LikeUtils.unlike(TestUtils.getActivity(LikeUtilsTest.this), entityKey.getKey(), new LikeDeleteListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
					
					@Override
					public void onDelete() {
						addResult("success");
						latch.countDown();
					}
				});
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		String success = getResult(0);
		assertNotNull(success);
		assertEquals("success", success);
	}
	
	public void testGetLikesByUser() throws SocializeException, InterruptedException {
		final User user = UserUtils.getCurrentUser(TestUtils.getActivity(this));
		final Entity entityKey = Entity.newInstance("testGetLikesByUser", "testGetLikesByUser");
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		LikeUtils.like(TestUtils.getActivity(this), entityKey, options, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				
				LikeUtils.getLikesByUser(TestUtils.getActivity(LikeUtilsTest.this), user, 0, 100, new LikeListListener() {
					
					@Override
					public void onList(List<Like> items, int totalSize) {
						addResult(items);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
				});
				
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		List<Like> items = getResult(0);
		assertNotNull(items);
		assertTrue(items.size() >= 1);
	}
	
	public void testGetLikesByEntity() throws SocializeException, InterruptedException {
		final Entity entityKey = Entity.newInstance("testGetLikesByEntity", "testGetLikesByEntity");
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		LikeUtils.like(TestUtils.getActivity(this), entityKey, options, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				
				LikeUtils.getLikesByEntity(TestUtils.getActivity(LikeUtilsTest.this), entityKey.getKey(), 0, 100, new LikeListListener() {
					
					@Override
					public void onList(List<Like> items, int totalSize) {
						addResult(items);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
				});
				
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		List<Like> items = getResult(0);
		assertNotNull(items);
		assertTrue(items.size() >= 1);
	}	
	
	public void testGetLikesByApplication() throws SocializeException, InterruptedException {
		final Activity context = TestUtils.getActivity(this);
		final Entity entityKey = Entity.newInstance("testGetLikesByApplication", "testGetLikesByApplication");
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		LikeUtils.like(context, entityKey, options, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				
				LikeUtils.getLikesByApplication(context, 0, 100, new LikeListListener() {
					
					@Override
					public void onList(List<Like> items, int totalSize) {
						addResult(items);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
				});
				
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		List<Like> items = getResult(0);
		assertNotNull(items);
		assertTrue(items.size() >= 1);
	}	
	
	public void testMakeLikeButtonDoLike() throws Throwable {
		
		final Activity context = TestUtils.getActivity(this);
		final Entity entity = Entity.newInstance("testMakeLikeButton", "testMakeLikeButton");
		final Like like = new Like();
		
		like.setId(0L);
		like.setEntity(entity);
		
		SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			public void like (Activity context, Entity entity, LikeOptions likeOptions, LikeAddListener listener, SocialNetwork...shareTo){
				addResult(0, "like");
				listener.onCreate(like);
			}
			
			public void unlike (Activity context, String entityKey, LikeDeleteListener listener) {
				addResult(1, "unlike");
				listener.onDelete();
			}
			
			public void getLike (Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null);
			}
		};
		
		final LikeButtonListener listener = new LikeButtonListener(){
			public void onClick(CompoundButton button) {
				addResult(4, "onClick");
			}
			
			public void onError(CompoundButton button, Exception error) {
			}

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				addResult(5, isChecked);
			}
		};
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				ToggleButton button = new ToggleButton(context);
				LikeUtils.makeLikeButton(context, button, entity, listener);
				button.performClick();
				
				addResult(3, button);
				
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		ToggleButton button = getResult(3);
		String result = getResult(0);
		assertTrue(button.isChecked());
		assertNotNull(result);
		assertEquals("like", result);
		
		assertNotNull(getResult(4));
		assertTrue((Boolean) getResult(5));
	}
	

	public void testMakeLikeButtonDoUnLike() throws Throwable {
		
		final Activity context = TestUtils.getActivity(this);
		final Entity entity = Entity.newInstance("testMakeLikeButton", "testMakeLikeButton");
		final Like like = new Like();
		
		like.setId(0L);
		like.setEntity(entity);
		
		SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			public void like (Activity context, Entity entity, LikeOptions likeOptions, LikeAddListener listener, SocialNetwork...shareTo){
				addResult(0, "like");
				listener.onCreate(like);
			}
			
			public void unlike (Activity context, String entityKey, LikeDeleteListener listener) {
				addResult(1, "unlike");
				listener.onDelete();
			}
			
			public void getLike (Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(like);
			}
		};
		
		final LikeButtonListener listener = new LikeButtonListener(){
			public void onClick(CompoundButton button) {
				addResult(4, "onClick");
			}
			
			public void onError(CompoundButton button, Exception error) {
			}

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				addResult(5, isChecked);
			}
		};
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				ToggleButton button = new ToggleButton(context);
				button.setChecked(true);
				LikeUtils.makeLikeButton(context, button, entity, listener);
				button.performClick();
				
				addResult(3, button);
				
				latch.countDown();
				
			
			}
		});
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		ToggleButton button = getResult(3);
		String result = getResult(1);
		assertFalse(button.isChecked());
		assertNotNull(result);
		assertEquals("unlike", result);
		
		assertNotNull(getResult(4));
		assertFalse((Boolean) getResult(5));
	}	
	
	@UsesMocks ({SocializeService.class, SocializeSession.class, UserSettings.class, LikeSystem.class})
	public void testDoLikeWithShare() {
		
		final Activity context = TestUtils.getActivity(this);
		final Entity entity = Entity.newInstance("testMakeLikeButton", "testMakeLikeButton");
		final Like like = new Like();
		
		like.setId(0L);
		like.setEntity(entity);
		
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final UserSettings settings = AndroidMock.createMock(UserSettings.class);
		
		final SocialNetwork[] network = new SocialNetwork[]{SocialNetwork.TWITTER};
		
		AndroidMock.expect(socialize.getSession()).andReturn(session);
		AndroidMock.expect(session.getUserSettings()).andReturn(settings);
		AndroidMock.expect(settings.setAutoPostPreferences(network)).andReturn(true);
		
		final SocializeUserUtils mockUserUtils = new SocializeUserUtils() {
			@Override
			public void saveUserSettings(Context context, UserSettings userSettings, UserSaveListener listener) {
				addResult(0, userSettings);
			}
		};
		
		final IShareDialogFactory mockShareDialogFactory = new IShareDialogFactory() {
			@Override
			public void preload(Context context) {}
			
			@Override
			public void show(Context context, Entity entity, SocialNetworkListener socialNetworkListener, ShareDialogListener shareDialoglistener, int displayOptions) {
				shareDialoglistener.onContinue(null, true, network);
			}
		};
		
		final SocializeLikeSystem mockLikeSystem = new SocializeLikeSystem(null) {
			public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks) {
				listener.onCreate(like);
			}
		};
		
		final LikeAddListener listener = new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {}
			
			@Override
			public void onCreate(Like result) {
				addResult(2, result);
			}
		};
		
		SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {

			@Override
			protected boolean isDisplayAuthDialog(Context context, SocializeSession session, ActionOptions options, SocialNetwork... networks) {
				return false;
			}

			@Override
			protected boolean isDisplayShareDialog(Context context, ShareableActionOptions options) {
				return true;
			}

			@Override
			protected void doActionShare(Activity context, SocializeAction action, String text, SocialNetworkListener listener, SocialNetwork... networks) {
				addResult(1, action);
			}

			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}
		};
		
		
		mockLikeUtils.setLikeSystem(mockLikeSystem);
		mockLikeUtils.setShareDialogFactory(mockShareDialogFactory);
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
		SocializeAccess.setUserUtilsProxy(mockUserUtils);
		
		AndroidMock.replay(socialize, session, settings);
		
		LikeUtils.like(context, entity, null, listener, network);
		
		AndroidMock.verify(socialize, session, settings);
		
		UserSettings settingsAfter = getResult(0);
		Like likeAfter0 = getResult(1);
		Like likeAfter1 = getResult(2);
		
		assertNotNull(settingsAfter);
		assertNotNull(likeAfter0);
		assertNotNull(likeAfter1);
		
		assertSame(settings, settingsAfter);
		assertSame(like, likeAfter0);
		assertSame(like, likeAfter1);
	}
	
	public void testLikeIsSharedWithAutoPost() {
		final Entity entity = Entity.newInstance("testLikeIsSharedWithAutoPost", "testLikeIsSharedWithAutoPost");
		
		final SocialNetwork[] autoPost = {
			SocialNetwork.TWITTER,
			SocialNetwork.FACEBOOK,
		};
		
		SocializeLikeUtils likeUtils = new SocializeLikeUtils() {
			@Override
			protected void doLikeWithoutShareDialog(Activity context, SocializeSession session, Entity entity, LikeOptions likeOptions, LikeAddListener listener, SocialNetwork... networks) {
				TestUtils.addResult(0, networks);
			}

			@Override
			protected boolean isDisplayAuthDialog(Context context, SocializeSession session, ActionOptions options, SocialNetwork... networks) {
				return false;
			}

			@Override
			protected boolean isDisplayShareDialog(Context context, ShareableActionOptions options) {
				return false;
			}
			
		};
		
		SocializeUserUtils userUtils = new SocializeUserUtils() {
			@Override
			public SocialNetwork[] getAutoPostSocialNetworks(Context context) {
				return autoPost;
			}
		};
		
		SocializeAccess.setUserUtilsProxy(userUtils);
		
		likeUtils.like(TestUtils.getActivity(this), entity, null);
		
		SocialNetwork[] networks = TestUtils.getResult(0);
		
		assertNotNull(networks);
		assertEquals(2, networks.length);
	}
}
