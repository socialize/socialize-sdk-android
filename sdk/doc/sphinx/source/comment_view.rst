.. raw:: html

	<link rel="stylesheet" href="static/css/gist.css" type="text/css" />
	
==================
Socialize Comments
==================

v0.4.0 of the Socialize SDK introduced the "Comment View" which provides the creation and viewing 
of comments associated with an entity (URL).  

.. image:: images/comment_view2.png	
.. image:: images/comment_view3.png
.. image:: images/comment_view4.png		

Diplaying the Comment View
~~~~~~~~~~~~~~~~~~~~~~~~~~

When you want to launch the comment view, simply call **showCommentView** from the SocializeUI instance:

.. raw:: html

	<script src="https://gist.github.com/1132979.js?file=show_comment_view.java"></script>

Here's an example of calling it on a button click:

.. raw:: html

	<script src="https://gist.github.com/1132979.js?file=show_comment_view_onclick.java"></script>

A Complete Example
~~~~~~~~~~~~~~~~~~

Here's a complete example in an Activity:

.. raw:: html

	<script src="https://gist.github.com/1132979.js?file=comment_view_activity_fb.java"></script>

