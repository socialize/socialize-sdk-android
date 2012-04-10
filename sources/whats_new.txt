.. include:: header.inc
	
.. _whats_new:	
	
==========
What's New
==========

v1.6.2 Automatic Authentication
-------------------------------
v1.6.2 introduces *Automatic Authentication* for your users.  Our data indicates that users who authenticate with a 3rd party (e.g. Twitter or Facebook)
are much more likely to introduce new users to your app via the viral effect of activity within your app propagating to these 
3rd party networks.  From v1.6.2 onwards the Socialize SDK will default to requiring users authenticate with a 3rd party prior to performing 
any social action (e.g. *Comment* or *Like*).

This default behavior can be overridden by the developer (you) via configuration options in your **socialize.properties** file.

Refer to the :ref:`config` section for details on the configuration settings required to override *Automatic Authentication*.

v1.6 Direct SmartAlerts 
-----------------------
v1.6 of the Socialize SDK for Android adds support for direct SmartAlerts.  This allows developers to push SmartAlert notifications 
to their user-base directly from the web.

Simply log into your Socialize account at http://www.getsocialize.com and go to the dashboard for your app:

.. image:: images/direct_alerts.png

Refer to the :ref:`notifications` section for details on implementing SmartAlerts.


v1.5 Twitter! 
-------------
.. image:: images/twitter_sm.png

v1.5 of Socialize includes support for sharing to twitter as well auto-posting comments and likes (if using the :doc:`action_bar`)

Refer to the :doc:`twitter` section for details on how to integrate Twitter into Socialize.

v1.4 Socialize Like Button
--------------------------

Introduced in v1.4 is a stand-alone "like" button which can be added to any view and which automatically handles all authentication
with 3rd party social networks like Facebook.

Refer to the :ref:`like_button` section for details on how to implement the Socialize Like button.

v1.3.2 Entity Loader Change
---------------------------

**Existing users please read**

As of 1.3.2 a small change was made to the SocializeEntityLoader class which **may** cause a compilation error in your app.

If you are using the Entity Loader system, you will notice a new method has been added to this interface:

.. include:: snippets/entity_loader_can_load.txt

This new method allows you to inform Socialize whether it should attempt to load your entity or not.

v1.3.1 Meta data is here!
-------------------------
In v1.3.1 we have added support for meta data on an entity.  This means you can add arbitrary meta data to an entity when it is created
and you will be given this data back when the entity is loaded from Socialize.

.. include:: snippets/entity_meta_data.txt

v1.3 SmartAlerts
-----------------------
v1.3 of Socialize includes a full push notification framework designed to bring users back to your app!

.. image:: images/comment.png
.. image:: images/subscribe.png

Refer to the :ref:`notifications` section for details on how to implement SmartAlerts.

v1.1 Recent Activity in User Profile
-------------------------------------

In v1.1 we introduced recent activity in the user profile view:

.. image:: images/profile_activity.png

This can provide a significant boost to in-app engagement, however to maximize its benefit 
developers must implement a Socialize "Entity Loader".  This provides Socialize with the information 
needed to load the original view of the entity the user acted upon.

With a Socialize Entity Loader specified, users will be able to navigate from the recent activity to 
other parts of your application.

Refer to the :ref:`entity_loader` section for details on how to implement a Socialize Entity Loader

.. include:: footer.inc	