.. include:: header.inc

====================
Socialize Action Bar
====================

Brand new in v0.7.0 of the Socialize SDK for Android is the "Action Bar" which provides a single 
entry point into the entire set of Socialize features.

.. image:: images/action_bar.png	
.. image:: images/share.png
.. image:: images/add_comment.png	

Displaying the Action Bar
~~~~~~~~~~~~~~~~~~~~~~~~~

Using socialize.properties configuration
########################################

Refer to the :doc:`getting_started` section for details on configuring your **socialize.properties** file.

.. include:: snippets/action_bar.txt

Using programmatic configuration
################################

If you prefer to do things the old fashioned way, you can set all the parameters programmatically.

.. include:: snippets/action_bar_programmatic.txt
	
Advanced Features
~~~~~~~~~~~~~~~~~

Using the Entity Loader
########################

New in v1.1 of Socialize is the "Recent Activity" view shown in a user's profile.  This displays the recent actions
performed by the user within the app.  To maximize the benefit of this it is recommended that an "Entity Loader" implementation 
be provided to Socialize to that it knows how to load the original view for the entity.

Developers should create an implementation of a SocializeEntityLoader and provide this implementation to the SocializeUI instance:

.. include:: snippets/entity_loader.txt

Refer to the :doc:`entity_loader` section for more details.

Disabling the ScrollView
########################

By default the ActionBar will create a ScrollView to house your existing content.  
This is typically necessary so that the ActionBar doesn't impede the use of your existing content.

If you don't want your content to be scrollable however, you can disable this feature by using **ActionBarOptions**

.. include:: snippets/action_bar_noscroll.txt

ActionBar Creation Listener
###########################

If you need or want to obtain a reference to the ActionBar view at runtime, you can use a creation listener to listen 
for the "onCreate" event of the ActionBar:

.. include:: snippets/action_bar_create_listener.txt

ActionBar Event Listener
########################

If you want to attach your own events to user operations on the ActionBar you can bind an **OnActionBarEventListener** 
to capture these:

.. include:: snippets/action_bar_event_listener.txt

Reloading the Action Bar (Changing its entity)
##############################################

If you want to maintain a single action bar instance shared across multiple entities you can simply use the **refresh** method 
to instruct the ActionBar to reload after you change the entity key:

.. include:: snippets/action_bar_reload.txt

XML Based Layout
################

If the "auto-pin" feature of the Action Bar is not to your liking, or doesn't play well with your existing layout 
you can always just add the view manually.

.. note:: There are some fairly important things the Action Bar expects that you should be aware of:

1. It MUST be included inside a RelativeLayout.  This is because several of the UI features need to be able to slide "over" your existing content.
2. It MUST be positioned at the bottom of your view. 
3. It MUST be included as the LAST element in your XML layout, otherwise you may get some strange behaviour with layers sliding over/under content.

Here is the recommended way to include the Action Bar in your XML layout:

.. include:: snippets/action_bar_layout_xml.txt
	
.. include:: footer.inc	
