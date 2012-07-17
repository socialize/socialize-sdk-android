.. include:: header.inc
	
.. _whats_new:	
	
==========
What's New
==========

In This version (v2.3)
----------------------

Facebook Open Graph Likes
~~~~~~~~~~~~~~~~~~~~~~~~~
Socialize now supports Facebook Open Graph "likes" by default.  When a user who is authenticated with Facebook posts a like using Socialize we will 
automatically post this to the user's activity stream on Facebook as a Facebook Open Graph Like.

Open Graph can significantly improve the visibility of posts made to Facebook and through the Socialize SmartDownload system can in-turn greatly
improve visibility and downloads of your app.

IMPORTANT NOTE FOR FACEBOOK INTEGRATION
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Facebook is now supporting "deep linking" into your app from links posted to Facebook.  This means that end users who see a post from your app on their wall 
are taken immediately to the Google Play store for your app if they do not already have the app installed.  This process will bypass the Socialize SmartDownload feature
(which serves the same purpose) however if SmartDownloads are bypassed you will not benefit from any of the analytics tracking or referral optimization done by the 
SmartDownload process.  Thus we recommend you ensure that Android Native Deep Linking is **DISABLED** on your Facebook application

	.. image:: images/fb_hash.png
	
More customization options for the Action Bar
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
We have added a bunch of additional configuration options for the ActionBar that allows greater control over the look-and-feel of the ActionBar.

You now have complete control over colors and positioning of the ActionBar within your app.

Refer to the :ref:`custom_actionbar` section for more details.

v2.2
----

.. raw:: html
   :file: snippets/important_notice.html

v2.1.1 adds support for the deprecation of the *offline_access* permission of Facebook applications (https://developers.facebook.com/roadmap/offline-access-removal). 

This means developers upgrading to Socialize v2.2+ **MUST IMPLEMENT THE SOCIALIZE LIFECYCLE** correctly in their app.

Refer to the :doc:`lifecycle` section for more details.

v2.1
----
In v2.1 we introduced a range of new features around the **sharing of photos**.

You can now share photos directly to a user's Facebook wall, or to their Twitter feed

Check out :doc:`facebook_adv` or :doc:`twitter_adv` for more details.

v2.0
----

v2.0 of the Socialize SDK represents a major improvement over previous releases in both easy of use and stability.

In this new release we are introducing several new features and bug fixes as well as a complete reworking of the core interfaces needed to 
access the entire range of Socialize services.


.. note:: 
	**Backwards Compatibility**
	
	In this new version we have tried to make the developer interfaces to Socialize as simple as possible and whilst we have done our
	best to ensure backwards compatibility with older versions some incompatibilities may exist.  
	
	If you are already using an older version of Socialize we **STRONGLY RECOMMEND** that any calls to deprecated methods be replaced with 
	their non-deprecated counterparts.


Looking Good!
~~~~~~~~~~~~~

We have re-designed the Share Dialog to bring it more in line with end user expectations and to make the job of the developer (you) easier

This new dialog is also completely customizable

=======================================		============================================
.. image:: images/2_0_share_default.png		.. image:: images/2_0_share_via_networks.png
=======================================		============================================

Take a look at the :ref:`custom_share` section for more details on customizing the share dialog.

Complete Control
~~~~~~~~~~~~~~~~

In this new version we have completely rewritten the top level interfaces exposed to the developer to make them simpler and more powerful.

Developers can now access a set of *utility* classes that provide access to the entire suite of Socialize features.

Head over to the :doc:`sdk_user_guide` section for detailed descriptions and code samples

- ShareUtils
	- Access all sharing functions and UIs to enable sharing to Facebook, Twitter, Email and SMS.
	
- CommentUtils
	- Create and retrieve comments as well as access to the packaged UIs for commenting.

- UserUtils
	- Access to all things User including User Settings and User Profile screens.
	
- LikeUtils
	- Simplified access to allow your users to *Like* content in your app.
	
- EntityUtils
	- Complete control over the creation and management of entities in your app.
	
- ActionUtils
	- Display all activity occurring in your app at the User, Entity and Application level.
	
- ViewUtils
	- Track and report on the behavior of users within your app by creating Socialize Views.

The SDK also includes several helper classes to make life easier.

- ActionBarUtils
	- Quickly and easily render the Socialize Action Bar
	
- LocationUtils
	- Simple and easy access to location services on the device.

- ConfigUtils
	- Programmatic access to the global Socialize config (socialize.properties)

New Sample App!
~~~~~~~~~~~~~~~

To show case these new features and to provide developers with concrete examples of how to use the new library we have created a completely 
new sample app.  

The new sample app is located in the same place as always, the **/sample** directory of the SDK download.

.. include:: footer.inc	