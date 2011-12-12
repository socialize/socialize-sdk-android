.. raw:: html

	<link rel="stylesheet" href="static/css/gist.css" type="text/css" />
	
.. _support:	
	
=========================
Troubleshooting & Support
=========================

Action Bar is Pushed up by Keyboard
-----------------------------------

If you are already using a ScrollView in your layout you may have noticed that when entering text the 
ActionBar is "pushed up" by the soft keyboard like so:

.. image:: images/action_bar_above.png

You can correct this by setting **android:isScrollContainer="false"** in the declaration of your ScrollView::

	<ScrollView 
		android:layout_width="fill_parent"
		android:layout_height="fill_parent"
		android:isScrollContainer="false">
		
		...
		
	</ScrollView>
	
Support and Feedback
--------------------

For direct access to our support team you can post questions on our support system here:

http://support.getsocialize.com/