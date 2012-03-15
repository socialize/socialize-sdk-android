.. include:: header.inc
	
==================
Socialize Comments
==================

v0.4.0 of the Socialize SDK introduced the "Comment View" which provides the creation and viewing 
of comments associated with an entity (URL).  

.. note:: If you are using the Socialize Action Bar you do not need to use the Comment View directly, it is included in the Action Bar

.. image:: images/comment_list.png	
.. image:: images/activity.png
.. image:: images/profile.png		

Displaying the Comment View
~~~~~~~~~~~~~~~~~~~~~~~~~~~

When you want to launch the comment view, simply call **showCommentView** from the SocializeUI instance:

.. include:: snippets/show_comment.txt

Here's an example of calling it on a button click:

.. include:: snippets/show_comment_onclick.txt

A Complete Example
~~~~~~~~~~~~~~~~~~

Here's a complete example in an Activity:

.. include:: snippets/show_comment_full.txt

.. include:: footer.inc	