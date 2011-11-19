.. raw:: html

	<link rel="stylesheet" href="static/css/gist.css" type="text/css" />
	
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
The Socialize SDK should be initialized in the **onCreate()** method of your Activity, 
and destroyed in the **onDestroy()** method

.. raw:: html

	<script src="https://gist.github.com/1132979.js?file=setup.java"></script>

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

.. raw:: html

	<script src="https://gist.github.com/1132956.js?file=authenticate_fb.java"></script>

If you **do not** already have a Facebook app refer to :doc:`facebook` for more information.

Authenticating with Twitter
~~~~~~~~~~~~~~~~~~~~~~~~~~~
*Coming Soon!*
	
.. _auth_anon:

Anonymous Authentication
~~~~~~~~~~~~~~~~~~~~~~~~
We recommend authenticating with 3rd parties (e.g. Facebook) as this provides a better user experience 
and ensures that user profiles and IDs are retained across app sessions and installs, however if you just 
want anonymous authentication, simply call the **authenticate** method:

.. raw:: html

	<script src="https://gist.github.com/1132956.js?file=authenticate.java"></script>

.. .. parsed-literal::

.. Socialize.getSocialize().authenticate(
		consumerKey,
		consumerSecret,
		**new** SocializeAuthListener() {
		
		**public** **void** onAuthSuccess(SocializeSession session) {
			// Success!
		}
		
		**public** **void** onAuthFail(SocializeException error) {
			// Handle auth fail
		}
		
		**public** **void** onError(SocializeException error) {
			// Handle error
		}
	});
	
Entities
--------
An entity is a single item of content in your app

Throughout the documentation and the code snippets we refer to an "entity".  This is simply a 
generic term for something that can be view, shared, liked or commented on.  Generally this will
correspond to a single item of content in your app.

**NOTE: Entities MUST be URLs!**

Entities in Socialize MUST be represented by an HTTP URL.  It is **strongly recommended** that a 
*real* URL be used (i.e. one that corresponds to an active web page) however if this is not possible 
because your content is not web based you can simply create a URL from key that uniquely identifies 
your entity.  

For example:

.. parsed-literal::

	**http://www.getsocialize.com (valid URL preferred)**
	
	*http://notarealurl.com (Not a real URL, but will still work)*
	
Creating an Entity
~~~~~~~~~~~~~~~~~~
An entity consists of a **key** (URL) and a **name**.  The name should be descriptive and help you identify the 
entity when viewing reports on the Socialize dashboard.

Creating an entity explicitly in this manner is **optional but recommended**.  If you simply post a 
comment,view,share or like against a URL that does not currently exist, it will be automatically created 
for you, but will not have a *name* associated with it.

To create an entity, simply call the **addEntity** method:

.. raw:: html

	<script src="https://gist.github.com/1132973.js?file=addentity.java"></script>


Retrieving Entity data
~~~~~~~~~~~~~~~~~~~~~~
An existing entity can be retrieved via the **getEntity** method.  Entities obtained in this way will also 
provide aggregate data on comments, likes, shares and views.  Refer to the `Entity object structure in the API Docs <http://www.getsocialize.com/docs/v1/#entity-object>`_.
for more detail on these aggregate values.

.. raw:: html

	<script src="https://gist.github.com/1132973.js?file=getentity.java"></script>


View
----
A 'view' is simply an event that records when a user views an entity.  Views are reported on the Socialize 
dashboard and provide an excellent way for you to determine which content items in your app are getting the 
most interest.

Creating a 'View'
~~~~~~~~~~~~~~~~~
To create a view, simply call the **view** method:

.. raw:: html

	<script src="https://gist.github.com/1132987.js?file=addview.java"></script>

Like
----
A 'like' represents a user's vote for an entity.  Likes are a way for you to determine which content items 
in your app are the most popular, and what is of most interest to your users.

Creating a 'Like'
~~~~~~~~~~~~~~~~~
To create a view, simply call the **like** method:

.. raw:: html

	<script src="https://gist.github.com/1132969.js?file=addlike.java"></script>
	
Removing a 'Like'
~~~~~~~~~~~~~~~~~
Removing a like (i.e. an 'unlike') is done via the **unlike** method.  In order to remove a like, you will 
need the ID of the like.  This is returned from the initial call to **like**

.. raw:: html

	<script src="https://gist.github.com/1132969.js?file=unlike.java"></script>

Comment
-------
Comments are a great way to build engagement in your app, and users love making comments!

Creating a Comment
~~~~~~~~~~~~~~~~~~
To create a comment on an entity, use the **addComment** method:

.. raw:: html

	<script src="https://gist.github.com/1132965.js?file=addcomment.java"></script>

Retrieving a single Comment
~~~~~~~~~~~~~~~~~~~~~~~~~~~
If you want to retrieve a single comment you can use the **getCommentById** method.  You will need the ID 
of the comment which was returned from the inital call to **addComment**:

.. raw:: html

	<script src="https://gist.github.com/1132965.js?file=getcommentbyid.java"></script>

Listing Comments
~~~~~~~~~~~~~~~~
To list all comments for an entity use the **listCommentsByEntity** method.  This will return a **maximum of 100 comments**

.. raw:: html

	<script src="https://gist.github.com/1132965.js?file=listcomments_np.java"></script>


Comments with pagination
~~~~~~~~~~~~~~~~~~~~~~~~
The **recommended** approach is to use pagination when listing comments:

.. raw:: html

	<script src="https://gist.github.com/1132965.js?file=listcomments.java"></script>
