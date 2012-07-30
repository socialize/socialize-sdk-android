.. include:: header.inc

	
.. _support:	
	
=========================
Troubleshooting & Support
=========================


.. _debug_logs:	

Displaying debug logs in LogCat
-------------------------------
If you're having problems with loading the Action Bar, or errors at runtime the simplest way to diagnose these problems is via the 
debug logs in the Android LogCat display.

By default Socialize will only render logs of level WARN to logcat.  To override this, simply add an entry to your **socialize.properties** file:

.. literalinclude:: snippets/props_log.txt
   :language: properties

Socialize will now render more details logs to the logcat console.  All Socialize logs will be tagged with the word "Socialize" so you can 
filter them in the logcat display:

.. image:: images/logcat.png

Common Problems
---------------

Action Bar is does not display
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
This is most often a result of mis-configuration in either the **AndroidManifest.xml**, or in your **socialize.properties** file.
In most cases the debug logs should give you an indication of the problem.  Refer to the above :ref:`debug_logs` section for details on displaying 
debug logs.

Action Bar is pushed up by keyboard
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you are already using a ScrollView in your layout you may have noticed that when entering text the 
ActionBar is "pushed up" by the soft keyboard like so:

.. image:: images/action_bar_above.png

You can correct this by setting **android:isScrollContainer="false"** in the declaration of your ScrollView

.. code-block:: xml

	<ScrollView 
		android:layout_width="fill_parent"
		android:layout_height="fill_parent"
		android:isScrollContainer="false">
		
		...
		
	</ScrollView>
	
Action Bar doesn't fit (appears unusually large)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. image:: images/action_bar_large.png

If your action bar appears too large for the display the most likely culprit is a rogue **anyDensity** setting in your **AndroidManifest.xml**

If you have the following setting in your manifest:

.. code-block:: xml

	<supports-screens android:anyDensity="false"/>

This will need to be changed to:

.. code-block:: xml

	<supports-screens android:anyDensity="true"/>
	
Or just removed completely as **false** is the default value for newer (1.6+) Android versions.

Auth Failure on Twitter
~~~~~~~~~~~~~~~~~~~~~~~

If you encounter the following error when attempting to authenticate with Twitter::

	Authentication error: Unable to respond to any of these challenges: {oauth=WWW-Authenticate: OAuth realm="https://api.twitter.com"}
	
This usually means you have not configured a **Callback URL** in your Twitter app.  Review the :doc:`twitter` section for details on adding a callback url.
	
Support and Feedback
--------------------

For direct access to our support team you can post questions on our support system here:

http://support.getsocialize.com/

.. include:: footer.inc	