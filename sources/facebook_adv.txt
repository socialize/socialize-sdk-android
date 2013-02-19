.. include:: header.inc

====================
Advanced Facebook 
====================

.. raw:: html
   :file: snippets/expert_warning.html

Linking Users with Facebook
---------------------------

When connecting users in your app to their Facebook account it is important to understand the distinction between READ permissions and WRITE permissions.

Facebook makes a clear distinction between those operations required READ access and those requiring WRITE (publish) access. 

It is important to observe the following policy conventions from Facebook:

- READ permissions must be requested **BEFORE** WRITE permissions
- It is invalid to request READ permissions at the same time as WRITE permissions (this will cause an error)

The default behavior of the Socialize Action Bar means that Facebook permissions are requested in the correct order so you should not 
need to worry if you are simply using the Action Bar, however if you want to perform any direct interaction with Facebook you will need to be mindful 
of these rules. 

By default Socialize requests no additional permissions for READ access, and requests the following permissions for WRITE access:

	"publish_stream", "publish_actions"
	
When performing a direct Facebook operation you must first check to see whether the appropriate permissions have been linked:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-14
	:end-before: end-snippet-14
	
If you want to include additional permissions not included in the default list you can just specify these as additional 
parameters to the **isLinked** and **link** methods

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-15
	:end-before: end-snippet-15

If you already have Facebook integration in your app and do not wish to replace this with the Socialize implementation you 
can still link the user to Facebook within Socialize by providing the user's Facebook auth token

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1
	
Unlinking Users from Facebook
-----------------------------
	
To unlink a user from their Facebook account simple call **unlink**.  This call executes synchronously.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-3
	:end-before: end-snippet-3

Posting to Facebook
-------------------
By default all actions created using the SDK will provide the end user with the opportunity to share their 
action with Facebook (if Facebook has been configured) however it is also possible to manually post content to a user's
Facebook wall.

Posting Socialize Entities to Facebook
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The simplest and most effective use of Facebook within Socialize is to post entities.  This approach takes full advantage of 
Socialize SmartDownloads as well as automatically supporting Facebook Open Graph which helps maximize the effectiveness of your social strategy.

To post an entity to a user's Facebook timeline simply call the **postEntity** method.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-4
	:end-before: end-snippet-4
	
Posting Directly to Facebook
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want complete control over what is posted to Facebook, Socialize provides a simple interface to the Facebook Graph API.

Refer to the Facebook Graph API documentation for more specific implementation information

http://developers.facebook.com/docs/reference/api/

Executing a **POST**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-5
	:end-before: end-snippet-5
	
If you want to take full advantage of Socialize SmartDownloads and post the auto-generated SmartDownload urls for your entity or app you can 
do this by creating a simple share object without propagation first.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-8
	:end-before: end-snippet-8
	
Executing a **GET**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-6
	:end-before: end-snippet-6
	
Executing a **DELETE**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-7
	:end-before: end-snippet-7	
	
.. _open_graph:

Using Facebook Open Graph
-------------------------
From v2.3 onwards Socialize supports Facebook "like" Open Graph actions.  This means that when a user executes a "like" on Socialize and elects to 
share this on Facebook it can be posted as an "Open Graph Like" in the user's Facebook Activity stream.

This behavior can be enabled via configuration in your **socialize.properties** file

.. raw:: html
   :file: snippets/facebook_og_like_notice.html

.. literalinclude:: snippets/props_fb_like.txt
   :language: properties

.. note:: If you are not already familiar with the Facebook Open Graph we recommend reviewing the `Facebook Documentation <https://developers.facebook.com/docs/opengraph/>`_ first

Configuring your Facebook App for Open Graph
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Before you are able to utilize Open Graph calls such as "like" you must first follow the Facebook Guidelines for approval of Open Graph actions.

This is detailed on the Facebook website here:

`Open Graph Approval Process <http://developers.facebook.com/docs/opengraph/opengraph-approval/>`_

The following steps apply to seeking approval for the in-built "like" action however the same approach can be followed for all Open Graph Actions.

Adding an Open Graph Action to your App
"""""""""""""""""""""""""""""""""""""""
In your app settings on Facebook, access the Open Graph section:

.. image:: images/fb_og_menu.png

You will be presented with the default configuration screen for Open Graph.  Click the **Create New Action Type** button and select the action to be added

.. image:: images/fb_og_page1.png

You will then be presented with a dialog in which your action can be selected.  Choose an appropriate action and click **Submit**

.. image:: images/fb_og_select_action.png

Once your action has been added you are ready to submit it for approval.  Click the **Submit** link.

.. image:: images/fb_og_page2.png

At this point you may encounter some validation errors.  The most common of which are:

.. image:: images/fb_og_error1.png

The **Publish Action** requirement is most easily satisfied by simply executing the same *curl* command provided by Facebook.

Click on the **Get Code** link next to your action and run the *curl* command you are provided from your terminal (command line) interface

.. image:: images/fb_og_curl.png

The final step in the approval process requires you to clarify with Facebook the exact purpose for your OG action.  

.. image:: images/fb_og_error2.png

Socialize has prepared a standard response for you to use in this situation.  If you are using the Socialize UI features "out-of-the-box" (that is, without modification) then you 
can simply refer to the content we have provided for you at:  

http://blog.getsocialize.com/facebook-open-graph-review

If you have implemented your own UI and/or are calling the Socialize SDK directly through any other means we recommend you follow the guidelines established by Facebook for the approval of 
Open Graph actions.

https://developers.facebook.com/docs/opengraph/checklist/


Using In Built Open Graph Actions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The in built OG actions provided by Facebook **require approval by Facebook** and there is a limited set of actions available and a corresponding limited set of object types.

The actions supported and the corresponding object types are as follows

+------------+-------------------+---------------+---------------+
| Action     | Path              | Object Types  | Parameter     |
+============+===================+===============+===============+
| like       | me/og.likes       | object        | object        |
+------------+-------------------+---------------+---------------+
| follow     | me/og.follows     | profile       | profile       |
+------------+-------------------+---------------+---------------+
| publish    | me/news.publishes | article       | article       |
+------------+-------------------+---------------+---------------+
| read       | me/news.reads     | article       | article       |
+------------+-------------------+---------------+---------------+
| watch      | me/video.watches  | video.movie   | movie         |
+------------+-------------------+---------------+---------------+
|            |                   | video.episode | movie         |
+------------+-------------------+---------------+---------------+
|            |                   | video.tv_show | movie         |
+------------+-------------------+---------------+---------------+
|            |                   | video.other   | movie         |
+------------+-------------------+---------------+---------------+

The Object Types specified in the Open Graph call **MUST** correspond to a valid *og:type* meta element in the HTML page that represents the object.

For example::

	<meta property="og:type" content="video.movie" /> 

Fortunately Socialize will **automatically generate OG tags** on your entity page, but you must specify the correct type on the entity object itself.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-10
	:end-before: end-snippet-10
	
Once you have setup your Entity with the correct type you can force an Open Graph post by changing the PostData in the SocialNetworkListener.  
Here's a complete example

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-13
	:end-before: end-snippet-13	
	
The corresponding entity page on Socialize will automatically populate the required OG meta tags::

	<meta property="og:title" content="My Entity Name" /> 
	<meta property="og:description" content="...parsed from your URL..." /> 
	<meta property="og:image" content="...parsed from your URL..." />
	<meta property="og:type" content="video.movie" />
	<meta property="og:url" content="...the URL of this page..." />
	
.. raw:: html
   :file: snippets/open_graph_notice.html

If you don't have an actual URL for your entity you can setup the correct description, image etc by changing the meta data on your entity.

Refer to :ref:`entity_no_url` for more details on customizing the entity page.

Using Custom Open Graph Actions
"""""""""""""""""""""""""""""""
In addition to using the built-in Open Graph actions, you can also define your own.

For example an app that reviews restaurants may want to post the fact that a user "ate" a "meal" rather than simply saying a user "shared" a "link".

There is however a fairly lengthy setup process to correctly configure your application to handle custom OG actions.

We recommend reviewing the `Open Graph Tutorial <http://developers.facebook.com/docs/opengraph/tutorial/>`_ to understand how to configure your app for custom Open Graph actions.

Configuring Custom Open Graph Actions
+++++++++++++++++++++++++++++++++++++
Facebook Open Graph implements several security systems to ensure that owners of content are verified.  One such security measure ensures that websites housing information about custom OG types are registered 
against the Facebook App to guarantee that 3rd party users cannot fraudulently represent custom types on external pages.

If you want to utilize custom open graph actions in your Socialize enabled app you must first configure your Facebook application to expect posts that refer to websites hosted by Socialize.

This consists of 3 simple steps:

#. Obtain your Socialize App Url from your dashboard at www.getsocialize.com
#. Nominate the Socialize App Url as your "Site URL" on Facebook
#. Add the **getsocialize.com** domain to your list of App Domains on Facebook.

.. image:: images/fb_og_config.png

You can obtain your Socialize App Url from the "SmartDownloads" section accessed from your dashboard at www.getsocialize.com

.. image:: images/app_url.png

Once you have configured your application to accept custom Open Graph actions you can post these actions to Facebook by changing the PostData in the SocialNetworkListener.  

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-12
	:end-before: end-snippet-12
	
Posting Photos to Facebook
--------------------------

You can easily post photos to a user's Facebook Photo Album using the **post** method on **FacebookUtils**
	
.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-9
	:end-before: end-snippet-9

Facebook Single Sign On (SSO)
-----------------------------

Some users have reported having problems with the Single Sign On implementation in the Facebook SDK.

If you have experienced problems with this (e.g. the "invalid_key" error), you can easily disable this feature in your **socialize.properties** file

.. literalinclude:: snippets/props_fb_sso.txt
   :language: properties
   
Or in code at runtime:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-2
	:end-before: end-snippet-2

.. include:: footer.inc	
