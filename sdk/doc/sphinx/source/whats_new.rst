.. include:: header.inc
	
.. _whats_new:	
	
==========
What's New
==========

In This Version
---------------

.. raw:: html
   :file: snippets/important_notice.html

v2.2 adds support for the deprecation of the *offline_access* permission of Facebook applications (https://developers.facebook.com/roadmap/offline-access-removal). 

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
	- Display all activity occuring in your app at the User, Entity and Application level.
	
- ViewUtils
	- Track and report on the behavior of users within your app by creating Socialize Views.

The SDK also includes several helper classes to make life easier.

- ActionBarUtils
	- Quickly and easily render the Socialize Action Bar
	
- LocationUtils
	- Simple and easy access to location services on the device.

- ConfigUtils
	- Programmatic access to the gloabl Socialize config (socialize.properties)

New Sample App!
~~~~~~~~~~~~~~~

To showcase these new features and to provide developers with concrete examples of how to use the new library we have created a completely 
new sample app.  

The new sample app is located in the same place as always, the **/sample** directory of the SDK download.

.. include:: footer.inc	