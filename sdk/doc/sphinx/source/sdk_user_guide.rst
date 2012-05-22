.. include:: header.inc
	
==============================
Socialize SDK (Advanced) Guide
==============================

Introduction
------------
The Socialize SDK provides a simple set of classes and methods built upon the `Socialize REST API <http://www.getsocialize.com/docs/v1/>`_

App developers can elect to use either the pre-defined user interface controls provided in the Socialize UI 
framework, or "roll their own" using direct SDK calls.

ALL calls to the Socialize SDK are *asynchronous*, meaning that your application will not "block" while 
waiting for a response from the Socialize server.

You are notified of the outcome of calls to the Socialize service via a *SocializeListener* 
passed into each call to the Socialize SDK.

Initializing Socialize
----------------------
The Socialize SDK should be initialized in the **onCreate()** method of your Activity.


.. include:: ../../../../demo/src/com/socialize/demo/snippets/CommentSnippets.java
	:language: java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0
	:emphasize-lines: 9-10


.. literalinclude:: snippets/socialize_init.txt
   :language: java
   :linenos:

The standard init method is *synchronous*, meaning that the main thread will block until Socialize is initialized.
If you want an *asynchronous* initialization, this can be done using the **initAsync()** method:

.. literalinclude:: snippets/socialize_init_async.txt
   :language: java
   :linenos:

.. note:: If using asynchronous init ensure that there are no operations on the Socialize instance before or while it is initializing!

Authentication
--------------
Every call against the Socialize SDK MUST be authenticated.  

On the first successful call to "authenticate" the credentials are automatically cached in the 
application so subsequent calls are much faster.

Authenticating with Facebook
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Although not required, we stongly recommend authenticating with 3rd parties (e.g. Facebook) as this provides a better user experience 
and ensures that user profiles and IDs are retained across app sessions and installs. 

If you don't want to use Facebook for authentication, you can always just use :ref:`auth_anon`. 

**You must already have a Facebook application** to enable Facebook authentication in Socialize.

A Facebook application is nothing more than an account on Facebook which links your Android application to 
a Facebook account and is required to "authorize" your Android app to access a user's Facebook profile.

If you already have a Facebook app, simply specify the ID of your Facebook app in the call 
to authenticate:

.. _fb_snippet:

Facebook Authentication Code Snippet
====================================

.. literalinclude:: snippets/auth_fb.txt
   :language: java
   :linenos:

If you have specified your Socialize consumer key, secret AND your Facebook App ID in **assets/socialize.properties**

.. literalinclude:: snippets/auth_fb_from_props.txt
   :language: java
   :linenos:

If you **do not** already have a Facebook app refer to :doc:`facebook` for more information.

Authenticating with Twitter
~~~~~~~~~~~~~~~~~~~~~~~~~~~
Although not required, we stongly recommend authenticating with 3rd parties (e.g. Twitter) as this provides a better user experience 
and ensures that user profiles and IDs are retained across app sessions and installs. 

If you don't want to use Twitter for authentication, you can always just use :ref:`auth_anon`. 

**You must already have a Twitter application** to enable Twitter authentication in Socialize.

A Twitter application is nothing more than an account on Twitter which links your Android application to 
a Twitter account and is required to "authorize" your Android app to access a user's Twitter feed.

If you already have a Twitter app, simply specify the consumer key and secret of your Twitter app in the call 
to authenticate:

.. _tw_snippet:

Twitter Authentication Code Snippet
====================================

.. literalinclude:: snippets/auth_tw.txt
   :language: java
   :linenos:

If you have specified your Socialize consumer key, secret AND your Twitter consumer key and secret in **assets/socialize.properties**

.. literalinclude:: snippets/auth_tw_from_props.txt
   :language: java
   :linenos:

If you **do not** already have a Twitter app refer to :doc:`twitter` for more information.
	
.. _auth_anon:

Anonymous Authentication
~~~~~~~~~~~~~~~~~~~~~~~~
We recommend authenticating with 3rd parties (e.g. Facebook) as this provides a better user experience 
and ensures that user profiles and IDs are retained across app sessions and installs, however if you just 
want anonymous authentication, simply call the **authenticate** method:

.. literalinclude:: snippets/auth_anon.txt
   :language: java
   :linenos:

If you have specified your Socialize consumer key, secret in **assets/socialize.properties**

.. literalinclude:: snippets/auth_anon_from_props.txt
   :language: java
   :linenos:

.. _entities:

Entities
--------
An entity is a single item of content in your app

Throughout the documentation and the code snippets we refer to an "entity".  This is simply a 
generic term for something that can be view, shared, liked or commented on.  Generally this will
correspond to a single item of content in your app.

Entities in Socialize MUST be associated with s unique key.  It is recommended that where possible an 
HTTP URL be used (i.e. one that corresponds to an active web page).

Creating an Entity
~~~~~~~~~~~~~~~~~~
An entity consists of a **key** and a **name**.  The name should be descriptive and help you identify the 
entity when viewing reports on the Socialize dashboard.

Creating an entity explicitly in this manner is **optional but recommended**.  If you simply post a 
comment,view,share or like against a key that does not currently exist, it will be automatically created 
for you.

To create an entity, simply call the **addEntity** method:

.. literalinclude:: snippets/create_entity.txt
   :language: java
   :linenos:

Retrieving Entity data
~~~~~~~~~~~~~~~~~~~~~~
An existing entity can be retrieved via the **getEntity** method.  Entities obtained in this way will also 
provide aggregate data on comments, likes, shares and views.  Refer to the `Entity object structure in the API Docs <http://www.getsocialize.com/docs/v1/#entity-object>`_.
for more detail on these aggregate values.

.. literalinclude:: snippets/get_entity.txt
   :language: java
   :linenos:

View
----
A 'view' is simply an event that records when a user views an entity.  Views are reported on the Socialize 
dashboard and provide an excellent way for you to determine which content items in your app are getting the 
most interest.

Creating a 'View'
~~~~~~~~~~~~~~~~~
To create a view, simply call the **view** method:

.. literalinclude:: snippets/create_view.txt
   :language: java
   :linenos:

Like
----
A 'like' represents a user's vote for an entity.  Likes are a way for you to determine which content items 
in your app are the most popular, and what is of most interest to your users.

Creating a 'Like'
~~~~~~~~~~~~~~~~~
To create a like, simply call the **like** method:

.. literalinclude:: snippets/create_like.txt
   :language: java
   :linenos:
	
Removing a 'Like'
~~~~~~~~~~~~~~~~~
Removing a like (i.e. an 'unlike') is done via the **unlike** method.  In order to remove a like, you will 
need the ID of the like.  This is returned from the initial call to **like**

.. literalinclude:: snippets/delete_like.txt

Listing 'Likes' for a User
~~~~~~~~~~~~~~~~~~~~~~~~~~
To obtain a list of all entities 'liked' by a single user, use the **listLikesByUser** method:

.. literalinclude:: snippets/list_likes.txt
   :language: java
   :linenos:

Comment
-------
Comments are a great way to build engagement in your app, and users love making comments!

Creating a Comment
~~~~~~~~~~~~~~~~~~
To create a comment on an entity, use the **addComment** method:

.. literalinclude:: snippets/create_comment.txt
   :language: java
   :linenos:

Retrieving a single Comment
~~~~~~~~~~~~~~~~~~~~~~~~~~~
If you want to retrieve a single comment you can use the **getCommentById** method.  You will need the ID 
of the comment which was returned from the inital call to **addComment**:

.. literalinclude:: snippets/get_comment.txt
   :language: java
   :linenos:

Listing Comments
~~~~~~~~~~~~~~~~
To list all comments for an entity use the **listCommentsByEntity** method.  This will return a **maximum of 100 comments**

.. literalinclude:: snippets/list_comments.txt
   :language: java
   :linenos:

Comments with pagination
~~~~~~~~~~~~~~~~~~~~~~~~
The **recommended** approach is to use pagination when listing comments:

.. literalinclude:: snippets/list_comments_paged.txt
   :language: java
   :linenos:
	
Listing Comments for a User
~~~~~~~~~~~~~~~~~~~~~~~~~~~
To obtain a list of all comments made by a single user, use the **listCommentsByUser** method:

.. literalinclude:: snippets/list_comments_user.txt
   :language: java
   :linenos:
	
Share
-----
Users love to share content, especially to their favorite social network.  Socialize makes sharing easy with a set 
of very simple SDK calls.

Sharing to Facebook
~~~~~~~~~~~~~~~~~~~
The example below shows sharing an entity to Facebook.

.. literalinclude:: snippets/create_share.txt
   :language: java
   :linenos:

Actually almost any social action performed with Socialize can be shared to facebook. 
Check out the :ref:`propagate_fb` section for more examples

User Activity
-------------
Comments, Shares and Likes made by a single user can all be retrieved in a single call using the SDK.

Listing Activity for a User
~~~~~~~~~~~~~~~~~~~~~~~~~~~
To obtain a list of all activity conducted by a single user, use the **listActivityByUser** method:

.. literalinclude:: snippets/list_activity.txt
   :language: java
   :linenos:
	
Listing Activity for a User with Pagination
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
As with all list operations the user activity list supports pagination:

.. literalinclude:: snippets/list_activity_paged.txt
   :language: java
   :linenos:
	
.. include:: footer.inc			
