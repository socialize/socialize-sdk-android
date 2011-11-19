.. raw:: html

	<link rel="stylesheet" href="static/css/gist.css" type="text/css" />

====================
Socialize Action Bar
====================

Brand new in v0.7.0 of the Socialize SDK for Android is the "Action Bar" which provides a single 
entry point into the entire set of Socialize features.

.. image:: images/ab_view0.png	
.. image:: images/ab_view1.png
.. image:: images/ab_view2.png	

Displaying the Action Bar
~~~~~~~~~~~~~~~~~~~~~~~~~

Using socialize.properties configuration
########################################

Refer to the :doc:`getting_started` section for details on configuring your **socialize.properties** file.

.. raw:: html

	<script src="https://gist.github.com/1376163.js?file=no_conf.java"></script>

Using programmatic configuration
################################

If you prefer to do things the old fashioned way, you can set all the parameters programmatically.

.. raw:: html

	<script src="https://gist.github.com/1376163.js?file=action_bar_init.java"></script>
	
Advanced Features
~~~~~~~~~~~~~~~~~

Disabling the ScrollView
########################

By default the ActionBar will create a ScrollView to house your existing content.  
This is typically necessary so that the ActionBar doesn't impede the use of your existing content.

If you don't want your content to be scrollable however, you can disable this feature by using **ActionBarOptions**

.. raw:: html

	<script src="https://gist.github.com/1376163.js?file=init_with_options.java"></script>

ActionBar Creation Listener
###########################

If you need or want to obtain a reference to the ActionBar view at runtime, you can use a creation listener to listen 
for the "onCreate" event of the ActionBar:

.. raw:: html

	<script src="https://gist.github.com/1376163.js?file=init_with_listener.java"></script>

ActionBar Event Listener
########################

If you want to attach your own events to user operations on the ActionBar you can bind an **OnActionBarEventListener** 
to capture these:

.. raw:: html

	<script src="https://gist.github.com/1376163.js?file=event_listener.java"></script>

XML Based Layout (Experimental)
###############################

If the "auto-pin" feature of the Action Bar is not to your liking, or doesn't play well with your existing layout 
you can always just add the view manually.

**NOTE:** There are some fairly important things the Action Bar expects that you should be aware of:

1. It MUST be included inside a RelativeLayout.  This is because several of the UI features need to be able to slide "over" your existing content.
2. It MUST be positioned at the bottom of your view. 
3. It MUST be included as the LAST element in your XML layout, otherwise you may get some strange behaviour with layers sliding over/under content.

Here is the recommended way to include the Action Bar in your XML layout:

.. raw:: html

	<script src="https://gist.github.com/1376163.js?file=manual_layout.xml"></script>
