package com.socialize.test.comment.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.socialize.SocializeService;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.comment.CommentUtilsProxy;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.comment.CommentAdapter;
import com.socialize.ui.comment.CommentAddButtonListener;
import com.socialize.ui.comment.CommentEditField;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.CommentScrollListener;
import com.socialize.ui.dialog.SimpleDialogFactory;
import com.socialize.ui.header.SocializeHeader;
import com.socialize.ui.view.LoadingListView;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
	public void testPostCommentSuccess() {
		
		final String title = "socialize_comment_dialog";
		final String message = "socialize_please_wait";
		final int totalCount = 69;
		final int startIndex = 0;
		final int endIndex = 10;
		final String commentString = "foobar_comment";
		boolean shareLocation = true;
		
		final Comment comment = Mockito.mock(Comment.class);
		final ProgressDialog dialog = Mockito.mock(ProgressDialog.class);
		final SimpleDialogFactory<ProgressDialog> progressDialogFactory = Mockito.mock(SimpleDialogFactory.class);
		final CommentAdapter commentAdapter = Mockito.mock(CommentAdapter.class);
		final List<Comment> comments = Mockito.mock(List.class);
		final CommentEditField field = Mockito.mock(CommentEditField.class);
		final SocializeHeader header = Mockito.mock(SocializeHeader.class);
		final LoadingListView content = Mockito.mock(LoadingListView.class);
		final Entity entity = Mockito.mock(Entity.class);

		Mockito.when(progressDialogFactory.show(getContext(), title, message)).thenReturn(dialog);
		Mockito.when(comment.getText()).thenReturn(commentString);
		Mockito.when(commentAdapter.getComments()).thenReturn(comments);
		Mockito.when(commentAdapter.getTotalCount()).thenReturn(totalCount);

		final CommentUtilsProxy mockCommentUtilsProxy = new SocializeCommentUtils() {
			@Override
			public void addComment(Activity context, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener, SocialNetwork... networks) {
				assertEquals(commentString, comment.getText());
				listener.onCreate(comment);
			}
		};
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {

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

        Mockito.verify(comments).add(0, comment);
        Mockito.verify(header).setText((totalCount) + " Comments");
        Mockito.verify(commentAdapter).setTotalCount((totalCount+1));
        Mockito.verify(commentAdapter).notifyDataSetChanged();
        Mockito.verify(content).scrollToTop();
        Mockito.verify(dialog).dismiss();

		// Make sure indexes were updated
		assertEquals(1, view.getStartIndex());
		assertEquals(endIndex+1, view.getEndIndex());
	}
	
	@SuppressWarnings("unchecked")
	public void testPostCommentFail() {
		
		final String title = "socialize_comment_dialog";
		final String message = "socialize_please_wait";
		final String comment = "foobar";
		
		final String entityKey = "foobar_entity_key";
		final String entityName = "foobar_entity_name";
		
		final Entity entity = new Entity();
		entity.setName(entityName);
		entity.setKey(entityKey);
		
		final SocializeException error = Mockito.mock(SocializeException.class);
		final ProgressDialog dialog = Mockito.mock(ProgressDialog.class);
		final SimpleDialogFactory<ProgressDialog> progressDialogFactory = Mockito.mock(SimpleDialogFactory.class);

		Mockito.when(progressDialogFactory.show(getContext(), title, message)).thenReturn(dialog);

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

        Mockito.verify(dialog).dismiss();

		Exception result = getNextResult();
		
		assertNotNull(result);
		
	}

	public void testGetNextSetIsLast() {
		
		final int totalCount = 69;
		final CommentAdapter commentAdapter = Mockito.mock(CommentAdapter.class);

		Mockito.when(commentAdapter.getTotalCount()).thenReturn(totalCount);

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

        Mockito.verify(commentAdapter).notifyDataSetChanged();
        Mockito.verify(commentAdapter).setLast(true);

		assertEquals(totalCount, view.getEndIndex());
		assertEquals(startIndex+grabLength, view.getStartIndex());
		assertFalse(view.isLoading());
	}
	
	@SuppressWarnings("unchecked")
	public void testGetNextSet() {
		
		final int startIndex = 0;
		final int endIndex = 70;
		
		final CommentAdapter commentAdapter = Mockito.mock(CommentAdapter.class);
		final List<Comment> comments = Mockito.mock(List.class);
		final List<Comment> listResultComments = Mockito.mock(List.class);
		
		final ListResult<Comment> entities = Mockito.mock(ListResult.class);

		Mockito.when(commentAdapter.getComments()).thenReturn(comments);
		Mockito.when(entities.getItems()).thenReturn(listResultComments);
		Mockito.when(comments.addAll(listResultComments)).thenReturn(true);
		Mockito.when(commentAdapter.getTotalCount()).thenReturn(79);
		
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

        Mockito.verify(commentAdapter).setComments(comments);
        Mockito.verify(commentAdapter).notifyDataSetChanged();
		
		assertFalse(view.isLoading());
	}
	
	@SuppressWarnings("unchecked")
	public void testDoListCommentsSuccessEmptyCommentsWithoutUpdate() {
		
		final int totalCount = 69;
		final int startIndex = 0;
		final int endIndex = 70;
		
		final CommentAdapter commentAdapter = Mockito.mock(CommentAdapter.class);
		final List<Comment> comments = Mockito.mock(List.class);
		final List<Comment> listResultComments = Mockito.mock(List.class);
		
		final SocializeHeader header = Mockito.mock(SocializeHeader.class);
		final LoadingListView content = Mockito.mock(LoadingListView.class);
		final ListResult<Comment> entities = Mockito.mock(ListResult.class);
		
		Mockito.when(commentAdapter.getComments()).thenReturn(comments);
		Mockito.when(comments.size()).thenReturn(0); // Empty comments
		Mockito.when(entities.getItems()).thenReturn(listResultComments);
		Mockito.when(entities.getTotalCount()).thenReturn(totalCount);
		Mockito.when(commentAdapter.getTotalCount()).thenReturn(totalCount);

		final CommentUtilsProxy mockCommentUtilsProxy = new SocializeCommentUtils() {
			@Override
			public void getCommentsByEntity(Activity context, String entityKey, int start, int end, CommentListListener listener) {
				listener.onList(entities);
			}
		};
		
		PublicCommentListView view = new PublicCommentListView(getContext()) {
//			@Override
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


        Mockito.verify(content).scrollToTop();
        Mockito.verify(commentAdapter).setComments(listResultComments);
        Mockito.verify(commentAdapter).setLast(true);
        Mockito.verify(commentAdapter).setTotalCount(totalCount);
        Mockito.verify(commentAdapter).notifyDataSetChanged();
        Mockito.verify(header).setText(totalCount + " Comments");
        Mockito.verify(content).showList();
	}
	
	@SuppressWarnings("unchecked")
	public void testDoListCommentsFailEmptyCommentsWithoutUpdate() {
		
		final SocializeException error = Mockito.mock(SocializeException.class);
		final CommentAdapter commentAdapter = Mockito.mock(CommentAdapter.class);
		final List<Comment> comments = Mockito.mock(List.class);
		final LoadingListView content = Mockito.mock(LoadingListView.class);

		Mockito.when(commentAdapter.getComments()).thenReturn(comments);
		Mockito.when(comments.size()).thenReturn(0); // Empty comments
		
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

        Mockito.verify(content).showList();

		Exception result = getNextResult();
		
		assertNotNull(result);
	}
	
	@SuppressWarnings("unchecked")
	public void testDoListCommentsSuccessPopulatedCommentsWithoutUpdate() {
		
		final CommentAdapter commentAdapter = Mockito.mock(CommentAdapter.class);
		final List<Comment> comments = Mockito.mock(List.class);
		final LoadingListView content = Mockito.mock(LoadingListView.class);
		final SocializeHeader header = Mockito.mock(SocializeHeader.class);
		
		Mockito.when(commentAdapter.getComments()).thenReturn(comments);
		Mockito.when(comments.size()).thenReturn(10); // Populated comments
		Mockito.when(commentAdapter.getTotalCount()).thenReturn(10); // Populated comments

		PublicCommentListView view = new PublicCommentListView(getContext());
		
		view.setCommentAdapter(commentAdapter);
		view.setContent(content);
		view.setHeader(header);
		
		view.doListComments(false);

        Mockito.verify(commentAdapter).notifyDataSetChanged();
        Mockito.verify(content).showList();
        Mockito.verify(header).setText(10 + " Comments");
		
		assertFalse(view.isLoading());
	}
	
	public void testOnViewLoadSuccess() {
		final SocializeService socialize = Mockito.mock(SocializeService.class);
		
		Mockito.when(socialize.isAuthenticated()).thenReturn(true);

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
	}
	
	public void testOnViewLoadFail() {
		final SocializeService socialize = Mockito.mock(SocializeService.class);
		final LoadingListView content = Mockito.mock(LoadingListView.class);
		
		Mockito.when(socialize.isAuthenticated()).thenReturn(false);

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
		
        Mockito.verify(content).showList();
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
