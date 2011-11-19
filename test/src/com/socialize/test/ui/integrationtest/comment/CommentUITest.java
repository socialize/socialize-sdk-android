package com.socialize.test.ui.integrationtest.comment;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.socialize.test.ui.integrationtest.SocializeUIRobotiumTest;
import com.socialize.ui.comment.CommentActivity;

public class CommentUITest extends SocializeUIRobotiumTest {
	
	@Override
	protected void startWithFacebook(boolean sso) {
		super.startWithFacebook(sso);
		showComments();
		robotium.waitForActivity("CommentActivity", 5000);
		robotium.waitForView(ListView.class, 1, 5000);
		sleep(2000);
	}

	@Override
	protected void startWithoutFacebook() {
		super.startWithoutFacebook();
		showComments();
		robotium.waitForActivity("CommentActivity", 5000);
		robotium.waitForView(ListView.class, 1, 5000);
		sleep(2000);
	}
/*
	public void testCommentAddWithoutFacebook() {
		
		final String txtComment = "UI Integration Test Comment";

		startWithoutFacebook();
		
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(CommentActivity.LIST_VIEW_ID);
		
		int count =  comments.getCount();
		
		robotium.enterText(0, txtComment);
		robotium.clickOnImageButton(0);
		robotium.waitForDialogToClose(5000);
	
		assertNotNull(comments);
		assertEquals( count+1, comments.getCount());
		
		Comment item = (Comment) comments.getItemAtPosition(0);
		
		assertNotNull(item);
		
		String comment = item.getText();
		assertEquals(txtComment, comment);
		
	}
	
	public void testAddAnonCommentWithFacebookAndSSO() {
		
		final String txtComment = "UI Integration Test Comment";

		startWithFacebook(true);
		
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(CommentActivity.LIST_VIEW_ID);
		
		int count =  comments.getCount();
		
		robotium.enterText(0, txtComment);
		robotium.clickOnImageButton(0);
		
		assertTrue(robotium.waitForView(AuthRequestDialogView.class, 1,  1000));
		robotium.goBack();
		assertTrue(robotium.waitForView(AuthConfirmDialogView.class, 1,  1000));
		
		AuthConfirmDialogView confirm = findView(AuthConfirmDialogView.class);

		assertNotNull(confirm);
		
		robotium.clickOnView(confirm.getSocializeSkipAuthButton());
		robotium.waitForDialogToClose(5000);
		
		assertNotNull(comments);
		assertEquals( count+1, comments.getCount());
		
		Comment item = (Comment) comments.getItemAtPosition(0);
		
		assertNotNull(item);
		
		String comment = item.getText();
		assertEquals(txtComment, comment);
	}
	
	public void testAddCommentWithFacebookAndSSO() {
		
		final String txtComment = "UI Integration Test Comment FB Auth";

		startWithFacebook(true);
		
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(CommentActivity.LIST_VIEW_ID);
		
		int count =  comments.getCount();
		
		robotium.enterText(0, txtComment);
		robotium.clickOnImageButton(0);
		
		robotium.waitForView(AuthRequestDialogView.class, 1, 5000);
		
		AuthRequestDialogView confirm = findView(AuthRequestDialogView.class);

		assertNotNull(confirm);
		
		robotium.clickOnView(confirm.getFacebookSignInButton());
		robotium.waitForDialogToClose(5000);
		
		assertNotNull(comments);
		
		sleep(2000);
		
		assertEquals( count+1, comments.getCount());
		
		Comment item = (Comment) comments.getItemAtPosition(0);
		
		assertNotNull(item);
		
		String comment = item.getText();
		assertEquals(txtComment, comment);
	}
	
*/
	public void testCommentListAndView() {
		
		startWithoutFacebook();
		
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(CommentActivity.LIST_VIEW_ID);
		
		assertNotNull(comments);
		assertTrue("Unexepected number of comments.  Expected >= 10 but found " +
				comments.getCount() +
				"", comments.getCount() >= 10);
		
		// Click on the first comment in list. 
		robotium.clickInList(0);
		
		robotium.waitForActivity("CommentDetailActivity");
		
		// Make sure we have user name, comment, image and location
		TextView userDisplayName = robotium.getText(0);
		TextView comment = robotium.getText(1);
		TextView date_location = robotium.getText(2);
		ImageView userProfilePic = robotium.getImage(0);
		
		assertNotNull(userDisplayName);
		assertNotNull(comment);
		assertNotNull(userProfilePic);
		assertNotNull(date_location);
		
		String name = userDisplayName.getText().toString();
		String commentText = userDisplayName.getText().toString();
		String location = date_location.getText().toString();
		
		Drawable drawable = userProfilePic.getDrawable();
		
		assertNotNull(name);
		assertTrue(name.trim().length() > 0);
		
		assertNotNull(commentText);
		assertTrue(commentText.trim().length() > 0);
		
		assertNotNull(location);
		assertTrue(location.trim().length() > 0);
		
		assertNotNull(drawable);
	}
}
