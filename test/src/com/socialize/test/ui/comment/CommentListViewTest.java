package com.socialize.test.ui.comment;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.comment.CommentUtilsProxy;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.comment.CommentAdapter;
import com.socialize.ui.comment.CommentAddButtonListener;
import com.socialize.ui.comment.CommentEditField;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.CommentScrollListener;
import com.socialize.ui.dialog.SimpleDialogFactory;
import com.socialize.ui.header.SocializeHeader;
import com.socialize.ui.view.LoadingListView;

public class CommentListViewTest extends SocializeActivityTest {
	
	Entity entity = Entity.newInstance("foobar", null);
	
	public void testGetCommentScrollListener() {
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			public void getNextSet() {
				addResult(true);
			}
		};
		
		
		view.setLoading(true);
		
		CommentScrollListener commentScrollListener = view.getCommentScrollListener();
		
		assertNotNull(commentScrollListener.getCallback());
		
		commentScrollListener.getCallback().onGetNextSet();
		
		assertTrue(commentScrollListener.getCallback().isLoading());
		
		Boolean nextResult = getNextResult();
		
		assertNotNull(nextResult);
		assertTrue(nextResult);
	}
	
	@UsesMocks ({SocializeService.class, SocializeConfig.class, SocializeSession.class})
	public void testGetCommentAddListener() throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			
			@Override
			public void showError(Context context, Exception message) {
				addResult(1, message);
			}
		};
		
		
		SocializeCommentUtils mockCommentUtils = new SocializeCommentUtils() {
			@Override
			public void addComment(Activity context, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener, SocialNetwork... networks) {
				addResult(0, text);
				latch.countDown();
			}
		};
		
		view.setCommentUtils(mockCommentUtils);
		
		final CommentAddButtonListener commentScrollListener = view.getCommentAddButtonListener();
		
		assertNotNull(commentScrollListener.getCallback());
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				commentScrollListener.getCallback().onComment("foobar", true, false);
				commentScrollListener.getCallback().onError(getContext(), new SocializeException("foobar_error"));
			}
		});
		
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		
		String comment = getResult(0);
		Exception message = getResult(1);
		
		assertNotNull(comment);
		assertNotNull(message);
		
		assertEquals("foobar", comment);
		assertEquals("foobar_error", message.getMessage());
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		Comment.class, 
		ProgressDialog.class,
		SimpleDialogFactory.class,
		CommentAdapter.class,
		List.class,
		View.class,
		SocializeHeader.class,
		LoadingListView.class,
		SocializeSession.class,
		Entity.class})
	public void testPostCommentSuccess() {
		
		final String title = "socialize_comment_dialog";
		final String message = "socialize_please_wait";
		final int totalCount = 69;
		final int startIndex = 0;
		final int endIndex = 10;
		final String commentString = "foobar_comment";
		boolean shareLocation = true;
		
		final Comment comment = AndroidMock.createMock(Comment.class);
		final ProgressDialog dialog = AndroidMock.createMock(ProgressDialog.class, TestUtils.getActivity(this));
		final SimpleDialogFactory<ProgressDialog> progressDialogFactory = AndroidMock.createMock(SimpleDialogFactory.class);
		final CommentAdapter commentAdapter = AndroidMock.createMock(CommentAdapter.class);
		final List<Comment> comments = AndroidMock.createMock(List.class);
		final CommentEditField field = AndroidMock.createMock(CommentEditField.class, getContext());
		final SocializeHeader header = AndroidMock.createMock(SocializeHeader.class, getContext());
		final LoadingListView content = AndroidMock.createMock(LoadingListView.class, getContext());
		final Entity entity = AndroidMock.createMock(Entity.class);

		AndroidMock.expect(progressDialogFactory.show(getContext(), title, message)).andReturn(dialog);
		
		AndroidMock.expect(comment.getText()).andReturn(commentString);
		AndroidMock.expect(commentAdapter.getComments()).andReturn(comments);
		AndroidMock.expect(commentAdapter.getTotalCount()).andReturn(totalCount).anyTimes();

		comments.add(0, comment);
		header.setText((totalCount) + " Comments");

		commentAdapter.setTotalCount((totalCount+1));
		commentAdapter.notifyDataSetChanged();
		content.scrollToTop();
		dialog.dismiss();
		
		AndroidMock.replay(progressDialogFactory);
		AndroidMock.replay(commentAdapter);
		AndroidMock.replay(comment);
		AndroidMock.replay(comments);
		AndroidMock.replay(header);
		AndroidMock.replay(field);
		AndroidMock.replay(content);
		AndroidMock.replay(dialog);
		
		final CommentUtilsProxy mockCommentUtilsProxy = new SocializeCommentUtils() {
			@Override
			public void addComment(Activity context, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener, SocialNetwork... networks) {
				assertEquals(commentString, comment.getText());
				listener.onCreate(comment);
			}
		};
		
		PublicCommentListView view = new PublicCommentListView(TestUtils.getActivity(this)) {

			@Override
			public Comment newComment() {
				return comment;
			}
		};
		
		view.setCommentAdapter(commentAdapter);
		view.setProgressDialogFactory(progressDialogFactory);
		view.setHeader(header);
		view.setCommentEntryField(field);
		view.setContent(content);
		view.setStartIndex(startIndex);
		view.setEndIndex(endIndex);
		view.setEntity(entity);
		view.setCommentUtils(mockCommentUtilsProxy);
		view.getCommentAddButtonListener().onComment(commentString, shareLocation, false);
		
		AndroidMock.verify(progressDialogFactory);
		AndroidMock.verify(commentAdapter);
		AndroidMock.verify(comment);
		AndroidMock.verify(comments);
		AndroidMock.verify(header);
		AndroidMock.verify(field);
		AndroidMock.verify(content);
		AndroidMock.verify(dialog);
		
		// Make sure indexes were updated
		assertEquals(startIndex+1, view.getStartIndex());
		assertEquals(endIndex+1, view.getEndIndex());
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		ProgressDialog.class,
		SimpleDialogFactory.class,
		SocializeException.class})
	public void testPostCommentFail() {
		
		final String title = "socialize_comment_dialog";
		final String message = "socialize_please_wait";
		final String comment = "foobar";
		
		final String entityKey = "foobar_entity_key";
		final String entityName = "foobar_entity_name";
		
		final Entity entity = new Entity();
		entity.setName(entityName);
		entity.setKey(entityKey);
		
		final SocializeException error = AndroidMock.createMock(SocializeException.class);
		final ProgressDialog dialog = AndroidMock.createMock(ProgressDialog.class, getContext());
		final SimpleDialogFactory<ProgressDialog> progressDialogFactory = AndroidMock.createMock(SimpleDialogFactory.class);

		dialog.dismiss();
		
		AndroidMock.expect(progressDialogFactory.show(getContext(), title, message)).andReturn(dialog);

		AndroidMock.replay(progressDialogFactory);
		AndroidMock.replay(dialog);
		AndroidMock.replay(error);
		
		final CommentUtilsProxy mockCommentUtilsProxy = new SocializeCommentUtils() {
			@Override
			public void addComment(Activity context, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener, SocialNetwork... networks) {
				// call onError manually for the test.
				listener.onError(error);
			}
		};
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			public void showError(Context context, Exception message) {
				// Expect this
				addResult(message);
			}
		};
		
		view.setProgressDialogFactory(progressDialogFactory);
		view.setEntity(entity);
		view.setCommentUtils(mockCommentUtilsProxy);
		view.getCommentAddButtonListener().onComment(comment, true, false);
		
		AndroidMock.verify(progressDialogFactory);
		AndroidMock.verify(dialog);
		AndroidMock.verify(error);
		
		Exception result = getNextResult();
		
		assertNotNull(result);
		
	}
	

	@UsesMocks ({CommentAdapter.class})
	public void testGetNextSetIsLast() {
		
		final int totalCount = 69;
		final CommentAdapter commentAdapter = AndroidMock.createMock(CommentAdapter.class);
		
		
		commentAdapter.notifyDataSetChanged();
		commentAdapter.setLast(true);
		
		AndroidMock.expect(commentAdapter.getTotalCount()).andReturn(totalCount).anyTimes();
		AndroidMock.replay(commentAdapter);
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			protected SocializeService getSocialize() {
				fail(); // Shouldn't be called
				return null;
			}
		};
		
		// Orchestrate the completion state
		
		final int endIndex = 70;
		final int startIndex = 60;
		final int grabLength = 10;
		
		view.setCommentAdapter(commentAdapter);
		view.setStartIndex(startIndex);
		view.setEndIndex(endIndex);
		view.setDefaultGrabLength(grabLength);
		
		
		view.getNextSet();
		
		AndroidMock.verify(commentAdapter);
		
		assertEquals(totalCount, view.getEndIndex());
		assertEquals(startIndex+grabLength, view.getStartIndex());
		assertFalse(view.isLoading());
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		CommentAdapter.class,
		List.class,
		ListResult.class})
	public void testGetNextSet() {
		
		final int startIndex = 0;
		final int endIndex = 70;
		
		final CommentAdapter commentAdapter = AndroidMock.createMock(CommentAdapter.class);
		final List<Comment> comments = AndroidMock.createMock(List.class);
		final List<Comment> listResultComments = AndroidMock.createMock(List.class);
		
		final ListResult<Comment> entities = AndroidMock.createMock(ListResult.class);

		AndroidMock.expect(commentAdapter.getComments()).andReturn(comments);
		AndroidMock.expect(entities.getItems()).andReturn(listResultComments);
		AndroidMock.expect(comments.addAll(listResultComments)).andReturn(true);
		AndroidMock.expect(commentAdapter.getTotalCount()).andReturn(69).anyTimes();
		
		
		commentAdapter.setComments(comments);
		commentAdapter.notifyDataSetChanged();
		
		AndroidMock.replay(entities);
		AndroidMock.replay(commentAdapter);
		AndroidMock.replay(comments);

		final CommentUtilsProxy mockCommentUtilsProxy = new SocializeCommentUtils() {
			@Override
			public void getCommentsByEntity(Activity context, String entityKey, int start, int end, CommentListListener listener) {
				listener.onList(entities);
			}
		};
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			protected void preLoadImages(List<Comment> comments) {
				// Do nothing.
			}
		};
		
		view.setCommentAdapter(commentAdapter);
		view.setStartIndex(startIndex);
		view.setEndIndex(endIndex);
		view.setDefaultGrabLength(10);
		view.setCommentUtils(mockCommentUtilsProxy);
		view.getNextSet();
		
		AndroidMock.verify(commentAdapter);
		AndroidMock.verify(comments);
		AndroidMock.verify(entities);
		
		assertFalse(view.isLoading());
	}
	
	

	@SuppressWarnings("unchecked")
	@UsesMocks ({
		CommentAdapter.class,
		List.class,
		SocializeHeader.class,
		LoadingListView.class,
		ListResult.class})
	public void testDoListCommentsSuccessEmptyCommentsWithoutUpdate() {
		
		final int totalCount = 69;
		final int startIndex = 0;
		final int endIndex = 70;
		
		final CommentAdapter commentAdapter = AndroidMock.createMock(CommentAdapter.class);
		final List<Comment> comments = AndroidMock.createMock(List.class);
		final List<Comment> listResultComments = AndroidMock.createMock(List.class);
		
		final SocializeHeader header = AndroidMock.createMock(SocializeHeader.class, getContext());
		final LoadingListView content = AndroidMock.createMock(LoadingListView.class, getContext());
		final ListResult<Comment> entities = AndroidMock.createMock(ListResult.class);
		
		AndroidMock.expect(commentAdapter.getComments()).andReturn(comments);
		AndroidMock.expect(comments.size()).andReturn(0); // Empty comments
		AndroidMock.expect(entities.getItems()).andReturn(listResultComments);
		AndroidMock.expect(entities.getTotalCount()).andReturn(totalCount).anyTimes();
		AndroidMock.expect(commentAdapter.getTotalCount()).andReturn(totalCount).anyTimes();

		commentAdapter.setComments(listResultComments);
		commentAdapter.setLast(true);
		commentAdapter.setTotalCount(totalCount);
		header.setText(totalCount + " Comments");
		content.showList();
		
		AndroidMock.replay(entities);
		AndroidMock.replay(commentAdapter);
		AndroidMock.replay(comments);
		AndroidMock.replay(header);
		AndroidMock.replay(content);
		
		final CommentUtilsProxy mockCommentUtilsProxy = new SocializeCommentUtils() {
			@Override
			public void getCommentsByEntity(Activity context, String entityKey, int start, int end, CommentListListener listener) {
				listener.onList(entities);
			}
		};
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			protected void preLoadImages(List<Comment> comments) {
				// Do nothing.
			}
		};
		
		view.setCommentAdapter(commentAdapter);
		view.setHeader(header);
		view.setContent(content);
		view.setStartIndex(startIndex);
		view.setDefaultGrabLength(endIndex);
		view.setCommentUtils(mockCommentUtilsProxy);
		view.doListComments(false);
		
		assertEquals(totalCount, view.getTotalCount());
		assertFalse(view.isLoading());
		
		AndroidMock.verify(commentAdapter);
		AndroidMock.verify(comments);
		AndroidMock.verify(header);
		AndroidMock.verify(content);
		AndroidMock.verify(entities);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		SocializeException.class,
		CommentAdapter.class,
		List.class,
		CommentUtilsProxy.class})
	public void testDoListCommentsFailEmptyCommentsWithoutUpdate() {
		
		final SocializeException error = AndroidMock.createMock(SocializeException.class);
		final CommentAdapter commentAdapter = AndroidMock.createMock(CommentAdapter.class);
		final List<Comment> comments = AndroidMock.createMock(List.class);
		final LoadingListView content = AndroidMock.createMock(LoadingListView.class, getContext());

		content.showList();
		
		AndroidMock.expect(commentAdapter.getComments()).andReturn(comments);
		AndroidMock.expect(comments.size()).andReturn(0); // Empty comments
		
		AndroidMock.replay(commentAdapter);
		AndroidMock.replay(comments);
		AndroidMock.replay(content);
		AndroidMock.replay(error);
		
		
		final CommentUtilsProxy mockCommentUtilsProxy = new SocializeCommentUtils() {
			@Override
			public void getCommentsByEntity(Activity context, String entityKey, int start, int end, CommentListListener listener) {
				listener.onError(error);
			}
		};
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			public void showError(Context context, Exception error) {
				addResult(error);
			}
		};
		
		view.setCommentAdapter(commentAdapter);
		view.setContent(content);
		view.setCommentUtils(mockCommentUtilsProxy);
		view.doListComments(false);
		
		AndroidMock.verify(commentAdapter);
		AndroidMock.verify(comments);
		AndroidMock.verify(error);
		AndroidMock.verify(content);
		
		Exception result = getNextResult();
		
		assertNotNull(result);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		CommentAdapter.class,
		List.class,
		LoadingListView.class})
	public void testDoListCommentsSuccessPopulatedCommentsWithoutUpdate() {
		
		final CommentAdapter commentAdapter = AndroidMock.createMock(CommentAdapter.class);
		final List<Comment> comments = AndroidMock.createMock(List.class);
		final LoadingListView content = AndroidMock.createMock(LoadingListView.class, getContext());
		final SocializeHeader header = AndroidMock.createMock(SocializeHeader.class, getContext());
		
		AndroidMock.expect(commentAdapter.getComments()).andReturn(comments);
		AndroidMock.expect(comments.size()).andReturn(10); // Populated comments

		AndroidMock.expect(commentAdapter.getTotalCount()).andReturn(10); // Populated comments
		
		commentAdapter.notifyDataSetChanged();
		content.showList();
		
		header.setText(10 + " Comments");
		
		AndroidMock.replay(commentAdapter);
		AndroidMock.replay(comments);
		AndroidMock.replay(content);
		AndroidMock.replay(header);
		
		PublicCommentListView view = new PublicCommentListView(getContext());
		
		view.setCommentAdapter(commentAdapter);
		view.setContent(content);
		view.setHeader(header);
		
		view.doListComments(false);
		
		AndroidMock.verify(commentAdapter);
		AndroidMock.verify(comments);
		AndroidMock.verify(content);
		AndroidMock.verify(header);
		
		assertFalse(view.isLoading());
	}
	

	@UsesMocks ({SocializeService.class})
	public void testOnViewLoadSuccess() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		
		AndroidMock.expect(socialize.isAuthenticated()).andReturn(true);
		AndroidMock.replay(socialize);
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}

			@Override
			public void doListComments(boolean update) {
				addResult(update);
			}
		};
		
		view.onViewRendered(10, 10);
		
		Boolean update = getNextResult();
		
		assertNotNull(update);
		assertFalse(update);
		
		AndroidMock.verify(socialize);
	}
	
	@UsesMocks ({
		SocializeService.class,
		LoadingListView.class})
	public void testOnViewLoadFail() {
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final LoadingListView content = AndroidMock.createMock(LoadingListView.class, getContext());
		
		AndroidMock.expect(socialize.isAuthenticated()).andReturn(false);
		content.showList();
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(content);
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
			@Override
			protected SocializeService getSocialize() {
				return socialize;
			}

			@Override
			public void doListComments(boolean update) {
				fail();
			}

			@Override
			public void showError(Context context, Exception error) {
				addResult(error);
			}
			
			
		};
		
		view.setContent(content);
		view.onViewRendered(10,10);
		
		Exception error = getNextResult();
		
		assertNotNull(error);
		assertEquals("Socialize not authenticated", error.getMessage());
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(content);
	}
	
	class PublicCommentListView extends CommentListView {

		public PublicCommentListView(Context context) {
			super(context);
			setEntity(entity);
		}
		
		@Override
		public CommentScrollListener getCommentScrollListener() {
			return super.getCommentScrollListener();
		}

		@Override
		public CommentAddButtonListener getCommentAddButtonListener() {
			return super.getCommentAddButtonListener();
		}

		@Override
		public void setLoading(boolean loading) {
			super.setLoading(loading);
		}

		@Override
		protected void setCommentEntryField(CommentEditField field) {
			super.setCommentEntryField(field);
		}

		@Override
		public void setHeader(SocializeHeader header) {
			super.setHeader(header);
		}

		@Override
		public void setContent(LoadingListView content) {
			super.setContent(content);
		}

		@Override
		public void setStartIndex(int startIndex) {
			super.setStartIndex(startIndex);
		}

		@Override
		public void setEndIndex(int endIndex) {
			super.setEndIndex(endIndex);
		}

		@Override
		public void getNextSet() {
			super.getNextSet();
		}

		@Override
		public void onAttachedToWindow() {
			super.onAttachedToWindow();
		}

		@Override
		public void onViewUpdate() {
			super.onViewUpdate();
		}

		@Override
		public void onViewLoad() {
			super.onViewLoad();
		}

		@Override
		public void onViewRendered(int width, int height) {
			super.onViewRendered(width, height);
		}

		@Override
		public CommentOptions newShareOptions() {
			return super.newShareOptions();
		}
		
		@Override
		public Comment newComment() {
			return super.newComment();
		}

		@Override
		public void doNotificationStatusSave() {
			super.doNotificationStatusSave();
		}

		@Override
		public void doNotificationStatusLoad() {
			super.doNotificationStatusLoad();
		}

		@Override
		public RelativeLayout getLayoutAnchor() {
			return super.getLayoutAnchor();
		}

		@Override
		public ViewGroup getSliderAnchor() {
			return super.getSliderAnchor();
		}
	}
}
